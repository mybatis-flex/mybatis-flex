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
package com.mybatisflex.core.query;


import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.impl.OracleDialect;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.EnumWrapper;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class WrapperUtil {

    private WrapperUtil() {
    }

    static List<QueryWrapper> getChildQueryWrapper(QueryCondition condition) {
        List<QueryWrapper> list = null;
        while (condition != null) {
            if (condition.checkEffective()) {
                if (condition instanceof Brackets) {
                    List<QueryWrapper> childQueryWrapper = getChildQueryWrapper(((Brackets) condition).getChildCondition());
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
            return FlexConsts.EMPTY_ARRAY;
        }

        List<Object> params = new ArrayList<>();
        getValues(condition, params);

        return params.isEmpty() ? FlexConsts.EMPTY_ARRAY : params.toArray();
    }


    private static void getValues(QueryCondition condition, List<Object> params) {
        if (condition == null) {
            return;
        }

        QueryColumn column = condition.getColumn();
        if (column instanceof HasParamsColumn) {
            addParam(params, ((HasParamsColumn) column).getParamValues());
        }

        Object value = condition.getValue();

        if (value == null) {
            // column = user_name; logic = eq; value = null
            // sql: user_name = null
            String logic;
            if (condition.checkEffective()
                && (logic = condition.getLogic()) != null
                && !logic.equals(SqlConsts.IS_NULL)
                && !logic.equals(SqlConsts.IS_NOT_NULL)) {
                params.add(null);
            }
            getValues(condition.next, params);
            return;
        }

        if (value instanceof QueryColumn || value instanceof RawQueryCondition) {
            getValues(condition.next, params);
            return;
        }

        addParam(params, value);
        getValues(condition.next, params);
    }

    @SuppressWarnings("all")
    private static void addParam(List<Object> paras, Object value) {
        if (value == null) {
            paras.add(null);
        } else if (ClassUtil.isArray(value.getClass())) {
            for (int i = 0; i < Array.getLength(value); i++) {
                addParam(paras, Array.get(value, i));
            }
        } else if (value instanceof QueryWrapper) {
            Object[] valueArray = ((QueryWrapper) value).getAllValueArray();
            paras.addAll(Arrays.asList(valueArray));
        } else if (value instanceof Enum) {
            // 枚举类型，处理枚举实际值
            EnumWrapper enumWrapper = EnumWrapper.of(value.getClass());
            // 如果是使用注解标识枚举实际值，则直接获取实际值，但如果是依靠全局枚举TypeHandler处理，则此处只能先存入枚举实例，在SQL执行时才能处理实际值
            value = enumWrapper.hasEnumValueAnnotation() ? enumWrapper.getEnumValue((Enum) value) : value;
            paras.add(value);
        } else {
            paras.add(value);
        }

    }

    static String buildValue(List<QueryTable> queryTables, Object value) {
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        } else if (value instanceof RawQueryCondition) {
            return ((RawQueryCondition) value).getContent();
        } else if (value instanceof QueryColumn) {
            return ((QueryColumn) value).toConditionSql(queryTables, DialectFactory.getDialect());
        } else {
            return SqlConsts.SINGLE_QUOTE + value + SqlConsts.SINGLE_QUOTE;
        }
    }


    static String withBracket(String sql) {
        return SqlConsts.BRACKET_LEFT + sql + SqlConsts.BRACKET_RIGHT;
    }

    static String withAlias(String sql, String alias, IDialect dialect) {
        return SqlConsts.BRACKET_LEFT + sql + SqlConsts.BRACKET_RIGHT + buildColumnAlias(alias, dialect);
    }

    static String buildAlias(String alias, IDialect dialect) {
        return StringUtil.isBlank(alias) ? SqlConsts.EMPTY : getAsKeyWord(dialect) + dialect.wrap(alias);
    }

    static String buildColumnAlias(String alias, IDialect dialect) {
        return StringUtil.isBlank(alias) ? SqlConsts.EMPTY : getAsKeyWord(dialect) + dialect.wrapColumnAlias(alias);
    }

    private static String getAsKeyWord(IDialect dialect) {
        return dialect instanceof OracleDialect ? SqlConsts.BLANK : SqlConsts.AS;
    }

}
