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

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.field.FieldQuery;
import com.mybatisflex.core.field.FieldQueryBuilder;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.util.*;
import java.util.function.Consumer;

public class MapperUtil {

    private MapperUtil() {
    }


    public static <R> void queryFields(BaseMapper<?> mapper, List<R> list, Consumer<FieldQueryBuilder<R>>[] consumers) {
        if (CollectionUtil.isEmpty(list) || ArrayUtil.isEmpty(consumers) || consumers[0] == null) {
            return;
        }
        list.forEach(entity -> {
            for (Consumer<FieldQueryBuilder<R>> consumer : consumers) {
                FieldQueryBuilder<R> fieldQueryBuilder = new FieldQueryBuilder<>(entity);
                consumer.accept(fieldQueryBuilder);
                FieldQuery fieldQuery = fieldQueryBuilder.build();
                QueryWrapper childQuery = fieldQuery.getQueryWrapper();

                FieldWrapper fieldWrapper = FieldWrapper.of(entity.getClass(), fieldQuery.getField());

                Class<?> fieldType = fieldWrapper.getFieldType();
                Class<?> mappingType = fieldWrapper.getMappingType();

                Object value;
                if (Collection.class.isAssignableFrom(fieldType)) {
                    value = mapper.selectListByQueryAs(childQuery, mappingType);
                    if (!fieldType.isAssignableFrom(value.getClass())) {
                        fieldType = getWrapType(fieldType);
                        Collection newValue = (Collection) ClassUtil.newInstance(fieldType);
                        newValue.addAll((Collection) value);
                        value = newValue;
                    }
                } else if (fieldType.isArray()) {
                    value = mapper.selectListByQueryAs(childQuery, mappingType);
                    value = ((List<?>) value).toArray();
                } else {
                    value = mapper.selectOneByQueryAs(childQuery, mappingType);
                }
                fieldWrapper.set(value, entity);
            }
        });
    }


    private static Class<?> getWrapType(Class<?> type) {
        if (List.class.isAssignableFrom(type)) {
            return ArrayList.class;
        }

        if (Set.class.isAssignableFrom(type)) {
            return HashSet.class;
        }

        throw new IllegalStateException("Field query can not support type: " + type.getName());
    }


    /**
     * 搬运加改造 {@link DefaultSqlSession#selectOne(String, Object)}
     */
    public static <T> T getSelectOneResult(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int size = list.size();
        if (size == 1) {
            return list.get(0);
        }
        throw new TooManyResultsException(
                "Expected one result (or null) to be returned by selectOne(), but found: " + size);
    }
}
