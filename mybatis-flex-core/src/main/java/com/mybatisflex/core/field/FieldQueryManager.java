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

package com.mybatisflex.core.field;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.FieldWrapper;
import org.apache.ibatis.reflection.TypeParameterResolver;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 属性查询管理。
 *
 * @author 王帅
 * @since 2023-07-15
 */
public class FieldQueryManager {

    private FieldQueryManager() {
    }

    public static void queryFields(BaseMapper<?> mapper, Collection<?> entities, Map<String, FieldQuery> fieldQueryMap) {
        for (Object entity : entities) {
            Class<?> entityClass = entity.getClass();
            List<Field> allFields = ClassUtil.getAllFields(entityClass);
            for (Field field : allFields) {
                String mapKey = entityClass.getName() + '#' + field.getName();
                FieldQuery fieldQuery = fieldQueryMap.get(mapKey);
                // 属性包含对应的查询包装器
                if (fieldQuery != null) {
                    @SuppressWarnings("unchecked")
                    QueryWrapper queryWrapper = fieldQuery.getQueryBuilder().build(entity);
                    FieldType fieldType = fieldQuery.getFieldType();
                    if (fieldType == FieldType.AUTO) {
                        fieldType = FieldType.determineFieldType(field);
                    }
                    Class<?> realFieldType = field.getType();
                    Object value;
                    switch (fieldType) {
                        case COLLECTION:
                            Class<?> genericType = getGenericType(entityClass, field);
                            List<?> list = mapper.selectListByQueryAs(queryWrapper, genericType);
                            // 转换成 Collection 子类，或者空 Collection 对象，避免 NPE
                            if (list != null) {
                                value = getCollectionValue(realFieldType, list);
                                // 循环查询泛型实体类
                                if ((!Number.class.isAssignableFrom(genericType)
                                    || !String.class.isAssignableFrom(genericType)
                                    || !Map.class.isAssignableFrom(genericType))
                                    && !fieldQuery.isPrevent()) {
                                    queryFields(mapper, (Collection<?>) value, fieldQueryMap);
                                }
                            } else {
                                value = new ArrayList<>();
                            }
                            break;
                        case ENTITY:
                            value = mapper.selectOneByQueryAs(queryWrapper, realFieldType);
                            // 循环查询嵌套类
                            if (!fieldQuery.isPrevent()) {
                                queryFields(mapper, Collections.singletonList(value), fieldQueryMap);
                            }
                            break;
                        case MAP:
                            List<Row> rows = mapper.selectRowsByQuery(queryWrapper);
                            // 转换成 Map 子类，或者空 Map 对象，避免 NPE
                            if (rows != null && !rows.isEmpty() && rows.get(0) != null) {
                                value = getMapValue(realFieldType, rows.get(0));
                            } else {
                                value = new HashMap<>();
                            }
                            break;
                        case BASIC:
                            value = mapper.selectObjectByQueryAs(queryWrapper, realFieldType);
                            break;
                        case ARRAY:
                            Class<?> componentType = realFieldType.getComponentType();
                            List<?> objects = mapper.selectListByQueryAs(queryWrapper, componentType);
                            value = getArrayValue(componentType, objects);
                            break;
                        default:
                            value = null;
                            break;
                    }
                    // 属性查询出来的值不为 null 时，为属性设置值
                    if (value != null) {
                        FieldWrapper.of(entityClass, fieldQuery.getFieldName()).set(value, entity);
                    }
                }
            }
        }
    }

    private static Class<?> getGenericType(Class<?> entityClass, Field field) {
        Type genericType = TypeParameterResolver.resolveFieldType(field, entityClass);
        if (genericType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        } else {
            throw FlexExceptions.wrap("Can not resolve generic type %s in field %s", genericType, field.getName());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object getCollectionValue(Class<?> fieldType, Collection value) {
        if (ClassUtil.canInstance(fieldType.getModifiers())) {
            Collection collection = (Collection) ClassUtil.newInstance(fieldType);
            collection.addAll(value);
            return collection;
        }

        if (List.class.isAssignableFrom(fieldType)) {
            return value;
        }

        if (Set.class.isAssignableFrom(fieldType)) {
            return new HashSet<>(value);
        }

        throw FlexExceptions.wrap("Unsupported collection type.");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object getMapValue(Class<?> fieldType, Map value) {
        if (ClassUtil.canInstance(fieldType.getModifiers())) {
            Map map = (Map) ClassUtil.newInstance(fieldType);
            map.putAll(value);
            return map;
        }

        return new HashMap<>(value);
    }

    @SuppressWarnings("unchecked")
    private static <T> Object getArrayValue(Class<?> componentType, List<T> list) {
        if (CollectionUtil.isEmpty(list)) {
            return Array.newInstance(componentType, 0);
        }

        T[] array = (T[]) Array.newInstance(componentType, list.size());

        for (int rows = 0; rows < list.size(); rows++) {
            array[rows] = list.get(rows);
        }

        return array;
    }

}
