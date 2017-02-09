package com.yaos.autogenerate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @AUTHOR yaos
 * @DATE 2017-02-09
 */
public class Util {
    public static void main(String[] args) throws IOException {
        AutoGen.use()
                .init("E:\\workspace\\Java\\hccj-ucenter\\src\\main\\resources\\config\\code-template-config.json")
                .create(new GetParamsListener() {
                    @Override
                    public Map<String, Object> addParamsAboutTableInfo(String tableName, String tablePre, String tableComment) {
                        Map<String,Object> params = new HashMap<String,Object>() ;
                        params.put("tableParamName",AutoGen.transJdbcName2ParaName(tableName.replace(tablePre,""))) ;
                        params.put("tableMethodName",AutoGen.upFirstLetter(params.get("tableParamName").toString()));
                        return params;
                    }

                    @Override
                    public Map<String, Object> addParamsAboutColumn(String Field, String Type, String Key, String Default, String Comment) {
                        Map<String,Object> params = new HashMap<String,Object>() ;
                        params.put("CodeType",AutoGen.transJdbcType2CodeType(Type)) ;
                        params.put("CodeParamName",AutoGen.transJdbcName2ParaName(Field.replace("is_","")));
                        params.put("CodeMethodName",AutoGen.upFirstLetter(params.get("CodeParamName").toString()));
                        return params;
                    }

                    @Override
                    public Map<String, Object> addParamsAboutOthers() {
                        Map<String,Object> params = new HashMap<String,Object>() ;
                        return params;
                    }
                });
    }
}
