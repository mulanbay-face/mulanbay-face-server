package cn.mulanbay.face.api.common;

/**
 * 错误代码
 * 规则：1位系统代码（pms为1）+2两位模块代码+2两位子模块代码+两位编码
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class ApiErrorCode {

    /**
     * 通用类 start
     **/

    public static final int USER_ENTITY_NOT_FOUND = 1010001;

    public static final int USER_ENTITY_NOT_ALLOWED = 1010002;

    public static final int UN_SUPPORT_DATE_GROUP_TYPE = 1010003;

    /**
     * 用户重要操作
     */
    public static final int USER_IMP_OP = 1010004;

    /**
     * 用户敏感操作
     */
    public static final int USER_SENS_OP = 1010005;

    /**
     * 用户登录
     */
    public static final int USER_LOGIN = 1010006;

    /**
     * 清除缓存失败
     */
    public static final int DELETE_CACHE_ERROR = 1010007;

    public static final int URL_DECODE_ERROR = 1010008;

    /**
     * 表单验证失败
     */
    public static final int FROM_CHECK_FAIL = 1010009;

    /**
     * 表单验证失败
     */
    public static final int CREATE_TREE_ERROR = 1010010;

    /** 通用类 end **/

    /**
     * 用户类 start
     **/

    public static final int USER_SEC_AUTH_PHONE_NULL_ = 1010101;
    public static final int USER_SEC_AUTH_EMAIL_NULL_ = 1010102;
    public static final int USER_SEC_AUTH_WECHAT_NULL_ = 1010103;
    public static final int USER_PASSWORD_ERROR = 1010104;
    public static final int USER_SEC_AUTH_CODE_NULL = 1010105;
    public static final int USER_SEC_AUTH_CODE_ERROR = 1010106;

    /** 用户 end **/

    /**
     * 日志类 start
     **/
    public static final int OPERATION_LOG_BEAN_ID_NULL = 1070101;

    public static final int OPERATION_LOG_COMPARE_ID_VALUE_NULL = 1070102;

    public static final int ERROR_CODE_NOT_DEFINED = 1070103;

    /** 日志类 end **/

    /**
     * 系统类 start
     **/
    public static final int START_YEAR_NOT_EQUALS_END_YEAR = 1080101;

    public static final int SYSTEM_FUNCTION_NOT_DEFINE = 1080102;

    public static final int SYSTEM_FUNCTION_DISABLED = 1080103;

    public static final int OPERATION_LOG_PARA_IS_NULL = 1080104;

    public static final int OPERATION_LOG_CANNOT_GET = 1080105;

    public static final int START_OR_END_DATE_NULL = 1080106;

    public static final int NETWORK_RE_OK = 1080107;

    public static final int DISK_ALERT = 1080108;

    public static final int MEMORY_ALERT = 1080109;

    public static final int CPU_ALERT = 1080110;

    public static final int CMD_NOT_FOUND = 1080111;

    public static final int CMD_DISABLED = 1080112;

    public static final int SYSTEM_ALERT_AUTO_JOB = 1080113;

    /**
     * 命令消息通知
     */
    public static final int CMD_NOTIFY = 1080112;

    /** 系统类 end **/

    /**
     * 消息提醒类 start
     **/

    /**
     * 通用提醒代码
     */
    public static final int MESSAGE_NOTIFY_COMMON_CODE = 1140100;

    public static final int CMD_EXECUTED = 1140111;

    public static final int MESSAGE_DUPLICATE_SEND = 1140112;

    public static final int USER_OPERATION_REMIND_STAT = 1140117;

    /** 消息提醒类 end **/

    /**
     * 微信 start
     **/

    public static final int WXPAY_JSAPITOCKEN_ERROR = 1160100;

    public static final int WXPAY_TOKEN_SHA_ERROR = 1160101;

    /** 微信 end **/


    /**
     * 图片 start
     **/

    public static final int FILE_WRITE_ERROR = 1170100;

    public static final int FACE_ENGINE_INIT_ERROR = 1170101;

    public static final int FACE_ENGINE_ACTIVE_ERROR = 1170102;

    public static final int FACE_ENGINE_GET_ERROR = 1170103;

    public static final int GET_FACE_ERROR = 1170104;

    public static final int FACE_HAS_NO_FEATURE = 1170105;

    /** 图片 end **/
}
