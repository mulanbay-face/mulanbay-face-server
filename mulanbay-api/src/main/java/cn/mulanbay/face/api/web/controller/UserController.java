package cn.mulanbay.face.api.web.controller;

import cn.mulanbay.business.domain.*;
import cn.mulanbay.business.enums.AuthType;
import cn.mulanbay.business.enums.MonitorBussType;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.BeanCopy;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.face.api.common.ApiErrorCode;
import cn.mulanbay.face.api.handler.PictureHandler;
import cn.mulanbay.face.api.handler.ThreadPoolHandler;
import cn.mulanbay.face.api.handler.TokenHandler;
import cn.mulanbay.face.api.handler.WxpayHandler;
import cn.mulanbay.face.api.persistent.dto.UserRoleDto;
import cn.mulanbay.face.api.persistent.service.AuthService;
import cn.mulanbay.face.api.persistent.service.DataService;
import cn.mulanbay.face.api.persistent.service.WechatService;
import cn.mulanbay.face.api.util.TreeBeanUtil;
import cn.mulanbay.face.api.web.bean.LoginUser;
import cn.mulanbay.face.api.web.bean.request.CommonBeanDeleteRequest;
import cn.mulanbay.face.api.web.bean.request.CommonBeanGetRequest;
import cn.mulanbay.face.api.web.bean.request.UserCommonRequest;
import cn.mulanbay.face.api.web.bean.request.auth.UserFormRequest;
import cn.mulanbay.face.api.web.bean.request.auth.UserSearch;
import cn.mulanbay.face.api.web.bean.request.auth.UserSystemMonitorRequest;
import cn.mulanbay.face.api.web.bean.request.user.*;
import cn.mulanbay.face.api.web.bean.response.TreeBean;
import cn.mulanbay.face.api.web.bean.response.user.UserInfoResponse;
import cn.mulanbay.face.api.web.bean.response.user.UserProfileResponse;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * ??????
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    private static Class<User> beanClass = User.class;

    @Value("${picture.user.avatar.path}")
    String avatarFilePath;

    @Autowired
    AuthService authService;

    @Autowired
    DataService dataService;

    @Autowired
    TokenHandler tokenHandler;

    @Autowired
    WxpayHandler wxpayHandler;

    @Autowired
    WechatService wechatService;

    @Autowired
    ThreadPoolHandler threadPoolHandler;

    @Autowired
    PictureHandler pictureHandler;

    /**
     * ?????????
     * @return
     */
    @RequestMapping(value = "/getUserTree")
    public ResultBean getUserTree(Boolean needRoot) {
        try {
            UserSearch sf = new UserSearch();
            PageResult<User> pageResult = getUserResult(sf);
            List<TreeBean> list = new ArrayList<TreeBean>();
            List<User> gtList = pageResult.getBeanList();
            for (User gt : gtList) {
                TreeBean tb = new TreeBean();
                tb.setId(gt.getId().toString());
                tb.setText(gt.getUsername());
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, needRoot));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "?????????????????????",
                    e);
        }
    }

    /**
     * ?????????????????????
     *
     * @param urt
     * @return
     */
    @RequestMapping(value = "/getUserRoleTree")
    public ResultBean getUserRoleTree(UserRoleTreeRequest urt) {
        try {
            List<UserRoleDto> urList = authService.selectUserRoleBeanList(urt.getUserId());
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (UserRoleDto ur : urList) {
                TreeBean tb = new TreeBean();
                tb.setId(ur.getRoleId().toString());
                tb.setText(ur.getRoleName());
                if (ur.getUserRoleId() != null) {
                    tb.setChecked(true);
                }
                list.add(tb);
            }
            Boolean b = urt.getSeparate();
            if (b != null && b) {
                Map map = new HashMap<>();
                map.put("treeData", list);
                List checkedKeys = new ArrayList();
                for (UserRoleDto sf : urList) {
                    if (sf.getUserRoleId() != null) {
                        checkedKeys.add(sf.getRoleId().longValue());
                    }
                }
                map.put("checkedKeys", checkedKeys);
                return callback(map);
            } else {
                return callback(TreeBeanUtil.addRoot(list, urt.getNeedRoot()));
            }
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "???????????????????????????",
                    e);
        }
    }

    /**
     * ???????????????????????????
     *
     * @param urt
     * @return
     */
    @RequestMapping(value = "/getSystemMonitorTree")
    public ResultBean getSystemMonitorTree(UserRoleTreeRequest urt) {
        try {
            List<SystemMonitorUser> urList = authService.selectSystemMonitorUserList(urt.getUserId());
            List<TreeBean> treeBeans = new ArrayList<>();
            List checkedKeys = new ArrayList();
            for (MonitorBussType sfb : MonitorBussType.values()) {
                if (sfb == MonitorBussType.ALL) {
                    continue;
                }
                TreeBean treeBean = new TreeBean();
                treeBean.setId(String.valueOf(sfb.getValue()));
                treeBean.setText(sfb.getName());
                if (checkMonitorExit(sfb, urList)) {
                    treeBean.setChecked(true);
                    checkedKeys.add(sfb.getValue());
                }
                treeBeans.add(treeBean);
            }
            Boolean b = urt.getSeparate();
            if (b != null && b) {
                Map map = new HashMap<>();
                map.put("treeData", treeBeans);
                map.put("checkedKeys", checkedKeys);
                return callback(map);
            } else {
                return callback(TreeBeanUtil.addRoot(treeBeans, urt.getNeedRoot()));
            }
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "?????????????????????????????????",
                    e);
        }
    }

    private boolean checkMonitorExit(MonitorBussType v, List<SystemMonitorUser> urList) {
        if (StringUtil.isEmpty(urList)) {
            return false;
        } else {
            for (SystemMonitorUser smu : urList) {
                if (smu.getBussType() == v) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @RequestMapping(value = "/saveSystemMonitor", method = RequestMethod.POST)
    public ResultBean saveSystemMonitor(@RequestBody @Valid UserSystemMonitorRequest ur) {
        authService.saveUserSystemMonitor(ur.getUserId(), ur.getBussTypes());
        return callback(null);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public ResultBean getData(UserSearch sf) {
        PageResult<User> pageResult = getUserResult(sf);
        return callbackDataGrid(pageResult);
    }

    private PageResult<User> getUserResult(UserSearch sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.ASC);
        pr.addSort(sort);
        PageResult<User> qr = baseService.getBeanResult(pr);
        return qr;
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid UserFormRequest bean) {
        User user = new User();
        BeanCopy.copyProperties(bean, user);
        // ????????????
        String encodePassword = tokenHandler.encodePassword(bean.getPassword());
        user.setPassword(encodePassword);
        user.setSecAuthType(bean.getSecAuthType());
        user.setCreatedTime(new Date());
        user.setLevel(3);
        user.setPoints(0);
        UserSetting us = new UserSetting();
        //us.setUserId(user.getId());
        us.setSendEmail(true);
        us.setSendWxMessage(true);
        us.setStatScore(false);
        us.setCreatedTime(new Date());
        authService.createUser(user, us);
        return callback(null);
    }


    /**
     * ????????????
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@Valid CommonBeanGetRequest getRequest) {
        User br = baseService.getObject(beanClass, getRequest.getId());
        return callback(br);
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid UserFormRequest bean) {
        User user = baseService.getObject(beanClass, bean.getId());
        String originalPawword = user.getPassword();
        BeanCopy.copyProperties(bean, user);
        String password = bean.getPassword();
        if (null != password && !password.isEmpty()) {
            // ????????????
            String encodePassword = tokenHandler.encodePassword(bean.getPassword());
            user.setPassword(encodePassword);
        } else {
            user.setPassword(originalPawword);
        }
        user.setLastModifyTime(new Date());
        UserSetting us = authService.getUserSetting(user.getId());
        us.setLastModifyTime(new Date());
        authService.updateUser(user, us);
        return callback(null);
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonBeanDeleteRequest deleteRequest) {
        String[] ss = deleteRequest.getIds().split(",");
        for(String s : ss){
            authService.deleteUser(Long.valueOf(s));
        }
        return callback(null);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @RequestMapping(value = "/getMyInfo", method = RequestMethod.GET)
    public ResultBean getMyInfo() {
        Long userId = this.getCurrentUserId();
        UserInfoResponse response = this.getUserInfo(userId);
        return callback(response);
    }

    private UserInfoResponse getUserInfo(Long userId) {
        User user = baseService.getObject(beanClass, userId);
        UserSetting us = authService.getUserSetting(userId);
        UserInfoResponse response = new UserInfoResponse();
        BeanCopy.copyProperties(user, response);
        BeanCopy.copyProperties(us, response);
        return response;
    }

    /**
     * ???????????????????????????????????????
     *
     * @return
     */
    @RequestMapping(value = "/getMyInfoWithPerms", method = RequestMethod.GET)
    public ResultBean getMyInfoWithPerms() {
        LoginUser loginUser = tokenHandler.getLoginUser(request);
        Long roleId = loginUser.getRoleId();
        Long userId = loginUser.getUserId();
        UserInfoResponse user = this.getUserInfo(userId);
        Map map = new HashMap();
        map.put("user", user);
        map.put("roles", new String[]{"admin"});
        List<String> perms = authService.selectRoleFPermsList(roleId);
        map.put("permissions", perms);
        return callback(map);
    }

    /**
     * ???????????????????????????????????????
     *
     * @return
     */
    @RequestMapping(value = "/getProfile", method = RequestMethod.GET)
    public ResultBean getProfile() {
        LoginUser loginUser = tokenHandler.getLoginUser(request);
        Long roleId = loginUser.getRoleId();
        Long userId = loginUser.getUserId();
        UserProfileResponse ups = new UserProfileResponse();
        User user = baseService.getObject(User.class, userId);
        UserSetting us = authService.getUserSetting(userId);
        BeanCopy.copyProperties(user, ups);
        BeanCopy.copyProperties(us, ups);
        if (roleId != null) {
            Role role = baseService.getObject(Role.class, roleId);
            ups.setRoleName(role.getName());
        }
        return callback(ups);
    }

    /**
     * ???????????????????????????????????????
     *
     * @return
     */
    @RequestMapping(value = "/editProfile", method = RequestMethod.POST)
    public ResultBean editProfile(@RequestBody @Valid UserProfileRequest upr) {
        User user = baseService.getObject(beanClass, upr.getUserId());
        if (upr.getSecAuthType() == AuthType.WECHAT) {
            //????????????????????????
            UserWxpayInfo wx = wxpayHandler.getWxpayInfo(user.getId());
            if (StringUtil.isEmpty(wx.getOpenId())) {
                return callbackErrorCode(ApiErrorCode.USER_SEC_AUTH_WECHAT_NULL_);
            }
        }
        UserSetting us = authService.getUserSetting(upr.getUserId());
        BeanCopy.copyProperties(upr, us);
        user.setLastModifyTime(new Date());
        BeanCopy.copyProperties(upr, user);
        us.setStatScore(true);
        us.setLastModifyTime(new Date());
        authService.updateUser(user, us);
        return callback(null);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @RequestMapping(value = "/editPassword", method = RequestMethod.POST)
    public ResultBean editPassword(@RequestBody @Valid UserPasswordEditRequest eui) {
        User user = baseService.getObject(beanClass, eui.getUserId());
        String pp = tokenHandler.encodePassword(eui.getOldPassword());
        if (!user.getPassword().equals(pp)) {
            return callbackErrorCode(ApiErrorCode.USER_PASSWORD_ERROR);
        }
        String newPP = tokenHandler.encodePassword(eui.getNewPassword());
        user.setPassword(newPP);
        user.setLastModifyTime(new Date());
        baseService.updateObject(user);
        return callback(null);
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    @RequestMapping(value = "/editMyInfo", method = RequestMethod.POST)
    public ResultBean editMyInfo(@RequestBody @Valid EditMyInfoRequest eui) {
        User user = baseService.getObject(beanClass, eui.getUserId());
        String pp = tokenHandler.encodePassword(eui.getPassword());
        if (!user.getPassword().equals(pp)) {
            return callbackErrorCode(ApiErrorCode.USER_PASSWORD_ERROR);
        }
        if (eui.getSecAuthType() == AuthType.SMS && StringUtil.isEmpty(eui.getPhone())) {
            return callbackErrorCode(ApiErrorCode.USER_SEC_AUTH_PHONE_NULL_);
        }
        if (eui.getSecAuthType() == AuthType.EMAIL && StringUtil.isEmpty(eui.getEmail())) {
            return callbackErrorCode(ApiErrorCode.USER_SEC_AUTH_EMAIL_NULL_);
        }
        if (eui.getSecAuthType() == AuthType.WECHAT) {
            //????????????????????????
            UserWxpayInfo wx = wxpayHandler.getWxpayInfo(user.getId());
            if (StringUtil.isEmpty(wx.getOpenId())) {
                return callbackErrorCode(ApiErrorCode.USER_SEC_AUTH_WECHAT_NULL_);
            }
        }
        UserSetting us = authService.getUserSetting(eui.getUserId());
        user.setUsername(eui.getUsername());
        user.setNickname(eui.getNickname());
        user.setBirthday(eui.getBirthday());
        user.setPhone(eui.getPhone());
        user.setEmail(eui.getEmail());
        user.setSecAuthType(eui.getSecAuthType());
        user.setLastModifyTime(new Date());
        if (!StringUtil.isEmpty(eui.getNewPassword())) {
            // ????????????
            String encodePassword = tokenHandler.encodePassword(eui.getNewPassword());
            user.setPassword(encodePassword);
        }
        baseService.updateObject(user);
        BeanCopy.copyProperties(eui, us);
        us.setStatScore(true);
        us.setLastModifyTime(new Date());
        baseService.updateObject(us);
        return callback(null);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @RequestMapping(value = "/getUserWxpayInfo", method = RequestMethod.GET)
    public ResultBean getUserWxpayInfo(Long userId) {
        UserWxpayInfo uw = wechatService.getUserWxpayInfo(userId, wxpayHandler.getAppId());
        if (uw == null) {
            uw = new UserWxpayInfo();
            uw.setUserId(userId);
        }
        return callback(uw);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @RequestMapping(value = "/editUserWxpayInfo", method = RequestMethod.POST)
    public ResultBean editUserWxpayInfo(@RequestBody @Valid EditUserWxpayInfoRequest eui) {
        if (eui.getId() == null) {
            UserWxpayInfo uw = new UserWxpayInfo();
            BeanCopy.copyProperties(eui, uw);
            uw.setAppId(wxpayHandler.getAppId());
            uw.setCreatedTime(new Date());
            baseService.saveObject(uw);
        } else {
            UserWxpayInfo uw = baseService.getObject(UserWxpayInfo.class, eui.getId());
            BeanCopy.copyProperties(eui, uw);
            uw.setLastModifyTime(new Date());
            baseService.updateByMergeObject(uw);
        }
        return callback(null);
    }

    /**
     * ??????
     *
     * @return
     */
    @RequestMapping(value = "/offline", method = RequestMethod.POST)
    public ResultBean offline(@RequestBody @Valid UserCommonRequest ucr) {
        return callback(null);
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @RequestMapping(value = "/getResidentCity", method = RequestMethod.GET)
    public ResultBean getResidentCity(UserCommonRequest ucr) {
        UserSetting us = authService.getUserSettingForCache(ucr.getUserId());
        return callback(us.getResidentCity());
    }

    /**
     * ????????????
     */
    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    public ResultBean uploadAvatar(@RequestParam("avatarfile") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String extractFilename = pictureHandler.writeFile(file,avatarFilePath);
            //???????????????
            authService.updateAvatar(this.getCurrentUserId(), extractFilename);
            return callback(extractFilename);
        } else {
            return callbackErrorInfo("????????????");
        }
    }

}
