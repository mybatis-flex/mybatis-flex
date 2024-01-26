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

import java.io.Serializable;

/**
 * 生成 ServiceImpl 的配置。
 *
 * @author 王帅
 * @since 2023-05-15
 */
@SuppressWarnings("unused")
public class ServiceImplConfig implements Serializable {

    private static final long serialVersionUID = 17115432462168151L;

    /**
     * 代码生成目录，当未配置时，使用 PackageConfig 的配置
     */
    private String sourceDir;

    /**
     * ServiceImpl 类的前缀。
     */
    private String classPrefix = "";

    /**
     * ServiceImpl 类的后缀。
     */
    private String classSuffix = "ServiceImpl";

    /**
     * 自定义 ServiceImpl 的父类。
     */
    private Class<?> superClass;

    /**
     * 是否覆盖之前生成的文件。
     */
    private boolean overwriteEnable;

    /**
     * 是否生成缓存样例代码。
     */
    private boolean cacheExample;

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String buildSuperClassImport() {
        if (superClass == null) {
            return "com.mybatisflex.spring.service.impl.ServiceImpl";
        }
        return superClass.getName();
    }

    public String buildSuperClassName() {
        if (superClass == null) {
            return "ServiceImpl";
        }
        return superClass.getSimpleName();
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
    public ServiceImplConfig setClassPrefix(String classPrefix) {
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
    public ServiceImplConfig setClassSuffix(String classSuffix) {
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
    public ServiceImplConfig setSuperClass(Class<?> superClass) {
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
    public ServiceImplConfig setOverwriteEnable(boolean overwriteEnable) {
        this.overwriteEnable = overwriteEnable;
        return this;
    }

    /**
     * 是否生成缓存例子。
     */
    public boolean isCacheExample() {
        return cacheExample;
    }

    /**
     * 设置生成缓存例子。
     */
    public ServiceImplConfig setCacheExample(boolean cacheExample) {
        this.cacheExample = cacheExample;
        return this;
    }

}
