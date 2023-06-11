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

package com.mybatisflex.core.query;

import java.io.Serializable;

/**
 * <p>克隆支持接口。
 *
 * <p>支持序列化克隆与 {@link Object#clone()} 方法。
 *
 * @param <T> 克隆对象类型
 * @author 王帅
 * @since 2023-06-10
 */
public interface CloneSupport<T> extends Serializable, Cloneable {

    /**
     * 改写 {@link Object#clone()} 方法。
     *
     * @return 克隆对象
     */
    T clone();

}