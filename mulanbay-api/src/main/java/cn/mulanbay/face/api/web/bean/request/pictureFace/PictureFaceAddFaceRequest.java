package cn.mulanbay.face.api.web.bean.request.pictureFace;

import cn.mulanbay.common.aop.BindUser;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/1/8
 */
public class PictureFaceAddFaceRequest implements BindUser {

    private Long userId;

    private String title;

    private Boolean open;

    private String originalUrl;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}
