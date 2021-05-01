package cn.mulanbay.face.spider.web.controller;

import cn.mulanbay.business.domain.SpiderLog;
import cn.mulanbay.face.spider.web.bean.request.task.SpiderLogSearch;
import cn.mulanbay.persistent.query.PageRequest;
import cn.mulanbay.persistent.query.PageResult;
import cn.mulanbay.persistent.query.Sort;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/spiderLog")
public class SpiderLogController extends BaseController {

    private static Class<SpiderLog> beanClass = SpiderLog.class;

    /**
     * 获取列表数据
     *
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public ResultBean getData(SpiderLogSearch sf) {
        PageRequest pr = sf.buildQuery();
        pr.setBeanClass(beanClass);
        Sort sort = new Sort("createdTime", Sort.DESC);
        pr.addSort(sort);
        PageResult<SpiderLog> qr = baseService.getBeanResult(pr);
        return callbackDataGrid(qr);
    }

}
