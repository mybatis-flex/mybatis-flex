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
package com.mybatisflex.codegen.constant;

/**
 * 生成类型常量。
 *
 * @author 王帅
 * @since 2023-05-17
 */
public class GenTypeConst {

    private GenTypeConst() {
    }

    public static final String ENTITY = "entity";
    public static final String MAPPER = "mapper";
    public static final String SERVICE = "service";
    public static final String SERVICE_IMPL = "serviceImpl";
    public static final String CONTROLLER = "controller";
    public static final String TABLE_DEF = "tableDef";
    public static final String MAPPER_XML = "mapperXml";
    public static final String PACKAGE_INFO = "package-info";

}