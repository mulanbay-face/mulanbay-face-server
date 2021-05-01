package cn.mulanbay.face.api.web.bean.request.pictureFace;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

import javax.validation.constraints.NotNull;

public class PictureFaceSearch extends PageSearch implements BindUser {

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
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
