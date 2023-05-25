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


import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import org.apache.ibatis.type.UnknownTypeHandler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
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

public class QueryEntityProcessor extends AbstractProcessor {

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
            byte[].class.getName(), Byte[].class.getName(), Byte.class.getName(),
            BigInteger.class.getName(), BigDecimal.class.getName(),
            char.class.getName(), String.class.getName(), Character.class.getName()
    );

    private static final String mapperTemplate = "package @package;\n" +
            "\n" +
            "import @baseMapperClass;\n" +
            "import @entityClass;\n" +
            "\n" +
            "public interface @entityNameMapper extends @baseMapperClzName<@entityName> {\n" +
            "}\n";


    private static final String classTableTemplate = "package @package;\n" +
            "\n" +
            "import com.mybatisflex.core.query.QueryColumn;\n" +
            "import com.mybatisflex.core.table.TableDef;\n" +
            "\n" +
            "// Auto generate by mybatis-flex, do not modify it.\n" +
            "public class @tablesClassName {\n" +
            "@classesInfo" +
            "}\n";

    private static final String tableDefTemplate = "\n\n    public static final @entityClassTableDef @tableField = new @entityClassTableDef(\"@tableName\");\n";

    private static final String singleEntityClassTemplate = "package @package;\n" +
            "\n" +
            "import com.mybatisflex.core.query.QueryColumn;\n" +
            "import com.mybatisflex.core.table.TableDef;\n" +
            "\n" +
            "// Auto generate by mybatis-flex, do not modify it.\n" +
            "public class @entityClassTableDef extends TableDef {\n" +
            "\n" +
            "@selfDef" +
            "@queryColumns" +
            "@defaultColumns" +
            "@allColumns" +
            "\n" +
            "    public @entityClassTableDef(String tableName) {\n" +
            "        super(tableName);\n" +
            "    }\n" +
            "}\n";


    private static final String allInTableEntityClassTemplate = "\n" +
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
    private static final String allColumnsTemplate = "        public QueryColumn ALL_COLUMNS = new QueryColumn(this, \"*\");\n";

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
            System.out.println("mybatis flex processor run start...");
            MyBatisFlexProps props = new MyBatisFlexProps(filer);

            String enable = props.getProperties().getProperty("processor.enable", "");
            if ("false".equalsIgnoreCase(enable)) {
                return true;
            }

            String genPath = props.getProperties().getProperty("processor.genPath", "");
            String genTablesPackage = props.getProperties().getProperty("processor.tablesPackage");
            String baseMapperClass = props.getProperties().getProperty("processor.baseMapperClass", "com.mybatisflex.core.BaseMapper");
            String mappersGenerateEnable = props.getProperties().getProperty("processor.mappersGenerateEnable", "false");
            String genMappersPackage = props.getProperties().getProperty("processor.mappersPackage");

            boolean allInTables = "true".equalsIgnoreCase(props.getProperties().getProperty("processor.allInTables", "false"));
            String className = props.getProperties().getProperty("processor.tablesClassName", "Tables");

            //upperCase, lowerCase, upperCamelCase, lowerCamelCase
            String tablesNameStyle = props.getProperties().getProperty("processor.tablesNameStyle", "upperCase");

            String[] entityIgnoreSuffixes = props.getProperties().getProperty("processor.entity.ignoreSuffixes", "").split(",");


            StringBuilder guessPackage = new StringBuilder();

            StringBuilder tablesContent = new StringBuilder();
            roundEnv.getElementsAnnotatedWith(Table.class).forEach((Consumer<Element>) entityClassElement -> {

                Table table = entityClassElement.getAnnotation(Table.class);

                //init genPackage
                if ((genTablesPackage == null || genTablesPackage.trim().length() == 0)
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
                do {
                    fillPropertyAndColumns(propertyAndColumns, defaultColumns, classElement, (table == null || table.camelToUnderline()));
                    classElement = (TypeElement) typeUtils.asElement(classElement.getSuperclass());
                } while (classElement != null);


                String entitySimpleName = entityClassElement.getSimpleName().toString();
                if (entityIgnoreSuffixes.length > 0) {
                    for (String entityIgnoreSuffix : entityIgnoreSuffixes) {
                        if (entitySimpleName.endsWith(entityIgnoreSuffix.trim())) {
                            entitySimpleName = entitySimpleName.substring(0, entitySimpleName.length() - entityIgnoreSuffix.length());
                            break;
                        }
                    }
                }

                if (allInTables) {
                    String content = buildTablesClass(entitySimpleName, tableName, propertyAndColumns, defaultColumns, tablesNameStyle, null, allInTables);
                    tablesContent.append(content);
                }
                //每一个 entity 生成一个独立的文件
                else {
                    String realGenPackage = genTablesPackage == null || genTablesPackage.trim().length() == 0 ? guessPackage.toString() : genTablesPackage;
                    String content = buildTablesClass(entitySimpleName, tableName, propertyAndColumns, defaultColumns, tablesNameStyle, realGenPackage, allInTables);
                    genClass(genPath, realGenPackage, entitySimpleName + "TableDef", content);
                }

                //是否开启 mapper 生成功能
                if ("true".equalsIgnoreCase(mappersGenerateEnable) && table.mapperGenerateEnable()) {
                    String realMapperPackage = genMappersPackage == null || genMappersPackage.trim().length() == 0
                            ? guessMapperPackage(entityClassElement.toString()) : genMappersPackage;
                    genMapperClass(genPath, realMapperPackage, entityClassElement.toString(), baseMapperClass, entitySimpleName);
                }
            });

            if (allInTables && tablesContent.length() > 0) {
                String realGenPackage = genTablesPackage == null || genTablesPackage.trim().length() == 0 ? guessPackage.toString() : genTablesPackage;
                genTablesClass(genPath, realGenPackage, className, tablesContent.toString());
            }

        }

        return false;
    }


    private void fillPropertyAndColumns(Map<String, String> propertyAndColumns, List<String> defaultColumns, TypeElement classElement, boolean camelToUnderline) {
        for (Element fieldElement : classElement.getEnclosedElements()) {

            //all fields
            if (ElementKind.FIELD == fieldElement.getKind()) {


                Set<Modifier> modifiers = fieldElement.getModifiers();
                if (modifiers.contains(Modifier.STATIC)) {
                    //ignore static fields
                    continue;
                }

                Column column = fieldElement.getAnnotation(Column.class);
                if (column != null && column.ignore()) {
                    continue;
                }


                //获取 typeHandlerClass 的名称，通过 column.typeHandler() 获取会抛出异常：MirroredTypeException:
                //参考 https://stackoverflow.com/questions/7687829/java-6-annotation-processing-getting-a-class-from-an-annotation
                final String[] typeHandlerClass = {""};
                List<? extends AnnotationMirror> annotationMirrors = fieldElement.getAnnotationMirrors();
                for (AnnotationMirror annotationMirror : annotationMirrors) {
                    annotationMirror.getElementValues().forEach((BiConsumer<ExecutableElement, AnnotationValue>) (executableElement, annotationValue) -> {
                        if (executableElement.getSimpleName().equals("typeHandler")) {
                            typeHandlerClass[0] = annotationValue.toString();
                        }
                    });
                }

                TypeMirror typeMirror = fieldElement.asType();
                Element element = typeUtils.asElement(typeMirror);
                if (element != null) {
                    typeMirror = element.asType();
                }

                String typeString = typeMirror.toString().trim();

                TypeElement typeElement = null;
                if (typeMirror.getKind() == TypeKind.DECLARED) {
                    typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
                }

                //未配置 typeHandler 的情况下，只支持基本数据类型，不支持比如 list set 或者自定义的类等
                if ((column == null || typeHandlerClass[0].equals(UnknownTypeHandler.class.getName()))
                        && !defaultSupportColumnTypes.contains(typeString)
                        && (typeElement != null && ElementKind.ENUM != typeElement.getKind())
                ) {
                    continue;
                }


                String columnName = column != null && column.value().trim().length() > 0 ? column.value() :
                        (camelToUnderline ? camelToUnderline(fieldElement.toString()) : fieldElement.toString());
                propertyAndColumns.put(fieldElement.toString(), columnName);

                if (column == null || (!column.isLarge() && !column.isLogicDelete())) {
                    defaultColumns.add(columnName);
                }
            }
        }
    }


    private static String guessMapperPackage(String entityClassName) {
        if (!entityClassName.contains(".")) {
            return "mapper";
        } else {
            String entityPackage = entityClassName.substring(0, entityClassName.lastIndexOf("."));
            if (entityPackage.contains(".")) {
                return entityPackage.substring(0, entityPackage.lastIndexOf(".")) + ".mapper";
            } else {
                return "mapper";
            }
        }
    }


    //upperCase, lowerCase, upperCamelCase, lowerCamelCase
    private static String buildName(String name, String style) {
        if ("upperCase".equalsIgnoreCase(style)) {
            return camelToUnderline(name).toUpperCase();
        } else if ("lowerCase".equalsIgnoreCase(style)) {
            return camelToUnderline(name).toLowerCase();
        } else if ("upperCamelCase".equalsIgnoreCase(style)) {
            return firstCharToUpperCase(name);
        }
        //lowerCamelCase
        else {
            return firstCharToLowerCase(name);
        }
    }


    private String buildTablesClass(String entityClass, String tableName, Map<String, String> propertyAndColumns
            , List<String> defaultColumns, String tablesNameStyle, String realGenPackage, boolean allInTables) {

        // tableDefTemplate = "    public static final @entityClassTableDef @tableField = new @entityClassTableDef(\"@tableName\");\n";

        String tableDef = tableDefTemplate.replace("@entityClass", entityClass)
                .replace("@tableField", buildName(entityClass, tablesNameStyle))
                .replace("@tableName", tableName);


        //columnsTemplate = "        public QueryColumn @property = new QueryColumn(this, \"@columnName\");\n";
        StringBuilder queryColumns = new StringBuilder();
        propertyAndColumns.forEach((property, column) ->
                queryColumns.append(columnsTemplate.substring(allInTables ? 0 : 4) //移除 4 个空格
                        .replace("@property", buildName(property, tablesNameStyle))
                        .replace("@columnName", column)
                ));


//        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{@allColumns};
        StringJoiner allColumns = new StringJoiner(", ");
        propertyAndColumns.forEach((property, column) -> allColumns.add(buildName(property, tablesNameStyle)));

//        String allColumnsString = allColumnsTemplate.replace("@allColumns", allColumns.toString())
//                .replace("ALL_COLUMNS", buildName("allColumns", tablesNameStyle));

        String allColumnsString = allColumnsTemplate;


        StringJoiner defaultColumnStringJoiner = new StringJoiner(", ");
        propertyAndColumns.forEach((property, column) -> {
            if (defaultColumns.contains(column)) {
                defaultColumnStringJoiner.add(buildName(property, tablesNameStyle));
            }
        });
        String defaultColumnsString = defaultColumnsTemplate.replace("@allColumns", defaultColumnStringJoiner.toString())
                .replace("DEFAULT_COLUMNS", buildName("defaultColumns", tablesNameStyle));


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

        String tableClass;
        if (allInTables) {
            tableClass = allInTableEntityClassTemplate.replace("@entityClass", entityClass)
                    .replace("@queryColumns", queryColumns)
                    .replace("@defaultColumns", defaultColumnsString)
                    .replace("@allColumns", allColumnsString);
        } else {
            tableClass = singleEntityClassTemplate
                    .replace("@package", realGenPackage)
                    .replace("@entityClass", entityClass)
                    .replace("@selfDef", tableDef.replace("\n\n", "") + "\n")
                    .replace("@queryColumns", queryColumns)
                    .replace("@defaultColumns", "\n" + defaultColumnsString.substring(5)) //移除 "换行 + 4 个空格"
                    .replace("@allColumns", allColumnsString.substring(4)); //移除 4 个空格
        }


        return allInTables ? tableDef + tableClass : tableClass;
    }


    private void genTablesClass(String genBasePath, String genPackageName, String className, String classContent) {
        String genContent = classTableTemplate.replace("@package", genPackageName)
                .replace("@classesInfo", classContent)
                .replace("@tablesClassName", className);

        genClass(genBasePath, genPackageName, className, genContent);
    }


    private void genClass(String genBasePath, String genPackageName, String className, String genContent) {
        Writer writer = null;
        try {
            JavaFileObject sourceFile = filer.createSourceFile(genPackageName + "." + className);
            if (genBasePath == null || genBasePath.trim().length() == 0) {
                writer = sourceFile.openWriter();
                writer.write(genContent);
                writer.flush();
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
                System.out.println(">>>>>ERROR: can not mkdirs by mybatis-flex processor for: " + genJavaFile.getParentFile());
                return;
            }

            writer = new PrintWriter(new FileOutputStream(genJavaFile));
            writer.write(genContent);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * @param genBasePath     生成路径
     * @param genPackageName  包名
     * @param entityClass     实体类名
     * @param baseMapperClass 自定义Mapper的父类全路径和类名 com.xx.mapper.BaseMapper，可通过mybatis-flex.properties 的属性processor.baseMapperClass配置， 默认为 com.mybatisflex.core.BaseMapper
     */
    private void genMapperClass(String genBasePath, String genPackageName, String entityClass, String baseMapperClass, String entityName) {
        entityName = entityClass.substring(entityClass.lastIndexOf(".") + 1);
        String baseMapperClzName = baseMapperClass.substring(baseMapperClass.lastIndexOf(".") + 1);
        String genContent = mapperTemplate
                .replace("@package", genPackageName)
                .replace("@entityClass", entityClass)
                .replace("@entityName", entityName)
                .replace("@baseMapperClass", baseMapperClass)
                .replace("@baseMapperClzName", baseMapperClzName);

        String mapperClassName = entityName + "Mapper";
        genClass(genBasePath, genPackageName, mapperClassName, genContent);
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

    public static String firstCharToUpperCase(String string) {
        char firstChar = string.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = string.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return string;
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
