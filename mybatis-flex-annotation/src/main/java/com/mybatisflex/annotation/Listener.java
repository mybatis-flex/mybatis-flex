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

/**
 * 监听器接口。
 *
 * @author snow
 * @since 2023/4/28
 */
public interface Listener extends Comparable<Listener> {

    /**
     * <p>多个监听器时的执行顺序。
     *
     * <p>值越小越早触发执行。
     *
     * @return order
     */
    default int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    default int compareTo(Listener other) {
        return order() - other.order();
    }

}
