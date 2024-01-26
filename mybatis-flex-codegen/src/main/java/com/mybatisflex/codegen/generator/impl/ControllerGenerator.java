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

import com.mybatisflex.codegen.config.ControllerConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.PackageConfig;
import com.mybatisflex.codegen.constant.TemplateConst;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.IGenerator;
import com.mybatisflex.core.util.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller 生成器。
 *
 * @author 王帅
 * @since 2023-05-14
 */
public class ControllerGenerator implements IGenerator {

    private String templatePath;

    public ControllerGenerator() {
        this(TemplateConst.CONTROLLER);
    }

    public ControllerGenerator(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public void generate(Table table, GlobalConfig globalConfig) {

        if (!globalConfig.isControllerGenerateEnable()) {
            return;
        }

        PackageConfig packageConfig = globalConfig.getPackageConfig();
        ControllerConfig controllerConfig = globalConfig.getControllerConfig();

        String sourceDir = StringUtil.isNotBlank(controllerConfig.getSourceDir()) ? controllerConfig.getSourceDir() : packageConfig.getSourceDir();

        String controllerPackagePath = packageConfig.getControllerPackage().replace(".", "/");
        File controllerJavaFile = new File(sourceDir, controllerPackagePath + "/" +
            table.buildControllerClassName() + ".java");


        if (controllerJavaFile.exists() && !controllerConfig.isOverwriteEnable()) {
            return;
        }


        Map<String, Object> params = new HashMap<>(4);
        params.put("table", table);
        params.put("packageConfig", packageConfig);
        params.put("controllerConfig", controllerConfig);
        params.put("javadocConfig", globalConfig.getJavadocConfig());
        params.put("withSwagger", globalConfig.isEntityWithSwagger());
        params.put("swaggerVersion", globalConfig.getSwaggerVersion());
        params.putAll(globalConfig.getCustomConfig());
        globalConfig.getTemplateConfig().getTemplate().generate(params, templatePath, controllerJavaFile);

        System.out.println("Controller ---> " + controllerJavaFile);
    }

    @Override
    public String getTemplatePath() {
        return templatePath;
    }

    @Override
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

}
