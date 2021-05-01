package cn.mulanbay.face.api.web.bean.request.picture;

import cn.mulanbay.business.enums.DownloadType;
import cn.mulanbay.business.enums.SearchType;
import cn.mulanbay.common.aop.BindUser;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 15:31
 */
public class PictureEditRequest implements BindUser {

    private Long userId;

    @NotNull(message = "{validate.picture.id.NotNull}")
    private Long id;

    /**
     * 搜索类型
     */
    @NotNull(message = "{validate.picture.searchType.NotNull}")
    private SearchType searchType;

    /**
     * 下载类型
     */
    @NotNull(message = "{validate.picture.downloadType.NotNull}")
    private DownloadType downloadType;

    @NotEmpty(message = "{validate.picture.title.NotEmpty}")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
