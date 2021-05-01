package cn.mulanbay.face.api.web.bean.request.pictureFace;

import cn.mulanbay.common.aop.BindUser;

import javax.validation.constraints.NotNull;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 15:31
 */
public class PictureFaceUpdateOpenRequest implements BindUser {

    private Long userId;

    @NotNull(message = "{validate.pictureFace.id.NotNull}")
    private Long id;

    @NotNull(message = "{validate.pictureFace.open.NotNull}")
    private Boolean open;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }
}
