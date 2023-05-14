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

import com.mybatisflex.annotation.EnumValue;
import com.mybatisflex.core.exception.FlexExceptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class EnumWrapper<E extends Enum<E>> {

    private Class<?> enumClass;

    private Class<?> enumPropertyType;
    private E[] enums;
    private Field property;
    private Method getter;
    private boolean hasEnumValueAnnotation = false;

    public EnumWrapper(Class<E> enumClass) {
        this.enumClass = enumClass;

        List<Field> allFields = ClassUtil.getAllFields(enumClass, field -> field.getAnnotation(EnumValue.class) != null);
        if (!allFields.isEmpty()) {
            hasEnumValueAnnotation = true;
        }

        if (hasEnumValueAnnotation) {
            Field field = allFields.get(0);

            String fieldGetterName = "get" + StringUtil.firstCharToUpperCase(field.getName());
            List<Method> allMethods = ClassUtil.getAllMethods(enumClass, method -> {
                String methodName = method.getName();
                return methodName.equals(fieldGetterName);
            });

            enumPropertyType = ClassUtil.wrap(field.getType());
            enums = enumClass.getEnumConstants();

            if (allMethods.isEmpty()) {
                if (Modifier.isPublic(field.getModifiers())) {
                    property = field;
                } else {
                    throw new IllegalStateException("Can not find \"" + fieldGetterName + "()\" method in enum: " + enumClass.getName());
                }
            } else {
                getter = allMethods.get(0);
            }
        }
    }


    public Object getEnumValue(E object) {
        try {
            return getter != null
                    ? getter.invoke(object)
                    : property.get(object);
        } catch (Exception e) {
            throw FlexExceptions.wrap(e);
        }
    }


    public E toEnum(Object value) {
        for (E e : enums) {
            if (value.equals(getEnumValue(e))) {
                return e;
            }
        }
        return null;
    }

    public Class<?> getEnumClass() {
        return enumClass;
    }

    public Class<?> getEnumPropertyType() {
        return enumPropertyType;
    }

    public E[] getEnums() {
        return enums;
    }

    public Field getProperty() {
        return property;
    }

    public Method getGetter() {
        return getter;
    }

    public boolean hasEnumValueAnnotation() {
        return hasEnumValueAnnotation;
    }
}
