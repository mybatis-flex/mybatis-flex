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

package com.mybatisflex.processor.util;

/**
 * 字符串工具类。
 *
 * @author 王帅
 * @since 2023-06-22
 */
@SuppressWarnings("all")
public class StrUtil {

    private StrUtil() {
    }

    /**
     * com.mybatisflex.test.entity.Account -> Account
     */
    public static String getClassName(String str) {
        return str.substring(str.lastIndexOf(".") + 1);
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String camelToUnderline(String str) {
        if (isBlank(str)) {
            return "";
        }
        int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    public static String buildFieldName(String name, String tableDefPropertiesNameStyle) {
        if ("upperCase".equalsIgnoreCase(tableDefPropertiesNameStyle)) {
            return camelToUnderline(name).toUpperCase();
        } else if ("lowerCase".equalsIgnoreCase(tableDefPropertiesNameStyle)) {
            return camelToUnderline(name).toLowerCase();
        } else if ("upperCamelCase".equalsIgnoreCase(tableDefPropertiesNameStyle)) {
            return firstCharToUpperCase(name);
        } else {
            //lowerCamelCase
            return firstCharToLowerCase(name);
        }
    }

    public static String buildTableDefPackage(String entityClass) {
        StringBuilder guessPackage = new StringBuilder();
        if (!entityClass.contains(".")) {
            guessPackage.append("table");
        } else {
            guessPackage.append(entityClass, 0, entityClass.lastIndexOf(".")).append(".table");
        }
        return guessPackage.toString();
    }

    public static String buildMapperPackage(String entityClass) {
        if (!entityClass.contains(".")) {
            return "mapper";
        } else {
            String entityPackage = entityClass.substring(0, entityClass.lastIndexOf("."));
            if (entityPackage.contains(".")) {
                return entityPackage.substring(0, entityPackage.lastIndexOf(".")) + ".mapper";
            } else {
                return "mapper";
            }
        }
    }


    public static boolean isGetterMethod(String methodName, String property) {
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return firstCharToUpperCase(property).concat("()").equals(methodName.substring(3));
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return firstCharToUpperCase(property).concat("()").equals(methodName.substring(2));
        } else {
            return false;
        }
    }

}