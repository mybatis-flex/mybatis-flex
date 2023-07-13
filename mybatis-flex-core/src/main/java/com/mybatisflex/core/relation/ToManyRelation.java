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
package com.mybatisflex.core.relation;

import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.MapperUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ToManyRelation<SelfEntity> extends AbstractRelation<SelfEntity> {


    public ToManyRelation(String selfField, String targetSchema, String targetTable, String targetField,
                          String joinTable, String joinSelfColumn, String joinTargetColumn,
                          String dataSource, Class<SelfEntity> selfEntityClass, Field relationField,
                          String extraCondition) {
        super(selfField, targetSchema, targetTable, targetField,
            joinTable, joinSelfColumn, joinTargetColumn,
            dataSource, selfEntityClass, relationField,
            extraCondition
        );
    }


    @SuppressWarnings("rawtypes")
    @Override
    public void join(List<SelfEntity> selfEntities, List<?> targetObjectList, List<Row> mappingRows) {
        selfEntities.forEach(selfEntity -> {
            Object selfValue = selfFieldWrapper.get(selfEntity);
            if (selfValue != null) {
                selfValue = selfValue.toString();
                Set<String> targetMappingValues = new HashSet<>();
                if (mappingRows != null) {
                    for (Row mappingRow : mappingRows) {
                        if (selfValue.equals(String.valueOf(mappingRow.getIgnoreCase(joinSelfColumn)))) {
                            Object joinValue = mappingRow.getIgnoreCase(joinTargetColumn);
                            if (joinValue != null) {
                                targetMappingValues.add(joinValue.toString());
                            }
                        }
                    }
                } else {
                    targetMappingValues.add((String) selfValue);
                }

                if (targetMappingValues.isEmpty()) {
                    return;
                }

                Class<?> wrapType = MapperUtil.getWrapType(relationFieldWrapper.getFieldType());
                Collection collection = (Collection) ClassUtil.newInstance(wrapType);
                for (Object targetObject : targetObjectList) {
                    Object targetValue = targetFieldWrapper.get(targetObject);
                    if (targetValue != null && targetMappingValues.contains(targetValue.toString())) {
                        collection.add(targetObject);
                    }
                }
                relationFieldWrapper.set(collection, selfEntity);
            }
        });
    }
}
