package com.yaos.autogenerate;

/**
 * 生成器对外入口
 * @AUTHOR yaos
 * @DATE 2017-02-06
 */
public class AutoGen {

    private static GenService genService = new GenService() ;

    /**
     * 设置配置项
     * @param _config 配置项
     */
    public static void config(AutoGenConfig _config){
        genService.setAutoGenConfig(_config) ;
    }

    /**
     * 设定批量生成的模板，适合于多模版统一数据源
     * @param templates 模板名称
     */
    public static void use(String... templates){
        genService.addTemplates(templates);
    }

    /**
     * 增加数据源
     * @param _params 数据源
     */
    public static void with(DbTablesData _params){
        genService.setParams(_params) ;
    }

    /**
     * 开始生成
     */
    public static void build() throws Exception{
        genService.start();
    }
}
