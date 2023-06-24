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

package com.mybatisflex.processor.config;

import com.mybatisflex.processor.util.FileUtil;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Mybatis Flex 生成配置。
 *
 * @author 王帅
 * @since 2023-06-22
 */
public class MybatisFlexConfig {

    /**
     * 配置文件名。
     */
    private static final String APT_FILE_NAME = "apt.config";

    /**
     * mybatis-flex.properties
     */
    protected final Properties properties = new Properties();

    public MybatisFlexConfig(Filer filer) {
        InputStream inputStream = null;
        try {
            //target/classes/apt.config
            FileObject aptConfigFileObject = filer.getResource(StandardLocation.CLASS_OUTPUT, "", APT_FILE_NAME);
            String projectRootPath = FileUtil.getProjectRootPath(aptConfigFileObject.toUri().getPath());


            File aptConfigFile = new File(aptConfigFileObject.toUri());
            while (!aptConfigFile.exists() && !projectRootPath.equals(aptConfigFile.getParentFile().getAbsolutePath())) {
                aptConfigFile = new File(aptConfigFile.getParentFile().getParentFile(), APT_FILE_NAME);
            }

            if (aptConfigFile.exists()) {
                inputStream = Files.newInputStream(aptConfigFile.toPath());
            }

            if (inputStream != null) {
                try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            }
        } catch (Exception ignored) {
            // do nothing here.
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                    // do nothing here.
                }
            }
        }
    }

    public String get(ConfigurationKey key) {
        return properties.getProperty(key.getConfigKey(), key.getDefaultValue());
    }

}