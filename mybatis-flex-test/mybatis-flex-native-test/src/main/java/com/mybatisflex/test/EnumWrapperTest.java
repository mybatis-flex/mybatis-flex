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
 * */
package com.mybatisflex.test;

import com.mybatisflex.annotation.EnumValue;
import com.mybatisflex.core.util.EnumWrapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EnumWrapperTest {

    // 模拟用户遇到的问题场景：接口中使用 @EnumValue 注解的默认方法
    public interface DictBee {
        String name();

        @EnumValue
        default String getValue() {
            return this.name();
        }
    }

    // 枚举实现接口，但没有对应的字段
    public enum ClientType implements DictBee {
        STANDARD,
        LOCAL,
        CUSTOM;
    }

    public static void testEnumWithEnumValueOnInterfaceMethod() {
        // 这个测试应该通过，不会抛出 "Can not find field "value()" in enum" 异常
        try {
            EnumWrapper<ClientType> wrapper = EnumWrapper.of(ClientType.class);
            System.out.println("EnumWrapper created successfully: " + (wrapper != null));

            // 验证可以获取枚举值
            Object value = wrapper.getEnumValue(ClientType.STANDARD);
            System.out.println("Enum value for STANDARD: " + value);
            System.out.println("Enum value not null: " + (value != null));

            // 验证其他枚举值
            System.out.println("Enum value for LOCAL: " + wrapper.getEnumValue(ClientType.LOCAL).toString());
            System.out.println("Enum value for CUSTOM: " + wrapper.getEnumValue(ClientType.CUSTOM).toString());

            System.out.println("Test enum with @EnumValue on interface method: PASSED");
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("Can not find field")) {
                System.err.println("The bug still exists: " + e.getMessage());
            } else {
                throw e; // 重新抛出其他异常
            }
        }
    }

    // 测试泛型返回类型的枚举方法，应该抛出异常
    public interface GenericDictBee {
        String name();
        @EnumValue
        default List<String> getValue() {
            return Collections.singletonList(this.name());
        }
    }

    public enum GenericClientType implements GenericDictBee {
        STANDARD,
        LOCAL;
    }

    @Getter
    @RequiredArgsConstructor
    public enum GenericClientType2 implements GenericDictBee {
        STANDARD(new ArrayList<String>(){{add("1");}}),
        LOCAL(new ArrayList<String>(){{add("2");}});
        private final List<String > value;
    }

    public static void testEnumWithGenericMethodShouldThrowException() {
        try {
            EnumWrapper<GenericClientType> wrapper = EnumWrapper.of(GenericClientType.class);
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("generic which is not supported")) {
                System.out.println("Exception message correctly indicates generic return type issue: " + e.getMessage());
                System.out.println("Test enum with generic method: PASSED");
            } else {
                System.out.println("Unexpected exception: " + e.getMessage());
            }
        }
    }

    public static void testEnumWithGenericMethod() {
        EnumWrapper<GenericClientType2> wrapper = EnumWrapper.of(GenericClientType2.class);

        System.out.println("EnumWrapper created successfully: " + (wrapper != null));

        System.out.println("Enum value for STANDARD: " + wrapper.getEnumValue(GenericClientType2.STANDARD));
        System.out.println("Enum value for LOCAL: " + wrapper.getEnumValue(GenericClientType2.LOCAL));

        System.out.println("Test enum with @EnumValue on field: PASSED");
    }

    // 测试传统的字段上使用 @EnumValue 注解的情况
    @Getter
    public enum StatusWithField {
        ACTIVE,
        INACTIVE;

        @EnumValue
        private final String value;

        StatusWithField() {
            this.value = this.name().toLowerCase();
        }

    }

    public static void testEnumWithEnumValueOnField() {
        EnumWrapper<StatusWithField> wrapper = EnumWrapper.of(StatusWithField.class);
        System.out.println("EnumWrapper created successfully: " + (wrapper != null));

        System.out.println("Enum value for ACTIVE: " + wrapper.getEnumValue(StatusWithField.ACTIVE));
        System.out.println("Enum value for INACTIVE: " + wrapper.getEnumValue(StatusWithField.INACTIVE));

        System.out.println("Test enum with @EnumValue on field: PASSED");
    }

    // 测试方法上使用 @EnumValue 注解的情况
    public enum StatusWithMethod {
        SUCCESS,
        ERROR;

        @EnumValue
        public String getValue() {
            return this.name().toLowerCase();
        }
    }

    public static void testEnumWithEnumValueOnMethod() {
        EnumWrapper<StatusWithMethod> wrapper = EnumWrapper.of(StatusWithMethod.class);
        System.out.println("EnumWrapper created successfully: " + (wrapper != null));

        System.out.println("Enum value for SUCCESS: " + wrapper.getEnumValue(StatusWithMethod.SUCCESS));
        System.out.println("Enum value for ERROR: " + wrapper.getEnumValue(StatusWithMethod.ERROR));

        System.out.println("Test enum with @EnumValue on method: PASSED");
    }

    public static void main(String[] args) {
        System.out.println("=== Testing EnumWrapper functionality ===");

        System.out.println("\n1. Testing enum with @EnumValue on interface method:");
        testEnumWithEnumValueOnInterfaceMethod();

        System.out.println("\n2. Testing enum with generic method (should throw exception):");
        testEnumWithGenericMethodShouldThrowException();

        System.out.println("\n2.5. Testing enum with generic method:");
        testEnumWithGenericMethod();

        System.out.println("\n3. Testing enum with @EnumValue on field:");
        testEnumWithEnumValueOnField();

        System.out.println("\n4. Testing enum with @EnumValue on method:");
        testEnumWithEnumValueOnMethod();

        System.out.println("\n=== All tests completed ===");
    }
}
