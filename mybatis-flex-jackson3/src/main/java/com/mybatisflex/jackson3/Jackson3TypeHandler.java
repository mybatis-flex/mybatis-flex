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
package com.mybatisflex.jackson3;

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.handler.BaseJsonTypeHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collection;

/**
 * Jackson 3 TypeHandler。
 * <p>
 * 使用 {@code tools.jackson} 包（Jackson 3）。Jackson 2 请继续使用
 * {@link com.mybatisflex.core.handler.JacksonTypeHandler}。
 */
public class Jackson3TypeHandler extends BaseJsonTypeHandler<Object> {

    private static volatile ObjectMapper objectMapper;
    private final Class<?> propertyType;
    private Class<?> genericType;
    private JavaType javaType;

    public Jackson3TypeHandler(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public Jackson3TypeHandler(Class<?> propertyType, Class<?> genericType) {
        this.propertyType = propertyType;
        this.genericType = genericType;
    }

    @Override
    protected Object parseJson(String json) {
        try {
            if (genericType != null && Collection.class.isAssignableFrom(propertyType)) {
                return getObjectMapper().readValue(json, getJavaType());
            }
            return getObjectMapper().readValue(json, propertyType);
        } catch (JacksonException e) {
            throw FlexExceptions.wrap(e, "Can not parseJson by Jackson3TypeHandler: " + json);
        }
    }

    @Override
    protected String toJson(Object object) {
        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JacksonException e) {
            throw FlexExceptions.wrap(e, "Can not convert object to Json by Jackson3TypeHandler: " + object);
        }
    }

    public JavaType getJavaType() {
        if (javaType == null) {
            javaType = getObjectMapper().getTypeFactory()
                .constructCollectionType((Class<? extends Collection>) propertyType, genericType);
        }
        return javaType;
    }

    public static ObjectMapper getObjectMapper() {
        ObjectMapper current = objectMapper;
        if (current == null) {
            synchronized (Jackson3TypeHandler.class) {
                current = objectMapper;
                if (current == null) {
                    current = JsonMapper.builder().build();
                    objectMapper = current;
                }
            }
        }
        return current;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        Jackson3TypeHandler.objectMapper = objectMapper;
    }

}
