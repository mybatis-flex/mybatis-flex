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
import com.mybatisflex.codegen.config.PackageConfig;
import com.mybatisflex.codegen.constant.TemplateConst;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.IGenerator;

import java.io.File;
import java.util.HashMap;
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

        PackageConfig packageConfig = globalConfig.getPackageConfig();

        String sourceDir = packageConfig.getSourceDir();

        Map<String, File> map = new HashMap<>(6);

        if (globalConfig.isEntityGenerateEnable()) {
            String entityPackage = packageConfig.getEntityPackage();
            map.put(entityPackage, getFilePath(sourceDir, entityPackage));
        }
        if (globalConfig.isMapperGenerateEnable()) {
            String mapperPackage = packageConfig.getMapperPackage();
            map.put(mapperPackage, getFilePath(sourceDir, mapperPackage));
        }
        if (globalConfig.isServiceGenerateEnable()) {
            String servicePackage = packageConfig.getServicePackage();
            map.put(servicePackage, getFilePath(sourceDir, servicePackage));
        }
        if (globalConfig.isServiceImplGenerateEnable()) {
            String serviceImplPackage = packageConfig.getServiceImplPackage();
            map.put(serviceImplPackage, getFilePath(sourceDir, serviceImplPackage));
        }
        if (globalConfig.isControllerGenerateEnable()) {
            String controllerPackage = packageConfig.getControllerPackage();
            map.put(controllerPackage, getFilePath(sourceDir, controllerPackage));
        }
        if (globalConfig.isTableDefGenerateEnable()) {
            String tableDefPackage = packageConfig.getTableDefPackage();
            map.put(tableDefPackage, getFilePath(sourceDir, tableDefPackage));
        }

        map.forEach((packageName, filePath) -> {
            Map<String, Object> params = new HashMap<>(3);
            params.put("packageName", packageName);
            params.put("javadocConfig", globalConfig.getJavadocConfig());
            globalConfig.getTemplateConfig().getTemplate().generate(params, templatePath, filePath);
        });
    }

    private File getFilePath(String sourceDir, String packageName) {
        return new File(sourceDir, packageName.replace(".", "/") + "/package-info.java");
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

}
