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

package com.mybatisflex.core.table;

import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.util.MapUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 表定义，内包含字段。
 *
 * @author 王帅
 * @since 2024-03-11
 */
public class TableDef extends QueryTable {

    private static final Map<String, TableDef> CACHE = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    protected static <V extends TableDef> V getCache(String key, Function<String, V> mappingFunction) {
        return MapUtil.computeIfAbsent((Map<String, V>) CACHE, key, mappingFunction);
    }

    protected TableDef(String schema, String tableName) {
        super(schema, tableName);
    }

    protected TableDef(String schema, String tableName, String alias) {
        super(schema, tableName, alias);
    }

    /**
     * 兼容方法，与 {@link #getName()} 相同。
     *
     * @return 表名
     */
    public String getTableName() {
        return name;
    }

    public TableDef as(String alias) {
        String key = getNameWithSchema() + "." + alias;
        return getCache(key, k -> new TableDef(this.schema, this.name, alias));
    }

}
