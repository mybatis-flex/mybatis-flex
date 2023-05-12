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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/14
 */
public class Join implements Serializable {

    private static final long serialVersionUID = 1L;

    static final String TYPE_LEFT = " LEFT JOIN ";
    static final String TYPE_RIGHT = " RIGHT JOIN ";
    static final String TYPE_INNER = " INNER JOIN ";
    static final String TYPE_FULL = " FULL JOIN ";
    static final String TYPE_CROSS = " CROSS JOIN ";


    private final String type;
    private final QueryTable queryTable;
    private QueryCondition on;
    private boolean effective;

    public Join(String type, String table, boolean when) {
        this.type = type;
        this.queryTable = new QueryTable(table);
        this.effective = when;
    }

    public Join(String type, QueryWrapper queryWrapper, boolean when) {
        this.type = type;
        this.queryTable = new SelectQueryTable(queryWrapper);
        this.effective = when;
    }



    QueryTable getQueryTable() {
        return queryTable;
    }


    public void on(QueryCondition condition) {
        this.on = condition;
    }

    QueryCondition getOnCondition(){
        return on;
    }

    public boolean checkEffective() {
        return effective;
    }

    public void when(boolean when) {
        this.effective = when;
    }

    public void when(Supplier<Boolean> fn) {
        this.effective = fn.get();
    }

    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        //left join, right join,  inner join ...
        StringBuilder sql = new StringBuilder(type);
        sql.append(queryTable.toSql(dialect));

        //left join xxx as xxx2 on xxx2.id = xxx3.other
        List<QueryTable> newQueryTables = new ArrayList<>(queryTables);
        newQueryTables.add(queryTable);
        sql.append(" ON ").append(on.toSql(newQueryTables, dialect));
        return sql.toString();
    }
}
