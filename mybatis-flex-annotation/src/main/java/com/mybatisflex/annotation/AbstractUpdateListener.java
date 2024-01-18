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

package com.mybatisflex.annotation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型支持 update 监听器。
 *
 * @author snow
 * @author robot.luo
 * @since 2023/4/28
 */
public abstract class AbstractUpdateListener<T> implements UpdateListener {

    /**
     * 支持的类型
     */
    private final Class<T> supportType;

    @SuppressWarnings("unchecked")
    protected AbstractUpdateListener() {
        Type[] params = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        if (params.length == 0) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + "继承AbstractUpdateListener请指定泛型");
        }
        supportType = (Class<T>) params[0];
    }

    /**
     * 更新操作的前置操作。
     *
     * @param entity 实体类
     */
    public abstract void doUpdate(T entity);

    @Override
    @SuppressWarnings("unchecked")
    public void onUpdate(Object entity) {
        if (supportType.isInstance(entity)) {
            T object = (T) entity;
            doUpdate(object);
        }
    }

}
