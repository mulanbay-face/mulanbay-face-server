package cn.mulanbay.business.enums;

/**
 * 任务来源
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum TaskSource {

    MANUAL(0, "手动"),
    AUTO(1, "自动");

    private int value;

    private String name;

    TaskSource(int value, String name) {
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
