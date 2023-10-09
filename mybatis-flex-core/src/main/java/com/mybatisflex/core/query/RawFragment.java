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
import com.mybatisflex.core.util.StringUtil;

import java.util.List;

/**
 * SQL 原生片段
 */
public class RawFragment extends QueryCondition {


    protected String content;


    public RawFragment(String content) {
        this.content = content;
    }

    public RawFragment(String content, Object... paras) {
        this.content = content;
        this.setValue(paras);
    }

    @Override
    boolean containsTable(String... tables) {
        for (String table : tables) {
            String[] tableNameWithAlisa = StringUtil.getTableNameWithAlisa(table);
            if (content.contains(tableNameWithAlisa[0])
                || (tableNameWithAlisa[1] != null && content.contains(tableNameWithAlisa[1]))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();

        //检测是否生效
        if (checkEffective()) {
            QueryCondition prevEffectiveCondition = getPrevEffectiveCondition();
            if (prevEffectiveCondition != null && this.connector != null) {
                sql.append(this.connector);
            }
            sql.append(SqlConsts.BLANK).append(content).append(SqlConsts.BLANK);
        }

        if (this.next != null) {
            return sql + next.toSql(queryTables, dialect);
        }

        return sql.toString();
    }

    @Override
    public RawFragment clone() {
        return (RawFragment) super.clone();
    }


    public String getContent() {
        return content;
    }

}
