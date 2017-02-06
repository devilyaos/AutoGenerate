package com.yaos.autogenerate;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动生成时的数据源
 * @AUTHOR yaos
 * @DATE 2017-02-06
 */
public class DbTablesData implements BaseAutoGenData{

    private Map<String,Object> dataSource = new HashMap<String,Object>();
    private DruidPlugin dp ;
    private ActiveRecordPlugin arp ;
    private String[] tableNames ;
    private String[] tableComments ;
    private JSONObject ruleObj ;

    /**
     * 设置数据源
     * @param _url 数据库地址
     * @param _user 数据库用户名
     * @param _pwd 数据库密码
     * @return 返回链式调用
     */
    public DbTablesData initDB(String _url,String _user,String _pwd){
        dp = new DruidPlugin(_url, _user, _pwd);
        arp = new ActiveRecordPlugin(dp);
        dp.start();
        arp.start();
        return this ;
    }

    /**
     * 添加表名，可同时添加多张表名
     * @param _tableNames (多张)表名
     * @return 返回链式调用
     */
    public DbTablesData addTableNames(String... _tableNames){
        tableNames = _tableNames ;
        return this ;
    }

    /**
     * 添加表注释，可同时添加多张表注释
     * @param _tableComments (多张)表注释
     * @return 返回链式调用
     */
    public DbTablesData addTableComments(String... _tableComments){
        tableComments = _tableComments ;
        return this ;
    }

    /**
     * 添加解析规则，如未填路径则使用默认模板
     * @param ruleFilePath 规则文件绝对路径
     * @return 返回链式调用
     */
    public DbTablesData setRule(String ruleFilePath){
        if(ruleFilePath == null || ruleFilePath.length() == 0){
            ruleFilePath = PathKit.getWebRootPath() + "/defaultRule.json";
        }
        BufferedReader reader = null;
        StringBuffer laststr = new StringBuffer("");
        try{
            FileInputStream fileInputStream = new FileInputStream(ruleFilePath);
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
            ruleObj = JSON.parseObject(laststr.toString()) ;
        }
        return this ;
    }

    public HashMap<String, Object> getData() throws Exception{
        if(dp == null || arp == null){
            throw new NotActiveException("数据库并没有初始化") ;
        }
        if(tableNames == null || tableNames.length == 0){
            throw new NullPointerException("缺少待检索的表名") ;
        }
        if(tableComments == null || tableComments.length != tableNames.length){
            throw new NullPointerException("表注释信息与表名数量不一致") ;
        }
        if(ruleObj == null || ruleObj.isEmpty() || !ruleObj.containsKey("columnType")){
            throw new NullPointerException("未找到规则定义");
        }
        List<Record> jdbcColumnList ;
        DbTablesInfo tableInfo ;
        HashMap<String,Object> params = new HashMap<>() ;
        for(int i = 0 , len = tableNames.length ; i < len ; i++){
            tableInfo = convertTableInfo(tableNames[i],tableComments[i]) ;
            jdbcColumnList = Db.find("show full columns from "+tableNames[i]) ;
            List<DbTablesColumn> columnList = transRecordList2ColumnList(jdbcColumnList);
            tableInfo.setColumnList(columnList);
            for(DbTablesColumn column : columnList){
                if(column.isPrimary()){
                    tableInfo.setPrimaryKey(column);
                    break ;
                }
            }
            params.put(tableNames[i],tableInfo) ;
        }

        return params;
    }

    public void clear() {
        dataSource.clear();
        arp.stop();
        dp.stop();
    }

    /**
     * 转换表信息
     * @param tableName 表名
     * @param tableComment 表注释
     * @return 表信息
     */
    private DbTablesInfo convertTableInfo(String tableName,String tableComment){
        DbTablesInfo info = new DbTablesInfo() ;
        info.setJdbcName(tableName);
        info.setComment(tableComment);
        info.setParaName(transJdbcName2ParaName(info.getJdbcName()));
        info.setClazzName(upFirstLetter(info.getParaName()));
        return info ;
    }

    /**
     * 将record列表转换成column列表
     * @param recordList record列表源
     * @return column列表源
     */
    private List<DbTablesColumn> transRecordList2ColumnList(List<Record> recordList){
        List<DbTablesColumn> columnList = new ArrayList<>();
        DbTablesColumn column ;
        String tempStr ;
        for(Record record : recordList){
            column = new DbTablesColumn() ;
            column.setJdbcName(record.get("Field","unknown"));
            tempStr = record.get("Type","unknown") ;
            column.setJdbcType(tempStr.indexOf("(") > 0 ? tempStr.substring(0,tempStr.indexOf("(")) : tempStr);
            column.setCanNull("YES".equals(record.get("Null","YES")));
            column.setPrimary("PRI".equals(record.get("Key","")));
            column.setDefaultValue(record.get("Default",""));
            column.setComment(record.get("comment",""));
            column.setParamType(ruleObj.containsKey(column.getJdbcType()) ? ruleObj.get(column.getJdbcType()).toString() : "undefined");
            column.setParamName(transJdbcName2ParaName(column.getJdbcName()));
            column.setMethodType(upFirstLetter(column.getParamName()));
            column.setMethodType(upFirstLetter(column.getParamType()));
        }
        return columnList ;
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
