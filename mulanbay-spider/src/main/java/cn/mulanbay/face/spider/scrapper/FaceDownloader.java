package cn.mulanbay.face.spider.scrapper;

import cn.mulanbay.face.spider.common.ConfigKey;
import cn.mulanbay.face.spider.handler.CacheHandler;
import cn.mulanbay.face.spider.scrapper.job.HttpDownloader;
import cn.mulanbay.face.spider.scrapper.proxy.MyProxy;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 一个调用器, 下载和封装数据的任务, 兼顾下载和封装过程的异常处理
 *  插件只会传回来 正确值或者ServiceException
 *  TODO, 执行出错的处理(重试机制, 以及异常处理, 通知机制)
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class FaceDownloader extends AbstractDownloader {

    private static final Logger logger = LoggerFactory.getLogger(FaceDownloader.class);

    @Autowired
    ProxyHandler proxyHandler;

    @Autowired
    CacheHandler cacheHandler;

    @Override
    public Page download(Request request, Task task) {
        //把最近一次爬取时间存入liveDownloader中
        saveLatelyDownloadTime();
        Site site = null != task ? task.getSite() : null;
        MyProxy proxy = null;
        int statusCode = MyProxy.SUCCESS;
        try {
            //注入 Proxy
            proxy = proxyHandler.getProxy();
            if (proxy != null) {
                logger.debug("使用代理, serverIp:" + proxy.getHost() + ",port:" + proxy.getPort());
                request.putExtra(ConfigKey.PROXY,proxy);
            }
            logger.info("Downloading: " + request.getUrl() + " Proxy: " + request.getExtra(ConfigKey.PROXY));
            String htmlContent = HttpDownloader.instance().download(request.getUrl(),proxy);
            //System.out.println(htmlContent);
            Page res = new Page();
            res.setRawText(htmlContent);
            res.setRequest(request);
            logger.debug("Download Result: " + JSON.toJSONString(res.getResultItems().getAll()));
            statusCode = res.getStatusCode();

            return res;
        } catch (Exception e) {
            logger.warn("Download Page Exception: " + request.getUrl(), e);
            return null;
        } finally {
            if (proxy != null) {
                proxyHandler.returnProxy(proxy, statusCode);
            }
        }
    }

    @Override
    public void setThread(int threadNum) {
        //Do Nothing
    }


    private void saveLatelyDownloadTime(){
        String latelyDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.setLatelyDownloaderDate(latelyDate);
    }



    private String latelyDownloaderDate;

    public String getLatelyDownloaderDate() {
        return latelyDownloaderDate;
    }

    public void setLatelyDownloaderDate(String latelyDownloaderDate) {
        this.latelyDownloaderDate = latelyDownloaderDate;
    }
}
