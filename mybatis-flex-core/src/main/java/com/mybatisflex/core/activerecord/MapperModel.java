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

package com.mybatisflex.core.activerecord;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.mybatis.Mappers;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.SqlUtil;

import java.io.Serializable;
import java.util.Optional;

/**
 * <p>使用 {@link BaseMapper} 进行 CRUD 操作的实体类的抽象接口。
 *
 * <p>使用接口是为了方便拓展，该接口提供了简单的根据 <b>主键</b> 操作数据的方法，
 * 实现类可以进行其他方法的扩展。
 *
 * @param <T> 实体类类型
 * @author 王帅
 * @since 2023-07-23
 */
@SuppressWarnings({"unused", "unchecked"})
public interface MapperModel<T> {

    /**
     * 获取实体类对应的 {@link BaseMapper} 接口。
     *
     * @return {@link BaseMapper} 接口
     */
    default BaseMapper<T> baseMapper() {
        return Mappers.ofEntityClass((Class<T>) getClass());
    }

    /**
     * <p>获取实体类主键数据。
     *
     * <p>可以拓展该方法提高效率，例如：
     * <pre>{@code
     * return new Object[]{id};
     * }</pre>
     *
     * @return 主键数据数组
     */
    default Object pkValue() {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(getClass());
        return tableInfo.getPkValue(this);
    }

    /**
     * 保存数据（自动忽略 {@code null} 值）。
     *
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    default boolean save() {
        return save(true);
    }

    /**
     * 保存数据（自动忽略 {@code null} 值），结果使用 {@link Optional}
     * 返回源对象回调，保存成功返回 {@code Optional.of(this)}，保存失败返回
     * {@code Optional.empty()}。
     *
     * @return {@link Optional} 链式调用
     */
    default Optional<T> saveOpt() {
        return saveOpt(true);
    }

    /**
     * 保存数据，并设置是否忽略 {@code null} 值。
     *
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    default boolean save(boolean ignoreNulls) {
        return SqlUtil.toBool(baseMapper().insert((T) this, ignoreNulls));
    }

    /**
     * 保存数据，并设置是否忽略 {@code null} 值，结果使用 {@link Optional}
     * 返回源对象回调，保存成功返回 {@code Optional.of(this)}，保存失败返回
     * {@code Optional.empty()}。
     *
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return {@link Optional} 链式调用
     */
    default Optional<T> saveOpt(boolean ignoreNulls) {
        return save(ignoreNulls) ? Optional.of((T) this) : Optional.empty();
    }

    /**
     * 保存或者更新数据，如果实体类主键没有值，则 <b>保存</b> 数据；如果实体类主键有值，则
     * <b>更新</b> 数据（全部自动忽略 {@code null} 值）。
     *
     * @return {@code true} 保存或更新成功，{@code false} 保存或更新失败
     */
    default boolean saveOrUpdate() {
        return saveOrUpdate(true);
    }

    /**
     * 保存或者更新数据，如果实体类主键没有值，则 <b>保存</b> 数据；如果实体类主键有值，则
     * <b>更新</b> 数据（全部自动忽略 {@code null} 值），结果使用 {@link Optional}
     * 返回源对象回调，保存或更新成功返回 {@code Optional.of(this)}，保存或更新失败返回
     * {@code Optional.empty()}。
     *
     * @return {@link Optional} 链式调用
     */
    default Optional<T> saveOrUpdateOpt() {
        return saveOrUpdateOpt(true);
    }

    /**
     * 保存或者更新数据，如果实体类主键没有值，则 <b>保存</b> 数据；如果实体类主键有值，则
     * <b>更新</b> 数据，并设置是否忽略 {@code null} 值。
     *
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return {@code true} 保存或更新成功，{@code false} 保存或更新失败
     */
    default boolean saveOrUpdate(boolean ignoreNulls) {
        return SqlUtil.toBool(baseMapper().insertOrUpdate((T) this, ignoreNulls));
    }

    /**
     * 保存或者更新数据，如果实体类主键没有值，则 <b>保存</b> 数据；如果实体类主键有值，则
     * <b>更新</b> 数据，并设置是否忽略 {@code null} 值，结果使用 {@link Optional}
     * 返回源对象回调，保存或更新成功返回 {@code Optional.of(this)}，保存或更新失败返回
     * {@code Optional.empty()}。
     *
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return {@link Optional} 链式调用
     */
    default Optional<T> saveOrUpdateOpt(boolean ignoreNulls) {
        return saveOrUpdate(ignoreNulls) ? Optional.of((T) this) : Optional.empty();
    }

    /**
     * 根据实体类主键删除数据。
     *
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    default boolean removeById() {
        return SqlUtil.toBool(baseMapper().deleteById((Serializable) pkValue()));
    }

    /**
     * 根据实体类主键删除数据，结果使用 {@link Optional} 返回源对象回调，删除成功返回
     * {@code Optional.of(this)}，删除失败返回 {@code Optional.empty()}。
     *
     * @return {@link Optional} 链式调用
     */
    default Optional<T> removeByIdOpt() {
        return removeById() ? Optional.of((T) this) : Optional.empty();
    }

    /**
     * 根据实体类主键更新数据（自动忽略 {@code null} 值）。
     *
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    default boolean updateById() {
        return updateById(true);
    }

    /**
     * 根据实体类主键更新数据（自动忽略 {@code null} 值），结果使用 {@link Optional}
     * 返回源对象回调，更新成功返回 {@code Optional.of(this)}，更新失败返回
     * {@code Optional.empty()}。
     *
     * @return {@link Optional} 链式调用
     */
    default Optional<T> updateByIdOpt() {
        return updateByIdOpt(true);
    }

    /**
     * 根据实体类主键更新数据，并设置是否忽略 {@code null} 值。
     *
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    default boolean updateById(boolean ignoreNulls) {
        return SqlUtil.toBool(baseMapper().update((T) this, ignoreNulls));
    }

    /**
     * 根据实体类主键更新数据，并设置是否忽略 {@code null} 值，结果使用 {@link Optional}
     * 返回源对象回调，更新成功返回 {@code Optional.of(this)}，更新失败返回
     * {@code Optional.empty()}。
     *
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return {@link Optional} 链式调用
     */
    default Optional<T> updateByIdOpt(boolean ignoreNulls) {
        return updateById(ignoreNulls) ? Optional.of((T) this) : Optional.empty();
    }

    /**
     * 根据实体类主键获取一条数据。
     *
     * @return 数据
     */
    default T oneById() {
        return baseMapper().selectOneById((Serializable) pkValue());
    }

    /**
     * 根据实体类主键获取一条数据，并封装为 {@link Optional} 返回。
     *
     * @return 数据
     */
    default Optional<T> oneByIdOpt() {
        return Optional.ofNullable(oneById());
    }

}
