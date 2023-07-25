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

import com.mybatisflex.core.activerecord.query.QueryModel;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.util.SqlUtil;

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
    implements MapperModel<T> {

    /**
     * 根据实体类构建的条件删除数据。
     *
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    public boolean remove() {
        return SqlUtil.toBool(baseMapper().deleteByQuery(getQueryWrapper()));
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
        return SqlUtil.toBool(baseMapper().updateByQuery((T) this, ignoreNulls, getQueryWrapper()));
    }

    /**
     * 根据实体类构建的条件查询数据数量。
     *
     * @return 数据数量
     */
    public long count() {
        return baseMapper().selectCountByQuery(getQueryWrapper());
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
        return baseMapper().selectOneByQuery(getQueryWrapper().limit(1));
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
     * 根据实体类构建的条件获取多条数据。
     *
     * @return 数据列表
     */
    public List<T> list() {
        return baseMapper().selectListByQuery(getQueryWrapper());
    }

    /**
     * 根据实体类构建的条件获取分页数据。
     *
     * @param page 分页对象
     * @return 分页数据
     */
    public Page<T> page(Page<T> page) {
        return baseMapper().paginate(page, getQueryWrapper());
    }

}
