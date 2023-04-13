/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.row;

import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.ConvertUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class RowSetter {

    static Map<Class<?>, Map<String, Method>> getterMethods = new ConcurrentHashMap<>();

    static void setObject(Row row, Object toObject) {
        Map<String, Method> setterMethods = getSetterMethods(toObject.getClass());
        row.forEach((column, columnValue) -> {
            Method setter = setterMethods.get(column);
            try {
                if (setter != null) {
                    Object value = ConvertUtil.convert(columnValue, setter.getParameterTypes()[0]);
                    setter.invoke(toObject, value);
                }
            } catch (Exception e) {
                throw new RuntimeException("Can not invoke method: " + setter);
            }
        });
    }


    private static Map<String, Method> getSetterMethods(Class<?> aClass) {
        return MapUtil.computeIfAbsent(getterMethods, aClass, aClass1 -> {
            Map<String, Method> getterMethodsMapping = new HashMap<>();
            List<Method> setters = ClassUtil.getAllMethods(aClass1,
                    method -> method.getName().startsWith("set")
                            && method.getParameterCount() == 1
                            && Modifier.isPublic(method.getModifiers())
            );
            for (Method setter : setters) {
                String column = setter.getName().substring(3);
                getterMethodsMapping.put(column, setter);
                getterMethodsMapping.put(column.toLowerCase(), setter);
                getterMethodsMapping.put(column.toUpperCase(), setter);
                getterMethodsMapping.put(StringUtil.firstCharToLowerCase(column), setter);
                getterMethodsMapping.put(StringUtil.camelToUnderline(column), setter);
                getterMethodsMapping.put(StringUtil.camelToUnderline(column).toUpperCase(), setter);
                getterMethodsMapping.put(StringUtil.underlineToCamel(column), setter);
            }
            return getterMethodsMapping;
        });
    }
}
