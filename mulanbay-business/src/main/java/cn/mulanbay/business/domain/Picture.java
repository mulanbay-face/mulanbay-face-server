package cn.mulanbay.business.domain;

import cn.mulanbay.business.enums.DownloadType;
import cn.mulanbay.business.enums.PictureSource;
import cn.mulanbay.business.enums.SearchType;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 照片
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "picture")
public class Picture implements java.io.Serializable {

    // Fields

    private static final long serialVersionUID = 4311541035363351436L;

    private Long id;
    private Long userId;
    /**
     * 搜索类型
     */
    private SearchType searchType;
    /**
     * 下载类型
     */
    private DownloadType downloadType;
    /**
     * 标题
     */
    private String title;

    private String keywords;

    /**
     * 文件名(处理后的),不包含路径
     */
    private String fileName;
    /**
     * 原始文件地址
     */
    private String originalUrl;
    /**
     * 文件名(处理后的),包含路径
     */
    private String url;

    /**
     * 图片来源
     */
    private PictureSource source;
    /**
     * 拍摄时间
     */
    private Date shotTime;
    private String remark;
    private Date createdTime;
    private Date lastModifyTime;


    // Constructors

    /**
     * default constructor
     */
    public Picture() {
    }


    // Property accessors
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "search_type")
    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }

    @Basic
    @Column(name = "download_type")
    public DownloadType getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(DownloadType downloadType) {
        this.downloadType = downloadType;
    }

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "keywords")
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Basic
    @Column(name = "file_name")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Basic
    @Column(name = "original_url")
    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    @Basic
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Basic
    @Column(name = "source")
    public PictureSource getSource() {
        return source;
    }

    public void setSource(PictureSource source) {
        this.source = source;
    }

    @Basic
    @Column(name = "shot_time")
    public Date getShotTime() {
        return shotTime;
    }

    public void setShotTime(Date shotTime) {
        this.shotTime = shotTime;
    }

    @Basic
    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "created_time")
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Basic
    @Column(name = "last_modify_time")
    public Date getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Date lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    @Transient
    public String getSearchTypeName() {
        return searchType==null ? null:searchType.getName();
    }

    @Transient
    public String getDownloadTypeName() {
        return downloadType==null ? null:downloadType.getName();
    }
}
