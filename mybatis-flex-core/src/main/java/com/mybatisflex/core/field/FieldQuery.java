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

import com.mybatisflex.core.util.FieldWrapper;

import java.io.Serializable;

/**
 * 查询属性的信息。
 */
@SuppressWarnings("rawtypes")
public class FieldQuery implements Serializable {

    private Class<?> entityClass;
    private String fieldName;
    private FieldWrapper fieldWrapper;
    private boolean prevent;
    private QueryBuilder queryBuilder;

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldWrapper getFieldWrapper() {
        return fieldWrapper;
    }

    public void setFieldWrapper(FieldWrapper fieldWrapper) {
        this.fieldWrapper = fieldWrapper;
    }

    public boolean isPrevent() {
        return prevent;
    }

    public void setPrevent(boolean prevent) {
        this.prevent = prevent;
    }

    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public void setQueryBuilder(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public static class Builder<T> {

        private final FieldQuery fieldQuery;

        public Builder(Class entityClass, String fieldName) {
            this.fieldQuery = new FieldQuery();
            this.fieldQuery.setEntityClass(entityClass);
            this.fieldQuery.setFieldName(fieldName);
            this.fieldQuery.setFieldWrapper(FieldWrapper.of(entityClass, fieldName));
        }

        /**
         * 阻止对嵌套类属性的查询，只对 集合 与 实体类 两种属性类型有效。
         *
         * @return 构建者
         */
        public Builder<T> prevent() {
            this.fieldQuery.setPrevent(true);
            return this;
        }

        /**
         * 设置是否阻止对嵌套类属性的查询，只对 集合 与 实体类 两种属性类型有效。
         *
         * @param prevent 是否阻止对嵌套类属性查询
         * @return 构建者
         */
        public Builder<T> prevent(boolean prevent) {
            this.fieldQuery.setPrevent(prevent);
            return this;
        }

        /**
         * 设置查询这个属性的 {@code QueryWrapper} 对象。
         *
         * @param queryBuilder 查询包装器
         * @return 构建者
         */
        public Builder<T> queryWrapper(QueryBuilder<T> queryBuilder) {
            this.fieldQuery.setQueryBuilder(queryBuilder);
            return this;
        }

        protected FieldQuery build() {
            return this.fieldQuery;
        }

    }

}
