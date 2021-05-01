package cn.mulanbay.face.api.web.bean.response.picture;

import cn.mulanbay.business.enums.MatchType;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 22:06
 */
public class FaceMatchVo {

    /**
     * 匹配者人脸
     */
    private Long matchFaceId;

    /**
     * 匹配者人脸
     */
    private String matchFaceUrl;

    /**
     * 哪张人脸被匹配
     */
    private Long matchedFaceId;

    /**
     * 哪张人脸被匹配
     */
    private String matchedFaceUrl;

    private Long userId;

    private String username;

    private String userAvatar;

    private MatchType matchType;

    /**
     * 匹配度
     */
    private double rate;

    public Long getMatchFaceId() {
        return matchFaceId;
    }

    public void setMatchFaceId(Long matchFaceId) {
        this.matchFaceId = matchFaceId;
    }

    public String getMatchFaceUrl() {
        return matchFaceUrl;
    }

    public void setMatchFaceUrl(String matchFaceUrl) {
        this.matchFaceUrl = matchFaceUrl;
    }

    public Long getMatchedFaceId() {
        return matchedFaceId;
    }

    public void setMatchedFaceId(Long matchedFaceId) {
        this.matchedFaceId = matchedFaceId;
    }

    public String getMatchedFaceUrl() {
        return matchedFaceUrl;
    }

    public void setMatchedFaceUrl(String matchedFaceUrl) {
        this.matchedFaceUrl = matchedFaceUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
