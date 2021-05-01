package cn.mulanbay.face.api.persistent.service;

import cn.mulanbay.business.enums.FunctionDataType;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.face.api.persistent.dto.common.SystemFunctionBean;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DataService extends BaseHibernateDao {

    /**
     * 获取功能点的菜单列表
     *
     * @return
     */
    public List<SystemFunctionBean> getSystemFunctionMenu() {
        try {
            String sql = "select id,name,pid from system_function where function_data_type=?0 or function_data_type =?1 order by pid,order_index ";
            List<SystemFunctionBean> result = this.getEntityListWithClassSQL(sql, -1, 0,
                    SystemFunctionBean.class, FunctionDataType.M.ordinal(), FunctionDataType.C.ordinal());
            return result;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取功能点的菜单列表异常", e);
        }
    }

    /**
     * 获取功能点的列表
     *
     * @return
     */
    public List<SystemFunctionBean> getSystemFunctionList() {
        try {
            String sql = "select id,name,pid from system_function order by pid ";
            List<SystemFunctionBean> result = this.getEntityListWithClassSQL(sql, -1, 0, SystemFunctionBean.class);
            return result;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取功能点的列表异常", e);
        }
    }

}
