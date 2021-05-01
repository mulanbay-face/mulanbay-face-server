package cn.mulanbay.face.spider.scrapper.processor;

import cn.mulanbay.business.domain.SpiderLog;
import cn.mulanbay.business.domain.SpiderTask;
import cn.mulanbay.business.enums.*;
import cn.mulanbay.face.spider.common.ConfigKey;
import cn.mulanbay.face.spider.persistent.service.SpiderTaskService;
import cn.mulanbay.face.spider.scrapper.SpiderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

import java.util.Date;

/**
 * 下载处理器
 * @Author: fenghong
 * @Create : 2021/4/19
 */
public abstract class AbstractProcessor {

    @Autowired
    protected SpiderTaskService spiderTaskService;

    @Autowired
    protected SpiderHandler spiderHandler;

    /**
     * 平台
     */
    private Platform platform;

    public AbstractProcessor(Platform platform) {
        this.platform = platform;
    }

    /**
     * 处理业务
     * @param page
     */
    public void handle(Page page){
        Request spiderJob = page.getRequest();
        SpiderTask spiderTask = spiderJob.getExtra(ConfigKey.SPIDER_TASK);
        TaskType taskType = spiderTask.getTaskType();
        switch (taskType){
            case LINK:
                this.handleLink(page);
                break;
            case PICTURE:
                this.handlePicture(page);
                break;
            default:
                break;
        }
        //增加日志
        this.addSpiderLog(spiderTask,spiderJob.getUrl());
    }

    /**
     * 增加日志
     * @param spiderTask
     * @param url
     */
    private void addSpiderLog(SpiderTask spiderTask,String url){
        SpiderLog log = new SpiderLog();
        log.setTaskId(spiderTask.getId());
        log.setPlatform(spiderTask.getPlatform());
        log.setTaskType(spiderTask.getTaskType());
        log.setUrl(url);
        log.setCreatedTime(new Date());
        spiderTaskService.addLog(log);
    }
    /**
     * 照片处理
     * @param page
     */
    public abstract void handlePicture(Page page);

    /**
     * 链接处理
     * @param page
     */
    public abstract void handleLink(Page page);

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    /**
     * 关闭
     * @param spiderTask
     */
    protected void closeTask(SpiderTask spiderTask){
        if(spiderTask.getTaskSource()== TaskSource.AUTO){
            //自动的直接删除
            spiderTaskService.deleteSpiderTask(spiderTask.getId());
            return;
        }
        if(spiderTask.getTaskPeriod()== TaskPeriod.ONCE){
            spiderTask.setStatus(CommonStatus.DISABLE);
        }else{
            spiderTask.setPage(1L);
        }
        //更新数据库
        spiderTaskService.updateSpiderTask(spiderTask);
    }

    /**
     * 是否需要处理下一页
     * @param spiderTask
     * @return
     */
    protected boolean needHandleNextPage(SpiderTask spiderTask){
        Long page = spiderTask.getPage();
        if(page==null){
            return false;
        }
        Long maxPage = spiderTask.getMaxPage();
        if(maxPage==null){
            return true;
        }
        if(page.longValue()<maxPage.longValue()){
            return true;
        }
        return false;
    }
}
