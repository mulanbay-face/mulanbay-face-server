package cn.mulanbay.face.api.web.bean.request.picture;

import cn.mulanbay.business.enums.MatchType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/4/19
 */
public class FaceMatchRequest extends PageSearch {

    @Query(fieldName = "matchType", op = Parameter.Operator.EQ)
    private MatchType matchType;

    /**
     * 匹配者人脸
     */
    @Query(fieldName = "matchFaceId", op = Parameter.Operator.EQ)
    private Long matchFaceId;

    @Query(fieldName = "rate", op = Parameter.Operator.GTE)
    private double minRate;

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

    public double getMinRate() {
        return minRate;
    }

    public void setMinRate(double minRate) {
        this.minRate = minRate;
    }
}
