package cn.mulanbay.face.api.handler;

import cn.mulanbay.business.domain.Picture;
import cn.mulanbay.business.domain.PictureDetail;
import cn.mulanbay.business.domain.PictureFace;
import cn.mulanbay.business.domain.UserFace;
import cn.mulanbay.business.enums.*;
import cn.mulanbay.business.handler.BaseHandler;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.BeanCopy;
import cn.mulanbay.common.util.FileUtil;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.face.api.common.ApiErrorCode;
import cn.mulanbay.face.api.common.CacheKey;
import cn.mulanbay.face.api.common.Constant;
import cn.mulanbay.face.api.persistent.service.PictureService;
import cn.mulanbay.face.api.web.bean.request.picture.PictureUploadRequest;
import cn.mulanbay.face.api.web.bean.request.pictureFace.PictureFaceAddFaceRequest;
import cn.mulanbay.face.api.web.bean.request.pictureFace.PictureFaceAddRequest;
import cn.mulanbay.face.api.web.bean.request.pictureFace.PictureFaceUploadRequest;
import cn.mulanbay.persistent.service.BaseService;
import com.arcsoft.face.FaceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * @Description: 图片处理
 * @Author: fenghong
 * @Create : 2021/1/8
 */
@Component
public class PictureHandler extends BaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(PictureHandler.class);

    @Value("${picture.original.path}")
    String originalPath;

    @Value("${picture.original.url}")
    String originalUrlPrefix;

    @Value("${picture.handled.path}")
    String handledPath;

    @Value("${picture.handled.url}")
    String handledUrlPrefix;

    @Value("${picture.face.path}")
    String facePath;

    @Value("${picture.face.url}")
    String faceUrlPrefix;

    @Value("${picture.user.face.path}")
    String userFacePath;

    @Value("${picture.user.face.url}")
    String userFacePrefix;

    @Autowired
    PictureService pictureService;

    @Autowired
    BaseService baseService;

    @Autowired
    FaceHandler faceHandler;

    public PictureHandler() {
        super("图片处理");
    }

    @Override
    public void init() {
        super.init();
    }

    /**
     * 保存图片，用户上传的图片，需要人脸识别
     * @param file
     * @param pur
     * @return
     */
    public Long savePicture(MultipartFile file, PictureUploadRequest pur){
        //Step1:保存原始文件
        String originalUrl = this.writeFile(file,originalPath);

        //Step2:人脸识别,写入头像
        int index = originalUrl.lastIndexOf(".");
        String extension = originalUrl.substring(index+1);
        String fileName = faceHandler.generateUUID()+"."+extension;
        String folderPath = Constant.FOLDER_SEP+faceHandler.generateFolder();
        List<PictureFace> faces = faceHandler.detectFace(originalPath+originalUrl,handledPath+folderPath,fileName,facePath, CacheKey.PICTURE_FACE_ID);

        //Step3:写入大小图
        faceHandler.resize(originalPath+originalUrl,handledPath+folderPath,fileName,extension);

        //Step4:写入头像
        Picture picture = new Picture();
        BeanCopy.copyProperties(pur,picture);
        picture.setFileName(fileName);
        picture.setOriginalUrl(originalUrl);
        picture.setUrl(folderPath+Constant.FOLDER_SEP+fileName);
        picture.setSource(PictureSource.UPLOAD);
        //todo 拍摄时间获取
        picture.setShotTime(new Date());
        picture.setCreatedTime(new Date());

        //Step4:保存数据
        pictureService.savePicture(picture,null,faces);
        return picture.getId();
    }

    /**
     * 保存图片，第三方图片
     * @param data
     * @param faceInfoList
     * @param webUrl
     * @param picUrl
     * @param platform
     * @return
     */
    public Long saveThirdPartPicture(byte[] data, List<FaceInfo> faceInfoList, String webUrl, String picUrl, Platform platform){
        int index = picUrl.lastIndexOf(".");
        String extension = picUrl.substring(index+1);
        //Step1:保存原始文件
        String originalUrl = this.writeFile(data,originalPath,extension);

        //Step2:人脸识别,写入头像
        String fileName = faceHandler.generateUUID()+"."+extension;
        String folderPath = Constant.FOLDER_SEP+faceHandler.generateFolder();
        List<PictureFace> faces = faceHandler.writeFaces(originalPath+originalUrl,handledPath+folderPath,fileName,facePath, CacheKey.PICTURE_FACE_ID,faceInfoList);

        //Step3:写入大小图
        faceHandler.resize(originalPath+originalUrl,handledPath+folderPath,fileName,extension);

        //Step4:写入头像
        Picture picture = new Picture();
        picture.setSearchType(SearchType.PUB);
        picture.setDownloadType(DownloadType.AUDIT);
        picture.setUserId(1L);
        picture.setFileName(fileName);
        picture.setOriginalUrl(originalUrl);
        picture.setUrl(folderPath+Constant.FOLDER_SEP+fileName);
        picture.setSource(PictureSource.THIRD_PART);
        //todo 拍摄时间获取
        picture.setShotTime(new Date());
        picture.setCreatedTime(new Date());
        PictureDetail pictureDetail = new PictureDetail();
        pictureDetail.setCreatedTime(new Date());
        pictureDetail.setPicUrl(picUrl);
        pictureDetail.setWebUrl(webUrl);
        pictureDetail.setPlatform(platform);
        //Step4:保存数据
        pictureService.savePicture(picture,pictureDetail,faces);
        return picture.getId();
    }

    /**
     * 保存用户人脸，用户上传的图片，需要人脸识别
     * @param file
     * @param pur
     * @return
     */
    public UserFace saveUserFace(MultipartFile file, PictureFaceUploadRequest pur){
        //Step1:保存原始文件
        String originalUrl = this.writeFile(file,originalPath);

        //Step2:人脸识别,写入头像
        int index = originalUrl.indexOf(".");
        String extension = originalUrl.substring(index+1);
        String fileName = faceHandler.generateUUID()+"."+extension;
        String folderPath = Constant.FOLDER_SEP+faceHandler.generateFolder();
        List<PictureFace> faces = faceHandler.detectFace(originalPath+originalUrl,handledPath+folderPath,fileName,userFacePath,CacheKey.USER_FACE_ID);
        UserFace userFace = new UserFace();
        BeanCopy.copyProperties(pur,userFace);
        userFace.setOriginalUrl(originalUrl);
        if(StringUtil.isEmpty(faces)){
            return userFace;
        }
        PictureFace pf = faces.get(0);
        //Step3:写入大小图
        //openCvHandler.resize(originalPath+originalUrl,handledPath+folderPath,fileName);

        //Step4:写入头像
        userFace.setId(pf.getId());
        userFace.setFaceId(pf.getFaceId());
        userFace.setUrl(pf.getUrl());
        userFace.setFileName(pf.getFileName());
        userFace.setMatchs(0L);
        userFace.setHits(0L);
        userFace.setSource(FaceSource.AUTO);
        userFace.setCreatedTime(new Date());

        //Step4:保存数据
        baseService.saveObject(userFace);
        return userFace;
    }

    /**
     * 增加图片人脸
     * @param file
     * @param pur
     */
    public void addPictureFace(MultipartFile file, PictureFaceAddRequest pur){
        //获取
        long faceId = faceHandler.getFaceId(CacheKey.PICTURE_FACE_ID);
        String faceFolder = faceHandler.generateFaceFolder(faceId);
        String faceUrl = faceFolder+faceId+Constant.FACE_IMG_SUFFIX;
        PictureFace pf = new PictureFace();
        pf.setId(faceId);
        pf.setUrl(faceUrl);
        //pf.setPosition(rect.x+","+rect.y+","+rect.width+","+rect.height);
        pf.setFaceId(faceId);
        pf.setFileName(faceId+Constant.FACE_IMG_SUFFIX);
        //写入硬盘
        this.writeFile2(file,facePath+faceUrl);
        //保存数据库
        pf.setPictureId(pur.getPictureId());
        pf.setUserId(pur.getUserId());
        pf.setOpen(true);
        pf.setMatchs(0L);
        pf.setHits(0L);
        pf.setSource(FaceSource.MANUAL);
        pf.setShotTime(new Date());
        pf.setCreatedTime(new Date());
        baseService.saveObject(pf);
    }

    /**
     * 增加用户人脸
     * @param file
     * @param pur
     */
    public void addUserFace(MultipartFile file, PictureFaceAddFaceRequest pur){
        //获取
        long faceId = faceHandler.getFaceId(CacheKey.USER_FACE_ID);
        String faceFolder = faceHandler.generateFaceFolder(faceId);
        String faceUrl = faceFolder+faceId+Constant.FACE_IMG_SUFFIX;
        //写入硬盘
        this.writeFile2(file,userFacePath+faceUrl);
        UserFace userFace =new UserFace();
        BeanCopy.copyProperties(pur,userFace);
        userFace.setOriginalUrl(pur.getOriginalUrl().replace(originalUrlPrefix,""));
        userFace.setId(faceId);
        userFace.setFaceId(faceId);
        userFace.setUrl(faceUrl);
        userFace.setFileName(faceId+Constant.FACE_IMG_SUFFIX);
        userFace.setMatchs(0L);
        userFace.setHits(0L);
        userFace.setSource(FaceSource.MANUAL);
        userFace.setCreatedTime(new Date());

        //Step4:保存数据
        baseService.saveObject(userFace);
    }

    /**
     * 更新照片人脸的开发状态
     * @param faceId
     * @param userId
     * @param open
     * @return
     */
    public boolean updatePictureFaceOpen(Long faceId,Long userId,Boolean open){
        PictureFace br = baseService.getObjectWithUser(PictureFace.class, faceId, userId);
        if(br==null){
            throw new ApplicationException(ApiErrorCode.USER_ENTITY_NOT_FOUND);
        }else if(br.getOpen().booleanValue()==open.booleanValue()){
            //未改变状态
            return true;
        }else{
            //重命名文件
            String faceUrl = br.getUrl();
            String faceFullPath = facePath+faceUrl;
            boolean b =false;
            if(open){
                //去除后缀
                //todo 上一次文件更新成功，但是数据库保存失败，导致多次添加后缀？
                b = (new File(faceFullPath+Constant.CLOSED_IMG_SUFFIX)).renameTo(new File(faceFullPath));
            }else{
                //添加后缀
                b = (new File(faceFullPath)).renameTo(new File(faceFullPath+Constant.CLOSED_IMG_SUFFIX));
            }
            br.setOpen(open);
            br.setLastModifyTime(new Date());
            baseService.updateObject(br);
            return b;
        }
    }

    /**
     * 更新照片人脸的开发状态
     * @param faceId
     * @param userId
     * @param open
     * @return
     */
    public boolean updateUserFaceOpen(Long faceId,Long userId,Boolean open){
        UserFace br = baseService.getObjectWithUser(UserFace.class, faceId, userId);
        if(br==null){
            throw new ApplicationException(ApiErrorCode.USER_ENTITY_NOT_FOUND);
        }else if(br.getOpen().booleanValue()==open.booleanValue()){
            //未改变状态
            return true;
        }else{
            //重命名文件
            String faceUrl = br.getUrl();
            String faceFullPath = userFacePath+faceUrl;
            boolean b =false;
            if(open){
                //去除后缀
                //todo 上一次文件更新成功，但是数据库保存失败，导致多次添加后缀？
                b = (new File(faceFullPath+Constant.CLOSED_IMG_SUFFIX)).renameTo(new File(faceFullPath));
            }else{
                //添加后缀
                b = (new File(faceFullPath)).renameTo(new File(faceFullPath+Constant.CLOSED_IMG_SUFFIX));
            }
            br.setOpen(open);
            br.setLastModifyTime(new Date());
            baseService.updateObject(br);
            return b;
        }
    }


    /**
     * 获取原图的全路径
     * @param originalUrl
     * @return
     */
    public String getOriginalFullUrl(String originalUrl){
        return originalUrlPrefix+originalUrl;
    }

    /**
     * 获取原图包含图片人脸位置的全路径
     * 打上人脸位置
     * @param url
     * @return
     */
    public String getPictureFacesFullUrl(String url){
        return handledUrlPrefix+url;
    }

    /**
     * 获取图片人脸的全路径
     * @param url
     * @return
     */
    public String getPictureFaceFullUrl(String url){
        return faceUrlPrefix+url;
    }

    /**
     * 获取图片人脸的本地硬盘全路径
     * @param url
     * @return
     */
    public String getPictureFaceFullPath(String url){
        return facePath+url;
    }

    /**
     * 获取用户人脸的全路径
     * @param url
     * @return
     */
    public String getUserFaceFullUrl(String url){
        return userFacePrefix+url;
    }

    /**
     * 获取图用户人脸的本地硬盘全路径
     * @param url
     * @return
     */
    public String getUserFaceFullPath(String url){
        return userFacePath+url;
    }

    /**
     * 获取图片小图的全路径
     * @param url
     * @return
     */
    public String getPictureSsFullUrl(String url){
        return handledUrlPrefix+url.replace(".","_s.");
    }

    /**
     * 获取图片中图的全路径
     * @param url
     * @return
     */
    public String getPictureMsFullUrl(String url){
        return handledUrlPrefix+url.replace(".","_m.");
    }

    /**
     * 写入图片
     * @param data 图片字节流
     * @param path
     * @param extension
     * @return
     */
    public String writeFile(byte[] data,String path,String extension){
        try {
            // 获取原文件名
            String extractFilename = faceHandler.extractFilename(extension);
            String fullPath = path+extractFilename;
            this.checkFolderPath(fullPath);
            Path outPath = Paths.get(fullPath);
            Files.write(outPath, data);
            return extractFilename;
        } catch (Exception e) {
            logger.error("写入图片异常",e);
            throw new ApplicationException(ApiErrorCode.FILE_WRITE_ERROR);
        }
    }

    /**
     * 写入图片
     * @param file
     * @param path
     * @return
     */
    public String writeFile(MultipartFile file,String path){
        try {
            // 获取原文件名
            String extractFilename = faceHandler.extractFilename(file);
            String fullPath = path+extractFilename;
            this.writeFile2(file,fullPath);
            return extractFilename;
        } catch (Exception e) {
            logger.error("写入图片异常",e);
            throw new ApplicationException(ApiErrorCode.FILE_WRITE_ERROR);
        }
    }

    /**
     * 写入图片
     * @param file
     * @param fullPath
     * @return
     */
    public void writeFile2(MultipartFile file,String fullPath){
        try {
            this.checkFolderPath(fullPath);
            // 创建文件实例
            File filePath = new File(fullPath);
            // 写入文件
            file.transferTo(filePath);
        } catch (Exception e) {
            logger.error("写入图片异常",e);
            throw new ApplicationException(ApiErrorCode.FILE_WRITE_ERROR);
        }
    }

    /**
     * 检测文件夹是否存在
     * @param fullPath
     */
    private void checkFolderPath(String fullPath){
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
        // 创建文件实例
        File filePath = new File(folder);
        FileUtil.checkPathExits(filePath);
    }
}
