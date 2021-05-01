package cn.mulanbay.business.domain;

import cn.mulanbay.business.enums.FaceSource;

import javax.persistence.*;
import java.util.Date;

/**
 * 照片的人脸
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "picture_face")
public class PictureFace implements java.io.Serializable {

    // Fields
    private static final long serialVersionUID = -5410566166142643559L;

    private Long id;
    private Long userId;
    private Long pictureId;
    /**
     * 目前和ID同值
     */
    private Long faceId;

    /**
     * 标题
     */
    private String title;
    /**
     * 路径
     */
    private String url;
    /**
     * 文件名(处理后的),不包含路径
     */
    private String fileName;
    /**
     * 人脸在照片中的坐标
     */
    private String position;
    /**
     * 匹配次数，系统搜索
     */
    private Long matchs;

    /**
     * 命中次数，比对正确，这个需要用户确认
     */
    private Long hits;

    /**
     * 是否开启，关闭情况下无法被识别
     */
    private Boolean open;

    /**
     * 人脸来源
     */
    private FaceSource source;

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
    public PictureFace() {
    }

    /**
     * 手动设置
     * @return
     */
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
    @Column(name = "picture_id")
    public Long getPictureId() {
        return pictureId;
    }

    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
    }

    @Basic
    @Column(name = "face_id")
    public Long getFaceId() {
        return faceId;
    }

    public void setFaceId(Long faceId) {
        this.faceId = faceId;
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
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
    @Column(name = "position")
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Basic
    @Column(name = "matchs")
    public Long getMatchs() {
        return matchs;
    }

    public void setMatchs(Long matchs) {
        this.matchs = matchs;
    }

    @Basic
    @Column(name = "hits")
    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }

    @Basic
    @Column(name = "open")
    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    @Basic
    @Column(name = "source")
    public FaceSource getSource() {
        return source;
    }

    public void setSource(FaceSource source) {
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
    public String getSourceName() {
        return source==null ? null:source.getName();
    }
}
