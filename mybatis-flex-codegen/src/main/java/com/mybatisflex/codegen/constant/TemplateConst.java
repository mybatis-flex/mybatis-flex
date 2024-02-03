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
 * 代码生成模板常量池。
 *
 * @author 王帅
 * @since 2023-05-16
 */
public final class TemplateConst {

    private TemplateConst() {
    }

    public static final String ENTITY = "/templates/enjoy/entityOrBase.tpl";
    public static final String MAPPER = "/templates/enjoy/mapper.tpl";
    public static final String SERVICE = "/templates/enjoy/service.tpl";
    public static final String SERVICE_IMPL = "/templates/enjoy/serviceImpl.tpl";
    public static final String SERVICE_IMPL_SOLON = "/templates/enjoy/serviceImpl-Solon.tpl";
    public static final String CONTROLLER = "/templates/enjoy/controller.tpl";
    public static final String TABLE_DEF = "/templates/enjoy/tableDef.tpl";
    public static final String MAPPER_XML = "/templates/enjoy/mapperXml.tpl";
    public static final String PACKAGE_INFO = "/templates/enjoy/package-info.tpl";

}
