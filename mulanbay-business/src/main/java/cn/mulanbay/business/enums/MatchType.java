package cn.mulanbay.business.enums;

/**
 * 图片的匹配类型
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public enum MatchType {

    PF2P(0, "照片人脸与照片人脸","pf","pf"),
    PF2UF(1, "照片人脸与用户人脸","pf","uf"),
    UF2P(2, "用户人脸与照片人脸","uf","pf"),
    UF2UF(3, "用户人脸与用户人脸","uf","uf");

    private int value;

    private String name;

    /**
     * 匹配的照片类型
     */
    private String mf;

    /**
     * 被匹配的照片类型
     */
    private String tmf;

    MatchType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    MatchType(int value, String name, String mf, String tmf) {
        this.value = value;
        this.name = name;
        this.mf = mf;
        this.tmf = tmf;
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

    public String getMf() {
        return mf;
    }

    public void setMf(String mf) {
        this.mf = mf;
    }

    public String getTmf() {
        return tmf;
    }

    public void setTmf(String tmf) {
        this.tmf = tmf;
    }

    /**
     * 获取反向值
     * @return
     */
    public MatchType getReverse() {
        if(this == PF2P){
            return PF2P;
        }else if(this == PF2UF){
            return UF2P;
        }else if(this == UF2P){
            return PF2UF;
        }else if(this == UF2UF){
            return UF2UF;
        }
        return null;
    }

}
