package cn.mulanbay.face.api.web.controller;

import cn.mulanbay.business.domain.PictureFace;
import cn.mulanbay.face.api.handler.PictureHandler;
import cn.mulanbay.face.api.web.bean.request.pictureFace.PictureFaceAddRequest;
import cn.mulanbay.face.api.web.bean.request.pictureFace.PictureFaceSearch;
import cn.mulanbay.face.api.web.bean.request.pictureFace.PictureFaceUpdateOpenRequest;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @Description: 照片人脸
 * @Author: fenghong
 * @Create : 2021/1/10
 */
@RestController
@RequestMapping("/pictureFace")
public class PictureFaceController extends BaseController{

    private static Class<PictureFace> beanClass = PictureFace.class;

    @Autowired
    PictureHandler pictureHandler;

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public ResultBean getData(PictureFaceSearch sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        PageResult<PictureFace> qr = baseService.getBeanResult(pr);
        for(PictureFace pf : qr.getBeanList()){
            pf.setUrl(pictureHandler.getPictureFaceFullUrl(pf.getUrl()));
        }
        return callbackDataGrid(qr);
    }

    /**
     * 增加人脸
     */
    @RequestMapping(value = "/addFace", method = RequestMethod.POST)
    public ResultBean upload(@RequestParam("file") MultipartFile file, PictureFaceAddRequest pur) {
        pictureHandler.addPictureFace(file,pur);
        return callback(null);
    }

    /**
     * 更新开启状态
     */
    @RequestMapping(value = "/updateOpen", method = RequestMethod.POST)
    public ResultBean updateOpen(@RequestBody @Valid PictureFaceUpdateOpenRequest pur) {
        boolean b = pictureHandler.updatePictureFaceOpen(pur.getId(),pur.getUserId(),pur.getOpen());
        return callback(b);
    }
}
