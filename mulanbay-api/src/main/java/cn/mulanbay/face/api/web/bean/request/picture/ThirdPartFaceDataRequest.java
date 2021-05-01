package cn.mulanbay.face.api.web.bean.request.picture;

import cn.mulanbay.business.enums.Platform;

import java.util.List;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/4/18
 */
public class ThirdPartFaceDataRequest {

    /**
     * web的URL
     */
    private String url;

    private Platform platform;

    /**
     * 照片数组
     */
    private List pictures;

    public ThirdPartFaceDataRequest() {
    }

    public ThirdPartFaceDataRequest(String url, Platform platform, List pictures) {
        this.url = url;
        this.platform = platform;
        this.pictures = pictures;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public List getPictures() {
        return pictures;
    }

    public void setPictures(List pictures) {
        this.pictures = pictures;
    }
}
