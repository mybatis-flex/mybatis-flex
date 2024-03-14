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

import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.ConvertUtil;
import com.mybatisflex.core.util.StringUtil;
import com.mybatisflex.core.util.MapUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RowUtil {

    private RowUtil() {
    }

    static final String INDEX_SEPARATOR = "$";

    private static final Map<Class<?>, Map<String, Method>> classSettersCache = new ConcurrentHashMap<>();

    public static <T> T toObject(Row row, Class<T> objectClass) {
        return toObject(row, objectClass, 0);
    }


    public static <T> T toObject(Row row, Class<T> objectClass, int index) {
        T instance = ClassUtil.newInstance(objectClass);
        Map<String, Method> classSetters = getSetterMethods(objectClass);
        Set<String> rowKeys = row.keySet();
        classSetters.forEach((property, setter) -> {
            try {
                if (index <= 0) {
                    for (String rowKey : rowKeys) {
                        if (property.equalsIgnoreCase(rowKey)) {
                            Object rowValue = row.get(rowKey);
                            Object value = ConvertUtil.convert(rowValue, setter.getParameterTypes()[0], true);
                            setter.invoke(instance, value);
                        }
                    }
                } else {
                    for (int i = index; i >= 0; i--) {
                        String newProperty = i <= 0 ? property : property + INDEX_SEPARATOR + i;
                        boolean fillValue = false;
                        for (String rowKey : rowKeys) {
                            if (newProperty.equalsIgnoreCase(rowKey)) {
                                Object rowValue = row.get(rowKey);
                                Object value = ConvertUtil.convert(rowValue, setter.getParameterTypes()[0], true);
                                setter.invoke(instance, value);
                                fillValue = true;
                                break;
                            }
                        }
                        if (fillValue) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Can not invoke method: " + setter);
            }
        });
        return instance;
    }


    public static <T> List<T> toObjectList(List<Row> rows, Class<T> objectClass) {
        return toObjectList(rows, objectClass, 0);
    }


    public static <T> List<T> toObjectList(List<Row> rows, Class<T> objectClass, int index) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<T> objectList = new ArrayList<>();
            for (Row row : rows) {
                objectList.add(toObject(row, objectClass, index));
            }
            return objectList;
        }
    }


    public static <T> T toEntity(Row row, Class<T> entityClass) {
        return toEntity(row, entityClass, 0);
    }


    public static <T> T toEntity(Row row, Class<T> entityClass, int index) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        return tableInfo.newInstanceByRow(row, index);
    }


    public static <T> List<T> toEntityList(List<Row> rows, Class<T> entityClass) {
        return toEntityList(rows, entityClass, 0);
    }


    public static <T> List<T> toEntityList(List<Row> rows, Class<T> entityClass, int index) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        } else {
            TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
            List<T> entityList = new ArrayList<>();
            for (Row row : rows) {
                T entity = tableInfo.newInstanceByRow(row, index);
                entityList.add(entity);
            }
            return entityList;
        }
    }


    public static void registerMapping(Class<?> clazz, Map<String, Method> columnSetterMapping) {
        classSettersCache.put(clazz, columnSetterMapping);
    }


    public static void printPretty(Row row) {
        if (row == null) {
            return;
        }
        printPretty(Collections.singletonList(row));
    }


    public static void printPretty(List<Row> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }

        Row firstRow = rows.get(0);
        List<Integer> textConsoleLengthList = new ArrayList<>();
        StringBuilder sb = new StringBuilder("\nTotal Count: " + rows.size() + "\n");
        Set<String> keys = firstRow.keySet();
        keys.forEach(s -> {
            String sa = "|" + s + "     ";
            sb.append(sa);
            textConsoleLengthList.add(calcTextConsoleLength(sa));
        });
        sb.append("|\n");

        rows.forEach(row -> {
            int i = 0;
            for (String key : keys) {
                sb.append(getColString(row.get(key), textConsoleLengthList.get(i)));
                i++;
            }
            sb.append("|\n");
        });

        System.out.println(sb);
    }


    private static String getColString(Object o, int len) {
        String v = "|" + o;
        while (calcTextConsoleLength(v) < len) {
            v += " ";
        }

        while (calcTextConsoleLength(v) > len) {
            v = v.substring(0, v.length() - 5) + "... ";
        }
        return v;
    }


    private static int calcTextConsoleLength(String s) {
        int result = 0;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (isCJK(c)) {
                result += 3;
            } else {
                result += 2;
            }
        }
        return result % 2 != 0 ? result + 1 : result;
    }


    private static boolean isCJK(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }


    private static Map<String, Method> getSetterMethods(Class<?> aClass) {
        return MapUtil.computeIfAbsent(classSettersCache, aClass, aClass1 -> {
            Map<String, Method> columnSetterMapping = new HashMap<>();
            List<Method> setters = ClassUtil.getAllMethods(aClass1,
                method -> method.getName().startsWith("set")
                    && method.getParameterCount() == 1
                    && Modifier.isPublic(method.getModifiers())
            );
            for (Method setter : setters) {
                String column = setter.getName().substring(3);
                columnSetterMapping.put(column, setter);
                columnSetterMapping.put(StringUtil.camelToUnderline(column), setter);
                columnSetterMapping.put(StringUtil.underlineToCamel(column), setter);
            }
            return columnSetterMapping;
        });
    }

}
