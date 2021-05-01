package cn.mulanbay.face.spider.web.bean.request.task;

import javax.validation.constraints.NotEmpty;

/**
 *
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class SpiderAddTaskRequest {

    @NotEmpty(message = "{validate.bean.ids.notNull}")
    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

}
