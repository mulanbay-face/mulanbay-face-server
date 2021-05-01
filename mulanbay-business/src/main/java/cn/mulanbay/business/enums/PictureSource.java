package cn.mulanbay.business.enums;

/**
 * 图片来源
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum PictureSource {

    UPLOAD(0, "网站上传"),
    THIRD_PART(1, "第三方");

    private int value;

    private String name;

    PictureSource(int value, String name) {
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
