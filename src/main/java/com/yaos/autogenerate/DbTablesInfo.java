package com.yaos.autogenerate;

import java.util.List;

/**
 * 表的信息
 * @AUTHOR yaos
 * @DATE 2017-02-06
 */
public class DbTablesInfo {
    /**
     * 数据库中的名称
     */
    private String jdbcName ;
    /**
     * 参数中的名称
     */
    private String paraName ;
    /**
     * 类中的名称
     */
    private String ClazzName ;
    /**
     * 表注释
     */
    private String comment ;
    /**
     * 主键
     */
    private DbTablesColumn primaryKey ;
    /**
     * 表字段信息
     */
    private List<DbTablesColumn> columnList ;

    public String getJdbcName() {
        return jdbcName;
    }

    public void setJdbcName(String jdbcName) {
        this.jdbcName = jdbcName;
    }

    public String getParaName() {
        return paraName;
    }

    public void setParaName(String paraName) {
        this.paraName = paraName;
    }

    public String getClazzName() {
        return ClazzName;
    }

    public void setClazzName(String clazzName) {
        ClazzName = clazzName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public DbTablesColumn getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(DbTablesColumn primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<DbTablesColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<DbTablesColumn> columnList) {
        this.columnList = columnList;
    }
}
