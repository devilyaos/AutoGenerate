package com.yaos.autogenerate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PathKit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @AUTHOR yaos
 * @DATE 2017-02-06
 */
public class AutoGen {

    private static AutoGen instance = new AutoGen();
    private JSONObject config ;

    /**
     * 获取单例
     * @return 单例对象
     */
    public static AutoGen use(){
        return instance ;
    }

    /**
     * 初始化
     * @return 链式调用
     */
    public AutoGen init(){
        init(PathKit.getRootClassPath() + "/defaultRule.json");
        return this ;
    }

    /**
     * 初始化
     * @param configPath 配置路径
     * @return 链式调用
     */
    public AutoGen init(String configPath){
        if(configPath == null || configPath.length() == 0){
            configPath = PathKit.getRootClassPath() + "/defaultRule.json" ;
        }
        config = getConfig(configPath) ;
        if(config == null){
            throw new NullPointerException("找不到配置");
        }
        return this ;
    }

    /**
     * 创建文件的服务
     */
    public void create(){
        String[] tableNames = (String[]) config.getJSONArray("tableName").toArray();
        for(String tableName : tableNames){
            if(tableName == null || tableName.length() == 0){
                continue;
            }
            foreachCreatation(tableName);
        }
    }

    /**
     * 循环创建服务
     * @param tableName 表名
     */
    private void foreachCreatation(String tableName){

    }

    /**
     * 获取配置
     * @param path 配置路径
     * @return 配置对象
     */
    private JSONObject getConfig(String path){
        BufferedReader reader = null;
        StringBuffer laststr = new StringBuffer("");
        try{
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while((tempString = reader.readLine()) != null){
                laststr.append(tempString);
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(laststr.length() > 0){
            return JSON.parseObject(laststr.toString()) ;
        }else{
            return null ;
        }
    }

    /**
     * 数据库名称按照小驼峰转换成参数名称
     * @param jdbcName 数据库名称
     * @return 参数名称
     */
    private String transJdbcName2ParaName(String jdbcName){
        String[] arr = jdbcName.split("_") ;
        if(arr == null || arr.length == 0){
            return "undefined" ;
        }
        String paraName = arr[0] ;
        for(int i = 1 , len = arr.length ; i < len ; i++){
            paraName += upFirstLetter(paraName);
        }
        return paraName ;
    }

    /**
     * 将单词的首字母大写
     * @param word 需要转换的单词
     * @return 抓换后的单词
     */
    private String upFirstLetter(String word){
        return word.substring(0,1).toUpperCase() + word.substring(1) ;
    }
}
