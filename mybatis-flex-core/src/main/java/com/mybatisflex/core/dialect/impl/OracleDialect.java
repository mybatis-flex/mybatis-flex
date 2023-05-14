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
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.*;

public class OracleDialect extends CommonsDialectImpl {

    private boolean caseSensitive;
    private final Set<String> keywords = CollectionUtil.newHashSet(
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

    public OracleDialect(LimitOffsetProcessor limitOffsetProcessor) {
        super(KeywordWrap.NONE, limitOffsetProcessor);
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    @Override
    public String wrap(String keyword) {
        if (StringUtil.isBlank(keyword)) {
            return "";
        }
        if (caseSensitive || keywords.contains(keyword.toUpperCase(Locale.ENGLISH))) {
            return "\"" + keyword + "\"";
        }
        return keyword;
    }
}
