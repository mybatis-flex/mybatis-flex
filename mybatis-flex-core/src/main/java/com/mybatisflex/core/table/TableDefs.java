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

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.io.ResolverUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author michael
 */
public class TableDefs implements Serializable {

    private static final Map<String, TableDef> TABLE_DEF_MAP = new HashMap<>();
    private static final Map<String, Map<String, QueryColumn>> QUERY_COLUMN_MAP = new HashMap<>();

    public static void init(String packageName) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(TableDef.class), packageName);
        Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
        for (Class<?> type : typeSet) {
            if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
                try {
                    registerTableDef(type);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static TableDef getTableDef(Class<?> entityClass, String tableNameWithSchema) {
        if (TABLE_DEF_MAP.isEmpty()) {
            init(entityClass.getPackage().getName());
        }
        return TABLE_DEF_MAP.get(tableNameWithSchema);
    }


    public static QueryColumn getQueryColumn(Class<?> entityClass, String tableNameWithSchema, String column) {
        if (TABLE_DEF_MAP.isEmpty()) {
            init(entityClass.getPackage().getName());
        }
        Map<String, QueryColumn> queryColumnMap = QUERY_COLUMN_MAP.get(tableNameWithSchema);
        return queryColumnMap != null ? queryColumnMap.get(column) : null;
    }


    public static void registerTableDef(Class<?> tableDefClass) throws IllegalAccessException {
        TableDef tableDef = (TableDef) ClassUtil.getFirstField(tableDefClass, field -> {
            int mod = Modifier.fieldModifiers();
            return Modifier.isPublic(mod) && Modifier.isStatic(mod);
        }).get(null);

        String key = StringUtil.buildSchemaWithTable(tableDef.getSchema(), tableDef.getTableName());

        TABLE_DEF_MAP.put(key, tableDef);

        List<Field> allFields = ClassUtil.getAllFields(tableDef.getClass(), field -> field.getType() == QueryColumn.class);

        Map<String, QueryColumn> columnMap = new HashMap<>(allFields.size());
        for (Field field : allFields) {
            QueryColumn queryColumn = (QueryColumn) field.get(tableDef);
            columnMap.put(queryColumn.getName(), queryColumn);
        }

        QUERY_COLUMN_MAP.put(key, columnMap);
    }


}
