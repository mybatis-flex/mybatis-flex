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
package com.mybatisflex.core.util;


import com.mybatisflex.core.exception.FlexExceptions;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;

public class StringUtil {

    private StringUtil() {
    }

    /**
     * @see org.apache.ibatis.reflection.property.PropertyNamer#methodToProperty(String)
     */
    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        } else {
            throw FlexExceptions.wrap("Error parsing property name '%s'.  Didn't start with 'is', 'get' or 'set'.", name);
        }
        if (!name.isEmpty()) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH).concat(name.substring(1));
        }
        return name;
    }


    /**
     * 第一个字符转换为小写
     *
     * @param string
     */
    public static String firstCharToLowerCase(String string) {
        char firstChar = string.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] chars = string.toCharArray();
            chars[0] += ('a' - 'A');
            return new String(chars);
        }
        return string;
    }


    /**
     * 第一个字符转换为大写
     *
     * @param string
     */
    public static String firstCharToUpperCase(String string) {
        char firstChar = string.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] chars = string.toCharArray();
            chars[0] -= ('a' - 'A');
            return new String(chars);
        }
        return string;
    }


    /**
     * 驼峰转下划线格式
     *
     * @param string
     */
    public static String camelToUnderline(String string) {
        if (isBlank(string)) {
            return "";
        }
        int strLen = string.length();
        StringBuilder sb = new StringBuilder(strLen);
        for (int i = 0; i < strLen; i++) {
            char c = string.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                char prev = string.charAt(i - 1);
                if (!Character.isUpperCase(prev) && prev != '_') {
                    sb.append('_');
                }
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰格式
     *
     * @param string
     */
    public static String underlineToCamel(String string) {
        if (isBlank(string)) {
            return "";
        }
        if (Character.isUpperCase(string.charAt(0))) {
            string = string.toLowerCase();
        }
        int strLen = string.length();
        StringBuilder sb = new StringBuilder(strLen);
        for (int i = 0; i < strLen; i++) {
            char c = string.charAt(i);
            if (c == '_') {
                if (++i < strLen) {
                    sb.append(Character.toUpperCase(string.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * 删除字符串中的字符
     */
    public static String deleteChar(String string, char deleteChar) {
        if (isBlank(string)) {
            return "";
        }
        char[] chars = string.toCharArray();
        StringBuilder sb = new StringBuilder(string.length());
        for (char aChar : chars) {
            if (aChar != deleteChar) {
                sb.append(aChar);
            }
        }
        return sb.toString();
    }

    public static String deleteChar(String string, char deleteChar1, char deleteChar2) {
        if (isBlank(string)) {
            return "";
        }
        char[] chars = string.toCharArray();
        StringBuilder sb = new StringBuilder(string.length());
        for (char aChar : chars) {
            if (aChar != deleteChar1 && aChar != deleteChar2) {
                sb.append(aChar);
            }
        }
        return sb.toString();
    }

    /**
     * 字符串为 null 或者内部字符全部为 ' ', '\t', '\n', '\r' 这四类字符时返回 true
     */
    public static boolean isBlank(String string) {
        if (string == null) {
            return true;
        }

        for (int i = 0, len = string.length(); i < len; i++) {
            if (string.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }


    public static boolean isAnyBlank(String... strings) {
        if (strings == null || strings.length == 0) {
            throw new IllegalArgumentException("strings is null or empty.");
        }

        for (String string : strings) {
            if (isBlank(string)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }


    public static boolean areNotBlank(String... strings) {
        return !isAnyBlank(strings);
    }


    /**
     * 这个字符串是否是全是数字
     *
     * @param string
     * @return 全部数数值时返回 true，否则返回 false
     */
    public static boolean isNumeric(String string) {
        if (isBlank(string)) {
            return false;
        }
        for (int i = string.length(); --i >= 0; ) {
            int chr = string.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }


    public static boolean startsWithAny(String string, String... prefixes) {
        if (isBlank(string) || prefixes == null) {
            return false;
        }

        for (String prefix : prefixes) {
            if (string.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }


    public static boolean endsWithAny(String str, String... suffixes) {
        if (isBlank(str) || suffixes == null) {
            return false;
        }

        for (String suffix : suffixes) {
            if (str.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 正则匹配
     *
     * @param regex
     * @param input
     * @return
     */
    public static boolean matches(String regex, String input) {
        if (null == regex || null == input) {
            return false;
        }
        return Pattern.matches(regex, input);
    }

    /**
     * 合并字符串，优化 String.join() 方法
     *
     * @param delimiter
     * @param elements
     * @return 新拼接好的字符串
     * @see String#join(CharSequence, CharSequence...)
     */
    public static String join(String delimiter, CharSequence... elements) {
        if (ArrayUtil.isEmpty(elements)) {
            return "";
        } else if (elements.length == 1) {
            return String.valueOf(elements[0]);
        } else {
            return String.join(delimiter, elements);
        }
    }

    /**
     * 合并字符串，优化 String.join() 方法
     *
     * @param delimiter
     * @param elements
     * @return 新拼接好的字符串
     * @see String#join(CharSequence, CharSequence...)
     */
    public static String join(String delimiter, Collection<? extends CharSequence> elements) {
        if (CollectionUtil.isEmpty(elements)) {
            return "";
        } else if (elements.size() == 1) {
            return String.valueOf(elements.iterator().next());
        } else {
            return String.join(delimiter, elements);
        }
    }


    /**
     * 合并字符串，优化 String.join() 方法
     *
     * @param delimiter
     * @param objs
     * @param function
     * @param <T>
     */
    public static <T> String join(String delimiter, Collection<T> objs, Function<T, String> function) {
        if (CollectionUtil.isEmpty(objs)) {
            return "";
        } else if (objs.size() == 1) {
            T next = objs.iterator().next();
            return String.valueOf(function.apply(next));
        } else {
            String[] strings = new String[objs.size()];
            int index = 0;
            for (T obj : objs) {
                strings[index++] = function.apply(obj);
            }
            return String.join(delimiter, strings);
        }
    }

    public static String buildSchemaWithTable(String schema, String tableName) {
        return isNotBlank(schema) ? schema + "." + tableName : tableName;
    }

    public static String[] getSchemaAndTableName(String tableNameWithSchema) {
        int index = tableNameWithSchema.indexOf(".");
        return index <= 0 ? new String[]{null, tableNameWithSchema.trim()}
            : new String[]{tableNameWithSchema.substring(0, index).trim(), tableNameWithSchema.substring(index + 1).trim()};
    }

    public static String[] getTableNameWithAlias(String tableNameWithAlias) {
        int index = tableNameWithAlias.indexOf(".");
        return index <= 0 ? new String[]{tableNameWithAlias, null}
            : new String[]{tableNameWithAlias.substring(0, index), tableNameWithAlias.substring(index + 1)};
    }

    public static String tryTrim(String string) {
        return string != null ? string.trim() : null;
    }

    public static String substringAfterLast(String text, String prefix) {
        if (text == null) {
            return null;
        }
        if (prefix == null) {
            return text;
        }
        return text.substring(text.lastIndexOf(prefix) + 1);
    }


}
