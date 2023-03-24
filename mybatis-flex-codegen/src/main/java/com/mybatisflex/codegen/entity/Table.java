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
        imports.add(com.mybatisflex.annotation.Table.class.getName());
        for (Column column : columns) {
            imports.addAll(column.getImportClasses());
        }
        return imports.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }


    /**
     * 构建 entity 的 Class 名称
     *
     * @return className
     */
    public String buildEntityClassName() {
        String entityJavaFileName = name;
        String tablePrefix = globalConfig.getTablePrefix();
        if (tablePrefix != null && name.startsWith(tablePrefix)) {
            entityJavaFileName = name.substring(tablePrefix.length());
        }
        return StringUtil.firstCharToUpperCase(StringUtil.underlineToCamel(entityJavaFileName));
    }

    /**
     * 构建 @Table(...) 注解
     */
    public String buildTableAnnotation() {
        StringBuilder tableAnnotation = new StringBuilder("@Table(");
        tableAnnotation.append("value=\"").append(name).append("\"");

        if (tableConfig != null) {
            if (tableConfig.getSchema() != null) {
                tableAnnotation.append(", schema=\"" + tableConfig.getSchema() + "\"");
            }
            if (tableConfig.getCamelToUnderline() != null) {
                tableAnnotation.append(", camelToUnderline=\"" + tableConfig.getCamelToUnderline() + "\"");
            }
        }
        return tableAnnotation.append(")").toString();
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
