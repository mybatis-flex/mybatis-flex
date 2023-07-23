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
package com.mybatisflex.core.row;

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.mybatisflex.core.util.SqlUtil;

/**
 * row 的主键策略
 */
public class RowKey {

    /**
     * 自增 ID
     */
    public static final RowKey AUTO = RowKey.of("id", KeyType.Auto, null, false);

    /**
     * UUID 的 ID
     */
    public static final RowKey UUID = RowKey.of("id", KeyType.Generator, KeyGenerators.uuid, true);

    /**
     * flexId
     */
    public static final RowKey FLEX_ID = RowKey.of("id", KeyType.Generator, KeyGenerators.flexId, true);

    /**
     * snowFlakeId
     */
    public static final RowKey SNOW_FLAKE_ID = RowKey.of("id", KeyType.Generator, KeyGenerators.snowFlakeId, true);


    public static RowKey of(String keyColumn) {
        SqlUtil.keepColumnSafely(keyColumn);
        RowKey rowKey = new RowKey();
        rowKey.keyColumn = keyColumn;
        return rowKey;
    }

    public static RowKey of(String keyColumn, KeyType keyType) {
        SqlUtil.keepColumnSafely(keyColumn);
        RowKey rowKey = new RowKey();
        rowKey.keyColumn = keyColumn;
        rowKey.keyType = keyType;
        return rowKey;
    }

    public static RowKey of(String keyColumn, KeyType keyType, String keyTypeValue) {
        SqlUtil.keepColumnSafely(keyColumn);
        RowKey rowKey = new RowKey();
        rowKey.keyColumn = keyColumn;
        rowKey.keyType = keyType;
        rowKey.value = keyTypeValue;
        return rowKey;
    }

    public static RowKey of(String keyColumn, KeyType keyType, String keyTypeValue, boolean before) {
        SqlUtil.keepColumnSafely(keyColumn);
        RowKey rowKey = new RowKey();
        rowKey.keyColumn = keyColumn;
        rowKey.keyType = keyType;
        rowKey.value = keyTypeValue;
        rowKey.before = before;
        return rowKey;
    }

    /**
     * 主键字段
     */
    protected String keyColumn;

    /**
     * 主键类型
     */
    protected KeyType keyType = KeyType.Auto;

    /**
     * 主键类型为 Sequence 和 Generator 时的对应的内容
     */
    protected String value;

    /**
     * 是否前执行
     */
    protected boolean before = true;

    private RowKey() {
    }

    public String getKeyColumn() {
        return keyColumn;
    }


    public KeyType getKeyType() {
        return keyType;
    }


    public String getValue() {
        return value;
    }


    public boolean isBefore() {
        return before;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof RowKey) {
            return keyColumn.equals(((RowKey) o).keyColumn);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return keyColumn.hashCode();
    }

}
