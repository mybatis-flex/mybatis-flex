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

import com.mybatisflex.core.util.StringUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 表策略配置。
 *
 * @author 王帅
 * @since 2023-05-14
 */
@SuppressWarnings("unused")
public class StrategyConfig {

    /**
     * 数据库表前缀，多个前缀用英文逗号（,） 隔开。
     */
    private String tablePrefix;

    /**
     * 逻辑删除的默认字段名称。
     */
    private String logicDeleteColumn;

    /**
     * 乐观锁的字段名称。
     */
    private String versionColumn;

    /**
     * 是否生成视图映射。
     */
    private boolean generateForView;

    /**
     * 单独为某张表添加独立的配置。
     */
    private Map<String, TableConfig> tableConfigMap;

    /**
     * 设置某个列的全局配置。
     */
    private Map<String, ColumnConfig> columnConfigMap;

    /**
     * 需要生成的表在哪个模式下
     */
    private String generateSchema;
    /**
     * 生成哪些表，白名单。
     */
    private Set<String> generateTables;

    /**
     * 不生成哪些表，黑名单。
     */
    private Set<String> unGenerateTables;


    /**
     * 设置要生成的模式
     *
     * @return
     */
    public String getGenerateSchema() {
        return generateSchema;
    }

    /**
     * 获取要生成的模式
     *
     * @param generateSchema
     * @return
     */
    public StrategyConfig setGenerateSchema(String generateSchema) {
        this.generateSchema = generateSchema;
        return this;
    }

    /**
     * 获取表配置。
     */
    public TableConfig getTableConfig(String tableName) {
        return tableConfigMap == null ? null : tableConfigMap.get(tableName);
    }

    /**
     * 设置表配置。
     */
    public void setTableConfig(TableConfig tableConfig) {
        if (tableConfigMap == null) {
            tableConfigMap = new HashMap<>();
        }
        tableConfigMap.put(tableConfig.getTableName(), tableConfig);
    }

    /**
     * 获取列配置。
     */
    public ColumnConfig getColumnConfig(String tableName, String columnName) {
        ColumnConfig columnConfig = null;

        TableConfig tableConfig = getTableConfig(tableName);
        if (tableConfig != null) {
            columnConfig = tableConfig.getColumnConfig(columnName);
        }

        if (columnConfig == null && columnConfigMap != null) {
            columnConfig = columnConfigMap.get(columnName);
        }

        if (columnConfig == null) {
            columnConfig = new ColumnConfig();
        }

        //全局配置的逻辑删除
        if (columnName.equals(logicDeleteColumn) && columnConfig.getLogicDelete() == null) {
            columnConfig.setLogicDelete(true);
        }

        //全部配置的乐观锁版本
        if (columnName.equals(versionColumn) && columnConfig.getVersion() == null) {
            columnConfig.setVersion(true);
        }


        return columnConfig;
    }

    /**
     * 设置列配置。
     */
    public void setColumnConfig(ColumnConfig columnConfig) {
        if (columnConfigMap == null) {
            columnConfigMap = new HashMap<>();
        }
        columnConfigMap.put(columnConfig.getColumnName(), columnConfig);
    }

    /**
     * 设置列配置。
     */
    public void setColumnConfig(String tableName, ColumnConfig columnConfig) {
        TableConfig tableConfig = getTableConfig(tableName);
        if (tableConfig == null) {
            tableConfig = new TableConfig();
            tableConfig.setTableName(tableName);
            setTableConfig(tableConfig);
        }

        tableConfig.addColumnConfig(columnConfig);
    }

    /**
     * 设置生成哪些表。
     */
    public void setGenerateTable(String... tables) {
        if (generateTables == null) {
            generateTables = new HashSet<>();
        }

        for (String table : tables) {
            if (table != null && table.trim().length() > 0) {
                generateTables.add(table.trim());
            }
        }
    }

    /**
     * 设置不生成哪些表。
     */
    public void setUnGenerateTable(String... tables) {
        if (unGenerateTables == null) {
            unGenerateTables = new HashSet<>();
        }

        for (String table : tables) {
            if (table != null && table.trim().length() > 0) {
                unGenerateTables.add(table.trim());
            }
        }
    }

    public boolean isSupportGenerate(String table) {
        if (unGenerateTables != null && unGenerateTables.contains(table)) {
            return false;
        }

        //不配置指定比表名的情况下，支持所有表
        if (generateTables == null || generateTables.isEmpty()) {
            return true;
        }

        for (String generateTable : generateTables) {
            if (generateTable.equals(table)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取表前缀。
     */
    public String getTablePrefix() {
        return tablePrefix;
    }

    /**
     * 设置表前缀。
     */
    public StrategyConfig setTablePrefix(String... tablePrefix) {
        this.tablePrefix = StringUtil.join(",", tablePrefix);
        return this;
    }

    /**
     * 获取逻辑删除列。
     */
    public String getLogicDeleteColumn() {
        return logicDeleteColumn;
    }

    /**
     * 设置逻辑删除列。
     */
    public StrategyConfig setLogicDeleteColumn(String logicDeleteColumn) {
        this.logicDeleteColumn = logicDeleteColumn;
        return this;
    }

    /**
     * 获取版本列。
     */
    public String getVersionColumn() {
        return versionColumn;
    }

    /**
     * 设置版本列。
     */
    public StrategyConfig setVersionColumn(String versionColumn) {
        this.versionColumn = versionColumn;
        return this;
    }

    /**
     * 是否生成视图。
     */
    public boolean isGenerateForView() {
        return generateForView;
    }

    /**
     * 设置是否生成视图。
     */
    public StrategyConfig setGenerateForView(boolean generateForView) {
        this.generateForView = generateForView;
        return this;
    }

    /**
     * 获取表配置。
     */
    public Map<String, TableConfig> getTableConfigMap() {
        return tableConfigMap;
    }

    /**
     * 设置表配置。
     */
    public StrategyConfig setTableConfigMap(Map<String, TableConfig> tableConfigMap) {
        this.tableConfigMap = tableConfigMap;
        return this;
    }

    /**
     * 获取列配置。
     */
    public Map<String, ColumnConfig> getColumnConfigMap() {
        return columnConfigMap;
    }

    /**
     * 设置列配置。
     */
    public StrategyConfig setColumnConfigMap(Map<String, ColumnConfig> columnConfigMap) {
        this.columnConfigMap = columnConfigMap;
        return this;
    }

    /**
     * 获取生成哪些表。
     */
    public Set<String> getGenerateTables() {
        return generateTables;
    }

    /**
     * 设置生成哪些表。
     */
    public StrategyConfig setGenerateTables(Set<String> generateTables) {
        this.generateTables = generateTables;
        return this;
    }

    /**
     * 获取不生成哪些表。
     */
    public Set<String> getUnGenerateTables() {
        return unGenerateTables;
    }

    /**
     * 设置不生成哪些表。
     */
    public StrategyConfig setUnGenerateTables(Set<String> unGenerateTables) {
        this.unGenerateTables = unGenerateTables;
        return this;
    }

}