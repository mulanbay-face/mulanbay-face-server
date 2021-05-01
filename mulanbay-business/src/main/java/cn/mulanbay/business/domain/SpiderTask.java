package cn.mulanbay.business.domain;

import cn.mulanbay.business.enums.*;

import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * 爬虫任务
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Entity
@Table(name = "spider_task")
public class SpiderTask implements java.io.Serializable {

    private static final long serialVersionUID = -8290503768108861704L;

    private Long id;
    /**
     * 名称
     */
    private String name;
    private Platform platform;
    /**
     * url的规则
     */
    private String urlPattern;
    /**
     * 最后一次的页码
     */
    private Long page;
    private Long maxPage;
    private TaskPeriod taskPeriod;
    /**
     * 获取数据的正则表达式
     */
    private String regex;
    private TaskType taskType;
    private TaskSource taskSource;
    /**
     * 优先级
     */
    private Long priority;
    //状态
    private CommonStatus status;
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
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @Column(name = "url_pattern")
    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Basic
    @Column(name = "page")
    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    @Basic
    @Column(name = "max_page")
    public Long getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(Long maxPage) {
        this.maxPage = maxPage;
    }

    @Basic
    @Column(name = "task_period")
    public TaskPeriod getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(TaskPeriod taskPeriod) {
        this.taskPeriod = taskPeriod;
    }

    @Basic
    @Column(name = "regex")
    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
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
    @Column(name = "task_source")
    public TaskSource getTaskSource() {
        return taskSource;
    }

    public void setTaskSource(TaskSource taskSource) {
        this.taskSource = taskSource;
    }

    @Basic
    @Column(name = "priority")
    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    @Basic
    @Column(name = "status")
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
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
    public String getStatusName() {
        if (this.status != null) {
            return status.getName();
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

    @Transient
    public String getTaskSourceName() {
        if (this.taskSource != null) {
            return taskSource.getName();
        } else {
            return null;
        }
    }

    @Transient
    public String getTaskPeriodName() {
        if (this.taskPeriod != null) {
            return taskPeriod.getName();
        } else {
            return null;
        }
    }

    @Transient
    public String getPlatformName() {
        if (this.platform != null) {
            return platform.getName();
        } else {
            return null;
        }
    }

}
