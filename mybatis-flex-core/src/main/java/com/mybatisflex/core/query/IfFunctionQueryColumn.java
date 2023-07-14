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
import com.mybatisflex.core.util.ArrayUtil;

import java.util.List;

/**
 * IF 函数查询列。
 *
 * @author 王帅
 * @since 2023-07-07
 */
public class IfFunctionQueryColumn extends QueryColumn implements HasParamsColumn {

    private QueryCondition condition;
    private QueryColumn trueValue;
    private QueryColumn falseValue;

    public IfFunctionQueryColumn(QueryCondition condition, QueryColumn trueValue, QueryColumn falseValue) {
        this.condition = condition;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        return "IF(" + condition.toSql(queryTables, dialect) + ", " +
            trueValue.toConditionSql(queryTables, dialect) + ", " +
            falseValue.toConditionSql(queryTables, dialect) + ")";
    }

    @Override
    public Object[] getParamValues() {
        Object[] params = WrapperUtil.getValues(condition);
        // IF 函数嵌套
        if (trueValue instanceof HasParamsColumn) {
            Object[] paramValues = ((HasParamsColumn) trueValue).getParamValues();
            params = ArrayUtil.concat(params, paramValues);
        }
        if (falseValue instanceof HasParamsColumn) {
            Object[] paramValues = ((HasParamsColumn) falseValue).getParamValues();
            params = ArrayUtil.concat(params, paramValues);
        }
        return params;
    }

    @Override
    public IfFunctionQueryColumn clone() {
        IfFunctionQueryColumn clone = (IfFunctionQueryColumn) super.clone();
        // deep clone ...
        clone.condition = this.condition.clone();
        clone.trueValue = this.trueValue.clone();
        clone.falseValue = this.falseValue.clone();
        return clone;
    }

}
