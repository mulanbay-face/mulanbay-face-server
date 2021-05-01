package cn.mulanbay.business.enums;

/**
 * 平台
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum Platform {

    BBS_8264(0, "8264论坛"),
    MAFENGWO(1, "马蜂窝");

    private int value;

    private String name;

    Platform(int value, String name) {
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
