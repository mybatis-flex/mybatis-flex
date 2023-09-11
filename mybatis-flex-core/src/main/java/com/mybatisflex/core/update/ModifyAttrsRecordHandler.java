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
package com.mybatisflex.core.update;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.core.util.FieldWrapper;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.javassist.util.proxy.MethodHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

class ModifyAttrsRecordHandler implements MethodHandler {

    /**
     * 更新的字段和内容
     */
    private final Map<String, Object> updates = new LinkedHashMap<>();

    public Map<String, Object> getUpdates() {
        return updates;
    }

    @Override
    public Object invoke(Object self, Method originalMethod, Method proxyMethod, Object[] args) throws Throwable {

        String methodName = originalMethod.getName();
        if (methodName.startsWith("set")
            && methodName.length() > 3
            && Character.isUpperCase(methodName.charAt(3))
            && originalMethod.getParameterCount() == 1) {
            String property = StringUtil.firstCharToLowerCase(originalMethod.getName().substring(3));
            updates.put(property, args[0]);

            //去除有忽略注解的字段
            Field field = getField(originalMethod.getDeclaringClass(), property);
            if (field != null) {
                Column column = field.getAnnotation(Column.class);
                if (column != null && column.ignore()) {
                    updates.remove(property);
                }
            }
        }

        return proxyMethod.invoke(self, args);
    }
    
    private Field getField(Class<?> clazz, String fieldName) {
        try {
            FieldWrapper fw = FieldWrapper.of(clazz, fieldName);
            if (fw != null) {
                return fw.getField();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

}