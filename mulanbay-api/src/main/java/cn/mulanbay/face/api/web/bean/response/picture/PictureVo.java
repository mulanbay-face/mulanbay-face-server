package cn.mulanbay.face.api.web.bean.response.picture;

import cn.mulanbay.business.domain.Picture;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 22:06
 */
public class PictureVo extends Picture {

    /**
     * 带有人脸识别的地址
     */
    private String facesUrl;

    /**
     * 小图地址
     */
    private String ssUrl;

    /**
     * 中图地址
     */
    private String msUrl;

    public String getFacesUrl() {
        return facesUrl;
    }

    public void setFacesUrl(String facesUrl) {
        this.facesUrl = facesUrl;
    }

    public String getSsUrl() {
        return ssUrl;
    }

    public void setSsUrl(String ssUrl) {
        this.ssUrl = ssUrl;
    }

    public String getMsUrl() {
        return msUrl;
    }

    public void setMsUrl(String msUrl) {
        this.msUrl = msUrl;
    }

}
