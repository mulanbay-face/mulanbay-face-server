package cn.mulanbay.business.enums;

/**
 * 任务周期
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum TaskPeriod {

    ONCE(0, "单次"),
    CYCLE(1, "循环");

    private int value;

    private String name;

    TaskPeriod(int value, String name) {
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
