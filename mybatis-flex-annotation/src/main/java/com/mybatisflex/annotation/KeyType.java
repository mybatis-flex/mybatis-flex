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
 * ID 生成策略。
 */
public enum KeyType {

    /**
     * 自增的方式。
     */
    Auto,

    /**
     * <p>通过执行数据库 sql 生成。
     *
     * <p>例如：select SEQ_USER_ID.nextval as id from dual
     */
    Sequence,

    /**
     * 通过 IKeyGenerator 生成器生成。
     */
    Generator,

    /**
     * 其他方式，比如在代码层用户手动设置。
     */
    None,

}
