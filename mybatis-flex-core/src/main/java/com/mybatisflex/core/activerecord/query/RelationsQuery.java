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
import com.mybatisflex.core.query.RelationsBuilder;
import com.mybatisflex.core.util.LambdaGetter;

import java.io.Serializable;

/**
 * 使用 {@code Relations Query} 的方式进行关联查询。
 *
 * @author 王帅
 * @since 2023-07-30
 */
public class RelationsQuery<T extends Model<T>> extends RelationsBuilder<T> {

    public RelationsQuery(Model<T> model) {
        super(model);
    }

    @Override
    public RelationsQuery<T> ignoreRelations(String... fields) {
        super.ignoreRelations(fields);
        return this;
    }

    @Override
    public <L> RelationsQuery<T> ignoreRelations(LambdaGetter<L>... fields) {
        super.ignoreRelations(fields);
        return this;
    }

    @Override
    public RelationsQuery<T> maxDepth(int maxDepth) {
        super.maxDepth(maxDepth);
        return this;
    }

    @Override
    public RelationsQuery<T> extraConditionParam(String key, Object value) {
        super.extraConditionParam(key, value);
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
        return baseMapper().selectOneWithRelationsById((Serializable) pkValue());
    }

    /**
     * 根据主键查询一条数据，返回的数据为 asType 类型。
     *
     * @param asType 接收数据类型
     * @param <R>    接收数据类型
     * @return 一条数据
     */
    public <R> R oneByIdAs(Class<R> asType) {
        return baseMapper().selectOneWithRelationsByIdAs((Serializable) pkValue(), asType);
    }

}
