package cn.mulanbay.face.spider.scrapper;

import java.text.MessageFormat;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/4/19
 */
public class UrlUtils {

    /**
     * 生成url
     * @param urlPattern
     * @param page
     * @return
     */
    public static String createUrl(String urlPattern,Long page) {
        return MessageFormat.format(urlPattern, page);
    }
}
