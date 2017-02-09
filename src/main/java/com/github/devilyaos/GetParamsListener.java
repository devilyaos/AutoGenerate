package com.github.devilyaos;

import java.util.Map;

/**
 * 获取参数
 * @AUTHOR yaos
 * @DATE 2017-02-09
 */
public abstract class GetParamsListener {
    /**
     * 增加表相关参数
     * 接口传入参数在模板默认包含
     * @param tableName 表名
     * @param tablePre 表前缀
     * @param tableComment 表注释
     * @return 参数集
     */
    public abstract Map<String,Object> addParamsAboutTableInfo(String tableName,String tablePre,String tableComment) ;

    /**
     * 增加字段相关参数
     * 注意:已有的column参数为show full columns from table返回的所有字段，接口传入参数在模板默认包含
     * @param Field 字段名称
     * @param Type 字段类型
     * @param Key 字段主键标记
     * @param Default 字段默认值
     * @param Comment 字段注释
     * @return 参数集
     */
    public abstract Map<String,Object> addParamsAboutColumn(String Field,String Type,String Key,String Default,String Comment) ;

    /**
     * 增加其他相关参数
     * @return 参数集
     */
    public abstract Map<String,Object> addParamsAboutOthers() ;
}
