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
 * @param <T> 实体类类型
 * @author 王帅
 * @since 2023-07-24
 */
@SuppressWarnings({"unused", "unchecked"})
public abstract class Model<T extends Model<T>>
    extends QueryModel<T>
    implements MapperModel<T> {

    public boolean remove() {
        return SqlUtil.toBool(baseMapper().deleteByQuery(getQueryWrapper()));
    }

    public boolean update() {
        return update(true);
    }

    public boolean update(boolean ignoreNulls) {
        return SqlUtil.toBool(baseMapper().updateByQuery((T) this, ignoreNulls, getQueryWrapper()));
    }

    public long count() {
        return baseMapper().selectCountByQuery(getQueryWrapper());
    }

    public boolean exists() {
        return SqlUtil.toBool(count());
    }

    public T one() {
        return baseMapper().selectOneByQuery(getQueryWrapper());
    }

    public Optional<T> oneOpt() {
        return Optional.ofNullable(one());
    }

    public List<T> list() {
        return baseMapper().selectListByQuery(getQueryWrapper());
    }

    public Page<T> page(Page<T> page) {
        return baseMapper().paginate(page, getQueryWrapper());
    }

}
