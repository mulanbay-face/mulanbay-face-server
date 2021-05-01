package cn.mulanbay.face.api.web.bean.response.picture;

import cn.mulanbay.business.enums.DownloadType;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/1/19
 */
public class PicturePreDownloadVo {

    private Long id;

    private DownloadType downloadType;

    private String originalUrl;

    private Boolean paid;

    private Long price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DownloadType getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(DownloadType downloadType) {
        this.downloadType = downloadType;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
