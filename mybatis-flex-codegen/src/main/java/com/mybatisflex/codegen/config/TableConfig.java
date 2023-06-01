/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.codegen.config;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;

import java.util.HashMap;
import java.util.Map;

public class TableConfig {

    private String tableName;

    /**
     * 数据库的 schema（模式）
     */
    private String schema;

    /**
     * 默认为 驼峰属性 转换为 下划线字段
     */
    private Boolean camelToUnderline;


    private Class<? extends InsertListener> insertListenerClass;


    private Class<? extends UpdateListener> updateListenerClass;


    private Class<? extends SetListener> setListenerClass;


    private Map<String, ColumnConfig> columnConfigMap;

    private Boolean mapperGenerateEnable = Boolean.TRUE;


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Boolean getCamelToUnderline() {
        return camelToUnderline;
    }

    public void setCamelToUnderline(Boolean camelToUnderline) {
        this.camelToUnderline = camelToUnderline;
    }

    public Class<? extends InsertListener> getInsertListenerClass() {
        return insertListenerClass;
    }

    public void setInsertListenerClass(Class<? extends InsertListener> insertListenerClass) {
        this.insertListenerClass = insertListenerClass;
    }

    public Class<? extends UpdateListener> getUpdateListenerClass() {
        return updateListenerClass;
    }

    public void setUpdateListenerClass(Class<? extends UpdateListener> updateListenerClass) {
        this.updateListenerClass = updateListenerClass;
    }

    public Class<? extends SetListener> getSetListenerClass() {
        return setListenerClass;
    }

    public void setSetListenerClass(Class<? extends SetListener> setListenerClass) {
        this.setListenerClass = setListenerClass;
    }

    public Map<String, ColumnConfig> getColumnConfigMap() {
        return columnConfigMap;
    }

    public void setColumnConfigMap(Map<String, ColumnConfig> columnConfigMap) {
        this.columnConfigMap = columnConfigMap;
    }

    public Boolean getMapperGenerateEnable() {
        return mapperGenerateEnable;
    }

    public void setMapperGenerateEnable(Boolean mapperGenerateEnable) {
        this.mapperGenerateEnable = mapperGenerateEnable;
    }

    public void addColumnConfig(ColumnConfig columnConfig) {
        if (columnConfigMap == null) {
            columnConfigMap = new HashMap<>();
        }
        columnConfigMap.put(columnConfig.getColumnName(), columnConfig);
    }

    public ColumnConfig getColumnConfig(String columnName) {
        return columnConfigMap == null ? null : columnConfigMap.get(columnName);
    }


}
