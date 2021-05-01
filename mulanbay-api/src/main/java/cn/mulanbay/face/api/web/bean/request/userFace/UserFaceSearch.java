package cn.mulanbay.face.api.web.bean.request.userFace;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

public class UserFaceSearch extends PageSearch implements BindUser {

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
