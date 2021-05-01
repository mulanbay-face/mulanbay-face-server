package cn.mulanbay.face.api.web.bean.response.chart;

import cn.mulanbay.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * echarts的树形结构
 *
 * @see https://echarts.baidu.com/examples/editor.html?c=tree-basic
 * @author fenghong
 * @create 2017-07-10 21:44
 */
public class ChartTreeDetailData {

    private double value;

    private String name;

    public ChartTreeDetailData() {
    }

    public ChartTreeDetailData(double value, String name) {
        this.value = value;
        this.name = name;
    }

    private List<ChartTreeDetailData> children;

    /**
     * 添加子节点
     *
     * @param value
     * @param name
     */
    public void addChild(double value, String name) {
        this.addChild(new ChartTreeDetailData(value, name));
    }

    public void addChild(ChartTreeDetailData tmb) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(tmb);
    }

    /**
     * 添加子节点
     *
     * @param value
     * @param name
     */
    public void findAndAppendChild(double value, String name) {
        ChartTreeDetailData ct = this.findChild(name);
        if (ct == null) {
            this.addChild(new ChartTreeDetailData(value, name));
        } else {
            ct.setValue(ct.getValue() + value);
        }
    }

    public ChartTreeDetailData findChild(String name) {
        if (children == null) {
            return null;
        }
        for (ChartTreeDetailData ct : children) {
            if (ct.getName().equals(name)) {
                return ct;
            }
        }
        return null;
    }

    /**
     * 如果有子节点，则由子节点的总和决定
     *
     * @return
     */
    public double getValue() {
        if (StringUtil.isNotEmpty(children)) {
            double b = 0;
            for (ChartTreeDetailData ct : children) {
                b += ct.getValue();
            }
            return b;
        }
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChartTreeDetailData> getChildren() {
        return children;
    }

    public void setChildren(List<ChartTreeDetailData> children) {
        this.children = children;
    }
}
