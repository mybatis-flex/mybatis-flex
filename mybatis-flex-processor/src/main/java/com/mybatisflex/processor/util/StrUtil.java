/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern PACKAGE_REGEX = Pattern.compile("(?<expression>\\$\\{entityPackage[.parent]*\\})(?<subPackage>.*)");

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
                char prev = str.charAt(i - 1);
                if (!Character.isUpperCase(prev) && prev != '_') {
                    sb.append('_');
                }
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

    /**
     * 解析包名表达式
     * <p>将{@code `${entityPackage}`}替换为实际实体包名, 表达式中如果存在一个{@code `.parent`}则缩减包名末尾的一位。</p>
     * <p>示例：{@code `entityClass = com.test1.test2`}<br>
     * 1. 对于{@code `packageStr = ${entityPackage}`}处理结果为 {@code `com.test1.test2`}<br>
     * 2. 对于{@code `packageStr = ${entityPackage.parent}`}处理结果为 {@code `com.test1`}<br>
     * 3. 对于{@code `packageStr = ${entityPackage.parent}.customize`}处理结果为 {@code `com.test1.customize`}
     * </p>
     */
    public static String processPackageExpression(String entityClass, String packageStr) {
        String entityPackage = entityClass.substring(0, entityClass.lastIndexOf("."));
        Matcher matcher = PACKAGE_REGEX.matcher(packageStr);
        if (!matcher.find()) {
            return entityPackage;
        }
        String expression = matcher.group("expression");
        expression = expression.substring(2, expression.length() - 1);
        String subPackage = matcher.group("subPackage");
        List<String> entityPackageSplit = Arrays.asList(entityPackage.split("\\."));
        while (expression.contains(".parent")) {
            if (entityPackageSplit.size() == 0) {
                throw new RuntimeException("Expression [.parent] has exceeded the maximum limit.");
            }
            int index = expression.lastIndexOf(".parent");
            if (index != -1) {
                expression = expression.substring(0, index);
                entityPackageSplit = entityPackageSplit.subList(0, entityPackageSplit.size() - 1);
            }
        }
        expression = expression.replace("entityPackage", String.join(".", entityPackageSplit));
        return expression + subPackage;
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
