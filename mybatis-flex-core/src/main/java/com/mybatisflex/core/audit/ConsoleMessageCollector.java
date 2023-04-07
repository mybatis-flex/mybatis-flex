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
package com.mybatisflex.core.audit;

import com.mybatisflex.core.util.DateUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

public class ConsoleMessageCollector implements MessageCollector {

    private SqlDebugPrinter printer = (sql, takedTimeMillis) -> {
        if (takedTimeMillis != null) {
            System.out.println("Flex exec sql taked " + takedTimeMillis + " ms >>>  " + sql);
        } else {
            System.out.println("Flex exec sql >>>  " + sql);
        }
    };

    public ConsoleMessageCollector() {
    }

    public ConsoleMessageCollector(SqlDebugPrinter printer) {
        this.printer = printer;
    }

    @Override
    public void collect(AuditMessage message) {
        String sql = getFullSql(message.getQuery(), message.getQueryParams());
        printer.print(sql, message.getElapsedTime());
    }

    private String getFullSql(String sql, List<Object> params) {
        if (params != null) {
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

    public interface SqlDebugPrinter {
        void print(String sql, Long takedTimeMillis);
    }
}
