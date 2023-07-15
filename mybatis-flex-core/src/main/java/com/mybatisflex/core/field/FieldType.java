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

package com.mybatisflex.core.field;

import com.mybatisflex.core.util.CollectionUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 属性类型。
 *
 * @author 王帅
 * @since 2023-07-15
 */
public enum FieldType {

    /**
     * Map 对象。
     */
    MAP,

    /**
     * 自动推断。
     */
    AUTO,

    /**
     * 数组。
     */
    ARRAY,

    /**
     * 基本数据类型。
     */
    BASIC,

    /**
     * 实体类。
     */
    ENTITY,

    /**
     * 集合。
     */
    COLLECTION;

    private static final Set<Class<?>> BASIC_TYPES = CollectionUtil.newHashSet(
        int.class, Integer.class,
        short.class, Short.class,
        long.class, Long.class,
        float.class, Float.class,
        double.class, Double.class,
        boolean.class, Boolean.class,
        Date.class, java.sql.Date.class, Time.class, Timestamp.class,
        Instant.class, LocalDate.class, LocalDateTime.class, LocalTime.class, OffsetDateTime.class, OffsetTime.class, ZonedDateTime.class,
        Year.class, Month.class, YearMonth.class, JapaneseDate.class,
        byte[].class, Byte[].class, Byte.class,
        BigInteger.class, BigDecimal.class,
        char.class, String.class, Character.class
    );

    /**
     * 自动推断属性类型
     *
     * @param field 属性
     * @return 属性类型
     * @see FieldType#AUTO
     */
    public static FieldType determineFieldType(Field field) {
        Class<?> fieldType = field.getType();
        if (Collection.class.isAssignableFrom(fieldType)) {
            return COLLECTION;
        } else if (Map.class.isAssignableFrom(fieldType)) {
            return MAP;
        } else if (fieldType.isArray()) {
            return ARRAY;
        } else if (BASIC_TYPES.contains(fieldType)
            || fieldType.isEnum()) {
            return BASIC;
        } else {
            return ENTITY;
        }
    }

}
