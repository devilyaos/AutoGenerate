package com.github.devilyaos;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import org.apache.commons.io.FileUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.joda.time.DateTime;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoGen {

    private static AutoGen instance = new AutoGen();
    private static JSONObject config ;
    private DruidPlugin dp ;
    private ActiveRecordPlugin arp ;
    private FileResourceLoader resourceLoader ;
    private Configuration cfg ;
    private GroupTemplate gt ;

    public static AutoGen use(){
        return instance ;
    }

    public AutoGen init() throws IOException {
        init(PathKit.getRootClassPath() + "/defaultRule.json");
        return this ;
    }

    public AutoGen init(String configPath) throws IOException {
        if(configPath == null || configPath.length() == 0){
            configPath = PathKit.getRootClassPath() + "/defaultRule.json" ;
        }
        config = getConfig(configPath) ;
        if(config == null){
            throw new NullPointerException("No Config.");
        }
        JSONObject dataSourceConfig = config.getJSONObject("dataSource") ;
        dp = new DruidPlugin(
                dataSourceConfig.getString("url"),
                dataSourceConfig.getString("user"),
                dataSourceConfig.getString("pwd")
        );
        dp.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setDbType("mysql");
        WallConfig wallConfig = new WallConfig();
        wallConfig.setFunctionCheck(false);
        wallConfig.setCreateTableAllow(true);
        wallConfig.setCommentAllow(true);
        wallConfig.setMultiStatementAllow(true);
        wall.setConfig(wallConfig);
        dp.addFilter(wall);
        arp = new ActiveRecordPlugin(dp);
        arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));// 大小写不敏感
        arp.setDialect(new MysqlDialect());
        dp.start();
        arp.start();
        resourceLoader = new FileResourceLoader(config.getString("baseTemplatePath"),"utf-8");
        cfg = Configuration.defaultConfiguration();
        gt = new GroupTemplate(resourceLoader, cfg);
        System.out.println("-- init success .");
        return this ;
    }

    public void create(GetParamsListener listener) throws IOException {
        JSONArray tables = config.getJSONArray("tables") ;
        JSONArray templates = config.getJSONArray("templates") ;
        Map<String,Object> params ;
        JSONObject table ;
        JSONObject tempObj ;
        StringBuffer columnLineStr = new StringBuffer("");
        JSONObject template ;
        String text ;
        File genFile ;
        String filePath ;
        String fileName ;
        List<Record> columnList ;
        Template beetlTemplate ;
        Map<String,Object> outsideParams ;
        for(int i = 0 ,len = tables.size() ; i < len ; i++){
            params = new HashMap<String,Object>();
            table = tables.getJSONObject(i) ;
            System.out.println(String.format("-- start generate %s files",table.getString("tableName")));
            params.put("author",config.getString("author")) ;
            params.put("now",DateTime.now().toString(config.getString("dateFormat"))) ;
            params.put("tableName",table.getString("tableName")) ;
            params.put("tableComment",table.getString("tableComment")) ;
            params.put("tablePre",table.getString("tablePre")) ;
            params.putAll(listener.addParamsAboutTableInfo(table.getString("tableName"),table.getString("tablePre"),table.getString("tableComment")));
            columnList = Db.find(String.format("show full columns from %s",table.getString("tableName")));
            for(Record record : columnList){
               Map<String, Object> columnParams = listener.addParamsAboutColumn(
                       record.getStr("Field"),record.getStr("Type"),record.getStr("Key"),
                       record.getStr("Default"),record.getStr("Comment")
               ) ;
                record.setColumns(columnParams);
            }
            params.put("columnList",columnList);
            params.putAll(listener.addParamsAboutOthers());
            for(int j = 0 , size = templates.size() ; j < size ; j++){
                tempObj = templates.getJSONObject(j) ;
                template = new JSONObject() ;
                for(String key : tempObj.keySet()){
                    if(tempObj.get(key) instanceof JSONArray){
                        template.put(key,tempObj.getJSONArray(key).get(i)) ;
                    }else{
                        template.put(key,tempObj.get(key)) ;
                    }
                }
                params.putAll(template);
                beetlTemplate = gt.getTemplate(template.getString("templateName"));
                beetlTemplate.binding(params);
                text = beetlTemplate.render() ;
                filePath = template.getString("filePath").replace("{fileName}",template.getString("fileName")) ;
                genFile = new File(filePath) ;
                FileUtils.touch(genFile);
                FileUtils.write(genFile,text,Charset.forName("utf-8"));
                System.out.println(String.format("---- %s : %s Done!",table.getString("tableName"),template.getString("templateName")));
            }
            System.out.println(String.format("-- %s Stop Deal",table.getString("tableName")));
        }
        arp.stop();
        dp.stop();
        System.out.println("-- release successfully .");
    }

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

    public static String transJdbcType2CodeType(String jdbcType){
        if(jdbcType == null || jdbcType.length() == 0){
            return "undefined" ;
        }
        if(jdbcType.indexOf("(") > 0){
            jdbcType = jdbcType.substring(0,jdbcType.indexOf("("));
        }
        if(config.getJSONObject("columnType").containsKey(jdbcType)){
            return config.getJSONObject("columnType").getString(jdbcType);
        }else{
            return "undefined" ;
        }
    }

    public static String transJdbcName2ParaName(String jdbcName){
        String[] arr = jdbcName.split("_") ;
        if(arr == null || arr.length == 0){
            return "undefined" ;
        }
        String paraName = arr[0];
        for(int i = 1 , len = arr.length ; i < len ; i++){
            paraName += upFirstLetter(arr[i]);
        }
        return paraName ;
    }

    public static String upFirstLetter(String word){
        return word.substring(0,1).toUpperCase() + word.substring(1) ;
    }
}
