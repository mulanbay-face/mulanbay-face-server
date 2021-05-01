package cn.mulanbay.face.spider.handler;

import cn.mulanbay.business.domain.ErrorCodeDefine;
import cn.mulanbay.business.domain.SystemConfig;
import cn.mulanbay.business.domain.SystemFunction;
import cn.mulanbay.business.enums.CommonStatus;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.business.handler.HandlerCmd;
import cn.mulanbay.business.handler.HandlerInfo;
import cn.mulanbay.business.handler.HandlerResult;
import cn.mulanbay.common.util.IPAddressUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.persistent.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@Component
public class SystemConfigHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(SystemConfigHandler.class);

    private String hostIpAddress;

    @Value("${system.nodeId}")
    private String nodeId;

    /**
     * 是否采用内存缓存
     */
    @Value("${system.configCache.byMemoryCache}")
    boolean byMemoryCache = false;

    @Autowired
    BaseService baseService;

    @Autowired
    CacheHandler cacheHandler;

    /**
     * key:urlAddress_supportMethod,例如：/buyRecord/edit_POST
     */
    private static Map<String, SystemFunction> functionMap = new HashMap<>();

    /**
     * 角色功能点缓存
     */
    private static Map<String, String> roleFunctionMap = new HashMap<>();

    /**
     * 配置
     */
    private static Map<String, String> configMap = new HashMap<>();


    public SystemConfigHandler() {
        super("系统配置");
    }

    /**
     * 重载功能点
     */
    public void reloadConfigs() {
        //获取所有的功能点
        List<SystemConfig> list = baseService.getBeanList(SystemConfig.class, 0, 0, null);
        configMap.clear();
        int configMapSize = 0;
        //封装
        for (SystemConfig sc : list) {
            if (sc.getStatus() == CommonStatus.DISABLE) {
                continue;
            } else {
                configMap.put(sc.getCode(), sc.getConfigValue());
                configMapSize++;
            }
        }
        if (!byMemoryCache) {
            cacheHandler.delete("system_config");
            cacheHandler.setHash("system_config", configMap, 0);
        }
        logger.debug("初始化了" + configMapSize + "条系统配置");
    }

    /**
     * 重载功能点
     */
    public void reloadFunctions() {
        //获取所有的功能点
        List<SystemFunction> list = baseService.getBeanList(SystemFunction.class, 0, 0, null);
        functionMap.clear();
        int urlMapSize = 0;
        //封装
        for (SystemFunction sf : list) {
            if (sf.getUrlAddress() == null || sf.getSupportMethods() == null) {
                continue;
            } else {
                String methods = sf.getSupportMethods();
                String[] ss = methods.split(",");
                for (String s : ss) {
                    // 数据库中功能点路径不需要设置项目名，因为项目名称在实际过程中会被修改过
                    String key = getUrlMethodKey(sf.getUrlAddress(), s);
                    functionMap.put(key, sf);
                    urlMapSize++;
                }
            }
        }
        if (!byMemoryCache) {
            cacheHandler.delete("system_function");
            cacheHandler.setHash("system_function", functionMap, 0);
        }
        logger.debug("初始化了" + urlMapSize + "条功能点记录");
    }

    /**
     * 目前返回本地地址
     *
     * @return
     */
    public String getServerDomain() {
        return hostIpAddress;
    }

    /**
     * 重载角色功能点
     */
    public void reloadRoleFunctions() {

    }

    @Override
    public void init() {
        super.init();
        //初始化
        hostIpAddress = IPAddressUtil.getLocalIpAddress();
        reloadFunctions();
        reloadRoleFunctions();
        reloadConfigs();
    }

    @Override
    public void reload() {
        super.reload();
        reloadFunctions();
        reloadRoleFunctions();
        reloadConfigs();
    }

    /**
     * 通过URL查询
     *
     * @param url
     * @param method
     * @return
     */
    public SystemFunction getFunction(String url, String method) {
        String key = getUrlMethodKey(url, method);
        if (byMemoryCache) {
            return functionMap.get(key);
        } else {
            return cacheHandler.getHash("system_function", key, SystemFunction.class);
        }
    }

    /**
     * 获取key
     *
     * @param url
     * @param method
     * @return
     */
    private String getUrlMethodKey(String url, String method) {
        return url + "_" + method.toUpperCase();
    }

    /**
     * 角色是否授权
     *
     * @param functionId
     * @param roleId
     * @return
     */
    public boolean isRoleAuth(Long roleId, Long functionId) {
        String rfKey = generateRoleFunctionKey(roleId, functionId);
        String s = null;
        if (byMemoryCache) {
            s = roleFunctionMap.get(rfKey);
        } else {
            s = cacheHandler.getHash("role_function", rfKey, String.class);
        }
        boolean b = (s == null ? false : true);
        logger.debug("角色是否授权,key:" + rfKey + ",auth:" + b);
        return b;
    }

    private String generateRoleFunctionKey(Long roleId, Long functionId) {
        return roleId.toString() + "_" + functionId.toString();
    }

    public String getHostIpAddress() {
        return hostIpAddress;
    }

    public String getNodeId() {
        return nodeId;
    }

    public BaseService getBaseService() {
        return baseService;
    }

    /**
     * 获取字符类型配置
     *
     * @param code
     * @return
     */
    public String getStringConfig(String code) {
        return configMap.get(code);
    }

    /**
     * 获取double类型配置
     *
     * @param code
     * @return
     */
    public Double getDoubleConfig(String code) {
        String s = configMap.get(code);
        return Double.valueOf(s);
    }

    /**
     * 获取int类型配置
     *
     * @param code
     * @return
     */
    public Integer getIntegerConfig(String code) {
        String s = configMap.get(code);
        return Integer.valueOf(s);
    }

    /**
     * 获取布尔类型配置
     *
     * @param code
     * @return
     */
    public Boolean getBooleanConfig(String code) {
        String s = configMap.get(code);
        if (StringUtil.isNotEmpty(s)) {
            s = s.toLowerCase();
            return s.equals("true") || s.equals("1");
        } else {
            return null;
        }
    }

    @Override
    public HandlerInfo getHandlerInfo() {
        HandlerInfo hi = super.getHandlerInfo();
        hi.addDetail("nodeId", nodeId);
        hi.addDetail("serverIp", hostIpAddress);
        hi.addDetail("byMemoryCache", String.valueOf(byMemoryCache));
        hi.addDetail("functionMap size", String.valueOf(functionMap.size()));
        hi.addDetail("roleFunctionMap size", String.valueOf(roleFunctionMap.size()));
        hi.addDetail("configMap size", String.valueOf(configMap.size()));
        return hi;
    }

    /**
     * 获取错误代码定义
     *
     * @param code
     * @return
     */
    public ErrorCodeDefine getErrorCodeDefine(int code) {
        return baseService.getObject(ErrorCodeDefine.class, code);
    }

    @Override
    public List<HandlerCmd> getSupportCmdList() {
        List<HandlerCmd> list = new ArrayList<>();
        list.add(new HandlerCmd("reloadFunctions", "重新加载功能点配置"));
        list.add(new HandlerCmd("reloadRoleFunctions", "重新加载角色功能点配置"));
        list.add(new HandlerCmd("reloadConfigs", "重新加载系统配置"));
        return list;
    }

    @Override
    public HandlerResult handle(String cmd) {
        if (cmd.equals("reloadFunctions")) {
            reloadFunctions();
        } else if (cmd.equals("reloadRoleFunctions")) {
            reloadRoleFunctions();
        } else if (cmd.equals("reloadConfigs")) {
            reloadConfigs();
        }
        return super.handle(cmd);
    }
}