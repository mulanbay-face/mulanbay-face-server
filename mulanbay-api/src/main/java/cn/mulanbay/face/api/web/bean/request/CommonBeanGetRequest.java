package cn.mulanbay.face.api.web.bean.request;

import cn.mulanbay.common.aop.BindUser;

import javax.validation.constraints.NotNull;

/**
 * 通用对象的获取详情请求类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class CommonBeanGetRequest implements BindUser {

    @NotNull(message = "{validate.bean.id.notNull}")
    private Long id;

    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
