package com.yaos.autogenerate;

import java.util.HashMap;

/**
 * 数据源基类
 * @AUTHOR yaos
 * @DATE 2017-02-06
 */
public interface BaseAutoGenData {

    /**
     * 对外提供供模板使用的数据
     * @return 数据集
     */
    HashMap<String,Object> getData() throws Exception;
    /**
     * 清除数据
     */
    void clear() ;
}
