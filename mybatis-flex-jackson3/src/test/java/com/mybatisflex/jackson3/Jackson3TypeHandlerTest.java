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

import org.junit.After;
import org.junit.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Jackson3TypeHandlerTest {

    @After
    public void resetMapper() {
        Jackson3TypeHandler.setObjectMapper(JsonMapper.builder().build());
    }

    @Test
    public void shouldSerializeAndDeserializeObject() {
        AccessibleHandler handler = new AccessibleHandler(Sample.class);
        Sample sample = new Sample("flex", 3);

        String json = handler.json(sample);
        Object parsed = handler.parse(json);

        assertTrue(parsed instanceof Sample);
        Sample result = (Sample) parsed;
        assertEquals("flex", result.getName());
        assertEquals(3, result.getAge());
    }

    @Test
    public void shouldSerializeAndDeserializeCollection() {
        AccessibleHandler handler = new AccessibleHandler(List.class, Sample.class);
        List<Sample> samples = Arrays.asList(new Sample("a", 1), new Sample("b", 2));

        String json = handler.json(samples);
        Object parsed = handler.parse(json);

        assertTrue(parsed instanceof List);
        List<?> list = (List<?>) parsed;
        assertEquals(2, list.size());
        assertTrue(list.get(0) instanceof Sample);
        assertEquals("a", ((Sample) list.get(0)).getName());
        assertEquals("b", ((Sample) list.get(1)).getName());
    }

    @Test
    public void shouldUseCustomObjectMapper() {
        JsonMapper custom = JsonMapper.builder().build();
        Jackson3TypeHandler.setObjectMapper(custom);

        assertNotNull(Jackson3TypeHandler.getObjectMapper());
        assertEquals(custom, Jackson3TypeHandler.getObjectMapper());
    }

    private static final class AccessibleHandler extends Jackson3TypeHandler {
        private AccessibleHandler(Class<?> propertyType) {
            super(propertyType);
        }

        private AccessibleHandler(Class<?> propertyType, Class<?> genericType) {
            super(propertyType, genericType);
        }

        private Object parse(String json) {
            return parseJson(json);
        }

        private String json(Object object) {
            return toJson(object);
        }
    }

    public static class Sample {
        private String name;
        private int age;

        public Sample() {
        }

        public Sample(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Sample)) {
                return false;
            }
            Sample sample = (Sample) o;
            return age == sample.age && Objects.equals(name, sample.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
    }

}
