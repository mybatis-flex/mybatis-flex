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
package com.mybatisflex.processer;


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class QueryEntityProcesser extends AbstractProcessor {

    private static final String classTableTemplate = "package @package;\n" +
            "\n" +
            "import com.mybatisflex.core.querywrapper.QueryColumn;\n" +
            "import com.mybatisflex.core.table.TableDef;\n" +
            "\n" +
            "// Auto generate by mybatis-flex, do not modify it.\n" +
            "public class Tables {\n" +
            "@classesInfo" +
            "}\n";

    private static final String tableDefTemplate = "\n\n    public static final @entityClassTableDef @tableField = new @entityClassTableDef(\"@tableName\");\n";


    private static final String classTemplate = "\n" +
            "    public static class @entityClassTableDef extends TableDef {\n" +
            "\n" +
            "@queryColumns" +
            "@allColumns" +
            "\n" +
            "        public @entityClassTableDef(String tableName) {\n" +
            "            super(tableName);\n" +
            "        }\n" +
            "    }\n";


    private static final String columnsTemplate = "        public QueryColumn @property = new QueryColumn(this, \"@columnName\");\n";
    private static final String allColumnsTemplate = "\n        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{@allColumns};\n\n";

    protected Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!roundEnv.processingOver()) {

            MyBatisFlexProps props = new MyBatisFlexProps("mybatis-flex.properties");

            String enable = props.getProperties().getProperty("processer.enable", "");
            if ("false".equalsIgnoreCase(enable)) {
                return true;
            }
            String genPath = props.getProperties().getProperty("processer.genPath", "");
            final String genPackage = props.getProperties().getProperty("processer.package");
            String className = props.getProperties().getProperty("processer.className", "Tables");

            StringBuilder guessPackage = new StringBuilder();


            StringBuilder tablesContent = new StringBuilder();
            roundEnv.getElementsAnnotatedWith(Table.class).forEach((Consumer<Element>) entityClassElement -> {

                Table table = entityClassElement.getAnnotation(Table.class);

                //init genPackage
                if ((genPackage == null || genPackage.trim().length() == 0)
                        && guessPackage.length() == 0) {
                    String entityClassName = entityClassElement.toString();
                    if (!entityClassName.contains(".")) {
                        guessPackage.append("table");// = "table";
                    } else {
                        guessPackage.append(entityClassName.substring(0, entityClassName.lastIndexOf(".")) + ".table");
                    }
                }

                String tableName = table != null && table.value().trim().length() != 0
                        ? table.value()
                        : firstCharToLowerCase(entityClassElement.getSimpleName().toString());


                Map<String, String> propertyAndColumns = new LinkedHashMap<>();

                TypeElement classElement = (TypeElement) entityClassElement;
                for (Element fieldElement : classElement.getEnclosedElements()) {

                    //all fields
                    if (ElementKind.FIELD == fieldElement.getKind()) {
                        Column column = fieldElement.getAnnotation(Column.class);
                        if (column != null && column.ignore()) {
                            continue;
                        }
                        String columnName = column != null && column.value().trim().length() > 0 ? column.value() : camelToUnderline(fieldElement.toString());
                        propertyAndColumns.put(fieldElement.toString(), columnName);
                    }
                }

                String entityClassName = entityClassElement.getSimpleName().toString();
                tablesContent.append(buildClass(entityClassName, tableName, propertyAndColumns));
            });

            if (tablesContent.length() > 0) {
                String realGenPackage = genPackage == null || genPackage.trim().length() == 0 ? guessPackage.toString() : genPackage;
                genClass(genPath, realGenPackage, className, tablesContent.toString());
            }

        }


        return false;
    }

    private String buildClass(String entityClass, String tableName, Map<String, String> propertyAndColumns) {

        // tableDefTemplate = "    public static final @entityClassTableDef @tableField = new @entityClassTableDef(\"@tableName\");\n";

        String tableDef = tableDefTemplate.replace("@entityClass", entityClass)
                .replace("@tableField", entityClass.toUpperCase())
                .replace("@tableName", tableName);


        //columnsTemplate = "        public QueryColumn @property = new QueryColumn(this, \"@columnName\");\n";
        StringBuilder queryColumns = new StringBuilder();
        propertyAndColumns.forEach((property, column) ->
                queryColumns.append(columnsTemplate
                        .replace("@property", camelToUnderline(property).toUpperCase())
                        .replace("@columnName", column)
                ));


//        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{@allColumns};
        StringJoiner allColumns = new StringJoiner(", ");
        propertyAndColumns.forEach((property, column) -> allColumns.add(camelToUnderline(property).toUpperCase()));
        String allColumnsString = allColumnsTemplate.replace("@allColumns", allColumns.toString());


//        classTemplate = "\n" +
//                "    public static class @entityClassTableDef extends TableDef {\n" +
//                "\n" +
//                "@queryColumns" +
//                "@allColumns" +
//                "\n" +
//                "        public @entityClassTableDef(String tableName) {\n" +
//                "            super(tableName);\n" +
//                "        }\n" +
//                "    }\n";

        String tableClass = classTemplate.replace("@entityClass", entityClass)
                .replace("@queryColumns", queryColumns)
                .replace("@allColumns", allColumnsString);

        return tableDef + tableClass;
    }


    private void genClass(String genBasePath, String genPackageName, String className, String classContent) {
        String genContent = classTableTemplate.replace("@package", genPackageName)
                .replace("@classesInfo", classContent);

        Writer writer = null;
        try {
            JavaFileObject sourceFile = filer.createSourceFile(genPackageName + "." + className);
            if (genBasePath == null || genBasePath.trim().length() == 0) {
                writer = sourceFile.openWriter();
                writer.write(genContent);
                writer.flush();

                printMessage(">>>>> mybatis-flex success generate tables class: \n" + sourceFile.toUri());
                return;
            }


            String defaultGenPath = sourceFile.toUri().getPath();

            //真实的生成代码的目录
            String realPath;

            //用户配置的路径为绝对路径
            if (isAbsolutePath(genBasePath)) {
                realPath = genBasePath;
            }
            //配置的是相对路径，那么则以项目根目录为相对路径
            else {
                String projectRootPath = getProjectRootPath(defaultGenPath);
                realPath = new File(projectRootPath, genBasePath).getAbsolutePath();
            }

            //通过在 test/java 目录下执行编译生成的
            boolean fromTestSource = isFromTestSource(defaultGenPath);
            if (fromTestSource) {
                realPath = new File(realPath, "src/test/java").getAbsolutePath();
            } else {
                realPath = new File(realPath, "src/main/java").getAbsolutePath();
            }

            File genJavaFile = new File(realPath, (genPackageName + "." + className).replace(".", "/") + ".java");
            if (!genJavaFile.getParentFile().exists() && !genJavaFile.getParentFile().mkdirs()) {
                System.out.println(">>>>>ERROR: can not mkdirs by mybatis-flex processer for: " + genJavaFile.getParentFile());
                return;
            }

            writer = new PrintWriter(new FileOutputStream(genJavaFile));
            writer.write(classContent);
            writer.flush();

            printMessage(">>>>> mybatis-flex success generate tables class: \n" + genJavaFile.toURI());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }


    private void printMessage(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, message);
        System.out.println(message);
    }

    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>();
        supportedAnnotationTypes.add(Table.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    private boolean isFromTestSource(String path) {
        return path.contains("test-sources") || path.contains("test-annotations");
    }


    public static boolean isAbsolutePath(String path) {
        return path != null && (path.startsWith("/") || path.indexOf(":") > 0);
    }

    public static String camelToUnderline(String string) {
        if (string == null || string.trim().length() == 0) {
            return "";
        }
        int len = string.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = string.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }


    /**
     * 获取项目的根目录，也就是根节点 pom.xml 所在的目录
     *
     * @return
     */
    private String getProjectRootPath(String genFilePath) {
        File file = new File(genFilePath);
        int count = 20;
        return getProjectRootPath(file, count);
    }


    private String getProjectRootPath(File file, int count) {
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

    /**
     * 当前 Maven 模块所在所在的目录
     *
     * @return
     */
    private String getModuleRootPath(String genFilePath) {
        File file = new File(genFilePath);
        int count = 20;
        return getModuleRootPath(file, count);
    }


    private String getModuleRootPath(File file, int count) {
        if (count <= 0) {
            return null;
        }
        if (file.isFile()) {
            return getModuleRootPath(file.getParentFile(), --count);
        } else {
            if (new File(file, "pom.xml").exists()) {
                return file.getAbsolutePath();
            } else {
                return getModuleRootPath(file.getParentFile(), --count);
            }
        }
    }
}
