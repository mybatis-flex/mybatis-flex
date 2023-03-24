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

import java.util.HashMap;
import java.util.Map;

public class TableConfig {

    private String tableName;

    /**
     * 数据库的 schema
     */
    private String schema;

    /**
     * 默认为 驼峰属性 转换为 下划线字段
     */
    private Boolean camelToUnderline;


    private Map<String,ColumnConfig> columnConfigMap;


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

    public Map<String, ColumnConfig> getColumnConfigMap() {
        return columnConfigMap;
    }

    public void setColumnConfigMap(Map<String, ColumnConfig> columnConfigMap) {
        this.columnConfigMap = columnConfigMap;
    }

    public void addColumnConfig(ColumnConfig columnConfig){
        if (columnConfigMap == null){
            columnConfigMap = new HashMap<>();
        }
        columnConfigMap.put(columnConfig.getColumnName(), columnConfig);
    }

    public ColumnConfig getColumnConfig(String columnName){
        return columnConfigMap == null ? null: columnConfigMap.get(columnName);
    }
}
