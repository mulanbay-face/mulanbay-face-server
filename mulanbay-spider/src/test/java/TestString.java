/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/4/18
 */
public class TestString {

    public static void main(String[] args){
        String  url = "https://bbs.8264.com/thread-5643569-22-1.html";
        int n =url.indexOf("-");
        int s = url.indexOf("-",n+1);
        int e = url.lastIndexOf("-");
        String page = url.substring(s+1,e);
        int next = Integer.valueOf(page)+1;
        url = url.replace("-"+page+"-","-"+next+"-");
        System.out.println(url);
        String fullPath = "D:\\test\\picData\\op\\20210419\\09\\60e07df4f64340818600a8cf3be614fb.jpg";
        int index = fullPath.lastIndexOf(".");
        String folder = fullPath.substring(0,index);
        System.out.println(folder);
    }
}
