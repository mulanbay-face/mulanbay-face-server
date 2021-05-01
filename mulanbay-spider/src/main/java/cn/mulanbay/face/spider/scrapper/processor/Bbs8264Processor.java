package cn.mulanbay.face.spider.scrapper.processor;

import cn.mulanbay.business.domain.SpiderTask;
import cn.mulanbay.business.enums.Platform;
import cn.mulanbay.face.spider.common.ConfigKey;
import cn.mulanbay.face.spider.persistent.service.SpiderTaskService;
import cn.mulanbay.face.spider.scrapper.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

/**
 * @Description: 8264处理
 * @Author: fenghong
 * @Create : 2021/4/19
 */
@Component
public class Bbs8264Processor extends AbstractProcessor {

    private static final Logger logger = LoggerFactory.getLogger(Bbs8264Processor.class);

    @Autowired
    SpiderTaskService spiderTaskService;

    public Bbs8264Processor() {
        super(Platform.BBS_8264);
    }

    /**
     * 图片处理
     * @param page
     */
    @Override
    public void handlePicture(Page page){
        try {
            Request spiderJob = page.getRequest();
            SpiderTask spiderTask = spiderJob.getExtra(ConfigKey.SPIDER_TASK);
            //抓取图片
            page.putField(ConfigKey.PICTURE, page.getHtml().regex(spiderTask.getRegex(),0).all());
            boolean nnp = this.needHandleNextPage(spiderTask);
            if(!nnp){
                logger.debug("无需处理下一页");
                //更新数据库
                this.closeTask(spiderTask);
            }
            String content = page.getRawText();
            if(content.contains("class=\"nxt\"")){
                //处理下一页
                Long nextPage = spiderTask.getPage()+1;
                String url = UrlUtils.createUrl(spiderTask.getUrlPattern(), nextPage);
                spiderJob.setUrl(url);
                spiderTask.setPage(nextPage);
                //更新数据库
                spiderTaskService.updatePage(spiderTask.getId(),nextPage);
                spiderJob.putExtra(ConfigKey.SPIDER_TASK,spiderTask);
                spiderHandler.addRequest(spiderJob);
            }else{
                logger.debug("没有下一页");
                //更新数据库
                this.closeTask(spiderTask);
            }
        } catch (Exception e) {
            logger.error("handleBbs8264异常",e);
        }
    }

    @Override
    public void handleLink(Page page) {
        try {
            Request spiderJob = page.getRequest();
            SpiderTask spiderTask = spiderJob.getExtra(ConfigKey.SPIDER_TASK);
            //抓取图片
            page.putField(ConfigKey.LINK, page.getHtml().regex(spiderTask.getRegex(),0).all());
            boolean nnp = this.needHandleNextPage(spiderTask);
            if(!nnp){
                logger.debug("无需处理下一页");
                //更新数据库
                this.closeTask(spiderTask);
            }
            String content = page.getRawText();
            //System.out.println(content);
            if(content.contains("class=\"nxt\"")){
                //处理下一页
                Long nextPage = spiderTask.getPage()+1;
                String url = UrlUtils.createUrl(spiderTask.getUrlPattern(), nextPage);
                spiderJob.setUrl(url);
                spiderTask.setPage(nextPage);
                //更新数据库
                spiderTaskService.updatePage(spiderTask.getId(),nextPage);
                spiderJob.putExtra(ConfigKey.SPIDER_TASK,spiderTask);
                spiderHandler.addRequest(spiderJob);
            }else{
                logger.debug("没有下一页");
                //更新数据库
                this.closeTask(spiderTask);
            }
        } catch (Exception e) {
            logger.error("handleBbs8264异常",e);
        }
    }
}
