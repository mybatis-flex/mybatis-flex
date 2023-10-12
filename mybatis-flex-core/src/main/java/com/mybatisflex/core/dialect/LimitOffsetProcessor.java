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
package com.mybatisflex.core.dialect;

import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryOrderBy;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.CollectionUtil;

import java.util.List;

import static com.mybatisflex.core.constant.SqlConsts.*;

/**
 * limit 和 offset 参数的处理器
 */
public interface LimitOffsetProcessor {

    /**
     * 处理构建 limit 和 offset
     *
     * @param dialect      数据方言
     * @param sql          已经构建的 sql
     * @param queryWrapper 参数内容
     * @param limitRows    用户传入的 limit 参数 可能为 null
     * @param limitOffset  用户传入的 offset 参数，可能为 null
     */
    StringBuilder process(IDialect dialect, StringBuilder sql, QueryWrapper queryWrapper, Long limitRows, Long limitOffset);


    /**
     * MySql 的处理器
     * 适合 {@link DbType#MYSQL,DbType#MARIADB,DbType#H2,DbType#CLICK_HOUSE,DbType#XCloud}
     */
    LimitOffsetProcessor MYSQL = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            sql.append(LIMIT).append(limitOffset).append(DELIMITER).append(limitRows);
        } else if (limitRows != null) {
            sql.append(LIMIT).append(limitRows);
        }
        return sql;
    };

    /**
     * Postgresql 的处理器
     * 适合  {@link DbType#POSTGRE_SQL,DbType#SQLITE,DbType#H2,DbType#HSQL,DbType#KINGBASE_ES,DbType#PHOENIX}
     * 适合  {@link DbType#SAP_HANA,DbType#IMPALA,DbType#HIGH_GO,DbType#VERTICA,DbType#REDSHIFT}
     * 适合  {@link DbType#OPENGAUSS,DbType#TDENGINE,DbType#UXDB}
     */
    LimitOffsetProcessor POSTGRESQL = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            sql.append(LIMIT).append(limitRows).append(OFFSET).append(limitOffset);
        } else if (limitRows != null) {
            sql.append(LIMIT).append(limitRows);
        }
        return sql;
    };

    /**
     * derby 的处理器
     * 适合  {@link DbType#DERBY,DbType#ORACLE_12C,DbType#SQLSERVER ,DbType#POSTGRE_SQL}
     */
    LimitOffsetProcessor DERBY = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            // OFFSET ** ROWS FETCH NEXT ** ROWS ONLY")
            sql.append(OFFSET).append(limitOffset).append(ROWS_FETCH_NEXT).append(limitRows).append(ROWS_ONLY);
        } else if (limitRows != null) {
            sql.append(OFFSET).append(0).append(ROWS_FETCH_NEXT).append(limitRows).append(ROWS_ONLY);
        }
        return sql;
    };

    /**
     * derby 的处理器
     * 适合  {@link DbType#DERBY,DbType#ORACLE_12C,DbType#SQLSERVER ,DbType#POSTGRE_SQL}
     */
    LimitOffsetProcessor SQLSERVER = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            // OFFSET ** ROWS FETCH NEXT ** ROWS ONLY")
            sql.append(OFFSET).append(limitOffset).append(ROWS_FETCH_NEXT).append(limitRows).append(ROWS_ONLY);
        } else if (limitRows != null) {
            List<QueryOrderBy> orderBys = CPI.getOrderBys(queryWrapper);
            if (CollectionUtil.isNotEmpty(orderBys)) {
                sql.append(OFFSET).append(0).append(ROWS_FETCH_NEXT).append(limitRows).append(ROWS_ONLY);
            } else {
                sql.insert(6, TOP + limitRows);
            }
        }
        return sql;
    };


    /**
     * SqlServer 2005 limit 处理器
     */
    LimitOffsetProcessor SQLSERVER_2005 = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null) {
            if (limitOffset == null) {
                limitOffset = 0L;
            }

            // fix-bug:#I87AOA QueryWrapper 构建的SQL 与 执行的SQL不一致
            List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
            List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
            List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);
            String originalSQL = sql.toString();
            String orderByString;
            List<QueryOrderBy> orderBys = CPI.getOrderBys(queryWrapper);
            if (orderBys == null || orderBys.isEmpty()) {
                orderByString = "ORDER BY CURRENT_TIMESTAMP";
            } else {
                StringBuilder orderBySql = new StringBuilder(ORDER_BY);
                int index = 0;
                for (QueryOrderBy orderBy : orderBys) {
                    orderBySql.append(orderBy.toSql(allTables, dialect));
                    if (index != orderBys.size() - 1) {
                        orderBySql.append(DELIMITER);
                    }
                    index++;
                }
                originalSQL = originalSQL.substring(0, sql.lastIndexOf(ORDER_BY));
                orderByString = orderBySql.toString();
            }

            StringBuilder newSql = new StringBuilder("WITH temp_datas AS(");
            newSql.append("SELECT ROW_NUMBER() OVER (").append(orderByString).append(") as __rn,").append(originalSQL.substring(6));
            newSql.append(")");
            newSql.append(" SELECT * FROM temp_datas WHERE __rn BETWEEN ").append(limitOffset + 1).append(" AND ").append(limitOffset + limitRows);
            newSql.append(" ORDER BY __rn");
            return newSql;
        }
        return sql;
    };


    /**
     * Informix 的处理器
     * 适合  {@link DbType#INFORMIX}
     * 文档 {@link <a href="https://www.ibm.com/docs/en/informix-servers/14.10?topic=clause-restricting-return-values-skip-limit-first-options">https://www.ibm.com/docs/en/informix-servers/14.10?topic=clause-restricting-return-values-skip-limit-first-options</a>}
     */
    LimitOffsetProcessor INFORMIX = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            // SELECT SKIP 2 FIRST 1 * FROM
            sql.insert(6, SKIP + limitOffset + FIRST + limitRows);
        } else if (limitRows != null) {
            sql.insert(6, FIRST + limitRows);
        }
        return sql;
    };


    /**
     *
     * SINODB 的处理器
     * 适合  {@link DbType#SINODB}
     */
    LimitOffsetProcessor SINODB = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            // SELECT SKIP 2 FIRST 1 * FROM
            sql.insert(6, SKIP + limitOffset + FIRST + limitRows);
        } else if (limitRows != null) {
            sql.insert(6, FIRST + limitRows);
        }
        return sql;
    };

    /**
     * Firebird 的处理器
     * 适合  {@link DbType#FIREBIRD}
     */
    LimitOffsetProcessor FIREBIRD = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            // ROWS 2 TO 3
            sql.append(ROWS).append(limitOffset).append(TO).append(limitOffset + limitRows);
        } else if (limitRows != null) {
            sql.insert(6, FIRST + limitRows);
        }
        return sql;
    };

    /**
     * Oracle11g及以下数据库的处理器
     * 适合  {@link DbType#ORACLE,DbType#DM,DbType#GAUSS}
     */
    LimitOffsetProcessor ORACLE = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null) {
            if (limitOffset == null) {
                limitOffset = 0L;
            }
            StringBuilder newSql = new StringBuilder("SELECT * FROM (SELECT TEMP_DATAS.*, ROWNUM RN FROM (");
            newSql.append(sql);
            newSql.append(") TEMP_DATAS WHERE ROWNUM <= ").append(limitOffset + limitRows).append(") WHERE RN > ").append(limitOffset);
            return newSql;
        }
        return sql;
    };

    /**
     * Sybase 处理器
     * 适合  {@link DbType#SYBASE}
     */
    LimitOffsetProcessor SYBASE = (dialect, sql, queryWrapper, limitRows, limitOffset) -> {
        if (limitRows != null && limitOffset != null) {
            //SELECT TOP 1 START AT 3 * FROM
            sql.insert(6, TOP + limitRows + START_AT + (limitOffset + 1));
        } else if (limitRows != null) {
            sql.insert(6, TOP + limitRows);
        }
        return sql;
    };


}
