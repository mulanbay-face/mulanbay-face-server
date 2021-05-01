package cn.mulanbay.business.enums;

/**
 * 图片的可下载类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum DownloadType {

    REDIRECT(0, "直接下载"),
    AUDIT(1, "审核下载"),
    PAY(2, "付费下载");

    private int value;

    private String name;

    DownloadType(int value, String name) {
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
