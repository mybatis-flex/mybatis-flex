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
package com.mybatisflex.core.util;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.regex.Matcher;

public class SqlUtil {

    private SqlUtil() {
    }

    public static void keepColumnSafely(String column) {
        if (StringUtil.isBlank(column)) {
            throw new IllegalArgumentException("Column must not be empty");
        } else {
            column = column.trim();
        }

        int strLen = column.length();
        for (int i = 0; i < strLen; ++i) {
            char ch = column.charAt(i);
            if (Character.isWhitespace(ch)) {
                throw new IllegalArgumentException("Column must not has space char.");
            }
            if (isUnSafeChar(ch)) {
                throw new IllegalArgumentException("Column has unsafe char: [" + ch + "].");
            }
        }
    }


    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    private static String SQL_ORDER_BY_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    public static void keepOrderBySqlSafely(String value) {
        if (!value.matches(SQL_ORDER_BY_PATTERN)) {
            throw new IllegalArgumentException("Order By sql not safe, order by string: " + value);
        }
    }


    private static final char[] UN_SAFE_CHARS = "'`\"<>&+=#-;".toCharArray();

    private static boolean isUnSafeChar(char ch) {
        for (char c : UN_SAFE_CHARS) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }


    /**
     * 根据数据库响应结果判断数据库操作是否成功。
     *
     * @param result 数据库操作返回影响条数
     * @return {@code true} 操作成功，{@code false} 操作失败。
     */
    public static boolean toBool(Number result) {
        return result != null && result.longValue() > 0;
    }


    /**
     * 替换 sql 中的问号 ？
     * @param sql sql 内容
     * @param params 参数
     * @return 完整的 sql
     */
    public static String replaceSqlParams(String sql, Object[] params) {
        if (params != null && params.length > 0) {
            for (Object value : params) {
                // null
                if (value == null) {
                    sql = sql.replaceFirst("\\?", "null");
                }
                // number
                else if (value instanceof Number || value instanceof Boolean) {
                    sql = sql.replaceFirst("\\?", value.toString());
                }
                // other
                else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("'");
                    if (value instanceof Date) {
                        sb.append(DateUtil.toDateTimeString((Date) value));
                    } else if (value instanceof LocalDateTime) {
                        sb.append(DateUtil.toDateTimeString(DateUtil.toDate((LocalDateTime) value)));
                    } else {
                        sb.append(value);
                    }
                    sb.append("'");
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(sb.toString()));
                }
            }
        }
        return sql;
    }



}
