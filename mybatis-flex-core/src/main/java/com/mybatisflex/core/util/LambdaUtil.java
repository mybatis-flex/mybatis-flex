/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.core.util;

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LambdaUtil {

    private LambdaUtil() {
    }

    private static final Map<Class<?>, String> fieldNameMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Class<?>> implClassMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, QueryColumn> queryColumnMap = new ConcurrentHashMap<>();

    public static <T> String getFieldName(LambdaGetter<T> getter) {
        return MapUtil.computeIfAbsent(fieldNameMap, getter.getClass(), aClass -> {
            SerializedLambda lambda = getSerializedLambda(getter);
            // 兼容 Kotlin KProperty 的 Lambda 解析
            if (lambda.getCapturedArgCount() == 1) {
                Object capturedArg = lambda.getCapturedArg(0);
                try {
                    return (String) capturedArg.getClass()
                        .getMethod("getName")
                        .invoke(capturedArg);
                } catch (Exception e) {
                    // 忽略这个异常，使用其他方式获取方法名
                }
            }
            String methodName = lambda.getImplMethodName();
            return StringUtil.methodToProperty(methodName);
        });
    }


    public static <T> Class<?> getImplClass(LambdaGetter<T> getter) {
        return MapUtil.computeIfAbsent(implClassMap, getter.getClass(), aClass -> {
            SerializedLambda lambda = getSerializedLambda(getter);
            return getImplClass(lambda, getter.getClass().getClassLoader());
        });
    }


    public static <T> String getAliasName(LambdaGetter<T> getter, boolean withPrefix) {
        QueryColumn queryColumn = getQueryColumn(getter);
        if (queryColumn != null) {
            String alias = StringUtil.isNotBlank(queryColumn.getAlias()) ? queryColumn.getAlias() : queryColumn.getName();
            return withPrefix ? queryColumn.getTable().getName() + "$" + alias : alias;
        }
        return getFieldName(getter);
    }


    public static <T> QueryColumn getQueryColumn(LambdaGetter<T> getter) {
        return MapUtil.computeIfAbsent(queryColumnMap, getter.getClass(), aClass -> {
            ClassLoader classLoader = getter.getClass().getClassLoader();
            SerializedLambda lambda = getSerializedLambda(getter);
            Class<?> entityClass = getImplClass(lambda, classLoader);
            TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
            String propertyName = getFieldName(getter);
            return tableInfo.getQueryColumnByProperty(propertyName);
        });
    }


    private static SerializedLambda getSerializedLambda(Serializable getter) {
        try {
            Method method = getter.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            return (SerializedLambda) method.invoke(getter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static Class<?> getImplClass(SerializedLambda lambda, ClassLoader classLoader) {
        String implClass = getImplClassName(lambda);
        try {
            return Class.forName(implClass.replace("/", "."), true, classLoader);
        } catch (ClassNotFoundException e) {
            throw FlexExceptions.wrap(e);
        }
    }

    private static String getImplClassName(SerializedLambda lambda) {
        String type = lambda.getInstantiatedMethodType();
        return type.substring(2, type.indexOf(";"));
    }

}
