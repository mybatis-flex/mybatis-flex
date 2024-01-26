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

import java.io.Serializable;

/**
 * 生成软件包的配置。
 *
 * @author 王帅
 * @since 2023-05-15
 */
@SuppressWarnings("unused")
public class PackageConfig implements Serializable {

    private static final long serialVersionUID = -8257632247633439537L;
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

    /**
     * 获取生成目录。
     */
    public String getSourceDir() {
        if (StringUtil.isBlank(sourceDir)) {
            return System.getProperty("user.dir") + "/src/main/java";
        }
        return sourceDir;
    }

    /**
     * 设置生成目录。
     */
    public PackageConfig setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
        return this;
    }

    /**
     * 获得根包路径。
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * 设置根包路径。
     */
    public PackageConfig setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    /**
     * 获取实体类层包路径。
     */
    public String getEntityPackage() {
        if (StringUtil.isBlank(entityPackage)) {
            return basePackage.concat(".entity");
        }
        return entityPackage;
    }

    /**
     * 设置实体类层包路径。
     */
    public PackageConfig setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
        return this;
    }

    /**
     * 获取映射层包路径。
     */
    public String getMapperPackage() {
        if (StringUtil.isBlank(mapperPackage)) {
            return basePackage.concat(".mapper");
        }
        return mapperPackage;
    }

    /**
     * 设置映射层包路径。
     */
    public PackageConfig setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
        return this;
    }

    /**
     * 获取服务层包路径。
     */
    public String getServicePackage() {
        if (StringUtil.isBlank(servicePackage)) {
            return basePackage.concat(".service");
        }
        return servicePackage;
    }

    /**
     * 设置服务层包路径。
     */
    public PackageConfig setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
        return this;
    }

    /**
     * 获取服务层实现包路径。
     */
    public String getServiceImplPackage() {
        if (StringUtil.isBlank(serviceImplPackage)) {
            return basePackage.concat(".service.impl");
        }
        return serviceImplPackage;
    }

    /**
     * 设置服务层实现包路径。
     */
    public PackageConfig setServiceImplPackage(String serviceImplPackage) {
        this.serviceImplPackage = serviceImplPackage;
        return this;
    }

    /**
     * 获取控制层包路径。
     */
    public String getControllerPackage() {
        if (StringUtil.isBlank(controllerPackage)) {
            return basePackage.concat(".controller");
        }
        return controllerPackage;
    }

    /**
     * 设置控制层包路径。
     */
    public PackageConfig setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
        return this;
    }

    /**
     * 获取表定义层包路径。
     */
    public String getTableDefPackage() {
        if (StringUtil.isBlank(tableDefPackage)) {
            return getEntityPackage().concat(".table");
        }
        return tableDefPackage;
    }

    /**
     * 设置表定义层包路径。
     */
    public PackageConfig setTableDefPackage(String tableDefPackage) {
        this.tableDefPackage = tableDefPackage;
        return this;
    }

    /**
     * 获取 Mapper XML 文件路径。
     */
    public String getMapperXmlPath() {
        if (StringUtil.isBlank(mapperXmlPath)) {
            return System.getProperty("user.dir").concat("/src/main/resources/mapper");
        }
        return mapperXmlPath;
    }

    /**
     * 设置 Mapper XML 文件路径。
     */
    public PackageConfig setMapperXmlPath(String mapperXmlPath) {
        this.mapperXmlPath = mapperXmlPath;
        return this;
    }

}
