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
package com.mybatisflex.core.query;


import com.mybatisflex.core.dialect.IDialect;

import java.util.List;

/**
 * 自定义字符串列，用于扩展
 */
public class StringQueryCondition extends QueryCondition {

    protected String sqlContent;


    public StringQueryCondition(String content) {
        this.sqlContent = content;
    }

    public StringQueryCondition(String content, Object... paras) {
        this.sqlContent = content;
        this.setValue(paras);
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();

        //检测是否生效
        if (checkEffective()) {
            QueryCondition effectiveBefore = getEffectiveBefore();
            if (effectiveBefore != null) {
                sql.append(effectiveBefore.connector);
            }
            sql.append(" ").append(sqlContent).append(" ");
        }

        if (this.next != null) {
            return sql + next.toSql(queryTables, dialect);
        }

        return sql.toString();
    }
}
