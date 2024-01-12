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

import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.LambdaGetter;
import com.mybatisflex.core.util.LambdaUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mybatisflex.core.constant.FuncName.*;

/**
 * SQL 函数。
 */
@SuppressWarnings("unused")
public class QueryMethods {

    private QueryMethods() {
    }

    // === 数学函数 ===

    /**
     * 返回 x 的绝对值。
     */
    public static QueryColumn abs(String columnX) {
        return new FunctionQueryColumn(ABS, columnX);
    }

    /**
     * 返回 x 的绝对值。
     */
    public static QueryColumn abs(QueryColumn columnX) {
        return new FunctionQueryColumn(ABS, columnX);
    }

    /**
     * 返回 x 的绝对值。
     */
    public static <T> QueryColumn abs(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(ABS, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回大于或等于 x 的最小整数（向上取整）。
     */
    public static QueryColumn ceil(String columnX) {
        return new FunctionQueryColumn(CEIL, columnX);
    }

    /**
     * 返回大于或等于 x 的最小整数（向上取整）。
     */
    public static QueryColumn ceil(QueryColumn columnX) {
        return new FunctionQueryColumn(CEIL, columnX);
    }

    /**
     * 返回大于或等于 x 的最小整数（向上取整）。
     */
    public static <T> QueryColumn ceil(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(CEIL, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回大于或等于 x 的最小整数（向上取整）。
     */
    public static QueryColumn ceiling(String columnX) {
        return new FunctionQueryColumn(CEILING, columnX);
    }

    /**
     * 返回大于或等于 x 的最小整数（向上取整）。
     */
    public static QueryColumn ceiling(QueryColumn columnX) {
        return new FunctionQueryColumn(CEILING, columnX);
    }

    /**
     * 返回大于或等于 x 的最小整数（向上取整）。
     */
    public static <T> QueryColumn ceiling(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(CEILING, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回小于或等于 x 的最大整数（向下取整）。
     */
    public static QueryColumn floor(String columnX) {
        return new FunctionQueryColumn(FLOOR, columnX);
    }

    /**
     * 返回小于或等于 x 的最大整数（向下取整）。
     */
    public static QueryColumn floor(QueryColumn columnX) {
        return new FunctionQueryColumn(FLOOR, columnX);
    }

    /**
     * 返回小于或等于 x 的最大整数（向下取整）。
     */
    public static <T> QueryColumn floor(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(FLOOR, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回 0~1 的随机数。
     */
    public static QueryColumn rand() {
        return new FunctionQueryColumn(RAND);
    }

    /**
     * 返回 0~1 的随机数，x 值相同时返回的随机数相同。
     */
    public static QueryColumn rand(String columnX) {
        return new FunctionQueryColumn(RAND, columnX);
    }

    /**
     * 返回 0~1 的随机数，x 值相同时返回的随机数相同。
     */
    public static QueryColumn rand(QueryColumn columnX) {
        return new FunctionQueryColumn(RAND, columnX);
    }

    /**
     * 返回 0~1 的随机数，x 值相同时返回的随机数相同。
     */
    public static <T> QueryColumn rand(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(RAND, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回 x 的符号，x 是负数、0、正数分别返回 -1、0、1。
     */
    public static QueryColumn sign(String columnX) {
        return new FunctionQueryColumn(SIGN, columnX);
    }

    /**
     * 返回 x 的符号，x 是负数、0、正数分别返回 -1、0、1。
     */
    public static QueryColumn sign(QueryColumn columnX) {
        return new FunctionQueryColumn(SIGN, columnX);
    }

    /**
     * 返回 x 的符号，x 是负数、0、正数分别返回 -1、0、1。
     */
    public static <T> QueryColumn sign(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(SIGN, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回圆周率。
     */
    public static QueryColumn pi() {
        return new FunctionQueryColumn(PI);
    }

    /**
     * 返回数值 x 保留到小数点后 y 位的值。
     */
    public static QueryColumn truncate(String columnX, String columnY) {
        return new FunctionQueryColumn(TRUNCATE, columnX, columnY);
    }

    /**
     * 返回数值 x 保留到小数点后 y 位的值。
     */
    public static QueryColumn truncate(QueryColumn columnX, QueryColumn columnY) {
        return new FunctionQueryColumn(TRUNCATE, columnX, columnY);
    }

    /**
     * 返回数值 x 保留到小数点后 y 位的值。
     */
    public static <X, Y> QueryColumn truncate(LambdaGetter<X> columnX, LambdaGetter<Y> columnY) {
        return new FunctionQueryColumn(TRUNCATE, LambdaUtil.getQueryColumn(columnX), LambdaUtil.getQueryColumn(columnY));
    }

    /**
     * 返回数值 x 保留到小数点后 y 位的值。
     */
    public static QueryColumn truncate(String columnX, Integer y) {
        return new FunctionQueryColumn(TRUNCATE, new QueryColumn(columnX), number(y));
    }

    /**
     * 返回数值 x 保留到小数点后 y 位的值。
     */
    public static QueryColumn truncate(QueryColumn columnX, Integer y) {
        return new FunctionQueryColumn(TRUNCATE, columnX, number(y));
    }

    /**
     * 返回数值 x 保留到小数点后 y 位的值。
     */
    public static <T> QueryColumn truncate(LambdaGetter<T> columnX, Integer y) {
        return new FunctionQueryColumn(TRUNCATE, LambdaUtil.getQueryColumn(columnX), number(y));
    }

    /**
     * 返回离 x 最近的整数（四舍五入）。
     */
    public static QueryColumn round(String columnX) {
        return new FunctionQueryColumn(ROUND, columnX);
    }

    /**
     * 返回离 x 最近的整数（四舍五入）。
     */
    public static QueryColumn round(QueryColumn columnX) {
        return new FunctionQueryColumn(ROUND, columnX);
    }

    /**
     * 返回离 x 最近的整数（四舍五入）。
     */
    public static <T> QueryColumn round(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(ROUND, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 保留 x 小数点后 y 位的值，但截断时要四舍五入。
     */
    public static QueryColumn round(String columnX, String columnY) {
        return new FunctionQueryColumn(ROUND, columnX, columnY);
    }

    /**
     * 保留 x 小数点后 y 位的值，但截断时要四舍五入。
     */
    public static QueryColumn round(QueryColumn columnX, QueryColumn columnY) {
        return new FunctionQueryColumn(ROUND, columnX, columnY);
    }

    /**
     * 保留 x 小数点后 y 位的值，但截断时要四舍五入。
     */
    public static <X, Y> QueryColumn round(LambdaGetter<X> columnX, LambdaGetter<Y> columnY) {
        return new FunctionQueryColumn(ROUND, LambdaUtil.getQueryColumn(columnX), LambdaUtil.getQueryColumn(columnY));
    }

    /**
     * 保留 x 小数点后 y 位的值，但截断时要四舍五入。
     */
    public static QueryColumn round(String columnX, Integer y) {
        return new FunctionQueryColumn(ROUND, new QueryColumn(columnX), number(y));
    }

    /**
     * 保留 x 小数点后 y 位的值，但截断时要四舍五入。
     */
    public static QueryColumn round(QueryColumn columnX, Integer y) {
        return new FunctionQueryColumn(ROUND, columnX, number(y));
    }

    /**
     * 保留 x 小数点后 y 位的值，但截断时要四舍五入。
     */
    public static <T> QueryColumn round(LambdaGetter<T> columnX, Integer y) {
        return new FunctionQueryColumn(ROUND, LambdaUtil.getQueryColumn(columnX), number(y));
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static QueryColumn pow(String columnX, String columnY) {
        return new FunctionQueryColumn(POW, columnX, columnY);
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static QueryColumn pow(QueryColumn columnX, QueryColumn columnY) {
        return new FunctionQueryColumn(POW, columnX, columnY);
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static <X, Y> QueryColumn pow(LambdaGetter<X> columnX, LambdaGetter<Y> columnY) {
        return new FunctionQueryColumn(POW, LambdaUtil.getQueryColumn(columnX), LambdaUtil.getQueryColumn(columnY));
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static QueryColumn pow(String columnX, Integer y) {
        return new FunctionQueryColumn(POW, new QueryColumn(columnX), number(y));
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static QueryColumn pow(QueryColumn columnX, Integer y) {
        return new FunctionQueryColumn(POW, columnX, number(y));
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static <T> QueryColumn pow(LambdaGetter<T> columnX, Integer y) {
        return new FunctionQueryColumn(POW, LambdaUtil.getQueryColumn(columnX), number(y));
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static QueryColumn power(String columnX, String columnY) {
        return new FunctionQueryColumn(POWER, columnX, columnY);
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static QueryColumn power(QueryColumn columnX, QueryColumn columnY) {
        return new FunctionQueryColumn(POWER, columnX, columnY);
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static <X, Y> QueryColumn power(LambdaGetter<X> columnX, LambdaGetter<Y> columnY) {
        return new FunctionQueryColumn(POWER, LambdaUtil.getQueryColumn(columnX), LambdaUtil.getQueryColumn(columnY));
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static QueryColumn power(String columnX, Integer y) {
        return new FunctionQueryColumn(POWER, new QueryColumn(columnX), number(y));
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static QueryColumn power(QueryColumn columnX, Integer y) {
        return new FunctionQueryColumn(POWER, columnX, number(y));
    }

    /**
     * 返回 x 的 y 次方。
     */
    public static <T> QueryColumn power(LambdaGetter<T> columnX, Integer y) {
        return new FunctionQueryColumn(POWER, LambdaUtil.getQueryColumn(columnX), number(y));
    }

    /**
     * 返回 x 的平方根。
     */
    public static QueryColumn sqrt(String columnX) {
        return new FunctionQueryColumn(SQRT, columnX);
    }

    /**
     * 返回 x 的平方根。
     */
    public static QueryColumn sqrt(QueryColumn columnX) {
        return new FunctionQueryColumn(SQRT, columnX);
    }

    /**
     * 返回 x 的平方根。
     */
    public static <T> QueryColumn sqrt(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(SQRT, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回 e 的 x 次方。
     */
    public static QueryColumn exp(String columnX) {
        return new FunctionQueryColumn(EXP, columnX);
    }

    /**
     * 返回 e 的 x 次方。
     */
    public static QueryColumn exp(QueryColumn columnX) {
        return new FunctionQueryColumn(EXP, columnX);
    }

    /**
     * 返回 e 的 x 次方。
     */
    public static <T> QueryColumn exp(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(EXP, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回 x 除以 y 以后的余数。
     */
    public static QueryColumn mod(String columnX, String columnY) {
        return new FunctionQueryColumn(MOD, columnX, columnY);
    }

    /**
     * 返回 x 除以 y 以后的余数。
     */
    public static QueryColumn mod(QueryColumn columnX, QueryColumn columnY) {
        return new FunctionQueryColumn(MOD, columnX, columnY);
    }

    /**
     * 返回 x 除以 y 以后的余数。
     */
    public static <X, Y> QueryColumn mod(LambdaGetter<X> columnX, LambdaGetter<Y> columnY) {
        return new FunctionQueryColumn(MOD, LambdaUtil.getQueryColumn(columnX), LambdaUtil.getQueryColumn(columnY));
    }

    /**
     * 返回 x 除以 y 以后的余数。
     */
    public static QueryColumn mod(String columnX, Integer y) {
        return new FunctionQueryColumn(MOD, new QueryColumn(columnX), number(y));
    }

    /**
     * 返回 x 除以 y 以后的余数。
     */
    public static QueryColumn mod(QueryColumn columnX, Integer y) {
        return new FunctionQueryColumn(MOD, columnX, number(y));
    }

    /**
     * 返回 x 除以 y 以后的余数。
     */
    public static <T> QueryColumn mod(LambdaGetter<T> columnX, Integer y) {
        return new FunctionQueryColumn(MOD, LambdaUtil.getQueryColumn(columnX), number(y));
    }

    /**
     * 返回自然对数（以 e 为底的对数）。
     */
    public static QueryColumn log(String columnX) {
        return new FunctionQueryColumn(LOG, columnX);
    }

    /**
     * 返回自然对数（以 e 为底的对数）。
     */
    public static QueryColumn log(QueryColumn columnX) {
        return new FunctionQueryColumn(LOG, columnX);
    }

    /**
     * 返回自然对数（以 e 为底的对数）。
     */
    public static <T> QueryColumn log(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(LOG, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回以 10 为底的对数。
     */
    public static QueryColumn log10(String columnX) {
        return new FunctionQueryColumn(LOG10, columnX);
    }

    /**
     * 返回以 10 为底的对数。
     */
    public static QueryColumn log10(QueryColumn columnX) {
        return new FunctionQueryColumn(LOG10, columnX);
    }

    /**
     * 返回以 10 为底的对数。
     */
    public static <T> QueryColumn log10(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(LOG10, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 将角度转换为弧度。
     */
    public static QueryColumn radians(String columnX) {
        return new FunctionQueryColumn(RADIANS, columnX);
    }

    /**
     * 将角度转换为弧度。
     */
    public static QueryColumn radians(QueryColumn columnX) {
        return new FunctionQueryColumn(RADIANS, columnX);
    }

    /**
     * 将角度转换为弧度。
     */
    public static <T> QueryColumn radians(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(RADIANS, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 将弧度转换为角度。
     */
    public static QueryColumn degrees(String columnX) {
        return new FunctionQueryColumn(DEGREES, columnX);
    }

    /**
     * 将弧度转换为角度。
     */
    public static QueryColumn degrees(QueryColumn columnX) {
        return new FunctionQueryColumn(DEGREES, columnX);
    }

    /**
     * 将弧度转换为角度。
     */
    public static <T> QueryColumn degrees(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(DEGREES, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 求正弦值。
     */
    public static QueryColumn sin(String columnX) {
        return new FunctionQueryColumn(SIN, columnX);
    }

    /**
     * 求正弦值。
     */
    public static QueryColumn sin(QueryColumn columnX) {
        return new FunctionQueryColumn(SIN, columnX);
    }

    /**
     * 求正弦值。
     */
    public static <T> QueryColumn sin(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(SIN, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 求反正弦值。
     */
    public static QueryColumn asin(String columnX) {
        return new FunctionQueryColumn(ASIN, columnX);
    }

    /**
     * 求反正弦值。
     */
    public static QueryColumn asin(QueryColumn columnX) {
        return new FunctionQueryColumn(ASIN, columnX);
    }

    /**
     * 求反正弦值。
     */
    public static <T> QueryColumn asin(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(ASIN, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 求余弦值。
     */
    public static QueryColumn cos(String columnX) {
        return new FunctionQueryColumn(COS, columnX);
    }

    /**
     * 求余弦值。
     */
    public static QueryColumn cos(QueryColumn columnX) {
        return new FunctionQueryColumn(COS, columnX);
    }

    /**
     * 求余弦值。
     */
    public static <T> QueryColumn cos(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(COS, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 求反余弦值。
     */
    public static QueryColumn acos(String columnX) {
        return new FunctionQueryColumn(ACOS, columnX);
    }

    /**
     * 求反余弦值。
     */
    public static QueryColumn acos(QueryColumn columnX) {
        return new FunctionQueryColumn(ACOS, columnX);
    }

    /**
     * 求反余弦值。
     */
    public static <T> QueryColumn acos(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(ACOS, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 求正切值。
     */
    public static QueryColumn tan(String columnX) {
        return new FunctionQueryColumn(TAN, columnX);
    }

    /**
     * 求正切值。
     */
    public static QueryColumn tan(QueryColumn columnX) {
        return new FunctionQueryColumn(TAN, columnX);
    }

    /**
     * 求正切值。
     */
    public static <T> QueryColumn tan(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(TAN, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 求反正切值。
     */
    public static QueryColumn atan(String columnX) {
        return new FunctionQueryColumn(ATAN, columnX);
    }

    /**
     * 求反正切值。
     */
    public static QueryColumn atan(QueryColumn columnX) {
        return new FunctionQueryColumn(ATAN, columnX);
    }

    /**
     * 求反正切值。
     */
    public static <T> QueryColumn atan(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(ATAN, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 求余切值。
     */
    public static QueryColumn cot(String columnX) {
        return new FunctionQueryColumn(COT, columnX);
    }

    /**
     * 求余切值。
     */
    public static QueryColumn cot(QueryColumn columnX) {
        return new FunctionQueryColumn(COT, columnX);
    }

    /**
     * 求余切值。
     */
    public static <T> QueryColumn cot(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(COT, LambdaUtil.getQueryColumn(columnX));
    }

    // === 字符串函数 ===

    /**
     * 返回字符串 s 的字符数。
     */
    public static QueryColumn charLength(String columnS) {
        return new FunctionQueryColumn(CHAR_LENGTH, columnS);
    }

    /**
     * 返回字符串 s 的字符数。
     */
    public static QueryColumn charLength(QueryColumn columnS) {
        return new FunctionQueryColumn(CHAR_LENGTH, columnS);
    }

    /**
     * 返回字符串 s 的字符数。
     */
    public static <T> QueryColumn charLength(LambdaGetter<T> columnS) {
        return new FunctionQueryColumn(CHAR_LENGTH, LambdaUtil.getQueryColumn(columnS));
    }

    /**
     * 返回字符串 s 的长度。
     */
    public static QueryColumn length(String columnS) {
        return new FunctionQueryColumn(LENGTH, columnS);
    }

    /**
     * 返回字符串 s 的长度。
     */
    public static QueryColumn length(QueryColumn columnS) {
        return new FunctionQueryColumn(LENGTH, columnS);
    }

    /**
     * 返回字符串 s 的长度。
     */
    public static <T> QueryColumn length(LambdaGetter<T> columnS) {
        return new FunctionQueryColumn(LENGTH, LambdaUtil.getQueryColumn(columnS));
    }

    /**
     * 将字符串 s1，s2 等多个字符串合并为一个字符串。
     */
    public static QueryColumn concat(String columnS1, String columnS2, String... columnN) {
        return new FunctionQueryColumn(CONCAT, ArrayUtil.concat(new String[]{columnS1, columnS2}, columnN));
    }

    /**
     * 将字符串 s1，s2 等多个字符串合并为一个字符串。
     */
    public static QueryColumn concat(QueryColumn columnS1, QueryColumn columnS2, QueryColumn... columnN) {
        return new FunctionQueryColumn(CONCAT, ArrayUtil.concat(new QueryColumn[]{columnS1, columnS2}, columnN));
    }

    /**
     * 同 CONCAT(s1, s2, ...)，但是每个字符串之间要加上 x。
     */
    public static QueryColumn concatWs(String columnX, String columnS1, String columnS2, String... columnN) {
        return new FunctionQueryColumn(CONCAT_WS, ArrayUtil.concat(new String[]{columnX, columnS1, columnS2}, columnN));
    }

    /**
     * 同 CONCAT(s1, s2, ...)，但是每个字符串之间要加上 x。
     */
    public static QueryColumn concatWs(QueryColumn columnX, QueryColumn columnS1, QueryColumn columnS2, QueryColumn... columnN) {
        return new FunctionQueryColumn(CONCAT_WS, ArrayUtil.concat(new QueryColumn[]{columnX, columnS1, columnS2}, columnN));
    }

    /**
     * 将字符串 s2 替换 s1 的 x 位置开始长度为 len 的字符串。
     */
    public static QueryColumn insert(String columnS1, String columnX, String columnLen, String columnS2) {
        return new FunctionQueryColumn(INSERT, columnS1, columnX, columnLen, columnS2);
    }

    /**
     * 将字符串 s2 替换 s1 的 x 位置开始长度为 len 的字符串。
     */
    public static QueryColumn insert(QueryColumn columnS1, QueryColumn columnX, QueryColumn columnLen, QueryColumn columnS2) {
        return new FunctionQueryColumn(INSERT, columnS1, columnX, columnLen, columnS2);
    }

    /**
     * 将字符串 s 的所有字符都变成大写字母。
     */
    public static QueryColumn upper(String columnS) {
        return new FunctionQueryColumn(UPPER, columnS);
    }

    /**
     * 将字符串 s 的所有字符都变成大写字母。
     */
    public static QueryColumn upper(QueryColumn columnS) {
        return new FunctionQueryColumn(UPPER, columnS);
    }

    /**
     * 将字符串 s 的所有字符都变成大写字母。
     */
    public static <T> QueryColumn upper(LambdaGetter<T> columnS) {
        return new FunctionQueryColumn(UPPER, LambdaUtil.getQueryColumn(columnS));
    }

    /**
     * 将字符串 s 的所有字符都变成小写字母。
     */
    public static QueryColumn lower(String columnS) {
        return new FunctionQueryColumn(LOWER, columnS);
    }

    /**
     * 将字符串 s 的所有字符都变成小写字母。
     */
    public static QueryColumn lower(QueryColumn columnS) {
        return new FunctionQueryColumn(LOWER, columnS);
    }

    /**
     * 将字符串 s 的所有字符都变成小写字母。
     */
    public static <T> QueryColumn lower(LambdaGetter<T> columnS) {
        return new FunctionQueryColumn(LOWER, LambdaUtil.getQueryColumn(columnS));
    }

    /**
     * 返回字符串 s 的前 n 个字符。
     */
    public static QueryColumn left(String columnS, int length) {
        return new FunctionQueryColumn(LEFT, string(columnS), number(length));
    }

    /**
     * 返回字符串 s 的前 n 个字符。
     */
    public static QueryColumn left(QueryColumn columnS, int length) {
        return new FunctionQueryColumn(LEFT, columnS, number(length));
    }

    /**
     * 返回字符串 s 的前 n 个字符。
     */
    public static <S, N> QueryColumn left(LambdaGetter<S> columnS, int length) {
        return new FunctionQueryColumn(LEFT, LambdaUtil.getQueryColumn(columnS), number(length));
    }


    /**
     * 返回字符串 s 的后 length 个字符。
     */
    public static QueryColumn right(String columnS, int length) {
        return new FunctionQueryColumn(RIGHT, string(columnS), number(length));
    }

    /**
     * 返回字符串 s 的后 length 个字符。
     */
    public static QueryColumn right(QueryColumn columnS, int length) {
        return new FunctionQueryColumn(RIGHT, columnS, number(length));
    }

    /**
     * 返回字符串 s 的后 length 个字符。
     */
    public static <S, N> QueryColumn right(LambdaGetter<S> columnS, int length) {
        return new FunctionQueryColumn(RIGHT, LambdaUtil.getQueryColumn(columnS), number(length));
    }

    /**
     * 字符串 s2 来填充 s1 的开始处，使字符串长度达到 len。
     */
    public static QueryColumn lpad(String columnS1, String columnLen, String columnS2) {
        return new FunctionQueryColumn(LPAD, columnS1, columnLen, columnS2);
    }

    /**
     * 字符串 s2 来填充 s1 的开始处，使字符串长度达到 len。
     */
    public static QueryColumn lpad(QueryColumn columnS1, QueryColumn columnLen, QueryColumn columnS2) {
        return new FunctionQueryColumn(LPAD, columnS1, columnLen, columnS2);
    }

    /**
     * 字符串 s2 来填充 s1 的开始处，使字符串长度达到 len。
     */
    public static <S1, L, S2> QueryColumn lpad(LambdaGetter<S1> columnS1, LambdaGetter<L> columnLen, LambdaGetter<S2> columnS2) {
        return new FunctionQueryColumn(LPAD, LambdaUtil.getQueryColumn(columnS1), LambdaUtil.getQueryColumn(columnLen), LambdaUtil.getQueryColumn(columnS2));
    }

    /**
     * 字符串 s2 来填充 s1 的结尾处，使字符串长度达到 len。
     */
    public static QueryColumn rpad(String columnS1, String columnLen, String columnS2) {
        return new FunctionQueryColumn(RPAD, columnS1, columnLen, columnS2);
    }

    /**
     * 字符串 s2 来填充 s1 的结尾处，使字符串长度达到 len。
     */
    public static QueryColumn rpad(QueryColumn columnS1, QueryColumn columnLen, QueryColumn columnS2) {
        return new FunctionQueryColumn(RPAD, columnS1, columnLen, columnS2);
    }

    /**
     * 字符串 s2 来填充 s1 的结尾处，使字符串长度达到 len。
     */
    public static <S1, L, S2> QueryColumn rpad(LambdaGetter<S1> columnS1, LambdaGetter<L> columnLen, LambdaGetter<S2> columnS2) {
        return new FunctionQueryColumn(RPAD, LambdaUtil.getQueryColumn(columnS1), LambdaUtil.getQueryColumn(columnLen), LambdaUtil.getQueryColumn(columnS2));
    }

    /**
     * 去掉字符串 s 开始处的空格。
     */
    public static QueryColumn ltrim(String columnS) {
        return new FunctionQueryColumn(LTRIM, columnS);
    }

    /**
     * 去掉字符串 s 开始处的空格。
     */
    public static QueryColumn ltrim(QueryColumn columnS) {
        return new FunctionQueryColumn(LTRIM, columnS);
    }

    /**
     * 去掉字符串 s 开始处的空格。
     */
    public static <T> QueryColumn ltrim(LambdaGetter<T> columnS) {
        return new FunctionQueryColumn(LTRIM, LambdaUtil.getQueryColumn(columnS));
    }

    /**
     * 去掉字符串 s 结尾处的空格。
     */
    public static QueryColumn rtrim(String columnS) {
        return new FunctionQueryColumn(RTRIM, columnS);
    }

    /**
     * 去掉字符串 s 结尾处的空格。
     */
    public static QueryColumn rtrim(QueryColumn columnS) {
        return new FunctionQueryColumn(RTRIM, columnS);
    }

    /**
     * 去掉字符串 s 结尾处的空格。
     */
    public static <T> QueryColumn rtrim(LambdaGetter<T> columnS) {
        return new FunctionQueryColumn(RTRIM, LambdaUtil.getQueryColumn(columnS));
    }

    /**
     * 去掉字符串 s 开始处和结尾处的空格。
     */
    public static QueryColumn trim(QueryColumn columnS) {
        return new FunctionQueryColumn(TRIM, columnS);
    }

    /**
     * 将字符串 s 重复 n 次。
     */
    public static QueryColumn repeat(String columnS, String columnN) {
        return new FunctionQueryColumn(REPEAT, columnS, columnN);
    }

    /**
     * 将字符串 s 重复 n 次。
     */
    public static QueryColumn repeat(QueryColumn columnS, QueryColumn columnN) {
        return new FunctionQueryColumn(REPEAT, columnS, columnN);
    }

    /**
     * 将字符串 s 重复 n 次。
     */
    public static <S, N> QueryColumn repeat(LambdaGetter<S> columnS, LambdaGetter<N> columnN) {
        return new FunctionQueryColumn(REPEAT, LambdaUtil.getQueryColumn(columnS), LambdaUtil.getQueryColumn(columnN));
    }

    /**
     * 将字符串 s 重复 n 次。
     */
    public static QueryColumn repeat(String columnX, Integer n) {
        return new FunctionQueryColumn(REPEAT, new QueryColumn(columnX), number(n));
    }

    /**
     * 将字符串 s 重复 n 次。
     */
    public static QueryColumn repeat(QueryColumn columnX, Integer n) {
        return new FunctionQueryColumn(REPEAT, columnX, number(n));
    }

    /**
     * 将字符串 s 重复 n 次。
     */
    public static <T> QueryColumn repeat(LambdaGetter<T> columnX, Integer n) {
        return new FunctionQueryColumn(REPEAT, LambdaUtil.getQueryColumn(columnX), number(n));
    }

    /**
     * 返回 n 个空格。
     */
    public static QueryColumn space(String columnN) {
        return new FunctionQueryColumn(SPACE, columnN);
    }

    /**
     * 返回 n 个空格。
     */
    public static QueryColumn space(QueryColumn columnN) {
        return new FunctionQueryColumn(SPACE, columnN);
    }

    /**
     * 返回 n 个空格。
     */
    public static <T> QueryColumn space(LambdaGetter<T> columnN) {
        return new FunctionQueryColumn(SPACE, LambdaUtil.getQueryColumn(columnN));
    }

    /**
     * 用字符串 s2 代替字符串 s 中的字符串 s1。
     */
    public static QueryColumn replace(String columnS, String columnS1, String columnS2) {
        return new StringFunctionQueryColumn(REPLACE, columnS, columnS1, columnS2);
    }

    /**
     * 用字符串 s2 代替字符串 s 中的字符串 s1。
     */
    public static QueryColumn replace(QueryColumn columnS, QueryColumn columnS1, QueryColumn columnS2) {
        return new FunctionQueryColumn(REPLACE, columnS, columnS1, columnS2);
    }

    /**
     * 用字符串 s2 代替字符串 s 中的字符串 s1。
     */
    public static <S, S1, S2> QueryColumn replace(LambdaGetter<S> columnS, LambdaGetter<S1> columnS1, LambdaGetter<S2> columnS2) {
        return new FunctionQueryColumn(REPLACE, LambdaUtil.getQueryColumn(columnS), LambdaUtil.getQueryColumn(columnS1), LambdaUtil.getQueryColumn(columnS2));
    }

    /**
     * 比较字符串 s1 和 s2。
     */
    public static QueryColumn strcmp(String columnS1, String columnS2) {
        return new FunctionQueryColumn(STRCMP, columnS1, columnS2);
    }

    /**
     * 比较字符串 s1 和 s2。
     */
    public static QueryColumn strcmp(QueryColumn columnS1, QueryColumn columnS2) {
        return new FunctionQueryColumn(STRCMP, columnS1, columnS2);
    }

    /**
     * 比较字符串 s1 和 s2。
     */
    public static <S1, S2> QueryColumn strcmp(LambdaGetter<S1> columnS1, LambdaGetter<S2> columnS2) {
        return new FunctionQueryColumn(STRCMP, LambdaUtil.getQueryColumn(columnS1), LambdaUtil.getQueryColumn(columnS2));
    }


    /**
     * 获取从字符串 s 中的第 position 个位置开始长度为 length 的字符串。
     */
    public static QueryColumn substring(String columnS, int position) {
        return new FunctionQueryColumn(SUBSTRING, string(columnS), number(position));
    }

    /**
     * 获取从字符串 s 中的第 position 个位置开始长度为 length 的字符串。
     */
    public static QueryColumn substring(QueryColumn columnS, int position) {
        return new FunctionQueryColumn(SUBSTRING, columnS, number(position));
    }

    /**
     * 获取从字符串 s 中的第 position 个位置开始长度为 length 的字符串。
     */
    public static <S, N, L> QueryColumn substring(LambdaGetter<S> columnS, int position) {
        return new FunctionQueryColumn(SUBSTRING, LambdaUtil.getQueryColumn(columnS), number(position));
    }

    /**
     * 获取从字符串 s 中的第 position 个位置开始长度为 length 的字符串。
     */
    public static QueryColumn substring(String columnS, int position, int length) {
        return new FunctionQueryColumn(SUBSTRING, string(columnS), number(position), number(length));
    }

    /**
     * 获取从字符串 s 中的第 position 个位置开始长度为 length 的字符串。
     */
    public static QueryColumn substring(QueryColumn columnS, int position, int length) {
        return new FunctionQueryColumn(SUBSTRING, columnS, number(position), number(length));
    }

    /**
     * 获取从字符串 s 中的第 position 个位置开始长度为 length 的字符串。
     */
    public static <S, N, L> QueryColumn substring(LambdaGetter<S> columnS, int position, int length) {
        return new FunctionQueryColumn(SUBSTRING, LambdaUtil.getQueryColumn(columnS), number(position), number(length));
    }

    /**
     * 从字符串 s 中获取 s1 的开始位置。
     */
    public static QueryColumn instr(String columnS, String columnS1) {
        return new FunctionQueryColumn(INSTR, columnS, columnS1);
    }

    /**
     * 从字符串 s 中获取 s1 的开始位置。
     */
    public static QueryColumn instr(QueryColumn columnS, QueryColumn columnS1) {
        return new FunctionQueryColumn(INSTR, columnS, columnS1);
    }

    /**
     * 从字符串 s 中获取 s1 的开始位置。
     */
    public static <S, S1> QueryColumn instr(LambdaGetter<S> columnS, LambdaGetter<S1> columnS1) {
        return new FunctionQueryColumn(INSTR, LambdaUtil.getQueryColumn(columnS), LambdaUtil.getQueryColumn(columnS1));
    }

    /**
     * 将字符串 s 的顺序反过来。
     */
    public static QueryColumn reverse(String columnS) {
        return new FunctionQueryColumn(REVERSE, columnS);
    }

    /**
     * 将字符串 s 的顺序反过来。
     */
    public static QueryColumn reverse(QueryColumn columnS) {
        return new FunctionQueryColumn(REVERSE, columnS);
    }

    /**
     * 将字符串 s 的顺序反过来。
     */
    public static <T> QueryColumn reverse(LambdaGetter<T> columnS) {
        return new FunctionQueryColumn(REVERSE, LambdaUtil.getQueryColumn(columnS));
    }

    /**
     * 返回第 n 个字符串。
     */
    public static QueryColumn elt(String columnN, String columnS1, String... columnSn) {
        return new FunctionQueryColumn(ELT, ArrayUtil.concat(new String[]{columnN, columnS1}, columnSn));
    }

    /**
     * 返回第 n 个字符串。
     */
    public static QueryColumn elt(QueryColumn columnN, QueryColumn columnS1, QueryColumn... columnSn) {
        return new FunctionQueryColumn(ELT, ArrayUtil.concat(new QueryColumn[]{columnN, columnS1}, columnSn));
    }

    /**
     * 返回第一个与字符串 s 匹配的字符串的位置。
     */
    public static QueryColumn field(String columnS, String columnS1, String... columnSn) {
        return new FunctionQueryColumn(FIELD, ArrayUtil.concat(new String[]{columnS, columnS1}, columnSn));
    }

    /**
     * 返回第一个与字符串 s 匹配的字符串的位置。
     */
    public static QueryColumn field(QueryColumn columnS, QueryColumn columnS1, QueryColumn... columnSn) {
        return new FunctionQueryColumn(FIELD, ArrayUtil.concat(new QueryColumn[]{columnS, columnS1}, columnSn));
    }

    /**
     * 返回在字符串 s2 中与 s1 匹配的字符串的位置。
     */
    public static QueryColumn findInSet(String columnS1, String columnS2) {
        return new FunctionQueryColumn(FIND_IN_SET, columnS1, columnS2);
    }

    /**
     * 返回在字符串 s2 中与 s1 匹配的字符串的位置。
     */
    public static QueryColumn findInSet(QueryColumn columnS1, QueryColumn columnS2) {
        return new FunctionQueryColumn(FIND_IN_SET, columnS1, columnS2);
    }

    /**
     * 返回在字符串 s2 中与 s1 匹配的字符串的位置。
     */
    public static <S1, S2> QueryColumn findInSet(LambdaGetter<S1> columnS1, LambdaGetter<S2> columnS2) {
        return new FunctionQueryColumn(FIND_IN_SET, LambdaUtil.getQueryColumn(columnS1), LambdaUtil.getQueryColumn(columnS2));
    }

    // === 日期时间函数 ===

    /**
     * 返回当前日期。
     */
    public static QueryColumn curDate() {
        return new FunctionQueryColumn(CURDATE);
    }

    /**
     * 返回当前日期。
     */
    public static QueryColumn currentDate() {
        return new FunctionQueryColumn(CURRENT_DATE);
    }

    /**
     * 返回当前时间。
     */
    public static QueryColumn curTime() {
        return new FunctionQueryColumn(CURTIME);
    }

    /**
     * 返回当前时间。
     */
    public static QueryColumn currentTime() {
        return new FunctionQueryColumn(CURRENT_TIME);
    }

    /**
     * 返回当前日期和时间。
     */
    public static QueryColumn now() {
        return new FunctionQueryColumn(NOW);
    }

    /**
     * 返回当前日期和时间。
     */
    public static QueryColumn currentTimestamp() {
        return new FunctionQueryColumn(CURRENT_TIMESTAMP);
    }

    /**
     * 返回当前日期和时间。
     */
    public static QueryColumn localTime() {
        return new FunctionQueryColumn(LOCALTIME);
    }

    /**
     * 返回当前日期和时间。
     */
    public static QueryColumn sysDate() {
        return new FunctionQueryColumn(SYSDATE);
    }


    /**
     * 返回当前日期和时间。
     */
    public static QueryColumn localTimestamp() {
        return new FunctionQueryColumn(LOCALTIMESTAMP);
    }

    /**
     * 以 UNIX 时间戳的形式返回当前时间。
     */
    public static QueryColumn unixTimestamp() {
        return new FunctionQueryColumn(UNIX_TIMESTAMP);
    }

    /**
     * 将时间 d 以 UNIX 时间戳的形式返回。
     */
    public static QueryColumn unixTimestamp(String columnD) {
        return new FunctionQueryColumn(UNIX_TIMESTAMP, columnD);
    }

    /**
     * 将时间 d 以 UNIX 时间戳的形式返回。
     */
    public static QueryColumn unixTimestamp(QueryColumn columnD) {
        return new FunctionQueryColumn(UNIX_TIMESTAMP, columnD);
    }

    /**
     * 将时间 d 以 UNIX 时间戳的形式返回。
     */
    public static <T> QueryColumn unixTimestamp(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(UNIX_TIMESTAMP, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 把 UNIX 时间戳的时间转换为普通格式的时间。
     */
    public static QueryColumn fromUnixTime(String columnD) {
        return new FunctionQueryColumn(FROM_UNIXTIME, columnD);
    }

    /**
     * 把 UNIX 时间戳的时间转换为普通格式的时间。
     */
    public static QueryColumn fromUnixTime(QueryColumn columnD) {
        return new FunctionQueryColumn(FROM_UNIXTIME, columnD);
    }

    /**
     * 把 UNIX 时间戳的时间转换为普通格式的时间。
     */
    public static <T> QueryColumn fromUnixTime(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(FROM_UNIXTIME, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 返回 UTC（国际协调时间）日期。
     */
    public static QueryColumn utcDate() {
        return new FunctionQueryColumn(UTC_DATE);
    }

    /**
     * 返回 UTC 时间。
     */
    public static QueryColumn utcTime() {
        return new FunctionQueryColumn(UTC_TIME);
    }

    /**
     * 返回日期 d 中的月份值，范围是 1~12。
     */
    public static QueryColumn month(String columnD) {
        return new FunctionQueryColumn(MONTH, columnD);
    }

    /**
     * 返回日期 d 中的月份值，范围是 1~12。
     */
    public static QueryColumn month(QueryColumn columnD) {
        return new FunctionQueryColumn(MONTH, columnD);
    }

    /**
     * 返回日期 d 中的月份值，范围是 1~12。
     */
    public static <T> QueryColumn month(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(MONTH, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 返回日期 d 中的月份名称，如 january。
     */
    public static QueryColumn monthName(String columnD) {
        return new FunctionQueryColumn(MONTHNAME, columnD);
    }

    /**
     * 返回日期 d 中的月份名称，如 january。
     */
    public static QueryColumn monthName(QueryColumn columnD) {
        return new FunctionQueryColumn(MONTHNAME, columnD);
    }

    /**
     * 返回日期 d 中的月份名称，如 january。
     */
    public static <T> QueryColumn monthName(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(MONTHNAME, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 返回日期 d 是星期几，如 Monday。
     */
    public static QueryColumn dayName(String columnD) {
        return new FunctionQueryColumn(DAYNAME, columnD);
    }

    /**
     * 返回日期 d 是星期几，如 Monday。
     */
    public static QueryColumn dayName(QueryColumn columnD) {
        return new FunctionQueryColumn(DAYNAME, columnD);
    }

    /**
     * 返回日期 d 是星期几，如 Monday。
     */
    public static <T> QueryColumn dayName(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(DAYNAME, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 返回日期 d 是星期几，1 表示星期日，2 表示星期二。
     */
    public static QueryColumn dayOfWeek(String columnD) {
        return new FunctionQueryColumn(DAYOFWEEK, columnD);
    }

    /**
     * 返回日期 d 是星期几，1 表示星期日，2 表示星期二。
     */
    public static QueryColumn dayOfWeek(QueryColumn columnD) {
        return new FunctionQueryColumn(DAYOFWEEK, columnD);
    }

    /**
     * 返回日期 d 是星期几，1 表示星期日，2 表示星期二。
     */
    public static <T> QueryColumn dayOfWeek(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(DAYOFWEEK, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 返回日期 d 是星期几，0 表示星期一，1 表示星期二。
     */
    public static QueryColumn weekday(String columnD) {
        return new FunctionQueryColumn(WEEKDAY, columnD);
    }

    /**
     * 返回日期 d 是星期几，0 表示星期一，1 表示星期二。
     */
    public static QueryColumn weekday(QueryColumn columnD) {
        return new FunctionQueryColumn(WEEKDAY, columnD);
    }

    /**
     * 返回日期 d 是星期几，0 表示星期一，1 表示星期二。
     */
    public static <T> QueryColumn weekday(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(WEEKDAY, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 计算日期 d 是本年的第几个星期，范围是 0-53。
     */
    public static QueryColumn week(String columnD) {
        return new FunctionQueryColumn(WEEK, columnD);
    }

    /**
     * 计算日期 d 是本年的第几个星期，范围是 0-53。
     */
    public static QueryColumn week(QueryColumn columnD) {
        return new FunctionQueryColumn(WEEK, columnD);
    }

    /**
     * 计算日期 d 是本年的第几个星期，范围是 0-53。
     */
    public static <T> QueryColumn week(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(WEEK, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 计算日期 d 是本年的第几个星期，范围是 1-53。
     */
    public static QueryColumn weekOfYear(String columnD) {
        return new FunctionQueryColumn(WEEKOFYEAR, columnD);
    }

    /**
     * 计算日期 d 是本年的第几个星期，范围是 1-53。
     */
    public static QueryColumn weekOfYear(QueryColumn columnD) {
        return new FunctionQueryColumn(WEEKOFYEAR, columnD);
    }

    /**
     * 计算日期 d 是本年的第几个星期，范围是 1-53。
     */
    public static <T> QueryColumn weekOfYear(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(WEEKOFYEAR, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 计算日期 d 是本年的第几天。
     */
    public static QueryColumn dayOfYear(String columnD) {
        return new FunctionQueryColumn(DAYOFYEAR, columnD);
    }

    /**
     * 计算日期 d 是本年的第几天。
     */
    public static QueryColumn dayOfYear(QueryColumn columnD) {
        return new FunctionQueryColumn(DAYOFYEAR, columnD);
    }

    /**
     * 计算日期 d 是本年的第几天。
     */
    public static <T> QueryColumn dayOfYear(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(DAYOFYEAR, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 计算日期 d 是本月的第几天。
     */
    public static QueryColumn dayOfMonth(String columnD) {
        return new FunctionQueryColumn(DAYOFMONTH, columnD);
    }

    /**
     * 计算日期 d 是本月的第几天。
     */
    public static QueryColumn dayOfMonth(QueryColumn columnD) {
        return new FunctionQueryColumn(DAYOFMONTH, columnD);
    }

    /**
     * 计算日期 d 是本月的第几天。
     */
    public static <T> QueryColumn dayOfMonth(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(DAYOFMONTH, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 返回日期 d 中的年份值。
     */
    public static QueryColumn year(String columnD) {
        return new FunctionQueryColumn(YEAR, columnD);
    }

    /**
     * 返回日期 d 中的年份值。
     */
    public static QueryColumn year(QueryColumn columnD) {
        return new FunctionQueryColumn(YEAR, columnD);
    }

    /**
     * 返回日期 d 中的年份值。
     */
    public static <T> QueryColumn year(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(YEAR, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 返回日期 d 中的天数值。
     */
    public static FunctionQueryColumn day(String columnD) {
        return new FunctionQueryColumn(DAY, columnD);
    }

    /**
     * 返回日期 d 中的天数值。
     */
    public static FunctionQueryColumn day(QueryColumn columnD) {
        return new FunctionQueryColumn(DAY, columnD);
    }

    /**
     * 返回日期 d 中的天数值。
     */
    public static <T> FunctionQueryColumn day(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(DAY, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 返回日期 d 是第几季度，范围 1-4。
     */
    public static QueryColumn quarter(String columnD) {
        return new FunctionQueryColumn(QUARTER, columnD);
    }

    /**
     * 返回日期 d 是第几季度，范围 1-4。
     */
    public static QueryColumn quarter(QueryColumn columnD) {
        return new FunctionQueryColumn(QUARTER, columnD);
    }

    /**
     * 返回日期 d 是第几季度，范围 1-4。
     */
    public static <T> QueryColumn quarter(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(QUARTER, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 返回时间 t 中的小时值。
     */
    public static QueryColumn hour(String columnT) {
        return new FunctionQueryColumn(HOUR, columnT);
    }

    /**
     * 返回时间 t 中的小时值。
     */
    public static QueryColumn hour(QueryColumn columnT) {
        return new FunctionQueryColumn(HOUR, columnT);
    }

    /**
     * 返回时间 t 中的小时值。
     */
    public static <T> QueryColumn hour(LambdaGetter<T> columnT) {
        return new FunctionQueryColumn(HOUR, LambdaUtil.getQueryColumn(columnT));
    }

    /**
     * 返回时间 t 中的分钟值。
     */
    public static QueryColumn minute(String columnT) {
        return new FunctionQueryColumn(MINUTE, columnT);
    }

    /**
     * 返回时间 t 中的分钟值。
     */
    public static QueryColumn minute(QueryColumn columnT) {
        return new FunctionQueryColumn(MINUTE, columnT);
    }

    /**
     * 返回时间 t 中的分钟值。
     */
    public static <T> QueryColumn minute(LambdaGetter<T> columnT) {
        return new FunctionQueryColumn(MINUTE, LambdaUtil.getQueryColumn(columnT));
    }

    /**
     * 返回时间 t 中的秒钟值。
     */
    public static QueryColumn second(String columnT) {
        return new FunctionQueryColumn(SECOND, columnT);
    }

    /**
     * 返回时间 t 中的秒钟值。
     */
    public static QueryColumn second(QueryColumn columnT) {
        return new FunctionQueryColumn(SECOND, columnT);
    }

    /**
     * 返回时间 t 中的秒钟值。
     */
    public static <T> QueryColumn second(LambdaGetter<T> columnT) {
        return new FunctionQueryColumn(SECOND, LambdaUtil.getQueryColumn(columnT));
    }

    /**
     * 将时间 t 转换为秒。
     */
    public static QueryColumn timeToSec(String columnT) {
        return new FunctionQueryColumn(TIME_TO_SEC, columnT);
    }

    /**
     * 将时间 t 转换为秒。
     */
    public static QueryColumn timeToSec(QueryColumn columnT) {
        return new FunctionQueryColumn(TIME_TO_SEC, columnT);
    }

    /**
     * 将时间 t 转换为秒。
     */
    public static <T> QueryColumn timeToSec(LambdaGetter<T> columnT) {
        return new FunctionQueryColumn(TIME_TO_SEC, LambdaUtil.getQueryColumn(columnT));
    }

    /**
     * 将以秒为单位的时间 s 转换为时分秒的格式。
     */
    public static QueryColumn secToTime(String columnS) {
        return new FunctionQueryColumn(SEC_TO_TIME, columnS);
    }

    /**
     * 将以秒为单位的时间 s 转换为时分秒的格式。
     */
    public static QueryColumn secToTime(QueryColumn columnS) {
        return new FunctionQueryColumn(SEC_TO_TIME, columnS);
    }

    /**
     * 将以秒为单位的时间 s 转换为时分秒的格式。
     */
    public static <T> QueryColumn secToTime(LambdaGetter<T> columnS) {
        return new FunctionQueryColumn(SEC_TO_TIME, LambdaUtil.getQueryColumn(columnS));
    }

    /**
     * 计算日期 d 到 0000 年 1 月 1 日的天数。
     */
    public static QueryColumn toDays(String columnD) {
        return new FunctionQueryColumn(TO_DAYS, columnD);
    }

    /**
     * 计算日期 d 到 0000 年 1 月 1 日的天数。
     */
    public static QueryColumn toDays(QueryColumn columnD) {
        return new FunctionQueryColumn(TO_DAYS, columnD);
    }

    /**
     * 计算日期 d 到 0000 年 1 月 1 日的天数。
     */
    public static <T> QueryColumn toDays(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(TO_DAYS, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 计算从 0000 年 1 月 1 日开始 n 天后的日期。
     */
    public static QueryColumn fromDays(String columnD) {
        return new FunctionQueryColumn(FROM_DAYS, columnD);
    }

    /**
     * 计算从 0000 年 1 月 1 日开始 n 天后的日期。
     */
    public static QueryColumn fromDays(QueryColumn columnD) {
        return new FunctionQueryColumn(FROM_DAYS, columnD);
    }

    /**
     * 计算从 0000 年 1 月 1 日开始 n 天后的日期。
     */
    public static <T> QueryColumn fromDays(LambdaGetter<T> columnD) {
        return new FunctionQueryColumn(FROM_DAYS, LambdaUtil.getQueryColumn(columnD));
    }

    /**
     * 计算日期 d1 到 d2 之间相隔的天数。
     */
    public static QueryColumn dateDiff(String columnD1, String columnD2) {
        return new FunctionQueryColumn(DATEDIFF, columnD1, columnD2);
    }

    /**
     * 计算日期 d1 到 d2 之间相隔的天数。
     */
    public static QueryColumn dateDiff(QueryColumn columnD1, QueryColumn columnD2) {
        return new FunctionQueryColumn(DATEDIFF, columnD1, columnD2);
    }

    /**
     * 计算日期 d1 到 d2 之间相隔的天数。
     */
    public static <D1, D2> QueryColumn dateDiff(LambdaGetter<D1> columnD1, LambdaGetter<D2> columnD2) {
        return new FunctionQueryColumn(DATEDIFF, LambdaUtil.getQueryColumn(columnD1), LambdaUtil.getQueryColumn(columnD2));
    }

    /**
     * 计算开始日期 d 加上 n 天的日期。
     */
    public static QueryColumn addDate(String columnD, String columnN) {
        return new FunctionQueryColumn(ADDDATE, columnD, columnN);
    }

    /**
     * 计算开始日期 d 加上 n 天的日期。
     */
    public static QueryColumn addDate(QueryColumn columnD, QueryColumn columnN) {
        return new FunctionQueryColumn(ADDDATE, columnD, columnN);
    }

    /**
     * 计算开始日期 d 加上 n 天的日期。
     */
    public static <D, N> QueryColumn addDate(LambdaGetter<D> columnD, LambdaGetter<N> columnN) {
        return new FunctionQueryColumn(ADDDATE, LambdaUtil.getQueryColumn(columnD), LambdaUtil.getQueryColumn(columnN));
    }

    /**
     * 计算起始日期 d 减去 n 天的日期。
     */
    public static QueryColumn subDate(String columnD, String columnN) {
        return new FunctionQueryColumn(SUBDATE, columnD, columnN);
    }

    /**
     * 计算起始日期 d 减去 n 天的日期。
     */
    public static QueryColumn subDate(QueryColumn columnD, QueryColumn columnN) {
        return new FunctionQueryColumn(SUBDATE, columnD, columnN);
    }

    /**
     * 计算起始日期 d 减去 n 天的日期。
     */
    public static <D, N> QueryColumn subDate(LambdaGetter<D> columnD, LambdaGetter<N> columnN) {
        return new FunctionQueryColumn(SUBDATE, LambdaUtil.getQueryColumn(columnD), LambdaUtil.getQueryColumn(columnN));
    }

    /**
     * 计算起始时间 t 加上 n 秒的时间。
     */
    public static QueryColumn addTime(String columnT, String columnN) {
        return new FunctionQueryColumn(ADDTIME, columnT, columnN);
    }

    /**
     * 计算起始时间 t 加上 n 秒的时间。
     */
    public static QueryColumn addTime(QueryColumn columnT, QueryColumn columnN) {
        return new FunctionQueryColumn(ADDTIME, columnT, columnN);
    }

    /**
     * 计算起始时间 t 加上 n 秒的时间。
     */
    public static <T, N> QueryColumn addTime(LambdaGetter<T> columnT, LambdaGetter<N> columnN) {
        return new FunctionQueryColumn(ADDTIME, LambdaUtil.getQueryColumn(columnT), LambdaUtil.getQueryColumn(columnN));
    }

    /**
     * 计算起始时间 t 减去 n 秒的时间。
     */
    public static QueryColumn subTime(String columnT, String columnN) {
        return new FunctionQueryColumn(SUBTIME, columnT, columnN);
    }

    /**
     * 计算起始时间 t 减去 n 秒的时间。
     */
    public static QueryColumn subTime(QueryColumn columnT, QueryColumn columnN) {
        return new FunctionQueryColumn(SUBTIME, columnT, columnN);
    }

    /**
     * 计算起始时间 t 减去 n 秒的时间。
     */
    public static <T, N> QueryColumn subTime(LambdaGetter<T> columnT, LambdaGetter<N> columnN) {
        return new FunctionQueryColumn(SUBTIME, LambdaUtil.getQueryColumn(columnT), LambdaUtil.getQueryColumn(columnN));
    }

    /**
     * 按照表达式 f 的要求显示日期 d。
     */
    public static QueryColumn dateFormat(String columnD, String format) {
        return new FunctionQueryColumn(DATE_FORMAT, string(columnD), string(format));
    }

    /**
     * 按照表达式 f 的要求显示日期 d。
     */
    public static QueryColumn dateFormat(QueryColumn columnD, String format) {
        return new FunctionQueryColumn(DATE_FORMAT, columnD, string(format));
    }

    /**
     * 按照表达式 f 的要求显示日期 d。
     */
    public static <D, F> QueryColumn dateFormat(LambdaGetter<D> columnD, String format) {
        return new FunctionQueryColumn(DATE_FORMAT, LambdaUtil.getQueryColumn(columnD), string(format));
    }

    /**
     * 按照表达式 f 的要求显示时间 t。
     */
    public static QueryColumn timeFormat(String columnT, String columnF) {
        return new FunctionQueryColumn(TIME_FORMAT, columnT, columnF);
    }

    /**
     * 按照表达式 f 的要求显示时间 t。
     */
    public static QueryColumn timeFormat(QueryColumn columnT, QueryColumn columnF) {
        return new FunctionQueryColumn(TIME_FORMAT, columnT, columnF);
    }

    /**
     * 按照表达式 f 的要求显示时间 t。
     */
    public static <T, F> QueryColumn timeFormat(LambdaGetter<T> columnT, LambdaGetter<F> columnF) {
        return new FunctionQueryColumn(TIME_FORMAT, LambdaUtil.getQueryColumn(columnT), LambdaUtil.getQueryColumn(columnF));
    }

    /**
     * 根据字符串 s 获取 type 类型数据的显示格式。
     */
    public static QueryColumn getFormat(String columnType, String columnS) {
        return new FunctionQueryColumn(GET_FORMAT, columnType, columnS);
    }

    /**
     * 根据字符串 s 获取 type 类型数据的显示格式。
     */
    public static QueryColumn getFormat(QueryColumn columnType, QueryColumn columnS) {
        return new FunctionQueryColumn(GET_FORMAT, columnType, columnS);
    }

    /**
     * 根据字符串 s 获取 type 类型数据的显示格式。
     */
    public static <T, S> QueryColumn getFormat(LambdaGetter<T> columnType, LambdaGetter<S> columnS) {
        return new FunctionQueryColumn(GET_FORMAT, LambdaUtil.getQueryColumn(columnType), LambdaUtil.getQueryColumn(columnS));
    }

    // === 系统信息函数 ===

    /**
     * 返回数据库的版本号。
     */
    public static QueryColumn version() {
        return new FunctionQueryColumn(VERSION);
    }

    /**
     * 返回服务器的连接数。
     */
    public static QueryColumn connectionId() {
        return new FunctionQueryColumn(CONNECTION_ID);
    }

    /**
     * 返回当前数据库名。
     */
    public static QueryColumn database() {
        return new FunctionQueryColumn(DATABASE);
    }

    /**
     * 返回当前数据库 schema。
     */
    public static QueryColumn schema() {
        return new FunctionQueryColumn(SCHEMA);
    }

    /**
     * 返回当前用户的名称。
     */
    public static QueryColumn user() {
        return new FunctionQueryColumn(USER);
    }

    /**
     * 返回字符串 str 的字符集。
     */
    public static QueryColumn charset(String columnStr) {
        return new FunctionQueryColumn(CHARSET, columnStr);
    }

    /**
     * 返回字符串 str 的字符集。
     */
    public static QueryColumn charset(QueryColumn columnStr) {
        return new FunctionQueryColumn(CHARSET, columnStr);
    }

    /**
     * 返回字符串 str 的字符集。
     */
    public static <T> QueryColumn charset(LambdaGetter<T> columnStr) {
        return new FunctionQueryColumn(CHARSET, LambdaUtil.getQueryColumn(columnStr));
    }

    /**
     * 返回字符串 str 的字符排列方式。
     */
    public static QueryColumn collation(String columnStr) {
        return new FunctionQueryColumn(COLLATION, columnStr);
    }

    /**
     * 返回字符串 str 的字符排列方式。
     */
    public static QueryColumn collation(QueryColumn columnStr) {
        return new FunctionQueryColumn(COLLATION, columnStr);
    }

    /**
     * 返回字符串 str 的字符排列方式。
     */
    public static <T> QueryColumn collation(LambdaGetter<T> columnStr) {
        return new FunctionQueryColumn(COLLATION, LambdaUtil.getQueryColumn(columnStr));
    }

    /**
     * 返回最后生成的 auto_increment 值。
     */
    public static QueryColumn lastInsertId() {
        return new FunctionQueryColumn(LAST_INSERT_ID);
    }

    // === 加密函数 ===

    /**
     * 对字符串 str 进行加密。
     */
    public static QueryColumn password(String columnStr) {
        return new FunctionQueryColumn(PASSWORD, columnStr);
    }

    /**
     * 对字符串 str 进行加密。
     */
    public static QueryColumn password(QueryColumn columnStr) {
        return new FunctionQueryColumn(PASSWORD, columnStr);
    }

    /**
     * 对字符串 str 进行加密。
     */
    public static <T> QueryColumn password(LambdaGetter<T> columnStr) {
        return new FunctionQueryColumn(PASSWORD, LambdaUtil.getQueryColumn(columnStr));
    }

    /**
     * 对字符串 str 进行加密。
     */
    public static QueryColumn md5(String columnStr) {
        return new FunctionQueryColumn(MD5, columnStr);
    }

    /**
     * 对字符串 str 进行加密。
     */
    public static QueryColumn md5(QueryColumn columnStr) {
        return new FunctionQueryColumn(MD5, columnStr);
    }

    /**
     * 对字符串 str 进行加密。
     */
    public static <T> QueryColumn md5(LambdaGetter<T> columnStr) {
        return new FunctionQueryColumn(MD5, LambdaUtil.getQueryColumn(columnStr));
    }

    /**
     * 使用字符串 pswd_str 来加密字符串 str，加密结果是一个二进制数，必须使用 BLOB 类型来保持它。
     */
    public static QueryColumn encode(String columnStr, String columnPswdStr) {
        return new FunctionQueryColumn(ENCODE, columnStr, columnPswdStr);
    }

    /**
     * 使用字符串 pswd_str 来加密字符串 str，加密结果是一个二进制数，必须使用 BLOB 类型来保持它。
     */
    public static QueryColumn encode(QueryColumn columnStr, QueryColumn columnPswdStr) {
        return new FunctionQueryColumn(ENCODE, columnStr, columnPswdStr);
    }

    /**
     * 使用字符串 pswd_str 来加密字符串 str，加密结果是一个二进制数，必须使用 BLOB 类型来保持它。
     */
    public static <S, P> QueryColumn encode(LambdaGetter<S> columnStr, LambdaGetter<P> columnPswdStr) {
        return new FunctionQueryColumn(ENCODE, LambdaUtil.getQueryColumn(columnStr), LambdaUtil.getQueryColumn(columnPswdStr));
    }

    /**
     * 解密函数，使用字符串 pswd_str 来为 crypt_str 解密。
     */
    public static QueryColumn decode(String columnCryptStr, String columnPswdStr) {
        return new FunctionQueryColumn(DECODE, columnCryptStr, columnPswdStr);
    }

    /**
     * 解密函数，使用字符串 pswd_str 来为 crypt_str 解密。
     */
    public static QueryColumn decode(QueryColumn columnCryptStr, QueryColumn columnPswdStr) {
        return new FunctionQueryColumn(DECODE, columnCryptStr, columnPswdStr);
    }

    /**
     * 解密函数，使用字符串 pswd_str 来为 crypt_str 解密。
     */
    public static <C, P> QueryColumn decode(LambdaGetter<C> columnCryptStr, LambdaGetter<P> columnPswdStr) {
        return new FunctionQueryColumn(DECODE, LambdaUtil.getQueryColumn(columnCryptStr), LambdaUtil.getQueryColumn(columnPswdStr));
    }

    // === 其他函数 ===

    /**
     * 格式化函数，可以将数字 x 进行格式化，将 x 保留到小数点后 n 位，这个过程需要进行四舍五入。
     */
    public static QueryColumn format(String columnX, String columnN) {
        return new FunctionQueryColumn(FORMAT, columnX, columnN);
    }

    /**
     * 格式化函数，可以将数字 x 进行格式化，将 x 保留到小数点后 n 位，这个过程需要进行四舍五入。
     */
    public static QueryColumn format(QueryColumn columnX, QueryColumn columnN) {
        return new FunctionQueryColumn(FORMAT, columnX, columnN);
    }

    /**
     * 格式化函数，可以将数字 x 进行格式化，将 x 保留到小数点后 n 位，这个过程需要进行四舍五入。
     */
    public static <X, N> QueryColumn format(LambdaGetter<X> columnX, LambdaGetter<N> columnN) {
        return new FunctionQueryColumn(FORMAT, LambdaUtil.getQueryColumn(columnX), LambdaUtil.getQueryColumn(columnN));
    }

    /**
     * 返回字符串 s 的第一个字符的 ASSCII 码。
     */
    public static QueryColumn ascii(String columnS) {
        return new FunctionQueryColumn(ASCII, columnS);
    }

    /**
     * 返回字符串 s 的第一个字符的 ASSCII 码。
     */
    public static QueryColumn ascii(QueryColumn columnS) {
        return new FunctionQueryColumn(ASCII, columnS);
    }

    /**
     * 返回字符串 s 的第一个字符的 ASSCII 码。
     */
    public static <T> QueryColumn ascii(LambdaGetter<T> columnS) {
        return new FunctionQueryColumn(ASCII, LambdaUtil.getQueryColumn(columnS));
    }

    /**
     * 返回 x 的二进制编码。
     */
    public static QueryColumn bin(String columnX) {
        return new FunctionQueryColumn(BIN, columnX);
    }

    /**
     * 返回 x 的二进制编码。
     */
    public static QueryColumn bin(QueryColumn columnX) {
        return new FunctionQueryColumn(BIN, columnX);
    }

    /**
     * 返回 x 的二进制编码。
     */
    public static <T> QueryColumn bin(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(BIN, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回 x 的十六进制编码。
     */
    public static QueryColumn hex(String columnX) {
        return new FunctionQueryColumn(HEX, columnX);
    }

    /**
     * 返回 x 的十六进制编码。
     */
    public static QueryColumn hex(QueryColumn columnX) {
        return new FunctionQueryColumn(HEX, columnX);
    }

    /**
     * 返回 x 的十六进制编码。
     */
    public static <T> QueryColumn hex(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(HEX, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 返回 x 的八进制编码。
     */
    public static QueryColumn oct(String columnX) {
        return new FunctionQueryColumn(OCT, columnX);
    }

    /**
     * 返回 x 的八进制编码。
     */
    public static QueryColumn oct(QueryColumn columnX) {
        return new FunctionQueryColumn(OCT, columnX);
    }

    /**
     * 返回 x 的八进制编码。
     */
    public static <T> QueryColumn oct(LambdaGetter<T> columnX) {
        return new FunctionQueryColumn(OCT, LambdaUtil.getQueryColumn(columnX));
    }

    /**
     * 将 x 从 f1 进制数变成 f2 进制数。
     */
    public static QueryColumn conv(String columnX, String columnF1, String columnF2) {
        return new FunctionQueryColumn(CONV, columnX, columnF1, columnF2);
    }

    /**
     * 将 x 从 f1 进制数变成 f2 进制数。
     */
    public static QueryColumn conv(QueryColumn columnX, QueryColumn columnF1, QueryColumn columnF2) {
        return new FunctionQueryColumn(CONV, columnX, columnF1, columnF2);
    }

    /**
     * 将 x 从 f1 进制数变成 f2 进制数。
     */
    public static <X, F1, F2> QueryColumn conv(LambdaGetter<X> columnX, LambdaGetter<F1> columnF1, LambdaGetter<F2> columnF2) {
        return new FunctionQueryColumn(CONV, LambdaUtil.getQueryColumn(columnX), LambdaUtil.getQueryColumn(columnF1), LambdaUtil.getQueryColumn(columnF2));
    }

    /**
     * 将 IP 地址转换为数字表示，IP 值需要加上引号。
     */
    public static QueryColumn inetAton(String columnIP) {
        return new FunctionQueryColumn(INET_ATON, columnIP);
    }

    /**
     * 将 IP 地址转换为数字表示，IP 值需要加上引号。
     */
    public static QueryColumn inetAton(QueryColumn columnIP) {
        return new FunctionQueryColumn(INET_ATON, columnIP);
    }

    /**
     * 将 IP 地址转换为数字表示，IP 值需要加上引号。
     */
    public static <T> QueryColumn inetAton(LambdaGetter<T> columnIP) {
        return new FunctionQueryColumn(INET_ATON, LambdaUtil.getQueryColumn(columnIP));
    }

    /**
     * 可以将数字 n 转换成 IP 的形式。
     */
    public static QueryColumn inetNtoa(String columnN) {
        return new FunctionQueryColumn(INET_NTOA, columnN);
    }

    /**
     * 可以将数字 n 转换成 IP 的形式。
     */
    public static QueryColumn inetNtoa(QueryColumn columnN) {
        return new FunctionQueryColumn(INET_NTOA, columnN);
    }

    /**
     * 可以将数字 n 转换成 IP 的形式。
     */
    public static <T> QueryColumn inetNtoa(LambdaGetter<T> columnN) {
        return new FunctionQueryColumn(INET_NTOA, LambdaUtil.getQueryColumn(columnN));
    }

    // === 聚合函数 ===

    /**
     * 返回指定列的最大值。
     */
    public static FunctionQueryColumn max(String column) {
        return new FunctionQueryColumn(MAX, column);
    }

    /**
     * 返回指定列的最大值。
     */
    public static FunctionQueryColumn max(QueryColumn column) {
        return new FunctionQueryColumn(MAX, column);
    }

    /**
     * 返回指定列的最大值。
     */
    public static <T> FunctionQueryColumn max(LambdaGetter<T> column) {
        return new FunctionQueryColumn(MAX, LambdaUtil.getQueryColumn(column));
    }

    /**
     * 返回指定列的最小值。
     */
    public static FunctionQueryColumn min(String column) {
        return new FunctionQueryColumn(MIN, column);
    }

    /**
     * 返回指定列的最小值。
     */
    public static FunctionQueryColumn min(QueryColumn column) {
        return new FunctionQueryColumn(MIN, column);
    }

    /**
     * 返回指定列的最小值。
     */
    public static <T> FunctionQueryColumn min(LambdaGetter<T> column) {
        return new FunctionQueryColumn(MIN, LambdaUtil.getQueryColumn(column));
    }

    /**
     * 返回指定列的平均值。
     */
    public static FunctionQueryColumn avg(String column) {
        return new FunctionQueryColumn(AVG, column);
    }

    /**
     * 返回指定列的平均值。
     */
    public static FunctionQueryColumn avg(QueryColumn column) {
        return new FunctionQueryColumn(AVG, column);
    }

    /**
     * 返回指定列的平均值。
     */
    public static <T> FunctionQueryColumn avg(LambdaGetter<T> column) {
        return new FunctionQueryColumn(AVG, LambdaUtil.getQueryColumn(column));
    }

    /**
     * 返回指定字段值的和。
     */
    public static FunctionQueryColumn sum(String column) {
        return new FunctionQueryColumn(SUM, column);
    }

    /**
     * 返回指定字段值的和。
     */
    public static FunctionQueryColumn sum(QueryColumn column) {
        return new FunctionQueryColumn(SUM, column);
    }

    /**
     * 返回指定字段值的和。
     */
    public static <T> FunctionQueryColumn sum(LambdaGetter<T> column) {
        return new FunctionQueryColumn(SUM, LambdaUtil.getQueryColumn(column));
    }

    // === COUNT ===

    /**
     * 返回指定列的总行数。
     */
    public static FunctionQueryColumn count() {
        return new FunctionQueryColumn(COUNT, new RawQueryColumn("*"));
    }

    /**
     * 返回指定列的总行数。
     */
    public static FunctionQueryColumn count(String column) {
        return new FunctionQueryColumn(COUNT, column);
    }

    /**
     * 返回指定列的总行数。
     */
    public static FunctionQueryColumn count(QueryColumn column) {
        return new FunctionQueryColumn(COUNT, column);
    }

    /**
     * 返回指定列的总行数。
     */
    public static <T> FunctionQueryColumn count(LambdaGetter<T> column) {
        return new FunctionQueryColumn(COUNT, LambdaUtil.getQueryColumn(column));
    }


    // === DISTINCT ===

    /**
     * 对指定列进行去重。
     */
    public static DistinctQueryColumn distinct(QueryColumn... columns) {
        return new DistinctQueryColumn(columns);
    }

    @SafeVarargs
    public static <T> DistinctQueryColumn distinct(LambdaGetter<T>... columns) {
        return new DistinctQueryColumn(Arrays.stream(columns)
            .map(LambdaUtil::getQueryColumn).toArray(QueryColumn[]::new));
    }

    // === CASE THEN ELSE ===

    /**
     * 构建 case then when 语句。
     */
    public static CaseQueryColumn.Builder case_() {
        return new CaseQueryColumn.Builder();
    }

    /**
     * 构建 case then when 语句。
     */
    public static CaseSearchQueryColumn.Builder case_(QueryColumn column) {
        return new CaseSearchQueryColumn.Builder(column);
    }

    // === CONVERT ===

    /**
     * 将所给类型类型转换为另一种类型。
     */
    public static StringFunctionQueryColumn convert(String... params) {
        return new StringFunctionQueryColumn(CONVERT, params);
    }

    // === 构建 column 列 ===

    /**
     * 构建 TRUE 常量。
     */
    public static QueryColumn true_() {
        return new RawQueryColumn("TRUE");
    }

    /**
     * 构建 FALSE 常量。
     */
    public static QueryColumn false_() {
        return new RawQueryColumn("FALSE");
    }

    /**
     * 构建 NULL 常量。
     */
    public static QueryColumn null_() {
        return new RawQueryColumn("NULL");
    }

    /**
     * 构建数字常量。
     */
    public static QueryColumn number(Number n) {
        return new RawQueryColumn(n);
    }

    /**
     * 构建数字常量。
     */
    public static QueryColumn string(String s) {
        return new RawQueryColumn("'" + s + "'");
    }

    /**
     * 构建相反数。
     */
    public static QueryColumn negative(QueryColumn queryColumn) {
        return new NegativeQueryColumn(queryColumn);
    }

    /**
     * 构建自定义列。
     */
    public static QueryColumn column(String column, Object... params) {
        return new RawQueryColumn(column, params);
    }

    /**
     * 构建自定义列。
     */
    public static QueryColumn column(String table, String column) {
        return new QueryColumn(table, column);
    }

    /**
     * 构建自定义列。
     */
    public static QueryColumn column(String schema, String table, String column) {
        return new QueryColumn(schema, table, column);
    }

    /**
     * 构建自定义列。
     */
    public static <T> QueryColumn column(LambdaGetter<T> column) {
        return LambdaUtil.getQueryColumn(column);
    }

    /**
     * 构建自定义列。
     */
    public static QueryColumn column(QueryWrapper queryWrapper) {
        return new SelectQueryColumn(queryWrapper);
    }

    /**
     * 构建所有列。
     */
    public static QueryColumn allColumns() {
        return column("*");
    }

    /**
     * 构建所有列。
     */
    public static Iterable<QueryColumn> allColumns(Class<?>... classes) {
        List<QueryColumn> queryColumns = new ArrayList<>(classes.length);
        for (Class<?> aClass : classes) {
            TableInfo tableInfo = TableInfoFactory.ofEntityClass(aClass);
            QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName());
            queryColumns.add(new QueryColumn(queryTable, "*"));
        }
        return queryColumns;
    }

    /**
     * 构建默认列。
     */
    public static Iterable<QueryColumn> defaultColumns(Class<?>... classes) {
        List<QueryColumn> queryColumns = new ArrayList<>();
        for (Class<?> aClass : classes) {
            TableInfo tableInfo = TableInfoFactory.ofEntityClass(aClass);
            queryColumns.addAll(tableInfo.getDefaultQueryColumn());
        }
        return queryColumns;
    }

    // === IF 函数 ===

    /**
     * IF 函数。
     */
    public static QueryColumn if_(QueryCondition condition, String trueColumn, String falseColumn) {
        return new IfFunctionQueryColumn(condition, new QueryColumn(trueColumn), new QueryColumn(falseColumn));
    }

    /**
     * IF 函数。
     */
    public static QueryColumn if_(QueryCondition condition, QueryColumn trueColumn, QueryColumn falseColumn) {
        return new IfFunctionQueryColumn(condition, trueColumn, falseColumn);
    }

    /**
     * IF 函数。
     */
    public static <T, F> QueryColumn if_(QueryCondition condition, LambdaGetter<T> trueColumn, LambdaGetter<F> falseColumn) {
        return new IfFunctionQueryColumn(condition, LambdaUtil.getQueryColumn(trueColumn), LambdaUtil.getQueryColumn(falseColumn));
    }

    /**
     * IFNULL 函数。
     */
    public static QueryColumn ifNull(String nullColumn, String elseColumn) {
        return new FunctionQueryColumn("IFNULL", new QueryColumn(nullColumn), new QueryColumn(elseColumn));
    }

    /**
     * IFNULL 函数。
     */
    public static QueryColumn ifNull(QueryColumn nullColumn, QueryColumn elseColumn) {
        return new FunctionQueryColumn("IFNULL", nullColumn, elseColumn);
    }

    /**
     * IFNULL 函数。
     */
    public static <N, E> QueryColumn ifNull(LambdaGetter<N> nullColumn, LambdaGetter<E> elseColumn) {
        return new FunctionQueryColumn("IFNULL", LambdaUtil.getQueryColumn(nullColumn), LambdaUtil.getQueryColumn(elseColumn));
    }

    /**
     * IFNULL 函数。
     */
    public static <N> QueryColumn ifNull(LambdaGetter<N> nullColumn, QueryColumn elseColumn) {
        return ifNull(LambdaUtil.getQueryColumn(nullColumn), elseColumn);
    }

    /**
     * IFNULL 函数。
     */
    public static <N> QueryColumn ifNull(LambdaGetter<N> nullColumn, String elseColumn) {
        return ifNull(nullColumn, new QueryColumn(elseColumn));
    }


    // === 构建 QueryCondition 查询条件 ===

    /**
     * EXIST (SELECT ...)
     */
    public static QueryCondition exists(QueryWrapper queryWrapper) {
        return new OperatorSelectCondition("EXISTS ", queryWrapper);
    }

    /**
     * NOT EXIST (SELECT ...)
     */
    public static QueryCondition notExists(QueryWrapper queryWrapper) {
        return new OperatorSelectCondition("NOT EXISTS ", queryWrapper);
    }

    /**
     * NOT (id = 1)
     */
    public static QueryCondition not(QueryCondition childCondition) {
        return new OperatorQueryCondition("NOT ", childCondition);
    }

    /**
     * {@code NOT (column)} 或 {@code NOT column}
     */
    public static <N> QueryColumn not(LambdaGetter<N> column) {
        return new FunctionQueryColumn("NOT", LambdaUtil.getQueryColumn(column));
    }

    /**
     * 空条件。
     */
    public static QueryCondition noCondition() {
        return QueryCondition.createEmpty();
    }

    /**
     * 括号条件。
     */
    public static QueryCondition bracket(QueryCondition condition) {
        return new Brackets(condition);
    }

    // === 构建 QueryWrapper 查询 ===

    /**
     * SELECT queryColumns FROM table
     */
    public static QueryWrapper select(QueryColumn... queryColumns) {
        return newWrapper().select(queryColumns);
    }

    /**
     * SELECT * FROM table UNION SELECT ...
     */
    public static QueryWrapper union(QueryWrapper queryWrapper) {
        return newWrapper().union(queryWrapper);
    }

    /**
     * SELECT 1 as temp_one FROM table
     */
    public static QueryWrapper selectOne() {
        return select(column("1").as("temp_one"));
    }

    /**
     * SELECT COUNT(*) as temp_count FROM table
     */
    public static QueryWrapper selectCount() {
        return select(count().as("temp_count"));
    }

    /**
     * SELECT COUNT(1) as temp_count_one FROM table
     */
    public static QueryWrapper selectCountOne() {
        return select(count(new RawQueryColumn("1")).as("temp_count_one"));
    }

    /**
     * SELECT * FROM table
     */
    private static QueryWrapper newWrapper() {
        return new QueryWrapper();
    }

    // 构建原生 SQL 条件

    /**
     * 构建原生查询条件。
     */
    public static QueryCondition raw(String raw) {
        return new RawQueryCondition(raw);
    }

    /**
     * 构建原生查询条件，并附带参数。
     */
    public static QueryCondition raw(String raw, Object... params) {
        return new RawQueryCondition(raw, params);
    }


    /**
     * 分组值拼接
     */
    public static QueryColumn groupConcat(QueryColumn columnX) {
        return new FunctionQueryColumn(GROUP_CONCAT, columnX);
    }

}
