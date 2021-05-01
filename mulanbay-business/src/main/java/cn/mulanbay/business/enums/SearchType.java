package cn.mulanbay.business.enums;

/**
 * 图片的可搜索类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum SearchType {

    PUB(0, "公开"),//利用关键字和人脸匹配
    FACE(1, "人脸识别"),//只能人脸匹配
    PRI(2, "私有");//只能自己看

    private int value;

    private String name;

    SearchType(int value, String name) {
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
