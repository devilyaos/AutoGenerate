package com.yaos.autogenerate;

import java.util.HashMap;

/**
 * 自动生成需要的配置
 * @AUTHOR yaos
 * @DATE 2017-02-06
 */
public class AutoGenConfig {
    private AutoGenConfig instance ;
    private HashMap<String,Object> params = new HashMap<>();

    private String dbUrl ;
    private String dbUser ;
    private String dbPwd ;

    public AutoGenConfig init(){
        if(instance == null){
            synchronized (this){
                if(instance == null){
                    instance = new AutoGenConfig() ;
                }
            }
        }
        return instance ;
    }

    public AutoGenConfig addGlobalParam(String key,Object value){
        params.put(key,value) ;
        return this ;
    }

    public HashMap<String,Object> getGlobalParams(){
        return params ;
    }

    public void initDB(String _url,String _user,String _pwd){
        dbUrl = _url ;
        dbUser = _user ;
        dbPwd = _pwd ;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPwd() {
        return dbPwd;
    }
}
