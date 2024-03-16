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
package com.mybatisflex.core.dialect.impl;

import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowCPI;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.SqlUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import static com.mybatisflex.core.constant.SqlConsts.*;

/**
 * @author michael
 */
public class OracleDialect extends CommonsDialectImpl {

    //https://docs.oracle.com/cd/A97630_01/appdev.920/a42525/apb.htm
    public static final Set<String> keywords = CollectionUtil.newHashSet(
        "ACCESS", "ELSE", "MODIFY", "START", "ADD", "EXCLUSIVE", "NOAUDIT", "SELECT",
        "ALL", "EXISTS", "NOCOMPRESS", "SESSION", "ALTER", "FILE", "NOT", "SET", "AND", "FLOAT",
        "NOTFOUND", "SHARE", "ANY", "FOR", "NOWAIT", "SIZE", "ARRAYLEN", "FROM", "NULL", "SMALLINT",
        "AS", "GRANT", "NUMBER", "SQLBUF", "ASC", "GROUP", "OF", "SUCCESSFUL", "AUDIT", "HAVING",
        "OFFLINE", "SYNONYM", "BETWEEN", "IDENTIFIED", "ON", "SYSDATE", "BY", "IMMEDIATE", "ONLINE",
        "TABLE", "CHAR", "IN", "OPTION", "THEN", "CHECK", "INCREMENT", "OR", "TO", "CLUSTER", "INDEX",
        "ORDER", "TRIGGER", "COLUMN", "INITIAL", "PCTFREE", "UID", "COMMENT", "INSERT", "PRIOR",
        "UNION", "COMPRESS", "INTEGER", "PRIVILEGES", "UNIQUE", "CONNECT", "INTERSECT", "PUBLIC",
        "UPDATE", "CREATE", "INTO", "RAW", "USER", "CURRENT", "IS", "RENAME", "VALIDATE", "DATE", "LEVEL",
        "RESOURCE", "VALUES", "DECIMAL", "LIKE", "REVOKE", "VARCHAR", "DEFAULT", "LOCK", "ROW", "VARCHAR2",
        "DELETE", "LONG", "ROWID", "VIEW", "DESC", "MAXEXTENTS", "ROWLABEL", "WHENEVER", "DISTINCT", "MINUS",
        "ROWNUM", "WHERE", "DROP", "MODE", "ROWS", "WITH", "ADMIN", "CURSOR", "FOUND", "MOUNT", "AFTER", "CYCLE",
        "FUNCTION", "NEXT", "ALLOCATE", "DATABASE", "GO", "NEW", "ANALYZE", "DATAFILE", "GOTO", "NOARCHIVELOG",
        "ARCHIVE", "DBA", "GROUPS", "NOCACHE", "ARCHIVELOG", "DEC", "INCLUDING", "NOCYCLE", "AUTHORIZATION",
        "DECLARE", "INDICATOR", "NOMAXVALUE", "AVG", "DISABLE", "INITRANS", "NOMINVALUE", "BACKUP", "DISMOUNT",
        "INSTANCE", "NONE", "BEGIN", "DOUBLE", "INT", "NOORDER", "BECOME", "DUMP", "KEY", "NORESETLOGS", "BEFORE",
        "EACH", "LANGUAGE", "NORMAL", "BLOCK", "ENABLE", "LAYER", "NOSORT", "BODY", "END", "LINK", "NUMERIC", "CACHE",
        "ESCAPE", "LISTS", "OFF", "CANCEL", "EVENTS", "LOGFILE", "OLD", "CASCADE", "EXCEPT", "MANAGE", "ONLY", "CHANGE",
        "EXCEPTIONS", "MANUAL", "OPEN", "CHARACTER", "EXEC", "MAX", "OPTIMAL", "CHECKPOINT", "EXPLAIN", "MAXDATAFILES",
        "OWN", "CLOSE", "EXECUTE", "MAXINSTANCES", "PACKAGE", "COBOL", "EXTENT", "MAXLOGFILES", "PARALLEL", "COMMIT",
        "EXTERNALLY", "MAXLOGHISTORY", "PCTINCREASE", "COMPILE", "FETCH", "MAXLOGMEMBERS", "PCTUSED", "CONSTRAINT",
        "FLUSH", "MAXTRANS", "PLAN", "CONSTRAINTS", "FREELIST", "MAXVALUE", "PLI", "CONTENTS", "FREELISTS", "MIN",
        "PRECISION", "CONTINUE", "FORCE", "MINEXTENTS", "PRIMARY", "CONTROLFILE", "FOREIGN", "MINVALUE", "PRIVATE",
        "COUNT", "FORTRAN", "MODULE", "PROCEDURE", "PROFILE", "SAVEPOINT", "SQLSTATE", "TRACING", "QUOTA", "SCHEMA",
        "STATEMENT_ID", "TRANSACTION", "READ", "SCN", "STATISTICS", "TRIGGERS", "REAL", "SECTION", "STOP", "TRUNCATE",
        "RECOVER", "SEGMENT", "STORAGE", "UNDER", "REFERENCES", "SEQUENCE", "SUM", "UNLIMITED", "REFERENCING", "SHARED",
        "SWITCH", "UNTIL", "RESETLOGS", "SNAPSHOT", "SYSTEM", "USE", "RESTRICTED", "SOME", "TABLES", "USING", "REUSE",
        "SORT", "TABLESPACE", "WHEN", "ROLE", "SQL", "TEMPORARY", "WRITE", "ROLES", "SQLCODE", "THREAD", "WORK", "ROLLBACK",
        "SQLERROR", "TIME", "ABORT", "BETWEEN", "CRASH", "DIGITS", "ACCEPT", "BINARY_INTEGER", "CREATE", "DISPOSE", "ACCESS",
        "BODY", "CURRENT", "DISTINCT", "ADD", "BOOLEAN", "CURRVAL", "DO", "ALL", "BY", "CURSOR", "DROP", "ALTER", "CASE", "DATABASE",
        "ELSE", "AND", "CHAR", "DATA_BASE", "ELSIF", "ANY", "CHAR_BASE", "DATE", "END", "ARRAY", "CHECK", "DBA", "ENTRY", "ARRAYLEN",
        "CLOSE", "DEBUGOFF", "EXCEPTION", "AS", "CLUSTER", "DEBUGON", "EXCEPTION_INIT", "ASC", "CLUSTERS", "DECLARE", "EXISTS",
        "ASSERT", "COLAUTH", "DECIMAL", "EXIT", "ASSIGN", "COLUMNS", "DEFAULT", "FALSE", "AT", "COMMIT", "DEFINITION", "FETCH",
        "AUTHORIZATION", "COMPRESS", "DELAY", "FLOAT", "AVG", "CONNECT", "DELETE", "FOR", "BASE_TABLE", "CONSTANT", "DELTA", "FORM",
        "BEGIN", "COUNT", "DESC", "FROM", "FUNCTION", "NEW", "RELEASE", "SUM", "GENERIC", "NEXTVAL", "REMR", "TABAUTH",
        "GOTO", "NOCOMPRESS", "RENAME", "TABLE", "GRANT", "NOT", "RESOURCE", "TABLES", "GROUP", "NULL", "RETURN", "TASK", "HAVING",
        "NUMBER", "REVERSE", "TERMINATE", "IDENTIFIED", "NUMBER_BASE", "REVOKE", "THEN", "IF", "OF", "ROLLBACK", "TO", "IN", "ON",
        "ROWID", "TRUE", "INDEX", "OPEN", "ROWLABEL", "TYPE", "INDEXES", "OPTION", "ROWNUM", "UNION", "INDICATOR", "OR", "ROWTYPE",
        "UNIQUE", "INSERT", "ORDER", "RUN", "UPDATE", "INTEGER", "OTHERS", "SAVEPOINT", "USE", "INTERSECT", "OUT", "SCHEMA", "VALUES",
        "INTO", "PACKAGE", "SELECT", "VARCHAR", "IS", "PARTITION", "SEPARATE", "VARCHAR2", "LEVEL", "PCTFREE", "SET", "VARIANCE",
        "LIKE", "POSITIVE", "SIZE", "VIEW", "LIMITED", "PRAGMA", "SMALLINT", "VIEWS", "LOOP", "PRIOR", "SPACE", "WHEN", "MAX", "PRIVATE",
        "SQL", "WHERE", "MIN", "PROCEDURE", "SQLCODE", "WHILE", "MINUS", "PUBLIC", "SQLERRM", "WITH", "MLSLABEL", "RAISE", "START",
        "WORK", "MOD", "RANGE", "STATEMENT", "XOR", "MODE", "REAL", "STDDEV", "NATURAL", "RECORD", "SUBTYPE", "GEN", "KP", "L",
        "NA", "NC", "ND", "NL", "NM", "NR", "NS", "NT", "NZ", "TTC", "UPI", "O", "S", "XA"

    );

    public OracleDialect() {
        this(LimitOffsetProcessor.ORACLE);
    }

    public OracleDialect(LimitOffsetProcessor limitOffsetProcessor) {
        this(new KeywordWrap(false, true, keywords, "\"", "\""), limitOffsetProcessor);
    }

    public OracleDialect(KeywordWrap keywordWrap, LimitOffsetProcessor limitOffsetProcessor) {
        super(keywordWrap, limitOffsetProcessor);
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
        sql.append(INSERT_ALL);
        String[] insertColumns = tableInfo.obtainInsertColumns(null, false);
        String[] warpedInsertColumns = new String[insertColumns.length];
        for (int i = 0; i < insertColumns.length; i++) {
            warpedInsertColumns[i] = wrap(insertColumns[i]);
        }


        Map<String, String> onInsertColumns = tableInfo.getOnInsertColumns();
        for (int i = 0; i < entities.size(); i++) {
            sql.append(INTO).append(tableInfo.getWrapSchemaAndTableName(this, OperateType.INSERT));
            sql.append(BLANK).append(BRACKET_LEFT).append(StringUtil.join(DELIMITER, warpedInsertColumns)).append(BRACKET_RIGHT);
            sql.append(VALUES);

            StringJoiner stringJoiner = new StringJoiner(DELIMITER, BRACKET_LEFT, BRACKET_RIGHT);
            for (String insertColumn : insertColumns) {
                if (onInsertColumns != null && onInsertColumns.containsKey(insertColumn)) {
                    //直接读取 onInsert 配置的值，而不用 "?" 代替
                    stringJoiner.add(onInsertColumns.get(insertColumn));
                } else {
                    stringJoiner.add(PLACEHOLDER);
                }
            }
            sql.append(stringJoiner);
        }

        return sql.append(INSERT_ALL_END).toString();
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
        Set<String> attrs = RowCPI.getInsertAttrs(firstRow);
        int index = 0;
        for (String column : attrs) {
            fields.append(wrap(column));
            if (index != attrs.size() - 1) {
                fields.append(SqlConsts.DELIMITER);
            }
            index++;
        }

        StringBuilder sql = new StringBuilder();
        sql.append(INSERT_ALL);

        String table = getRealTable(tableName, OperateType.INSERT);
        String tableNameWrap = StringUtil.isNotBlank(schema)
            ? wrap(getRealSchema(schema, table, OperateType.INSERT)) + REFERENCE + wrap(table)
            : wrap(table);
        String questionStrings = SqlUtil.buildSqlParamPlaceholder(attrs.size());

        for (int i = 0; i < rows.size(); i++) {
            sql.append(INTO).append(tableNameWrap);
            sql.append(BLANK).append(BRACKET_LEFT).append(fields).append(BRACKET_RIGHT);
            sql.append(VALUES).append(questionStrings);
        }

        return sql.append(INSERT_ALL_END).toString();
    }

}
