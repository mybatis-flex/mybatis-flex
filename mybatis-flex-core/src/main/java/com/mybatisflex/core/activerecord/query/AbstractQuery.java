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

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.activerecord.Model;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import java.util.List;
import java.util.Optional;

/**
 * 抽象关联查询。
 *
 * @author 王帅
 * @since 2023-07-30
 */
public abstract class AbstractQuery<T extends Model<T>> {

    protected final Model<T> model;

    protected AbstractQuery(Model<T> model) {
        this.model = model;
    }

    /**
     * @return 主键
     */
    protected Object[] pkValues() {
        return model.pkValues();
    }

    /**
     * @return BaseMapper
     */
    protected BaseMapper<T> baseMapper() {
        return model.baseMapper();
    }

    /**
     * @return QueryWrapper
     */
    protected QueryWrapper queryWrapper() {
        return model.queryWrapper();
    }

    /**
     * 根据实体类主键获取一条数据。
     *
     * @return 数据
     */
    public abstract T oneById();

    /**
     * 根据实体类主键获取一条数据，并封装为 {@link Optional} 返回。
     *
     * @return 数据
     */
    public Optional<T> oneByIdOpt() {
        return Optional.ofNullable(oneById());
    }

    /**
     * 根据实体类主键获取一条数据。
     *
     * @return 数据
     */
    public abstract <R> R oneByIdAs(Class<R> asType);

    /**
     * 根据实体类主键获取一条数据，并封装为 {@link Optional} 返回。
     *
     * @return 数据
     */
    public <R> Optional<R> oneByIdAsOpt(Class<R> asType) {
        return Optional.ofNullable(oneByIdAs(asType));
    }

    /**
     * 根据实体类构建的条件获取一条数据。
     *
     * @return 数据
     */
    public abstract T one();

    /**
     * 根据实体类构建的条件获取一条数据，返回的数据为 asType 类型。
     *
     * @param asType 接收数据类型
     * @return 数据
     */
    public abstract <R> R oneAs(Class<R> asType);

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
    public <R> Optional<R> oneOptAs(Class<R> asType) {
        return Optional.ofNullable(oneAs(asType));
    }

    /**
     * 根据实体类构建的条件获取多条数据。
     *
     * @return 数据列表
     */
    public abstract List<T> list();

    /**
     * 根据实体类构建的条件获取多条数据，返回的数据为 asType 类型。
     *
     * @param asType 接收数据类型
     * @return 数据列表
     */
    public abstract <R> List<R> listAs(Class<R> asType);

    /**
     * 根据实体类构建的条件获取分页数据。
     *
     * @param page 分页对象
     * @return 分页数据
     */
    public abstract Page<T> page(Page<T> page);

    /**
     * 根据实体类构建的条件获取分页数据，返回的数据为 asType 类型。
     *
     * @param page   分页对象
     * @param asType 接收数据类型
     * @return 分页数据
     */
    public abstract <R> Page<R> pageAs(Page<R> page, Class<R> asType);

}
