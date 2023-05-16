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
package com.mybatisflex.processor;


import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.util.Properties;


class MyBatisFlexProps {

    private static final String DEFAULT_ENCODING = "UTF-8";
    protected Properties properties = new Properties();

    public MyBatisFlexProps(Filer filer) {
        InputStream inputStream = null;
        try {
            FileObject propertiesFileObject = filer.getResource(StandardLocation.CLASS_OUTPUT, ""
                    , "mybatis-flex.properties");

            File propertiesFile = new File(propertiesFileObject.toUri());
            if (propertiesFile.exists()) {
                inputStream = propertiesFileObject.openInputStream();
            } else if (getClass().getClassLoader().getResource("mybatis-flex.properties") != null) {
                inputStream = getClass().getClassLoader().getResourceAsStream("mybatis-flex.properties");
            } else {
                File pomXmlFile = new File(propertiesFile.getParentFile().getParentFile().getParentFile(), "pom.xml");
                if (pomXmlFile.exists()) {
                    propertiesFile = new File(pomXmlFile.getParentFile(), "src/main/resources/mybatis-flex.properties");
                }
            }

            if (inputStream == null && propertiesFile.exists()) {
                inputStream = new FileInputStream(propertiesFile);
            }

            if (inputStream != null) {
                properties.load(new InputStreamReader(inputStream, DEFAULT_ENCODING));
            }
        } catch (Exception e) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
