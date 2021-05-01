package cn.mulanbay.face.spider.scrapper.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.utils.FilePersistentBase;
import us.codecraft.webmagic.utils.ProxyUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by fenghong on 2016/7/6.
 * 改写webmagic中的代理池,主要解决：
 * 1. 代理池allProxy以serverIP为key，导致不能使用同一台机子上不同的代理
 */
public class ProxyPool {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private BlockingQueue<MyProxy> proxyQueue = new DelayQueue<MyProxy>();
    private Map<String, MyProxy> allProxy = new ConcurrentHashMap<String, MyProxy>();

    // ms
    private int reuseInterval = 1500;
    // ms
    private int reviveTime = 2 * 60 * 60 * 1000;
    // ms
    private int saveProxyInterval = 10 * 60 * 1000;

    private boolean isEnable = false;
    private boolean validateWhenInit = false;
    // private boolean isUseLastProxy = true;
    private String proxyFilePath = "/data/webmagic/lastUse.proxy";

    private FilePersistentBase fBase = new FilePersistentBase();

    private Timer timer = new Timer(true);

    private TimerTask saveProxyTask = new TimerTask() {

        @Override
        public void run() {
            saveProxyList();
            logger.info(allProxyStatus());
        }
    };

    public ProxyPool() {
        this(null, true);
    }

    /**
     * 指定是否可以使用最后使用过的代理
     * @param isUseLastProxy
     */
    public ProxyPool(boolean isUseLastProxy) {
        this(null, isUseLastProxy);
    }

    public ProxyPool(List<String[]> httpProxyList) {
        this(httpProxyList, true);
    }

    public ProxyPool(List<String[]> httpProxyList, boolean isUseLastProxy) {
        if (httpProxyList != null) {
            addProxy(httpProxyList.toArray(new String[httpProxyList.size()][]));
        }
        if (isUseLastProxy) {
            if (!new File(proxyFilePath).exists()) {
                setFilePath();
            }
            readProxyList();
            timer.schedule(saveProxyTask, 0, saveProxyInterval);
        }
    }

    private void setFilePath() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String path = tmpDir + FilePersistentBase.PATH_SEPERATOR + "webmagic" + FilePersistentBase.PATH_SEPERATOR + "lastUse.proxy";
        if (tmpDir != null && new File(tmpDir).isDirectory()) {
            fBase.setPath(tmpDir + FilePersistentBase.PATH_SEPERATOR + "webmagic");
            File f = fBase.getFile(path);
            if (!f.exists()) {
                try {
                    f.createNewFile();

                } catch (IOException e) {
                    logger.error("proxy file create error", e);
                }
            }

        } else {
            logger.error("java tmp dir not exists");
        }
        this.proxyFilePath = path;
    }

    private void saveProxyList() {
        if (allProxy.size() == 0) {
            return;
        }
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(fBase.getFile(proxyFilePath)));
            os.writeObject(prepareForSaving());
            os.close();
            logger.info("save proxy");
        } catch (FileNotFoundException e) {
            logger.error("proxy file not found", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, MyProxy> prepareForSaving() {
        Map<String, MyProxy> tmp = new HashMap<String, MyProxy>();
        for (Map.Entry<String, MyProxy> e : allProxy.entrySet()) {
            MyProxy p = e.getValue();
            p.setFailedNum(0);
            tmp.put(e.getKey(), p);
        }
        return tmp;
    }

    private void readProxyList() {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(fBase.getFile(proxyFilePath)));
            addProxy((Map<String, MyProxy>) is.readObject());
            is.close();
        } catch (FileNotFoundException e) {
            logger.info("last use proxy file not found", e);
        } catch (IOException e) {
            // e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
        }
    }

    private void addProxy(Map<String, MyProxy> httpProxyMap) {
        isEnable = true;
        for (Map.Entry<String, MyProxy> entry : httpProxyMap.entrySet()) {
            try {
                if (allProxy.containsKey(entry.getKey())) {
                    continue;
                }
                if (!validateWhenInit || ProxyUtils.validateProxy(entry.getValue())) {
                    entry.getValue().setFailedNum(0);
                    entry.getValue().setReuseTimeInterval(reuseInterval);
                    proxyQueue.add(entry.getValue());
                    allProxy.put(entry.getKey(), entry.getValue());
                }
            } catch (NumberFormatException e) {
                logger.error("HttpHost init error:", e);
            }
        }
        logger.info("proxy pool size>>>>" + allProxy.size());
    }

    public void addProxy(String[]... httpProxyList) {
        isEnable = true;
        for (String[] s : httpProxyList) {
            String key=getProxyKey(s);
            try {
                if (allProxy.containsKey(key)) {
                    continue;
                }
                MyProxy proxy = new MyProxy(s[0], Integer.valueOf(s[1]),null,null,reuseInterval);
                if (!validateWhenInit || ProxyUtils.validateProxy(proxy)) {
                    proxyQueue.add(proxy);
                    allProxy.put(key, proxy);
                }
            } catch (NumberFormatException e) {
                logger.error("HttpHost init error:", e);
            }
        }
        logger.info("proxy pool size>>>>" + allProxy.size());
    }

    /**
     * 重新加载代理池
     * @param httpProxyList
     */
    public synchronized void reloadProxy(String[]... httpProxyList) {
        allProxy.clear();
        proxyQueue.clear();
        addProxy(httpProxyList);
        logger.info("reloaded Proxy ");
    }

    public MyProxy getProxy() {
        MyProxy proxy = null;
        try {
            Long time = System.currentTimeMillis();
            proxy = proxyQueue.poll(3, TimeUnit.SECONDS);
            if(proxy==null){
                //TODO 如果要防止获取不到proxy出现，那么方法里poll、put操作要同时同步proxyQueue
                logger.warn("获取不到连接池数据");
                return null;
            }
            proxyQueue.put(proxy);
            double costTime = (System.currentTimeMillis() - time) / 1000.0;
            if (costTime > reuseInterval) {
                logger.info("get proxy time >>>> " + costTime);
            }
            MyProxy p = allProxy.get(getProxyKey(proxy));
            p.setLastBorrowTime(System.currentTimeMillis());
            p.borrowNumIncrement(1);
        } catch (Exception e) {
            logger.error("get proxy error", e);
        }
        if (proxy == null) {
            throw new NoSuchElementException();
        }
        return proxy;
    }

    public void returnProxy(MyProxy proxy, int statusCode) {
        MyProxy p = allProxy.get(getProxyKey(proxy));
        if (p == null) {
            return;
        }
        switch (statusCode) {
            case MyProxy.SUCCESS:
                p.setReuseTimeInterval(reuseInterval);
                p.setFailedNum(0);
                p.setFailedErrorType(new ArrayList<Integer>());
                p.recordResponse();
                p.successNumIncrement(1);
                break;
            case MyProxy.ERROR_403:
                // banned,try longer interval
                p.fail(MyProxy.ERROR_403);
                p.setReuseTimeInterval(reuseInterval * p.getFailedNum());
                logger.info(proxy + " >>>> reuseTimeInterval is >>>> " + p.getReuseTimeInterval() / 1000.0);
                break;
            case MyProxy.ERROR_BANNED:
                p.fail(MyProxy.ERROR_BANNED);
                p.setReuseTimeInterval(10 * 60 * 1000 * p.getFailedNum());
                logger.warn("this proxy is banned >>>> " + p.getHost());
                logger.info(proxy + " >>>> reuseTimeInterval is >>>> " + p.getReuseTimeInterval() / 1000.0);
                break;
            case MyProxy.ERROR_404:
                // p.fail(Proxy.ERROR_404);
                // p.setReuseTimeInterval(reuseInterval * p.getFailedNum());
                break;
            default:
                p.fail(statusCode);
                break;
        }
        if (p.getFailedNum() > 20) {
            p.setReuseTimeInterval(reviveTime);
            logger.error("remove proxy >>>> " + p.getHost() + ">>>>" + p.getFailedType() + " >>>> remain proxy >>>> " + proxyQueue.size());
            return;
        }
        if (p.getFailedNum() > 0 && p.getFailedNum() % 5 == 0) {
            if (!ProxyUtils.validateProxy(p)) {
                p.setReuseTimeInterval(reviveTime);
                logger.error("remove proxy >>>> " + p.getHost() + ">>>>" + p.getFailedType() + " >>>> remain proxy >>>> " + proxyQueue.size());
                return;
            }
        }
        // 不需要重新put，getProxy时已经放回去了
//        try {
//            proxyQueue.put(p);
//        } catch (InterruptedException e) {
//            logger.warn("proxyQueue return proxy error", e);
//        }
    }

    public String allProxyStatus() {
        String re = "all proxy info >>>> \n";
        for (Map.Entry<String, MyProxy> entry : allProxy.entrySet()) {
            re += entry.getValue().toString() + "\n";
        }
        return re;
    }

    public int getIdleNum() {
        return proxyQueue.size();
    }

    public int getReuseInterval() {
        return reuseInterval;
    }

    public void setReuseInterval(int reuseInterval) {
        this.reuseInterval = reuseInterval;
    }

    public void enable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public int getReviveTime() {
        return reviveTime;
    }

    public void setReviveTime(int reviveTime) {
        this.reviveTime = reviveTime;
    }

    public boolean isValidateWhenInit() {
        return validateWhenInit;
    }

    public void validateWhenInit(boolean validateWhenInit) {
        this.validateWhenInit = validateWhenInit;
    }

    public int getSaveProxyInterval() {
        return saveProxyInterval;
    }

    public void setSaveProxyInterval(int saveProxyInterval) {
        this.saveProxyInterval = saveProxyInterval;
    }

    public String getProxyFilePath() {
        return proxyFilePath;
    }

    public void setProxyFilePath(String proxyFilePath) {
        this.proxyFilePath = proxyFilePath;
    }

    /**
     * 原来的allProxy以serverIp为key，修改为serverIp+port
     * @param ss：ss[0]serverIp,ss[1]:port
     * @return
     */
    private String getProxyKey(String[] ss){
        return ss[0]+"_"+ss[1];
    }

    /**
     * 原来的allProxy以serverIp为key，修改为serverIp+port
     * @param proxy
     * @return
     */
    private String getProxyKey(MyProxy proxy){
        return proxy.getHost()+"_"+proxy.getPort();
    }
}
