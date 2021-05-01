package cn.mulanbay.face.spider.scrapper;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;


public class RedisPriorityScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    private JedisPool pool;

    /**
     *  任务队列：存放需要执行的任务队列，根据优先级不同建立不同队列，值为request的url
     */
    private static final String QUEUE_PREFIX = "spider_tasks:";

    /**
     *  任务索引：存放所有任务索引 值为request的url
     */
    private static final String URL_PREFIX = "spider_all_url:";

    /**
     *  任务具体内容：存放所有任务的具体执行内容 key为任务索引的hash值，value为整个request
     */
    private static final String REQUEST_PREFIX = "spider_all_request:";

    /**
     * 优先级：默认0-2 数字越大优先级越高
     */
    private int Priority_CAPACITY = 2;

    public RedisPriorityScheduler(String host) {
        this(new JedisPool(new JedisPoolConfig(), host));
    }

    public RedisPriorityScheduler(String host, int capacity) {
        this(new JedisPool(new JedisPoolConfig(), host), capacity);
    }

    public RedisPriorityScheduler(JedisPool pool) {
        this.pool = pool;
        setDuplicateRemover(this);
    }

    public RedisPriorityScheduler(JedisPool pool, int capacity) {
        this.pool = pool;
        this.Priority_CAPACITY = capacity;
        setDuplicateRemover(this);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        Jedis jedis = pool.getResource();
        try {
            jedis.del(getSetKey(task));
        } finally {
            //pool.returnResource(jedis);
        }
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            boolean isDuplicate = jedis.sismember(getSetKey(task), request.getUrl());
            if (!isDuplicate) {
                jedis.sadd(getSetKey(task), request.getUrl());
            }
            return isDuplicate;
        } finally {
            //pool.returnResource(jedis);
        }

    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            jedis.rpush(getQueueKey(getPriority(request), task), request.getUrl());
            if (request.getExtras() != null) {
                String field = DigestUtils.shaHex(request.getUrl());
                String value = JSON.toJSONString(request);
                jedis.hset((REQUEST_PREFIX + task.getUUID()), field, value);
            }
        } finally {
            //pool.returnResource(jedis);
        }
    }

    @Override
    public synchronized Request poll(Task task) {
        Jedis jedis = pool.getResource();
        try {
            String url = null;
            for (int i = Priority_CAPACITY; i >= 0; i--) {
                url = jedis.lpop(getQueueKey(i, task));
                if (url != null) {
                    break;
                }
            }
            if (url == null) {
                return null;
            }
            String key = REQUEST_PREFIX + task.getUUID();
            String field = DigestUtils.shaHex(url);
            byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
            if (bytes != null) {
                Request o = JSON.parseObject(new String(bytes), Request.class);
                return o;
            }
            Request request = new Request(url);
            return request;
        } finally {
            //pool.returnResource(jedis);
        }
    }

    protected String getSetKey(Task task) {
        return URL_PREFIX + task.getUUID();
    }

    protected String getQueueKey(int priority, Task task) {
        return QUEUE_PREFIX + priority + "_" + task.getUUID();
    }

    protected int getPriority(Request request) {
        int priority = (int) request.getPriority();
        if (priority > Priority_CAPACITY) {
            priority = Priority_CAPACITY;
        }
        return priority;
    }

    @Override
    public int getLeftRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = new Long(0);
            for (int i = Priority_CAPACITY; i >= 0; i--) {
                Long prioritySize = jedis.llen(getQueueKey(i, task));
                size = size + prioritySize;
            }
            return size.intValue();
        } finally {
            //pool.returnResource(jedis);
        }
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.scard(getSetKey(task));
            return size.intValue();
        } finally {
            //pool.returnResource(jedis);
        }
    }
}
