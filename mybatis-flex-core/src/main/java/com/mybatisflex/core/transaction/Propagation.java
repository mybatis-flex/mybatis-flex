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
package com.mybatisflex.core.transaction;

/**
 * 事务的传递方式，参考 spring
 */
public enum Propagation {

    //若存在当前事务，则加入当前事务，若不存在当前事务，则创建新的事务
    REQUIRED(0),

    //若存在当前事务，则加入当前事务，若不存在当前事务，则已非事务的方式运行
    SUPPORTS(1),

    //若存在当前事务，则加入当前事务，若不存在当前事务，则抛出异常
    MANDATORY(2),

    //始终以新事务的方式运行，若存在当前事务，则暂停（挂起）当前事务。
    REQUIRES_NEW(3),

    //以非事务的方式运行，若存在当前事务，则暂停（挂起）当前事务。
    NOT_SUPPORTED(4),

    //以非事务的方式运行，若存在当前事务，则抛出异常。
    NEVER(5),

    //如果存在当前事务，则在嵌套事务中执行，否则行为类似于 PROPAGATION_REQUIRED
    NESTED(6),
    ;

    private int value;

    Propagation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
