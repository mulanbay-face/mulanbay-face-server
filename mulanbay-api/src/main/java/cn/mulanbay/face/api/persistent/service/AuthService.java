package cn.mulanbay.face.api.persistent.service;

import cn.mulanbay.business.domain.*;
import cn.mulanbay.business.enums.MonitorBussType;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.face.api.persistent.dto.FastMenuDto;
import cn.mulanbay.face.api.persistent.dto.RoleFunctionDto;
import cn.mulanbay.face.api.persistent.dto.UserRoleDto;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fenghong on 2017/8/6.
 */
@Service
@Transactional
public class AuthService extends BaseHibernateDao {

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    public void deleteUser(Long userId) {
        try {
            String hql = "delete from UserSetting where userId = ?0  ";
            this.updateEntities(hql,userId);

            String hql2 = "delete from User where id = ?0  ";
            this.updateEntities(hql2,userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除用户异常", e);
        }
    }

    /**
     * 获取用户设置
     *
     * @param userId
     * @return
     */
    public UserSetting getUserSetting(Long userId) {
        try {
            String hql = "from UserSetting where userId = ?0  ";
            return (UserSetting) this.getEntityForOne(hql, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取用户设置异常", e);
        }
    }

    @Cacheable(value = "UserCache", key = "('pms:userSetting:').concat(#userId)")
    public UserSetting getUserSettingForCache(Long userId) {
        return this.getUserSetting(userId);
    }

    @Cacheable(value = "UserCache", key = "('pms:user:').concat(#userId)")
    public User getUserForCache(Long userId) {
        try {
            return (User) this.getEntityById(User.class, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取用户信息异常", e);
        }
    }

    /**
     * 通过手机号或者用户名查询用户
     *
     * @param username
     * @return
     */
    public User getUserByUsernameOrPhone(String username) {
        try {
            String hql = "from User where username = ?0 or phone=?1";
            List<User> list = this.getEntityListNoPageHQL(hql, username, username);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户信息异常", e);
        }
    }

    /**
     * 清除最后的登录token
     *
     * @param userId
     * @return
     */
    public void deleteLastLoginToken(Long userId) {
        try {
            String hql = "update User set lastLoginToken=null where id = ?0";
            this.updateEntities(hql, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "清除最后的登录token异常", e);
        }
    }

    /**
     * 通过最后一次登录token获取用户
     *
     * @param token
     * @return
     */
    public User getUserByLastLoginToken(String token) {
        try {
            String hql = "from User where lastLoginToken =?0";
            List<User> list = this.getEntityListNoPageHQL(hql, token);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户信息异常", e);
        }
    }

    /**
     * 获取用户当前积分
     *
     * @param userId
     * @return
     */
    public Integer getUserPoint(Long userId) {
        try {
            String hql = "select points from User where id=?0";
            return (Integer) this.getEntityForOne(hql, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户当前积分异常", e);
        }
    }

    /**
     * 注册
     *
     * @param user
     * @param us
     */
    public void userRegister(User user, UserSetting us) {
        try {
            this.saveEntity(user);
            us.setUserId(user.getId());
            this.saveEntity(us);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "注册异常", e);
        }
    }

    /**
     * 获取系统监控用户配置
     *
     * @param bussType
     * @return
     */
    public List<SystemMonitorUser> selectSystemMonitorUserList(MonitorBussType bussType) {
        try {
            String hql = "from SystemMonitorUser where bussType=0 or bussType=?0";
            return this.getEntityListNoPageHQL(hql, bussType);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取系统监控用户配置异常", e);
        }
    }

    /**
     * 获取系统监控用户配置
     *
     * @param userId
     * @return
     */
    public List<SystemMonitorUser> selectSystemMonitorUserList(Long userId) {
        try {
            String hql = "from SystemMonitorUser where userId=?0";
            return this.getEntityListNoPageHQL(hql, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取系统监控用户配置异常", e);
        }
    }

    /**
     * 更新头像
     *
     * @param userId
     * @return
     */
    public void updateAvatar(Long userId, String avatar) {
        try {
            String hql = "update User set avatar=?0 where id=?1";
            this.updateEntities(hql, avatar, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "更新头像异常", e);
        }
    }

    /**
     * 获取用户角色列表
     *
     * @param userId
     * @return
     */
    public List<UserRole> selectUserRoleList(Long userId) {
        try {
            String hql = "from UserRole where userId=?0";
            return this.getEntityListNoPageHQL(hql, userId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户角色列表异常", e);
        }
    }

    /**
     * 删除角色
     *
     * @param roleId
     * @return
     */
    public void deleteRole(Long roleId) {
        try {
            //step 1 ：删除用户角色
            String hql = "delete from UserRole where roleId=?0";
            this.updateEntities(hql, roleId);

            //step 2 ：删除角色功能点
            String hql2 = "delete from RoleFunction where roleId=?0";
            this.updateEntities(hql2, roleId);

            //step 3 ：删除角色
            String hql3 = "delete from Role where id=?0";
            this.updateEntities(hql3, roleId);

        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_DELETE_ERROR,
                    "删除角色异常", e);
        }
    }

    /**
     * 保存角色功能点
     *
     * @param roleId
     * @return
     */
    public void saveRoleFunction(Long roleId, String functionIds) {
        try {
            String hql = "delete from RoleFunction where roleId=?0";
            this.updateEntities(hql, roleId);
            if (StringUtil.isNotEmpty(functionIds)) {
                String[] ids = functionIds.split(",");
                List<RoleFunction> list = new ArrayList<>();
                for (String s : ids) {
                    RoleFunction rf = new RoleFunction();
                    rf.setRoleId(roleId);
                    rf.setFunctionId(Long.valueOf(s));
                    list.add(rf);
                }
                this.saveEntities(list.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存角色功能点异常", e);
        }
    }

    /**
     * 保存快捷菜单
     *
     * @param userId
     * @return
     */
    public void saveFastMenu(Long userId, String functionIds) {
        try {
            String hql = "delete from FastMenu where userId=?0";
            this.updateEntities(hql, userId);
            if (StringUtil.isNotEmpty(functionIds)) {
                String[] ids = functionIds.split(",");
                List<FastMenu> list = new ArrayList<>();
                int i = 0;
                for (String s : ids) {
                    FastMenu rf = new FastMenu();
                    rf.setUserId(userId);
                    rf.setFunctionId(Long.valueOf(s));
                    rf.setOrderIndex((short) i++);
                    list.add(rf);
                }
                this.saveEntities(list.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存快捷菜单异常", e);
        }
    }


    /**
     * 保存用户角色
     *
     * @param userId
     * @param roleIds
     */
    public void saveUserRole(Long userId, String roleIds) {
        try {
            String hql = "delete from UserRole where userId=?0";
            this.updateEntities(hql, userId);

            if (StringUtil.isNotEmpty(roleIds)) {
                List<UserRole> list = new ArrayList<>();
                String[] ids = roleIds.split(",");
                for (String s : ids) {
                    UserRole rf = new UserRole();
                    rf.setUserId(userId);
                    rf.setRoleId(Long.valueOf(s));
                    list.add(rf);
                }
                this.saveEntities(list.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存用户角色异常", e);
        }
    }

    /**
     * 保存用户监控配置
     *
     * @param userId
     * @param bussTypes
     */
    public void saveUserSystemMonitor(Long userId, String bussTypes) {
        try {
            String hql = "delete from SystemMonitorUser where userId=?0";
            this.updateEntities(hql, userId);

            if (StringUtil.isNotEmpty(bussTypes)) {
                List<SystemMonitorUser> list = new ArrayList<>();
                String[] ids = bussTypes.split(",");
                for (String s : ids) {
                    SystemMonitorUser rf = new SystemMonitorUser();
                    rf.setUserId(userId);
                    MonitorBussType mbt = MonitorBussType.getMonitorBussType(Integer.valueOf(s));
                    rf.setBussType(mbt);
                    rf.setCreatedTime(new Date());
                    rf.setSmsNotify(true);
                    rf.setSysMsgNotify(true);
                    rf.setWxNotify(true);
                    list.add(rf);
                }
                this.saveEntities(list.toArray());
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存用户角色异常", e);
        }
    }

    /**
     * 获取用户角色列表
     *
     * @param userId
     * @return
     */
    public List<UserRoleDto> selectUserRoleBeanList(Long userId) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select r.id as roleId,r.name as roleName,ur.role_id as userRoleId ");
            sb.append("from role r ");
            sb.append("left join user_role ur ");
            sb.append("on r.id = ur.role_id ");
            sb.append("and ur.user_id=?0 ");
            sb.append("order by r.id ");
            List<UserRoleDto> list = this.getEntityListWithClassSQL(sb.toString(), 0, 0, UserRoleDto.class, userId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户角色列表异常", e);
        }
    }

    /**
     * 获取角色功能点列表
     *
     * @param roleId
     * @return
     */
    public List<RoleFunctionDto> selectRoleFunctionBeanList(Long roleId) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select sf.id as functionId,sf.name as functionName,sf.name as functionShortName,sf.pid as pid,rf.function_id as roleFunctionId ");
            sb.append("from system_function sf ");
            sb.append("left join role_function rf ");
            sb.append("on sf.id = rf.function_id ");
            sb.append("and rf.role_id=?0 ");
            //sb.append("where (sf.function_data_type in (0,3) or sf.permission_auth=1) ");
            sb.append("where sf.permission_auth=1 order by sf.pid,sf.order_index");
            List<RoleFunctionDto> list = this.getEntityListWithClassSQL(sb.toString(), 0, 0, RoleFunctionDto.class, roleId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取角色功能点列表异常", e);
        }
    }

    /**
     * 新增用户
     *
     * @param user
     * @param us
     */
    public void createUser(User user, UserSetting us) {
        try {
            this.saveEntity(user);
            us.setUserId(user.getId());
            this.saveEntity(us);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "新增用户异常", e);
        }
    }

    /**
     * 修改用户
     *
     * @param user
     * @param us
     */
    public void updateUser(User user, UserSetting us) {
        try {
            this.updateEntity(user);
            this.updateEntity(us);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_UPDATE_ERROR,
                    "修改用户异常", e);
        }
    }

    /**
     * 获取角色功能点的菜单列表
     * 不需要授权的+需要授权且在用户角色功能里面的+需要授权但是始终显示的(目录类型)
     * @param roleId
     * @return
     */
    public List<SystemFunction> selectRoleFunctionMenuList(Long roleId, Boolean visible) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("from SystemFunction ");
            sb.append("where router =1 and ");
            sb.append("(permissionAuth=0 or (permissionAuth=1 and id in (select functionId from RoleFunction where roleId =?0)) or (permissionAuth=1 and alwaysShow=1) ) ");
            if (visible != null) {
                sb.append("and visible=" + (visible == true ? "1 " : "0 "));
            }
            sb.append("order by pid,orderIndex ");
            List<SystemFunction> list = this.getEntityListNoPageHQL(sb.toString(), roleId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取角色功能点的菜单列表", e);
        }
    }

    /**
     * 获取角色功能点的按钮列表
     *
     * @param roleId
     * @return
     */
    public List<String> selectRoleFPermsList(Long roleId) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT perms FROM system_function where perms is not null and ");
            sb.append("(permission_auth=0 or (permission_auth=1 and id in (select function_id from role_function where role_id =?0)) ) ");
            List<String> list = this.getEntityListNoPageSQL(sb.toString(), roleId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取角色功能点的按钮列表列表", e);
        }
    }

    /**
     * 获取用户快捷菜单
     *
     * @param userId
     * @return
     */
    public List<FastMenuDto> selectUserFastMenu(Long userId) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT fm.id,fm.function_id as functionId,sf.name as functionName,sf.path ");
            sb.append("FROM fast_menu fm,system_function sf ");
            sb.append("where fm.function_id = sf.id ");
            sb.append("and fm.user_id=?0 ");
            sb.append(" order by fm.order_index ");
            List<FastMenuDto> list = this.getEntityListWithClassSQL(sb.toString(), 0, 0, FastMenuDto.class, userId);
            return list;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取用户快捷菜单列表异常", e);
        }
    }

}
