package cn.mulanbay.face.spider.handler.job;

import cn.mulanbay.schedule.ParaCheckResult;
import cn.mulanbay.schedule.TaskResult;
import cn.mulanbay.schedule.job.AbstractBaseJob;

/**
 * @Description:加载周期类的job
 * @Author: fenghong
 * @Create : 2021/4/20
 */
public class SpiderTaskLoadJob extends AbstractBaseJob {

    @Override
    public TaskResult doTask() {
        return null;
    }

    @Override
    public ParaCheckResult checkTriggerPara() {
        return null;
    }

    @Override
    public Class getParaDefine() {
        return null;
    }
}
