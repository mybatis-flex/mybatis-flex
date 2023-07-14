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
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.ObjectUtil;

public class UnionWrapper implements CloneSupport<UnionWrapper> {

    private String key;
    private QueryWrapper queryWrapper;

    static UnionWrapper union(QueryWrapper queryWrapper) {
        UnionWrapper unionWrapper = new UnionWrapper();
        unionWrapper.key = SqlConsts.UNION;
        unionWrapper.queryWrapper = queryWrapper;
        return unionWrapper;
    }

    static UnionWrapper unionAll(QueryWrapper queryWrapper) {
        UnionWrapper unionWrapper = new UnionWrapper();
        unionWrapper.key = SqlConsts.UNION_ALL;
        unionWrapper.queryWrapper = queryWrapper;
        return unionWrapper;
    }


    private UnionWrapper() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    public void buildSql(StringBuilder sqlBuilder, IDialect dialect) {
        sqlBuilder.append(key)
            .append(SqlConsts.BRACKET_LEFT)
            .append(dialect.buildSelectSql(queryWrapper))
            .append(SqlConsts.BRACKET_RIGHT);
    }

    @Override
    public UnionWrapper clone() {
        try {
            UnionWrapper clone = (UnionWrapper) super.clone();
            // deep clone ...
            clone.queryWrapper = ObjectUtil.clone(this.queryWrapper);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }

}
