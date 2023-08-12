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

package com.mybatisflex.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChineseCount {

    private static long count = 0;

    public static void main(String[] args) {
        String rootPath = System.getProperty("user.dir") + "/docs";
        File rootFile = new File(rootPath);
        calculate(rootFile);
        System.out.println("words count: " + count);
    }


    private static void calculate(File dir) {
        File[] files = dir.listFiles(pathname -> !"node_modules".equals(pathname.getName()));
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    calculate(file);
                } else if (file.getName().endsWith(".md")) {
                    System.out.println(file);
                    count += chinese(readString(file));
                }
            }
        }
    }


    private static String readString(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bao = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[1024];
            for (int len; (len = fis.read(buffer)) > 0; ) {
                bao.write(buffer, 0, len);
            }
            return bao.toString("utf-8");
        } catch (Exception e) {
            return "";
        }
    }

    static Pattern symbol = Pattern.compile("[\\u4e00-\\u9fa5]+");

    private static int chinese(String string) {
        int n = 0;
        Matcher matcher = symbol.matcher(string);
        while (matcher.find()) {
            n += (matcher.end() - matcher.start());
        }
        return n;
    }

}
