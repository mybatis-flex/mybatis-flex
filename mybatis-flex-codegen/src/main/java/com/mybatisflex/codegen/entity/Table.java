/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.codegen.entity;

import com.mybatisflex.codegen.config.ControllerConfig;
import com.mybatisflex.codegen.config.EntityConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.MapperConfig;
import com.mybatisflex.codegen.config.MapperXmlConfig;
import com.mybatisflex.codegen.config.ServiceConfig;
import com.mybatisflex.codegen.config.ServiceImplConfig;
import com.mybatisflex.codegen.config.TableConfig;
import com.mybatisflex.codegen.config.TableDefConfig;
import com.mybatisflex.core.util.StringUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据库表信息。
 */
public class Table {

    /**
     * 表名。
     */
    private String name;

    /**
     * schema（模式）。
     */
    private String schema;

    /**
     * 表注释。
     */
    private String comment;

    /**
     * 主键。
     */
    private Set<String> primaryKeys;

    /**
     * 所包含的列。
     */
    private List<Column> columns = new ArrayList<>();

    /**
     * 表配置。
     */
    private TableConfig tableConfig;

    private EntityConfig entityConfig;

    /**
     * 全局配置。
     */
    private GlobalConfig globalConfig;

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        if (StringUtil.isNotBlank(comment)) {
            return globalConfig.getJavadocConfig().formatTableComment(comment);
        }
        return null;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Column getPrimaryKey() {
        // 这里默认表中一定会有字段，就不做空判断了
        return columns.stream()
            .filter(Column::isPrimaryKey)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("PrimaryKey can't be null"));
    }

    public Set<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(Set<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public void addPrimaryKey(String primaryKey) {
        if (primaryKeys == null) {
            primaryKeys = new LinkedHashSet<>();
        }
        primaryKeys.add(primaryKey);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Column> getSortedColumns() {
        ArrayList<Column> arrayList = new ArrayList<>(columns);
        // 生成字段排序
        arrayList.sort(Comparator.comparingInt((Column c) -> c.getProperty().length())
            .thenComparing(Column::getProperty));
        return arrayList;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }


    public boolean containsColumn(String columnName) {
        if (columns == null || columns.isEmpty() || StringUtil.isBlank(columnName)) {
            return false;
        }
        for (Column column : columns) {
            if (columnName.equals(column.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsColumn(String... columnNames) {
        for (String columnName : columnNames) {
            if (!containsColumn(columnName)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAnyColumn(String... columnNames) {
        for (String columnName : columnNames) {
            if (containsColumn(columnName)) {
                return true;
            }
        }
        return false;
    }


    public void addColumn(Column column) {
        //主键
        if (primaryKeys != null && primaryKeys.contains(column.getName())) {
            column.setPrimaryKey(true);
            if (column.getAutoIncrement() == null && (column.getPropertyType().equals(Integer.class.getName()) || column.getPropertyType().equals(BigInteger.class.getName()))) {
                column.setAutoIncrement(true);
            }
        }

        if (column.getAutoIncrement() == null) {
            column.setAutoIncrement(false);
        }

        column.setColumnConfig(globalConfig.getStrategyConfig().getColumnConfig(name, column.getName()));
        column.setEntityConfig(globalConfig.getEntityConfig());

        columns.add(column);
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    public TableConfig getTableConfig() {
        return tableConfig;
    }

    public void setTableConfig(TableConfig tableConfig) {
        this.tableConfig = tableConfig;
    }

    public EntityConfig getEntityConfig() {
        return entityConfig;
    }

    public void setEntityConfig(EntityConfig entityConfig) {
        this.entityConfig = entityConfig;
    }

    // ===== 构建实体类文件 =====

    /**
     * 构建 import 导包。
     */
    public List<String> buildImports(boolean isBase) {
        Set<String> imports = new HashSet<>();

        //base 类不需要添加 Table 的导入，没有 @Table 注解
        if (!isBase) {
            imports.add("com.mybatisflex.annotation.Table");
        }

        EntityConfig entityConfig = globalConfig.getEntityConfig();

        //未开启基类生成，或者是基类的情况下，添加 Column 类型的导入
        if (!entityConfig.isWithBaseClassEnable() || (entityConfig.isWithBaseClassEnable() && isBase)) {
            for (Column column : columns) {
                imports.addAll(column.getImportClasses());
            }

            Class<?> superClass = entityConfig.getSuperClass(this);
            if (superClass != null) {
                imports.add(superClass.getName());
            }

            if (entityConfig.getImplInterfaces() != null) {
                for (Class<?> entityInterface : entityConfig.getImplInterfaces()) {
                    imports.add(entityInterface.getName());
                }
            }
        }


        if (!entityConfig.isWithBaseClassEnable() || (entityConfig.isWithBaseClassEnable() && !isBase)) {
            if (tableConfig != null) {
                if (tableConfig.getInsertListenerClass() != null) {
                    imports.add(tableConfig.getInsertListenerClass().getName());
                }
                if (tableConfig.getUpdateListenerClass() != null) {
                    imports.add(tableConfig.getUpdateListenerClass().getName());
                }
                if (tableConfig.getSetListenerClass() != null) {
                    imports.add(tableConfig.getSetListenerClass().getName());
                }
            }
        }

        return imports.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    /**
     * 构建 @Table(...) 注解。
     */
    public String buildTableAnnotation() {
        StringBuilder tableAnnotation = new StringBuilder();

        tableAnnotation.append("@Table(value = \"").append(name).append("\"");

        String globalSchema;

        if (tableConfig == null) {
            // 未配置 tableConfig 以策略中的 schema 为主
            globalSchema = schema;
        } else if (StringUtil.isBlank(tableConfig.getSchema())) {
            // 配置 tableConfig 但未指定 schema 还是以策略中的 schema 为主
            globalSchema = schema;
        } else {
            // 以用户设置的 tableConfig 中的 schema 为主
            globalSchema = null;
        }

        if (StringUtil.isNotBlank(globalSchema)) {
            tableAnnotation.append(", schema = \"").append(globalSchema).append("\"");
        }

        // 添加 dataSource 配置，因为代码生成器是一个数据源生成的，所以这些实体类应该都是一个数据源。
        String dataSource = globalConfig.getEntityDataSource();
        if (StringUtil.isNotBlank(dataSource)) {
            tableAnnotation.append(", dataSource = \"").append(dataSource).append("\"");
        }


        if (tableConfig != null) {
            if (StringUtil.isNotBlank(tableConfig.getSchema())) {
                tableAnnotation.append(", schema = \"").append(tableConfig.getSchema()).append("\"");
            }
            if (tableConfig.getCamelToUnderline() != null) {
                tableAnnotation.append(", camelToUnderline = ").append(tableConfig.getCamelToUnderline());
            }
            if (tableConfig.getInsertListenerClass() != null) {
                tableAnnotation.append(", onInsert = ").append(tableConfig.getInsertListenerClass().getSimpleName()).append(".class");
            }
            if (tableConfig.getUpdateListenerClass() != null) {
                tableAnnotation.append(", onUpdate = ").append(tableConfig.getUpdateListenerClass().getSimpleName()).append(".class");
            }
            if (tableConfig.getSetListenerClass() != null) {
                tableAnnotation.append(", onSet = ").append(tableConfig.getSetListenerClass().getSimpleName()).append(".class");
            }
            if (Boolean.FALSE.equals(tableConfig.getMapperGenerateEnable())) {
                tableAnnotation.append(", mapperGenerateEnable = false");
            }
        }


        if (entityConfig != null && entityConfig.isColumnCommentEnable() && StringUtil.isNotBlank(comment)) {
            tableAnnotation.append(", comment = \"")
                .append(this.comment.replace("\n", "").replace("\"", "\\\"").trim())
                .append("\"");
        }

        // @Table(value = "sys_user") -> @Table("sys_user")
        int index = tableAnnotation.indexOf(",");
        if (index == -1) {
            int start = tableAnnotation.indexOf("value");
            if (start != -1) {
                tableAnnotation.delete(start, start + 8);
            }
        }

        return tableAnnotation.append(")\n").toString();
    }

    /**
     * 构建 extends 继承。
     */
    public String buildExtends() {
        EntityConfig entityConfig = globalConfig.getEntityConfig();
        Class<?> superClass = entityConfig.getSuperClass(this);
        if (superClass != null) {
            return " extends " + superClass.getSimpleName();
        } else {
            return "";
        }
    }

    /**
     * 构建 implements 实现。
     */
    public String buildImplements() {
        Class<?>[] entityInterfaces = globalConfig.getEntityConfig().getImplInterfaces();
        if (entityInterfaces != null && entityInterfaces.length > 0) {
            return " implements " + StringUtil.join(", ", Arrays.stream(entityInterfaces)
                .map(Class::getSimpleName).collect(Collectors.toList()));
        } else {
            return "";
        }
    }

    // ===== 构建相关类名 =====

    /**
     * 获取生成 Java 文件名。
     */
    public String getEntityJavaFileName() {
        String entityJavaFileName = name;
        String tablePrefix = globalConfig.getStrategyConfig().getTablePrefix();
        if (tablePrefix != null) {
            String[] tablePrefixes = tablePrefix.split(",");
            for (String prefix : tablePrefixes) {
                String trimPrefix = prefix.trim();
                if (trimPrefix.length() > 0 && name.startsWith(trimPrefix)) {
                    entityJavaFileName = name.substring(trimPrefix.length());
                    break;
                }
            }
        }
        return StringUtil.firstCharToUpperCase(StringUtil.underlineToCamel(entityJavaFileName));
    }

    /**
     * 构建 entity 的 Class 名称。
     */
    public String buildEntityClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        EntityConfig entityConfig = globalConfig.getEntityConfig();
        return entityConfig.getClassPrefix()
            + entityJavaFileName
            + entityConfig.getClassSuffix();
    }

    /**
     * 构建 tableDef 的 Class 名称。
     */
    public String buildTableDefClassName() {
        String tableDefJavaFileName = getEntityJavaFileName();
        TableDefConfig tableDefConfig = globalConfig.getTableDefConfig();
        return tableDefConfig.getClassPrefix()
            + tableDefJavaFileName
            + tableDefConfig.getClassSuffix();
    }

    /**
     * 构建 mapper 的 Class 名称。
     */
    public String buildMapperClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        MapperConfig mapperConfig = globalConfig.getMapperConfig();
        return mapperConfig.getClassPrefix()
            + entityJavaFileName
            + mapperConfig.getClassSuffix();
    }

    /**
     * 构建 service 的 Class 名称。
     */
    public String buildServiceClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        ServiceConfig serviceConfig = globalConfig.getServiceConfig();
        return serviceConfig.getClassPrefix()
            + entityJavaFileName
            + serviceConfig.getClassSuffix();
    }

    /**
     * 构建 serviceImpl 的 Class 名称。
     */
    public String buildServiceImplClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        ServiceImplConfig serviceImplConfig = globalConfig.getServiceImplConfig();
        return serviceImplConfig.getClassPrefix()
            + entityJavaFileName
            + serviceImplConfig.getClassSuffix();
    }

    /**
     * 构建 controller 的 Class 名称。
     */
    public String buildControllerClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        ControllerConfig controllerConfig = globalConfig.getControllerConfig();
        return controllerConfig.getClassPrefix()
            + entityJavaFileName
            + controllerConfig.getClassSuffix();
    }

    /**
     * 构建访问路径的前缀
     */
    public String buildControllerRequestMappingPrefix() {
        String mappingPrefix = globalConfig.getControllerConfig().getRequestMappingPrefix();
        return mappingPrefix == null ? "" : mappingPrefix.trim();
    }

    /**
     * 构建 MapperXml 的文件名称。
     */
    public String buildMapperXmlFileName() {
        String tableDefJavaFileName = getEntityJavaFileName();
        MapperXmlConfig mapperXmlConfig = globalConfig.getMapperXmlConfig();
        return mapperXmlConfig.getFilePrefix()
            + tableDefJavaFileName
            + mapperXmlConfig.getFileSuffix();
    }

    @Override
    public String toString() {
        return "Table{" +
            "schema'" + schema + '\'' +
            "name='" + name + '\'' +
            ", remarks='" + comment + '\'' +
            ", primaryKeys='" + primaryKeys + '\'' +
            ", columns=" + columns +
            '}';
    }

}
