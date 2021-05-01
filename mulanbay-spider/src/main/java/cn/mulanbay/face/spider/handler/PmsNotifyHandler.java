package cn.mulanbay.face.spider.handler;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.HandlerCmd;
import cn.mulanbay.business.handler.HandlerResult;
import cn.mulanbay.common.exception.MessageNotify;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.schedule.NotifiableProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 提醒处理
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class PmsNotifyHandler extends BaseHandler implements NotifiableProcessor, MessageNotify {

    private static final Logger logger = LoggerFactory.getLogger(PmsNotifyHandler.class);

    @Value("${system.nodeId}")
    String nodeId;

    @Value("${notify.message.expectSendTime}")
    String defaultExpectSendTime;

    /**
     * 是否需要提醒表单验证类的错误代码
     */
    @Value("${notify.validateError}")
    boolean notifyValidateError;

    /**
     * 表单验证类的错误代码最小值，一般是8位数开始
     */
    @Value("${notify.validateError.minErrorCode}")
    int minValidateErrorCode;

    @Autowired
    BaseService baseService;

    @Autowired
    SystemConfigHandler systemConfigHandler;

    @Autowired
    LogHandler logHandler;

    @Autowired
    CacheHandler cacheHandler;

    public PmsNotifyHandler() {
        super("提醒处理");
    }

    public Long addNotifyMessage(int code, String title, String content, Long userId, Date notifyTime) {
        return this.addNotifyMessage(code, title, content, userId, notifyTime, null);
    }

    /**
     * 向某个特定的人添加消息
     * 消息可能针对普通用户，或者是系统管理员
     *
     * @param title
     * @param content
     * @param userId
     * @param notifyTime
     * @param url        微信消息跳转地址
     */
    public Long addNotifyMessage(int code, String title, String content, Long userId, Date notifyTime, String url) {
        logger.debug("消息："+content);
        return 0L;
    }

    /**
     * 向系统中需要通知的人发送系统消息
     * 消息只针对管理员，所以这里发送的都是系统消息
     *
     * @param title
     * @param content
     * @param notifyTime
     */
    public void addMessageToNotifier(int code, String title, String content, Date notifyTime, String url, String remark) {
        logger.debug("消息："+content);
    }

    /**
     * 调度的消息通知
     *
     * @param taskTriggerId
     * @param code          错误代码
     * @param title
     * @param message
     */
    @Override
    public void notifyMessage(Long taskTriggerId, int code, String title, String message) {
        //todo 后期可以通过taskTriggerId来选择通知给谁
        this.addMessageToNotifier(code, title, message, null, null, null);
    }

    @Override
    public List<HandlerCmd> getSupportCmdList() {
        List<HandlerCmd> list = new ArrayList<>();
        list.add(new HandlerCmd("clear", "清除所有未发送消息"));
        return list;
    }

    @Override
    public HandlerResult handle(String cmd) {

        return super.handle(cmd);
    }

    @Override
    public void notifyMsg(int code, String title, String content) {
        this.addMessageToNotifier(code, title, content, null, null, null);
    }
}
