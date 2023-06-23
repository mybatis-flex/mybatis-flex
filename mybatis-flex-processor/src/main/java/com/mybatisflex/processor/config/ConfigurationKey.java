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
     * APT 代码生成路径。
     */
    GEN_PATH("processor.genPath", ""),

    /**
     * 自定义 Tables 生成的包名。
     */
    TABLES_PACKAGE("processor.tablesPackage", null),

    /**
     * 自定义 Mapper 的父类。
     */
    BASE_MAPPER_CLASS("processor.baseMapperClass", "com.mybatisflex.core.BaseMapper"),

    /**
     * 开启 Mapper 自动生成。
     */
    MAPPERS_GENERATE_ENABLE("processor.mappersGenerateEnable", "false"),

    /**
     * 自定义 Mapper 生成的包名。
     */
    MAPPERS_PACKAGE("processor.mappersPackage", null),

    /**
     * 是否所有的类都生成在 Tables 类里。
     */
    ALL_IN_TABLES("processor.allInTables", "false"),

    /**
     * Tables 类名。
     */
    TABLES_CLASS_NAME("processor.tablesClassName", "Tables"),

    /**
     * 生成辅助类的字段风格。
     */
    TABLE_NAME_STYLE("processor.tablesNameStyle", "upperCase"),

    /**
     * 生成的表对应的变量后缀。
     */
    TABLES_DEF_SUFFIX("processor.tablesDefSuffix", ""),

    /**
     * 过滤 Entity 后缀。
     */
    IGNORE_SUFFIXES("processor.entity.ignoreSuffixes", "");

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