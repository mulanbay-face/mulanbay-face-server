package cn.mulanbay.face.api.persistent.dto;

import cn.mulanbay.face.api.persistent.dto.common.CalendarDateStat;

import java.math.BigInteger;

public class OperationLogDateStat implements DateStat, CalendarDateStat {
    // 月份
    private Integer indexValue;
    private BigInteger totalCount;

    public Integer getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(Integer indexValue) {
        this.indexValue = indexValue;
    }

    public BigInteger getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(BigInteger totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public double getCalendarStatValue() {
        return totalCount.doubleValue();
    }

    @Override
    public int getDateIndexValue() {
        return indexValue.intValue();
    }
}
