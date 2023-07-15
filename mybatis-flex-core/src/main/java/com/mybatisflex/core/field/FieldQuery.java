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

import java.io.Serializable;

/**
 * 查询属性的信息。
 */
@SuppressWarnings("rawtypes")
public class FieldQuery implements Serializable {

    private String className;
    private String fieldName;
    private FieldType fieldType = FieldType.AUTO;
    private boolean prevent;
    private QueryBuilder queryBuilder;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
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

        public Builder(String className, String fieldName) {
            this.fieldQuery = new FieldQuery();
            this.fieldQuery.setClassName(className);
            this.fieldQuery.setFieldName(fieldName);
        }

        /**
         * 设置属性类型（可选，默认自动识别）。
         *
         * @param fieldType 属性类型
         * @return 构建者
         */
        public Builder<T> fieldType(FieldType fieldType) {
            this.fieldQuery.setFieldType(fieldType);
            return this;
        }

        /**
         * 阻止对嵌套类属性的查询，只对 {@link FieldType#COLLECTION} 与
         * {@link FieldType#ENTITY} 两种属性类型有效。
         *
         * @return 构建者
         */
        public Builder<T> prevent() {
            this.fieldQuery.setPrevent(true);
            return this;
        }

        /**
         * 设置是否阻止对嵌套类属性的查询，只对 {@link FieldType#COLLECTION} 与
         * {@link FieldType#ENTITY} 两种属性类型有效。
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
