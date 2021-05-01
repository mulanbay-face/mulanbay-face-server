package cn.mulanbay.face.api.thread;

import cn.mulanbay.business.domain.FaceMatch;
import cn.mulanbay.business.enums.MatchType;
import cn.mulanbay.common.util.BeanFactoryUtil;
import cn.mulanbay.face.api.persistent.service.PictureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @Description: 人脸匹配成功后记录日志
 * @Author: fenghong
 * @Create : 2021/4/18
 */
public class FaceMatchThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(FaceMatchThread.class);

    private MatchType matchType;

    /**
     * 匹配者人脸
     */
    private Long matchFaceId;

    /**
     * 哪张人脸被匹配
     */
    private Long matchedFaceId;

    /**
     * 匹配度
     */
    private double rate;

    public FaceMatchThread() {
    }

    public FaceMatchThread(MatchType matchType, Long matchFaceId, Long matchedFaceId, double rate) {
        this.matchType = matchType;
        this.matchFaceId = matchFaceId;
        this.matchedFaceId = matchedFaceId;
        this.rate = rate;
    }

    @Override
    public void run() {
        PictureService pictureService = BeanFactoryUtil.getBean(PictureService.class);
        try {
            FaceMatch fm = new FaceMatch();
            fm.setMatchType(matchType);
            fm.setMatchFaceId(matchFaceId);
            fm.setMatchedFaceId(matchedFaceId);
            fm.setRate(rate);
            fm.setCreatedTime(new Date());
            pictureService.saveFaceMatch(fm);
        } catch (Exception e) {
            logger.error("保存FaceMatch异常",e);
        }
        try {
            //反向
            FaceMatch fm2 = new FaceMatch();
            fm2.setMatchType(matchType.getReverse());
            fm2.setMatchFaceId(matchedFaceId);
            fm2.setMatchedFaceId(matchFaceId);
            fm2.setRate(rate);
            fm2.setCreatedTime(new Date());
            pictureService.saveFaceMatch(fm2);
        } catch (Exception e) {
            logger.error("保存反向FaceMatch异常",e);
        }

    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public Long getMatchFaceId() {
        return matchFaceId;
    }

    public void setMatchFaceId(Long matchFaceId) {
        this.matchFaceId = matchFaceId;
    }

    public Long getMatchedFaceId() {
        return matchedFaceId;
    }

    public void setMatchedFaceId(Long matchedFaceId) {
        this.matchedFaceId = matchedFaceId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
