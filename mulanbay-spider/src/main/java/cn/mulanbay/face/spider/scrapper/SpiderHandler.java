package cn.mulanbay.face.spider.scrapper;

import cn.mulanbay.business.domain.SpiderTask;
import cn.mulanbay.business.enums.TaskPeriod;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.face.spider.common.ConfigKey;
import cn.mulanbay.face.spider.common.SpiderErrorCode;
import cn.mulanbay.face.spider.persistent.service.SpiderTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import javax.management.JMException;
import java.util.Date;
import java.util.List;


/**
 * 爬虫入口
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class SpiderHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(SpiderHandler.class);

    @Value("${spider.enable.jmx.monitor}")
    private boolean enableJMX;

    @Value("${spider.threads}")
    private int threads;

    @Autowired
    FaceDownloader faceDownloader;

    @Autowired
    PageProcessor pageProcessor;

    @Autowired
    SpiderTaskService spiderTaskService;

    @Autowired
    List<Pipeline> pipelineList;

    private static Spider spider;

    public SpiderHandler() {
        super("SpiderHandler");
    }

    @Override
    public void init() {
        logger.info("初始化SpiderManager......");
        if (spider != null) {
            logger.error("spider 已经初始化，不能多次初始化");
            throw new ApplicationException(SpiderErrorCode.SPIDER_DUPLICATE, "spider 已经初始化，不能多次初始化");
        }

        spider = Spider.create(pageProcessor)
                .setUUID("Spider_" + DateUtil.getFormatDate(new Date(),DateUtil.Format24Datetime2))
                //.setDownloader(faceDownloader)
                .setPipelines(pipelineList)
                .setScheduler(new QueueScheduler())
                .setExitWhenComplete(false)
                .thread(threads);
        spider.setEmptySleepTime(3000);

        logger.info("加载Spider任务");
        //this.loadTasks();

        logger.info("启动Spider...");
        spider.start();
        logger.info("启动Spider 结束");

        logger.info("开启SpiderManager监控...");
        startJMXMonitor();
        logger.info("开启SpiderManager监控结束");

        logger.info("初始化SpiderManager 结束");
    }

    private void startJMXMonitor() {
        if (enableJMX) {
            try {
                SpiderMonitor.instance().register(spider);
            } catch (JMException e) {
                logger.warn("Add JMX Monitor Error", e);
            }
        } else {
            logger.warn("No JMX Monitor By Config");
        }
    }

    /**
     * 添加任务
     * @param request
     */
    public void addRequest(Request request) {
        SpiderTask spiderTask = request.getExtra(ConfigKey.SPIDER_TASK);
        if(spiderTask.getTaskPeriod()== TaskPeriod.ONCE){
            String url = request.getUrl();
            //查找url是否处理过
            boolean b = spiderTaskService.urlHandled(url);
            if(b){
                logger.warn(url+"已经处理过");
                return;
            }
        }
        spider.addRequest(request);
    }

    public Spider getSpider() {
        return spider;
    }

    /**
     * 加载任务
     */
    private void loadTasks(){
        List<SpiderTask> list = spiderTaskService.getEnableSpiderTaskList();
        for(SpiderTask st : list){
            this.addSpiderTask(st);
        }
        logger.info("一共加载任务个数："+list.size());
    }

    /**
     * 增加任务到调度器
     * @param st
     */
    public void addSpiderTask(SpiderTask st){
        //添加任务
        Request spiderJob = new Request();
        spiderJob.setPriority(st.getPriority());
        String url = UrlUtils.createUrl(st.getUrlPattern(), st.getPage());
        spiderJob.setUrl(url);
        spiderJob.putExtra(ConfigKey.SPIDER_TASK,st);
        this.addRequest(spiderJob);
        logger.debug("增加了一个任务:"+st.getName());
    }

    /**
     * 是否在运行
     *
     * @return
     */
    public boolean isRunning() {
        if (spider == null) {
            return false;
        } else {
            if (spider.getStatus() == Spider.Status.Running) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void destroy() {
        spider.stop();
    }

    @Override
    public void reload() {

    }

}
