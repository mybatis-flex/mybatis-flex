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

import com.mybatisflex.core.activerecord.query.FieldsQuery;
import com.mybatisflex.core.activerecord.query.QueryModel;
import com.mybatisflex.core.activerecord.query.RelationsQuery;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.util.SqlUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Active Record 模型。
 *
 * @param <T> 实体类类型
 * @author 王帅
 * @since 2023-07-24
 */
@SuppressWarnings({"unused", "unchecked"})
public abstract class Model<T extends Model<T>>
    extends QueryModel<T>
    implements MapperModel<T>, Serializable {

    /**
     * 根据实体类构建的条件删除数据。
     *
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    public boolean remove() {
        return SqlUtil.toBool(baseMapper().deleteByQuery(queryWrapper()));
    }

    /**
     * 根据实体类构建的条件更新数据（自动忽略 {@code null} 值）。
     *
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    public boolean update() {
        return update(true);
    }

    /**
     * 根据实体类构建的条件更新数据，并设置是否忽略 {@code null} 值。
     *
     * @param ignoreNulls 是否忽略 {@code null} 值
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    public boolean update(boolean ignoreNulls) {
        return SqlUtil.toBool(baseMapper().updateByQuery((T) this, ignoreNulls, queryWrapper()));
    }

    /**
     * 根据实体类构建的条件查询数据数量。
     *
     * @return 数据数量
     */
    public long count() {
        return baseMapper().selectCountByQuery(queryWrapper());
    }

    /**
     * 根据实体类构建的条件判断数据是否存在。
     *
     * @return {@code true} 数据存在，{@code false} 数据不存在
     */
    public boolean exists() {
        return SqlUtil.toBool(count());
    }

    /**
     * 根据实体类构建的条件获取一条数据。
     *
     * @return 数据
     */
    public T one() {
        return baseMapper().selectOneByQuery(queryWrapper());
    }

    /**
     * 根据实体类构建的条件获取一条数据，返回的数据为 asType 类型。
     *
     * @param asType 接收数据类型
     * @return 数据
     */
    public <R> R oneAs(Class<R> asType) {
        return baseMapper().selectOneByQueryAs(queryWrapper(), asType);
    }


    /**
     * 根据实体类构建的条件获取一条数据，并封装为 {@link Optional} 返回。
     *
     * @return 数据
     */
    public Optional<T> oneOpt() {
        return Optional.ofNullable(one());
    }

    /**
     * 根据实体类构建的条件获取一条数据，返回的数据为 asType 类型，并封装为 {@link Optional} 返回。
     *
     * @param asType 接收数据类型
     * @return 数据
     */
    public <R> Optional<R> oneAsOpt(Class<R> asType) {
        return Optional.ofNullable(oneAs(asType));
    }

    /**
     * 根据实体类构建的条件获取第一列，且第一条数据。
     *
     * @return 第一列数据
     */
    public Object obj() {
        return baseMapper().selectObjectByQuery(queryWrapper());
    }

    /**
     * 根据实体类构建的条件获取第一列，且第一条数据并转换为指定类型，比如 {@code Long}, {@code String} 等。
     *
     * @param asType 接收数据类型
     * @return 第一列数据
     */
    public <R> R objAs(Class<R> asType) {
        return baseMapper().selectObjectByQueryAs(queryWrapper(), asType);
    }

    /**
     * 根据实体类构建的条件获取第一列，且第一条数据，并封装为 {@link Optional} 返回。
     *
     * @return 第一列数据
     */
    public Optional<Object> objOpt() {
        return Optional.ofNullable(obj());
    }

    /**
     * 根据实体类构建的条件获取第一列，且第一条数据并转换为指定类型，比如 {@code Long}, {@code String}
     * 等，封装为 {@link Optional} 返回。
     *
     * @param asType 接收数据类型
     * @return 第一列数据
     */
    public <R> Optional<R> objAsOpt(Class<R> asType) {
        return Optional.ofNullable(objAs(asType));
    }

    /**
     * 根据实体类构建的条件获取第一列的所有数据。
     *
     * @return 第一列数据
     */
    public List<Object> objList() {
        return baseMapper().selectObjectListByQuery(queryWrapper());
    }

    /**
     * 根据实体类构建的条件获取第一列的所有数据，并转换为指定类型，比如 {@code Long}, {@code String} 等。
     *
     * @param asType 接收数据类型
     * @return 第一列数据
     */
    public <R> List<R> objListAs(Class<R> asType) {
        return baseMapper().selectObjectListByQueryAs(queryWrapper(), asType);
    }

    /**
     * 根据实体类构建的条件获取多条数据。
     *
     * @return 数据列表
     */
    public List<T> list() {
        return baseMapper().selectListByQuery(queryWrapper());
    }

    /**
     * 根据实体类构建的条件获取多条数据，返回的数据为 asType 类型。
     *
     * @param asType 接收数据类型
     * @return 数据列表
     */
    public <R> List<R> listAs(Class<R> asType) {
        return baseMapper().selectListByQueryAs(queryWrapper(), asType);
    }

    /**
     * 根据实体类构建的条件获取分页数据。
     *
     * @param page 分页对象
     * @return 分页数据
     */
    public Page<T> page(Page<T> page) {
        return baseMapper().paginate(page, queryWrapper());
    }

    /**
     * 根据实体类构建的条件获取分页数据，返回的数据为 asType 类型。
     *
     * @param page   分页对象
     * @param asType 接收数据类型
     * @return 分页数据
     */
    public <R> Page<R> pageAs(Page<R> page, Class<R> asType) {
        return baseMapper().paginateAs(page, queryWrapper(), asType);
    }

    /**
     * 使用 {@code Fields Query} 的方式进行关联查询。
     *
     * @return {@code Fields Query} 查询
     */
    public FieldsQuery<T> withFields() {
        return new FieldsQuery<>(this);
    }

    /**
     * 使用 {@code Relations Query} 的方式进行关联查询。
     *
     * @return {@code Relations Query} 查询
     */
    public RelationsQuery<T> withRelations() {
        return new RelationsQuery<>(this);
    }

}
