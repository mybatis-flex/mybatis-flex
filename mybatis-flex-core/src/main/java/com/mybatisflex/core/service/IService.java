/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.core.service;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.SqlUtil;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * 由 Mybatis-Flex 提供的顶级增强 Service 接口。
 *
 * @param <T> 实体类（Entity）类型
 * @author 王帅
 * @since 2023-05-01
 */
@SuppressWarnings({"unused", "unchecked"})
public interface IService<T> {

    int DEFAULT_BATCH_SIZE = 1000;

    /**
     * <p>获取对应实体类（Entity）的基础映射类（BaseMapper）。
     *
     * @return 基础映射类（BaseMapper）
     */
    @NonNull BaseMapper<T> getMapper();

    //region ===== 保存（增）操作 =====

    /**
     * <p>保存实体类对象数据。
     *
     * @param entity 实体类对象
     * @return {@code true} 保存成功，{@code false} 保存失败。
     * @apiNote 默认调用的是 {@link BaseMapper#insertSelective(Object)} 方法，忽略实体类
     * {@code null} 属性的数据，使数据库配置的默认值生效。
     */
    default boolean save(@NonNull T entity) {
        return SqlUtil.toBool(getMapper().insert(entity, true));
    }

    /**
     * <p>批量保存实体类对象数据。
     *
     * @param entities 实体类对象
     * @return {@code true} 保存成功，{@code false} 保存失败。
     * @apiNote 默认调用的是 {@link BaseMapper#insertSelective(Object)} 方法，忽略实体类
     * {@code null} 属性的数据，使数据库配置的默认值生效。
     */
    default boolean saveBatch(@NonNull Collection<T> entities) {
        return saveBatch(entities, DEFAULT_BATCH_SIZE);
    }

    /**
     * <p>批量保存实体类对象数据。
     *
     * @param entities  实体类对象
     * @param batchSize 每次保存切分的数量
     * @return {@code true} 保存成功，{@code false} 保存失败。
     * @apiNote 默认调用的是 {@link BaseMapper#insertSelective(Object)} 方法，忽略实体类
     * {@code null} 属性的数据，使数据库配置的默认值生效。
     */
    default boolean saveBatch(@NonNull Collection<T> entities, int batchSize) {
        Class<BaseMapper<T>> usefulClass = (Class<BaseMapper<T>>) ClassUtil.getUsefulClass(getMapper().getClass());
        return SqlUtil.toBool(Db.executeBatch(entities, batchSize, usefulClass, BaseMapper::insertSelective));
    }

    /**
     * <p>保存或者更新实体类对象数据。
     *
     * @param entity 实体类对象
     * @return {@code true} 保存或更新成功，{@code false} 保存或更新失败。
     * @apiNote 如果实体类对象主键有值，则更新数据，若没有值，则保存数据，无论新增还是更新都会忽略实体类
     * {@code null} 属性的数据。
     */
    default boolean saveOrUpdate(@NonNull T entity) {
        return SqlUtil.toBool(getMapper().insertOrUpdate(entity, true));
    }

    /**
     * <p>保存或者更新实体类对象数据。
     *
     * @param entities 实体类对象
     * @return {@code true} 保存或更新成功，{@code false} 保存或更新失败。
     * @apiNote 如果实体类对象主键有值，则更新数据，若没有值，则保存数据，无论新增还是更新都会忽略实体类
     * {@code null} 属性的数据。
     */
    default boolean saveOrUpdateBatch(@NonNull Collection<T> entities) {
        return saveOrUpdateBatch(entities, DEFAULT_BATCH_SIZE);
    }

    /**
     * <p>保存或者更新实体类对象数据。
     *
     * @param entities  实体类对象
     * @param batchSize 每次操作切分的数量
     * @return {@code true} 保存或更新成功，{@code false} 保存或更新失败。
     * @apiNote 如果实体类对象主键有值，则更新数据，若没有值，则保存数据，无论新增还是更新都会忽略实体类
     * {@code null} 属性的数据。
     */
    default boolean saveOrUpdateBatch(@NonNull Collection<T> entities, int batchSize) {
        Class<BaseMapper<T>> usefulClass = (Class<BaseMapper<T>>) ClassUtil.getUsefulClass(getMapper().getClass());
        return SqlUtil.toBool(Db.executeBatch(entities, batchSize, usefulClass, BaseMapper::insertOrUpdateSelective));
    }
    //endregion ===== 保存（增）操作 =====

    //region ===== 删除（删）操作 =====

    /**
     * <p>根据查询条件删除数据。
     *
     * @param query 查询条件
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean remove(@NonNull QueryWrapper query) {
        return SqlUtil.toBool(getMapper().deleteByQuery(query));
    }

    /**
     * <p>根据查询条件删除数据。
     *
     * @param condition 查询条件
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean remove(@NonNull QueryCondition condition) {
        return remove(query().where(condition));
    }

    /**
     * <p>根据实体主键删除数据。
     *
     * @param entity 实体类对象
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeById(@NonNull T entity) {
        return SqlUtil.toBool(getMapper().delete(entity));
    }

    /**
     * <p>根据数据主键删除数据。
     *
     * @param id 数据主键
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeById(@NonNull Serializable id) {
        return SqlUtil.toBool(getMapper().deleteById(id));
    }

    /**
     * <p>根据数据主键批量删除数据。
     *
     * @param ids 数据主键
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeByIds(@NonNull Collection<? extends Serializable> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        return SqlUtil.toBool(getMapper().deleteBatchByIds(ids));
    }

    /**
     * <p>根据 {@link Map} 构建查询条件删除数据。
     *
     * @param query 查询条件
     * @return {@code true} 删除成功，{@code false} 删除失败。
     */
    default boolean removeByMap(@NonNull Map<String, Object> query) {
        // 防止全表删除
        if (query == null || query.isEmpty()) {
            throw FlexExceptions.wrap("deleteByMap is not allow empty map.");
        }
        return remove(query().where(query));
    }
    //endregion ===== 删除（删）操作 =====

    //region ===== 更新（改）操作 =====

    /**
     * <p>根据数据主键更新数据。
     *
     * @param entity 实体类对象
     * @return {@code true} 更新成功，{@code false} 更新失败。
     * @apiNote 若实体类属性数据为 {@code null}，该属性不会新到数据库。
     */
    default boolean updateById(@NonNull T entity) {
        return updateById(entity, true);
    }

    /**
     * 根据主键更新数据
     *
     * @param entity      实体对象
     * @param ignoreNulls 是否忽略 null 值
     * @return {@code true} 更新成功，{@code false} 更新失败。
     */
    default boolean updateById(@NonNull T entity, boolean ignoreNulls) {
        return SqlUtil.toBool(getMapper().update(entity, ignoreNulls));
    }

    /**
     * <p>根据 {@link Map} 构建查询条件更新数据。
     *
     * @param entity 实体类对象
     * @param query  查询条件
     * @return {@code true} 更新成功，{@code false} 更新失败。
     * @apiNote 若实体类属性数据为 {@code null}，该属性不会新到数据库。
     */
    default boolean update(@NonNull T entity, @NonNull Map<String, Object> query) {
        return update(entity, query().where(query));
    }

    /**
     * <p>根据查询条件更新数据。
     *
     * @param entity 实体类对象
     * @param query  查询条件
     * @return {@code true} 更新成功，{@code false} 更新失败。
     * @apiNote 若实体类属性数据为 {@code null}，该属性不会新到数据库。
     */
    default boolean update(@NonNull T entity, @NonNull QueryWrapper query) {
        return SqlUtil.toBool(getMapper().updateByQuery(entity, query));
    }

    /**
     * <p>根据查询条件更新数据。
     *
     * @param entity    实体类对象
     * @param condition 查询条件
     * @return {@code true} 更新成功，{@code false} 更新失败。
     * @apiNote 若实体类属性数据为 {@code null}，该属性不会新到数据库。
     */
    default boolean update(@NonNull T entity, @NonNull QueryCondition condition) {
        return update(entity, query().where(condition));
    }

    /**
     * <p>根据数据主键批量更新数据
     *
     * @param entities 实体类对象集合
     * @return boolean {@code true} 更新成功，{@code false} 更新失败。
     * @apiNote 若实体类属性数据为 {@code null}，该属性不会新到数据库。
     */
    default boolean updateBatch(@NonNull Collection<T> entities) {
        return updateBatch(entities, DEFAULT_BATCH_SIZE);
    }


    /**
     * <p>根据数据主键批量更新数据
     *
     * @param entities    实体类对象集合
     * @param ignoreNulls 是否忽略空字段
     *                    {@code true} 表示忽略实体类中为 {@code null} 的字段，不更新这些字段。
     *                    {@code false} 表示不忽略空字段，允许将对应字段更新为 {@code null}。
     * @return boolean {@code true} 更新成功，{@code false} 更新失败。
     * @apiNote 若 {@code ignoreNulls} 为 {@code true}，实体类中为 {@code null} 的属性不会更新到数据库。
     */
    default boolean updateBatch(@NonNull Collection<T> entities, boolean ignoreNulls) {
        return updateBatch(entities, DEFAULT_BATCH_SIZE, ignoreNulls);
    }

    /**
     * <p>根据数据主键批量更新数据
     *
     * @param entities  实体类对象集合
     * @param batchSize 每批次更新数量
     * @return {@code true} 更新成功，{@code false} 更新失败。
     * @apiNote 若实体类属性数据为 {@code null}，该属性不会新到数据库。
     */
    default boolean updateBatch(@NonNull Collection<T> entities, int batchSize) {
        Class<BaseMapper<T>> usefulClass = (Class<BaseMapper<T>>) ClassUtil.getUsefulClass(getMapper().getClass());
        return SqlUtil.toBool(Db.executeBatch(entities, batchSize, usefulClass, BaseMapper::update));
    }


    /**
     * <p>根据数据主键批量更新数据
     *
     * @param entities    实体类对象集合
     * @param batchSize   每批次更新数量
     * @param ignoreNulls 是否忽略空字段
     *                    {@code true} 表示忽略实体类中为 {@code null} 的字段，不更新这些字段。
     *                    {@code false} 表示不忽略空字段，允许将对应字段更新为 {@code null}。
     * @return {@code true} 更新成功，{@code false} 更新失败。
     * @apiNote 若 {@code ignoreNulls} 为 {@code true}，实体类中为 {@code null} 的属性不会更新到数据库。
     */
    default boolean updateBatch(@NonNull Collection<T> entities, int batchSize, boolean ignoreNulls) {
        Class<BaseMapper<T>> usefulClass = (Class<BaseMapper<T>>) ClassUtil.getUsefulClass(getMapper().getClass());
        return SqlUtil.toBool(Db.executeBatch(entities, batchSize, usefulClass, (mapper, entity) -> mapper.update(entity, ignoreNulls)));
    }
    //endregion ===== 更新（改）操作 =====

    //region ===== 查询（查）操作 =====

    /**
     * <p>根据数据主键查询一条数据。
     *
     * @param id 数据主键
     * @return 查询结果数据
     */
    default @Nullable T getById(@NonNull Serializable id) {
        return getMapper().selectOneById(id);
    }

    /**
     * <p>根据实体主键查询数据。
     *
     * @param entity 实体对象，必须包含有主键
     * @return 查询结果数据
     */
    default @Nullable T getOneByEntityId(@NonNull T entity) {
        return getMapper().selectOneByEntityId(entity);
    }

    /**
     * <p>根据实体主键查询数据。
     *
     * @param entity 实体对象，必须包含有主键
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default Optional<T> getByEntityIdOpt(@NonNull T entity) {
        return Optional.ofNullable(getOneByEntityId(entity));
    }

    /**
     * <p>根据数据主键查询一条数据。
     *
     * @param id 数据主键
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default Optional<T> getByIdOpt(@NonNull Serializable id) {
        return Optional.ofNullable(getById(id));
    }

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param query 查询条件
     * @return 查询结果数据
     */
    default @Nullable T getOne(@NonNull QueryWrapper query) {
        return getMapper().selectOneByQuery(query);
    }

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param query 查询条件
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default Optional<T> getOneOpt(@NonNull QueryWrapper query) {
        return Optional.ofNullable(getOne(query));
    }

    /**
     * <p>根据查询条件查询一条数据，并通过 asType 进行接收。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 查询结果数据
     */
    default <R> @Nullable R getOneAs(@NonNull QueryWrapper query, @NonNull Class<R> asType) {
        return getMapper().selectOneByQueryAs(query, asType);
    }

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default <R> Optional<R> getOneAsOpt(@NonNull QueryWrapper query, @NonNull Class<R> asType) {
        return Optional.ofNullable(getOneAs(query, asType));
    }

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param condition 查询条件
     * @return 查询结果数据
     */
    default @Nullable T getOne(@NonNull QueryCondition condition) {
        return getOne(query().where(condition).limit(1));
    }

    /**
     * <p>根据查询条件查询一条数据。
     *
     * @param condition 查询条件
     * @return 查询结果数据
     * @apiNote 该方法会将查询结果封装为 {@link Optional} 类进行返回，方便链式操作。
     */
    default Optional<T> getOneOpt(@NonNull QueryCondition condition) {
        return Optional.ofNullable(getOne(condition));
    }

    /**
     * <p>查询结果集中第一列，且第一条数据。
     *
     * @param query 查询条件
     * @return 数据值
     */
    default @Nullable Object getObj(@NonNull QueryWrapper query) {
        return getMapper().selectObjectByQuery(query);
    }

    /**
     * <p>查询结果集中第一列，且第一条数据，并封装为 {@link Optional} 返回。
     *
     * @param query 查询条件
     * @return 数据值
     */
    default Optional<Object> getObjOpt(@NonNull QueryWrapper query) {
        return Optional.ofNullable(getObj(query));
    }

    /**
     * <p>查询结果集中第一列，且第一条数据，并转换为指定类型，比如 {@code Long}, {@code String} 等。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 数据值
     */
    default <R> @Nullable R getObjAs(@NonNull QueryWrapper query, @NonNull Class<R> asType) {
        return getMapper().selectObjectByQueryAs(query, asType);
    }

    /**
     * <p>查询结果集中第一列，且第一条数据，并转换为指定类型，比如 {@code Long}, {@code String}
     * 等，封装为 {@link Optional} 返回。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 数据值
     */
    default <R> Optional<R> getObjAsOpt(@NonNull QueryWrapper query, @NonNull Class<R> asType) {
        return Optional.ofNullable(getObjAs(query, asType));
    }

    /**
     * <p>查询结果集中第一列所有数据。
     *
     * @param query 查询条件
     * @return 数据列表
     */
    default List<Object> objList(@NonNull QueryWrapper query) {
        return getMapper().selectObjectListByQuery(query);
    }

    /**
     * <p>查询结果集中第一列所有数据，并转换为指定类型，比如 {@code Long}, {@code String} 等。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 数据列表
     */
    default <R> List<R> objListAs(@NonNull QueryWrapper query, @NonNull Class<R> asType) {
        return getMapper().selectObjectListByQueryAs(query, asType);
    }

    /**
     * <p>查询所有数据。
     *
     * @return 所有数据
     */
    default List<T> list() {
        return list(query());
    }

    /**
     * <p>根据查询条件查询数据集合。
     *
     * @param query 查询条件
     * @return 数据集合
     */
    default List<T> list(@NonNull QueryWrapper query) {
        return getMapper().selectListByQuery(query);
    }

    /**
     * <p>根据查询条件查询数据集合。
     *
     * @param condition 查询条件
     * @return 数据集合
     */
    default List<T> list(@NonNull QueryCondition condition) {
        return list(query().where(condition));
    }

    /**
     * <p>根据查询条件查询数据集合，并通过 asType 进行接收。
     *
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 数据集合
     */
    default <R> List<R> listAs(@NonNull QueryWrapper query, @NonNull Class<R> asType) {
        return getMapper().selectListByQueryAs(query, asType);
    }

    /**
     * <p>根据数据主键查询数据集合。
     *
     * @param ids 数据主键
     * @return 数据集合
     */
    default List<T> listByIds(@NonNull Collection<? extends Serializable> ids) {
        return getMapper().selectListByIds(ids);
    }

    /**
     * <p>根据 {@link Map} 构建查询条件查询数据集合。
     *
     * @param query 查询条件
     * @return 数据集合
     */
    default List<T> listByMap(@NonNull Map<String, Object> query) {
        return list(query().where(query));
    }
    //endregion ===== 查询（查）操作 =====

    //region ===== 数量查询操作 =====

    /**
     * <p>根据查询条件判断数据是否存在。
     *
     * @param query 查询条件
     * @return {@code true} 数据存在，{@code false} 数据不存在。
     */
    default boolean exists(@NonNull QueryWrapper query) {
        return exists(CPI.getWhereQueryCondition(query));
    }

    /**
     * <p>根据查询条件判断数据是否存在。
     *
     * @param condition 查询条件
     * @return {@code true} 数据存在，{@code false} 数据不存在。
     */
    default boolean exists(@NonNull QueryCondition condition) {
        // 根据查询条件构建 SQL 语句
        // SELECT 1 FROM table WHERE ... LIMIT 1
        QueryWrapper queryWrapper = QueryMethods.selectOne()
            .where(condition)
            .limit(1);
        // 获取数据集合，空集合：[] 不存在数据，有一个元素的集合：[1] 存在数据
        List<Object> objects = getMapper().selectObjectListByQuery(queryWrapper);
        // 判断是否存在数据
        return CollectionUtil.isNotEmpty(objects);
    }

    /**
     * <p>查询所有数据数量。
     *
     * @return 所有数据数量
     */
    default long count() {
        return count(query());
    }

    /**
     * <p>根据查询条件查询数据数量。
     *
     * @param query 查询条件
     * @return 数据数量
     */
    default long count(@NonNull QueryWrapper query) {
        return getMapper().selectCountByQuery(query);
    }

    /**
     * <p>根据查询条件查询数据数量。
     *
     * @param condition 查询条件
     * @return 数据数量
     */
    default long count(@NonNull QueryCondition condition) {
        return count(query().where(condition));
    }
    //endregion ===== 数量查询操作 =====

    //region ===== 分页查询操作 =====

    /**
     * <p>分页查询所有数据。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    default Page<T> page(Page<T> page) {
        return page(page, query());
    }

    /**
     * <p>根据查询条件分页查询数据。
     *
     * @param page  分页对象
     * @param query 查询条件
     * @return 分页对象
     */
    default Page<T> page(@NonNull Page<T> page, @NonNull QueryWrapper query) {
        return pageAs(page, query, null);
    }

    /**
     * <p>根据查询条件分页查询数据。
     *
     * @param page      分页对象
     * @param condition 查询条件
     * @return 分页对象
     */
    default Page<T> page(@NonNull Page<T> page, @NonNull QueryCondition condition) {
        return page(page, query().where(condition));
    }

    /**
     * <p>根据查询条件分页查询数据，并通过 asType 进行接收。
     *
     * @param page   分页对象
     * @param query  查询条件
     * @param asType 接收的数据类型
     * @return 分页对象
     */
    default <R> Page<R> pageAs(@NonNull Page<R> page, @NonNull QueryWrapper query, @Nullable Class<R> asType) {
        return getMapper().paginateAs(page, query, asType);
    }
    //endregion ===== 分页查询操作 =====

    //region ===== 查询包装器操作 =====

    /**
     * 默认 {@link QueryWrapper} 构建。
     *
     * @return {@link QueryWrapper} 对象
     */
    default QueryWrapper query() {
        return QueryWrapper.create();
    }

    /**
     * 链式查询。
     *
     * @return {@link QueryChain} 对象
     */
    default QueryChain<T> queryChain() {
        return QueryChain.of(getMapper());
    }

    /**
     * 链式更新。
     *
     * @return {@link UpdateChain} 对象
     */
    default UpdateChain<T> updateChain() {
        return UpdateChain.create(getMapper());
    }
    //endregion ===== 查询包装器操作 =====
}
