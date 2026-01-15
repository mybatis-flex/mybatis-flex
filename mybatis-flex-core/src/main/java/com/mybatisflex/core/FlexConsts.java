/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.core;

/**
 * Mybatis-Flex 可能用到的静态常量
 *
 * @author michael
 * @author 王帅
 */
public class FlexConsts {

    private FlexConsts() {
    }

    public static final String NAME = "MyBatis-Flex";
    public static final String VERSION = "1.11.5";


    public static final String SQL = "$$sql";
    public static final String SQL_ARGS = "$$sql_args";
    public static final String RAW_ARGS = "$$raw_args";
    public static final String SCHEMA_NAME = "$$schemaName";
    public static final String TABLE_NAME = "$$tableName";
    public static final String FIELD_NAME = "$$fieldName";
    public static final String PRIMARY_KEY = "$$primaryKey";
    public static final String PRIMARY_VALUE = "$$primaryValue";
    public static final String VALUE = "$$value";

    public static final String QUERY = "$$query";
    public static final String ROW = "$$row";
    public static final String ROWS = "$$rows";

    public static final String ENTITY = "$$entity";
    public static final String ENTITIES = "$$entities";
    public static final String IGNORE_NULLS = "$$ignoreNulls";

    public static final String METHOD_INSERT_BATCH = "insertBatch";

    public static final Object[] EMPTY_ARRAY = new Object[0];


    /**
     * 当 entity 使用逻辑删除时，0 为 entity 的正常状态
     */
    public static final int LOGIC_DELETE_NORMAL = 0;
    /**
     * 当 entity 使用逻辑删除时，1 为 entity 的删除状态
     */
    public static final int LOGIC_DELETE_DELETED = 1;

}
