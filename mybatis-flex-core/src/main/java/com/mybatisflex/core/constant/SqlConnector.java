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
package com.mybatisflex.core.constant;

/**
 * @author michael
 */
public enum SqlConnector {


    /**
     * And
     */
    AND(" AND "),
    //    AND_NOT(" AND NOT "),
//    AND_EXISTS(" AND EXISTS "),
//    AND_NOT_EXISTS(" AND NOT EXISTS "),

    /**
     * OR
     */
    OR(" OR "),
//    OR_NOT(" OR NOT "),
//    OR_EXISTS(" OR EXISTS "),
//    OR_NOT_EXISTS(" OR NOT EXISTS "),
//    NOT(" NOT "),
    ;


    private final String value;

    SqlConnector(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
