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

import com.mybatisflex.core.activerecord.Model;
import com.mybatisflex.core.field.FieldQueryManager;
import com.mybatisflex.core.field.QueryBuilder;
import com.mybatisflex.core.mybatis.MappedStatementTypes;
import com.mybatisflex.core.query.FieldsBuilder;
import com.mybatisflex.core.util.LambdaGetter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 使用 {@code Fields Query} 的方式进行关联查询。
 *
 * @author 王帅
 * @since 2023-07-30
 */
public class FieldsQuery<T extends Model<T>> extends FieldsBuilder<T> {

    public FieldsQuery(Model<T> model) {
        super(model);
    }

    @Override
    public <F> FieldsQuery<T> fieldMapping(LambdaGetter<F> field, QueryBuilder<F> builder) {
        super.fieldMapping(field, builder);
        return this;
    }

    @Override
    public <F> FieldsBuilder<T> fieldMapping(LambdaGetter<F> field, boolean prevent, QueryBuilder<F> builder) {
        super.fieldMapping(field, prevent, builder);
        return this;
    }

    protected Object pkValue() {
        // 懒加载，实际用到的时候才会生成 主键值
        return ((Model<T>) delegate).pkValue();
    }

    /**
     * 根据主键查询一条数据。
     *
     * @return 一条数据
     */
    public T oneById() {
        List<T> entities = Collections.singletonList(baseMapper().selectOneById((Serializable) pkValue()));
        FieldQueryManager.queryFields(baseMapper(), entities, fieldQueryMap);
        return entities.get(0);
    }

    /**
     * 根据主键查询一条数据，返回的数据为 asType 类型。
     *
     * @param asType 接收数据类型
     * @param <R>    接收数据类型
     * @return 一条数据
     */
    @SuppressWarnings("unchecked")
    public <R> R oneByIdAs(Class<R> asType) {
        try {
            MappedStatementTypes.setCurrentType(asType);
            List<R> entities = Collections.singletonList((R) baseMapper().selectOneById((Serializable) pkValue()));
            FieldQueryManager.queryFields(baseMapper(), entities, fieldQueryMap);
            return entities.get(0);
        } finally {
            MappedStatementTypes.clear();
        }
    }

}
