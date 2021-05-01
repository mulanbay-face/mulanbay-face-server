package cn.mulanbay.face.api.common;

import java.text.MessageFormat;

/**
 * 缓存key的定义
 *
 * @author fenghong
 * @create 2018-01-20 21:44
 */
public class CacheKey {

    /**
     * 命令的锁定（命令编号）
     */
    public static final String CMD_SEND_LOCK = "distributeLock:cmdSend:{0}";

    /**
     * 用户登录失败次数（用户名称/手机号）
     */
    public static final String USER_LOGIN_FAIL = "userLoginFail:{0}";

    /**
     * 用户新增操作（sessionId:URL）
     */
    public static final String USER_OPERATE_OP = "userOperateOp:{0}:{1}";

    /**
     * 用户错误代码发送限流（用户编号：错误代码）
     */
    public static final String USER_ERROR_CODE_LIMIT = "userErrorCodeLimit:{0}:{1}";

    /**
     * 系统监控时间线
     */
    public static final String SERVER_DETAIL_MONITOR_TIMELINE = "serverDetailMonitorTimeline";

    /**
     * 用户登录token（token）
     */
    public static final String USER_LOGIN_TOKEN = "userLogin:{0}";

    /**
     * 用户二次认证（用户编号）
     */
    public static final String USER_SEC_AUTH_CODE = "user:secAuthCode:{0}";

    /**
     * 微信WX_JSAPI_TICKET（appid）
     */
    public static final String WX_JSAPI_TICKET = "wx:jsApi:ticket:{0}";

    /**
     * 用户最新的一条消息（用户编号）
     */
    public static final String USER_LATEST_MESSAGE = "user:latestMessage:{0}";

    /**
     * 验证码
     */
    public static final String CAPTCHA_CODE = "captcha:code:{0}";

    /**
     * 图片人脸
     * 图片的人脸和用户本人的人脸是不同的序列号
     */
    public static final String PICTURE_FACE_ID = "picture_face_id";

    /**
     * 用户人脸
     */
    public static final String USER_FACE_ID = "user:face:id";

    /**
     * 最大用户图片人脸数
     */
    public static final String PICTURE_FACE_MAX_ID = "picture:face:maxId";

    /**
     * 最大用户人脸数
     */
    public static final String USER_FACE_MAX_ID = "user:face:maxId";

    /**
     * 人脸匹配缓存:图片类型，faceId
     */
    public static final String FACE_FEATURE = "face:feature:{0}:{1}";

    /**
     * 第三方图片人脸处理:图片地址
     */
    public static final String THIRD_PART_PICTURE_FACE = "thirdPart:face:{0}";

    /**
     * @param pattern
     * @param args
     * @return
     */
    public static String getKey(String pattern, String... args) {
        return MessageFormat.format(pattern, args);
    }

}
