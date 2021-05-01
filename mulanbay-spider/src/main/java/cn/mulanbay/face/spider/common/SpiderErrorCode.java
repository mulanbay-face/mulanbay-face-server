package cn.mulanbay.face.spider.common;

/**
 * 错误代码
 * 规则：1位系统代码（spider为1）+2两位模块代码+2两位子模块代码+两位编码
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class SpiderErrorCode {

    /** 日志类 start **/

    public static final int OPERATION_LOG_PARA_IS_NULL = 2010001;

    /** 日志类 end **/

    /** 爬虫类 start **/

    public static final int SPIDER_DUPLICATE = 2020001;

    public static final int UNACCEPETD_HTTP_CODE = 2020002;

    public static final int DOWNLOAD_ERROR = 2020003;
    /** 爬虫类 end **/
}
