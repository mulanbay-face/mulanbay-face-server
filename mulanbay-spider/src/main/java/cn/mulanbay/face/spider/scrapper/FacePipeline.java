package cn.mulanbay.face.spider.scrapper;

import cn.mulanbay.business.domain.SpiderTask;
import cn.mulanbay.business.enums.*;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.face.spider.common.ConfigKey;
import cn.mulanbay.persistent.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;
import java.util.List;

/**
 * @Description: 存储处理
 * @Author: fenghong
 * @Create : 2021/4/18
 */
@Component
public class FacePipeline implements Pipeline {

    private static final Logger logger = LoggerFactory.getLogger(FacePipeline.class);

    @Value("${spider.faceApiUrl}")
    String apiUrl;

    @Autowired
    SpiderHandler spiderHandler;

    @Autowired
    BaseService baseService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        Request spiderJob = resultItems.getRequest();
        SpiderTask spiderTask = spiderJob.getExtra(ConfigKey.SPIDER_TASK);
        TaskType taskType = spiderTask.getTaskType();
        switch (taskType){
            case LINK:
                this.handleLink(resultItems);
                break;
            case PICTURE:
                this.handlePicture(resultItems);
                break;
            default:
                break;
        }



    }

    /**
     * 处理链接
     * @param resultItems
     */
    private void handleLink(ResultItems resultItems){
        Request spiderJob = resultItems.getRequest();
        SpiderTask spiderTask = spiderJob.getExtra(ConfigKey.SPIDER_TASK);
        List<String> links = (List) resultItems.getAll().get(ConfigKey.LINK);
        if(!StringUtil.isNotEmpty(links)){
            logger.warn("未爬取到链接数据");
            return;
        }
        Platform platform = spiderTask.getPlatform();
        switch (platform){
            case BBS_8264:
                this.handleBbs8264Link(links);
                break;
            case MAFENGWO:
                this.handleBbs8264Link(links);
                break;
            default:
                break;
        }
    }

    private void handleBbs8264Link(List<String> links){
        for(String link :links){
            try {
                logger.debug("爬取到链接数据："+link);
                String urlPattern = link.replace("-1-","-{0}-");
                //logger.debug("新链接："+urlPattern);
                if(urlPattern.contains("-{0}-")){
                    logger.debug("增加图片抓取任务："+urlPattern);
                    //有效的
                    SpiderTask st = new SpiderTask();
                    st.setPage(1L);
                    st.setStatus(CommonStatus.ENABLE);
                    st.setPlatform(Platform.BBS_8264);
                    st.setCreatedTime(new Date());
                    st.setName("自动生成");
                    st.setPriority(2L);
                    st.setRegex("http://image1.8264.com/(?!(\\.jpg|\\.png|\\.jpeg)).+?(\\.jpg|\\.png|\\.jpeg)");
                    st.setTaskPeriod(TaskPeriod.ONCE);
                    st.setTaskSource(TaskSource.AUTO);
                    st.setUrlPattern(urlPattern);
                    st.setTaskType(TaskType.PICTURE);
                    baseService.saveObject(st);
                    spiderHandler.addSpiderTask(st);
                }
            } catch (Exception e) {
                logger.error("处理"+link+"error",e);
            }
        }
    }
    /**
     * 处理图片
     * @param resultItems
     */
    private void handlePicture(ResultItems resultItems){
        Request spiderJob = resultItems.getRequest();
        SpiderTask spiderTask = spiderJob.getExtra(ConfigKey.SPIDER_TASK);
        String url = spiderJob.getUrl();
        Platform platform = spiderTask.getPlatform();
        List pictures = (List) resultItems.getAll().get(ConfigKey.PICTURE);
        if(!StringUtil.isNotEmpty(pictures)){
            logger.warn("未爬取到图片数据");
            return;
        }
//        FaceData faceData = new FaceData(url,platform,pictures);
//        //发送数据
//        HttpResult hr = HttpUtil.doPostJson(apiUrl,JsonUtil.beanToJson(faceData));
//        logger.debug("发送数据处理结果："+JsonUtil.beanToJson(hr));
    }
}
