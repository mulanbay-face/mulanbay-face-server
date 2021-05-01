package cn.mulanbay.face.api.web.controller;

import cn.mulanbay.business.enums.DateGroupType;
import cn.mulanbay.business.handler.MessageHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.ValidateError;
import cn.mulanbay.common.util.DateUtil;
import cn.mulanbay.face.api.common.ApiErrorCode;
import cn.mulanbay.face.api.common.CacheKey;
import cn.mulanbay.face.api.handler.CacheHandler;
import cn.mulanbay.face.api.handler.TokenHandler;
import cn.mulanbay.face.api.web.bean.LoginUser;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.web.bean.response.DataGrid;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller的基类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected BaseService baseService;

    @Autowired
    protected MessageHandler messageHandler;

    @Autowired
    protected TokenHandler tokenHandler;

    @Autowired
    CacheHandler cacheHandler;

    // # 统计图里面的子标题是否需要总的统计值
    @Value("${system.chart.subTitle.hasTotal}")
    boolean chartSubTitleHasTotal;

    private static ResultBean defaultResultBean = new ResultBean();

    private static List emptyList = new ArrayList<>();

    /**
     * 获取当前用户的编号
     *
     * @return
     */
    protected Long getCurrentUserId() {
        LoginUser lu = tokenHandler.getLoginUser(request);
        return lu == null ? null : lu.getUserId();
    }

    /**
     * 操作实体并且设置ID值，主要提供给操作日志使用
     * 目前针对新增
     *
     * @param id 需要保存的hibernate实体对象的ID值
     */
    protected void setOperateBeanId(Object id) {
        if (id != null) {
            String cacheKey = CacheKey.getKey(CacheKey.USER_OPERATE_OP, request.getRequestedSessionId(), request.getServletPath());
            cacheHandler.set(cacheKey, id.toString(), 300);
        }
    }

    /**
     * 跟easyui结合
     *
     * @param pr
     * @return
     */
    protected ResultBean callbackDataGrid(PageResult<?> pr) {
        ResultBean rb = new ResultBean();
        DataGrid dg = new DataGrid();
        dg.setPage(pr.getPage());
        dg.setTotal(pr.getMaxRow());
        dg.setRows(pr.getBeanList() == null ? emptyList : pr.getBeanList());
        rb.setData(dg);
        return rb;
    }


    protected ResultBean callback(Object o) {
        if (o == null) {
            return defaultResultBean;
        }
        ResultBean rb = new ResultBean();
        rb.setData(o);
        return rb;
    }

    /**
     * 直接返回错误代码
     *
     * @param errorCode
     * @return
     */
    protected ResultBean callbackErrorCode(int errorCode) {
        ResultBean rb = new ResultBean();
        rb.setCode(errorCode);
        ValidateError ve = messageHandler.getErrorCodeInfo(errorCode);
        rb.setMessage(ve.getErrorInfo());
        return rb;
    }

    /**
     * 直接返回错误信息
     *
     * @param msg
     * @return
     */
    protected ResultBean callbackErrorInfo(String msg) {
        ResultBean rb = new ResultBean();
        rb.setCode(ErrorCode.DO_BUSS_ERROR);
        rb.setMessage(msg);
        return rb;
    }

    /**
     * 获取用户的数据
     * 查找实体时id和userId同时绑定
     * todo 后期可以根据当前用户身份，如果是管理员则直接根据id查询
     *
     * @param c
     * @param id
     * @param userId
     * @param <T>
     * @return
     */
    protected <T> T getUserEntity(Class<T> c, Serializable id, Long userId) {
        T bean = baseService.getObjectWithUser(c, id, userId);
        if (bean == null) {
            // 找不到直接抛异常
            throw new ApplicationException(ApiErrorCode.USER_ENTITY_NOT_FOUND);
        }
        return bean;
    }

    /**
     * 删除用户数据
     *
     * @param c
     * @param ids
     * @param userId
     */
    protected void deleteUserEntity(Class c, Serializable[] ids, Long userId) {
        baseService.deleteObjectsWithUser(c, ids, userId);
    }

    /**
     * 删除用户数据
     *
     * @param c
     * @param strIds
     * @param userId
     */
    protected void deleteUserEntity(Class c, String strIds,Class idClass, Long userId) {
        baseService.deleteObjectsWithUser(c,strIds,idClass,userId);
    }


    /**
     * 获取时间区间
     *
     * @param dateGroupType
     * @param date
     * @return
     */
    protected Date[] getStatDateRange(DateGroupType dateGroupType, Date date) {
        Date[] dd = new Date[2];
        if (dateGroupType == DateGroupType.DAY) {
            dd[0] = DateUtil.getFromMiddleNightDate(date);
            dd[1] = DateUtil.getTodayTillMiddleNightDate(date);
        } else if (dateGroupType == DateGroupType.MONTH) {
            dd[0] = DateUtil.getFromMiddleNightDate(DateUtil.getFirstDayOfMonth(date));
            Date endDate = DateUtil.getLastDayOfMonth(date);
            dd[1] = DateUtil.getTodayTillMiddleNightDate(endDate);
        } else {
            int year = Integer.valueOf(DateUtil.getFormatDate(date, "yyyy"));
            dd[0] = DateUtil.getDate(year + "-01-01 00:00:00", DateUtil.Format24Datetime);
            dd[1] = DateUtil.getDate(year + "-12-31 23:59:59", DateUtil.Format24Datetime);
        }
        return dd;
    }
}
