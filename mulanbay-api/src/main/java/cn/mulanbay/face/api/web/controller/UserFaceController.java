package cn.mulanbay.face.api.web.controller;

import cn.mulanbay.business.domain.UserFace;
import cn.mulanbay.face.api.handler.PictureHandler;
import cn.mulanbay.face.api.web.bean.request.pictureFace.PictureFaceAddFaceRequest;
import cn.mulanbay.face.api.web.bean.request.pictureFace.PictureFaceUploadRequest;
import cn.mulanbay.face.api.web.bean.request.userFace.UserFaceSearch;
import cn.mulanbay.face.api.web.bean.request.userFace.UserFaceUpdateOpenRequest;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @Description: 用户人脸
 * @Author: fenghong
 * @Create : 2021/1/10
 */
@RestController
@RequestMapping("/userFace")
public class UserFaceController extends BaseController{

    private static Class<UserFace> beanClass = UserFace.class;

    @Autowired
    PictureHandler pictureHandler;

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public ResultBean getData(UserFaceSearch sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        PageResult<UserFace> qr = baseService.getBeanResult(pr);
        for(UserFace pf : qr.getBeanList()){
            pf.setUrl(pictureHandler.getUserFaceFullUrl(pf.getUrl()));
            pf.setOriginalUrl(pictureHandler.getOriginalFullUrl(pf.getUrl()));
        }
        return callbackDataGrid(qr);
    }

    /**
     * 上传
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResultBean upload(@RequestParam("file") MultipartFile file, PictureFaceUploadRequest pur) {
        //检测出人脸包含id数据，否则没有
        UserFace userFace = pictureHandler.saveUserFace(file,pur);
        userFace.setOriginalUrl(pictureHandler.getOriginalFullUrl(userFace.getOriginalUrl()));
        userFace.setUrl(pictureHandler.getUserFaceFullUrl(userFace.getUrl()));
        return callback(userFace);
    }

    /**
     * 增加人脸
     */
    @RequestMapping(value = "/addFace", method = RequestMethod.POST)
    public ResultBean addFace(@RequestParam("file") MultipartFile file, PictureFaceAddFaceRequest pur) {
        pictureHandler.addUserFace(file,pur);
        return callback(null);
    }


    /**
     * 更新开启状态
     */
    @RequestMapping(value = "/updateOpen", method = RequestMethod.POST)
    public ResultBean updateOpen(@RequestBody @Valid UserFaceUpdateOpenRequest pur) {
        boolean b = pictureHandler.updateUserFaceOpen(pur.getId(),pur.getUserId(),pur.getOpen());
        return callback(b);
    }
}
