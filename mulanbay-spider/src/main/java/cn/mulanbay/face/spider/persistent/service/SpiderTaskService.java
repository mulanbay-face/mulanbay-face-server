package cn.mulanbay.face.spider.persistent.service;

import cn.mulanbay.business.domain.SpiderLog;
import cn.mulanbay.business.domain.SpiderTask;
import cn.mulanbay.business.enums.CommonStatus;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/4/19
 */
@Transactional
@Service
public class SpiderTaskService extends BaseHibernateDao {


    /**
     * 获取有效的任务
     * @return
     */
    public List<SpiderTask> getEnableSpiderTaskList(){
        try {
            String hql = "from SpiderTask where status=?0 ";
            return this.getEntityListNoPageHQL(hql, CommonStatus.ENABLE);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取图片人脸列表异常", e);
        }
    }

    /**
     * 更新页码
     * @return
     */
    public void updatePage(Long id,Long page){
        try {
            String hql = "update SpiderTask set page=?0,lastModifyTime=?1 where id=?2 ";
            this.updateEntities(hql,page,new Date(),id);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新页码异常", e);
        }
    }

    /**
     * 增加日志
     * @return
     */
    public void addLog(SpiderLog log){
        try {
            this.saveEntity(log);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "增加日志异常", e);
        }
    }

    /**
     * 删除任务
     * @return
     */
    public void deleteSpiderTask(Long id){
        try {
            String hql = "delete from SpiderTask where id=?0";
            this.updateEntities(hql,id);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "增加日志异常", e);
        }
    }

    /**
     * 更新任务
     * @return
     */
    public void updateSpiderTask(Long id,CommonStatus status,Long page){
        try {
            String sql = "update SpiderTask set status=?0,page=?1 where id=?2";
            int n = this.updateEntities(sql,status,page,id);
            System.out.println(id+"记录影响行数:"+n);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新任务异常", e);
        }
    }

    /**
     * 更新任务
     * @return
     */
    public void updateSpiderTask(SpiderTask spiderTask){
        try {
            this.updateEntity(spiderTask);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新任务异常", e);
        }
    }

    /**
     * URL是否处理过
     * @return
     */
    public boolean urlHandled(String url) {
        try {
            String sql = "select count(0) from spider_log where url=?0 ";
            Long n = this.getCountSQL(sql, url);
            return n > 0 ? true : false;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "URL是否处理过异常", e);
        }
    }
}
