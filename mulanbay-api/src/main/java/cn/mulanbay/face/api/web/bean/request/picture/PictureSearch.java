package cn.mulanbay.face.api.web.bean.request.picture;

import cn.mulanbay.common.aop.BindUser;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

public class PictureSearch extends PageSearch implements BindUser {

    @Query(fieldName = "userId", op = Parameter.Operator.EQ)
    private Long userId;

    @Query(fieldName = "name", op = Parameter.Operator.LIKE)
    private String name;

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
