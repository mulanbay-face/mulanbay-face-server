package cn.mulanbay.face.api.web.bean.request.picture;

import cn.mulanbay.common.aop.BindUser;

import javax.validation.constraints.NotNull;

/**
 * @Description: 用户图片的人脸与用户图片匹配
 * @Author: fenghong
 * @Create : 2021/1/11
 */
public class Pf2pMatchRequest implements BindUser {

    private Long userId;

    @NotNull(message = "{validate.pictureFace.pictureFaceId.NotNull}")
    private Long pictureFaceId;

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

    public Long getPictureFaceId() {
        return pictureFaceId;
    }

    public void setPictureFaceId(Long pictureFaceId) {
        this.pictureFaceId = pictureFaceId;
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
