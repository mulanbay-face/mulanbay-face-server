package cn.mulanbay.face.api.web.bean.request.pictureFace;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;

import javax.validation.constraints.NotNull;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 15:31
 */
public class PictureFaceAddRequest implements BindUser {

    private Long userId;

    @NotNull(message = "{validate.pictureFace.pictureId.NotNull}")
    @Query(fieldName = "pictureId", op = Parameter.Operator.EQ)
    private Long pictureId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPictureId() {
        return pictureId;
    }

    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
    }
}
