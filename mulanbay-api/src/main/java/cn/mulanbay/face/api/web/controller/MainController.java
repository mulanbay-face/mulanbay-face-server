package cn.mulanbay.face.api.web.controller;

import cn.mulanbay.business.domain.SystemFunction;
import cn.mulanbay.business.domain.User;
import cn.mulanbay.business.domain.UserSetting;
import cn.mulanbay.business.enums.AuthType;
import cn.mulanbay.business.enums.FunctionDataType;
import cn.mulanbay.business.enums.LogLevel;
import cn.mulanbay.business.enums.UserStatus;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.IPAddressUtil;
import cn.mulanbay.common.util.NumberUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.face.api.common.ApiErrorCode;
import cn.mulanbay.face.api.common.CacheKey;
import cn.mulanbay.face.api.handler.*;
import cn.mulanbay.face.api.persistent.service.AuthService;
import cn.mulanbay.face.api.web.bean.LoginUser;
import cn.mulanbay.face.api.web.bean.request.UserCommonRequest;
import cn.mulanbay.face.api.web.bean.request.auth.LoginRequest;
import cn.mulanbay.face.api.web.bean.request.auth.RegisterRequest;
import cn.mulanbay.face.api.web.bean.request.auth.UserSecAuthRequest;
import cn.mulanbay.face.api.web.bean.response.MyInfoResponse;
import cn.mulanbay.face.api.web.bean.response.auth.RouterMetaVo;
import cn.mulanbay.face.api.web.bean.response.auth.RouterVo;
import cn.mulanbay.face.api.web.bean.response.auth.SecAuthInfoResponse;
import cn.mulanbay.web.bean.response.ResultBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * 主页功能
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/main")
public class MainController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Value("${system.version}")
    private String version;

    @Value("${security.login.maxFail}")
    private int loginMaxFail;

    @Autowired
    AuthService authService;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    PmsNotifyHandler pmsNotifyHandler;

    @Autowired
    PmsMessageSendHandler pmsMessageSendHandler;

    @Autowired
    WxpayHandler wxpayHandler;

    @Autowired
    TokenHandler tokenHandler;

    /**
     * 登陆
     *
     * @return
     */
    @RequestMapping(value = "/loginAuth", method = RequestMethod.POST)
    public ResultBean loginAuth(@RequestBody @Valid LoginRequest login) {
        //判定验证码
        String verifyKey = CacheKey.getKey(CacheKey.CAPTCHA_CODE, login.getUuid());
        String serverCode = cacheHandler.getForString(verifyKey);
        if (StringUtil.isEmpty(serverCode) || !serverCode.equals(login.getCode())) {
            return callbackErrorCode(ErrorCode.USER_VERIFY_CODE_ERROR);
        }
        //错误次数验证
        String username = login.getUsername();
        String failKey = CacheKey.getKey(CacheKey.USER_LOGIN_FAIL, username);
        Integer fails = cacheHandler.get(failKey, Integer.class);
        if (fails != null && fails >= loginMaxFail) {
            return callbackErrorCode(ErrorCode.USER_LOGIN_FAIL_MAX);
        }
        //用户验证
        User user = authService.getUserByUsernameOrPhone(username);
        if (user == null) {
            return callbackErrorCode(ErrorCode.USER_NOTFOUND);
        } else {
            if (user.getStatus() == UserStatus.DISABLE) {
                return callbackErrorCode(ErrorCode.USER_IS_STOP);
            }
            if (user.getExpireTime() != null && user.getExpireTime().before(new Date())) {
                return callbackErrorCode(ErrorCode.USER_EXPIRED);
            }
            // 检测密码
            String rp = user.getPassword();
            String encodePassword = tokenHandler.encodePassword(login.getPassword());
            if (!rp.equalsIgnoreCase(encodePassword)) {
                if (fails == null) {
                    fails = 1;
                } else {
                    fails++;
                }
                cacheHandler.set(failKey, fails, 300);
                return callbackErrorCode(ErrorCode.USER_PASSWORD_ERROR);
            }
            String token = doLogin(user);
            addLoginNotifyMsg(user.getId(), user.getUsername());
            Map map = new HashMap<>();
            map.put("token", token);
            return callback(map);
        }
    }

    private void addLoginNotifyMsg(Long userId, String username) {
        try {
            // 发送系统通知
            pmsNotifyHandler.addMessageToNotifier(ApiErrorCode.USER_LOGIN, "用户登录系统", "用户[" + username + "]登录系统", new Date(), null);
            pmsNotifyHandler.addNotifyMessage(ApiErrorCode.USER_LOGIN, "您的账户正在登录系统", "您的账户[" + username + "]登录系统", userId, new Date());
        } catch (Exception e) {
            logger.error("增加登录提醒日志异常", e);
        }

    }

    /**
     * 登录
     *
     * @param user
     */
    private String doLogin(User user) {
        //更新登录信息
        user.setLastLoginIp(IPAddressUtil.getIpAddress(request));
        user.setLastLoginTime(new Date());
        LoginUser lu = tokenHandler.createLoginUser(user);
        String token = tokenHandler.createToken(lu);
        user.setLastLoginToken(lu.getLoginToken());
        baseService.updateObject(user);
        return token;
    }

    /**
     * 二次认证
     *
     * @return
     */
    @RequestMapping(value = "/secAuth", method = RequestMethod.POST)
    public ResultBean secAuth(@RequestBody @Valid UserSecAuthRequest sa) {
        User user = baseService.getObject(User.class, sa.getUserId());
        String serverAuthCode;
        if (user.getSecAuthType() == AuthType.PASSWORD) {
            serverAuthCode = user.getPassword();
        } else {
            String key = CacheKey.getKey(CacheKey.USER_SEC_AUTH_CODE, sa.getUserId().toString());
            //服务器端存的授权码，加密过的
            serverAuthCode = cacheHandler.getForString(key);
            if (StringUtil.isEmpty(serverAuthCode)) {
                return callbackErrorCode(ApiErrorCode.USER_SEC_AUTH_CODE_NULL);
            }
        }
        String clientAuthCode = tokenHandler.encodePassword(sa.getAuthCode());
        if (!serverAuthCode.equals(clientAuthCode)) {
            return callbackErrorCode(ApiErrorCode.USER_SEC_AUTH_CODE_ERROR);
        }
        LoginUser lu = tokenHandler.createLoginUser(user);
        tokenHandler.verifyToken(lu);
        return callback(null);
    }

    /**
     * 发送二次认证码
     *
     * @return
     */
    @RequestMapping(value = "/sendSecAuthCode", method = RequestMethod.POST)
    public ResultBean sendSecAuthCode(UserCommonRequest sa) {
        User user = baseService.getObject(User.class, sa.getUserId());
        String serverAuthCode = null;
        boolean res = true;
        int expMin = 5;
        if (user.getSecAuthType() == AuthType.PASSWORD) {
            serverAuthCode = user.getPassword();
        } else {
            String code = NumberUtil.getRandNum(6);
            String title = "用户二次认证授权码";
            String content = "您当前的二次认证授权码为" + code + ",在" + expMin + "分钟内有效";
            if (user.getSecAuthType() == AuthType.EMAIL) {
                res = pmsMessageSendHandler.sendMail(title, content, user.getEmail());
            } else if (user.getSecAuthType() == AuthType.WECHAT) {
                res = wxpayHandler.sendTemplateMessage(user.getId(), title, content, new Date(), LogLevel.NORMAL, null);
            }
            serverAuthCode = tokenHandler.encodePassword(code);
        }
        String key = CacheKey.getKey(CacheKey.USER_SEC_AUTH_CODE, sa.getUserId().toString());
        cacheHandler.set(key, serverAuthCode, expMin * 60);
        return callback(res);
    }

    /**
     * 获取二次认证码
     *
     * @return
     */
    @RequestMapping(value = "/getSecAuthInfo", method = RequestMethod.GET)
    public ResultBean getSecAuthInfo(UserCommonRequest sa) {
        User user = baseService.getObject(User.class, sa.getUserId());
        SecAuthInfoResponse res = new SecAuthInfoResponse();
        res.setSecAuthType(user.getSecAuthType());
        if (user.getSecAuthType() == AuthType.PASSWORD) {
            res.setAddress("用户的登录密码");
        } else if (user.getSecAuthType() == AuthType.EMAIL) {
            res.setAddress(user.getEmail());
        } else if (user.getSecAuthType() == AuthType.WECHAT) {
            //todo 微信的名称
            res.setAddress(user.getUsername());
        }
        return callback(res);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResultBean logout(UserCommonRequest uc) {
        tokenHandler.deleteLoginUser(request);
        if (uc.getUserId() != null) {
            authService.deleteLastLoginToken(uc.getUserId());
        }
        return callback(null);
    }

    /**
     * 我的信息
     *
     * @return
     */
    @RequestMapping(value = "/myInfo", method = RequestMethod.GET)
    public ResultBean myInfo(UserCommonRequest uc) {
        Long userId = uc.getUserId();
        User user = baseService.getObject(User.class, userId);
        MyInfoResponse res = new MyInfoResponse();
        res.setUsername(user.getUsername());
        res.setNickname(user.getNickname());
        res.setVersion(version);
        res.setAvatar(user.getAvatar());
        return callback(res);
    }

    /**
     * 注册
     *
     * @return
     */
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    public ResultBean userRegister(@RequestBody @Valid RegisterRequest rr) {
        //判定验证码
        String verifyKey = CacheKey.getKey(CacheKey.CAPTCHA_CODE, rr.getUuid());
        String serverCode = cacheHandler.getForString(verifyKey);
        if (StringUtil.isEmpty(serverCode) || !serverCode.equals(rr.getCode())) {
            return callbackErrorCode(ErrorCode.USER_VERIFY_CODE_ERROR);
        }
        User user = new User();
        user.setCreatedTime(new Date());
        user.setUsername(rr.getUsername());
        user.setPassword(tokenHandler.encodePassword(rr.getPassword()));
        user.setLevel(3);
        user.setNickname(rr.getUsername());
        user.setPoints(0);
        UserSetting us = new UserSetting();
        us.setCreatedTime(new Date());
        us.setSendEmail(true);
        us.setSendWxMessage(true);
        us.setStatScore(true);
        authService.userRegister(user, us);
        //自动登录
        String token = doLogin(user);
        Map map = new HashMap<>();
        map.put("token", token);
        return callback(map);
    }

    /**
     * 路由表
     *
     * @return
     */
    @RequestMapping(value = "/getRouters", method = RequestMethod.GET)
    public ResultBean getRouters(UserCommonRequest ucr) {
        LoginUser loginUser = tokenHandler.getLoginUser(request);
        Long roleId = loginUser.getRoleId();
        List<SystemFunction> sfList = authService.selectRoleFunctionMenuList(roleId, null);
        List<SystemFunction> funcTree = this.getFunctionTree(sfList, 0L);
        return callback(buildMenus(funcTree));
    }

    /**
     * 直接采用RuoYi的代码实现
     *
     * @param menus
     * @return
     */
    private List<RouterVo> buildMenus(List<SystemFunction> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SystemFunction sf : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(sf.getVisible().booleanValue() == true ? false : true);
            router.setName(getRouteName(sf));
            router.setPath(getRouterPath(sf));
            router.setComponent(getComponent(sf));
            router.setMeta(new RouterMetaVo(sf.getName(), sf.getImageName(),!sf.getCache()));
            List<SystemFunction> cMenus = sf.getChildren();
            if (!cMenus.isEmpty() && cMenus.size() > 0 && FunctionDataType.M.equals(sf.getFunctionDataType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(sf)) {
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(sf.getPath());
                children.setComponent(sf.getComponent());
                children.setName(StringUtils.capitalize(sf.getPath()));
                children.setMeta(new RouterMetaVo(sf.getName(), sf.getImageName(),!sf.getCache()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    private List<SystemFunction> getFunctionTree(List<SystemFunction> list, long pid) {
        List<SystemFunction> res = new ArrayList<>();
        for (SystemFunction sf : list) {
            if (sf.getParentId() == pid) {
                res.add(sf);
                List<SystemFunction> children = getFunctionTree(list, sf.getId().longValue());
                sf.setChildren(children);
            }
        }
        return res;
    }

    /**
     * 获取路由名称
     * 如果配置的path包含斜杠/,则过滤掉，vue的组件名称不推荐斜杠/
     * 比如path：buyRecord/dateStat,则name:BuyRecordDateStat
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SystemFunction menu) {
        String path = menu.getPath();
        String[] ss = path.split("/");
        String routerName="";
        for(String s : ss){
            routerName += StringUtils.capitalize(s);
        }
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }

        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SystemFunction menu) {
        String routerPath = menu.getPath();
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && FunctionDataType.M.equals(menu.getFunctionDataType())
                && (false == menu.getFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SystemFunction menu) {
        //去除菜单类型判断：&& FunctionDataType.C.equals(menu.getFunctionDataType())
        return menu.getParentId().intValue() == 0
                && (false == menu.getFrame());
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SystemFunction menu) {
        String component = "Layout";
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        }
        return component;
    }

}
