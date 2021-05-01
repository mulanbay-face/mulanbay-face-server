package cn.mulanbay.face.api.handler.face;

import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.face.api.common.ApiErrorCode;
import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.ErrorInfo;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 人脸引擎池
 */
public class FaceEngineFactory extends BasePooledObjectFactory<FaceEngine> {

    private static final Logger logger = LoggerFactory.getLogger(FaceEngineFactory.class);

    /**
     * 计数
     */
    private static AtomicLong fec = new AtomicLong(0L);

    private String libPath;
    private String appId;
    private String sdkKey;
    private String activeKey;
    private EngineConfiguration engineConfiguration;


    public FaceEngineFactory(String libPath, String appId, String sdkKey, String activeKey, EngineConfiguration engineConfiguration) {
        this.appId = appId;
        this.sdkKey = sdkKey;
        this.activeKey = activeKey;
        this.libPath = libPath;
        this.engineConfiguration = engineConfiguration;
    }


    @Override
    public FaceEngine create() {
        logger.info("开始创建引擎");
        FaceEngine faceEngine = new FaceEngine(libPath);
        int activeCode = faceEngine.activeOnline(appId, sdkKey);
        if (activeCode != ErrorInfo.MOK.getValue() && activeCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            logger.error("引擎激活失败" + activeCode);
            throw new ApplicationException(ApiErrorCode.FACE_ENGINE_ACTIVE_ERROR, "引擎激活失败" + activeCode);
        }
        int initCode = faceEngine.init(engineConfiguration);
        if (initCode != ErrorInfo.MOK.getValue()) {
            logger.error("引擎初始化失败" + initCode);
            throw new ApplicationException(ApiErrorCode.FACE_ENGINE_INIT_ERROR, "引擎初始化失败" + initCode);
        }
        logger.info("创建引擎结束");
        long n = fec.incrementAndGet();
        logger.info("创建过的引擎总数:"+n);
        return faceEngine;
    }

    @Override
    public PooledObject<FaceEngine> wrap(FaceEngine faceEngine) {
        return new DefaultPooledObject<>(faceEngine);
    }


    @Override
    public void destroyObject(PooledObject<FaceEngine> p) throws Exception {
        FaceEngine faceEngine = p.getObject();
        int result = faceEngine.unInit();
        super.destroyObject(p);
    }
}
