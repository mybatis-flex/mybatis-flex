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
package com.mybatisflex.codegen.template;

import com.jfinal.template.Engine;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.core.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EnjoyTemplate implements ITemplate {

    private Engine engine;

    public EnjoyTemplate() {
        engine = Engine.create("mybatis-flex", engine -> {
            engine.setToClassPathSourceFactory();
            engine.addSharedMethod(StringUtil.class);
        });
    }

    @Override
    public void generateEntity(GlobalConfig globalConfig, Table table, File entityJavaFile) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("globalConfig", globalConfig);
        params.put("table", table);


        FileOutputStream fileOutputStream = new FileOutputStream(entityJavaFile);
        engine.getTemplate("/templates/enjoy/entity.tpl").render(params, fileOutputStream);
    }


    @Override
    public void generateMapper(GlobalConfig globalConfig, Table table, File mapperJavaFile) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("globalConfig", globalConfig);
        params.put("table", table);


        FileOutputStream fileOutputStream = new FileOutputStream(mapperJavaFile);
        engine.getTemplate("/templates/enjoy/mapper.tpl").render(params, fileOutputStream);
    }
}
