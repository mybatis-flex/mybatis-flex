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

import com.mybatisflex.core.query.CloneSupport;

import java.util.Objects;

public class ObjectUtil {

    private ObjectUtil() {
    }

    public static Object cloneObject(Object value) {
        // ROLE.ROLE_ID.ge(USER.USER_ID)
        if (value instanceof CloneSupport) {
            return ((CloneSupport<?>) value).clone();
        }
        return value;
    }

    public static <T extends CloneSupport<T>> T clone(T value) {
        if (value != null) {
            return value.clone();
        }
        return null;
    }

    public static <T> T requireNonNullElse(T t1, T t2) {
        return t1 == null ? t2 : t1;
    }

    public static boolean areNotNull(Object... objs) {
        for (Object obj : objs) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean areNull(Object... objs) {
        for (Object obj : objs) {
            if (obj != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsAny(Object a, Object... others) {
        if (others == null || others.length == 0) {
            throw new IllegalArgumentException("others must not be null or empty.");
        }
        for (Object other : others) {
            if (Objects.equals(a, other)) {
                return true;
            }
        }
        return false;
    }

}
