package com.github.devilyaos;

import java.util.Map;

public abstract class GetParamsListener {
    public abstract Map<String,Object> addParamsAboutTableInfo(String tableName,String tablePre,String tableComment) ;

    public abstract Map<String,Object> addParamsAboutColumn(String Field,String Type,String Key,String Default,String Comment) ;

    public abstract Map<String,Object> addParamsAboutOthers() ;
}
