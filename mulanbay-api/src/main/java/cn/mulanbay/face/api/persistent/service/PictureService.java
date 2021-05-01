package cn.mulanbay.face.api.persistent.service;

import cn.mulanbay.business.domain.FaceMatch;
import cn.mulanbay.business.domain.Picture;
import cn.mulanbay.business.domain.PictureDetail;
import cn.mulanbay.business.domain.PictureFace;
import cn.mulanbay.business.enums.FaceSource;
import cn.mulanbay.common.exception.ErrorCode;
import cn.mulanbay.common.exception.PersistentException;
import cn.mulanbay.face.api.persistent.dto.FaceMatchDto;
import cn.mulanbay.face.api.persistent.dto.PictureMatchDto;
import cn.mulanbay.persistent.common.BaseException;
import cn.mulanbay.persistent.dao.BaseHibernateDao;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description: 图片
 * @Author: fenghong
 * @Create : 2021/1/8
 */
@Service
@Transactional
public class PictureService extends BaseHibernateDao {

    /**
     * 保存图片
     * @param picture
     * @param pictureDetail
     * @param faces
     */
    public void savePicture(Picture picture, PictureDetail pictureDetail, List<PictureFace> faces) {
        try {
            this.saveEntity(picture);
            if(pictureDetail!=null){
                pictureDetail.setId(picture.getId());
                pictureDetail.setUserId(picture.getUserId());
                this.saveEntity(pictureDetail);
            }
            for(PictureFace pf : faces){
                pf.setUserId(picture.getUserId());
                pf.setPictureId(picture.getId());
                pf.setOpen(true);
                pf.setMatchs(0L);
                pf.setHits(0L);
                pf.setSource(FaceSource.AUTO);
                pf.setShotTime(picture.getShotTime());
                pf.setCreatedTime(new Date());
                this.saveEntity(pf);
            }
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存图片异常", e);
        }
    }

    /**
     * 保存人脸匹配数据
     * @param fm
     * @return
     */
    public void saveFaceMatch(FaceMatch fm){
        try {
            String hql = "select count(0) from FaceMatch where matchType=?0 and matchFaceId=?1 and matchedFaceId=?2 ";
            Long n = this.getCount(hql,fm.getMatchType(),fm.getMatchFaceId(),fm.getMatchedFaceId());
            if(n>0){
                //已经存在
                return;
            }
            this.saveEntity(fm);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_ADD_ERROR,
                    "保存人脸匹配数据异常", e);
        }
    }


    /**
     * 获取图片人脸列表
     * @param pictureId
     * @return
     */
    public List<PictureFace> getPictureFaces(Long pictureId){
        try {
            String hql = "from PictureFace where pictureId=?0 ";
            return this.getEntityListNoPageHQL(hql,pictureId);
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取图片人脸列表异常", e);
        }
    }

    /**
     * 获取图片人脸最大ID
     * @return
     */
    public Long getMaxPictureFaceId(){
        try {
            String hql = "select max(id) from PictureFace";
            Long id = (Long) this.getEntityForOne(hql);
            return id;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取图片人脸最大ID异常", e);
        }
    }


    /**
     * 获取用户人脸最大ID
     * @return
     */
    public Long getMaxUserFaceId(){
        try {
            String hql = "select max(id) from UserFace";
            Long id = (Long) this.getEntityForOne(hql);
            return id;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_ERROR,
                    "获取用户人脸最大ID异常", e);
        }
    }

    /**
     * 获取图片匹配列表
     *
     * @return
     */
    public List<PictureMatchDto> getPictureMatchList(List<Long> ids) {
        try {
            String p="";
            int n = ids.size();
            for(int i=0;i<n;i++){
                if(i<n-1){
                    p+="?"+i+",";
                }else{
                    p+="?"+i;
                }
            }
            String sql = "select p.id,p.user_id as userId,p.title, p.url,pf.id as matchedFaceId  from picture p,picture_face pf where p.id = pf.picture_id and pf.id in("+p+")";
            List<PictureMatchDto> result = this.getEntityListWithClassSQL(sql, -1, 0,
                    PictureMatchDto.class,ids.toArray());
            return result;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取图片匹配列表异常", e);
        }
    }

    /**
     * 获取人脸匹配列表
     *
     * @return
     */
    public List<FaceMatchDto> getFaceMatchList(List<Long> ids) {
        try {
            String p="";
            int n = ids.size();
            for(int i=0;i<n;i++){
                if(i<n-1){
                    p+="?"+i+",";
                }else{
                    p+="?"+i;
                }
            }
            String sql = "select id,user_id as userId from user_face where id in("+p+")";
            List<FaceMatchDto> result = this.getEntityListWithClassSQL(sql, -1, 0,
                    FaceMatchDto.class,ids.toArray());
            return result;
        } catch (BaseException e) {
            throw new PersistentException(ErrorCode.OBJECT_GET_LIST_ERROR,
                    "获取人脸匹配列表异常", e);
        }
    }
}
