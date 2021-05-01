package cn.mulanbay.face.api.handler;

import cn.mulanbay.business.domain.PictureFace;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.*;
import cn.mulanbay.face.api.common.CacheKey;
import cn.mulanbay.face.api.common.Constant;
import cn.mulanbay.face.api.handler.face.AsFaceHandler;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.*;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * @Description:
 * @Author: fenghong
 * @Create : 2021/4/7
 */
@Component
public class FaceHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(FaceHandler.class);

    @Value("${picture.face.width}")
    int faceWidth;

    @Value("${face.match.cache.expire}")
    int expireSeconds;

    @Autowired
    CacheHandler cacheHandler;

    @Autowired
    AsFaceHandler asFaceHandler;

    public FaceHandler() {
        super("人脸处理");
    }

    @Override
    public void init() {

    }

    /**
     * 缩率图
     * 文件名规则：原始文件夹名为aa.jpg,那么小图:aa_s.jpg，中图:aa_m.jpg
     * @param imagePath
     * @param outFolder
     * @param fileName
     * @return
     */
    public void resize(String imagePath, String outFolder,String fileName,String extension){
        String sFileName = outFolder+ Constant.FOLDER_SEP+fileName.replace(".","_s.");
        this.resize(400,imagePath,sFileName,extension);
        String mFileName = outFolder+ Constant.FOLDER_SEP+fileName.replace(".","_m.");
        this.resize(800,imagePath,mFileName,extension);
    }

    /**
     * 生成缩略图
     * @param wl 最长边的宽度
     * @param imagePath 原图地址
     * @param outFileName 生成的文件名
     * @param extension 文件扩展名
     */
    public void resize(int wl,String imagePath,String outFileName,String extension){
        OutputStream outStream=null;
        try {
            //ImageIO获取图片流信息
            Image image= ImageIO.read(new File(imagePath));
            float width=image.getWidth(null);
            float height=image.getHeight(null);
            float sc=wl/width;
            if(sc>1){
                sc=1;
            }
            //计算缩略图最终的宽度和高度
            int newWidth= (int) (width*sc);
            int newHeight= (int) (height*sc);
            BufferedImage bufferedImage=new BufferedImage(newWidth, newHeight, TYPE_INT_RGB);
            //图片缩略图实现
            bufferedImage.getGraphics().drawImage(image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
            outStream = new FileOutputStream(outFileName);
            ImageIO.write(bufferedImage, extension, outStream);
        } catch (Exception e) {
            logger.error("生成缩略图异常",e);
        }finally {
            try {
                if(outStream!=null){
                    outStream.close();
                }
            } catch (Exception e) {
                logger.error("关闭流异常",e);

            }
        }

    }


    /**
     * 图片你对
     * @param file1
     * @param file2
     * @return
     */
    public double compare(String file1,String file2){
        try {
            File img1 = new File(file1);
            File img2 = new File(file2);
            if(!img1.exists()){
                logger.warn(file1+" 图片不存在");
                return 0;
            }
            if(!img2.exists()){
                logger.warn(file2+" 图片不存在");
                return 0;
            }
            byte[] bytes1 = FileUtils.readFileToByteArray(img1);
            byte[] bytes2 = FileUtils.readFileToByteArray(img2);
            ImageInfo rgbData1 = ImageFactory.getRGBData(bytes1);
            ImageInfo rgbData2 = ImageFactory.getRGBData(bytes2);
            Float similar = asFaceHandler.compareFace(rgbData1, rgbData2);
            return similar;
        } catch (Exception e) {
            logger.error("人脸比对异常",e);
        }
        return 0;
    }

    /**
     * 图片比对
     * @param feature1
     * @param feature2
     * @return
     */
    public double compare(FaceFeature feature1,FaceFeature feature2){
        if(feature1==null||feature2==null){
            logger.warn("未找到特征值");
            return 0;
        }
        return asFaceHandler.compareFace(feature1,feature2);
    }

    public FaceFeature extractFaceFeature(String file1){
        try {
            File img1 = new File(file1);
            if(!img1.exists()){
                logger.warn(file1+" 图片不存在");
                return null;
            }
            byte[] bytes1 = FileUtils.readFileToByteArray(img1);
            ImageInfo rgbData1 = ImageFactory.getRGBData(bytes1);
            List<FaceInfo> faceInfoList1 = asFaceHandler.detectFaces(rgbData1);
            if(StringUtil.isEmpty(faceInfoList1)){
                logger.warn(file1+" 未能找到特征值");
                return null;
            }
            FaceFeature feature1 = asFaceHandler.extractFaceFeature(rgbData1, faceInfoList1.get(0));
            return feature1;
        } catch (ApplicationException ae) {
            logger.error("操作人脸特征异常，code:"+ae.getErrorCode(),ae);
            throw ae;
        } catch (Exception e) {
            logger.error("获取人脸特征异常",e);
            return null;
        }
    }

    /**
     * 获取特征值
     * @param file1
     * @param faceId
     * @param faceType
     * @return
     */
    public FaceFeature extractFaceFeature(String file1,long faceId,String faceType){
        FaceFeature feature = this.getFaceFeatureCache(faceId,faceType);
        if(feature!=null){
            return feature;
        }else{
            feature = this.extractFaceFeature(file1);
            if(feature==null){
                return null;
            }
            this.setFaceFeatureCache(faceId,faceType,feature.getFeatureData());
            return feature;
        }
    }

    /**
     * 图片特征缓存
     * @param faceId
     * @param faceType
     * @return
     */
    private FaceFeature getFaceFeatureCache(long faceId,String faceType){
        String key = CacheKey.getKey(CacheKey.FACE_FEATURE,faceType,faceId+"");
        byte[] featureData = cacheHandler.get(key,byte[].class);
        if(featureData!=null){
            FaceFeature faceFeature = new FaceFeature();
            faceFeature.setFeatureData(featureData);
            return faceFeature;
        }
        return null;
    }

    /**
     * 图片特征缓存
     * @param faceId
     * @param faceType
     * @param featureData
     */
    private void setFaceFeatureCache(long faceId,String faceType, byte[] featureData){
        String key = CacheKey.getKey(CacheKey.FACE_FEATURE,faceType,faceId+"");
        cacheHandler.set(key,featureData,expireSeconds);
    }

    /**
     * 识别人脸并写入
     * @param imagePath 原始图片地址
     * @param outFolder 标记有人脸位置人脸图片的文件夹
     * @param newFileName 标记有人脸位置的原始图片大图
     * @param facePath 人脸的保存文件夹
     * @param faceKey 人脸的序列号前缀key
     * @return
     */
    public List<PictureFace> detectFace(String imagePath, String outFolder, String newFileName , String facePath, String faceKey) {
        List<PictureFace> faces = new ArrayList<>();
        try {
            File img1 = new File(imagePath);
            if(!img1.exists()){
                logger.warn(imagePath+" 图片不存在");
                return faces;
            }
            byte[] bytes1 = FileUtils.readFileToByteArray(img1);
            ImageInfo rgbData1 = ImageFactory.getRGBData(bytes1);
            List<FaceInfo> faceInfoList = asFaceHandler.detectFaces(rgbData1);
            if(StringUtil.isEmpty(faceInfoList)){
                logger.warn("图片"+imagePath+"未识别到人脸");
                return faces;
            }
            return this.writeFaces(imagePath,outFolder,newFileName,facePath,faceKey,faceInfoList);
        } catch (Exception e) {
            logger.error("识别人脸异常",e);
        }
        return faces;
    }

    /**
     * 写入人脸
     * @param imagePath 原始图片地址
     * @param outFolder 标记有人脸位置人脸图片的文件夹
     * @param newFileName 标记有人脸位置的原始图片大图
     * @param facePath 人脸的保存文件夹
     * @param faceKey 人脸的序列号前缀key
     * @param faceInfoList 人脸信息
     * @return
     */
    public List<PictureFace> writeFaces(String imagePath, String outFolder, String newFileName , String facePath, String faceKey,List<FaceInfo> faceInfoList) {
        List<PictureFace> faces = new ArrayList<>();
        try {
            for(FaceInfo fi : faceInfoList){
                // 为每个人脸输出一个图片
                long faceId = this.getFaceId(faceKey);
                String faceFolder = generateFaceFolder(faceId);
                String faceUrl = faceFolder+faceId+Constant.FACE_IMG_SUFFIX;
                //检查文件夹是否存在
                FileUtil.checkPathExits(facePath+faceFolder);
                //todo 检查文件是否存在

                int x = fi.getRect().getLeft();
                int y = fi.getRect().getTop();
                int width = fi.getRect().getRight() - x;
                int height = fi.getRect().getBottom() - y;

                //写入图片
                this.cropImage(imagePath,facePath+faceUrl,x,y,width,height,"png");
                PictureFace pf = new PictureFace();
                pf.setId(faceId);
                pf.setUrl(faceUrl);
                pf.setFaceId(faceId);
                pf.setFileName(faceId+Constant.FACE_IMG_SUFFIX);
                faces.add(pf);
            }
            //todo 无论有没有都写faces图
            FileUtil.checkPathExits(outFolder);
            String outFile = outFolder+Constant.FOLDER_SEP+newFileName;
        } catch (Exception e) {
            logger.error("识别人脸异常",e);
        }
        return faces;
    }

    /**
     * 对图片裁剪，并把裁剪新图片保存
     * @see {https://blog.csdn.net/tielan/article/details/43760301}
     * @param srcPath 读取源图片路径
     * @param toPath    写入图片路径
     * @param x 剪切起始点x坐标
     * @param y 剪切起始点y坐标
     * @param width 剪切宽度
     * @param height     剪切高度
     * @param writeImageFormat 写入图片格式
     */
    public void cropImage(String srcPath,String toPath,
                          int x,int y,int width,int height,String writeImageFormat){
        ImageInputStream iis =null ;
        try{
            //读取图片文件
            iis = ImageIO.createImageInputStream(new File(srcPath));
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
            ImageReader reader = iterator.next();
            //获取图片流
            reader.setInput(iis,true) ;
            ImageReadParam param = reader.getDefaultReadParam();
            //定义一个矩形
            Rectangle rect = new Rectangle(x, y, width, height);
            //提供一个 BufferedImage，将其用作解码像素数据的目标。
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0,param);
            if(width<faceWidth){
                //不缩放
                //保存新图片
                ImageIO.write(bi, writeImageFormat, new File(toPath));
            }else{
                Image image = bi.getScaledInstance(faceWidth, faceWidth, Image.SCALE_SMOOTH);
                BufferedImage tag = new BufferedImage(faceWidth, faceWidth, BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.setColor(Color.RED);
                g.drawImage(image, 0, 0, null); // 绘制处理后的图
                g.dispose();
                //保存新图片
                ImageIO.write(tag, writeImageFormat, new File(toPath));
            }
        }catch (Exception e) {
            logger.error("写入人脸异常",e);
        }finally{
            try {
                if(iis!=null){
                    iis.close();
                }
            } catch (Exception e) {
                logger.error("关闭流异常",e);
            }
        }
    }

    /**
     * 获取face的id
     * @return
     */
    public long getFaceId(String key){
        return cacheHandler.incre(key,1);
    }

    /**
     * 取前4位
     * 比如1算出的hash值是abcdefghijk****,那么目录名为:/ab/cd
     * 一共为16*16=256,256*256=65536个文件夹
     * @param faceId
     * @return
     */
    public String generateFaceFolder(long faceId){
        String hash = Md5Util.encodeByMD5(faceId+"").toLowerCase();
        return Constant.FOLDER_SEP+hash.substring(0,2)+Constant.FOLDER_SEP+hash.substring(2,4)+Constant.FOLDER_SEP;
    }

    /**
     *
     * @param faceId
     * @return
     */
    public String generateFaceFolderUrl(long faceId){
        return this.generateFaceFolder(faceId)+faceId+Constant.FACE_IMG_SUFFIX;
    }


    /**
     * 编码文件名
     */
    public String extractFilename(MultipartFile file) {
        String extension = getExtension(file);
        return this.extractFilename(extension);
    }

    /**
     * 编码文件名
     */
    public final String extractFilename(String extension) {
        String fileName = generateFolder() + Constant.FOLDER_SEP + generateUUID() + "." + extension;
        return Constant.FOLDER_SEP + fileName;
    }

    /**
     * 文件夹名
     * @return
     */
    public String generateFolder(){
        return DateUtil.getFormatDate(new Date(), "yyyyMMdd/HH");
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtil.isEmpty(extension)) {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

    public String generateUUID(){
        return UUID.randomUUID().toString().replace("-","").toLowerCase();
    }
}
