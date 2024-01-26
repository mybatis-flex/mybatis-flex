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
 * 生成 TableDef 的配置。
 *
 * @author 王帅
 * @since 2023-05-15
 */
@SuppressWarnings("unused")
public class TableDefConfig implements Serializable {

    private static final long serialVersionUID = 8137903163796008036L;
    /**
     * 代码生成目录，当未配置时，使用 PackageConfig 的配置
     */
    private String sourceDir;
    /**
     * TableDef 类的前缀。
     */
    private String classPrefix = "";

    /**
     * TableDef 类的后缀。
     */
    private String classSuffix = "TableDef";

    /**
     * 是否覆盖之前生成的文件。
     */
    private boolean overwriteEnable;

    /**
     * 生成辅助类的字段风格。
     */
    private NameStyle propertiesNameStyle = NameStyle.UPPER_CASE;

    /**
     * 生成辅助类的引用常量名后缀。
     */
    private String instanceSuffix = "";

    public String buildFieldName(String property) {
        switch (propertiesNameStyle) {
            case UPPER_CASE:
                return StringUtil.camelToUnderline(property).toUpperCase();
            case LOWER_CASE:
                return StringUtil.camelToUnderline(property).toLowerCase();
            case UPPER_CAMEL_CASE:
                return StringUtil.firstCharToUpperCase(property);
            case LOWER_CAMEL_CASE:
            default:
                return StringUtil.firstCharToLowerCase(property);
        }
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    /**
     * 获取类前缀。
     */
    public String getClassPrefix() {
        return classPrefix;
    }

    /**
     * 设置类前缀。
     */
    public TableDefConfig setClassPrefix(String classPrefix) {
        this.classPrefix = classPrefix;
        return this;
    }

    /**
     * 获取类后缀。
     */
    public String getClassSuffix() {
        return classSuffix;
    }

    /**
     * 设置类后缀。
     */
    public TableDefConfig setClassSuffix(String classSuffix) {
        this.classSuffix = classSuffix;
        return this;
    }

    /**
     * 是否覆盖原有文件。
     */
    public boolean isOverwriteEnable() {
        return overwriteEnable;
    }

    /**
     * 设置是否覆盖原有文件。
     */
    public TableDefConfig setOverwriteEnable(boolean overwriteEnable) {
        this.overwriteEnable = overwriteEnable;
        return this;
    }

    /**
     * 获取生成辅助类的字段风格。
     */
    public NameStyle getPropertiesNameStyle() {
        return propertiesNameStyle;
    }

    /**
     * 设置生成辅助类的引用常量名后缀。
     */
    public TableDefConfig setPropertiesNameStyle(NameStyle propertiesNameStyle) {
        this.propertiesNameStyle = propertiesNameStyle;
        return this;
    }

    /**
     * 获取生成辅助类的引用常量名后缀。
     */
    public String getInstanceSuffix() {
        return instanceSuffix;
    }

    /**
     * 设置生成的表对应的变量后缀。
     */
    public TableDefConfig setInstanceSuffix(String instanceSuffix) {
        this.instanceSuffix = instanceSuffix;
        return this;
    }

    public enum NameStyle {

        UPPER_CASE,
        LOWER_CASE,
        UPPER_CAMEL_CASE,
        LOWER_CAMEL_CASE

    }

}
