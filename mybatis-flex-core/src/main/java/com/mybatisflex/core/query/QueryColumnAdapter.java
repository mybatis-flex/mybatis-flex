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
import com.mybatisflex.core.exception.FlexAssert;

import java.util.List;

/**
 * {@link QueryColumn} 适配器，用于将 {@link QueryCondition} 转换为 {@link QueryColumn}。
 *
 * @author 王帅
 * @since 2024-09-29
 */
public class QueryColumnAdapter extends QueryColumn implements HasParamsColumn {

    private final QueryCondition condition;

    public QueryColumnAdapter(QueryCondition condition) {
        FlexAssert.notNull(condition, "condition");
        this.condition = condition;
    }

    public QueryCondition getCondition() {
        return condition;
    }

    @Override
    public Object[] getParamValues() {
        return WrapperUtil.getValues(condition);
    }

    @Override
    protected String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return condition.toSql(queryTables, dialect) + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    protected String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        return condition.toSql(queryTables, dialect);
    }

}
