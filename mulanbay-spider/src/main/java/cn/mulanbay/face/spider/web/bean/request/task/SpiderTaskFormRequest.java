package cn.mulanbay.face.spider.web.bean.request.task;

import cn.mulanbay.business.enums.*;

/**
 * ${DESCRIPTION}
 *
 * @author fenghong
 * @create 2018-02-14 15:55
 */
public class SpiderTaskFormRequest {

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(Long maxPage) {
        this.maxPage = maxPage;
    }

    public TaskPeriod getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(TaskPeriod taskPeriod) {
        this.taskPeriod = taskPeriod;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskSource getTaskSource() {
        return taskSource;
    }

    public void setTaskSource(TaskSource taskSource) {
        this.taskSource = taskSource;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
