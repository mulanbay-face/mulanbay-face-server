package cn.mulanbay.face.api.web.listener;

import cn.mulanbay.business.enums.LogLevel;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.BeanFactoryUtil;
import cn.mulanbay.face.api.handler.LogHandler;
import cn.mulanbay.face.api.handler.SystemConfigHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 系统监听器基类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class BaseListener {

    private static final Logger logger = LoggerFactory.getLogger(BaseListener.class);

    /**
     * 记录日志
     * @param errorCode
     * @param title
     * @param msg
     */
    protected void doLog(Integer errorCode, String title, String msg) {
        try {
            if (errorCode == null) {
                errorCode = ErrorCode.SUCCESS;
            }
            LogHandler logHandler = BeanFactoryUtil.getBean(LogHandler.class);
            //增加IP地址和节点
            SystemConfigHandler systemConfigHandler = BeanFactoryUtil.getBean(SystemConfigHandler.class);
            msg = msg + ",IP地址:" + systemConfigHandler.getHostIpAddress() + "，节点编号:" + systemConfigHandler.getNodeId() + ".";
            logHandler.addSystemLog(LogLevel.WARNING, title, msg, errorCode);
        } catch (Exception e) {
            logger.error("doLog 异常", e);
        }

    }
}
