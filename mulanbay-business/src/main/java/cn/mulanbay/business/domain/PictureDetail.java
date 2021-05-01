package cn.mulanbay.business.domain;

import cn.mulanbay.business.enums.Platform;

import javax.persistence.*;
import java.util.Date;

/**
 * 照片详情
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "picture_detail")
public class PictureDetail implements java.io.Serializable {

    // Fields

    private static final long serialVersionUID = 4311541035363351436L;

    /**
     * Picture表的ID
     */
    private Long id;
    private Long userId;
    /**
     * 平台
     */
    private Platform platform;
    /**
     * 图片的原始链接地址
     */
    private String picUrl;
    /**
     * 图片所在网址
     */
    private String webUrl;
    private String remark;
    private Date createdTime;
    private Date lastModifyTime;


    // Constructors

    /**
     * default constructor
     */
    public PictureDetail() {
    }

    @Id
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
    @Column(name = "platform")
    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    @Basic
    @Column(name = "pic_url")
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Basic
    @Column(name = "web_url")
    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
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

}
