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
import com.mybatisflex.codegen.config.TableDefConfig;
import com.mybatisflex.codegen.constant.TemplateConst;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.IGenerator;
import com.mybatisflex.core.util.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * TableDef 生成器。
 *
 * @author Michael Yang
 * @author 王帅
 */
public class TableDefGenerator implements IGenerator {

    private String templatePath;

    public TableDefGenerator() {
        this(TemplateConst.TABLE_DEF);
    }

    public TableDefGenerator(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public void generate(Table table, GlobalConfig globalConfig) {

        if (!globalConfig.isTableDefGenerateEnable()) {
            return;
        }

        PackageConfig packageConfig = globalConfig.getPackageConfig();
        TableDefConfig tableDefConfig = globalConfig.getTableDefConfig();

        String sourceDir = StringUtil.isNotBlank(tableDefConfig.getSourceDir()) ? tableDefConfig.getSourceDir() : packageConfig.getSourceDir();

        String tableDefPackagePath = packageConfig.getTableDefPackage().replace(".", "/");
        File tableDefJavaFile = new File(sourceDir, tableDefPackagePath + "/" +
            table.buildTableDefClassName() + ".java");


        if (tableDefJavaFile.exists() && !tableDefConfig.isOverwriteEnable()) {
            return;
        }


        Map<String, Object> params = new HashMap<>(4);
        params.put("table", table);
        params.put("packageConfig", packageConfig);
        params.put("tableDefConfig", tableDefConfig);
        params.put("javadocConfig", globalConfig.getJavadocConfig());
        params.put("entityConfig", globalConfig.getEntityConfig());
        params.putAll(globalConfig.getCustomConfig());
        globalConfig.getTemplateConfig().getTemplate().generate(params, templatePath, tableDefJavaFile);

        System.out.println("TableDef ---> " + tableDefJavaFile);
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
