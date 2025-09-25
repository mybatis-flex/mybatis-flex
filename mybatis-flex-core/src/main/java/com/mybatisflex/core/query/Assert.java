package com.mybatisflex.core.query;

import com.mybatisflex.core.util.StringUtil;

public class Assert {


    private Assert() {
    }


    /**
     * 断言表达式为 true
     *
     * @param <T>        要返回的值类型
     * @param value      要返回的值
     * @param expression 断言表达式
     * @return 原始值
     * @throws IllegalArgumentException 当表达式为 false 时抛出
     */
    public static <T> T that(T value, boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException("Assertion failed");
        }
        return value;
    }

    /**
     * 断言表达式为 true，成功返回指定的值
     *
     * @param <T>        要返回的值类型
     * @param value      要返回的值
     * @param expression 断言表达式
     * @param message    异常消息
     * @return 原始值
     * @throws IllegalArgumentException 当表达式为 false 时抛出
     */
    public static <T> T that(T value, boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message != null ? message : "Assertion failed");
        }
        return value;
    }


    /**
     * 断言字符串不为空白（延迟计算异常消息）
     *
     * @param text    要验证的字符串
     * @param message 异常消息
     * @return 原始字符串
     * @throws IllegalArgumentException 当字符串为空白时抛出
     */
    public static String hasText(String text, String message) {
        if (StringUtil.noText(text)) {
            throw new IllegalArgumentException(
                StringUtil.hasText(message) ? message : "value must have text"
            );
        }
        return text;
    }


    /**
     * 断言字符串不为空白
     *
     * @param text 要验证的参数
     * @return 原始字符串
     * @throws IllegalArgumentException 当字符串为空白时抛出
     */
    public static boolean hasText(String text) {
        if (StringUtil.noText(text)) {
            throw new IllegalArgumentException("value must have text");
        }
        return true;
    }


    /**
     * 断言对象不为空
     *
     * @param obj 要验证的参数
     * @return 原始对象
     * @throws IllegalArgumentException 当对象为空时抛出
     */
    public static boolean notNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        return true;
    }

    /**
     * 断言对象不为空
     *
     * @param obj     要验证的参数
     * @param message 异常消息
     * @return 原始对象
     * @throws IllegalArgumentException 当对象为空时抛出
     */
    public static boolean notNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(StringUtil.hasText(message) ? message : "value must not be null");
        }
        return true;
    }
}
