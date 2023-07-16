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

import com.mybatisflex.core.exception.FlexAssert;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.io.Serializable;

/**
 * 属性查询构建者。
 *
 * @param <T> 属性所在实体类类型
 * @author 开源海哥
 * @author 王帅
 */
public class FieldQueryBuilder<T> implements Serializable {

    private FieldQuery.Builder<?> builder;

    /**
     * 为指定属性创建查询。
     *
     * @param fn Lambda 引用
     * @return {@link FieldQuery.Builder} 构建者
     */
    public FieldQuery.Builder<T> field(LambdaGetter<T> fn) {
        return createBuilder(fn);
    }

    /**
     * 为指定嵌套属性创建查询。
     *
     * @param <N> 嵌套属性类型
     * @param fn  Lambda 引用
     * @return {@link FieldQuery.Builder} 构建者
     */
    public <N> FieldQuery.Builder<N> nestedField(LambdaGetter<N> fn) {
        return createBuilder(fn);
    }

    /**
     * <p>创建 {@link FieldQuery.Builder} 用于构建属性类型与 {@code QueryWrapper} 用于查询。
     *
     * <p>该方法主要用作于创建构建者对象，以及泛型的转换。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private <R> FieldQuery.Builder<R> createBuilder(LambdaGetter fn) {
        FlexAssert.notNull(fn, "Field can not be null.");
        Class<?> entityClass = LambdaUtil.getImplClass(fn);
        String fieldName = LambdaUtil.getFieldName(fn);
        builder = new FieldQuery.Builder<>(entityClass, fieldName);
        return (FieldQuery.Builder<R>) builder;
    }

    public FieldQuery build() {
        return builder.build();
    }

}
