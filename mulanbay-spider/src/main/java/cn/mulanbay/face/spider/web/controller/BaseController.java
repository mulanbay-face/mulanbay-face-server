package cn.mulanbay.face.spider.web.controller;

import cn.mulanbay.business.handler.MessageHandler;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.ValidateError;
import cn.mulanbay.face.spider.handler.CacheHandler;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.service.BaseService;
import cn.mulanbay.web.bean.response.DataGrid;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
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
    CacheHandler cacheHandler;

    private static ResultBean defaultResultBean = new ResultBean();

    private static List emptyList = new ArrayList<>();

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

}
