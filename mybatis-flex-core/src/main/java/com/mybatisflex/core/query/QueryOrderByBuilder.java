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


import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

/**
 * 排序字段构建器
 * @author michael
 */
@SuppressWarnings("unchecked")
public class QueryOrderByBuilder<Wrapper extends QueryWrapper> {

    private Wrapper queryWrapper;
    private QueryColumn orderByColumn;

    public <T> QueryOrderByBuilder(Wrapper queryWrapper, LambdaGetter<T> getter) {
        this.queryWrapper = queryWrapper;
        this.orderByColumn = LambdaUtil.getQueryColumn(getter);
    }

    public Wrapper asc(){
        return (Wrapper) queryWrapper.orderBy(orderByColumn.asc());
    }

    public Wrapper desc(){
        return (Wrapper) queryWrapper.orderBy(orderByColumn.desc());
    }
}
