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

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.FieldWrapper;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.TypeParameterResolver;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

abstract class Relation<SelfEntity> {

    protected Class<SelfEntity> selfEntityClass;
    protected Field relationField;
    protected FieldWrapper relationFieldWrapper;

    protected Field selfField;
    protected FieldWrapper selfFieldWrapper;

    protected Field targetField;
    protected Class<?> targetEntityClass;
    protected TableInfo targetTableInfo;
    protected FieldWrapper targetFieldWrapper;

    public Relation(String selfField, String targetField, Class<SelfEntity> entityClass, Field relationField) {
        this.selfEntityClass = entityClass;
        this.relationField = relationField;
        this.relationFieldWrapper = FieldWrapper.of(entityClass, relationField.getName());

        this.selfField = ClassUtil.getFirstField(entityClass, field -> field.getName().equals(selfField));
        this.selfFieldWrapper = FieldWrapper.of(entityClass, selfField);


        Reflector reflector = new Reflector(entityClass);
        Class<?> targetClass = reflector.getGetterType(relationField.getName());

        if (Collection.class.isAssignableFrom(targetClass)) {
            Type genericType = TypeParameterResolver.resolveFieldType(relationField, entityClass);
            if (genericType instanceof ParameterizedType) {
                this.targetEntityClass = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
            }
        } else if (targetClass.isArray()) {
            this.targetEntityClass = targetClass.getComponentType();
        } else {
            this.targetEntityClass = targetClass;
        }

        this.targetField = ClassUtil.getFirstField(targetEntityClass, field -> field.getName().equals(targetField));
        this.targetFieldWrapper = FieldWrapper.of(targetEntityClass, targetField);

        this.targetTableInfo = TableInfoFactory.ofEntityClass(targetEntityClass);
    }


    public Class<SelfEntity> getSelfEntityClass() {
        return selfEntityClass;
    }

    public void setSelfEntityClass(Class<SelfEntity> selfEntityClass) {
        this.selfEntityClass = selfEntityClass;
    }

    public Field getRelationField() {
        return relationField;
    }

    public void setRelationField(Field relationField) {
        this.relationField = relationField;
    }

    public FieldWrapper getRelationFieldWrapper() {
        return relationFieldWrapper;
    }

    public void setRelationFieldWrapper(FieldWrapper relationFieldWrapper) {
        this.relationFieldWrapper = relationFieldWrapper;
    }

    public Field getSelfField() {
        return selfField;
    }

    public void setSelfField(Field selfField) {
        this.selfField = selfField;
    }

    public FieldWrapper getSelfFieldWrapper() {
        return selfFieldWrapper;
    }

    public void setSelfFieldWrapper(FieldWrapper selfFieldWrapper) {
        this.selfFieldWrapper = selfFieldWrapper;
    }

    public Field getTargetField() {
        return targetField;
    }

    public void setTargetField(Field targetField) {
        this.targetField = targetField;
    }

    public Class<?> getTargetEntityClass() {
        return targetEntityClass;
    }

    public void setTargetEntityClass(Class<?> targetEntityClass) {
        this.targetEntityClass = targetEntityClass;
    }

    public TableInfo getTargetTableInfo() {
        return targetTableInfo;
    }

    public void setTargetTableInfo(TableInfo targetTableInfo) {
        this.targetTableInfo = targetTableInfo;
    }

    public FieldWrapper getTargetFieldWrapper() {
        return targetFieldWrapper;
    }

    public void setTargetFieldWrapper(FieldWrapper targetFieldWrapper) {
        this.targetFieldWrapper = targetFieldWrapper;
    }

    protected Set<Object> getSelfFieldValues(List<SelfEntity> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Object> values = new LinkedHashSet<>();
        list.forEach(self -> {
            Object value = selfFieldWrapper.get(self);
            if (value != null && !"".equals(value)) {
                values.add(value);
            }
        });
        return values;
    }

    public abstract QueryWrapper toQueryWrapper(List<SelfEntity> selfEntities);

    public abstract void map(List<SelfEntity> selfEntities, List<?> mappedObjectList, BaseMapper<?> mapper);

    public Class<?> getMappingType() {
        return relationFieldWrapper.getMappingType();
    }
}
