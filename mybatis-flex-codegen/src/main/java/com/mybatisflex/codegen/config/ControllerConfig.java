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

/**
 * 生成 Controller 的配置。
 *
 * @author 王帅
 * @since 2023-05-15
 */
@SuppressWarnings("unused")
public class ControllerConfig {

    /**
     * Controller 类的前缀。
     */
    private String classPrefix = "";

    /**
     * Controller 类的后缀。
     */
    private String classSuffix = "Controller";

    /**
     * 自定义 Controller 的父类。
     */
    private Class<?> supperClass;

    /**
     * 生成 REST 风格的 Controller。
     */
    private boolean restStyle = true;

    public String buildSuperClassImport() {
        return supperClass.getName();
    }

    public String buildSuperClassName() {
        return supperClass.getSimpleName();
    }

    public String getClassPrefix() {
        return classPrefix;
    }

    public ControllerConfig setClassPrefix(String classPrefix) {
        this.classPrefix = classPrefix;
        return this;
    }

    public String getClassSuffix() {
        return classSuffix;
    }

    public ControllerConfig setClassSuffix(String classSuffix) {
        this.classSuffix = classSuffix;
        return this;
    }

    public Class<?> getSupperClass() {
        return supperClass;
    }

    public ControllerConfig setSupperClass(Class<?> supperClass) {
        this.supperClass = supperClass;
        return this;
    }

    public boolean isRestStyle() {
        return restStyle;
    }

    public ControllerConfig setRestStyle(boolean restStyle) {
        this.restStyle = restStyle;
        return this;
    }

}