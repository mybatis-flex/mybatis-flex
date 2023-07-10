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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.description.JavadocDescription;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaseMapperDocsGen {

    public static void main(String[] args) throws IOException {
        String mapperPath = System.getProperty("user.dir") + "/mybatis-flex-core/src/main/java/com/mybatisflex/core/BaseMapper.java";

        List<String> methodDescriptions = getMethodDescriptions(mapperPath);

        List<String> insertMethods = new LinkedList<>();
        List<String> deleteMethods = new LinkedList<>();
        List<String> updateMethods = new LinkedList<>();
        List<String> selectMethods = new LinkedList<>();
        List<String> relationMethods = new LinkedList<>();
        List<String> paginateMethods = new LinkedList<>();

        methodDescriptions.stream()
            .map(e -> e.replace("<br>", ""))
            .map(e -> e.replace("\r\n", ""))
            .map(e -> e.replaceFirst("\\{@code ", "`"))
            .map(e -> e.replaceFirst("}", "`"))
            .map(e -> e.replace("`}", "}`"))
            .forEach(methodDescription -> {
                if (methodDescription.contains("insert")) {
                    insertMethods.add(methodDescription);
                }
                if (methodDescription.contains("delete")) {
                    deleteMethods.add(methodDescription);
                }
                if (methodDescription.contains("update")) {
                    updateMethods.add(methodDescription);
                }
                if (methodDescription.contains("select")) {
                    selectMethods.add(methodDescription);
                }
                if (methodDescription.contains("Relation")) {
                    relationMethods.add(methodDescription);
                }
                if (methodDescription.contains("paginate")) {
                    paginateMethods.add(methodDescription);
                }
            });

        String mdDir = System.getProperty("user.dir") + "/docs/zh/base/parts/";

        writeString(new File(mdDir, "base-mapper-insert-methods.md"), insertMethods);
        writeString(new File(mdDir, "base-mapper-delete-methods.md"), deleteMethods);
        writeString(new File(mdDir, "base-mapper-update-methods.md"), updateMethods);
        writeString(new File(mdDir, "base-mapper-query-methods.md"), selectMethods);
        writeString(new File(mdDir, "base-mapper-relation-methods.md"), relationMethods);
        writeString(new File(mdDir, "base-mapper-paginate-methods.md"), paginateMethods);
    }

    public static void writeString(File file, List<String> contents) throws IOException {
        try (FileWriter fw = new FileWriter(file, false);
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (String content : contents) {
                bw.write(content);
                bw.newLine();
            }
        }
    }

    public static List<String> getMethodDescriptions(String mapperPath) throws IOException {
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_8);
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> parseResult = javaParser.parse(Paths.get(mapperPath));
        Optional<CompilationUnit> compilationUnitOpt = parseResult.getResult();
        List<String> methodDescriptions = new LinkedList<>();
        if (compilationUnitOpt.isPresent()) {
            CompilationUnit compilationUnit = compilationUnitOpt.get();
            List<MethodDeclaration> methodDeclarations = compilationUnit.stream()
                .filter(e -> e instanceof MethodDeclaration)
                .map(e -> (MethodDeclaration) e)
                .collect(Collectors.toList());
            for (MethodDeclaration methodDeclaration : methodDeclarations) {
                String methodName = methodDeclaration.getNameAsString();
                String params = methodDeclaration.getParameters()
                    .stream()
                    .map(NodeWithSimpleName::getNameAsString)
                    .collect(Collectors.joining(", ", "(", ")"));
                String comment = methodDeclaration.getJavadocComment()
                    .map(JavadocComment::parse)
                    .map(Javadoc::getDescription)
                    .map(JavadocDescription::toText)
                    .orElse("");
                methodDescriptions.add("- **`" + methodName + params + "`**：" + comment);
            }
        }
        return methodDescriptions;
    }

}
