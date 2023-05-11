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

import com.mybatisflex.codegen.template.EnjoyTemplate;
import com.mybatisflex.codegen.template.ITemplate;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.util.StringUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlobalConfig {

    //代码生成目录
    private String sourceDir;

    //entity 的包名
    private String entityPackage;

    //entity 类的前缀
    private String entityClassPrefix;

    //entity 类的后缀
    private String entityClassSuffix;

    //entity 类的父类，可以自定义一些 BaseEntity 类
    private Class<?> entitySupperClass;

    //entity 默认实现的接口
    private Class<?>[] entityInterfaces = {Serializable.class};

    //entity 是否使用 Lombok
    private boolean entityWithLombok = false;

    private boolean tableDefGenerateEnable = false;

    //tableDef 的包名
    private String tableDefPackage;

    //tableDef 类的前缀
    private String tableDefClassPrefix;

    //tableDef 类的后缀
    private String tableDefClassSuffix;

    //是否生成 mapper 类
    private boolean mapperGenerateEnable = false;

    //是否覆盖已经存在的 mapper
    private boolean mapperOverwriteEnable = false;

    //mapper 类的前缀
    private String mapperClassPrefix;

    //mapper 类的后缀
    private String mapperClassSuffix = "Mapper";

    //mapper 的包名
    private String mapperPackage;

    //自定义 mapper 的父类
    private Class<?> mapperSupperClass = BaseMapper.class;

    //数据库表前缀，多个前缀用英文逗号（,） 隔开
    private String tablePrefix;

    //逻辑删除的默认字段名称
    private String logicDeleteColumn;

    //乐观锁的字段名称
    private String versionColumn;

    //是否生成视图映射
    private boolean generateForView = false;

    //单独为某张表添加独立的配置
    private Map<String, TableConfig> tableConfigMap;

    //设置某个列的全局配置
    private Map<String, ColumnConfig> defaultColumnConfigMap;

    //生成那些表，白名单
    private Set<String> generateTables;

    //不生成那些表，黑名单
    private Set<String> unGenerateTables;

    //使用哪个模板引擎来生成代码
    protected ITemplate templateEngine;

    //其他自定义配置
    private Map<String, Object> others;


    public String getSourceDir() {
        if (sourceDir == null || sourceDir.trim().length() == 0) {
            return System.getProperty("user.dir") + "/src/main/java";
        }
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = StringUtil.trimOrNull(sourceDir);
    }

    public String getEntityPackage() {
        if (StringUtil.isBlank(entityPackage)) {
            throw new IllegalStateException("entityPackage can not be null or blank in GlobalConfig.");
        }
        return entityPackage;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = StringUtil.trimOrNull(entityPackage);
    }

    public String getEntityClassPrefix() {
        if (StringUtil.isBlank(entityClassPrefix)) {
            return "";
        }
        return entityClassPrefix;
    }

    public void setEntityClassPrefix(String entityClassPrefix) {
        this.entityClassPrefix = StringUtil.trimOrNull(entityClassPrefix);
    }

    public String getEntityClassSuffix() {
        if (StringUtil.isBlank(entityClassSuffix)) {
            return "";
        }
        return entityClassSuffix;
    }

    public void setEntityClassSuffix(String entityClassSuffix) {
        this.entityClassSuffix = StringUtil.trimOrNull(entityClassSuffix);
    }

    public Class<?> getEntitySupperClass() {
        return entitySupperClass;
    }

    public void setEntitySupperClass(Class<?> entitySupperClass) {
        this.entitySupperClass = entitySupperClass;
    }

    public Class<?>[] getEntityInterfaces() {
        return entityInterfaces;
    }

    public void setEntityInterfaces(Class<?>[] entityInterfaces) {
        this.entityInterfaces = entityInterfaces;
    }

    public boolean isEntityWithLombok() {
        return entityWithLombok;
    }

    public void setEntityWithLombok(boolean entityWithLombok) {
        this.entityWithLombok = entityWithLombok;
    }

    public boolean isTableDefGenerateEnable() {
        return tableDefGenerateEnable;
    }

    public void setTableDefGenerateEnable(boolean tableDefGenerateEnable) {
        this.tableDefGenerateEnable = tableDefGenerateEnable;
    }

    public String getTableDefPackage() {
        if (StringUtil.isBlank(tableDefPackage) && StringUtil.isNotBlank(entityPackage)) {
            return entityPackage + ".tables";
        }
        return tableDefPackage;
    }

    public void setTableDefPackage(String tableDefPackage) {
        this.tableDefPackage = StringUtil.trimOrNull(tableDefPackage);
    }

    public String getTableDefClassPrefix() {
        return tableDefClassPrefix;
    }

    public void setTableDefClassPrefix(String tableDefClassPrefix) {
        this.tableDefClassPrefix = StringUtil.trimOrNull(tableDefClassPrefix);
    }

    public String getTableDefClassSuffix() {
        return tableDefClassSuffix;
    }

    public void setTableDefClassSuffix(String tableDefClassSuffix) {
        this.tableDefClassSuffix = StringUtil.trimOrNull(tableDefClassSuffix);
    }

    public boolean isMapperGenerateEnable() {
        return mapperGenerateEnable;
    }

    public void setMapperGenerateEnable(boolean mapperGenerateEnable) {
        this.mapperGenerateEnable = mapperGenerateEnable;
    }

    public boolean isMapperOverwriteEnable() {
        return mapperOverwriteEnable;
    }

    public void setMapperOverwriteEnable(boolean mapperOverwriteEnable) {
        this.mapperOverwriteEnable = mapperOverwriteEnable;
    }

    public String getMapperClassPrefix() {
        if (StringUtil.isBlank(mapperClassPrefix)) {
            return "";
        }
        return mapperClassPrefix;
    }

    public void setMapperClassPrefix(String mapperClassPrefix) {
        this.mapperClassPrefix = StringUtil.trimOrNull(mapperClassPrefix);
    }

    public String getMapperClassSuffix() {
        return mapperClassSuffix;
    }

    public void setMapperClassSuffix(String mapperClassSuffix) {
        this.mapperClassSuffix = StringUtil.trimOrNull(mapperClassSuffix);
    }

    public String getMapperPackage() {
        if (StringUtil.isBlank(mapperPackage)) {
            throw new IllegalStateException("mapperPackage can not be null or blank in GlobalConfig.");
        }
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = StringUtil.trimOrNull(mapperPackage);
    }

    public Class<?> getMapperSupperClass() {
        return mapperSupperClass;
    }

    public void setMapperSupperClass(Class<?> mapperSupperClass) {
        this.mapperSupperClass = mapperSupperClass;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = StringUtil.trimOrNull(tablePrefix);
    }

    public String getLogicDeleteColumn() {
        return logicDeleteColumn;
    }

    public void setLogicDeleteColumn(String logicDeleteColumn) {
        this.logicDeleteColumn = StringUtil.trimOrNull(logicDeleteColumn);
    }

    public String getVersionColumn() {
        return versionColumn;
    }

    public void setVersionColumn(String versionColumn) {
        this.versionColumn = StringUtil.trimOrNull(versionColumn);
    }

    public Map<String, TableConfig> getTableConfigMap() {
        return tableConfigMap;
    }

    public void setTableConfigMap(Map<String, TableConfig> tableConfigMap) {
        this.tableConfigMap = tableConfigMap;
    }

    public void addTableConfig(TableConfig tableConfig) {
        if (tableConfigMap == null) {
            tableConfigMap = new HashMap<>();
        }
        tableConfigMap.put(tableConfig.getTableName(), tableConfig);
    }

    public TableConfig getTableConfig(String tableName) {
        return tableConfigMap == null ? null : tableConfigMap.get(tableName);
    }

    public Map<String, ColumnConfig> getDefaultColumnConfigMap() {
        return defaultColumnConfigMap;
    }

    public void setDefaultColumnConfigMap(Map<String, ColumnConfig> defaultColumnConfigMap) {
        this.defaultColumnConfigMap = defaultColumnConfigMap;
    }


    public void addColumnConfig(ColumnConfig columnConfig) {
        if (defaultColumnConfigMap == null) {
            defaultColumnConfigMap = new HashMap<>();
        }
        defaultColumnConfigMap.put(columnConfig.getColumnName(), columnConfig);
    }

    public void addColumnConfig(String tableName, ColumnConfig columnConfig) {
        TableConfig tableConfig = getTableConfig(tableName);
        if (tableConfig == null) {
            tableConfig = new TableConfig();
            tableConfig.setTableName(tableName);
            addTableConfig(tableConfig);
        }

        tableConfig.addColumnConfig(columnConfig);
    }


    public ColumnConfig getColumnConfig(String tableName, String columnName) {
        ColumnConfig columnConfig = null;

        TableConfig tableConfig = getTableConfig(tableName);
        if (tableConfig != null) {
            columnConfig = tableConfig.getColumnConfig(columnName);
        }

        if (columnConfig == null && defaultColumnConfigMap != null) {
            columnConfig = defaultColumnConfigMap.get(columnName);
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

    public boolean isGenerateForView() {
        return generateForView;
    }

    public void setGenerateForView(boolean generateForView) {
        this.generateForView = generateForView;
    }

    public Set<String> getGenerateTables() {
        return generateTables;
    }

    public void setGenerateTables(Set<String> generateTables) {
        this.generateTables = generateTables;
    }

    public void addGenerateTable(String... tables) {
        if (generateTables == null) {
            generateTables = new HashSet<>();
        }

        for (String table : tables) {
            if (table != null && table.trim().length() > 0) {
                generateTables.add(table.trim());
            }
        }
    }

    public Set<String> getUnGenerateTables() {
        return unGenerateTables;
    }

    public void setUnGenerateTables(Set<String> unGenerateTables) {
        this.unGenerateTables = unGenerateTables;
    }


    public void addUnGenerateTable(String... tables) {
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

    public ITemplate getTemplateEngine() {
        if (templateEngine == null) {
            templateEngine = new EnjoyTemplate();
        }
        return templateEngine;
    }

    public void setTemplateEngine(ITemplate templateEngine) {
        this.templateEngine = templateEngine;
    }

    public Map<String, Object> getOthers() {
        return others;
    }

    public void setOthers(Map<String, Object> others) {
        this.others = others;
    }

    public void addConfig(String key, Object value) {
        if (others == null) {
            others = new HashMap<>();
        }
        others.put(key, value);
    }
}
