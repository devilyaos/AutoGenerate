#AutoGenerate
一款简单的代码生成器，通过json进行生成配置,基于JFinal 3.0 ，jdk 1.7+，更低版本未测试，理论上未使用高版本的api。
###当前版本
V0.2
###调用方法
```java
AutoGen.use().init("配置文件绝对路径").create(new GetParamsListener() {
              @Override
              public Map<String, Object> addParamsAboutTableInfo(String tableName, String tablePre, String tableComment) {
                //返回table相关的参数，例如将tableName转换为类名，参数名格式
              }

              @Override
              public Map<String, Object> addParamsAboutColumn(String Field, String Type, String Key, String Default, String Comment) {
                //返回column相关的参数，例如将Field转换为方法中使用的名称，或是按照自己的规则转换为符合业务的名称
              }

              @Override
              public Map<String, Object> addParamsAboutOthers() {
                  //一些需要的其他的全局参数，将会作用于所有模板
              }
          });
```
> 未避免不必要的错误，不提供默认配置，请指定读取路径

###功能描述
#####配置文件
```json
{
  "author":"作者姓名",
  "dateFormat":"yyyy-MM-dd",
  "baseTemplatePath":"模板目录绝对路径",
  "dataSource":{
    "url":"数据库地址",
    "user":"数据库用户名",
    "pwd":"数据库密码"
  },
  "tables":[
    {"tableName":"表名n","tablePre":"表前缀n","tableComment":"表注释n"}
  ],
  "columnType":{
    "char":"String",
    "varchar":"String",
    "text":"String",
    "blob":"String",
    "bigint":"Long",
    "smallint":"Integer",
    "tinyint":"Integer"
  },
  "templates":[
    {
      "templateName":"模板名",
      "filePath":"生成文件名称，文件名用{fileName}指定，如{fileName}.java",
      "fileName":"文件名，需要与代码中的类名相符，故统一调参",
      "param1":"自定义参数一，参数名可自定义",
      "param2":"自定义参数二，参数名可自定义",
      "param_n":"自定义参数n，参数名可自定义"
    }
  ]
}
```
#####默认会有的模板参数
* author `作者姓名，config中配置`
* now `当前时间，config中配置格式`
* tableName `表名`
* tableComment `表注释`
* tablePre `表前缀`
* columnList `字段列表，其中每个字段的基础值为show full columns from table中返回的值`
#####模板文件
目前使用的模板引擎是beetl，所以所有的模板语法就是beetl语法，模板后缀名可随意指定，暂时不支持传入自定义变量，当然下一个
版本是会加入的。

#####Beetl地址：[http://ibeetl.com/](http://ibeetl.com/)
#####JFinal地址：[http://www.jfinal.com/](http://www.jfinal.com/)

###开源协议
目前采用GPL协议开源，请谨慎选择，欢迎学习交流。