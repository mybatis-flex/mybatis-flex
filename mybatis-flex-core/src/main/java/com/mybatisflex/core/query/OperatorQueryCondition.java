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

import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.util.ObjectUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;

/**
 * 操作类型的操作
 * 示例1：and not ( id > 100 and name like %%)
 */
public class OperatorQueryCondition extends QueryCondition {

    private final String operator;
    private QueryCondition childCondition;

    public OperatorQueryCondition(String operator, QueryCondition childCondition) {
        this.operator = operator;
        this.childCondition = childCondition;
    }

    public QueryCondition getChildCondition() {
        return childCondition;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();

        //检测是否生效
        if (checkEffective()) {
            String childSql = childCondition.toSql(queryTables, dialect);
            if (StringUtil.isNotBlank(childSql)) {
                QueryCondition prevEffectiveCondition = getPrevEffectiveCondition();
                if (prevEffectiveCondition != null && this.connector != null) {
                    sql.append(this.connector);
                }
                sql.append(operator)
                    .append(SqlConsts.BRACKET_LEFT)
                    .append(childSql)
                    .append(SqlConsts.BRACKET_RIGHT);
            }
        }

        if (this.next != null) {
            return sql + next.toSql(queryTables, dialect);
        }

        return sql.toString();
    }

    @Override
    public Object getValue() {
        return checkEffective() ? WrapperUtil.getValues(childCondition) : null;
    }

    @Override
    boolean containsTable(String... tables) {
        if (childCondition != null && childCondition.containsTable(tables)) {
            return true;
        }
        return nextContainsTable(tables);
    }

    @Override
    public OperatorQueryCondition clone() {
        OperatorQueryCondition clone = (OperatorQueryCondition) super.clone();
        // deep clone ...
        clone.childCondition = ObjectUtil.clone(this.childCondition);
        return clone;
    }

}
