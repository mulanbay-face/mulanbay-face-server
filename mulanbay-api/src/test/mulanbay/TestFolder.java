package mulanbay;

/**
 * @Description: TODO(一句话描述该类的功能)
 * @Author: fenghong
 * @Create : 2021/1/8 15:21
 */
public class TestFolder {

    public static void main(String[] args){
        String fullPath="D:/ss/dd/ee.jpg";
        int index = fullPath.lastIndexOf(".");
        if(index<0){
            if(fullPath.endsWith("/")){
                index = fullPath.length()-1;
            }else{
                index = fullPath.length();
            }
        }else{
            index = fullPath.lastIndexOf("/");
        }
        String folder = fullPath.substring(0,index);
        System.out.println(folder);
    }
}
