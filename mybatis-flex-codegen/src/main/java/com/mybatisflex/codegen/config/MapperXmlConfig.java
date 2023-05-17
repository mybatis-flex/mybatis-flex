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

package com.mybatisflex.codegen.config;

/**
 * 生成 MapperXml 的配置。
 *
 * @author 王帅
 * @since 2023-05-17
 */
public class MapperXmlConfig {

    /**
     * MapperXml 文件的前缀。
     */
    private String filePrefix = "";

    /**
     * MapperXml 文件的后缀。
     */
    private String fileSuffix = "Mapper";

    public String getFilePrefix() {
        return filePrefix;
    }

    public MapperXmlConfig setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
        return this;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public MapperXmlConfig setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
        return this;
    }

}