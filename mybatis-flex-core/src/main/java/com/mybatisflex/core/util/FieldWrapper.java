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

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldWrapper {

    public static Map<Class<?>, Map<String, FieldWrapper>> cache = new ConcurrentHashMap<>();

    private Class<?> fieldType;
    private Class<?> mappingType;
    private Method setterMethod;

    public static FieldWrapper of(Class<?> clazz, String fieldName) {
        Map<String, FieldWrapper> wrapperMap = cache.get(clazz);
        if (wrapperMap == null) {
            synchronized (clazz) {
                if (wrapperMap == null) {
                    wrapperMap = new ConcurrentHashMap<>();
                    cache.put(clazz, wrapperMap);
                }
            }
        }

        FieldWrapper fieldWrapper = wrapperMap.get(fieldName);
        if (fieldWrapper == null) {
            synchronized (clazz) {
                fieldWrapper = wrapperMap.get(fieldName);
                if (fieldWrapper == null) {
                    Field findField = ClassUtil.getFirstField(clazz, field -> field.getName().equals(fieldName));
                    if (findField == null) {
                        throw new IllegalStateException("Can not find field \"" + fieldName + "\" in class: " + clazz);
                    }

                    Method setter = ClassUtil.getFirstMethod(clazz, method ->
                            method.getParameterCount() == 1
                                    && Modifier.isPublic(method.getModifiers())
                                    && method.getName().equals("set" + StringUtil.firstCharToUpperCase(fieldName)));

                    if (setter == null) {
                        throw new IllegalStateException("Can not find method \"set" + StringUtil.firstCharToUpperCase(fieldName) + "\" in class: " + clazz);
                    }

                    fieldWrapper = new FieldWrapper();
                    fieldWrapper.fieldType = findField.getType();
                    fieldWrapper.mappingType = parseMappingType(findField);
                    fieldWrapper.setterMethod = setter;

                    wrapperMap.put(fieldName, fieldWrapper);
                }
            }
        }

        return fieldWrapper;
    }

    private static Class<?> parseMappingType(Field field) {
        Class<?> fieldType = field.getType();
        if (Collection.class.isAssignableFrom(fieldType)) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                return (Class<?>) actualTypeArgument;
            }
        }

        if (fieldType.isArray()) {
            return field.getType().getComponentType();
        }

        return fieldType;
    }


    public void set(Object value, Object to) {
        try {
            setterMethod.invoke(to, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public Class<?> getMappingType() {
        return mappingType;
    }
}
