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
package com.mybatisflex.core.row;

import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.ConvertUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RowUtil {

    private static final Map<Class<?>, Map<String, Method>> classGettersMapping = new ConcurrentHashMap<>();

    public static <T> T toObject(Row row, Class<T> objectClass) {
        T instance = ClassUtil.newInstance(objectClass);
        Map<String, Method> setterMethods = getSetterMethods(objectClass);
        row.forEach((column, columnValue) -> {
            Method setter = setterMethods.get(column.toLowerCase());
            try {
                if (setter != null) {
                    Object value = ConvertUtil.convert(columnValue, setter.getParameterTypes()[0]);
                    setter.invoke(instance, value);
                }
            } catch (Exception e) {
                throw new RuntimeException("Can not invoke method: " + setter);
            }
        });
        return instance;
    }


    public static <T> List<T> toObjectList(List<Row> rows, Class<T> objectClass) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<T> objectList = new ArrayList<>();
            for (Row row : rows) {
                objectList.add(toObject(row, objectClass));
            }
            return objectList;
        }
    }


    public static <T> T toEntity(Row row, Class<T> entityClass) {
        TableInfo tableInfo = TableInfoFactory.getByEntityClass(entityClass);
        return tableInfo.newInstanceByRow(row);
    }


    public static <T> List<T> toEntityList(List<Row> rows, Class<T> entityClass) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        } else {
            TableInfo tableInfo = TableInfoFactory.getByEntityClass(entityClass);
            List<T> entityList = new ArrayList<>();
            for (Row row : rows) {
                T entity = tableInfo.newInstanceByRow(row);
                entityList.add(entity);
            }
            return entityList;
        }
    }


    public static void registerMapping(Class<?> clazz, Map<String, Method> columnSetterMapping) {
        classGettersMapping.put(clazz, columnSetterMapping);
    }


    private static Map<String, Method> getSetterMethods(Class<?> aClass) {
        return MapUtil.computeIfAbsent(classGettersMapping, aClass, aClass1 -> {
            Map<String, Method> columnSetterMapping = new HashMap<>();
            List<Method> setters = ClassUtil.getAllMethods(aClass1,
                    method -> method.getName().startsWith("set")
                            && method.getParameterCount() == 1
                            && Modifier.isPublic(method.getModifiers())
            );
            for (Method setter : setters) {
                String column = setter.getName().substring(3);
                columnSetterMapping.put(column.toLowerCase(), setter);
                columnSetterMapping.put(StringUtil.camelToUnderline(column).toLowerCase(), setter);
                columnSetterMapping.put(StringUtil.underlineToCamel(column).toUpperCase(), setter);
            }
            return columnSetterMapping;
        });
    }
}
