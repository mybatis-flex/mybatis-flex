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

package com.mybatisflex.processor.config;

/**
 * 配置键值。
 *
 * @author 王帅
 * @since 2023-06-22
 */
public enum ConfigurationKey {

    /**
     * 全局启用 APT 开关。
     */
    ENABLE("processor.enable", ""),

    /**
     * 生成文件的字符集。
     */
    CHARSET("processor.charset", "UTF-8"),

    /**
     * APT 代码生成路径。
     */
    GEN_PATH("processor.genPath", ""),


    /**
     * 是否所有的类都生成在 Tables 类里。
     */
    ALL_IN_TABLES_ENABLE("processor.allInTables.enable", "false"),

    /**
     * Tables 类名。
     */
    ALL_IN_TABLES_CLASS_NAME("processor.allInTables.className", "Tables"),

    /**
     * 自定义 Tables 生成的包名。
     */
    ALL_IN_TABLES_PACKAGE("processor.allInTables.package", null),


    /**
     * 开启 Mapper 自动生成。
     */
    MAPPER_GENERATE_ENABLE("processor.mapper.generateEnable", "false"),

    /**
     * 开启 @Mapper 注解。
     */
    MAPPER_ANNOTATION("processor.mapper.annotation", "false"),

    /**
     * 自定义 Mapper 的父类。
     */
    MAPPER_BASE_CLASS("processor.mapper.baseClass", "com.mybatisflex.core.BaseMapper"),

    /**
     * 自定义 Mapper 生成的包名。
     */
    MAPPER_PACKAGE("processor.mapper.package", null),


    /**
     * 生成的 Class 的后缀。
     */
    TABLE_DEF_CLASS_SUFFIX("processor.tableDef.classSuffix", "TableDef"),

    /**
     * 生成的表对应的变量后缀。
     */
    TABLE_DEF_INSTANCE_SUFFIX("processor.tableDef.instanceSuffix", ""),

    /**
     * 生成辅助类的字段风格。
     */
    TABLE_DEF_PROPERTIES_NAME_STYLE("processor.tableDef.propertiesNameStyle", "upperCase"),

    /**
     * 过滤 Entity 后缀。
     */
    TABLE_DEF_IGNORE_ENTITY_SUFFIXES("processor.tableDef.ignoreEntitySuffixes", "");


    private final String configKey;
    private final String defaultValue;

    ConfigurationKey(String configKey, String defaultValue) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    /**
     * 获取配置键。
     *
     * @return 键
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * 获取配置默认值。
     *
     * @return 默认值
     */
    public String getDefaultValue() {
        return defaultValue;
    }

}
