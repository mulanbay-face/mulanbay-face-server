package cn.mulanbay.business.domain;

import cn.mulanbay.business.enums.Platform;
import cn.mulanbay.business.enums.TaskType;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 爬虫日志
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "spider_log")
public class SpiderLog implements java.io.Serializable {

    private static final long serialVersionUID = -8290503768108861704L;

    private Long id;

    private Long taskId;

    private Platform platform;
    private TaskType taskType;

    /**
     * url
     */
    private String url;

    private String remark;
    private Date createdTime;
    private Date lastModifyTime;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "task_id")
    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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
    @Column(name = "task_type")
    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
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
    public String getPlatformName() {
        if (this.platform != null) {
            return platform.getName();
        } else {
            return null;
        }
    }

    @Transient
    public String getTaskTypeName() {
        if (this.taskType != null) {
            return taskType.getName();
        } else {
            return null;
        }
    }
}
