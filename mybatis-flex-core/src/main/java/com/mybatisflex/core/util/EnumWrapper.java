/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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

import com.mybatisflex.annotation.EnumValue;
import com.mybatisflex.core.exception.FlexExceptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumWrapper<E extends Enum<E>> {

    private static final Map<Class, EnumWrapper> cache = new ConcurrentHashMap<>();

    private boolean hasEnumValueAnnotation = false;

    private final Class<?> enumClass;
    private final E[] enums;
    private Field property;
    private Class<?> propertyType;
    private Method getterMethod;

    public static <R extends Enum<R>> EnumWrapper<R> of(Class<?> enumClass) {
        return MapUtil.computeIfAbsent(cache, enumClass, EnumWrapper::new);
    }

    public EnumWrapper(Class<E> enumClass) {
        this.enumClass = enumClass;
        this.enums = enumClass.getEnumConstants();

        Field enumValueField = ClassUtil.getFirstField(enumClass, field -> field.getAnnotation(EnumValue.class) != null);
        if (enumValueField != null) {
            hasEnumValueAnnotation = true;
        }

        if (hasEnumValueAnnotation) {
            String getterMethodName = "get" + StringUtil.firstCharToUpperCase(enumValueField.getName());

            Method getter = ClassUtil.getFirstMethod(enumClass, method -> {
                String methodName = method.getName();
                return methodName.equals(getterMethodName) && Modifier.isPublic(method.getModifiers());
            });

            propertyType = ClassUtil.getWrapType(enumValueField.getType());

            if (getter == null) {
                if (Modifier.isPublic(enumValueField.getModifiers())) {
                    property = enumValueField;
                } else {
                    throw new IllegalStateException("Can not find method \"" + getterMethodName + "()\" in enum: " + enumClass.getName());
                }
            } else {
                this.getterMethod = getter;
            }
        }

        if (!hasEnumValueAnnotation) {
            Method enumValueMethod = ClassUtil.getFirstMethodByAnnotation(enumClass, EnumValue.class);
            if (enumValueMethod != null) {
                String methodName = enumValueMethod.getName();
                if (!(methodName.startsWith("get") && methodName.length() > 3)) {
                    throw new IllegalStateException("Can not find get method \"" + methodName + "()\" in enum: " + enumClass.getName());
                }

                String enumValueFieldName;
                if (methodName.startsWith("get")) {
                    enumValueFieldName = StringUtil.firstCharToLowerCase(enumValueMethod.getName().substring(3));
                } else {
                    enumValueFieldName = enumValueMethod.getName().toLowerCase();
                }
                enumValueField = ClassUtil.getFirstField(enumClass, field -> enumValueFieldName.equals(field.getName()));
                if (enumValueField != null) {
                    propertyType = ClassUtil.getWrapType(enumValueField.getType());
                } else {
                    throw new IllegalStateException("Can not find field \"" + enumValueFieldName + "()\" in enum: " + enumClass.getName());
                }

                this.getterMethod = enumValueMethod;
                this.hasEnumValueAnnotation = true;
            }
        }
    }

    /**
     * 获取枚举值
     * 顺序：
     * 1、@EnumValue标识的get方法
     * 2、@EnumValue标识的属性
     * 3、没有使用@EnumValue，取枚举name
     *
     * @param object
     * @return
     */
    public Object getEnumValue(Object object) {
        try {
            if (getterMethod != null) {
                return getterMethod.invoke(object);
            } else if (property != null) {
                return property.get(object);
            } else {
                //noinspection unchecked
                return ((E) object).name();
            }
        } catch (Exception e) {
            throw FlexExceptions.wrap(e);
        }
    }


    public E getEnum(Object value) {
        if (value != null) {
            for (E e : enums) {
                if (value.equals(getEnumValue(e))) {
                    return e;
                }
            }
        }
        return null;
    }


    public boolean hasEnumValueAnnotation() {
        return hasEnumValueAnnotation;
    }

    public Class<?> getEnumClass() {
        return enumClass;
    }

    public E[] getEnums() {
        return enums;
    }

    public Field getProperty() {
        return property;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

}
