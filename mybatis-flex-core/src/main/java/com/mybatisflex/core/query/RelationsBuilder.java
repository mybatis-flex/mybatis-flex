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

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.util.LambdaGetter;

import java.util.List;

/**
 * 使用 {@code Relations Query} 的方式进行关联查询。
 *
 * @author 王帅
 * @since 2023-08-08
 */
public class RelationsBuilder<T> extends AbstractQueryBuilder<T> {

    public RelationsBuilder(MapperQueryChain<T> delegate) {
        super(delegate);
    }

    /**
     * 忽略查询部分 {@code Relations} 注解标记的属性。
     *
     * @param fields 属性
     * @return {@code Relations} 查询构建
     */
    public RelationsBuilder<T> ignoreRelations(String... fields) {
        RelationManager.addIgnoreRelations(fields);
        return this;
    }

    /**
     * 忽略查询部分 {@code Relations} 注解标记的属性。
     *
     * @param fields 属性
     * @return {@code Relations} 查询构建
     */
    public <L> RelationsBuilder<T> ignoreRelations(LambdaGetter<L>... fields) {
        RelationManager.addIgnoreRelations(fields);
        return this;
    }

    /**
     * 设置父子关系查询中，默认的递归查询深度。
     *
     * @param maxDepth 查询深度
     * @return {@code Relations} 查询构建
     */
    public RelationsBuilder<T> maxDepth(int maxDepth) {
        RelationManager.setMaxDepth(maxDepth);
        return this;
    }

    /**
     * 添加额外的 {@code Relations} 查询条件。
     *
     * @param key   键
     * @param value 值
     * @return {@code Relations} 查询构建
     */
    public RelationsBuilder<T> extraConditionParam(String key, Object value) {
        RelationManager.addExtraConditionParam(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T one() {
        return baseMapper().selectOneWithRelationsByQuery(queryWrapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R oneAs(Class<R> asType) {
        return baseMapper().selectOneWithRelationsByQueryAs(queryWrapper(), asType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> list() {
        return baseMapper().selectListWithRelationsByQuery(queryWrapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> List<R> listAs(Class<R> asType) {
        return baseMapper().selectListWithRelationsByQueryAs(queryWrapper(), asType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> page(Page<T> page) {
        return baseMapper().paginateWithRelations(page, queryWrapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Page<R> pageAs(Page<R> page, Class<R> asType) {
        return baseMapper().paginateWithRelationsAs(page, queryWrapper(), asType);
    }

}
