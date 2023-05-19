/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.service;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.RowMapper;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.SqlUtil;

import java.io.Serializable;
import java.util.*;


/**
 * 由 Mybatis-Flex 提供的顶级增强 Service 接口。
 *
 * @param <T> 实体类（Entity）类型
 * @author 王帅
 * @since 2023-05-01
 */
@SuppressWarnings("unused")
public interface IService<T> {

    // ===== 保存（增）操作 =====

    /**
     * 获取对应实体类（Entity）的基础映射类（BaseMapper）。
     *
     * @return 基础映射类（BaseMapper）
     */
    BaseMapper<T> getMapper();

    /**
     * 保存实体类对象数据。
     *
     * @param entity 实体类对象
     * @return {@code true} 保存成功，{@code false} 保存失败。
     * @apiNote 默认调用的是 {@link BaseMapper#insertSelective(Object)} 方法，忽略
     * {@code null} 字段的数据，使数据库配置的默认值生效。
     */
    default boolean save(T entity) {
        return SqlUtil.toBool(getMapper().insertSelective(entity));
    }

    /**
     * 保存或者更新实体类对象数据。
     *
     * @param entity 实体类对象
     * @return {@code true} 保存或更新成功，{@code false} 保存或更新失败。
     * @apiNote 如果实体类对象主键有值，则更新数据，若没有值，则保存数据。
     */
    default boolean saveOrUpdate(T entity) {
        return SqlUtil.toBool(getMapper().insertOrUpdate(entity));
    }

    /**
     * 批量保存实体类对象数据。
     *
     * @param entities 实体类对象
     * @return {@code true} 保存成功，{@code false} 保存失败。
     */
    default boolean saveBatch(Collection<T> entities) {
        return SqlUtil.toBool(getMapper().insertBatch(new ArrayList<>(entities)));
    }

    // ===== 删除（删）操作 =====

    /**
     * 批量保存实体类对象数据。
     *
     * @param entities 实体类对象
     * @param size     每次保存切分的数量
     * @return {@code true} 保存成功，{@code false} 保存失败。
     */
    default boolean saveBatch(Collection<T> entities, int size) {
        return SqlUtil.toBool(getMapper().insertBatch(new ArrayList<>(entities), size));
    }

    /**
     * 根据查询条件删除数据。
     *
     * @param query 查询条件
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean remove(QueryWrapper query) {
        return SqlUtil.toBool(getMapper().deleteByQuery(query));
    }

    /**
     * 根据查询条件删除数据。
     *
     * @param condition 查询条件
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean remove(QueryCondition condition) {
        return SqlUtil.toBool(getMapper().deleteByCondition(condition));
    }

    /**
     * 根据数据主键删除数据。
     *
     * @param id 数据主键
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeById(Serializable id) {
        return SqlUtil.toBool(getMapper().deleteById(id));
    }

    /**
     * 根据数据主键批量删除数据。
     *
     * @param ids 数据主键
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeByIds(Collection<? extends Serializable> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        return SqlUtil.toBool(getMapper().deleteBatchByIds(ids));
    }

    // ===== 更新（改）操作 =====

    /**
     * 根据 {@link Map} 构建查询条件删除数据。
     *
     * @param query 查询条件
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeByMap(Map<String, Object> query) {
        return SqlUtil.toBool(getMapper().deleteByMap(query));
    }

    /**
     * 根据查询条件更新数据。
     *
     * @param entity 实体类对象
     * @param query  查询条件
     * @return {@code true} 更新成功，{@code false} 更新失败。
     */
    default boolean update(T entity, QueryWrapper query) {
        return SqlUtil.toBool(getMapper().updateByQuery(entity, query));
    }

    /**
     * 根据查询条件更新数据。
     *
     * @param entity    实体类对象
     * @param condition 查询条件
     * @return {@code true} 更新成功，{@code false} 更新失败。
     */
    default boolean update(T entity, QueryCondition condition) {
        return SqlUtil.toBool(getMapper().updateByCondition(entity, condition));
    }

    /**
     * 根据 id 批量更新数据
     *
     * @param entities 实体类对象集合
     * @return boolean {@code true} 更新成功，{@code false} 更新失败。
     */
    default boolean updateBatch(Collection<T> entities) {
        return updateBatch(entities, RowMapper.DEFAULT_BATCH_SIZE);
    }

    /**
     * 根据 id 批量更新数据
     *
     * @param entities  实体类对象集合
     * @param batchSize 每批次更新数量
     * @return boolean {@code true} 更新成功，{@code false} 更新失败。
     */
    @SuppressWarnings("unchecked")
    default boolean updateBatch(Collection<T> entities, int batchSize) {
        return Db.tx(() -> {
            final List<T> entityList = CollectionUtil.toList(entities);

            // BaseMapper 是经过 Mybatis 动态代理处理过的对象，需要获取原始 BaseMapper 类型
            final Class<BaseMapper<T>> usefulClass = (Class<BaseMapper<T>>) ClassUtil.getUsefulClass(getMapper().getClass());
            return SqlUtil.toBool(Arrays.stream(Db.executeBatch(entityList.size(), batchSize, usefulClass, (mapper, index) -> mapper.update(entityList.get(index)))).sum());
        });
    }

    /**
     * 根据数据主键更新数据。
     *
     * @param entity 实体类对象
     * @return {@code true} 更新成功，{@code false} 更新失败。
     */
    default boolean updateById(T entity) {
        return SqlUtil.toBool(getMapper().update(entity));
    }

    /**
     * 根据 {@link Map} 构建查询条件更新数据。
     *
     * @param entity 实体类对象
     * @param query  查询条件
     * @return {@code true} 更新成功，{@code false} 更新失败。
     */
    default boolean updateByMap(T entity, Map<String, Object> query) {
        return SqlUtil.toBool(getMapper().updateByMap(entity, query));
    }

    // ===== 查询（查）操作 =====

    /**
     * 根据数据主键查询一条数据。
     *
     * @param id 数据主键
     * @return 查询结果数据
     */
    default T getById(Serializable id) {
        return getMapper().selectOneById(id);
    }

    /**
     * 根据数据主键查询一条数据。
     *
     * @param id 数据主键
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default Optional<T> getByIdOpt(Serializable id) {
        return Optional.ofNullable(getById(id));
    }

    /**
     * 根据查询条件查询一条数据。
     *
     * @param query 查询条件
     * @return 查询结果数据
     */
    default T getOne(QueryWrapper query) {
        return getMapper().selectOneByQuery(query);
    }


    /**
     * 根据查询条件查询一条数据，并通过 asType 进行接收
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 查询结果数据
     */
    default <R> R getOneAs(QueryWrapper query, Class<R> asType) {
        return getMapper().selectOneByQueryAs(query, asType);
    }

    /**
     * 根据查询条件查询一条数据。
     *
     * @param query 查询条件
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default Optional<T> getOneOpt(QueryWrapper query) {
        return Optional.ofNullable(getOne(query));
    }


    /**
     * 根据查询条件查询一条数据。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default <R> Optional<R> getOneOptAs(QueryWrapper query, Class<R> asType) {
        return Optional.ofNullable(getOneAs(query, asType));
    }

    /**
     * 根据查询条件查询一条数据。
     *
     * @param condition 查询条件
     * @return 查询结果数据
     */
    default T getOne(QueryCondition condition) {
        return getMapper().selectOneByCondition(condition);
    }

    /**
     * 根据查询条件查询一条数据。
     *
     * @param condition 查询条件
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default Optional<T> getOneOpt(QueryCondition condition) {
        return Optional.ofNullable(getOne(condition));
    }

    /**
     * 查询所有数据。
     *
     * @return 所有数据
     */
    default List<T> list() {
        return getMapper().selectAll();
    }

    /**
     * 根据查询条件查询数据集合。
     *
     * @param condition 查询条件
     * @return 数据集合
     */
    default List<T> list(QueryCondition condition) {
        return getMapper().selectListByCondition(condition);
    }

    /**
     * 根据查询条件查询数据集合。
     *
     * @param condition 查询条件
     * @return 数据集合
     */
    default List<T> list(QueryCondition condition, int count) {
        return getMapper().selectListByCondition(condition, count);
    }


    /**
     * 根据查询条件查询数据集合。
     *
     * @param query 查询条件
     * @return 数据集合
     */
    default List<T> list(QueryWrapper query) {
        return getMapper().selectListByQuery(query);
    }

    /**
     * 根据查询条件查询数据集合，并通过 asType 进行接收
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 数据集合
     */
    default <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        return getMapper().selectListByQueryAs(query, asType);
    }


    /**
     * 根据数据主键查询数据集合。
     *
     * @param ids 数据主键
     * @return 数据集合
     */
    default List<T> listByIds(Collection<? extends Serializable> ids) {
        return getMapper().selectListByIds(ids);
    }

    /**
     * 根据 {@link Map} 构建查询条件查询数据集合。
     *
     * @param query 查询条件
     * @return 数据集合
     */
    default List<T> listByMap(Map<String, Object> query) {
        return getMapper().selectListByMap(query);
    }

    // ===== 数量查询操作 =====

    /**
     * 根据查询条件判断数据是否存在。
     *
     * @param query 查询条件
     * @return {@code true} 数据存在，{@code false} 数据不存在。
     */
    default boolean exists(QueryWrapper query) {
        return SqlUtil.toBool(count(query));
    }

    /**
     * 根据查询条件判断数据是否存在。
     *
     * @param condition 查询条件
     * @return {@code true} 数据存在，{@code false} 数据不存在。
     */
    default boolean exists(QueryCondition condition) {
        return SqlUtil.toBool(count(condition));
    }

    /**
     * 查询所有数据数量。
     *
     * @return 所有数据数量
     */
    default long count() {
        return getMapper().selectCountByQuery(QueryWrapper.create());
    }

    /**
     * 根据查询条件查询数据数量。
     *
     * @param query 查询条件
     * @return 数据数量
     */
    default long count(QueryWrapper query) {
        return getMapper().selectCountByQuery(query);
    }

    /**
     * 根据查询条件查询数据数量。
     *
     * @param condition 查询条件
     * @return 数据数量
     */
    default long count(QueryCondition condition) {
        return getMapper().selectCountByCondition(condition);
    }

    // ===== 分页查询操作 =====

    /**
     * 分页查询所有数据。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    default Page<T> page(Page<T> page) {
        return getMapper().paginate(page, QueryWrapper.create());
    }

    /**
     * 根据查询条件分页查询数据。
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页对象
     */
    default Page<T> page(Page<T> page, QueryWrapper query) {
        return getMapper().paginate(page, query);
    }

    /**
     * 根据查询条件分页查询数据。
     *
     * @param page      分页对象
     * @param condition 查询条件
     * @return 分页对象
     */
    default Page<T> page(Page<T> page, QueryCondition condition) {
        return getMapper().paginate(page, QueryWrapper.create().where(condition));
    }

}