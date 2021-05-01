package cn.mulanbay.face.spider.web.controller;

import cn.mulanbay.business.domain.SpiderTask;
import cn.mulanbay.common.util.BeanCopy;
import cn.mulanbay.face.spider.web.bean.request.CommonBeanDeleteRequest;
import cn.mulanbay.face.spider.web.bean.request.CommonBeanGetRequest;
import cn.mulanbay.face.spider.web.bean.request.task.SpiderTaskFormRequest;
import cn.mulanbay.face.spider.web.bean.request.task.SpiderTaskSearch;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 *
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/spiderTask")
public class SpiderTaskController extends BaseController {

    private static Class<SpiderTask> beanClass = SpiderTask.class;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public ResultBean getData(SpiderTaskSearch sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<SpiderTask> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

    /**
     * 创建
     *
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResultBean create(@RequestBody @Valid SpiderTaskFormRequest formRequest) {
        SpiderTask bean = new SpiderTask();
        BeanCopy.copyProperties(formRequest, bean);
        bean.setCreatedTime(new Date());
        baseService.saveObject(bean);
        return callback(null);
    }


    /**
     * 获取详情
     *
     * @return
     */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public ResultBean get(@Valid CommonBeanGetRequest getRequest) {
        SpiderTask bean = baseService.getObject(beanClass, getRequest.getId());
        return callback(bean);
    }

    /**
     * 修改
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultBean edit(@RequestBody @Valid SpiderTaskFormRequest formRequest) {
        SpiderTask bean = baseService.getObject(beanClass, formRequest.getId());
        BeanCopy.copyProperties(formRequest, bean);
        bean.setLastModifyTime(new Date());
        baseService.updateObject(bean);
        return callback(null);
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResultBean delete(@RequestBody @Valid CommonBeanDeleteRequest deleteRequest) {
        String[] ids = deleteRequest.getIds().split(",");
        baseService.deleteObjects(beanClass,ids);
        return callback(null);
    }

}
