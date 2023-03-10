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
import org.apache.ibatis.type.UnknownTypeHandler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class QueryEntityProcesser extends AbstractProcessor {

    private static final List<String> defaultSupportColumnTypes = Arrays.asList(
            int.class.getName(), Integer.class.getName(),
            short.class.getName(), Short.class.getName(),
            long.class.getName(), Long.class.getName(),
            float.class.getName(), Float.class.getName(),
            double.class.getName(), Double.class.getName(),
            boolean.class.getName(), Boolean.class.getName(),
            Date.class.getName(), java.sql.Date.class.getName(), Time.class.getName(), Timestamp.class.getName(),
            Instant.class.getName(), LocalDate.class.getName(), LocalDateTime.class.getName(), LocalTime.class.getName(),
            OffsetDateTime.class.getName(), OffsetTime.class.getName(), ZonedDateTime.class.getName(),
            Year.class.getName(), Month.class.getName(), YearMonth.class.getName(), JapaneseDate.class.getName(),
            byte[].class.getName(), Byte[].class.getName(),
            BigInteger.class.getName(), BigDecimal.class.getName(),
            char.class.getName(), String.class.getName(), Character.class.getName()
    );

    private static final String classTableTemplate = "package @package;\n" +
            "\n" +
            "import com.mybatisflex.core.query.QueryColumn;\n" +
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
            "@defaultColumns" +
            "@allColumns" +
            "\n" +
            "        public @entityClassTableDef(String tableName) {\n" +
            "            super(tableName);\n" +
            "        }\n" +
            "    }\n";


    private static final String columnsTemplate = "        public QueryColumn @property = new QueryColumn(this, \"@columnName\");\n";

    private static final String defaultColumnsTemplate = "\n        public QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{@allColumns};\n";
    private static final String allColumnsTemplate = "        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{@allColumns};\n\n";

    private Filer filer;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.filer = processingEnvironment.getFiler();
        this.elementUtils = processingEnvironment.getElementUtils();
        this.typeUtils = processingEnvironment.getTypeUtils();
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
                List<String> defaultColumns = new ArrayList<>();

                TypeElement classElement = (TypeElement) entityClassElement;
                for (Element fieldElement : classElement.getEnclosedElements()) {

                    //all fields
                    if (ElementKind.FIELD == fieldElement.getKind()) {

                        TypeMirror typeMirror = fieldElement.asType();

                        Column column = fieldElement.getAnnotation(Column.class);
                        if (column != null && column.ignore()) {
                            continue;
                        }

                        //?????? typeHandlerClass ?????????????????? column.typeHandler() ????????????????????????MirroredTypeException:
                        //?????? https://stackoverflow.com/questions/7687829/java-6-annotation-processing-getting-a-class-from-an-annotation
                        final String[] typeHandlerClass = {""};
                        List<? extends AnnotationMirror> annotationMirrors = fieldElement.getAnnotationMirrors();
                        for (AnnotationMirror annotationMirror : annotationMirrors) {
                            annotationMirror.getElementValues().forEach((BiConsumer<ExecutableElement, AnnotationValue>) (executableElement, annotationValue) -> {
                                if (executableElement.getSimpleName().equals("typeHandler")){
                                    typeHandlerClass[0] = annotationValue.toString();
                                }
                            });
                        }

                        //????????? typeHandler ???????????????????????????????????????????????????????????? list set ????????????????????????
                        if ((column == null || typeHandlerClass[0].equals(UnknownTypeHandler.class.getName()))
                                && !defaultSupportColumnTypes.contains(typeMirror.toString())) {
                            continue;
                        }


                        String columnName = column != null && column.value().trim().length() > 0 ? column.value() : camelToUnderline(fieldElement.toString());
                        propertyAndColumns.put(fieldElement.toString(), columnName);

                        if (column == null || (!column.isLarge() && !column.isLogicDelete())) {
                            defaultColumns.add(columnName);
                        }
                    }
                }

                String entityClassName = entityClassElement.getSimpleName().toString();
                tablesContent.append(buildClass(entityClassName, tableName, propertyAndColumns, defaultColumns));
            });

            if (tablesContent.length() > 0) {
                String realGenPackage = genPackage == null || genPackage.trim().length() == 0 ? guessPackage.toString() : genPackage;
                genClass(genPath, realGenPackage, className, tablesContent.toString());
            }

        }

        return false;
    }





    private String buildClass(String entityClass, String tableName, Map<String, String> propertyAndColumns, List<String> defaultColumns) {

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


        StringJoiner defaultColumnStringJoiner = new StringJoiner(", ");
        defaultColumns.forEach(s -> defaultColumnStringJoiner.add(camelToUnderline(s).toUpperCase()));
        String defaultColumnsString = defaultColumnsTemplate.replace("@allColumns", defaultColumnStringJoiner.toString());


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
                .replace("@defaultColumns", defaultColumnsString)
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

            //??????????????????????????????
            String realPath;

            //????????????????????????????????????
            if (isAbsolutePath(genBasePath)) {
                realPath = genBasePath;
            }
            //?????????????????????????????????????????????????????????????????????
            else {
                String projectRootPath = getProjectRootPath(defaultGenPath);
                realPath = new File(projectRootPath, genBasePath).getAbsolutePath();
            }

            //????????? test/java ??????????????????????????????
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
     * ????????????????????????????????????????????? pom.xml ???????????????
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
     * ?????? Maven ???????????????????????????
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
