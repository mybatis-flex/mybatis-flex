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

package com.mybatisflex.core.constant;

/**
 * SQL 构建常量池。
 *
 * @author 王帅
 * @since 2023-06-12
 */
public final class SqlConsts {

    private SqlConsts() {
    }

    // === 常用符号 ===

    public static final String EMPTY = "";
    public static final String BLANK = " ";
    public static final String ASTERISK = "*";
    public static final String REFERENCE = ".";
    public static final String SEMICOLON = ";";
    public static final String DELIMITER = ", ";
    public static final String PLACEHOLDER = "?";
    public static final String PERCENT_SIGN = "%";
    public static final String SINGLE_QUOTE = "'";
    public static final String BRACKET_LEFT = "(";
    public static final String BRACKET_RIGHT = ")";
    public static final String HINT_START = "/*+ ";
    public static final String HINT_END = " */ ";


    // === SQL 关键字 ===

    public static final String AS = " AS ";
    public static final String OR = " OR ";
    public static final String END = " END";
    public static final String AND = " AND ";
    public static final String SET = " SET ";
    public static final String CASE = "CASE";
    public static final String WHEN = " WHEN ";
    public static final String THEN = " THEN ";
    public static final String ELSE = " ELSE ";
    public static final String FROM = " FROM ";
    public static final String WHERE = " WHERE ";
    public static final String SELECT = "SELECT ";
    public static final String VALUES = " VALUES ";
    public static final String DELETE = "DELETE";
    public static final String UPDATE = "UPDATE ";
    public static final String HAVING = " HAVING ";
    public static final String DISTINCT = "DISTINCT ";
    public static final String GROUP_BY = " GROUP BY ";
    public static final String ORDER_BY = " ORDER BY ";
    public static final String INSERT = "INSERT";
    public static final String INTO = " INTO ";
    public static final String WITH = "WITH ";
    public static final String RECURSIVE = "RECURSIVE ";
    public static final String INSERT_INTO = INSERT + INTO;
    public static final String DELETE_FROM = DELETE + FROM;
    public static final String SELECT_ALL_FROM = SELECT + ASTERISK + FROM;


    // === Oracle SQl ===

    public static final String INSERT_ALL = "INSERT ALL ";
    public static final String INSERT_ALL_END = " SELECT 1 FROM DUAL";


    // === Limit Offset ===

    public static final String TO = " TO ";
    public static final String TOP = " TOP ";
    public static final String ROWS = " ROWS ";
    public static final String SKIP = " SKIP ";
    public static final String FIRST = " FIRST ";
    public static final String LIMIT = " LIMIT ";
    public static final String OFFSET = " OFFSET ";
    public static final String START_AT = " START AT ";
    public static final String ROWS_ONLY = " ROWS ONLY";
    public static final String ROWS_FETCH_NEXT = " ROWS FETCH NEXT ";


    // === 联表查询关键字 ===

    public static final String ON = " ON ";
    public static final String JOIN = " JOIN ";
    public static final String UNION = " UNION ";
    public static final String UNION_ALL = " UNION ALL ";
    public static final String LEFT_JOIN = " LEFT JOIN ";
    public static final String FULL_JOIN = " FULL JOIN ";
    public static final String RIGHT_JOIN = " RIGHT JOIN ";
    public static final String INNER_JOIN = " INNER JOIN ";
    public static final String CROSS_JOIN = " CROSS JOIN ";


    // === 逻辑符号 ===

    public static final String GT = " > ";
    public static final String GE = " >= ";
    public static final String LT = " < ";
    public static final String LE = " <= ";
    public static final String LIKE = " LIKE ";
    public static final String NOT_LIKE = " NOT LIKE ";
    public static final String EQUALS = " = ";
    public static final String NOT_EQUALS = " != ";
    public static final String IS_NULL = " IS NULL ";
    public static final String IS_NOT_NULL = " IS NOT NULL ";
    public static final String IN = " IN ";
    public static final String NOT_IN = " NOT IN ";
    public static final String BETWEEN = " BETWEEN ";
    public static final String NOT_BETWEEN = " NOT BETWEEN ";


    // === 排序相关关键字 ===

    public static final String ASC = " ASC";
    public static final String DESC = " DESC";
    public static final String NULLS_FIRST = " NULLS FIRST";
    public static final String NULLS_LAST = " NULLS LAST";


    // === 数学运算符 ===

    public static final String PLUS_SIGN = " + ";
    public static final String MINUS_SIGN = " - ";
    public static final String DIVISION_SIGN = " / ";
    public static final String MULTIPLICATION_SIGN = " * ";

    // === 其他拼接需要的字符串 ===

    public static final String EQUALS_PLACEHOLDER = " = ? ";
    public static final String AND_PLACEHOLDER = BLANK + PLACEHOLDER + AND + PLACEHOLDER + BLANK;

}
