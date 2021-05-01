package cn.mulanbay.face.api.web.bean.request;

import cn.mulanbay.common.aop.BindUser;

/**
 * 日历图表统计
 *
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class CalendarChartStatSearch implements BindUser {

    private Integer year;

    private Long userId;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
