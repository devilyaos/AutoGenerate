package com.yaos.autogenerate;

/**
 * 表的列
 * @AUTHOR yaos
 * @DATE 2017-02-06
 */
public class DbTablesColumn {
    /**
     * 方法名称
     */
    private String methodName ;
    /**
     * 方法类型
     */
    private String methodType ;
    /**
     * 参数名称
     */
    private String paramName ;
    /**
     * 参数类型
     */
    private String paramType ;
    /**
     * 数据库中的名称
     */
    private String jdbcName ;
    /**
     * 数据库中的类型
     */
    private String jdbcType ;
    /**
     * 是否可以为空
     */
    private Boolean canNull ;
    /**
     * 是否是主键
     */
    private Boolean isPrimary ;
    /**
     * 默认值的字符串
     */
    private String defaultValue ;
    /**
     * 备注
     */
    private String comment ;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getJdbcName() {
        return jdbcName;
    }

    public void setJdbcName(String jdbcName) {
        this.jdbcName = jdbcName;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public Boolean canNull() {
        return canNull;
    }

    public void setCanNull(Boolean canNull) {
        this.canNull = canNull;
    }

    public Boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
