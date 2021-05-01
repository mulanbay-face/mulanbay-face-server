package cn.mulanbay.face.spider.scrapper;

import cn.mulanbay.business.domain.SpiderTask;
import cn.mulanbay.business.enums.Platform;
import cn.mulanbay.face.spider.common.ConfigKey;
import cn.mulanbay.face.spider.scrapper.processor.AbstractProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 *
 * @Description:
 * @Author: fenghong
 * @Create : 2021/4/18
 */
@Component
public class FaceProcessor implements PageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FaceProcessor.class);

    @Autowired
    List<AbstractProcessor> processorList;

    /**
     * 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
     */
    private Site site = Site.me().setRetryTimes(1).setSleepTime(1000).setCycleRetryTimes(1).setCharset("UTF-8");

    /**
     * process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
     * @param page
     */
    @Override
    public void process(Page page) {
        Request spiderJob = page.getRequest();
        SpiderTask spiderTask = spiderJob.getExtra(ConfigKey.SPIDER_TASK);
        Platform platform = spiderTask.getPlatform();
        AbstractProcessor processor = this.getProcessor(platform);
        if(processor==null){
            logger.error("未能找到"+platform+"的处理器");
        }else {
            processor.handle(page);
        }
    }

    /**
     * 获取处理器
     * @param platform
     * @return
     */
    private AbstractProcessor getProcessor(Platform platform){
        for(AbstractProcessor ap : processorList){
            if(ap.getPlatform()==platform){
                return ap;
            }
        }
        return null;
    }
    @Override
    public Site getSite() {
        return site;
    }

}
