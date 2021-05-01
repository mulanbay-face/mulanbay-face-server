package cn.mulanbay.face.api.persistent.dto;

import java.math.BigInteger;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/11
 */
public class FaceMatchDto {

    private BigInteger id;

    private BigInteger userId;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

}
