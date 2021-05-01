package cn.mulanbay.face.api.web.controller;

import cn.mulanbay.business.domain.User;
import cn.mulanbay.business.enums.AuthType;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.util.ClazzUtils;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.face.api.util.TreeBeanUtil;
import cn.mulanbay.face.api.web.bean.request.common.GetEnumTreeRequest;
import cn.mulanbay.face.api.web.bean.request.common.GetYearTreeSearch;
import cn.mulanbay.face.api.web.bean.response.TreeBean;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 公共接口
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/common")
public class CommonController extends BaseController {

    /**
     * 获取月统计年份列表
     *
     * @return
     */
    @RequestMapping(value = "/getYearTree")
    public ResultBean getYearTree(GetYearTreeSearch sf) {
        try {
            User user = baseService.getObject(User.class, sf.getUserId());
            //最小年份由注册时间决定
            int minYear = Integer.valueOf(DateUtil.getFormatDate(user.getCreatedTime(), "yyyy"));
            int maxYear = Integer.valueOf(DateUtil.getFormatDate(new Date(), "yyyy"));
            List<TreeBean> list = new ArrayList<TreeBean>();
            for (int i = maxYear; i >= minYear; i--) {
                TreeBean tb = new TreeBean();
                tb.setId(i + "");
                tb.setText(i + "年");
                list.add(tb);
            }
            return callback(TreeBeanUtil.addRoot(list, sf.getNeedRoot()));
        } catch (Exception e) {
            throw new ApplicationException(ErrorCode.SYSTEM_ERROR, "获取年份列表树异常",
                    e);
        }
    }

    /**
     * 根据枚举类型获取类型树
     *
     * @return
     */
    @RequestMapping(value = "/getEnumTree")
    public ResultBean getEnumTree(GetEnumTreeRequest etr) {
        List<TreeBean> list = TreeBeanUtil.createTree(etr.getEnumClass(), etr.getIdType(), etr.getNeedRoot());
        return callback(list);
    }

    /**
     * 映射实体
     *
     * @return
     */
    @RequestMapping(value = "/getEnumClassNamesTree", method = RequestMethod.GET)
    public ResultBean getDomainClassNamesTree(Boolean needRoot) {
        List<TreeBean> treeBeans = new ArrayList<>();
        //根据指定的一个枚举类
        String packageName1 = AuthType.class.getPackage().getName();
        List<String> list = ClazzUtils.getClazzName(packageName1, false);
        Collections.sort(list);
        for (String s : list) {
            TreeBean treeBean = new TreeBean();
            int n = s.lastIndexOf(".");
            String className = s.substring(n + 1, s.length());
            treeBean.setId(className);
            treeBean.setText(className);
            treeBeans.add(treeBean);
        }
        return callback(TreeBeanUtil.addRoot(treeBeans, needRoot));
    }

}
