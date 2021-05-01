package cn.mulanbay.face.api.thread;

import cn.mulanbay.business.domain.OperationLog;
import cn.mulanbay.business.domain.SystemFunction;
import cn.mulanbay.business.domain.SystemLog;
import cn.mulanbay.business.enums.LogLevel;
import cn.mulanbay.common.util.BeanCopy;
import cn.mulanbay.common.util.BeanFactoryUtil;
import cn.mulanbay.common.util.JsonUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.face.api.common.ApiErrorCode;
import cn.mulanbay.face.api.handler.LogHandler;
import cn.mulanbay.face.api.handler.SystemConfigHandler;
import cn.mulanbay.persistent.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * 操作记录记录线程
 *
 * @author fenghong
 * @create 2018-02-17 22:53
 */
public class OperationLogThread extends BaseLogThread {

    private static final Logger logger = LoggerFactory.getLogger(OperationLogThread.class);

    private OperationLog log;

    public OperationLogThread(OperationLog log) {
        super("操作日志");
        this.log = log;
    }

    @Override
    public void run() {
        handleLog(log);
    }

    /**
     * 增加操作日志
     *
     * @param log
     */
    private void handleLog(OperationLog log) {
        try {
            SystemConfigHandler systemConfigHandler = getSystemConfigHandler();
            SystemFunction sf = log.getSystemFunction();
            int errorCode = 0;
            String msgContent = "";
            if (log.getUrlAddress() != null) {
                msgContent = log.getUrlAddress();
            }
            if (sf == null) {
                logger.warn("找不到请求地址[" + log.getUrlAddress() + "],method[" + log.getMethod() + "]功能点配置信息");
                return;
            } else {
                errorCode = sf.getErrorCode();
                msgContent += "(" + sf.getName() + ")";
                log.setSystemFunction(sf);
                if (StringUtil.isNotEmpty(sf.getIdField())&&StringUtil.isEmpty(log.getIdValue())) {
                    Map<String, String> paraMap = (Map<String, String>) JsonUtil.jsonToBean(log.getParas(), Map.class);
                    log.setIdValue(this.getParaIdValue(sf, paraMap));
                }
            }
            Date now = new Date();
            log.setStoreTime(now);
            //会比较慢
            log.setHostIpAddress(systemConfigHandler.getHostIpAddress());
            log.setCreatedTime(now);
            //序列化比较耗时间
            //log.setParas(JsonUtil.beanToJson(changeToNormalMap(log.getParaMap())));
            log.setHandleDuration(log.getOccurEndTime().getTime() - log.getOccurStartTime().getTime());
            log.setStoreDuration(log.getStoreTime().getTime() - log.getOccurEndTime().getTime());
            if (log.getUserId() == null) {
                log.setUserId(0L);
                log.setUserName("未知");
            }
            BaseService baseService = BeanFactoryUtil.getBean(BaseService.class);
            baseService.saveObject(log);
            this.notifyError(log.getUserId(), errorCode, msgContent);
        } catch (Exception e) {
            logger.error("增加操作日志异常", e);
        }
    }

    private void addParaNotFoundSystemLog(OperationLog log) {
        //有可能在request的InputStream里面
        SystemLog systemLog = new SystemLog();
        BeanCopy.copyProperties(log, systemLog);
        systemLog.setLogLevel(LogLevel.WARNING);
        systemLog.setTitle("获取不到请求参数信息");
        systemLog.setContent("获取不到请求参数信息");
        systemLog.setErrorCode(ApiErrorCode.OPERATION_LOG_PARA_IS_NULL);
        BeanFactoryUtil.getBean(LogHandler.class).addSystemLog(systemLog);
    }

    private SystemConfigHandler getSystemConfigHandler() {
        return BeanFactoryUtil.getBean(SystemConfigHandler.class);
    }

}
