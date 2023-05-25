/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.util;

import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.util.MapUtil;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LambdaUtil {

    private static final Map<Class<?>, SerializedLambda> lambdaMap = new ConcurrentHashMap<>();

    public static <T> String getFieldName(LambdaGetter<T> getter) {
        SerializedLambda lambda = getSerializedLambda(getter);
        String methodName = lambda.getImplMethodName();
        return PropertyNamer.methodToProperty(methodName);
    }


    private static SerializedLambda getSerializedLambda(Serializable getter) {
        return MapUtil.computeIfAbsent(lambdaMap, getter.getClass(), aClass -> {
            try {
                Method method = getter.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                return (SerializedLambda) method.invoke(getter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
