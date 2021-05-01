package cn.mulanbay.face.api.web.bean.request.picture;

import cn.mulanbay.common.aop.BindUser;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/1/11
 */
public class Uf2pMatchRequest implements BindUser {

    private Long userId;

    @NotNull(message = "{validate.pictureFace.userFaceId.NotNull}")
    private Long userFaceId;

    private Long cursor;

    private Integer pageSize=10;

    /**
     * 最低匹配率
     */
    private double minRate=0.8;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserFaceId() {
        return userFaceId;
    }

    public void setUserFaceId(Long userFaceId) {
        this.userFaceId = userFaceId;
    }

    public Long getCursor() {
        return cursor;
    }

    public void setCursor(Long cursor) {
        this.cursor = cursor;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public double getMinRate() {
        return minRate;
    }

    public void setMinRate(double minRate) {
        this.minRate = minRate;
    }
}
