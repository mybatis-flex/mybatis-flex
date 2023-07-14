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
package com.mybatisflex.core.util;

import com.mybatisflex.annotation.EnumValue;
import com.mybatisflex.core.exception.FlexExceptions;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnumWrapper<E extends Enum<E>> {

    private static final Map<Class, EnumWrapper> cache = new ConcurrentHashMap<>();

    private boolean hasEnumValueAnnotation = false;

    private Class<?> enumClass;
    private E[] enums;
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
    }


    public Object getEnumValue(E object) {
        try {
            return getterMethod != null ? getterMethod.invoke(object) : property.get(object);
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
