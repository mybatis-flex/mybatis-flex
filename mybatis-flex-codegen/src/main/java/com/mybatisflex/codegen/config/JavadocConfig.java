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

package com.mybatisflex.codegen.config;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 注释配置类。
 *
 * @author 王帅
 * @since 2023-05-17
 */
@SuppressWarnings("unused")
public class JavadocConfig {

    /**
     * 作者。
     */
    private String author = System.getProperty("user.name");

    /**
     * 自。
     */
    private Supplier<String> since = () -> DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());

    /**
     * 表名格式化。
     */
    private Function<String, String> tableCommentFormat = Function.identity();

    public String getAuthor() {
        return author;
    }

    public JavadocConfig setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getSince() {
        return since.get();
    }

    public JavadocConfig setSince(String since) {
        this.since = () -> since;
        return this;
    }

    public JavadocConfig setSince(Supplier<String> since) {
        this.since = since;
        return this;
    }

    public String formatTableComment(String comment) {
        return tableCommentFormat.apply(comment);
    }

    public Function<String, String> getTableCommentFormat() {
        return tableCommentFormat;
    }

    public JavadocConfig setTableCommentFormat(Function<String, String> tableCommentFormat) {
        this.tableCommentFormat = tableCommentFormat;
        return this;
    }

}