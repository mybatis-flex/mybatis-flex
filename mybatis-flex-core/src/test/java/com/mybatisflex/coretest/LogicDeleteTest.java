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

package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.logicdelete.impl.*;
import org.junit.Test;

/**
 * 逻辑删除测试。
 *
 * @author 王帅
 * @since 2023-06-20
 */
@SuppressWarnings("all")
public class LogicDeleteTest {

    private final String logicColumn = "deleted";
    private final IDialect dialect = DialectFactory.getDialect();

    @Test
    public void test() {
        print("DefaultLogicDeleteProcessor", new DefaultLogicDeleteProcessor());
        print("BooleanLogicDeleteProcessor", new BooleanLogicDeleteProcessor());
        print("IntegerLogicDeleteProcessor", new IntegerLogicDeleteProcessor());
        print("DateTimeLogicDeleteProcessor", new DateTimeLogicDeleteProcessor());
        print("TimeStampLogicDeleteProcessor", new TimeStampLogicDeleteProcessor());
    }

    public void print(String type, LogicDeleteProcessor processor) {
        System.out.println("===== " + type + " =====");
        System.out.println(processor.buildLogicDeletedSet(logicColumn, dialect));
        System.out.println(processor.buildLogicNormalCondition(logicColumn, dialect));
    }

}