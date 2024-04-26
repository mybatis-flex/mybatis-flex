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

/**
 * 查询的 table，
 * 实例1：用于构建 select * from (select ...) 中的第二个 select
 * 实例2：用于构建 left join (select ...) 中的 select
 */
public class SelectQueryTable extends QueryTable {

    private QueryWrapper queryWrapper;

    public SelectQueryTable(QueryWrapper queryWrapper) {
        super();
        this.queryWrapper = queryWrapper;
    }

    public QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    @Override
    Object[] getValueArray() {
        return queryWrapper.getAllValueArray();
    }

    @Override
    public String toSql(IDialect dialect, OperateType operateType) {
        String sql = dialect.buildSelectSql(queryWrapper);
        if (StringUtil.isNotBlank(alias)) {
            return WrapperUtil.withAlias(sql, alias, dialect);
        } else {
            return WrapperUtil.withBracket(sql);
        }
    }

    @Override
    public SelectQueryTable clone() {
        SelectQueryTable clone = (SelectQueryTable) super.clone();
        // deep clone ...
        clone.queryWrapper = this.queryWrapper.clone();
        return clone;
    }

    @Override
    public String toString() {
        return queryWrapper.toSQL();
    }
}
