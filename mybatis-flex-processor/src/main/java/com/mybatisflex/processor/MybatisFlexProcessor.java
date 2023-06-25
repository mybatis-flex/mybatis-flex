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

package com.mybatisflex.processor;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.processor.builder.ContentBuilder;
import com.mybatisflex.processor.config.ConfigurationKey;
import com.mybatisflex.processor.config.MybatisFlexConfig;
import com.mybatisflex.processor.util.FileUtil;
import com.mybatisflex.processor.util.StrUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * MyBatis Flex Processor.
 *
 * @author 王帅
 * @since 2023-06-22
 */
public class MybatisFlexProcessor extends AbstractProcessor {

    private static final List<String> DEFAULT_SUPPORT_COLUMN_TYPES = Arrays.asList(
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

    private Filer filer;
    private Types typeUtils;
    private MybatisFlexConfig configuration;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.filer = processingEnvironment.getFiler();
        this.typeUtils = processingEnvironment.getTypeUtils();
        this.configuration = new MybatisFlexConfig(filer);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            System.out.println("mybatis flex processor run start...");

            // 不启用 APT 功能
            if ("false".equalsIgnoreCase(configuration.get(ConfigurationKey.ENABLE))) {
                return true;
            }

            // 是否所有的类常量都生成在 Tables 类里
            boolean allInTables = "true".equalsIgnoreCase(configuration.get(ConfigurationKey.ALL_IN_TABLES));

            StringBuilder importBuilder;
            StringBuilder fieldBuilder;

            if (allInTables) {
                importBuilder = new StringBuilder();
                fieldBuilder = new StringBuilder();
            } else {
                fieldBuilder = null;
                importBuilder = null;
            }

            // 其他配置选项
            String genPath = configuration.get(ConfigurationKey.GEN_PATH);
            String tablesPackage = configuration.get(ConfigurationKey.TABLES_PACKAGE);
            String mappersPackage = configuration.get(ConfigurationKey.MAPPERS_PACKAGE);
            String baseMapperClass = configuration.get(ConfigurationKey.BASE_MAPPER_CLASS);
            String tablesDefSuffix = configuration.get(ConfigurationKey.TABLES_DEF_SUFFIX);
            String tablesNameStyle = configuration.get(ConfigurationKey.TABLE_NAME_STYLE);
            String tablesClassName = configuration.get(ConfigurationKey.TABLES_CLASS_NAME);
            String mappersGenerateEnable = configuration.get(ConfigurationKey.MAPPERS_GENERATE_ENABLE);

            String[] entityIgnoreSuffixes = configuration.get(ConfigurationKey.IGNORE_SUFFIXES).split(",");

            // 如果不指定 Tables 生成包，那么 Tables 文件就会和最后一个 entity 文件在同一个包
            String entityClassReference = null;

            // 获取需要生成的类，开始构建文件
            Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Table.class);

            int size = elementsAnnotatedWith.size();
            int index = 0;

            for (Element entityClassElement : elementsAnnotatedWith) {

                index++;

                // 获取 Table 注解
                Table table = entityClassElement.getAnnotation(Table.class);

                assert table != null;

                // 类属性
                Map<String, String> propertyAndColumns = new LinkedHashMap<>();
                // 默认查询的属性，非 isLarge 字段
                List<String> defaultColumns = new ArrayList<>();

                TypeElement classElement = (TypeElement) entityClassElement;
                do {
                    // 获取类属性和默认查询字段
                    fillPropertyAndColumns(propertyAndColumns, defaultColumns, classElement, table.camelToUnderline());
                    classElement = (TypeElement) typeUtils.asElement(classElement.getSuperclass());
                } while (classElement != null);

                // 获取 entity 类名
                String entityClass = entityClassElement.toString();
                String entityClassName = StrUtil.getClassName(entityClass);

                // 处理 entity 后缀
                for (String entityIgnoreSuffix : entityIgnoreSuffixes) {
                    if (entityClassName.endsWith(entityIgnoreSuffix.trim())) {
                        entityClassName = entityClassName.substring(0, entityClassName.length() - entityIgnoreSuffix.length());
                        break;
                    }
                }

                // 生成 TableDef 文件
                String tableDefPackage = StrUtil.buildTableDefPackage(entityClass);
                String tableDefClassName = entityClassName.concat("TableDef");
                String tableDefContent = ContentBuilder.buildTableDef(table, entityClass, entityClassName, allInTables, tableDefPackage, tableDefClassName, tablesNameStyle, tablesDefSuffix, propertyAndColumns, defaultColumns);
                processGenClass(genPath, tableDefPackage, tableDefClassName, tableDefContent);

                if (allInTables) {
                    // 标记 entity 类，如果没有配置 Tables 生成位置，以 entity 位置为准
                    entityClassReference = entityClass;
                    // 构建 Tables 常量属性及其导包
                    ContentBuilder.buildTablesField(importBuilder, fieldBuilder, table, entityClass, entityClassName, tablesNameStyle, tablesDefSuffix);
                }

                // 是否生成 Mapper 文件
                if ("true".equalsIgnoreCase(mappersGenerateEnable) && table.mapperGenerateEnable()) {
                    String realMapperPackage = StrUtil.isBlank(mappersPackage) ? StrUtil.buildMapperPackage(entityClass) : mappersPackage;
                    String mapperClassName = entityClassName.concat("Mapper");
                    String mapperClassContent = ContentBuilder.buildMapper(entityClass, entityClassName, realMapperPackage, mapperClassName, baseMapperClass);
                    processGenClass(genPath, realMapperPackage, mapperClassName, mapperClassContent);
                }

                // handle NPE, ensure TableDef already generate.
                if (index == size && allInTables) {
                    // 生成 Tables 文件
                    String realTablesPackage = StrUtil.isBlank(tablesPackage) ? StrUtil.buildTableDefPackage(entityClassReference) : tablesPackage;
                    String realTablesClassName = StrUtil.isBlank(tablesClassName) ? "Tables" : tablesClassName;
                    String tablesContent = ContentBuilder.buildTables(importBuilder, fieldBuilder, realTablesPackage, tablesClassName);
                    processGenClass(genPath, realTablesPackage, realTablesClassName, tablesContent);
                }
            }
        }
        return false;
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

    private void fillPropertyAndColumns(Map<String, String> propertyAndColumns, List<String> defaultColumns, TypeElement classElement, boolean camelToUnderline) {
        for (Element fieldElement : classElement.getEnclosedElements()) {

            // all fields
            if (ElementKind.FIELD == fieldElement.getKind()) {

                Set<Modifier> modifiers = fieldElement.getModifiers();
                if (modifiers.contains(Modifier.STATIC)) {
                    // ignore static fields
                    continue;
                }

                Column column = fieldElement.getAnnotation(Column.class);
                if (column != null && column.ignore()) {
                    continue;
                }


                // 获取 typeHandlerClass 的名称，通过 column.typeHandler() 获取会抛出异常：MirroredTypeException:
                // 参考 https://stackoverflow.com/questions/7687829/java-6-annotation-processing-getting-a-class-from-an-annotation
                final String[] typeHandlerClass = {""};
                List<? extends AnnotationMirror> annotationMirrors = fieldElement.getAnnotationMirrors();
                for (AnnotationMirror annotationMirror : annotationMirrors) {
                    annotationMirror.getElementValues().forEach((BiConsumer<ExecutableElement, AnnotationValue>) (executableElement, annotationValue) -> {
                        if ("typeHandler".contentEquals(executableElement.getSimpleName())) {
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

                // 未配置 typeHandler 的情况下，只支持基本数据类型，不支持比如 list set 或者自定义的类等
                if ((column == null || "org.apache.ibatis.type.UnknownTypeHandler".equals(typeHandlerClass[0]))
                        && !DEFAULT_SUPPORT_COLUMN_TYPES.contains(typeString)
                        && (typeElement != null && ElementKind.ENUM != typeElement.getKind())
                ) {
                    continue;
                }


                String columnName;
                if (column != null && !StrUtil.isBlank(column.value())) {
                    columnName = column.value();
                } else {
                    if (camelToUnderline) {
                        columnName = StrUtil.camelToUnderline(fieldElement.toString());
                    } else {
                        columnName = fieldElement.toString();
                    }
                }

                propertyAndColumns.put(fieldElement.toString(), columnName);

                if (column == null || (!column.isLarge() && !column.isLogicDelete())) {
                    defaultColumns.add(columnName);
                }
            }
        }
    }

    private void processGenClass(String genBasePath, String genPackageName, String className, String genContent) {
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

            // 真实的生成代码的目录
            String realPath;

            if (FileUtil.isAbsolutePath(genBasePath)) {
                // 用户配置的路径为绝对路径
                realPath = genBasePath;
            } else {
                // 配置的是相对路径，那么则以项目根目录为相对路径
                String projectRootPath = FileUtil.getProjectRootPath(defaultGenPath);
                realPath = new File(projectRootPath, genBasePath).getAbsolutePath();
            }

            // 通过在 test/java 目录下执行编译生成的
            boolean fromTestSource = FileUtil.isFromTestSource(defaultGenPath);
            if (fromTestSource) {
                realPath = new File(realPath, "src/test/java").getAbsolutePath();
            } else {
                realPath = new File(realPath, "src/main/java").getAbsolutePath();
            }

            File genJavaFile = new File(realPath, (genPackageName + "." + className).replace(".", "/") + ".java");
            if (!genJavaFile.getParentFile().exists() && !genJavaFile.getParentFile().mkdirs()) {
                System.err.println(">>>>> ERROR: can not mkdirs by mybatis-flex processor for: " + genJavaFile.getParentFile());
                return;
            }

            writer = new PrintWriter(Files.newOutputStream(genJavaFile.toPath()));
            writer.write(genContent);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                    // do nothing here.
                }
            }
        }
    }

}