/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.codegen.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成全局配置类。
 *
 * @author 王帅
 * @since 2023-05-15
 */
@SuppressWarnings("unused")
public class GlobalConfig {

    // === 必须配置 ===

    private final JavadocConfig javadocConfig;
    private final PackageConfig packageConfig;
    private final StrategyConfig strategyConfig;

    // === 可选配置 ===

    private EntityConfig entityConfig;
    private MapperConfig mapperConfig;
    private ServiceConfig serviceConfig;
    private ServiceImplConfig serviceImplConfig;
    private ControllerConfig controllerConfig;
    private TableDefConfig tableDefConfig;
    private MapperXmlConfig mapperXmlConfig;

    // === 其他配置 ===

    private Map<String, Object> customConfig;

    // === 是否启用生成 ===

    private boolean entityGenerateEnable;
    private boolean mapperGenerateEnable;
    private boolean serviceGenerateEnable;
    private boolean serviceImplGenerateEnable;
    private boolean controllerGenerateEnable;
    private boolean tableDefGenerateEnable;
    private boolean mapperXmlGenerateEnable;

    public GlobalConfig() {
        this.javadocConfig = new JavadocConfig();
        this.packageConfig = new PackageConfig();
        this.strategyConfig = new StrategyConfig();
    }

    public JavadocConfig getJavadocConfig() {
        return javadocConfig;
    }

    public PackageConfig getPackageConfig() {
        return packageConfig;
    }

    public StrategyConfig getStrategyConfig() {
        return strategyConfig;
    }

    public EntityConfig getEntityConfig() {
        if (entityConfig == null) {
            entityConfig = new EntityConfig();
        }
        return entityConfig;
    }

    public MapperConfig getMapperConfig() {
        if (mapperConfig == null) {
            mapperConfig = new MapperConfig();
        }
        return mapperConfig;
    }

    public ServiceConfig getServiceConfig() {
        if (serviceConfig == null) {
            serviceConfig = new ServiceConfig();
        }
        return serviceConfig;
    }

    public ServiceImplConfig getServiceImplConfig() {
        if (serviceImplConfig == null) {
            serviceImplConfig = new ServiceImplConfig();
        }
        return serviceImplConfig;
    }

    public ControllerConfig getControllerConfig() {
        if (controllerConfig == null) {
            controllerConfig = new ControllerConfig();
        }
        return controllerConfig;
    }

    public TableDefConfig getTableDefConfig() {
        if (tableDefConfig == null) {
            tableDefConfig = new TableDefConfig();
        }
        return tableDefConfig;
    }

    public MapperXmlConfig getMapperXmlConfig() {
        if (mapperXmlConfig == null) {
            mapperXmlConfig = new MapperXmlConfig();
        }
        return mapperXmlConfig;
    }

    public EntityConfig enableEntity() {
        entityGenerateEnable = true;
        return getEntityConfig();
    }

    public MapperConfig enableMapper() {
        mapperGenerateEnable = true;
        return getMapperConfig();
    }

    public ServiceConfig enableService() {
        serviceGenerateEnable = true;
        return getServiceConfig();
    }

    public ServiceImplConfig enableServiceImpl() {
        serviceImplGenerateEnable = true;
        return getServiceImplConfig();
    }

    public ControllerConfig enableController() {
        controllerGenerateEnable = true;
        return getControllerConfig();
    }

    public TableDefConfig enableTableDef() {
        tableDefGenerateEnable = true;
        return getTableDefConfig();
    }

    public MapperXmlConfig enableMapperXml() {
        mapperXmlGenerateEnable = true;
        return mapperXmlConfig;
    }

    public void disableEntity() {
        entityGenerateEnable = false;
    }

    public void disableMapper() {
        mapperGenerateEnable = false;
    }

    public void disableService() {
        serviceGenerateEnable = false;
    }

    public void disableServiceImpl() {
        serviceImplGenerateEnable = false;
    }

    public void disableController() {
        controllerGenerateEnable = false;
    }

    public void disableTableDef() {
        tableDefGenerateEnable = false;
    }

    public void disableMapperXml() {
        mapperXmlGenerateEnable = false;
    }

    public boolean isEntityGenerateEnable() {
        return entityGenerateEnable;
    }

    public boolean isMapperGenerateEnable() {
        return mapperGenerateEnable;
    }

    public boolean isServiceGenerateEnable() {
        return serviceGenerateEnable;
    }

    public boolean isServiceImplGenerateEnable() {
        return serviceImplGenerateEnable;
    }

    public boolean isControllerGenerateEnable() {
        return controllerGenerateEnable;
    }

    public boolean isTableDefGenerateEnable() {
        return tableDefGenerateEnable;
    }

    public boolean isMapperXmlGenerateEnable() {
        return mapperXmlGenerateEnable;
    }

    public void addCustomConfig(String key, Object value) {
        if (customConfig == null) {
            customConfig = new HashMap<>();
        }
        customConfig.put(key, value);
    }

}
