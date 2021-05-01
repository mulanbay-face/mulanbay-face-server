package cn.mulanbay.face.api.thread;

import cn.mulanbay.common.util.BeanFactoryUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.face.api.common.CacheKey;
import cn.mulanbay.face.api.handler.CacheHandler;
import cn.mulanbay.face.api.handler.PictureHandler;
import cn.mulanbay.face.api.handler.face.AsFaceHandler;
import cn.mulanbay.face.api.web.bean.request.picture.ThirdPartFaceDataRequest;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/4/18
 */
public class ThirdPartFaceThread extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(ThirdPartFaceThread.class);

    private ThirdPartFaceDataRequest faceData;

    public ThirdPartFaceThread(ThirdPartFaceDataRequest faceData) {
        this.faceData = faceData;
    }

    @Override
    public void run() {
        AsFaceHandler asFaceHandler = BeanFactoryUtil.getBean(AsFaceHandler.class);
        PictureHandler pictureHandler = BeanFactoryUtil.getBean(PictureHandler.class);
        CacheHandler cacheHandler = BeanFactoryUtil.getBean(CacheHandler.class);
        List pictures = faceData.getPictures();
        int n = pictures.size();
        for(int i=0;i<n;i++){
            String pic = pictures.get(i).toString();
            String cacheKey = CacheKey.getKey(CacheKey.THIRD_PART_PICTURE_FACE,pic);
            String vv = cacheHandler.getForString(cacheKey);
            if(vv!=null){
                logger.warn("图片"+pic+"已经处理过");
                continue;
            }
            try {
                byte[] data = readPicData(pic);
                logger.debug("图片"+pic+"大小:"+data.length);
                ImageInfo rgbData1 = ImageFactory.getRGBData(data);
                List<FaceInfo> faceInfoList = asFaceHandler.detectFaces(rgbData1);
                if(StringUtil.isNotEmpty(faceInfoList)){
                    logger.debug("图片"+pic+"识别到人脸数："+faceInfoList.size());
                    pictureHandler.saveThirdPartPicture(data,faceInfoList,faceData.getUrl(),pic,faceData.getPlatform());
                }
                cacheHandler.set(cacheKey,"aa",0);
            } catch (Exception e) {
                logger.error("处理图片"+pic+"异常",e);
            }
        }
    }

    /**
     * 读取网络图片数据
     * @param picUrl
     * @return
     */
    private byte[] readPicData(String picUrl){
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;
        try {
            //new一个URL对象
            URL url = new URL(picUrl);
            //打开链接
            URLConnection conn = url.openConnection();
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[10*1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while( (len=inStream.read(buffer)) != -1 ){
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            //关闭输入流
            inStream.close();
            //把outStream里的数据写入内存
            return outStream.toByteArray();
        } catch (Exception e) {
            logger.error("读取图片"+picUrl+"异常",e);
            return null;
        } finally {
            try {
                if(inStream!=null){
                    inStream.close();
                }
                if(outStream!=null){
                    outStream.close();
                }
            } catch (Exception e) {
                logger.error("关闭流异常",e);
            }
        }
    }
}
