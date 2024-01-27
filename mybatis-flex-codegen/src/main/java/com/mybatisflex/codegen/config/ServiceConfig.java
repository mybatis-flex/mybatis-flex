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


import com.mybatisflex.core.service.IService;

import java.io.Serializable;

/**
 * 生成 Service 的配置。
 *
 * @author 王帅
 * @since 2023-05-15
 */
@SuppressWarnings("unused")
public class ServiceConfig implements Serializable {

    private static final long serialVersionUID = -2152473328300910220L;
    /**
     * 代码生成目录，当未配置时，使用 PackageConfig 的配置
     */
    private String sourceDir;

    /**
     * Service 类的前缀。
     */
    private String classPrefix = "";

    /**
     * Service 类的后缀。
     */
    private String classSuffix = "Service";

    /**
     * 自定义 Service 的父类。
     */
    private Class<?> superClass = IService.class;

    /**
     * 是否覆盖之前生成的文件。
     */
    private boolean overwriteEnable;

    public String buildSuperClassImport() {
        return superClass.getName();
    }

    public String buildSuperClassName() {
        return superClass.getSimpleName();
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
    public ServiceConfig setClassPrefix(String classPrefix) {
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
    public ServiceConfig setClassSuffix(String classSuffix) {
        this.classSuffix = classSuffix;
        return this;
    }

    /**
     * 获取父类。
     */
    public Class<?> getSuperClass() {
        return superClass;
    }

    /**
     * 设置父类。
     */
    public ServiceConfig setSuperClass(Class<?> superClass) {
        this.superClass = superClass;
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
    public ServiceConfig setOverwriteEnable(boolean overwriteEnable) {
        this.overwriteEnable = overwriteEnable;
        return this;
    }

}
