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
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.util.StringUtil;

import java.util.Objects;

/**
 * 原生查询表。
 *
 * @author 王帅
 * @since 2023-10-16
 */
public class RawQueryTable extends QueryTable {

    protected String content;

    public RawQueryTable(String content) {
        this.content = content;
    }

    @Override
    public String toSql(IDialect dialect, OperateType operateType) {
        return this.content + WrapperUtil.buildAlias(alias, dialect);
    }

    @Override
    boolean isSameTable(QueryTable table) {
        if (table == null) {
            return false;
        }
        // 只比较别名，不比较内容
        if (StringUtil.isNotBlank(alias)
            && StringUtil.isNotBlank(table.alias)) {
            return Objects.equals(alias, table.alias);
        }
        return false;
    }

    @Override
    public String toString() {
        return "RawQueryTable{" +
            "content='" + content + '\'' +
            '}';
    }

    public String getContent() {
        return content;
    }

    @Override
    public RawQueryTable clone() {
        return (RawQueryTable) super.clone();
    }

}
