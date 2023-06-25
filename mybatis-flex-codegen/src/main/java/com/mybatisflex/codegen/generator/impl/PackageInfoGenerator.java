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
package com.mybatisflex.codegen.generator.impl;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.JavadocConfig;
import com.mybatisflex.codegen.config.PackageConfig;
import com.mybatisflex.codegen.constant.TemplateConst;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.IGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * package-info.java 生成器。
 *
 * @author 王帅
 * @since 2023-05-17
 */
public class PackageInfoGenerator implements IGenerator {

    private String templatePath;

    public PackageInfoGenerator() {
        this(TemplateConst.PACKAGE_INFO);
    }

    public PackageInfoGenerator(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public void generate(Table table, GlobalConfig globalConfig) {

        if (!globalConfig.isPackageInfoGenerateEnable()) {
            return;
        }

        JavadocConfig javadocConfig = globalConfig.getJavadocConfig();
        PackageConfig packageConfig = globalConfig.getPackageConfig();

        String sourceDir = packageConfig.getSourceDir();

        List<Data> dataList = new ArrayList<>();

        if (globalConfig.isEntityGenerateEnable()) {
            dataList.add(new Data(sourceDir, packageConfig.getEntityPackage(), javadocConfig.getEntityPackage()));
        }
        if (globalConfig.isMapperGenerateEnable()) {
            dataList.add(new Data(sourceDir, packageConfig.getMapperPackage(), javadocConfig.getMapperPackage()));
        }
        if (globalConfig.isServiceGenerateEnable()) {
            dataList.add(new Data(sourceDir, packageConfig.getServicePackage(), javadocConfig.getServicePackage()));
        }
        if (globalConfig.isServiceImplGenerateEnable()) {
            dataList.add(new Data(sourceDir, packageConfig.getServiceImplPackage(), javadocConfig.getServiceImplPackage()));
        }
        if (globalConfig.isControllerGenerateEnable()) {
            dataList.add(new Data(sourceDir, packageConfig.getControllerPackage(), javadocConfig.getControllerPackage()));
        }
        if (globalConfig.isTableDefGenerateEnable()) {
            dataList.add(new Data(sourceDir, packageConfig.getTableDefPackage(), javadocConfig.getTableDefPackage()));
        }

        dataList.forEach(data -> {
            Map<String, Object> params = new HashMap<>(3);
            params.put("packageName", data.packageName);
            params.put("packageComment", data.packageComment);
            params.put("javadocConfig", javadocConfig);
            globalConfig.getTemplateConfig().getTemplate().generate(params, templatePath, data.filePath);
        });
    }

    @Override
    public String getTemplatePath() {
        return templatePath;
    }

    @Override
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * 内置类，用于存放数据。
     */
    private static class Data {

        String packageName;
        String packageComment;
        File filePath;

        Data(String sourceDir, String packageName, String packageComment) {
            this.packageName = packageName;
            this.packageComment = packageComment;
            this.filePath = getFilePath(sourceDir, packageName);
        }

        File getFilePath(String sourceDir, String packageName) {
            return new File(sourceDir, packageName.replace(".", "/") + "/package-info.java");
        }

    }

}
