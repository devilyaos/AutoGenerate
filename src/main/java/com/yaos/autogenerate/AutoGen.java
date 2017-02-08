package com.yaos.autogenerate;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.JMap;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;
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

/**
 * @AUTHOR yaos
 * @DATE 2017-02-06
 */
public class AutoGen {

    private static AutoGen instance = new AutoGen();
    private JSONObject config ;
    private DruidPlugin dp ;
    private ActiveRecordPlugin arp ;
    private FileResourceLoader resourceLoader ;
    private Configuration cfg ;
    private GroupTemplate gt ;

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
    public AutoGen init() throws IOException {
        init(PathKit.getRootClassPath() + "/defaultRule.json");
        return this ;
    }

    /**
     * 初始化
     * @param configPath 配置路径
     * @return 链式调用
     */
    public AutoGen init(String configPath) throws IOException {
        if(configPath == null || configPath.length() == 0){
            configPath = PathKit.getRootClassPath() + "/defaultRule.json" ;
        }
        config = getConfig(configPath) ;
        if(config == null){
            throw new NullPointerException("找不到配置");
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
        System.out.println("-- 初始化成功");
        return this ;
    }

    /**
     * 创建文件的服务
     */
    public void create() throws IOException {
        JSONArray tables = config.getJSONArray("tables") ;
        JSONArray templates = config.getJSONArray("templates") ;
        Map<String,Object> params ;
        JSONObject table ;
        StringBuffer columnLineStr = new StringBuffer("");
        JSONObject template ;
        String text ;
        File genFile ;
        String filePath ;
        List<Record> columnList ;
        Template beetlTemplate ;
        Map<String,Object> templateParams ;
        //循环表
        for(int i = 0 ,len = tables.size() ; i < len ; i++){
            params = new HashMap<String,Object>();
            table = tables.getJSONObject(i) ;
            System.out.println(String.format("-- 开始生成 %s 相关文件",table.getString("tableName")));
            //设置全局变量
            params.put("author",config.getString("author")) ;
            params.put("now",DateTime.now().toString(config.getString("dateFormat"))) ;
            //设置表信息
            params.put("tableName",table.getString("tableName")) ;
            params.put("tableComment",table.getString("tableComment")) ;
            params.put("tableMethodName",upFirstLetter(transJdbcName2ParaName(
                    table.getString("tableName").replace(table.getString("tablePre"),"")
            )));
            //设置列信息
            columnList = Db.find(String.format("show full columns from %s",table.getString("tableName")));
            for(Record record : columnList){
                record.set("ColumnName",record.getStr("Field"));
                record.set("CodeType",transJdbcType2CodeType(record.getStr("Type"))) ;
                record.set("CodeParamName",transJdbcName2ParaName(record.getStr("Field").replace("is_","")));
                record.set("CodeMethodName",upFirstLetter(transJdbcName2ParaName(record.getStr("Field").replace("is_",""))));
                columnLineStr.append(record.getStr("CodeType")).append(" ").append(record.getStr("CodeParamName")).append(",") ;
            }
            params.put("columnList",columnList) ;
            params.put("columnLine",columnLineStr.length() > 0 ?
                    columnLineStr.substring(0,columnLineStr.lastIndexOf(",")) : "") ;
            //循环模板
            for(int j = 0 , size = templates.size() ; j < size ; j++){
                template = templates.getJSONObject(j) ;
//                params.put("package",template.getString("package")) ;
//                params.put("comment",template.getString("comment")) ;
                params.putAll(template);
                beetlTemplate = gt.getTemplate(template.getString("templateName"));
                beetlTemplate.binding(params);
                text = beetlTemplate.render() ;
//                text = Engine.use()
//                        .setBaseTemplatePath(config.getString("baseTemplatePath"))
//                        .getTemplate(template.getString("templateName")).renderToString(params);
                filePath = template.getString("filePath").replace("{name}",params.get("tableMethodName").toString()) ;
                genFile = new File(filePath) ;
                FileUtils.touch(genFile);
                FileUtils.write(genFile,text,Charset.forName("utf-8"));
                System.out.println(String.format("---- %s : %s Done!",table.getString("tableName"),template.getString("templateName")));
            }
            System.out.println(String.format("-- %s 处理结束",table.getString("tableName")));
        }
        arp.stop();
        dp.stop();
        System.out.println("-- 成功释放");
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
     * 将数据库类型转换成代码类型
     * @param jdbcType 数据库类型
     * @return 代码类型
     */
    private String transJdbcType2CodeType(String jdbcType){
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
