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

import com.mybatisflex.annotation.Column;
import org.apache.ibatis.reflection.Reflector;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldWrapper {

    public static Map<Class<?>, Map<String, FieldWrapper>> cache = new ConcurrentHashMap<>();

    private Field field;
    private boolean isIgnore = false;
    private Class<?> fieldType;
    private Class<?> mappingType;
    private Class<?> keyType;
    private Method getterMethod;
    private Method setterMethod;

    public static FieldWrapper of(Class<?> clazz, String fieldName) {
        Map<String, FieldWrapper> wrapperMap = cache.get(clazz);
        if (wrapperMap == null) {
            synchronized (clazz) {
                wrapperMap = cache.get(clazz);
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
                    Field findField = ClassUtil.getFirstField(clazz, field -> field.getName().equalsIgnoreCase(fieldName));
                    if (findField == null) {
                        throw new IllegalStateException("Can not find field \"" + fieldName + "\" in class: " + clazz.getName());
                    }

                    String setterName = "set" + StringUtil.firstCharToUpperCase(findField.getName());
                    Method setter = ClassUtil.getFirstMethod(clazz, method ->
                        method.getParameterCount() == 1
                            && Modifier.isPublic(method.getModifiers())
                            && method.getName().equals(setterName));

                    fieldWrapper = new FieldWrapper();
                    fieldWrapper.field = findField;
                    fieldWrapper.fieldType = findField.getType();
                    initMappingTypeAndKeyType(clazz, findField, fieldWrapper);

                    Column column = findField.getAnnotation(Column.class);
                    if (column != null && column.ignore()) {
                        fieldWrapper.isIgnore = true;
                    }

                    fieldWrapper.setterMethod = setter;

                    String[] getterNames = new String[]{"get" + StringUtil.firstCharToUpperCase(findField.getName()), "is" + StringUtil.firstCharToUpperCase(findField.getName())};
                    fieldWrapper.getterMethod = ClassUtil.getFirstMethod(clazz, method -> method.getParameterCount() == 0
                        && Modifier.isPublic(method.getModifiers())
                        && ArrayUtil.contains(getterNames, method.getName()));

                    wrapperMap.put(fieldName, fieldWrapper);
                }
            }
        }

        return fieldWrapper;
    }

    private static void initMappingTypeAndKeyType(Class<?> clazz, Field field, FieldWrapper fieldWrapper) {
        Reflector reflector = Reflectors.of(clazz);
        Class<?> fieldType = reflector.getGetterType(field.getName());

        if (Collection.class.isAssignableFrom(fieldType)) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                fieldWrapper.mappingType = (Class<?>) actualTypeArgument;
            }
        } else if (Map.class.isAssignableFrom(fieldType)) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                fieldWrapper.keyType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[1];
                if (actualTypeArgument instanceof ParameterizedType) {
                    fieldWrapper.mappingType = (Class<?>) ((ParameterizedType) actualTypeArgument).getRawType();
                } else {
                    fieldWrapper.mappingType = (Class<?>) actualTypeArgument;
                }
            }
        } else {
            fieldWrapper.mappingType = fieldType;
        }
    }


    public void set(Object value, Object to) {
        try {
            if (setterMethod == null) {
                throw new IllegalStateException("Can not find method \"set" + StringUtil.firstCharToUpperCase(field.getName()) + "\" in class: " + to.getClass().getName());
            }
            setterMethod.invoke(to, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object get(Object target) {
        try {
            if (getterMethod == null) {
                throw new IllegalStateException("Can not find method \"get" + StringUtil.firstCharToUpperCase(field.getName()) + ", is"
                    + StringUtil.firstCharToUpperCase(field.getName()) + "\" in class: " + target.getClass().getName());
            }
            return getterMethod.invoke(target);
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

    public Class<?> getKeyType() {
        return keyType;
    }

    public Field getField() {
        return field;
    }

    public boolean isIgnore() {
        return isIgnore;
    }
}
