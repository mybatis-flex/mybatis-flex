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
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.StringUtil;

import java.util.Objects;

/**
 * 查询表。
 *
 * @author michael
 * @author 王帅
 */
public class QueryTable implements CloneSupport<QueryTable> {


    protected String schema;
    protected String name;
    protected String alias;

    protected QueryTable() {
    }

    public QueryTable(String name) {
        String[] schemaAndTableName = StringUtil.getSchemaAndTableName(name);
        this.schema = schemaAndTableName[0];
        this.name = schemaAndTableName[1];
    }

    public QueryTable(String schema, String name) {
        this.schema = StringUtil.tryTrim(schema);
        this.name = StringUtil.tryTrim(name);
    }

    public QueryTable(String schema, String table, String alias) {
        this.schema = StringUtil.tryTrim(schema);
        this.name = StringUtil.tryTrim(table);
        this.alias = StringUtil.tryTrim(alias);
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

    public String getNameWithSchema() {
        return StringUtil.isNotBlank(schema) ? schema + "." + name : name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public QueryTable as(String alias) {
        this.alias = alias;
        return this;
    }

    boolean isSameTable(QueryTable table) {
        if (table == null) {
            return false;
        }
        if (this == table) {
            return true;
        }
        if (StringUtil.isNotBlank(alias)
            && StringUtil.isNotBlank(table.alias)) {
            return Objects.equals(alias, table.alias);
        }
        return Objects.equals(name, table.name);
    }

    Object[] getValueArray() {
        return FlexConsts.EMPTY_ARRAY;
    }

    public String toSql(IDialect dialect, OperateType operateType) {
        String sql;
        if (StringUtil.isNotBlank(schema)) {
            String table = dialect.getRealTable(name, operateType);
            sql = dialect.wrap(dialect.getRealSchema(schema, table, operateType)) + "." + dialect.wrap(table) + WrapperUtil.buildAlias(alias, dialect);
        } else {
            sql = dialect.wrap(dialect.getRealTable(name, operateType)) + WrapperUtil.buildAlias(alias, dialect);
        }
        return sql;
    }

    @Override
    public String toString() {
        return "QueryTable{" + "schema='" + schema + '\'' + ", name='" + name + '\'' + ", alias='" + alias + '\'' + '}';
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
