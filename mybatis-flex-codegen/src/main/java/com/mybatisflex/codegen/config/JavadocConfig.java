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

import com.mybatisflex.core.util.StringUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * 注释配置类。
 *
 * @author 王帅
 * @since 2023-05-17
 */
@SuppressWarnings("unused")
public class JavadocConfig implements Serializable {

    private static final long serialVersionUID = -4280345489968397327L;
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
    private UnaryOperator<String> tableCommentFormat = UnaryOperator.identity();

    /**
     * 列名格式化。
     */
    private UnaryOperator<String> columnCommentFormat = UnaryOperator.identity();

    /**
     * Entity 包注释。
     */
    private String entityPackage = "实体类层（Entity）软件包。";

    /**
     * Mapper 包注释。
     */
    private String mapperPackage = "映射层（Mapper）软件包。";

    /**
     * Service 包注释。
     */
    private String servicePackage = "服务层（Service）软件包。";

    /**
     * ServiceImpl 包注释。
     */
    private String serviceImplPackage = "服务层实现（ServiceImpl）软件包。";

    /**
     * Controller 包注释。
     */
    private String controllerPackage = "控制层（Controller）软件包。";

    /**
     * TableDef 包注释。
     */
    private String tableDefPackage = "表定义层（TableDef）软件包。";

    /**
     * 获取作者。
     */
    public String getAuthor() {
        return author;
    }

    /**
     * 设置作者。
     */
    public JavadocConfig setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * 获取自。
     */
    public String getSince() {
        return since.get();
    }

    /**
     * 设置自。
     */
    public JavadocConfig setSince(String since) {
        this.since = () -> since;
        return this;
    }

    /**
     * 设置自。
     */
    public JavadocConfig setSince(Supplier<String> since) {
        this.since = since;
        return this;
    }

    public String formatTableComment(String comment) {
        if (StringUtil.isBlank(comment)) {
            return "";
        }
        return tableCommentFormat.apply(comment);
    }

    /**
     * 获取表注释格式化。
     */
    public Function<String, String> getTableCommentFormat() {
        return tableCommentFormat;
    }

    /**
     * 设置表注释格式化方案，用来生成实体类注释。
     */
    public JavadocConfig setTableCommentFormat(UnaryOperator<String> tableCommentFormat) {
        this.tableCommentFormat = tableCommentFormat;
        return this;
    }

    public String formatColumnComment(String comment) {
        if (StringUtil.isBlank(comment)) {
            return "";
        }
        return columnCommentFormat.apply(comment);
    }

    /**
     * 获取列注释格式化。
     */
    public Function<String, String> getColumnCommentFormat() {
        return columnCommentFormat;
    }

    /**
     * 设置列注释格式化方案，用来生成实体类属性注释。
     */
    public JavadocConfig setColumnCommentFormat(UnaryOperator<String> columnCommentFormat) {
        this.columnCommentFormat = columnCommentFormat;
        return this;
    }

    /**
     * 获取实体类层包注释。
     */
    public String getEntityPackage() {
        return entityPackage;
    }

    /**
     * 设置实体类层包注释。
     */
    public JavadocConfig setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
        return this;
    }

    /**
     * 获取映射层包注释。
     */
    public String getMapperPackage() {
        return mapperPackage;
    }

    /**
     * 设置映射层包注释。
     */
    public JavadocConfig setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
        return this;
    }

    /**
     * 获取服务层包注释。
     */
    public String getServicePackage() {
        return servicePackage;
    }

    /**
     * 设置服务层包注释。
     */
    public JavadocConfig setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
        return this;
    }

    /**
     * 获取服务层实现包注释。
     *
     * @return {@link String}
     */
    public String getServiceImplPackage() {
        return serviceImplPackage;
    }

    /**
     * 设置服务层实现包注释。
     */
    public JavadocConfig setServiceImplPackage(String serviceImplPackage) {
        this.serviceImplPackage = serviceImplPackage;
        return this;
    }

    /**
     * 获取控制层包注释。
     */
    public String getControllerPackage() {
        return controllerPackage;
    }

    /**
     * 设置控制层包注释。
     */
    public JavadocConfig setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
        return this;
    }

    /**
     * 获取表定义层包注释。
     */
    public String getTableDefPackage() {
        return tableDefPackage;
    }

    /**
     * 设置表定义层包注释。
     */
    public JavadocConfig setTableDefPackage(String tableDefPackage) {
        this.tableDefPackage = tableDefPackage;
        return this;
    }

}
