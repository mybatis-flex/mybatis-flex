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

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.util.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

public class ToManyRelation<SelfEntity> extends AbstractRelation<SelfEntity> {

    protected String mapKeyField;

    protected FieldWrapper mapKeyFieldWrapper;

    protected String orderBy;

    protected long limit = 0;

    protected String selfValueSplitBy;


    public ToManyRelation(String selfField, String targetSchema, String targetTable, String targetField, String valueField,
                          String joinTable, String joinSelfColumn, String joinTargetColumn,
                          String dataSource, Class<SelfEntity> selfEntityClass, Field relationField,
                          String extraCondition, String[] selectColumns) {
        super(selfField, targetSchema, targetTable, targetField, valueField,
            joinTable, joinSelfColumn, joinTargetColumn,
            dataSource, selfEntityClass, relationField,
            extraCondition, selectColumns
        );
    }

    public static Class<? extends Map> getMapWrapType(Class<?> type) {
        if (ClassUtil.canInstance(type.getModifiers())) {
            return (Class<? extends Map>) type;
        }

        return HashMap.class;
    }

    /**
     * 构建查询目标对象的 QueryWrapper
     *
     * @param targetValues 条件的值
     * @return QueryWrapper
     */
    @Override
    public QueryWrapper buildQueryWrapper(Set<Object> targetValues) {
        if (StringUtil.isNotBlank(selfValueSplitBy) && CollectionUtil.isNotEmpty(targetValues)) {
            Set<Object> newTargetValues = new HashSet<>();
            for (Object targetValue : targetValues) {
                if (targetValue == null) {
                    continue;
                }
                if (!(targetValue instanceof String)) {
                    throw FlexExceptions.wrap("split field only support String type, but current type is: \"" + targetValue.getClass().getName() + "\"");
                }
                String[] splitValues = ((String) targetValue).split(selfValueSplitBy);
                for (String splitValue : splitValues) {
                    //优化分割后的数据类型(防止在数据库查询时候出现隐式转换)
                    newTargetValues.add(ConvertUtil.convert(splitValue, targetFieldWrapper.getFieldType()));
                }
            }
            targetValues = newTargetValues;
        }
        return super.buildQueryWrapper(targetValues);
    }

    @Override
    public void customizeQueryWrapper(QueryWrapper queryWrapper) {
        if (StringUtil.isNotBlank(orderBy)) {
            queryWrapper.orderBy(orderBy);
        }

        if (limit > 0) {
            queryWrapper.limit(limit);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void join(List<SelfEntity> selfEntities, List<?> targetObjectList, List<Row> mappingRows) {

        //目标表关联字段->目标表对象
        Map<String, List<Object>> leftFieldToRightTableMap = new HashMap<>(targetObjectList.size());
        for (Object targetObject : targetObjectList) {
            Object targetJoinFieldValue = targetFieldWrapper.get(targetObject);
            if (targetJoinFieldValue != null) {
                leftFieldToRightTableMap.computeIfAbsent(targetJoinFieldValue.toString(), k -> new ArrayList<>(1)).add(targetObject);
            }
        }

        //通过中间表
        if (mappingRows != null) {
            //当使用中间表时，需要重新映射关联关系
            Map<String, List<Object>> temp = new HashMap<>(selfEntities.size());
            for (Row mappingRow : mappingRows) {
                Object midTableJoinSelfValue = mappingRow.getIgnoreCase(joinSelfColumn);
                if (midTableJoinSelfValue == null) {
                    continue;
                }
                Object midTableJoinTargetValue = mappingRow.getIgnoreCase(joinTargetColumn);
                if (midTableJoinTargetValue == null) {
                    continue;
                }
                List<Object> targetObjects = leftFieldToRightTableMap.get(midTableJoinTargetValue.toString());
                if (targetObjects == null) {
                    continue;
                }
                temp.computeIfAbsent(midTableJoinSelfValue.toString(), k -> new ArrayList<>(targetObjects.size())).addAll(targetObjects);
            }
            leftFieldToRightTableMap = temp;
        }


        //关联集合的类型
        Class<?> fieldType = relationFieldWrapper.getFieldType();
        boolean isMapType = Map.class.isAssignableFrom(fieldType);
        Class<?> wrapType = isMapType ? getMapWrapType(fieldType) : MapperUtil.getCollectionWrapType(fieldType);
        boolean splitMode = StringUtil.isNotBlank(selfValueSplitBy);

        for (SelfEntity selfEntity : selfEntities) {
            if (selfEntity == null) {
                continue;
            }
            Object selfValue = selfFieldWrapper.get(selfEntity);
            if (selfValue == null) {
                continue;
            }
            selfValue = selfValue.toString();

            //只有当splitBy不为空时才会有多个值
            Set<String> targetMappingValues;

            if (splitMode) {
                String[] splitValues = ((String) selfValue).split(selfValueSplitBy);
                targetMappingValues = new LinkedHashSet<>(Arrays.asList(splitValues));
            } else {
                targetMappingValues = new HashSet<>(1);
                targetMappingValues.add((String) selfValue);
            }

            if (targetMappingValues.isEmpty()) {
                return;
            }

            // map
            if (isMapType) {
                Map map = (Map) ClassUtil.newInstance(wrapType);
                Set<Object> validateCountSet = new HashSet<>(targetMappingValues.size());
                for (String targetMappingValue : targetMappingValues) {
                    List<Object> targetObjects = leftFieldToRightTableMap.get(targetMappingValue);
                    //如果非真实外键约束 可能没有对应的对象
                    if (targetObjects == null) {
                        continue;
                    }
                    for (Object targetObject : targetObjects) {
                        Object keyValue = mapKeyFieldWrapper.get(targetObject);
                        Object needKeyValue = ConvertUtil.convert(keyValue, relationFieldWrapper.getKeyType());
                        if (validateCountSet.contains(needKeyValue)) {
                            //当字段类型为Map时，一个key对应的value只能有一个
                            throw FlexExceptions.wrap("When fieldType is Map, the target entity can only be one,\n" +
                                " current entity type is : " + selfEntity + "\n" +
                                " relation field name is : " + relationField.getName() + "\n" +
                                " target entity is : " + targetObjects);
                        }
                        validateCountSet.add(needKeyValue);

                        //noinspection unchecked
                        map.put(needKeyValue, targetObject);
                    }
                }
                if (!map.isEmpty()) {
                    relationFieldWrapper.set(map, selfEntity);
                }

            }
            //集合
            else {
                Collection collection = (Collection) ClassUtil.newInstance(wrapType);
                if (onlyQueryValueField) {
                    Object first = targetObjectList.iterator().next();
                    //将getter方法用单独的变量存储 FieldWrapper.of虽然有缓存 但每次调用至少有一个HashMap的get开销
                    FieldWrapper fieldValueFieldWrapper = FieldWrapper.of(first.getClass(), valueField);
                    for (String targetMappingValue : targetMappingValues) {
                        List<Object> targetObjects = leftFieldToRightTableMap.get(targetMappingValue);
                        if (targetObjects == null) {
                            continue;
                        }
                        for (Object targetObject : targetObjects) {
                            //仅绑定某个字段
                            //noinspection unchecked
                            collection.add(fieldValueFieldWrapper.get(targetObject));
                        }
                    }
                } else {
                    for (String targetMappingValue : targetMappingValues) {
                        List<Object> targetObjects = leftFieldToRightTableMap.get(targetMappingValue);
                        if (targetObjects == null) {
                            continue;
                        }
                        //noinspection unchecked
                        collection.addAll(targetObjects);
                    }
                }

                relationFieldWrapper.set(collection, selfEntity);
            }
        }

    }

    public void setMapKeyField(String mapKeyField) {
        this.mapKeyField = mapKeyField;
        if (StringUtil.isNotBlank(mapKeyField)) {
            this.mapKeyFieldWrapper = FieldWrapper.of(targetEntityClass, mapKeyField);
        } else {
            if (Map.class.isAssignableFrom(relationFieldWrapper.getFieldType())) {
                throw FlexExceptions.wrap("Please config mapKeyField for map field: " + relationFieldWrapper.getField());
            }
        }
    }

}
