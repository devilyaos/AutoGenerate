package com.yaos.autogenerate;

import com.jfinal.kit.JMap;

import java.util.*;

/**
 * 构建逻辑
 * @AUTHOR yaos
 * @DATE 2017-02-06
 */
public class GenService {

    private String[] templateNameArr ;
    private BaseAutoGenData dataSource ;
    private AutoGenConfig autoGenConfig ;
    private static GenService instance ;

    public static GenService init(){
        if(instance == null){
            synchronized (GenService.class){
                if(instance == null){
                    instance = new GenService() ;
                }
            }
        }
        return instance ;
    }

    protected void addTemplates(String... templateNames){
        templateNameArr = templateNames;
    }

    protected void setParams(BaseAutoGenData _params) {
        dataSource = _params;
    }

    protected void setAutoGenConfig(AutoGenConfig _autoGenConfig) {
        autoGenConfig = _autoGenConfig;
    }

    protected void start() throws Exception{
        JMap dataMap = JMap.create() ;
        //获取全局变量
        HashMap<String,Object> globalParam = autoGenConfig.getGlobalParams() ;
        //获取数据集的内容
        HashMap<String,Object> params = dataSource.getData() ;
        //TODO 遍历生成文件，此处params每一个key代表一个新的模板任务
        clearAllService();
    }

    private void clearAllService(){
        dataSource.clear();
    }
}
