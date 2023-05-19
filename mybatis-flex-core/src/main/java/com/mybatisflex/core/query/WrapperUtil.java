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
import com.mybatisflex.core.util.EnumWrapper;
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

        List<Object> params = new ArrayList<>();
        getValues(condition, params);

        return params.isEmpty() ? NULL_PARA_ARRAY : params.toArray();
    }


    private static void getValues(QueryCondition condition, List<Object> params) {
        if (condition == null) {
            return;
        }

        Object value = condition.getValue();
        if (value == null
                || value instanceof QueryColumn
                || value instanceof RawValue) {
            getValues(condition.next, params);
            return;
        }

        addParam(params, value);
        getValues(condition.next, params);
    }

    private static void addParam(List<Object> paras, Object value) {
        if (value == null) {
            paras.add(null);
        } else if (ClassUtil.isArray(value.getClass())) {
            for (int i = 0; i < Array.getLength(value); i++) {
                addParam(paras, Array.get(value, i));
            }
        } else if (value instanceof QueryWrapper) {
            Object[] valueArray = ((QueryWrapper) value).getValueArray();
            paras.addAll(Arrays.asList(valueArray));
        } else if (value.getClass().isEnum()) {
            EnumWrapper enumWrapper = EnumWrapper.of(value.getClass());
            if (enumWrapper.hasEnumValueAnnotation()) {
                paras.add(enumWrapper.getEnumValue((Enum) value));
            } else {
                paras.add(value);
            }
        } else {
            paras.add(value);
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
