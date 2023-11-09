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
import com.mybatisflex.core.util.SqlUtil;

import java.util.List;

/**
 * 原生排序字段。
 *
 * @author michael
 * @author 王帅
 */
public class RawQueryOrderBy extends QueryOrderBy {

    protected String content;

    public RawQueryOrderBy(String content) {
        this(content, true);
    }

    public RawQueryOrderBy(String content, boolean checkAvailable) {
        if (checkAvailable) {
            SqlUtil.keepOrderBySqlSafely(content);
        }
        this.content = content;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        return content;
    }

    @Override
    public String toString() {
        return "RawQueryOrderBy{" +
            "content='" + content + '\'' +
            '}';
    }

    public String getContent() {
        return content;
    }

    @Override
    public RawQueryOrderBy clone() {
        return (RawQueryOrderBy) super.clone();
    }

}
