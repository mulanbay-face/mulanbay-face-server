package cn.mulanbay.face.api.web.bean.request.log;

import cn.mulanbay.business.enums.FunctionDataType;
import cn.mulanbay.business.enums.FunctionType;
import cn.mulanbay.business.enums.UrlType;
import cn.mulanbay.common.aop.FullEndDateTime;
import cn.mulanbay.persistent.query.CrossType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

import java.util.Date;

public class OperationLogSearch extends PageSearch implements FullEndDateTime {

    @Query(fieldName = "userName,urlAddress,method,ipAddress,hostIpAddress", op = Parameter.Operator.LIKE, crossType = CrossType.OR)
    private String name;

    @Query(fieldName = "paras", op = Parameter.Operator.LIKE)
    private String paras;

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    public Long userId;

    @Query(fieldName = "occurEndTime", op = Parameter.Operator.GTE)
    private Date startDate;

    @Query(fieldName = "occurEndTime", op = Parameter.Operator.LTE)
    private Date endDate;

    @Query(fieldName = "systemFunction.id", op = Parameter.Operator.EQ)
    private Long functionId;

    @Query(fieldName = "method", op = Parameter.Operator.EQ)
    private String method;

    @Query(fieldName = "systemFunction.urlType", op = Parameter.Operator.EQ)
    private UrlType urlType;

    @Query(fieldName = "systemFunction.functionType", op = Parameter.Operator.EQ)
    private FunctionType functionType;

    @Query(fieldName = "systemFunction.functionDataType", op = Parameter.Operator.EQ)
    private FunctionDataType functionDataType;

    @Query(fieldName = "idValue", op = Parameter.Operator.EQ)
    private String idValue;

    /**
     * 直接用sql模式
     */
    @Query(fieldName = "", op = Parameter.Operator.SQL)
    private String beanName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParas() {
        return paras;
    }

    public void setParas(String paras) {
        this.paras = paras;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public UrlType getUrlType() {
        return urlType;
    }

    public void setUrlType(UrlType urlType) {
        this.urlType = urlType;
    }

    public FunctionType getFunctionType() {
        return functionType;
    }

    public void setFunctionType(FunctionType functionType) {
        this.functionType = functionType;
    }

    public FunctionDataType getFunctionDataType() {
        return functionDataType;
    }

    public void setFunctionDataType(FunctionDataType functionDataType) {
        this.functionDataType = functionDataType;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


}
