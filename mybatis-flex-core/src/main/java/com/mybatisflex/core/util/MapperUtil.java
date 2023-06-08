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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MapperUtil {

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
                if (fieldType.isAssignableFrom(List.class)) {
                    value = mapper.selectListByQueryAs(childQuery, mappingType);
                } else if (fieldType.isAssignableFrom(Set.class)) {
                    value = mapper.selectListByQueryAs(childQuery, mappingType);
                    value = new HashSet<>((Collection<?>) value);
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
}
