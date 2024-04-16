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
import com.mybatisflex.core.util.FieldWrapper;

import java.lang.reflect.Field;
import java.util.List;

public class ToOneRelation<SelfEntity> extends AbstractRelation<SelfEntity> {


    public ToOneRelation(String selfField, String targetSchema, String targetTable, String targetField, String valueField,
                         String joinTable, String joinSelfColumn, String joinTargetColumn,
                         String dataSource, Class<SelfEntity> selfEntityClass, Field relationField,
                         String extraCondition,String[] selectColumns) {
        super(selfField, targetSchema, targetTable, targetField, valueField,
            joinTable, joinSelfColumn, joinTargetColumn,
            dataSource, selfEntityClass, relationField,
            extraCondition, selectColumns
        );
    }


    @Override
    public void join(List<SelfEntity> selfEntities, List<?> targetObjectList, List<Row> mappingRows) {
        selfEntities.forEach(selfEntity -> {
            Object selfValue = selfFieldWrapper.get(selfEntity);
            if (selfValue != null) {
                selfValue = selfValue.toString();
                String targetMappingValue = null;
                if (mappingRows != null) {
                    targetMappingValue = getTargetMappingValue(mappingRows, selfValue);
                    if (targetMappingValue == null) {
                        return;
                    }
                } else {
                    targetMappingValue = (String) selfValue;
                }

                for (Object targetObject : targetObjectList) {
                    Object targetValue = targetFieldWrapper.get(targetObject);
                    if (targetValue != null && targetMappingValue.equals(targetValue.toString())) {
                        if (onlyQueryValueField) {
                            //仅绑定某个字段
                            relationFieldWrapper.set(FieldWrapper.of(targetObject.getClass(), valueField).get(targetObject), selfEntity);
                        } else {
                            relationFieldWrapper.set(targetObject, selfEntity);
                        }
                        break;
                    }
                }
            }
        });
    }


    private String getTargetMappingValue(List<Row> mappingRows, Object selfValue) {
        for (Row mappingRow : mappingRows) {
            if (selfValue.equals(String.valueOf(mappingRow.getIgnoreCase(joinSelfColumn)))) {
                Object joinValue = mappingRow.getIgnoreCase(joinTargetColumn);
                if (joinValue != null) {
                    return joinValue.toString();
                }
            }
        }
        return null;
    }

}
