package mulanbay;

import cn.mulanbay.common.util.Md5Util;
import cn.mulanbay.face.api.common.Constant;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 15:21
 */
public class TestFileName {

    public static void main(String[] args){
        String originalUrl ="214214.jpg";
        int index = originalUrl.indexOf(".");
        String extension = originalUrl.substring(index+1);
        System.out.println(extension);

        String hash = Md5Util.encodeByMD5("1").toLowerCase();
        System.out.println(hash);
        System.out.println(Constant.FOLDER_SEP+hash.substring(0,2)+Constant.FOLDER_SEP+hash.substring(2,4)+Constant.FOLDER_SEP+hash.substring(4,6));
    }
}
