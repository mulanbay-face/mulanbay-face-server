package cn.mulanbay.face.spider.scrapper;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.face.spider.scrapper.proxy.MyProxy;
import cn.mulanbay.face.spider.scrapper.proxy.ProxyPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by fenghong on 2016/6/16.
 * 代理池管理
 * webmagic有自动检查代理的机制，最开始时会发送经常连不上情况
 * 启动时会去{java.io.tmpdir}中（windows下为：C:\Users\{当前用户名}\AppData\Local\Temp\webmagic）
 * 读取上次已经成功的代理配置文件lastUse.proxy,如果以前的代理不使用，需要删除lastUse.proxy文件
 * 如果采用定时去刷新代理设置，会导致经常连不上
 */
@Component
public class ProxyHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProxyHandler.class);

    @Value("${system.nodeId}")
    private String nodeId;

    /**
     * 是否需要使用最后使用过的代理
     */
    @Value("${spider.isUseLastProxy}")
    private boolean isUseLastProxy;

    @Value("${spider.enable.proxy}")
    private boolean enableProxy;

    /**
     * 采用自己的连接池（改写了webmagic的池）
     */
    private ProxyPool proxyPool;

    public ProxyHandler() {
        super("ProxyHandler");
    }

    @Override
    public void init() {
        logger.info(this.getHandlerName() + " begin init...");
        if (enableProxy) {
            proxyPool = new ProxyPool(isUseLastProxy);
            proxyPool.addProxy(getProxyConfigs());
        } else {
            logger.warn("不启动代理");
        }

    }

    @Override
    public void reload() {
        if (enableProxy && proxyPool != null) {
            proxyPool.reloadProxy(getProxyConfigs());
        }
    }

    private String[][] getProxyConfigs() {
        return null;
    }

    public void disableProxy(String serverIp, int port) {

    }

    /**
     * 获取连接池
     *
     * @return
     */
    public MyProxy getProxy() {
        return proxyPool == null ? null : proxyPool.getProxy();
    }

    /**
     * 返回代理连接池
     *
     * @param proxy
     * @param statusCode
     */
    public void returnProxy(MyProxy proxy, int statusCode) {
        if (proxy != null && proxyPool != null) {
            logger.debug("回收代理池,serverIp:" + proxy.getHost() + ",port:" + proxy.getPort() + ",statusCode=" + statusCode);
            //todo 需要重写,因为如果不是200类型，ProxyPool会设置delay时间的
            statusCode = MyProxy.SUCCESS;
            proxyPool.returnProxy(proxy, statusCode);
        }
    }
}
