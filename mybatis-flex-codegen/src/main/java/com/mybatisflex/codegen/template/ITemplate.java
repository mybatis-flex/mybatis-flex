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
package com.mybatisflex.codegen.template;

import java.io.File;
import java.util.Map;

/**
 * 模板引擎。
 */
@FunctionalInterface
public interface ITemplate {

    /**
     * 使用模板引擎生成代码。
     *
     * @param params           生成参数
     * @param templateFilePath 模板文件位置
     * @param generateFile     生成文件位置
     */
    void generate(Map<String, Object> params, String templateFilePath, File generateFile);

}
