package cn.mulanbay.business.enums;

/**
 * 人脸来源
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum FaceSource {

    AUTO(0, "系统生成"),
    MANUAL(1, "手动添加");

    private int value;

    private String name;

    FaceSource(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
