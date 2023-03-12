/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.query;


import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class WrapperUtil {


    static String buildAsAlias(String alias) {
        return StringUtil.isBlank(alias) ? "" : " AS " + alias;
    }

    static final Object[] NULL_PARA_ARRAY = new Object[0];

    static Object[] getValues(QueryCondition condition) {
        if (condition == null) {
            return NULL_PARA_ARRAY;
        }

        List<Object> paras = new LinkedList<>();
        getValues(condition, paras);

        return paras.isEmpty() ? NULL_PARA_ARRAY : paras.toArray();
    }


    private static void getValues(QueryCondition condition, List<Object> paras) {
        if (condition == null) {
            return;
        }
        Object value = condition.getValue();
        if (value != null) {
            if (value.getClass().isArray()) {
                Object[] values = (Object[]) value;
                for (Object v : values) {
                    if (v.getClass() == int[].class) {
                        addAll(paras, (int[]) v);
                    } else if (v.getClass() == long[].class) {
                        addAll(paras, (long[]) v);
                    } else if (v.getClass() == short[].class) {
                        addAll(paras, (short[]) v);
                    } else {
                        paras.add(v);
                    }
                }
            } else if (value instanceof QueryWrapper) {
                Object[] valueArray = ((QueryWrapper) value).getValueArray();
                paras.addAll(Arrays.asList(valueArray));
            } else {
                paras.add(value);
            }
        }

        getValues(condition.next, paras);
    }


    private static void addAll(List<Object> paras, int[] ints) {
        for (int i : ints) {
            paras.add(i);
        }
    }

    private static void addAll(List<Object> paras, long[] longs) {
        for (long i : longs) {
            paras.add(i);
        }
    }


    private static void addAll(List<Object> paras, short[] shorts) {
        for (short i : shorts) {
            paras.add(i);
        }
    }


    public static String getColumnTableName(List<QueryTable> queryTables, QueryTable queryTable) {
        if (queryTables == null) {
            return "";
        }

        if (queryTables.size() == 1 && queryTables.get(0).isSameTable(queryTable)) {
            return "";
        }

        QueryTable realTable = getRealTable(queryTables, queryTable);
        if (realTable == null) {
            return "";
        }

        return StringUtil.isNotBlank(realTable.alias) ? realTable.alias : realTable.name;
    }

    public static QueryTable getRealTable(List<QueryTable> queryTables, QueryTable queryTable) {
        if (CollectionUtil.isEmpty(queryTables)) {
            return queryTable;
        }

        if (queryTable == null && queryTables.size() == 1) {
            return queryTables.get(0);
        }

        for (QueryTable table : queryTables) {
            if (table.isSameTable(queryTable)) {
                return table;
            }
        }
        return queryTable;
    }

}
