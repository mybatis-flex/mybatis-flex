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

import java.util.Optional;

/**
 * @param <T> 实体类类型
 * @author 王帅
 * @since 2023-07-23
 */
@SuppressWarnings({"unused", "unchecked"})
public interface MapperModel<T> {

    default BaseMapper<T> baseMapper() {
        return Mappers.ofEntityClass((Class<T>) getClass());
    }

    default Object[] getPkValues() {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(getClass());
        return tableInfo.buildPkSqlArgs(this);
    }

    default boolean save() {
        return save(true);
    }

    default boolean save(boolean ignoreNulls) {
        return SqlUtil.toBool(baseMapper().insert((T) this, ignoreNulls));
    }

    default boolean saveOrUpdate() {
        return saveOrUpdate(true);
    }

    default boolean saveOrUpdate(boolean ignoreNulls) {
        return SqlUtil.toBool(baseMapper().insertOrUpdate((T) this, ignoreNulls));
    }

    default boolean removeById() {
        return SqlUtil.toBool(baseMapper().deleteById(getPkValues()));
    }

    default boolean updateById() {
        return updateById(true);
    }

    default boolean updateById(boolean ignoreNulls) {
        return SqlUtil.toBool(baseMapper().update((T) this, ignoreNulls));
    }

    default T oneById() {
        return baseMapper().selectOneById(getPkValues());
    }

    default Optional<T> oneByIdOpt() {
        return Optional.ofNullable(oneById());
    }

}
