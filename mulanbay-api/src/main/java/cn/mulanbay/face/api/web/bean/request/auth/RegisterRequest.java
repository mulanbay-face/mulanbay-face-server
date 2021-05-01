package cn.mulanbay.face.api.web.bean.request.auth;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotEmpty;

public class RegisterRequest {

    @NotBlank(message = "{validate.user.username.notNull}")
    private String username;

    @NotBlank(message = "{validate.user.password.notNull}")
    private String password;

    @NotEmpty(message = "{validate.user.nickname.notEmpty}")
    private String nickname;

    @NotEmpty(message = "{validate.user.uuid.notNull}")
    private String uuid;

    @NotEmpty(message = "{validate.user.code.notNull}")
    private String code;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
