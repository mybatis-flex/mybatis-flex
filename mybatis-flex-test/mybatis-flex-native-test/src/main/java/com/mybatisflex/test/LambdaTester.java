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

package com.mybatisflex.test;

import com.mybatisflex.core.util.ClassUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LambdaTester {

    static Function<Account, List<Article>> getArticles = Account::getArticles;
    static BiConsumer<Account, List<Article>> setArticles = Account::setArticles;

    static Map<String,Function<?,?>> getters = new HashMap<>();
    {
        getters.put("getArticles",getArticles);
    }
    static Map<String,BiConsumer<?,?>> setters = new HashMap<>();
    {
        setters.put("setArticles",setArticles);
    }

    public static void main(String[] args) throws Exception {

        LambdaTester a = new LambdaTester();

//        Function getArticles = JavaTester.getArticles;
        Function getArticles =getters.get("getArticles");

        Account account = new Account();

        Method method = ClassUtil.getFirstMethod(Account.class, method1 -> "getArticles".equals(method1.getName()));


        method.invoke(account);
        List<Article> apply = ( List<Article>)getArticles.apply(account);


        long timeMillis = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            List<Article> apply1 = (List<Article>) method.invoke(account);
        }

        System.out.println(">>>>> invoke: " +(System.currentTimeMillis() - timeMillis));

        timeMillis = System.currentTimeMillis();
        for (int i = 0; i < 100000000; i++) {
            List<Article> apply2 = ( List<Article>)getArticles.apply(account);
        }

        System.out.println(">>>>> apply: " +(System.currentTimeMillis() - timeMillis));
    }


//    public static interface FlexFunction<T,R>{
//        R apply(T t);
//    }

}
