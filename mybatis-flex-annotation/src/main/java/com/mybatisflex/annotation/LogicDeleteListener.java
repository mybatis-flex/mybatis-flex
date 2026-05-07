/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
 * 用于监听实体类数据被逻辑删除，可以在实体类被删除时做一些前置操作。
 *
 * @author fangzhengjin
 * @since 2024/12/1
 * @see AbstractInsertListener
 */
@FunctionalInterface
public interface LogicDeleteListener extends Listener {

    /**
     * 逻辑删除操作的前置操作。
     *
     * @param entity 代理实体类，用于收集需要更新的字段，所有字段均为 null。
     */
    void onLogicDelete(Object entity);

}
