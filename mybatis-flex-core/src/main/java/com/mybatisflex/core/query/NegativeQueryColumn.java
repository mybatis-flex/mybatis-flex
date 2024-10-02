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

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.dialect.IDialect;

import java.util.List;

/**
 * 取相反数（{@code -column}）。
 *
 * @author 王帅
 * @since 2023-11-09
 */
public class NegativeQueryColumn extends QueryColumn implements HasParamsColumn {

    private final QueryColumn queryColumn;

    public NegativeQueryColumn(QueryColumn queryColumn) {
        this.queryColumn = queryColumn;
    }

    @Override
    public Object[] getParamValues() {
        if (queryColumn instanceof HasParamsColumn) {
            return ((HasParamsColumn) queryColumn).getParamValues();
        }
        return FlexConsts.EMPTY_ARRAY;
    }

    @Override
    protected String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        return toConditionSql(queryTables, dialect) + WrapperUtil.buildColumnAlias(alias, dialect);
    }

    @Override
    protected String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        return "-" + queryColumn.toConditionSql(queryTables, dialect);
    }

}
