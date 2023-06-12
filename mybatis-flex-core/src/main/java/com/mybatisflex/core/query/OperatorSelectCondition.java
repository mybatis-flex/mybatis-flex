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
import com.mybatisflex.core.util.ObjectUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;

/**
 * 操作类型的操作
 * 示例1：and EXISTS (select 1 from ... where ....)
 * 示例2：and not EXISTS (select ... from ... where ....)
 */
public class OperatorSelectCondition extends QueryCondition {
    //操作符，例如 exist, not exist
    private final String operator;
    private QueryWrapper queryWrapper;

    public OperatorSelectCondition(String operator, QueryWrapper queryWrapper) {
        this.operator = operator;
        this.queryWrapper = queryWrapper;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();

        //检测是否生效
        if (checkEffective()) {
            String childSql = dialect.buildSelectSql(queryWrapper);
            if (StringUtil.isNotBlank(childSql)) {

                QueryCondition prevEffectiveCondition = getPrevEffectiveCondition();
                if (prevEffectiveCondition != null) {
                    sql.append(prevEffectiveCondition.connector);
                }
                sql.append(operator).append("(").append(childSql).append(")");
            }
        }

        if (this.next != null) {
            return sql + next.toSql(queryTables, dialect);
        }

        return sql.toString();
    }

    @Override
    public Object getValue() {
        return queryWrapper.getValueArray();
    }

    @Override
    boolean containsTable(String... tables) {
        QueryCondition condition = queryWrapper.getWhereQueryCondition();
        return condition != null && condition.containsTable(tables);
    }

    @Override
    public OperatorSelectCondition clone() {
        OperatorSelectCondition clone = (OperatorSelectCondition) super.clone();
        // deep clone ...
        clone.queryWrapper = ObjectUtil.clone(this.queryWrapper);
        return clone;
    }
}
