package cn.mulanbay.face.api.web.controller;

import cn.mulanbay.business.domain.User;
import cn.mulanbay.business.domain.UserMessage;
import cn.mulanbay.business.enums.LogLevel;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.face.api.common.ApiErrorCode;
import cn.mulanbay.face.api.common.CacheKey;
import cn.mulanbay.face.api.handler.PmsMessageSendHandler;
import cn.mulanbay.face.api.handler.PmsNotifyHandler;
import cn.mulanbay.face.api.handler.WxpayHandler;
import cn.mulanbay.face.api.persistent.service.AuthService;
import cn.mulanbay.face.api.web.bean.request.CommonBeanDeleteRequest;
import cn.mulanbay.face.api.web.bean.request.CommonBeanGetRequest;
import cn.mulanbay.face.api.web.bean.request.UserCommonRequest;
import cn.mulanbay.face.api.web.bean.request.log.MyUserMessageSearch;
import cn.mulanbay.face.api.web.bean.request.log.UserMessageSearch;
import cn.mulanbay.face.api.web.bean.request.user.UserMessageRequest;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 用户消息
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/userMessage")
public class UserMessageController extends BaseController {

    private static Class<UserMessage> beanClass = UserMessage.class;

    @Autowired
    AuthService authService;

    @Autowired
    PmsMessageSendHandler pmsMessageSendHandler;

    @Autowired
    PmsNotifyHandler pmsNotifyHandler;

    @Autowired
    WxpayHandler wxpayHandler;

    /**
     * 获取任务列表
     *
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public ResultBean getData(UserMessageSearch sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<UserMessage> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 移动端个人中心使用
     *
     * @return
     */
    @RequestMapping(value = "/getMyList", method = RequestMethod.GET)
    public ResultBean getMyList(MyUserMessageSearch sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<UserMessage> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(Long id) {
        UserMessage br = baseService.getObject(beanClass, id);
        return callback(br);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/getByUser", method = RequestMethod.GET)
    public ResultBean getByUser(@Valid CommonBeanGetRequest ubg) {
        UserMessage br = baseService.getObjectWithUser(beanClass, ubg.getId(), ubg.getUserId());
        return callback(br);
    }

    /**
     * 重新发送
     *
     * @return
     */
    @RequestMapping(value = "/resend", method = RequestMethod.GET)
    public ResultBean resend(Long id) {
        UserMessage br = baseService.getObject(beanClass, id);
        pmsMessageSendHandler.sendMessage(br);
        return callback(br);
    }

    /**
     * 发送
     *
     * @return
     */
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResultBean send(@RequestBody @Valid UserMessageRequest um) {
        User user = authService.getUserByUsernameOrPhone(um.getUsername());
        if (user == null) {
            return callbackErrorInfo("未找到相关用户");
        } else {
            Integer code = um.getCode();
            if (code == null) {
                //直接发送
                boolean b = wxpayHandler.sendTemplateMessage(user.getId(), um.getTitle(), um.getContent(), um.getNotifyTime(), LogLevel.NORMAL, null);
                return callback(b);
            } else {
                if (code == 0) {
                    code = ApiErrorCode.MESSAGE_NOTIFY_COMMON_CODE;
                }
                pmsNotifyHandler.addNotifyMessage(code, um.getTitle(), um.getContent(),
                        user.getId(), um.getNotifyTime());
                return callback(null);
            }
        }
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonBeanDeleteRequest deleteRequest) {
        this.deleteUserEntity(beanClass,
                NumberUtil.stringArrayToLongArray(deleteRequest.getIds().split(",")),
                deleteRequest.getUserId());
        return callback(null);
    }

    /**
     * 获取最新的一条消息
     *
     * @return
     */
    @RequestMapping(value = "/getLatestMessage", method = RequestMethod.GET)
    public ResultBean getLatestMessage(UserCommonRequest uc) {
        String key = CacheKey.getKey(CacheKey.USER_LATEST_MESSAGE, uc.getUserId().toString());
        UserMessage br = cacheHandler.get(key, beanClass);
        if (br != null) {
            cacheHandler.delete(key);
        }
        return callback(br);
    }

}
