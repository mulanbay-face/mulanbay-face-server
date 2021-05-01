package cn.mulanbay.face.api.handler.face;

import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.face.api.common.ApiErrorCode;
import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.toolkit.ImageInfo;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 虹软的人脸处理
 */
@Component
public class AsFaceHandler extends BaseHandler {

    public final static Logger logger = LoggerFactory.getLogger(AsFaceHandler.class);

    @Value("${as.face.libPath}")
    public String sdkLibPath;

    @Value("${as.face.appId}")
    public String appId;

    @Value("${as.face.sdkKey}")
    public String sdkKey;

    @Value("${as.face.detectPooSize}")
    public Integer detectPooSize;

    private ExecutorService compareExecutorService;

    //通用人脸识别引擎池
    private GenericObjectPool<FaceEngine> faceEngineGeneralPool;

    public AsFaceHandler() {
        super("虹软人脸处理");
    }

    /**
     * 初始化，引擎池的实现
     * @see {https://segmentfault.com/a/1190000006889810}
     */
    @Override
    public void init() {
        GenericObjectPoolConfig detectPoolConfig = new GenericObjectPoolConfig();
        detectPoolConfig.setMaxIdle(detectPooSize);
        detectPoolConfig.setMaxTotal(detectPooSize);
        detectPoolConfig.setMinIdle(3);
        detectPoolConfig.setLifo(false);
        detectPoolConfig.setTimeBetweenEvictionRunsMillis(5000);
        AbandonedConfig abandonedConfig = new AbandonedConfig();
        //在Maintenance的时候检查是否有泄漏
        abandonedConfig.setRemoveAbandonedOnMaintenance(true);
        //borrow 的时候检查泄漏
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        //如果一个对象borrow之后10秒还没有返还给pool，认为是泄漏的对象
        abandonedConfig.setRemoveAbandonedTimeout(10);

        EngineConfiguration detectCfg = new EngineConfiguration();
        FunctionConfiguration detectFunctionCfg = new FunctionConfiguration();
        //开启人脸检测功能
        detectFunctionCfg.setSupportFaceDetect(true);
        //开启人脸识别功能
        detectFunctionCfg.setSupportFaceRecognition(true);
        //开启年龄检测功能
        //detectFunctionCfg.setSupportAge(true);
        //开启性别检测功能
        //detectFunctionCfg.setSupportGender(true);
        //开启活体检测功能
        //detectFunctionCfg.setSupportLiveness(true);
        detectCfg.setFunctionConfiguration(detectFunctionCfg);
        //图片检测模式，如果是连续帧的视频流图片，那么改成VIDEO模式
        detectCfg.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        //人脸旋转角度
        detectCfg.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);
        //底层库算法对象池
        faceEngineGeneralPool = new GenericObjectPool(new FaceEngineFactory(sdkLibPath, appId, sdkKey, null, detectCfg), detectPoolConfig);
        faceEngineGeneralPool.setAbandonedConfig(abandonedConfig);
    }

    /**
     * 人脸检测
     * @param imageInfo
     * @return
     */
    public List<FaceInfo> detectFaces(ImageInfo imageInfo) {
        FaceEngine faceEngine = null;
        try {
            faceEngine = this.getEngine();
            if (faceEngine == null) {
                logger.error("无法在数据池中获取FaceEngine");
                return null;
            }
            //人脸检测得到人脸列表
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            //人脸检测
            int errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            if (errorCode == 0) {
                return faceInfoList;
            } else {
                logger.error("人脸检测失败，errorCode：" + errorCode);
                return null;
            }
        } catch (Exception e) {
            logger.error("人脸检测异常",e);
            return null;
        } finally {
            this.returnEngine(faceEngine);
        }

    }

    /**
     * 人脸比对：根据原始图片信息
     * @param imageInfo1
     * @param imageInfo2
     * @return
     */
    public Float compareFace(ImageInfo imageInfo1, ImageInfo imageInfo2) {
        List<FaceInfo> faceInfoList1 = detectFaces(imageInfo1);
        List<FaceInfo> faceInfoList2 = detectFaces(imageInfo2);

        if (StringUtil.isEmpty(faceInfoList1) || StringUtil.isEmpty(faceInfoList2)) {
            logger.info("未检测到人脸");
            return 0f;
        }
        FaceFeature feature1 = extractFaceFeature(imageInfo1, faceInfoList1.get(0));
        FaceFeature feature2 = extractFaceFeature(imageInfo2, faceInfoList2.get(0));
        return this.compareFace(feature1,feature2);
    }

    /**
     * 人脸比对：根据特征值
     * @param feature1
     * @param feature2
     * @return
     */
    public Float compareFace(FaceFeature feature1, FaceFeature feature2) {
        FaceEngine faceEngine = null;
        try {
            faceEngine = this.getEngine();
            if (faceEngine == null) {
                logger.error("无法在数据池中获取FaceEngine");
                return 0f;
            }
            //提取人脸特征
            FaceSimilar faceSimilar = new FaceSimilar();
            int errorCode = faceEngine.compareFaceFeature(feature1, feature2, faceSimilar);
            if (errorCode == 0) {
                return faceSimilar.getScore();
            } else {
                logger.error("特征提取失败，errorCode：" + errorCode);
                return 0f;
            }
        } catch (Exception e) {
            logger.error("人脸比对异常",e);
            return 0f;
        } finally {
            this.returnEngine(faceEngine);
        }
    }

    /**
     * 人脸特征
     *
     * @param imageInfo
     * @return
     */
    public FaceFeature extractFaceFeature(ImageInfo imageInfo, FaceInfo faceInfo) {
        FaceEngine faceEngine = null;
        try {
            faceEngine = this.getEngine();
            if (faceEngine == null) {
                logger.error("无法在数据池中获取FaceEngine");
                return null;
            }
            FaceFeature faceFeature = new FaceFeature();
            //提取人脸特征
            int errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfo, faceFeature);
            if (errorCode == 0) {
                return faceFeature;
            } else {
                logger.error("特征提取失败，errorCode：" + errorCode);
                return null;
            }
        } catch (Exception e) {
            logger.error("人脸特征获取异常",e);
            return null;
        } finally {
            this.returnEngine(faceEngine);
        }
    }

    /**
     * 获取人脸识别引擎
     * @return
     */
    private FaceEngine getEngine(){
        try {
            FaceEngine faceEngine = faceEngineGeneralPool.borrowObject(10000);
            if (faceEngine == null) {
                logger.error("无法在数据池中获取FaceEngine");
                throw new ApplicationException(ApiErrorCode.FACE_ENGINE_GET_ERROR,"获取引擎失败");
            }
            return faceEngine;
        } catch (Exception e) {
            logger.error("设置引擎异常",e);
            return null;
        }
    }

    /**
     * 回收
     * @param faceEngine
     */
    public void returnEngine(FaceEngine faceEngine){
        //释放引擎对象
        if (faceEngine != null) {
            //释放引擎对象
            faceEngineGeneralPool.returnObject(faceEngine);
        }
    }
}
