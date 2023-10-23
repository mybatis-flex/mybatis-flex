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

import java.lang.reflect.Array;
import java.util.*;

import static com.mybatisflex.core.table.TableInfoFactory.defaultSupportColumnTypes;

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

            if (entity == null) {
                continue;
            }

            String className = ClassUtil.getUsefulClass(entity.getClass()).getName();

            fieldQueryMap.forEach((key, fieldQuery) -> {
                // 不是当前类的内容
                if (!key.startsWith(className + "#")) {
                    return;
                }

                @SuppressWarnings("unchecked")
                QueryWrapper queryWrapper = fieldQuery.getQueryBuilder().build(entity);

                // QueryWrapper 为 null 值时，不进行属性查询
                if (queryWrapper == null) {
                    return;
                }

                Class<?> filedType = fieldQuery.getFieldWrapper().getFieldType();
                Object value;

                if (Collection.class.isAssignableFrom(filedType)) {
                    Class<?> mappingType = fieldQuery.getFieldWrapper().getMappingType();
                    List<?> list = mapper.selectListByQueryAs(queryWrapper, mappingType);
                    // 转换成 Collection 子类，或者空 Collection 对象，避免 NPE
                    value = getCollectionValue(filedType, list);
                    // 循环查询泛型实体类
                    if ((!Number.class.isAssignableFrom(mappingType)
                        || !String.class.isAssignableFrom(mappingType)
                        || !Map.class.isAssignableFrom(mappingType))
                        && !fieldQuery.isPrevent()) {
                        queryFields(mapper, (Collection<?>) value, fieldQueryMap);
                    }
                } else if (Map.class.isAssignableFrom(filedType)) {
                    List<Row> rows = mapper.selectRowsByQuery(queryWrapper);
                    // 转换成 Map 子类，或者空 Map 对象，避免 NPE
                    if (rows != null && !rows.isEmpty() && rows.get(0) != null) {
                        value = getMapValue(filedType, rows.get(0));
                    } else {
                        value = new HashMap<>();
                    }
                } else if (filedType.isArray()) {
                    Class<?> componentType = filedType.getComponentType();
                    List<?> objects = mapper.selectListByQueryAs(queryWrapper, componentType);
                    value = getArrayValue(componentType, objects);
                } else if (defaultSupportColumnTypes.contains(filedType)) {
                    value = mapper.selectObjectByQueryAs(queryWrapper, filedType);
                } else {
                    value = mapper.selectOneByQueryAs(queryWrapper, filedType);
                    // 循环查询嵌套类
                    if (!fieldQuery.isPrevent()) {
                        queryFields(mapper, Collections.singletonList(value), fieldQueryMap);
                    }
                }
                // 属性查询出来的值不为 null 时，为属性设置值
                if (value != null) {
                    fieldQuery.getFieldWrapper().set(value, entity);
                }
            });
        }
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Object getCollectionValue(Class<?> fieldType, Collection value) {
        if (value == null) {
            if (fieldType == List.class) {
                return Collections.emptyList();
            } else if (fieldType == Set.class) {
                return Collections.emptySet();
            }
            // avoid NPE
            return ClassUtil.newInstance(fieldType);
        }

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

        throw FlexExceptions.wrap("Unsupported collection type: " + fieldType);
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
