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
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.SqlUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 数据库 聚合函数，例如 CONVERT(NVARCHAR(30), GETDATE(), 126) 等等
 */
public class StringFunctionQueryColumn extends QueryColumn {

    protected String fnName;
    protected List<String> params;

    public StringFunctionQueryColumn(String fnName, String... params) {
        SqlUtil.keepColumnSafely(fnName);
        this.fnName = fnName;
        this.params = Arrays.asList(params);
    }


    public String getFnName() {
        return fnName;
    }

    public void setFnName(String fnName) {
        this.fnName = fnName;
    }


    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    @Override
    public String toSelectSql(List<QueryTable> queryTables, IDialect dialect) {
        String sql = StringUtil.join(SqlConsts.DELIMITER, params);
        if (StringUtil.isBlank(sql)) {
            return SqlConsts.EMPTY;
        }
        if (StringUtil.isBlank(alias)) {
            return fnName + WrapperUtil.withBracket(sql);
        }
        return fnName + WrapperUtil.withAlias(sql, alias, dialect);
    }

    @Override
    String toConditionSql(List<QueryTable> queryTables, IDialect dialect) {
        String sql = StringUtil.join(SqlConsts.DELIMITER, params);
        if (StringUtil.isBlank(sql)) {
            return SqlConsts.EMPTY;
        }
        return fnName + WrapperUtil.withBracket(sql);
    }


    @Override
    public String toString() {
        return "StringFunctionQueryColumn{" +
            "fnName='" + fnName + '\'' +
            ", params=" + params +
            '}';
    }

    @Override
    public StringFunctionQueryColumn clone() {
        StringFunctionQueryColumn clone = (StringFunctionQueryColumn) super.clone();
        // deep clone ...
        clone.params = CollectionUtil.newArrayList(this.params);
        return clone;
    }

}
