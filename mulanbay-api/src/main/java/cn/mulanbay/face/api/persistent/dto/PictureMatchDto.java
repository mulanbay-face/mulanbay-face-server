package cn.mulanbay.face.api.persistent.dto;

import java.math.BigInteger;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/11
 */
public class PictureMatchDto {

    private BigInteger id;

    private String title;

    private BigInteger userId;

    private String url;

    /**
     * 哪张人脸被匹配
     */
    private BigInteger matchedFaceId;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BigInteger getMatchedFaceId() {
        return matchedFaceId;
    }

    public void setMatchedFaceId(BigInteger matchedFaceId) {
        this.matchedFaceId = matchedFaceId;
    }
}
