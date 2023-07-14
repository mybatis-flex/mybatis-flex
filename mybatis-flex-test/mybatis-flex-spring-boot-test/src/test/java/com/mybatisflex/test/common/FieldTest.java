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

import com.mybatisflex.test.model.Account;
import com.mybatisflex.test.model.BaseEntity;
import com.mybatisflex.test.model.UserVO;
import org.apache.ibatis.reflection.Reflector;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author 王帅
 * @since 2023-06-13
 */
class FieldTest {

    @Test
    void test() {
        String genericString = BaseEntity.class.toGenericString();
        System.out.println(genericString);
    }

    @Test
    void test02() {
        Class<Account> accountClass = Account.class;
        Method[] declaredMethods = accountClass.getMethods();
        Arrays.stream(declaredMethods)
            .filter(e -> e.getName().startsWith("get"))
            .forEach(System.out::println);
    }

    @Test
    void test03() {
        Reflector reflector = new Reflector(Account.class);
        Class<?> id = reflector.getGetterType("id");
        Class<?> userName = reflector.getGetterType("userName");
        Class<?> age = reflector.getGetterType("age");
        System.out.println(id);
        System.out.println(userName);
        System.out.println(age);
    }

    @Test
    void test04() {
        Reflector reflector = new Reflector(UserVO.class);
        Class<?> roleList = reflector.getGetterType("roleList");
        System.out.println(roleList);
    }

}
