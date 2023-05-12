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
import com.mybatisflex.core.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class EnjoyTemplate implements ITemplate {

    private final Engine engine;

    public EnjoyTemplate() {
        engine = Engine.create("mybatis-flex", engine -> {
            engine.setToClassPathSourceFactory();
            engine.addSharedMethod(StringUtil.class);
        });
    }

    @Override
    public void generate(Map<String, Object> params, String templateFilePath, File generateFile) {
        if (!generateFile.getParentFile().exists() && !generateFile.getParentFile().mkdirs()){
            throw new IllegalStateException("Can not mkdirs by dir: " + generateFile.getParentFile());
        }

        try(FileOutputStream  fileOutputStream = new FileOutputStream(generateFile)) {
            engine.getTemplate(templateFilePath).render(params, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
