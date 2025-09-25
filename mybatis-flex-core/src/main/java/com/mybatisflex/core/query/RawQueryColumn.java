/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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

import java.util.Arrays;
import java.util.List;

/**
 * 原生列。
 *
 * @author michael
 * @author 王帅
 */
public class RawQueryColumn extends QueryColumn implements HasParamsColumn {

    protected String content;
    protected Object[] params;

    public RawQueryColumn(Object content, Object... params) {
        this.content = String.valueOf(content);
        this.params = params;
    }

    @Override
    protected String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        return dialect.wrap(content);
    }

    @Override
    protected String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return dialect.wrap(content) + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    public String toString() {
        return "RawQueryColumn{" +
                "content='" + content + '\'' +
                ", params='" + Arrays.toString(params) + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    public Object[] getParams() {
        return params;
    }

    @Override
    public RawQueryColumn clone() {
        return (RawQueryColumn) super.clone();
    }

    @Override
    public Object[] getParamValues() {
        return params;
    }

}
