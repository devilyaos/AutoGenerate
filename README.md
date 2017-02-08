#AutoGenerate
一款简单的代码生成器，通过json进行生成配置,基于JFinal 3.0 ，jdk 1.7+，更低版本未测试，理论上未使用高版本的api。
###当前版本
V0.1
###调用方法
```java
AutoGen.use().init("配置文件绝对路径").create();
```
> 未避免不必要的错误，不提供默认配置，请指定读取路径

###功能描述
#####配置文件
```json
{
  "author":"你的名字",
  "dateFormat":"yyyy-MM-dd",
  "baseTemplatePath":"模板所在目录的绝对路径",
  "dataSource":{
    "url":"数据库连接",
    "user":"数据库用户名",
    "pwd":"密码"
  },
  "tables":[
    {"tableName":"表1","tablePre":"表前缀1","tableComment":"表注释1"},
    {"tableName":"表2","tablePre":"表前缀2","tableComment":"表注释2"}
  ],
  "columnType":{
    "char":"String",//数据库中的char转换为String，下同，仅举例
    "varchar":"String",
    "text":"String",
    "blob":"String",
    "bigint":"Long",
    "smallint":"Integer",
    "tinyint":"Integer"
  },
  "templates":[
    {
      "templateName":"模板名称1",
      "filePath":"模板路径1",//留了个小彩蛋，文件名可以用类似{name}Controller.java的方式指定，会用首字母大写的驼峰式的表名代替
      "package":"模板的包名1",
      "comment":"模板文件需要的注释1"
    },
    {
      "templateName":"模板名称2",
      "filePath":"模板路径2",
      "package":"模板的包名2",
      "comment":"模板文件需要的注释2"
    }
  ]
}
```
#####模板文件
目前使用的模板引擎是beetl，所以所有的模板语法就是beetl语法，模板后缀名可随意指定，暂时不支持传入自定义变量，当然下一个
版本是会加入的。

#####Beetl地址：[http://ibeetl.com/](http://ibeetl.com/)
#####JFinal地址：[http://www.jfinal.com/](http://www.jfinal.com/)

###开源协议
目前采用GPL协议开源，请谨慎选择，欢迎学习交流。