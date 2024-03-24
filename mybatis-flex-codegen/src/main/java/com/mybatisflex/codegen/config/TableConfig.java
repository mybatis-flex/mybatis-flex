/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.codegen.config;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 表的单独设置。
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TableConfig implements Serializable {

    public static final String ALL_TABLES = "*";
    private static final long serialVersionUID = -2568968178699265858L;

    /**
     * 数据库的 schema（模式）。
     */
    private String schema;

    /**
     * 表名。
     */
    private String tableName = ALL_TABLES;

    /**
     * 默认为 驼峰属性 转换为 下划线字段。
     */
    private Boolean camelToUnderline;

    /**
     * 监听 entity 的 insert 行为。
     */
    private Class<? extends InsertListener> insertListenerClass;

    /**
     * 监听 entity 的 update 行为。
     */
    private Class<? extends UpdateListener> updateListenerClass;

    /**
     * 监听 entity 的查询数据的 set 行为。
     */
    private Class<? extends SetListener> setListenerClass;

    /**
     * 是否开启 Mapper 生成。
     */
    private Boolean mapperGenerateEnable = Boolean.TRUE;

    /**
     * 对应列的配置。
     */
    private Map<String, ColumnConfig> columnConfigMap;


    public static TableConfig create() {
        return new TableConfig();
    }

    public String getSchema() {
        return this.schema;
    }

    public TableConfig setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public String getTableName() {
        return this.tableName;
    }

    public TableConfig setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Boolean getCamelToUnderline() {
        return this.camelToUnderline;
    }

    public TableConfig setCamelToUnderline(Boolean camelToUnderline) {
        this.camelToUnderline = camelToUnderline;
        return this;
    }

    public Class<? extends InsertListener> getInsertListenerClass() {
        return this.insertListenerClass;
    }

    public TableConfig setInsertListenerClass(Class<? extends InsertListener> insertListenerClass) {
        this.insertListenerClass = insertListenerClass;
        return this;
    }

    public Class<? extends UpdateListener> getUpdateListenerClass() {
        return this.updateListenerClass;
    }

    public TableConfig setUpdateListenerClass(Class<? extends UpdateListener> updateListenerClass) {
        this.updateListenerClass = updateListenerClass;
        return this;
    }

    public Class<? extends SetListener> getSetListenerClass() {
        return this.setListenerClass;
    }

    public TableConfig setSetListenerClass(Class<? extends SetListener> setListenerClass) {
        this.setListenerClass = setListenerClass;
        return this;
    }

    public Boolean getMapperGenerateEnable() {
        return this.mapperGenerateEnable;
    }

    public TableConfig setMapperGenerateEnable(Boolean mapperGenerateEnable) {
        this.mapperGenerateEnable = mapperGenerateEnable;
        return this;
    }

    public Map<String, ColumnConfig> getColumnConfigMap() {
        return this.columnConfigMap;
    }

    public TableConfig setColumnConfigMap(Map<String, ColumnConfig> columnConfigMap) {
        this.columnConfigMap = columnConfigMap;
        return this;
    }

    public TableConfig setColumnConfig(ColumnConfig columnConfig) {
        if (this.columnConfigMap == null) {
            this.columnConfigMap = new HashMap<>();
        }
        this.columnConfigMap.put(columnConfig.getColumnName(), columnConfig);
        return this;
    }

    protected ColumnConfig getColumnConfig(String columnName) {
        return this.columnConfigMap == null ? null : this.columnConfigMap.get(columnName);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final TableConfig tableConfig;

        private Builder() {
            this.tableConfig = new TableConfig();
        }

        public Builder schema(String schema) {
            this.tableConfig.setSchema(schema);
            return this;
        }

        public Builder tableName(String tableName) {
            this.tableConfig.setTableName(tableName);
            return this;
        }

        public Builder camelToUnderline(Boolean camelToUnderline) {
            this.tableConfig.setCamelToUnderline(camelToUnderline);
            return this;
        }

        public Builder insertListenerClass(Class<? extends InsertListener> insertListenerClass) {
            this.tableConfig.setInsertListenerClass(insertListenerClass);
            return this;
        }

        public Builder updateListenerClass(Class<? extends UpdateListener> updateListenerClass) {
            this.tableConfig.setUpdateListenerClass(updateListenerClass);
            return this;
        }

        public Builder setListenerClass(Class<? extends SetListener> setListenerClass) {
            this.tableConfig.setSetListenerClass(setListenerClass);
            return this;
        }

        public Builder mapperGenerateEnable(Boolean mapperGenerateEnable) {
            this.tableConfig.setMapperGenerateEnable(mapperGenerateEnable);
            return this;
        }

        public Builder columnConfig(ColumnConfig columnConfigMap) {
            this.tableConfig.setColumnConfig(columnConfigMap);
            return this;
        }

        public TableConfig build() {
            return this.tableConfig;
        }

    }

}
