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

package com.mybatisflex.processor.entity;

/**
 * 表详细信息。
 *
 * @author 王帅
 * @since 2023-07-13
 */
public class TableInfo {

    /**
     * 实体类全类名。
     */
    private String entityName;

    /**
     * 实体类简单类名。
     */
    private String entitySimpleName;

    /**
     * 实体类注释。
     */
    private String entityComment;

    /**
     * 表名称。
     */
    private String tableName;

    /**
     * Schema 模式。
     */
    private String schema;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntitySimpleName() {
        return entitySimpleName;
    }

    public void setEntitySimpleName(String entitySimpleName) {
        this.entitySimpleName = entitySimpleName;
    }

    public String getEntityComment() {
        return entityComment;
    }

    public void setEntityComment(String entityComment) {
        this.entityComment = entityComment;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        int indexOf = tableName.indexOf(".");
        if (indexOf > 0) {
            if (schema == null || schema.trim().length() == 0) {
                this.schema = tableName.substring(0, indexOf);
                this.tableName = tableName.substring(indexOf + 1);
            } else {
                this.tableName = tableName;
            }
        } else {
            this.tableName = tableName;
        }
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}
