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

package com.mybatisflex.codegen.config;

import com.mybatisflex.core.util.StringUtil;

/**
 * 生成软件包的配置。
 *
 * @author 王帅
 * @since 2023-05-15
 */
@SuppressWarnings("unused")
public class PackageConfig {

    /**
     * 代码生成目录。
     */
    private String sourceDir;

    /**
     * 根包。
     */
    private String basePackage = "com.mybatisflex";

    /**
     * Entity 所在包。
     */
    private String entityPackage;

    /**
     * Mapper 所在包。
     */
    private String mapperPackage;

    /**
     * Service 所在包。
     */
    private String servicePackage;

    /**
     * ServiceImpl 所在包。
     */
    private String serviceImplPackage;

    /**
     * Controller 所在包。
     */
    private String controllerPackage;

    /**
     * TableDef 所在包。
     */
    private String tableDefPackage;

    /**
     * MapperXml 文件所在位置。
     */
    private String mapperXmlPath;

    public String getSourceDir() {
        if (StringUtil.isBlank(sourceDir)) {
            return System.getProperty("user.dir") + "/src/main/java";
        }
        return sourceDir;
    }

    public PackageConfig setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
        return this;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public PackageConfig setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public String getEntityPackage() {
        if (StringUtil.isBlank(entityPackage)) {
            return basePackage.concat(".entity");
        }
        return entityPackage;
    }

    public PackageConfig setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
        return this;
    }

    public String getMapperPackage() {
        if (StringUtil.isBlank(mapperPackage)) {
            return basePackage.concat(".mapper");
        }
        return mapperPackage;
    }

    public PackageConfig setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
        return this;
    }

    public String getServicePackage() {
        if (StringUtil.isBlank(servicePackage)) {
            return basePackage.concat(".service");
        }
        return servicePackage;
    }

    public PackageConfig setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
        return this;
    }

    public String getServiceImplPackage() {
        if (StringUtil.isBlank(serviceImplPackage)) {
            return basePackage.concat(".service.impl");
        }
        return serviceImplPackage;
    }

    public PackageConfig setServiceImplPackage(String serviceImplPackage) {
        this.serviceImplPackage = serviceImplPackage;
        return this;
    }

    public String getControllerPackage() {
        if (StringUtil.isBlank(controllerPackage)) {
            return basePackage.concat(".controller");
        }
        return controllerPackage;
    }

    public PackageConfig setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
        return this;
    }

    public String getTableDefPackage() {
        if (StringUtil.isBlank(tableDefPackage)) {
            return getEntityPackage().concat(".tables");
        }
        return tableDefPackage;
    }

    public PackageConfig setTableDefPackage(String tableDefPackage) {
        this.tableDefPackage = tableDefPackage;
        return this;
    }

    public String getMapperXmlPath() {
        if (StringUtil.isBlank(mapperXmlPath)) {
            return getSourceDir().concat("/resources/mapper");
        }
        return mapperXmlPath;
    }

    public PackageConfig setMapperXmlPath(String mapperXmlPath) {
        this.mapperXmlPath = mapperXmlPath;
        return this;
    }

}