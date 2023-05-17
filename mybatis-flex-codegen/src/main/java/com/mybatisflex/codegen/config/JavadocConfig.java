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
    private Function<String, String> tableRemarkFormat = Function.identity();

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
        return tableRemarkFormat.apply(comment);
    }

    public Function<String, String> getTableRemarkFormat() {
        return tableRemarkFormat;
    }

    public JavadocConfig setTableRemarkFormat(Function<String, String> tableRemarkFormat) {
        this.tableRemarkFormat = tableRemarkFormat;
        return this;
    }

}