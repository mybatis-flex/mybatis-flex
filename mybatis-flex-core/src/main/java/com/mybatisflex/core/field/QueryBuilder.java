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
package com.mybatisflex.core.field;

import com.mybatisflex.core.query.QueryWrapper;

/**
 * 属性查询条件构建。
 *
 * @param <T> 实体类类型
 */
public interface QueryBuilder<T> {

    /**
     * 构建查询属性的 {@link QueryWrapper} 对象。
     *
     * @param entity 实体类
     * @return 查询条件
     */
    QueryWrapper build(T entity);

}
