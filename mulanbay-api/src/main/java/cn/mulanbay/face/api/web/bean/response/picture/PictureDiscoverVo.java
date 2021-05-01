package cn.mulanbay.face.api.web.bean.response.picture;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 22:06
 */
public class PictureDiscoverVo {

    private Long id;

    private String title;

    private Long likes;

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


    private Long userId;

    private String username;

    private String userAvatar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

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
}
