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
package com.mybatisflex.core.query;


import com.mybatisflex.core.dialect.IDialect;

import java.util.List;

/**
 * 自定义字符串列，用于扩展
 */
public class StringQueryColumn extends QueryColumn {

    protected String content;


    public StringQueryColumn(String content) {
        this.content = content;
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        return content;
    }

    @Override
    String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return content;
    }

    @Override
    public String toString() {
        return "StringQueryColumn{" +
                "content='" + content + '\'' +
                '}';
    }

    @Override
    public StringQueryColumn clone() {
        return (StringQueryColumn) super.clone();
    }
}
