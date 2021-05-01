package cn.mulanbay.face.api.web.controller;

import cn.mulanbay.business.domain.FaceMatch;
import cn.mulanbay.business.domain.Picture;
import cn.mulanbay.business.domain.User;
import cn.mulanbay.business.enums.DownloadType;
import cn.mulanbay.business.enums.MatchType;
import cn.mulanbay.business.enums.SearchType;
import cn.mulanbay.common.exception.ApplicationException;
import cn.mulanbay.common.util.BeanCopy;
import cn.mulanbay.common.util.StringUtil;
import cn.mulanbay.face.api.common.ApiErrorCode;
import cn.mulanbay.face.api.common.CacheKey;
import cn.mulanbay.face.api.handler.FaceHandler;
import cn.mulanbay.face.api.handler.PictureHandler;
import cn.mulanbay.face.api.handler.ThreadPoolHandler;
import cn.mulanbay.face.api.persistent.dto.FaceMatchDto;
import cn.mulanbay.face.api.persistent.dto.PictureMatchDto;
import cn.mulanbay.face.api.persistent.service.AuthService;
import cn.mulanbay.face.api.persistent.service.PictureService;
import cn.mulanbay.face.api.thread.FaceMatchThread;
import cn.mulanbay.face.api.thread.ThirdPartFaceThread;
import cn.mulanbay.face.api.web.bean.request.CommonBeanGetRequest;
import cn.mulanbay.face.api.web.bean.request.picture.*;
import cn.mulanbay.face.api.web.bean.response.picture.*;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.web.bean.response.ResultBean;
import com.arcsoft.face.FaceFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 图片功能
 * @Author: fenghong
 * @Create : 2021/1/8
 */
@RestController
@RequestMapping("/picture")
public class PictureController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(PictureController.class);

    private static Class<Picture> beanClass = Picture.class;

    @Autowired
    PictureHandler pictureHandler;

    @Autowired
    PictureService pictureService;

    @Autowired
    AuthService authService;

    @Autowired
    FaceHandler faceHandler;

    @Autowired
    ThreadPoolHandler threadPoolHandler;
    /**
     * 每次最多匹配几张，防止过多导致处理时间过长
     * 优点是无论照片数据有多少，匹配接口的响应时间是一定的（目前是2-7s左右）
     * 缺点是如果没有匹配到数据，前端需要不停的调用匹配接口
     */
    @Value("${face.match.maxCompare}")
    int maxCompare;

    @Value("${face.match.dbRate}")
    double dbRate;

    /**
     * 发现列表
     *
     * @return
     */
    @RequestMapping(value = "/discover", method = RequestMethod.GET)
    public ResultBean discover(PictureDiscoverSearch sf) {
        sf.setSearchType(SearchType.PUB);
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<Picture> qr = baseService.getBeanResult(pr);
        PageResult<PictureDiscoverVo> res = new PageResult();
        res.setMaxRow(qr.getMaxRow());
        res.setPage(qr.getPage());
        res.setPageSize(qr.getPageSize());
        List<PictureDiscoverVo> list = new ArrayList<>();
        for(Picture p : qr.getBeanList()){
            PictureDiscoverVo vo = new PictureDiscoverVo();
            vo.setId(p.getId());
            vo.setTitle(p.getTitle());
            vo.setUserId(p.getUserId());
            vo.setFacesUrl(pictureHandler.getPictureFacesFullUrl(p.getUrl()));
            vo.setSsUrl(pictureHandler.getPictureSsFullUrl(p.getUrl()));
            vo.setMsUrl(pictureHandler.getPictureMsFullUrl(p.getUrl()));
            User user = authService.getUserForCache(vo.getUserId());
            vo.setUserAvatar(user.getAvatar());
            vo.setUsername(user.getUsername());
            vo.setLikes(0L);
            list.add(vo);
        }
        res.setBeanList(list);
        return callbackDataGrid(res);
    }

    /**
     * 获取列表
     *
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public ResultBean getData(PictureSearch sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<Picture> qr = baseService.getBeanResult(pr);
        for(Picture pf : qr.getBeanList()){
            pf.setUrl(pictureHandler.getPictureSsFullUrl(pf.getUrl()));
            pf.setOriginalUrl(pictureHandler.getOriginalFullUrl(pf.getOriginalUrl()));
        }
        return callbackDataGrid(qr);
    }

    /**
     * 上传
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResultBean upload(@RequestParam("file") MultipartFile file,PictureUploadRequest pur) {
        Long id = pictureHandler.savePicture(file,pur);
        this.refreshMaxPictureFaceId();
        return callback(id);
    }

    /**
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid PictureEditRequest pur) {
        Picture br = this.getUserEntity(beanClass, pur.getId(), pur.getUserId());
        BeanCopy.copyProperties(pur,br);
        baseService.updateObject(br);
        return callback(null);
    }

    /**
     * 获取(不绑定用户)
     *
     * @return
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResultBean info(@Valid CommonBeanGetRequest ubg) {
        Picture br = baseService.getObject(beanClass, ubg.getId());
        br.setUrl(pictureHandler.getPictureMsFullUrl(br.getUrl()));
        return callback(br);
    }

    /**
     * 获取（编辑页面使用）
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@Valid CommonBeanGetRequest ubg) {
        Picture br = this.getUserEntity(beanClass, ubg.getId(), ubg.getUserId());
        br.setUrl(pictureHandler.getPictureMsFullUrl(br.getUrl()));
        return callback(br);
    }

    /**
     * 详情（编辑页面、人脸页面使用）
     *
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ResultBean detail(@Valid CommonBeanGetRequest ubg) {
        Picture br = this.getUserEntity(beanClass, ubg.getId(), ubg.getUserId());
        PictureVo pv = new PictureVo();
        BeanCopy.copyProperties(br,pv);
        pv.setOriginalUrl(pictureHandler.getOriginalFullUrl(br.getOriginalUrl()));
        pv.setFacesUrl(pictureHandler.getPictureFacesFullUrl(br.getUrl()));
        pv.setSsUrl(pictureHandler.getPictureSsFullUrl(br.getUrl()));
        pv.setMsUrl(pictureHandler.getPictureMsFullUrl(br.getUrl()));
        return callback(pv);
    }

    /**
     * 预下载
     *
     * @return
     */
    @RequestMapping(value = "/preDownload", method = RequestMethod.GET)
    public ResultBean preDownload(@Valid CommonBeanGetRequest ubg) {
        Picture br = baseService.getObject(beanClass, ubg.getId());
        PicturePreDownloadVo vo = new PicturePreDownloadVo();
        vo.setId(br.getId());
        vo.setDownloadType(br.getDownloadType());
        if(vo.getDownloadType()== DownloadType.REDIRECT){
            //设置原始图片
            vo.setOriginalUrl(pictureHandler.getOriginalFullUrl(br.getOriginalUrl()));
        }else if(vo.getDownloadType()== DownloadType.PAY){
            //查询是否支付过
            //todo 设置价格
            vo.setPrice(100L);
        }
        return callback(vo);
    }

    /**
     * 用户图片的人脸与用户图片匹配
     */
    @RequestMapping(value = "/pf2pMatch", method = RequestMethod.POST)
    public ResultBean pf2pMatch(@RequestBody @Valid Pf2pMatchRequest pmr) {
        long matchFaceId = pmr.getPictureFaceId();
        String matchFaceFaceUrl = faceHandler.generateFaceFolderUrl(matchFaceId);
        String matchFaceFullPath = pictureHandler.getPictureFaceFullPath(matchFaceFaceUrl);
        String matchFaceFullUrl = pictureHandler.getPictureFaceFullUrl(matchFaceFaceUrl);
        Map res = this.getPictureMatches(pmr.getCursor(),pmr.getMinRate(),pmr.getPageSize(),pmr.getPictureFaceId(),matchFaceFullPath,matchFaceFullUrl,MatchType.PF2P);
        return callback(res);
    }

    /**
     * 用户自己的人脸与用户图片匹配
     */
    @RequestMapping(value = "/uf2pMatch", method = RequestMethod.POST)
    public ResultBean uf2pMatch(@RequestBody @Valid Uf2pMatchRequest pmr) {
        long matchFaceId = pmr.getUserFaceId();
        String matchFaceFaceUrl = faceHandler.generateFaceFolderUrl(matchFaceId);
        String matchFaceFullPath = pictureHandler.getUserFaceFullPath(matchFaceFaceUrl);
        String matchFaceFullUrl = pictureHandler.getUserFaceFullUrl(matchFaceFaceUrl);
        Map res = this.getPictureMatches(pmr.getCursor(),pmr.getMinRate(),pmr.getPageSize(),pmr.getUserFaceId(),matchFaceFullPath,matchFaceFullUrl,MatchType.UF2P);
        return callback(res);
    }

    /**
     * 图片匹配
     * @param cursor
     * @param minRate
     * @param pageSize
     * @param matchFaceId
     * @param matchFaceFullPath
     * @param matchFaceFullUrl
     * @param matchType
     * @return
     */
    private Map getPictureMatches(Long cursor, double minRate, int pageSize, long matchFaceId, String matchFaceFullPath, String matchFaceFullUrl, MatchType matchType){
        if(cursor==null){
            Map res = this.getDbPictureMatchFaces(minRate,pageSize,matchFaceId,matchFaceFullUrl,matchType);
            if(res.get("matchList")!=null){
                return res;
            }
        }
        //直接匹配查询
        return this.matchPicture(cursor,minRate,pageSize,matchFaceId,matchFaceFullPath,matchFaceFullUrl,matchType);
    }

    /**
     * 数据库中加载
     * @param minRate
     * @param pageSize
     * @param matchFaceId
     * @param matchFaceFullUrl
     * @param matchType
     * @return
     */
    private Map getDbPictureMatchFaces(double minRate, int pageSize, long matchFaceId, String matchFaceFullUrl, MatchType matchType){
        FaceMatchRequest fmr = new FaceMatchRequest();
        fmr.setMatchType(matchType);
        fmr.setMatchFaceId(matchFaceId);
        fmr.setMinRate(minRate);
        fmr.setPage(1);
        fmr.setPageSize(pageSize);
        PageRequest pr = fmr.buildQuery();
        pr.setBeanClass(FaceMatch.class);
        Sort sort = new Sort("rate",Sort.DESC);
        pr.addSort(sort);
        List<FaceMatch> list = baseService.getBeanList(pr);
        Map res = new HashMap<>();
        Map<String,PictureMatchVo> matchRates = new HashMap<>();
        List<Long> ids = new ArrayList<>();
        if(StringUtil.isNotEmpty(list)){
            for(FaceMatch fm : list){
                PictureMatchVo vo = new PictureMatchVo();
                vo.setMatchFaceId(matchFaceId);
                vo.setMatchFaceUrl(matchFaceFullUrl);
                Long matchedFaceId = fm.getMatchedFaceId();
                vo.setMatchedFaceId(matchedFaceId);
                String toMatchFaceFaceUrl = faceHandler.generateFaceFolderUrl(matchedFaceId);
                vo.setMatchedFaceUrl(pictureHandler.getPictureFaceFullUrl(toMatchFaceFaceUrl));
                vo.setMatchType(matchType);
                vo.setRate(fm.getRate());
                ids.add(matchedFaceId);
                matchRates.put(matchedFaceId+"",vo);
            }
            List<PictureMatchVo> matchList = this.assemblePictureMatch(ids,matchRates);
            res.put("matchList", matchList);
            res.put("nextCursor",this.getMaxPictureFaceId());
        }
        return res;
    }

    /**
     * 匹配
     * @param cursor
     * @param minRate
     * @param pageSize
     * @param matchFaceId
     * @param matchFaceFullPath 硬盘上全路径
     * @param matchFaceFullUrl http的全url
     * @return
     */
    private Map matchPicture(Long cursor, double minRate, int pageSize, long matchFaceId, String matchFaceFullPath, String matchFaceFullUrl, MatchType matchType){
        if(cursor==null){
            cursor = this.getMaxPictureFaceId();
        }
        long nextCursor=0L;
        List<Long> ids = new ArrayList<>();
        FaceFeature matchFeature = faceHandler.extractFaceFeature(matchFaceFullPath,matchFaceId,matchType.getMf());
        if(matchFeature==null){
            throw new ApplicationException(ApiErrorCode.FACE_HAS_NO_FEATURE);
        }
        Map<String,PictureMatchVo> matchRates = new HashMap<>();
        //从最新的开始
        int cs =0;
        for(long i=cursor;i>0;i--){
            String toMatchFaceFaceUrl = faceHandler.generateFaceFolderUrl(i);
            String toMatchFaceFaceFullUrl = pictureHandler.getPictureFaceFullPath(toMatchFaceFaceUrl);
            FaceFeature toMatchFeature = faceHandler.extractFaceFeature(toMatchFaceFaceFullUrl,i,matchType.getTmf());
            double rate = faceHandler.compare(matchFeature,toMatchFeature);
            this.addFaceMatch(matchType,matchFaceId,i,rate);
            logger.debug(matchFaceFullPath+" vs "+toMatchFaceFaceFullUrl+" 匹配度:"+rate);
            if(rate>=minRate){
                ids.add(i);
                PictureMatchVo vo = new PictureMatchVo();
                vo.setMatchFaceId(matchFaceId);
                vo.setMatchFaceUrl(matchFaceFullUrl);
                vo.setMatchedFaceId(i);
                vo.setMatchedFaceUrl(pictureHandler.getPictureFaceFullUrl(toMatchFaceFaceUrl));
                vo.setMatchType(matchType);
                vo.setRate(rate);
                matchRates.put(i+"",vo);
            }
            cs++;
            if(ids.size()>=pageSize||cs>=maxCompare){
                //退出循环
                nextCursor=i;
                break;
            }
        }
        Map res = new HashMap<>();
        res.put("nextCursor",nextCursor);
        List<PictureMatchVo> matchList = this.assemblePictureMatch(ids,matchRates);
        res.put("matchList", matchList);
        return res;
    }

    /**
     * 组装
     * @param ids
     * @param matchRates
     * @return
     */
    private List<PictureMatchVo> assemblePictureMatch(List<Long> ids,Map<String,PictureMatchVo> matchRates){
        if(ids.isEmpty()){
             return new ArrayList<>();
        }else {
            List<PictureMatchVo> matchList = new ArrayList<>();
            List<PictureMatchDto> dtoList = pictureService.getPictureMatchList(ids);
            for (PictureMatchDto dto : dtoList) {
                PictureMatchVo vo = matchRates.get(dto.getMatchedFaceId().toString());
                vo.setId(dto.getId().longValue());
                vo.setTitle(dto.getTitle());
                vo.setUserId(dto.getUserId().longValue());
                vo.setFacesUrl(pictureHandler.getPictureFacesFullUrl(dto.getUrl()));
                vo.setSsUrl(pictureHandler.getPictureSsFullUrl(dto.getUrl()));
                vo.setMsUrl(pictureHandler.getPictureMsFullUrl(dto.getUrl()));
                User user = authService.getUserForCache(vo.getUserId());
                vo.setUserAvatar(user.getAvatar());
                vo.setUsername(user.getUsername());
                matchList.add(vo);
            }
            return matchList;
        }
    }

    /**
     * 增加人脸匹配
     * @param matchType
     * @param matchFaceId
     * @param matchedFaceId
     * @param rate
     */
    private void addFaceMatch(MatchType matchType, Long matchFaceId, Long matchedFaceId, double rate){
        if(rate>=dbRate){
            FaceMatchThread thread = new FaceMatchThread(matchType,matchFaceId,matchedFaceId,rate);
            threadPoolHandler.pushThread(thread);
        }
    }
    /**
     * 用户图片的人脸与用户的人脸匹配
     */
    @RequestMapping(value = "/pf2ufMatch", method = RequestMethod.POST)
    public ResultBean pf2ufMatch(@RequestBody @Valid Pf2ufMatchRequest pmr) {
        long matchFaceId = pmr.getPictureFaceId();
        String matchFaceFaceUrl = faceHandler.generateFaceFolderUrl(matchFaceId);
        String matchFaceFullPath = pictureHandler.getPictureFaceFullPath(matchFaceFaceUrl);
        String matchFaceFullUrl = pictureHandler.getPictureFaceFullUrl(matchFaceFaceUrl);
        Map res = this.getFacesMatches(pmr.getCursor(),pmr.getMinRate(),pmr.getPageSize(),pmr.getPictureFaceId(),matchFaceFullPath,matchFaceFullUrl,MatchType.PF2UF);
        return callback(res);
    }

    /**
     * 用户自己的人脸与用户的人脸匹配
     */
    @RequestMapping(value = "/uf2ufMatch", method = RequestMethod.POST)
    public ResultBean uf2ufMatch(@RequestBody @Valid Uf2ufMatchRequest pmr) {
        long matchFaceId = pmr.getUserFaceId();
        String matchFaceFaceUrl = faceHandler.generateFaceFolderUrl(matchFaceId);
        String matchFaceFullPath = pictureHandler.getUserFaceFullPath(matchFaceFaceUrl);
        String matchFaceFullUrl = pictureHandler.getUserFaceFullUrl(matchFaceFaceUrl);
        Map res = this.getFacesMatches(pmr.getCursor(),pmr.getMinRate(),pmr.getPageSize(),pmr.getUserFaceId(),matchFaceFullPath,matchFaceFullUrl,MatchType.UF2UF);
        return callback(res);
    }

    /**
     * 图片匹配
     * @param cursor
     * @param minRate
     * @param pageSize
     * @param matchFaceId
     * @param matchFaceFullPath
     * @param matchFaceFullUrl
     * @param matchType
     * @return
     */
    private Map getFacesMatches(Long cursor, double minRate, int pageSize, long matchFaceId, String matchFaceFullPath, String matchFaceFullUrl, MatchType matchType){
        if(cursor==null){
            Map res = this.getDbFaceMatchs(minRate,pageSize,matchFaceId,matchFaceFullUrl,matchType);
            if(res.get("nextCursor")!=null){
                return res;
            }
        }
        //直接匹配查询
        return this.matchFace(cursor,minRate,pageSize,matchFaceId,matchFaceFullPath,matchFaceFullUrl,matchType);
    }

    /**
     * 数据库中加载
     * @param minRate
     * @param pageSize
     * @param matchFaceId
     * @param matchFaceFullUrl
     * @param matchType
     * @return
     */
    private Map getDbFaceMatchs(double minRate, int pageSize, long matchFaceId, String matchFaceFullUrl, MatchType matchType){
        FaceMatchRequest fmr = new FaceMatchRequest();
        fmr.setMatchType(matchType);
        fmr.setMatchFaceId(matchFaceId);
        fmr.setMinRate(minRate);
        fmr.setPage(1);
        fmr.setPageSize(pageSize);
        PageRequest pr = fmr.buildQuery();
        pr.setBeanClass(FaceMatch.class);
        Sort sort = new Sort("rate",Sort.DESC);
        pr.addSort(sort);
        List<FaceMatch> list = baseService.getBeanList(pr);
        Map res = new HashMap<>();
        Map<String,FaceMatchVo> matchRates = new HashMap<>();
        List<Long> ids = new ArrayList<>();
        if(StringUtil.isNotEmpty(list)){
            for(FaceMatch fm : list){
                FaceMatchVo vo = new FaceMatchVo();
                vo.setMatchFaceId(matchFaceId);
                vo.setMatchFaceUrl(matchFaceFullUrl);
                Long matchedFaceId = fm.getMatchedFaceId();
                vo.setMatchedFaceId(matchedFaceId);
                String toMatchFaceFaceUrl = faceHandler.generateFaceFolderUrl(matchedFaceId);
                vo.setMatchedFaceUrl(pictureHandler.getUserFaceFullUrl(toMatchFaceFaceUrl));
                vo.setMatchType(matchType);
                vo.setRate(fm.getRate());
                ids.add(matchedFaceId);
                matchRates.put(matchedFaceId+"",vo);
            }
            List<FaceMatchVo> matchList = this.assembleFaceMatch(ids,matchRates);
            res.put("matchList", matchList);
            res.put("nextCursor",this.getMaxPictureFaceId());
        }
        return res;
    }

    /**
     * 组装
     * @param ids
     * @param matchRates
     * @return
     */
    private List<FaceMatchVo> assembleFaceMatch(List<Long> ids,Map<String,FaceMatchVo> matchRates){
        if(ids.isEmpty()){
            return new ArrayList<>();
        }else {
            List<FaceMatchVo> matchList = new ArrayList<>();
            List<FaceMatchDto> dtoList = pictureService.getFaceMatchList(ids);
            for (FaceMatchDto dto : dtoList) {
                FaceMatchVo vo = matchRates.get(dto.getId().toString());
                vo.setUserId(dto.getUserId().longValue());
                User user = authService.getUserForCache(vo.getUserId());
                vo.setUserAvatar(user.getAvatar());
                vo.setUsername(user.getUsername());
                matchList.add(vo);
            }
            return matchList;
        }
    }

    /**
     * 获取用户人脸匹配列表
     * @param cursor
     * @param minRate
     * @param pageSize
     * @param matchFaceId
     * @param matchFaceFullPath
     * @param matchFaceFullUrl
     * @return
     */
    private Map matchFace(Long cursor, double minRate, int pageSize, long matchFaceId, String matchFaceFullPath, String matchFaceFullUrl, MatchType matchType){
        if(cursor==null){
            cursor = this.getMaxUserFaceId();
        }
        FaceFeature matchFeature = faceHandler.extractFaceFeature(matchFaceFullPath,matchFaceId,matchType.getMf());
        if(matchFeature==null){
            throw new ApplicationException(ApiErrorCode.FACE_HAS_NO_FEATURE);
        }
        long nextCursor=0L;
        List<Long> ids = new ArrayList<>();
        Map<String,FaceMatchVo> matchRates = new HashMap<>();
        int cs=0;
        //从最新的开始
        for(long i=cursor;i>0;i--){
            String toMatchFaceFaceUrl = faceHandler.generateFaceFolderUrl(i);
            String toMatchFaceFaceFullUrl = pictureHandler.getPictureFaceFullPath(toMatchFaceFaceUrl);
            FaceFeature toMatchFeature = faceHandler.extractFaceFeature(toMatchFaceFaceFullUrl,i,matchType.getTmf());
            double rate = faceHandler.compare(matchFeature,toMatchFeature);
            this.addFaceMatch(matchType,matchFaceId,i,rate);
            if(rate>=minRate){
                ids.add(i);
                FaceMatchVo vo = new FaceMatchVo();
                vo.setMatchFaceId(matchFaceId);
                vo.setMatchFaceUrl(matchFaceFullUrl);
                vo.setMatchedFaceId(i);
                vo.setMatchedFaceUrl(pictureHandler.getUserFaceFullUrl(toMatchFaceFaceUrl));
                vo.setMatchType(matchType);
                vo.setRate(rate);
                matchRates.put(i+"",vo);
            }
            cs++;
            if(ids.size()>=pageSize||cs>=maxCompare){
                //退出循环
                nextCursor=i;
                break;
            }
        }
        Map res = new HashMap<>();
        res.put("nextCursor",nextCursor);
        List<FaceMatchVo> matchList = this.assembleFaceMatch(ids,matchRates);
        res.put("matchList", matchList);
        return res;
    }

    /**
     * 第三方人脸处理
     */
    @RequestMapping(value = "/thirdPartFace", method = RequestMethod.POST)
    public ResultBean thirdPartFace(@RequestBody @Valid ThirdPartFaceDataRequest pur) {
        threadPoolHandler.pushThread(new ThirdPartFaceThread(pur));
        return callback(null);
    }

    /**
     * 获取图片人脸最大ID
     * todo 加读写锁
     * @return
     */
    private long getMaxPictureFaceId(){
        Long id = (Long) cacheHandler.get(CacheKey.PICTURE_FACE_MAX_ID);
        if(id == null){
            id = pictureService.getMaxPictureFaceId();
        }
        cacheHandler.set(CacheKey.PICTURE_FACE_MAX_ID,id,300);
        return id.longValue();
    }

    /**
     * 刷新图片人脸最大ID
     */
    private void refreshMaxPictureFaceId(){
        cacheHandler.delete(CacheKey.PICTURE_FACE_MAX_ID);
    }

    /**
     * 获取用户人脸最大ID
     * todo 加读写锁
     * @return
     */
    private long getMaxUserFaceId(){
        Long id = (Long) cacheHandler.get(CacheKey.USER_FACE_MAX_ID);
        if(id == null){
            id = pictureService.getMaxUserFaceId();
        }
        cacheHandler.set(CacheKey.USER_FACE_MAX_ID,id,300);
        return id.longValue();
    }

    /**
     * 刷新用户人脸最大ID
     */
    private void refreshMaxUserFaceId(){
        cacheHandler.delete(CacheKey.USER_FACE_MAX_ID);
    }

}
