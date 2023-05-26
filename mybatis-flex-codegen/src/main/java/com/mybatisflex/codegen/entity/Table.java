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
package com.mybatisflex.codegen.entity;

import com.mybatisflex.codegen.config.*;
import com.mybatisflex.core.util.StringUtil;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class Table {

    private String name;
    private String comment;
    private Set<String> primaryKeys;
    private List<Column> columns = new ArrayList<>();

    private GlobalConfig globalConfig;
    private TableConfig tableConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return globalConfig.getJavadocConfig().formatTableComment(comment);
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void addColumn(Column column) {

        //主键
        if (primaryKeys != null && primaryKeys.contains(column.getName())) {
            column.setPrimaryKey(true);
            if (column.getAutoIncrement() == null) {
                if (column.getPropertyType().equals(Integer.class.getName()) || column.getPropertyType().equals(BigInteger.class.getName())) {
                    column.setAutoIncrement(true);
                }
            }
        }

        if (column.getAutoIncrement() == null) {
            column.setAutoIncrement(false);
        }

        column.setColumnConfig(globalConfig.getStrategyConfig().getColumnConfig(name, column.getName()));

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

    public List<String> buildImports() {
        Set<String> imports = new HashSet<>();
//        imports.add(com.mybatisflex.annotation.Table.class.getName());
        imports.add("com.mybatisflex.annotation.Table");
        for (Column column : columns) {
            imports.addAll(column.getImportClasses());
        }

        EntityConfig entityConfig = globalConfig.getEntityConfig();

        //开启 lombok
        if (entityConfig.isWithLombok()) {
            //import lombok.AllArgsConstructor;
            //import lombok.Builder;
            //import lombok.Data;
            //import lombok.NoArgsConstructor;
            imports.add("lombok.AllArgsConstructor");
            imports.add("lombok.Builder");
            imports.add("lombok.Data");
            imports.add("lombok.NoArgsConstructor");
        }

        if (entityConfig.getSupperClass() != null) {
            imports.add(entityConfig.getSupperClass().getName());
        }

        if (entityConfig.getImplInterfaces() != null) {
            for (Class<?> entityInterface : entityConfig.getImplInterfaces()) {
                imports.add(entityInterface.getName());
            }
        }

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

        return imports.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

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
     * 构建 entity 的 Class 名称
     *
     * @return className
     */
    public String buildEntityClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        EntityConfig entityConfig = globalConfig.getEntityConfig();
        return entityConfig.getClassPrefix()
                + entityJavaFileName
                + entityConfig.getClassSuffix();
    }

    /**
     * 构建 tableDef 的 Class 名称
     *
     * @return className
     */
    public String buildTableDefClassName() {
        String tableDefJavaFileName = getEntityJavaFileName();
        TableDefConfig tableDefConfig = globalConfig.getTableDefConfig();
        return tableDefConfig.getClassPrefix()
                + tableDefJavaFileName
                + tableDefConfig.getClassSuffix();
    }

    /**
     * 构建 MapperXml 的文件名称
     *
     * @return fileName
     */
    public String buildMapperXmlFileName() {
        String tableDefJavaFileName = getEntityJavaFileName();
        MapperXmlConfig mapperXmlConfig = globalConfig.getMapperXmlConfig();
        return mapperXmlConfig.getFilePrefix()
                + tableDefJavaFileName
                + mapperXmlConfig.getFileSuffix();
    }

    public String buildExtends() {
        EntityConfig entityConfig = globalConfig.getEntityConfig();
        if (entityConfig.getSupperClass() != null) {
            return " extends " + entityConfig.getSupperClass().getSimpleName();
        } else {
            return "";
        }
    }

    public String buildImplements() {
        Class<?>[] entityInterfaces = globalConfig.getEntityConfig().getImplInterfaces();
        if (entityInterfaces != null && entityInterfaces.length > 0) {
            return " implements " + StringUtil.join(", ", Arrays.stream(entityInterfaces)
                    .map(Class::getSimpleName).collect(Collectors.toList()));
        } else {
            return "";
        }
    }


    public String buildMapperClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        MapperConfig mapperConfig = globalConfig.getMapperConfig();
        return mapperConfig.getClassPrefix()
                + entityJavaFileName
                + mapperConfig.getClassSuffix();
    }

    public String buildServiceClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        ServiceConfig serviceConfig = globalConfig.getServiceConfig();
        return serviceConfig.getClassPrefix()
                + entityJavaFileName
                + serviceConfig.getClassSuffix();
    }

    public String buildServiceImplClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        ServiceImplConfig serviceImplConfig = globalConfig.getServiceImplConfig();
        return serviceImplConfig.getClassPrefix()
                + entityJavaFileName
                + serviceImplConfig.getClassSuffix();
    }

    public String buildControllerClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        ControllerConfig controllerConfig = globalConfig.getControllerConfig();
        return controllerConfig.getClassPrefix()
                + entityJavaFileName
                + controllerConfig.getClassSuffix();
    }

    /**
     * 构建 @Table(...) 注解
     */
    public String buildTableAnnotation() {
        StringBuilder tableAnnotation = new StringBuilder();
        if (globalConfig.getEntityConfig().isWithLombok()) {
            //@Data
            //@Builder
            //@NoArgsConstructor
            //@AllArgsConstructor
            tableAnnotation.append("@Data\n");
            tableAnnotation.append("@Builder\n");
            tableAnnotation.append("@NoArgsConstructor\n");
            tableAnnotation.append("@AllArgsConstructor\n");
        }

        tableAnnotation.append("@Table(value = \"").append(name).append("\"");

        if (tableConfig != null) {
            if (tableConfig.getSchema() != null) {
                tableAnnotation.append(", schema = \"").append(tableConfig.getSchema()).append("\"");
            }
            if (tableConfig.getCamelToUnderline() != null) {
                tableAnnotation.append(", camelToUnderline = \"").append(tableConfig.getCamelToUnderline()).append("\"");
            }
            if (tableConfig.getInsertListenerClass() != null) {
                tableAnnotation.append(", onInsert = ").append(tableConfig.getInsertListenerClass().getSimpleName()).append(".class");
            }
            if (tableConfig.getUpdateListenerClass() != null) {
                tableAnnotation.append(", onUpdate = ").append(tableConfig.getUpdateListenerClass().getSimpleName()).append(".class");
            }
            if (tableConfig.getSetListenerClass() != null) {
                tableAnnotation.append(", onSet = ").append(tableConfig.getUpdateListenerClass().getSimpleName()).append(".class");
            }
            if (Boolean.FALSE.equals(tableConfig.getMapperGenerateEnable())) {
                tableAnnotation.append(", mapperGenerateEnable = false");
            }
        }
        return tableAnnotation.append(")").toString();
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", remarks='" + comment + '\'' +
                ", primaryKeys='" + primaryKeys + '\'' +
                ", columns=" + columns +
                '}';
    }


}
