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
package com.mybatisflex.codegen.generator.impl;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.IGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EntityGenerator implements IGenerator {

    private String templatePath = "/templates/enjoy/entity.tpl";

    public EntityGenerator() {
    }

    public EntityGenerator(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public void generate(Table table, GlobalConfig globalConfig) {

        String entityPackagePath = globalConfig.getEntityPackage().replace(".", "/");
        File entityJavaFile = new File(globalConfig.getSourceDir(), entityPackagePath + "/" +
                table.buildEntityClassName() + ".java");

        Map<String, Object> params = new HashMap<>();
        params.put("table", table);
        params.put("globalConfig", globalConfig);

        globalConfig.getTemplateEngine().generate(params, templatePath, entityJavaFile);
    }
}
