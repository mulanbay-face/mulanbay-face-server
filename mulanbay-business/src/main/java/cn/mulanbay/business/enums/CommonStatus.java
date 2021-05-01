package cn.mulanbay.business.enums;

/**
 * 通用状态类
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum CommonStatus {

    DISABLE(0, "不可用"), ENABLE(1, "可用");

    private int value;

    private String name;

    CommonStatus(int value, String name) {
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
