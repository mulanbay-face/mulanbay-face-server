package cn.mulanbay.face.spider.web.bean.request.task;

import cn.mulanbay.business.enums.*;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

/**
 * Created by fenghong on 2017/2/1.
 */
public class SpiderTaskSearch extends PageSearch {

    @Query(fieldName = "name", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "status", op = Parameter.Operator.EQ)
    private CommonStatus status;

    @Query(fieldName = "platform", op = Parameter.Operator.EQ)
    private Platform platform;

    @Query(fieldName = "taskPeriod", op = Parameter.Operator.EQ)
    private TaskPeriod taskPeriod;

    @Query(fieldName = "taskType", op = Parameter.Operator.EQ)
    private TaskType taskType;

    @Query(fieldName = "taskSource", op = Parameter.Operator.EQ)
    private TaskSource taskSource;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public TaskPeriod getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(TaskPeriod taskPeriod) {
        this.taskPeriod = taskPeriod;
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
}
