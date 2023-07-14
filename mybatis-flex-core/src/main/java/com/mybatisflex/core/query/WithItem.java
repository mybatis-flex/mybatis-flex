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
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.ObjectUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.List;

import static com.mybatisflex.core.constant.SqlConsts.*;

public class WithItem implements CloneSupport<WithItem> {

    private String name;
    private List<String> params;

    private WithDetail withDetail;

    public WithItem() {
    }

    public WithItem(String name, List<String> params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public WithDetail getWithDetail() {
        return withDetail;
    }

    public void setWithDetail(WithDetail withDetail) {
        this.withDetail = withDetail;
    }

    public String toSql(IDialect dialect) {
        StringBuilder sql = new StringBuilder(name);
        if (CollectionUtil.isNotEmpty(params)) {
            sql.append(BRACKET_LEFT).append(StringUtil.join(DELIMITER, params)).append(BRACKET_RIGHT);
        }
        sql.append(AS).append(BRACKET_LEFT);
        sql.append(withDetail.toSql(dialect));
        return sql.append(BRACKET_RIGHT).toString();
    }

    public Object[] getParamValues() {
        return withDetail.getParamValues();
    }

    @Override
    public WithItem clone() {
        try {
            WithItem clone = (WithItem) super.clone();
            // deep clone ...
            clone.withDetail = ObjectUtil.clone(this.withDetail);
            clone.params = CollectionUtil.newArrayList(this.params);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }

}
