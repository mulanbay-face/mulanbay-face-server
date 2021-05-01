package cn.mulanbay.face.api.persistent.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fenghong on 2017/8/29.
 * 计划报表sql封装类
 */
public class CommonSqlDto {

    private String sqlContent;

    private List args = new ArrayList();

    public String getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public List getArgs() {
        return args;
    }

    public void addArg(Object arg) {
        this.args.add(arg);
    }
}
