package cn.mulanbay.face.api.web.bean.response.picture;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 22:07
 */
public class PictureFaceVo {

    private Long id;

    private String url;

    private Long matchs;

    private Long hits;

    private Boolean open;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getMatchs() {
        return matchs;
    }

    public void setMatchs(Long matchs) {
        this.matchs = matchs;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }
}
