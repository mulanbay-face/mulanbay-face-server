package cn.mulanbay.face.api.web.bean.request.picture;

import cn.mulanbay.business.enums.DownloadType;
import cn.mulanbay.business.enums.SearchType;
import cn.mulanbay.common.aop.BindUser;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 15:31
 */
public class PictureUploadRequest implements BindUser {

    private Long userId;
    /**
     * 搜索类型
     */
    private SearchType searchType;
    /**
     * 下载类型
     */
    private DownloadType downloadType;

    private String title;

    private String keywords;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    public DownloadType getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(DownloadType downloadType) {
        this.downloadType = downloadType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
