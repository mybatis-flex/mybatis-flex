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

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

public class MapUtil {
    private static final boolean IS_JDK8 = (8 == getJvmVersion0());

    private MapUtil() {
    }

    private static int getJvmVersion0() {
        int jvmVersion = -1;
        try {
            String javaSpecVer = StringUtil.tryTrim(System.getProperty("java.specification.version"));
            if (StringUtil.isNotBlank(javaSpecVer)) {
                if (javaSpecVer.startsWith("1.")) {
                    javaSpecVer = javaSpecVer.substring(2);
                }
                if (javaSpecVer.indexOf('.') == -1) {
                    jvmVersion = Integer.parseInt(javaSpecVer);
                }
            }
        } catch (Throwable ignore) {
            // ignore
        }
        // default is jdk8
        if (jvmVersion == -1) {
            jvmVersion = 8;
        }
        return jvmVersion;
    }

    /**
     * A temporary workaround for Java 8 specific performance issue JDK-8161372 .<br>
     * This class should be removed once we drop Java 8 support.
     *
     * @see <a href=
     * "https://bugs.openjdk.java.net/browse/JDK-8161372">https://bugs.openjdk.java.net/browse/JDK-8161372</a>
     */
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<K, V> mappingFunction) {
        if (IS_JDK8) {
            V value = map.get(key);
            if (value != null) {
                return value;
            }
        }
        return map.computeIfAbsent(key, mappingFunction);
    }


    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }


}
