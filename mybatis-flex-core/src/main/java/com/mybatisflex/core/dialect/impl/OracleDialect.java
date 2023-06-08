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
package com.mybatisflex.core.dialect.impl;

import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class OracleDialect extends CommonsDialectImpl {

    private static final Set<String> keywords = CollectionUtil.newHashSet(
            "ACCESS", "ADD", "ALL", "ALTER",
            "AND", "ANY", "ARRAYLEN", "AS",
            "ASC", "AUDIT", "BETWEEN", "BY",
            "CHAR", "CHECK", "CLUSTER", "COLUMN",
            "COMMENT", "COMPRESS", "CONNECT", "CREATE",
            "CURRENT", "DATE", "DECIMAL", "DEFAULT",
            "DELETE", "DESC", "DISTINCT", "DROP",
            "ELSE", "EXCLUSIVE", "EXISTS", "FILE",
            "FLOAT", "FOR", "FROM", "GRANT",
            "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE",
            "IN", "INCREMENT", "INDEX", "INITIAL",
            "INSERT", "INTEGER", "INTERSECT", "INTO",
            "IS", "LEVEL", "LIKE", "LOCK",
            "LONG", "MAXEXTENTS", "MINUS", "MODE",
            "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT",
            "NOTFOUND", "NOWAIT", "NULL", "NUMBER",
            "OF", "OFFLINE", "ON", "ONLINE",
            "OPTION", "OR", "ORDER", "PCTFREE",
            "PRIOR", "PRIVILEGES", "PUBLIC", "RAW",
            "RENAME", "RESOURCE", "REVOKE", "ROW",
            "ROWID", "ROWLABEL", "ROWNUM", "ROWS",
            "START", "SELECT", "SESSION", "SET",
            "SHARE", "SIZE", "SMALLINT", "SQLBUF",
            "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE",
            "THEN", "TO", "TRIGGER", "UID",
            "UNION", "UNIQUE", "UPDATE", "USER",
            "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2"
    );

    public OracleDialect() {
        this(LimitOffsetProcessor.ORACLE);
    }

    public OracleDialect(LimitOffsetProcessor limitOffsetProcessor) {
        super(new KeywordWrap(keywords, "\"", "\""), limitOffsetProcessor);
    }

    @Override
    public String forInsertEntityBatch(TableInfo tableInfo, List<?> entities) {
        /**
         * INSERT ALL
         *    INTO t (col1, col2, col3) VALUES ('val1_1', 'val1_2', 'val1_3')
         *    INTO t (col1, col2, col3) VALUES ('val2_1', 'val2_2', 'val2_3')
         *    INTO t (col1, col2, col3) VALUES ('val3_1', 'val3_2', 'val3_3')
         *    .
         *    .
         *    .
         * SELECT 1 FROM DUAL;
         */
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT ALL");
        String[] insertColumns = tableInfo.obtainInsertColumns(null, false);
        String[] warpedInsertColumns = new String[insertColumns.length];
        for (int i = 0; i < insertColumns.length; i++) {
            warpedInsertColumns[i] = wrap(insertColumns[i]);
        }


        Map<String, String> onInsertColumns = tableInfo.getOnInsertColumns();
        for (int i = 0; i < entities.size(); i++) {
            sql.append(" INTO ").append(tableInfo.getWrapSchemaAndTableName(this));
            sql.append(" (").append(StringUtil.join(", ", warpedInsertColumns)).append(")");
            sql.append(" VALUES ");

            StringJoiner stringJoiner = new StringJoiner(", ", "(", ")");
            for (String insertColumn : insertColumns) {
                if (onInsertColumns != null && onInsertColumns.containsKey(insertColumn)) {
                    //直接读取 onInsert 配置的值，而不用 "?" 代替
                    stringJoiner.add(onInsertColumns.get(insertColumn));
                } else {
                    stringJoiner.add("?");
                }
            }
            sql.append(stringJoiner);
        }

        return sql.append(" SELECT 1 FROM DUAL").toString();
    }


    @Override
    public String forInsertBatchWithFirstRowColumns(String schema, String tableName, List<Row> rows) {
        /**
         * INSERT ALL
         *    INTO t (col1, col2, col3) VALUES ('val1_1', 'val1_2', 'val1_3')
         *    INTO t (col1, col2, col3) VALUES ('val2_1', 'val2_2', 'val2_3')
         *    INTO t (col1, col2, col3) VALUES ('val3_1', 'val3_2', 'val3_3')
         *    .
         *    .
         *    .
         * SELECT 1 FROM DUAL;
         */
        StringBuilder fields = new StringBuilder();
        Row firstRow = rows.get(0);
        Set<String> attrs = firstRow.obtainModifyAttrs();
        int index = 0;
        for (String column : attrs) {
            fields.append(wrap(column));
            if (index != attrs.size() - 1) {
                fields.append(", ");
            }
            index++;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT ALL");

        String tableNameWrap = StringUtil.isNotBlank(schema)
                ? wrap(getRealSchema(schema)) + "." + wrap(getRealTable(tableName))
                : wrap(getRealTable(tableName));
        String questionStrings = buildQuestion(attrs.size(), true);

        for (int i = 0; i < rows.size(); i++) {
            sql.append(" INTO ").append(tableNameWrap);
            sql.append(" (").append(fields).append(")");
            sql.append(" VALUES ").append(questionStrings);
        }

        return sql.append(" SELECT 1 FROM DUAL").toString();
    }
}
