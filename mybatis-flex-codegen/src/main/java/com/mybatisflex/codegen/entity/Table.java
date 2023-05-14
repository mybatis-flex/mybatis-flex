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

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.TableConfig;
import com.mybatisflex.core.util.StringUtil;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class Table {

    private String name;
    private String remarks;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

        column.setColumnConfig(globalConfig.getColumnConfig(name, column.getName()));

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

        //开启 lombok
        if (globalConfig.isEntityWithLombok()) {
            //import lombok.AllArgsConstructor;
            //import lombok.Builder;
            //import lombok.Data;
            //import lombok.NoArgsConstructor;
            imports.add("lombok.AllArgsConstructor");
            imports.add("lombok.Builder");
            imports.add("lombok.Data");
            imports.add("lombok.NoArgsConstructor");
        }

        if (globalConfig.getEntitySupperClass() != null) {
            imports.add(globalConfig.getEntitySupperClass().getName());
        }

        if (globalConfig.getEntityInterfaces() != null) {
            for (Class<?> entityInterface : globalConfig.getEntityInterfaces()) {
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

    public String buildRemarks(){
        if (StringUtil.isBlank(remarks)){
            return "";
        }else {
            StringBuilder sb = new StringBuilder("/**\n")
                    .append(" * ").append(remarks).append("\n")
                    .append(" */");
            return sb.toString();
        }
    }

    public String getEntityJavaFileName() {
        String entityJavaFileName = name;
        String tablePrefix = globalConfig.getTablePrefix();
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
        return globalConfig.getEntityClassPrefix()
                + entityJavaFileName
                + globalConfig.getEntityClassSuffix();
    }

    /**
     * 构建 tableDef 的 Class 名称
     *
     * @return className
     */
    public String buildTableDefClassName() {
        String tableDefJavaFileName = getEntityJavaFileName();
        return globalConfig.getTableDefClassPrefix()
                + tableDefJavaFileName
                + globalConfig.getTableDefClassSuffix();
    }

    public String buildExtends() {
        if (globalConfig.getEntitySupperClass() != null) {
            return " extends " + globalConfig.getEntitySupperClass().getSimpleName();
        } else {
            return "";
        }
    }

    public String buildImplements() {
        Class<?>[] entityInterfaces = globalConfig.getEntityInterfaces();
        if (entityInterfaces != null && entityInterfaces.length > 0) {
            return " implements " + StringUtil.join(", ", Arrays.stream(entityInterfaces)
                    .map(Class::getSimpleName).collect(Collectors.toList()));
        } else {
            return "";
        }
    }


    public String buildMapperClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        return globalConfig.getMapperClassPrefix()
                + entityJavaFileName
                + globalConfig.getMapperClassSuffix();
    }

    public String buildServiceClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        return globalConfig.getServiceClassPrefix()
                + entityJavaFileName
                + globalConfig.getServiceClassSuffix();
    }

    public String buildServiceImplClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        return globalConfig.getServiceImplClassPrefix()
                + entityJavaFileName
                + globalConfig.getServiceImplClassSuffix();
    }

    public String buildControllerClassName() {
        String entityJavaFileName = getEntityJavaFileName();
        return globalConfig.getControllerClassPrefix()
                + entityJavaFileName
                + globalConfig.getControllerClassSuffix();
    }

    /**
     * 构建 @Table(...) 注解
     */
    public String buildTableAnnotation() {
        StringBuilder tableAnnotation = new StringBuilder();
        if (globalConfig.isEntityWithLombok()) {
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
                tableAnnotation.append(", schema = \"" + tableConfig.getSchema() + "\"");
            }
            if (tableConfig.getCamelToUnderline() != null) {
                tableAnnotation.append(", camelToUnderline = \"" + tableConfig.getCamelToUnderline() + "\"");
            }
            if (tableConfig.getInsertListenerClass() != null) {
                tableAnnotation.append(", onInsert = " + tableConfig.getInsertListenerClass().getSimpleName() + ".class");
            }
            if (tableConfig.getUpdateListenerClass() != null) {
                tableAnnotation.append(", onUpdate = " + tableConfig.getUpdateListenerClass().getSimpleName() + ".class");
            }
            if (tableConfig.getSetListenerClass() != null) {
                tableAnnotation.append(", onSet = " + tableConfig.getUpdateListenerClass().getSimpleName() + ".class");
            }
            if (Boolean.FALSE.equals(tableConfig.getMapperGenerateEnable())) {
                tableAnnotation.append(", mapperGenerateEnable = false");
            }
        }
        return tableAnnotation.append(")").toString();
    }

    public String buildMapperImport() {
        return globalConfig.getMapperSupperClass().getName();
    }

    public String buildServiceImport() {
        return globalConfig.getServiceSupperClass().getName();
    }

    public String buildServiceImplImport() {
        return globalConfig.getServiceImplSupperClass().getName();
    }

    public String buildMapperName() {
        return globalConfig.getMapperSupperClass().getSimpleName();
    }

    public String buildServiceName() {
        return globalConfig.getServiceSupperClass().getSimpleName();
    }

    public String buildServiceImplName() {
        return globalConfig.getServiceImplSupperClass().getSimpleName();
    }

    public String buildControllerName() {
        return globalConfig.getControllerSupperClass().getSimpleName();
    }


    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", remarks='" + remarks + '\'' +
                ", primaryKeys='" + primaryKeys + '\'' +
                ", columns=" + columns +
                '}';
    }


}
