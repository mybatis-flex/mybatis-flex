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

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.table.TableDef;
import com.mybatisflex.core.util.StringUtil;

import java.util.Objects;

/**
 * 查询列，描述的是一张表的字段
 */
public class QueryTable implements CloneSupport<QueryTable> {

    protected String schema;
    protected String name;
    protected String alias;

    public QueryTable() {
    }

    public QueryTable(TableDef tableDef) {
        this.name = tableDef.getTableName();
        this.schema = tableDef.getSchema();
    }

    public QueryTable(String name) {
        this.name = name;
    }

    public QueryTable(String schema, String name) {
        this.schema = schema;
        this.name = name;
    }

    public QueryTable(String schema, String table, String alias) {
        this.schema = schema;
        this.name = table;
        this.alias = alias;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public QueryTable as(String alias) {
        this.alias = alias;
        return this;
    }

    boolean isSameTable(QueryTable table) {
        if (table == null) {
            return false;
        }
        if (StringUtil.isNotBlank(alias)
                && StringUtil.isNotBlank(table.alias)
                && (Objects.equals(alias, table.alias))) {
                return false;

        }
        return Objects.equals(name, table.name);
    }


    Object[] getValueArray() {
        return FlexConsts.EMPTY_ARRAY;
    }

    public String toSql(IDialect dialect) {
        String sql;
        if (StringUtil.isNotBlank(schema)) {
            sql = dialect.wrap(dialect.getRealSchema(schema)) + "." + dialect.wrap(dialect.getRealTable(name)) + WrapperUtil.withAliasIf(alias, dialect);
        } else {
            sql = dialect.wrap(dialect.getRealTable(name)) + WrapperUtil.withAliasIf(alias, dialect);
        }
        return sql;
    }


    @Override
    public String toString() {
        return "QueryTable{" +
                "schema='" + schema + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }

    @Override
    public QueryTable clone() {
        try {
            return (QueryTable) super.clone();
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }
}
