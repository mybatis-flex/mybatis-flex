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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.Date;

public class ConvertUtil {

    private ConvertUtil() {
    }

    @SuppressWarnings("rawtypes")
    public static Object convert(Object value, Class targetClass) {
        return convert(value, targetClass, false);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object convert(Object value, Class targetClass, boolean ignoreConvertError) {
        if (value == null && targetClass.isPrimitive()) {
            return getPrimitiveDefaultValue(targetClass);
        }
        if (value == null || (targetClass != String.class && value.getClass() == String.class
            && StringUtil.isBlank((String) value))) {
            return null;
        }
        if (value.getClass().isAssignableFrom(targetClass)) {
            return value;
        }
        if (targetClass == Serializable.class && value instanceof Serializable) {
            return value;
        }
        if (targetClass == String.class) {
            return value.toString();
        } else if (targetClass == Integer.class || targetClass == int.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        } else if (targetClass == Long.class || targetClass == long.class) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.parseLong(value.toString());
        } else if (targetClass == Double.class || targetClass == double.class) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.parseDouble(value.toString());
        } else if (targetClass == Float.class || targetClass == float.class) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            return Float.parseFloat(value.toString());
        } else if (targetClass == Boolean.class || targetClass == boolean.class) {
            String v = value.toString().toLowerCase();
            if ("1".equals(v) || "true".equalsIgnoreCase(v)) {
                return Boolean.TRUE;
            } else if ("0".equals(v) || "false".equalsIgnoreCase(v)) {
                return Boolean.FALSE;
            } else {
                throw new RuntimeException("Can not parse to boolean type of value: \"" + value + "\"");
            }
        } else if (targetClass == java.math.BigDecimal.class) {
            return new java.math.BigDecimal(value.toString());
        } else if (targetClass == java.math.BigInteger.class) {
            return new java.math.BigInteger(value.toString());
        } else if (targetClass == byte[].class) {
            return value.toString().getBytes();
        } else if (targetClass == Date.class) {
            return DateUtil.parseDate(value);
        } else if (targetClass == LocalDateTime.class) {
            return toLocalDateTime(value);
        } else if (targetClass == LocalDate.class) {
            return DateUtil.toLocalDate(DateUtil.parseDate(value));
        } else if (targetClass == LocalTime.class) {
            return DateUtil.toLocalTime(DateUtil.parseDate(value));
        } else if (targetClass == Short.class || targetClass == short.class) {
            if (value instanceof Number) {
                return ((Number) value).shortValue();
            }
            return Short.parseShort(value.toString());
        } else if (targetClass.isEnum()) {
            EnumWrapper<?> enumWrapper = EnumWrapper.of(targetClass);
            if (enumWrapper.hasEnumValueAnnotation()) {
                return enumWrapper.getEnum(value);
            } else if (value instanceof String) {
                return Enum.valueOf(targetClass, value.toString());
            }
        }
        if (ignoreConvertError) {
            return null;
        } else {
            throw new IllegalArgumentException("Can not convert \"" + value + "\" to type\"" + targetClass.getName() + "\".");
        }
    }


    //Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE
    public static Object getPrimitiveDefaultValue(Class<?> paraClass) {
        if (paraClass == int.class || paraClass == long.class || paraClass == float.class || paraClass == double.class) {
            return 0;
        } else if (paraClass == boolean.class) {
            return Boolean.FALSE;
        } else if (paraClass == short.class) {
            return (short) 0;
        } else if (paraClass == byte.class) {
            return (byte) 0;
        } else if (paraClass == char.class) {
            return '\u0000';
        } else {
            throw new IllegalArgumentException("Can not get primitive default value for type: " + paraClass);
        }
    }

    public static Class<?> primitiveToBoxed(Class<?> paraClass) {
        if (paraClass == Integer.TYPE) {
            return Integer.class;
        } else if (paraClass == Long.TYPE) {
            return Long.class;
        } else if (paraClass == Double.TYPE) {
            return Double.class;
        } else if (paraClass == Float.TYPE) {
            return Float.class;
        } else if (paraClass == Boolean.TYPE) {
            return Boolean.class;
        } else if (paraClass == Short.TYPE) {
            return Short.class;
        } else if (paraClass == Byte.TYPE) {
            return Byte.class;
        } else if (paraClass == Character.TYPE) {
            return Character.class;
        } else {
            throw new IllegalArgumentException("Can not convert primitive class for type: " + paraClass);
        }
    }


    public static Integer toInt(Object i) {
        if (i instanceof Integer) {
            return (Integer) i;
        } else if (i instanceof Number) {
            return ((Number) i).intValue();
        }
        return i != null ? Integer.parseInt(i.toString()) : null;
    }

    public static Long toLong(Object l) {
        if (l instanceof Long) {
            return (Long) l;
        } else if (l instanceof Number) {
            return ((Number) l).longValue();
        }
        return l != null ? Long.parseLong(l.toString()) : null;
    }

    public static Double toDouble(Object d) {
        if (d instanceof Double) {
            return (Double) d;
        } else if (d instanceof Number) {
            return ((Number) d).doubleValue();
        }

        return d != null ? Double.parseDouble(d.toString()) : null;
    }

    public static BigDecimal toBigDecimal(Object b) {
        if (b instanceof BigDecimal) {
            return (BigDecimal) b;
        } else if (b != null) {
            return new BigDecimal(b.toString());
        } else {
            return null;
        }
    }

    public static BigInteger toBigInteger(Object b) {
        if (b instanceof BigInteger) {
            return (BigInteger) b;
        }
        // 数据类型 id(19 number)在 Oracle Jdbc 下对应的是 BigDecimal,
        // 但是在 MySql 下对应的是 BigInteger，这会导致在 MySql 下生成的代码无法在 Oracle 数据库中使用
        if (b instanceof BigDecimal) {
            return ((BigDecimal) b).toBigInteger();
        } else if (b instanceof Number) {
            return BigInteger.valueOf(((Number) b).longValue());
        } else if (b instanceof String) {
            return new BigInteger((String) b);
        }

        return (BigInteger) b;
    }

    public static Float toFloat(Object f) {
        if (f instanceof Float) {
            return (Float) f;
        } else if (f instanceof Number) {
            return ((Number) f).floatValue();
        }
        return f != null ? Float.parseFloat(f.toString()) : null;
    }


    public static Short toShort(Object s) {
        if (s instanceof Short) {
            return (Short) s;
        } else if (s instanceof Number) {
            return ((Number) s).shortValue();
        }
        return s != null ? Short.parseShort(s.toString()) : null;
    }


    public static Byte toByte(Object b) {
        if (b instanceof Byte) {
            return (Byte) b;
        } else if (b instanceof Number) {
            return ((Number) b).byteValue();
        }
        return b != null ? Byte.parseByte(b.toString()) : null;
    }

    public static Boolean toBoolean(Object b) {
        if (b instanceof Boolean) {
            return (Boolean) b;
        } else if (b == null) {
            return null;
        }

        // 支持 Number 之下的整数类型
        if (b instanceof Number) {
            int n = ((Number) b).intValue();
            if (n == 1) {
                return Boolean.TRUE;
            } else if (n == 0) {
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("Can not support convert: \"" + b + "\" to boolean.");
        }

        // 支持 String
        if (b instanceof String) {
            String s = b.toString();
            if ("true".equalsIgnoreCase(s) || "1".equals(s)) {
                return Boolean.TRUE;
            } else if ("false".equalsIgnoreCase(s) || "0".equals(s)) {
                return Boolean.FALSE;
            }
        }

        return (Boolean) b;
    }


    public static Date toDate(Object o) {
        if (o instanceof Date) {
            return (Date) o;
        }

        if (o instanceof Temporal) {
            if (o instanceof LocalDateTime) {
                return DateUtil.toDate((LocalDateTime) o);
            }
            if (o instanceof LocalDate) {
                return DateUtil.toDate((LocalDate) o);
            }
            if (o instanceof LocalTime) {
                return DateUtil.toDate((LocalTime) o);
            }
        }

        if (o instanceof String) {
            String s = (String) o;
            return DateUtil.parseDate(s);
        }

        return (java.util.Date) o;
    }


    public static LocalDateTime toLocalDateTime(Object o) {
        if (o instanceof LocalDateTime) {
            return (LocalDateTime) o;
        }
        if (o instanceof java.util.Date) {
            return DateUtil.toLocalDateTime((java.util.Date) o);
        }
        if (o instanceof LocalDate) {
            return ((LocalDate) o).atStartOfDay();
        }
        if (o instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.now(), (LocalTime) o);
        }

        if (o instanceof String) {
            String s = (String) o;
            return DateUtil.parseLocalDateTime(s);
        }

        return (LocalDateTime) o;
    }

}
