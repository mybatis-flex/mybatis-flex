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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
    private static final String APT_FILE_NAME = "mybatis-flex.config";

    /**
     * mybatis-flex.properties
     */
    protected final Properties properties = new Properties();

    public MybatisFlexConfig(Filer filer) {
        try {
            //target/classes/
            FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "mybatis-flex");
            File classPathFile = new File(resource.toUri()).getParentFile();

            String projectRootPath = FileUtil.getProjectRootPath(classPathFile, 10);

            List<File> aptConfigFiles = new ArrayList<>();

            while (projectRootPath != null && classPathFile != null
                && projectRootPath.length() <= classPathFile.getAbsolutePath().length()) {
                File aptConfig = new File(classPathFile, APT_FILE_NAME);
                if (aptConfig.exists()) {
                    aptConfigFiles.add(aptConfig);
                }
                classPathFile = classPathFile.getParentFile();
            }


            for (File aptConfigFile : aptConfigFiles) {
                try (InputStream stream = Files.newInputStream(aptConfigFile.toPath());
                     Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                    Properties config = new Properties();
                    config.load(reader);

                    boolean stopBubbling = false;
                    for (Object key : config.keySet()) {
                        if (!properties.containsKey(key)) {
                            properties.put(key, config.getProperty((String) key));
                        }
                        if ("processor.stopBubbling".equalsIgnoreCase((String) key)
                            && "true".equalsIgnoreCase(String.valueOf(config.getProperty((String) key)))) {
                            stopBubbling = true;
                        }
                    }
                    if (stopBubbling) {
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(ConfigurationKey key) {
        return properties.getProperty(key.getConfigKey(), key.getDefaultValue());
    }

}
