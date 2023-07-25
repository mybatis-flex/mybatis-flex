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
package com.mybatisflex.core.activerecord.query;


import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

/**
 * Lambda 排序构建器。
 *
 * @author 王帅
 * @since 2023-07-25
 */
public class OrderByBuilder<R extends QueryModel<R>> {

    private final R queryModel;
    private final QueryColumn queryColumn;

    public <T> OrderByBuilder(R queryModel, LambdaGetter<T> getter) {
        this.queryModel = queryModel;
        this.queryColumn = LambdaUtil.getQueryColumn(getter);
    }

    public R asc() {
        return queryModel.orderBy(queryColumn.asc());
    }

    public R desc() {
        return queryModel.orderBy(queryColumn.desc());
    }

}
