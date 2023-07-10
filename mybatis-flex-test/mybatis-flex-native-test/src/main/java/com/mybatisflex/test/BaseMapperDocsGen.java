package com.mybatisflex.test;

import com.alibaba.fastjson2.JSON;
import com.mybatisflex.core.util.StringUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BaseMapperDocsGen {

    public static void main(String[] args) throws IOException {

        List<MethodInfo> methodInfos = readBaseMapperMethodsInfo();

        StringBuilder insertMethods = new StringBuilder();
        StringBuilder deleteMethods = new StringBuilder();
        StringBuilder updateMethods = new StringBuilder();
        StringBuilder selectMethods = new StringBuilder();
        StringBuilder relationMethods = new StringBuilder();
        StringBuilder paginateMethods = new StringBuilder();

        for (MethodInfo methodInfo : methodInfos) {
            if (methodInfo.name.startsWith("insert")) {
                insertMethods.append("- **`").append(methodInfo.getName()).append("`**: ").append(methodInfo.desc).append("\n");
            } else if (methodInfo.name.startsWith("delete")) {
                deleteMethods.append("- **`").append(methodInfo.getName()).append("`**: ").append(methodInfo.desc).append("\n");
            } else if (methodInfo.name.startsWith("update")) {
                updateMethods.append("- **`").append(methodInfo.getName()).append("`**: ").append(methodInfo.desc).append("\n");
            } else  if (methodInfo.name.startsWith("select")) {
                selectMethods.append("- **`").append(methodInfo.getName()).append("`**: ").append(methodInfo.desc).append("\n");
                if (methodInfo.name.contains("WithRelation")){
                    relationMethods.append("- **`").append(methodInfo.getName()).append("`**: ").append(methodInfo.desc).append("\n");
                }
            }else  if (methodInfo.name.startsWith("paginate")) {
                paginateMethods.append("- **`").append(methodInfo.getName()).append("`**: ").append(methodInfo.desc).append("\n");
				if (methodInfo.name.contains("WithRelation")){
					relationMethods.append("- **`").append(methodInfo.getName()).append("`**: ").append(methodInfo.desc).append("\n");
				}
            }
        }

        String mdDir = System.getProperty("user.dir") + "/docs/zh/base/parts/";
        writeString(new File(mdDir, "base-mapper-insert-methods.md"), insertMethods.toString());
        writeString(new File(mdDir, "base-mapper-delete-methods.md"), deleteMethods.toString());
        writeString(new File(mdDir, "base-mapper-update-methods.md"), updateMethods.toString());
        writeString(new File(mdDir, "base-mapper-query-methods.md"), selectMethods.toString());
        writeString(new File(mdDir, "base-mapper-relation-methods.md"), relationMethods.toString());
        writeString(new File(mdDir, "base-mapper-paginate-methods.md"), paginateMethods.toString());


        ///Users/michael/work/git/mybatis-flex/docs/zh/base/parts/base-mapper-insert-methods.md

        System.out.println(JSON.toJSON(methodInfos));
    }

    private static List<MethodInfo> readBaseMapperMethodsInfo() throws IOException {
        List<MethodInfo> methodInfos = new ArrayList<>();
        String path = System.getProperty("user.dir") + "/mybatis-flex-core/src/main/java/com/mybatisflex/core/BaseMapper.java";
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        MethodInfo methodInfo = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.equals("/**")) {
                methodInfo = new MethodInfo();
            } else {
                if (methodInfo != null && line.length() > 3 && line.startsWith("*") && line.charAt(2) != '@') {
                    methodInfo.addDesc(line.substring(1));
                } else if (methodInfo != null && !line.contains("=") && (line.startsWith("default")
                        || line.startsWith("int")
                        || line.startsWith("T ")
                        || line.startsWith("List<T> ")
                )) {
                    String[] tokens = line.split(" ");
                    int methodTokenIndex = 1;
                    if (line.contains("default")) {
                        methodTokenIndex++;
                    }
                    if (line.contains("<R>")) {
                        methodTokenIndex++;
                    }
                    String methodToken = tokens[methodTokenIndex];
                    methodInfo.setName(line.substring(line.indexOf(methodToken)));
                    methodInfos.add(methodInfo);
                }
            }
        }

        br.close();
        return methodInfos;
    }


    public static void writeString(File file, String content) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, false);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
    }

    public static class MethodInfo {
        private String name;
        private String desc;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (name.endsWith("{") || name.endsWith(";")) {
                name = name.substring(0, name.length() - 1);
            }

            name = name.trim();
            String paramsString = name.substring(name.indexOf("(") + 1, name.lastIndexOf(")"));
            if (paramsString.length() > 0) {
                String[] params = paramsString.split(",");
                for (int i = 0; i < params.length; i++) {
                    String[] paramInfos = params[i].split(" ");
                    params[i] = paramInfos[paramInfos.length - 1];
                }
                paramsString = StringUtil.join(", ", params);
                name = name.substring(0, name.indexOf("(") + 1) + paramsString + ")";
                this.name = name;
            } else {
                this.name = name;
            }
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public void addDesc(String desc) {
            if (this.desc == null) {
                this.desc = desc.trim();
            } else {
                this.desc += desc.trim();
            }
            if (this.desc.contains("{@code")){
                this.desc = this.desc.replace("{@code ","`").replace("}","`");
            }
        }

        @Override
        public String toString() {
            return "MethodInfo{" +
                    "name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }
}
