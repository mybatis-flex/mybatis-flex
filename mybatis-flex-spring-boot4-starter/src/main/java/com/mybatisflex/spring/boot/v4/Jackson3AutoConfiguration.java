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
package com.mybatisflex.spring.boot.v4;

import com.mybatisflex.jackson3.Jackson3TypeHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * 将 Spring Boot 4 容器中的 Jackson 3 {@link JsonMapper} 注入到 {@link Jackson3TypeHandler}。
 */
@AutoConfiguration(afterName = "org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration")
@ConditionalOnClass({Jackson3TypeHandler.class, JsonMapper.class})
public class Jackson3AutoConfiguration {

    @Bean
    public Jackson3ObjectMapperCustomizer jackson3ObjectMapperCustomizer(
        ObjectProvider<JsonMapper> jsonMapperProvider,
        ObjectProvider<ObjectMapper> objectMapperProvider) {
        JsonMapper jsonMapper = jsonMapperProvider.getIfAvailable();
        ObjectMapper objectMapper = jsonMapper != null ? jsonMapper : objectMapperProvider.getIfAvailable();
        if (objectMapper != null) {
            Jackson3TypeHandler.setObjectMapper(objectMapper);
        }
        return new Jackson3ObjectMapperCustomizer(objectMapper);
    }

    /**
     * 标记 Bean，便于确认自动配置已生效。
     */
    public static final class Jackson3ObjectMapperCustomizer {
        private final ObjectMapper objectMapper;

        public Jackson3ObjectMapperCustomizer(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public ObjectMapper getObjectMapper() {
            return objectMapper;
        }
    }

}
