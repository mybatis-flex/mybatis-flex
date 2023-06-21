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
import com.mybatisflex.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class WithValuesDetail implements WithDetail{

    private List<Object> values;
    private QueryWrapper queryWrapper;

    public WithValuesDetail() {
    }

    public WithValuesDetail(List<Object> values, QueryWrapper queryWrapper) {
        this.values = values;
        this.queryWrapper = queryWrapper;
    }

    public QueryWrapper getQueryWrapper() {
        return queryWrapper;
    }

    public void setQueryWrapper(QueryWrapper queryWrapper) {
        this.queryWrapper = queryWrapper;
    }

    @Override
    public String toSql(IDialect dialect) {
        List<String> stringValues = new ArrayList<>(values.size());
        for (Object value : values) {
            stringValues.add(String.valueOf(value));
        }
        StringBuilder sql = new StringBuilder("VALUES (")
                .append(StringUtil.join(", ",stringValues)).append(") ");
        return sql.append(dialect.buildNoSelectSql(queryWrapper)).toString();
    }

    @Override
    public Object[] getParamValues() {
        return queryWrapper.getValueArray();
    }
}
