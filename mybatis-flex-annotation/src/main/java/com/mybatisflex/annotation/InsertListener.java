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
 * 用于监听实体类数据被新增到数据库，可以在实体类被新增时做一些前置操作。
 *
 * @see AbstractInsertListener
 */
@FunctionalInterface
public interface InsertListener extends Listener {

    /**
     * 新增操作的前置操作。
     *
     * @param entity 实体类
     */
    void onInsert(Object entity);

}
