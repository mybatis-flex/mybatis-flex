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

package com.mybatisflex.processor.util;

import java.io.File;

/**
 * 文件工具类。
 *
 * @author 王帅
 * @since 2023-06-22
 */
public class FileUtil {

    private FileUtil() {
    }

    public static boolean isFromTestSource(String path) {
        return path.contains("test-sources") || path.contains("test-annotations");
    }

    public static boolean isAbsolutePath(String path) {
        return path != null && (path.startsWith("/") || path.contains(":"));
    }

    /**
     * 获取项目的根目录，也就是根节点 pom.xml 所在的目录
     */
    public static String getProjectRootPath(String genFilePath) {
        File file = new File(genFilePath);
        int count = 20;
        return getProjectRootPath(file, count);
    }

    public static String getProjectRootPath(File file, int count) {
        if (count <= 0) {
            return null;
        }
        if (file.isFile()) {
            return getProjectRootPath(file.getParentFile(), --count);
        } else {
            if (new File(file, "pom.xml").exists() && !new File(file.getParentFile(), "pom.xml").exists()) {
                return file.getAbsolutePath();
            } else {
                return getProjectRootPath(file.getParentFile(), --count);
            }
        }
    }

}