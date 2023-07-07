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

import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.MapperUtil;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.mybatisflex.core.query.QueryMethods.column;

class OneToMany<SelfEntity> extends Relation<SelfEntity> {

    private String orderBy;
    private int limit;


    public OneToMany(RelationOneToMany annotation, Class<SelfEntity> entityClass, Field relationField) {
        super(getDefaultPrimaryProperty(annotation.selfField(),entityClass,"@RelationOneToMany.selfField can not be empty in field: \"" + entityClass.getName() + "." + relationField.getName() + "\""),
                annotation.targetField(), entityClass, relationField);
        this.orderBy = annotation.orderBy();
        this.limit = annotation.limit();
    }

    @Override
    public QueryWrapper toQueryWrapper(List<SelfEntity> selfEntities) {
        Set<Object> selfFieldValues = getSelfFieldValues(selfEntities);
        if (selfFieldValues.isEmpty()) {
            return null;
        }
        QueryWrapper queryWrapper = QueryWrapper.create().select()
                .from(targetTableInfo.getTableNameWithSchema());
        if (selfFieldValues.size() > 1) {
            queryWrapper.where(column(targetTableInfo.getColumnByProperty(targetField.getName())).in(selfFieldValues));
        } else {
            queryWrapper.where(column(targetTableInfo.getColumnByProperty(targetField.getName())).eq(selfFieldValues.iterator().next()));
        }

        if (StringUtil.isNotBlank(orderBy)) {
            queryWrapper.orderBy(orderBy);
        }

        if (limit > 0) {
            queryWrapper.limit(limit);
        }

        return queryWrapper;
    }

    @Override
    public void map(List<SelfEntity> selfEntities, List<?> targetObjectList, BaseMapper<?> mapper) {
        selfEntities.forEach(selfEntity -> {
            Object selfValue = selfFieldWrapper.get(selfEntity);
            if (selfValue != null) {
                Class<?> wrapType = MapperUtil.getWrapType(relationFieldWrapper.getFieldType());
                Collection<Object> collection = (Collection) ClassUtil.newInstance(wrapType);
                for (Object targetObject : targetObjectList) {
                    Object targetValue = targetFieldWrapper.get(targetObject);
                    if (selfValue.equals(targetValue)) {
                        collection.add(targetObject);
                    }
                }
                relationFieldWrapper.set(collection, selfEntity);
            }
        });
    }
}
