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

package com.mybatisflex.test.common;

import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.test.model.Account;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author 王帅
 * @since 2023-06-14
 */
class ReflectTest {

    @Test
    void test() {
        List<Field> allFields = ClassUtil.getAllFields(Account.class);
        for (Field field : allFields) {
            Type type = TypeParameterResolver.resolveFieldType(field, Account.class);
            System.out.println("field: " + field + "----->Type:" + type);
        }

    }

}
