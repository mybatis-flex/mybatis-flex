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

public class WithSelectDetail implements WithDetail {

    private QueryWrapper queryWrapper;

    public WithSelectDetail() {
    }

    public WithSelectDetail(QueryWrapper queryWrapper) {
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
        return dialect.buildSelectSql(queryWrapper);
    }

    @Override
    public Object[] getParamValues() {
        return queryWrapper.getAllValueArray();
    }

    @Override
    public WithSelectDetail clone() {
        try {
            WithSelectDetail clone = (WithSelectDetail) super.clone();
            // deep clone ...
            clone.queryWrapper = this.queryWrapper.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw FlexExceptions.wrap(e);
        }
    }

}
