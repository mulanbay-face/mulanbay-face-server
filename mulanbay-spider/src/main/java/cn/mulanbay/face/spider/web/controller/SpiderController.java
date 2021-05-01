package cn.mulanbay.face.spider.web.controller;

import cn.mulanbay.business.domain.SpiderTask;
import cn.mulanbay.face.spider.scrapper.SpiderHandler;
import cn.mulanbay.face.spider.web.bean.request.task.SpiderAddTaskRequest;
import cn.mulanbay.web.bean.response.ResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 *
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
@RestController
@RequestMapping("/spider")
public class SpiderController extends BaseController {

    @Autowired
    SpiderHandler spiderHandler;

    /**
     * 增加调度
     *
     * @return
     */
    @RequestMapping(value = "/addTask", method = RequestMethod.POST)
    public ResultBean addTask(@RequestBody @Valid SpiderAddTaskRequest atr) {
        String[] ids = atr.getIds().split(",");
        for(String s : ids){
            SpiderTask spiderTask = baseService.getObject(SpiderTask.class,Long.valueOf(s));
            spiderHandler.addSpiderTask(spiderTask);
        }
        return callback(null);
    }

}
