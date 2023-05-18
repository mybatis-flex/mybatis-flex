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
package com.mybatisflex.codegen.generator;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.entity.Table;

/**
 * 文件生成器接口。
 *
 * @author Michael Yang
 * @author 王帅
 */
public interface IGenerator {

    /**
     * 获取模板文件位置。
     *
     * @return 路径
     */
    String getTemplatePath();

    /**
     * 设置模板文件位置。
     *
     * @param templatePath
     */
    void setTemplatePath(String templatePath);

    /**
     * 根据模板生成文件。
     *
     * @param table        表内容
     * @param globalConfig 全局配置
     */
    void generate(Table table, GlobalConfig globalConfig);

}
