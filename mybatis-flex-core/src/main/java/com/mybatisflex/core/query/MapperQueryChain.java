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

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;
import com.mybatisflex.core.util.SqlUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * <p>链式 {@link BaseMapper} 查询。
 *
 * <p>要求实现类除了包含有 {@link BaseMapper} 接口的引用外，还必须具有 {@link QueryWrapper}
 * 的查询条件构建功能。在使用时：
 * <ul>
 *     <li>通过 {@link #baseMapper()} 获取该实现类对应的 {@link BaseMapper} 引用。
 *     <li>通过 {@link #toQueryWrapper()} 将该实现类转换为 {@link QueryWrapper} 对象。
 * </ul>
 *
 * @param <T> 实体类类型
 * @author 王帅
 * @since 2023-08-08
 */
public interface MapperQueryChain<T> extends ChainQuery<T> {

    /**
     * 该实现类对应的 {@link BaseMapper} 对象。
     *
     * @return {@link BaseMapper}
     */
    BaseMapper<T> baseMapper();

    /**
     * 将该实现类转换为 {@link QueryWrapper} 对象。
     *
     * @return {@link QueryWrapper}
     */
    QueryWrapper toQueryWrapper();

    /**
     * 查询数据数量。
     *
     * @return 数据数量
     */
    default long count() {
        return baseMapper().selectCountByQuery(toQueryWrapper());
    }

    /**
     * 判断数据是否存在。
     *
     * @return {@code true} 数据存在，{@code false} 数据不存在
     */
    default boolean exists() {
        return SqlUtil.toBool(count());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default T one() {
        return baseMapper().selectOneByQuery(toQueryWrapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <R> R oneAs(Class<R> asType) {
        return baseMapper().selectOneByQueryAs(toQueryWrapper(), asType);
    }

    /**
     * 获取第一列，且第一条数据。
     *
     * @return 第一列数据
     */
    default Object obj() {
        return baseMapper().selectObjectByQuery(toQueryWrapper());
    }

    /**
     * 获取第一列，且第一条数据并转换为指定类型，比如 {@code Long}, {@code String} 等。
     *
     * @param asType 接收数据类型
     * @param <R>    接收数据类型
     * @return 第一列数据
     */
    default <R> R objAs(Class<R> asType) {
        return baseMapper().selectObjectByQueryAs(toQueryWrapper(), asType);
    }

    /**
     * 获取第一列，且第一条数据，并封装为 {@link Optional} 返回。
     *
     * @return 第一列数据
     */
    default Optional<Object> objOpt() {
        return Optional.ofNullable(obj());
    }

    /**
     * 获取第一列，且第一条数据并转换为指定类型，比如 {@code Long}, {@code String}
     * 等，封装为 {@link Optional} 返回。
     *
     * @param asType 接收数据类型
     * @param <R>    接收数据类型
     * @return 第一列数据
     */
    default <R> Optional<R> objAsOpt(Class<R> asType) {
        return Optional.ofNullable(objAs(asType));
    }

    /**
     * 获取第一列的所有数据。
     *
     * @return 第一列数据
     */
    default List<Object> objList() {
        return baseMapper().selectObjectListByQuery(toQueryWrapper());
    }

    /**
     * 获取第一列的所有数据，并转换为指定类型，比如 {@code Long}, {@code String} 等。
     *
     * @param asType 接收数据类型
     * @param <R>    接收数据类型
     * @return 第一列数据
     */
    default <R> List<R> objListAs(Class<R> asType) {
        return baseMapper().selectObjectListByQueryAs(toQueryWrapper(), asType);
    }

    /**
     * {@inheritDoc}
     */
    default List<T> list() {
        return baseMapper().selectListByQuery(toQueryWrapper());
    }

    /**
     * {@inheritDoc}
     */
    default <R> List<R> listAs(Class<R> asType) {
        return baseMapper().selectListByQueryAs(toQueryWrapper(), asType);
    }

    /**
     * {@inheritDoc}
     */
    default Page<T> page(Page<T> page) {
        return baseMapper().paginate(page, toQueryWrapper());
    }

    /**
     * {@inheritDoc}
     */
    default <R> Page<R> pageAs(Page<R> page, Class<R> asType) {
        return baseMapper().paginateAs(page, toQueryWrapper(), asType);
    }

    /**
     * 使用 {@code Fields Query} 的方式进行关联查询。
     *
     * @return {@code Fields Query} 查询
     */
    default FieldsBuilder<T> withFields() {
        return new FieldsBuilder<>(this);
    }

    /**
     * 使用 {@code Relations Query} 的方式进行关联查询。
     *
     * @return {@code Relations Query} 查询
     */
    default RelationsBuilder<T> withRelations() {
        return new RelationsBuilder<>(this);
    }

    /**
     * 使用 Relations Query 的方式进行关联查询。
     * @param columns 需要关联的字段
     * @return Relations Query 查询
     */
    default RelationsBuilder<T> withRelations(LambdaGetter<T>... columns) {
        if(columns != null && columns.length > 0) {
            String[] array = Arrays.stream(columns)
                .map(LambdaUtil::getFieldName)
                .toArray(String[]::new);
            RelationManager.addQueryRelations(array);
        }
        return new RelationsBuilder<>(this);
    }

}
