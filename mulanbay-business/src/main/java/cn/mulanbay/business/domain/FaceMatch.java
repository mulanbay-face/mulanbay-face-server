package cn.mulanbay.business.domain;

import cn.mulanbay.business.enums.MatchType;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 人脸匹配记录
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "face_match")
public class FaceMatch implements java.io.Serializable {

    // Fields

    private static final long serialVersionUID = 4311541035363351436L;

    private Long id;

    private MatchType matchType;

    /**
     * 匹配者人脸
     */
    private Long matchFaceId;

    /**
     * 哪张人脸被匹配
     */
    private Long matchedFaceId;

    /**
     * 匹配度
     */
    private double rate;

    private String remark;
    private Date createdTime;
    private Date lastModifyTime;


    // Constructors

    /**
     * default constructor
     */
    public FaceMatch() {
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
    @Column(name = "match_type")
    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    @Basic
    @Column(name = "match_face_id")
    public Long getMatchFaceId() {
        return matchFaceId;
    }

    public void setMatchFaceId(Long matchFaceId) {
        this.matchFaceId = matchFaceId;
    }

    @Basic
    @Column(name = "matched_face_id")
    public Long getMatchedFaceId() {
        return matchedFaceId;
    }

    public void setMatchedFaceId(Long matchedFaceId) {
        this.matchedFaceId = matchedFaceId;
    }

    @Basic
    @Column(name = "rate")
    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
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
