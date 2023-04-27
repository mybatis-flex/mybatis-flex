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


import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class WrapperUtil {


    static String buildAsAlias(String alias) {
        return StringUtil.isBlank(alias) ? "" : " AS " + alias;
    }

    static final Object[] NULL_PARA_ARRAY = new Object[0];

    static List<QueryWrapper> getChildSelect(QueryCondition condition) {
        List<QueryWrapper> list = null;
        while (condition != null) {
            if (condition.checkEffective()) {
                if (condition instanceof Brackets) {
                    List<QueryWrapper> childQueryWrapper = getChildSelect(((Brackets) condition).getChildCondition());
                    if (!childQueryWrapper.isEmpty()) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.addAll(childQueryWrapper);
                    }
                }
                // not Brackets
                else {
                    Object value = condition.getValue();
                    if (value instanceof QueryWrapper) {
                        if (list == null) {
                            list = new ArrayList<>();
                        }
                        list.add((QueryWrapper) value);
                        list.addAll(((QueryWrapper) value).getChildSelect());
                    } else if (value != null && value.getClass().isArray()) {
                        for (int i = 0; i < Array.getLength(value); i++) {
                            Object arrayValue = Array.get(value, i);
                            if (arrayValue instanceof QueryWrapper) {
                                if (list == null) {
                                    list = new ArrayList<>();
                                }
                                list.add((QueryWrapper) arrayValue);
                                list.addAll(((QueryWrapper) arrayValue).getChildSelect());
                            }
                        }
                    }
                }
            }
            condition = condition.next;
        }
        return list == null ? Collections.emptyList() : list;
    }


    static Object[] getValues(QueryCondition condition) {
        if (condition == null) {
            return NULL_PARA_ARRAY;
        }

        List<Object> paras = new ArrayList<>();
        getValues(condition, paras);

        return paras.isEmpty() ? NULL_PARA_ARRAY : paras.toArray();
    }


    private static void getValues(QueryCondition condition, List<Object> paras) {
        if (condition == null) {
            return;
        }

        Object value = condition.getValue();
        if (value == null
                || value instanceof QueryColumn
                || value instanceof RawValue) {
            getValues(condition.next, paras);
            return;
        }

        if (value.getClass().isArray()) {
            Object[] values = (Object[]) value;
            for (Object object : values) {
                if (object != null && ClassUtil.isArray(object.getClass())) {
                    for (int i = 0; i < Array.getLength(object); i++) {
                        paras.add(Array.get(object, i));
                    }
                } else {
                    paras.add(object);
                }
            }
        } else if (value instanceof QueryWrapper) {
            Object[] valueArray = ((QueryWrapper) value).getValueArray();
            paras.addAll(Arrays.asList(valueArray));
        } else {
            paras.add(value);
        }

        getValues(condition.next, paras);
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
