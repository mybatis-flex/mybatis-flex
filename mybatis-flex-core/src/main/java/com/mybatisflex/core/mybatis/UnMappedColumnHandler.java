package com.mybatisflex.core.mybatis;

import org.apache.ibatis.reflection.MetaObject;

/**
 * UnMappedColumnHandler
 * 自定义未匹配列处理
 * @author ArthurWang
 * @version 1.0
 * @date 2024/9/12 9:16
 **/
public interface UnMappedColumnHandler {

    void handleUnMappedColumn(MetaObject metaObject, String unmappedColumnName, Object value);
}
