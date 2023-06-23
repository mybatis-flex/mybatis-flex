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

package com.mybatisflex.processor.builder;

import com.mybatisflex.annotation.Table;
import com.mybatisflex.processor.util.StrUtil;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 文件内容构建。
 *
 * @author 王帅
 * @since 2023-06-23
 */
@SuppressWarnings("all")
public class ContentBuilder {

    private ContentBuilder() {
    }

    /**
     * 构建 Mapper 文件内容。
     */
    public static String buildMapper(String entityClass, String entityClassName,
                                     String mappersPackage, String mapperClassName, String baseMapperClass) {
        StringBuilder content = new StringBuilder("package ");
        content.append(mappersPackage).append(";\n\n");
        content.append("import ").append(baseMapperClass).append(";\n");
        content.append("import ").append(entityClass).append(";\n\n");
        String realEntityClassName = StrUtil.getClassName(entityClass);
        String baseMapperClassName = StrUtil.getClassName(baseMapperClass);
        content.append("public interface ").append(mapperClassName).append(" extends ").append(baseMapperClassName).append("<").append(realEntityClassName).append("> {\n}");
        return content.toString();
    }

    /**
     * 构建 TableDef 文件内容。
     */
    public static String buildTableDef(Table table, String entityClass, String entityClassName, boolean allInTables,
                                       String tableDefPackage, String tableDefClassName,
                                       String tablesNameStyle, String tablesDefSuffix,
                                       Map<String, String> propertyAndColumns, List<String> defaultColumns) {
        StringBuilder content = new StringBuilder("package ");
        content.append(tableDefPackage).append(";\n\n");
        content.append("import com.mybatisflex.core.query.QueryColumn;\n");
        content.append("import com.mybatisflex.core.table.TableDef;\n\n");
        content.append("// Auto generate by mybatis-flex, do not modify it.\n");
        content.append("public class ").append(tableDefClassName).append(" extends TableDef {\n\n");
        if (!allInTables) {
            String schema = !StrUtil.isBlank(table.schema())
                    ? table.schema()
                    : "";
            String tableName = !StrUtil.isBlank(table.value())
                    ? table.value()
                    : StrUtil.firstCharToLowerCase(entityClassName);
            content.append("    public static final ").append(tableDefClassName).append(' ').append(StrUtil.buildFieldName(entityClassName.concat(tablesDefSuffix != null ? tablesDefSuffix.trim() : ""), tablesNameStyle))
                    .append(" = new ").append(tableDefClassName).append("(\"").append(schema).append("\", \"").append(tableName).append("\");\n\n");
        }
        propertyAndColumns.forEach((property, column) -> content.append("    public QueryColumn ")
                .append(StrUtil.buildFieldName(property, tablesNameStyle))
                .append(" = new QueryColumn(this, \"")
                .append(column).append("\");\n"));
        content.append("    public QueryColumn ").append(StrUtil.buildFieldName("allColumns", tablesNameStyle)).append(" = new QueryColumn(this, \"*\");\n");
        StringJoiner defaultColumnJoiner = new StringJoiner(", ");
        propertyAndColumns.forEach((property, column) -> {
            if (defaultColumns.contains(column)) {
                defaultColumnJoiner.add(StrUtil.buildFieldName(property, tablesNameStyle));
            }
        });
        content.append("    public QueryColumn[] ").append(StrUtil.buildFieldName("defaultColumns", tablesNameStyle)).append(" = new QueryColumn[]{").append(defaultColumnJoiner).append("};\n\n");
        content.append("    public ").append(tableDefClassName).append("(String schema, String tableName) {\n")
                .append("       super(schema, tableName);\n")
                .append("    }\n\n}\n");
        return content.toString();
    }

    /**
     * 构建 Tables 文件内容。
     */
    public static String buildTables(StringBuilder importBuilder, StringBuilder fieldBuilder,
                                     String tablesPackage, String tablesClassName) {
        return "package " + tablesPackage + ";\n\n" +
                importBuilder.toString() +
                "\n// Auto generate by mybatis-flex, do not modify it.\n" +
                "public class " + tablesClassName + " {\n\n" +
                "   private " + tablesClassName + "() {\n" +
                "   }\n\n" +
                fieldBuilder.toString() +
                "\n}\n";
    }

    /**
     * 构建 Tables 文件常量属性。
     */
    public static void buildTablesField(StringBuilder importBuilder, StringBuilder fieldBuilder, Table table,
                                        String entityClass, String entityClassName, String tablesNameStyle, String tablesDefSuffix) {
        String tableDefPackage = StrUtil.buildTableDefPackage(entityClass);
        String tableDefClassName = entityClassName.concat("TableDef");
        importBuilder.append("import ").append(tableDefPackage).append('.').append(tableDefClassName).append(";\n");
        String schema = !StrUtil.isBlank(table.schema())
                ? table.schema()
                : "";
        String tableName = !StrUtil.isBlank(table.value())
                ? table.value()
                : StrUtil.firstCharToLowerCase(entityClassName);
        fieldBuilder.append("   public static final ").append(tableDefClassName).append(' ')
                .append(StrUtil.buildFieldName(entityClassName.concat(tablesDefSuffix != null ? tablesDefSuffix.trim() : ""), tablesNameStyle))
                .append(" = new ").append(tableDefClassName).append("(\"").append(schema).append("\", \"").append(tableName).append("\");\n");
    }

}