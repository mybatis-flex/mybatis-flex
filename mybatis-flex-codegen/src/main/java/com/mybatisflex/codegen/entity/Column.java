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
package com.mybatisflex.codegen.entity;

import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.EntityConfig;
import com.mybatisflex.core.mask.MaskManager;
import com.mybatisflex.core.mask.Masks;
import com.mybatisflex.core.util.StringUtil;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 数据库表里面的列信息。
 */
public class Column {

    /**
     * 字段名称。
     */
    private String name;

    /**
     * 属性名称。
     */
    private String property;

    /**
     * 属性类型。
     */
    private String propertyType;

    /**
     * 字段注释。
     */
    private String comment;

    /**
     * 是否可为空。
     */
    private Integer nullable;

    /**
     * 是否为主键。
     */
    private boolean isPrimaryKey = false;

    /**
     * 是否自增。
     */
    private boolean isAutoIncrement;

    /**
     * 数据库的字段类型，比如 varchar/tinyint 等
     */
    private String rawType;

    /**
     * 数据库中的字段长度，比如 varchar(32) 中的 32
     */
    private int rawLength;

    /**
     * 字段配置。
     */
    private ColumnConfig columnConfig;

    private EntityConfig entityConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.property = buildPropertyName();
    }

    public String getProperty() {
        return property;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public String getPropertyDefaultValue() {
        return columnConfig.getPropertyDefaultValue();
    }

    public String getPropertySimpleType() {
        if (columnConfig.getPropertyType() != null) {
            if (!columnConfig.getPropertyType().contains(".")) {
                return columnConfig.getPropertyType();
            }
            return StringUtil.substringAfterLast(columnConfig.getPropertyType(), ".");
        } else {
            return StringUtil.substringAfterLast(propertyType, ".");
        }
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getNullable() {
        return nullable;
    }

    public void setNullable(Integer nullable) {
        this.nullable = nullable;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public Boolean getAutoIncrement() {
        return isAutoIncrement;
    }

    public void setAutoIncrement(Boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public String getRawType() {
        return rawType;
    }

    public void setRawType(String rawType) {
        this.rawType = rawType;
    }

    public int getRawLength() {
        return rawLength;
    }

    public void setRawLength(int rawLength) {
        this.rawLength = rawLength;
    }

    public ColumnConfig getColumnConfig() {
        return columnConfig;
    }

    public void setColumnConfig(ColumnConfig columnConfig) {
        this.columnConfig = columnConfig;
    }

    public EntityConfig getEntityConfig() {
        return entityConfig;
    }

    public void setEntityConfig(EntityConfig entityConfig) {
        this.entityConfig = entityConfig;
    }

    public String getterMethod() {
        return "get" + StringUtil.firstCharToUpperCase(property);
    }

    public String setterMethod() {
        return "set" + StringUtil.firstCharToUpperCase(property);
    }

    public String buildComment() {
        if (StringUtil.isBlank(comment)) {
            return "";
        } else {
            return "/**\n" +
                "     * " + comment + "\n" +
                "     */";
        }
    }

    public String buildPropertyName() {
        String entityJavaFileName = name;
        return StringUtil.firstCharToLowerCase(StringUtil.underlineToCamel(entityJavaFileName));
    }

    /**
     * importClass为类的全限定名
     */
    private static void addImportClass(Set<String> importClasses, String importClass) {
        importClass = importClass.trim();

        // java.util.List<String> >>>>> java.util.List
        if (importClass.contains("<") && importClass.endsWith(">")) {
            importClass = importClass.substring(0, importClass.indexOf("<"));
        }

        // 不包含“.”则认为是原始类型，不需要import
        // lang 包不需要显式导入
        if (importClass.contains(".") && !importClass.startsWith("java.lang.")) {
            importClasses.add(importClass);
        }
    }

    private void addComma(StringBuilder annotations, boolean needComma) {
        if (needComma) {
            annotations.append(", ");
        }
    }

    public String buildAnnotations() {
        StringBuilder annotations = new StringBuilder();

        //@Id 的注解
        if (isPrimaryKey || columnConfig.isPrimaryKey()) {
            annotations.append("@Id(");

            boolean needComma = false;
            if (isAutoIncrement) {
                annotations.append("keyType = KeyType.Auto");
                needComma = true;
            } else if (columnConfig.getKeyType() != null) {
                annotations.append("keyType = KeyType.").append(columnConfig.getKeyType().name());
                needComma = true;
            }

            if (columnConfig.getKeyValue() != null) {
                addComma(annotations, needComma);
                annotations.append("value = \"").append(columnConfig.getKeyValue()).append("\"");
                needComma = true;
            }

            if (columnConfig.getKeyBefore() != null) {
                addComma(annotations, needComma);
                annotations.append("before = ").append(columnConfig.getKeyBefore());
                needComma = true;
            }


            if (entityConfig != null && entityConfig.isColumnCommentEnable() && StringUtil.isNotBlank(comment)) {
                addComma(annotations, needComma);
                annotations.append("comment = \"")
                    .append(this.comment.replace("\n", "")
                        .replace("\"", "\\\"")
                        .trim())
                    .append("\"");
            }

            if (annotations.length() == 4) {
                annotations.deleteCharAt(annotations.length() - 1);
            } else {
                annotations.append(")");
            }

            if (entityConfig != null && entityConfig.isAlwaysGenColumnAnnotation()) {
                annotations.append("\n    ");
            }
        }

        boolean needGenColumnAnnotation = (entityConfig != null && entityConfig.isAlwaysGenColumnAnnotation())
            || !name.equalsIgnoreCase(StringUtil.camelToUnderline(property))
            || (entityConfig != null && entityConfig.isColumnCommentEnable() && StringUtil.isNotBlank(this.comment) && annotations.length() == 0);

        StringBuilder columnAnnotation = new StringBuilder("@Column(");

        //@Column 注解
        if (columnConfig.getOnInsertValue() != null
            || columnConfig.getOnUpdateValue() != null
            || columnConfig.getLarge() != null
            || columnConfig.getLogicDelete() != null
            || columnConfig.getVersion() != null
            || columnConfig.getJdbcType() != null
            || columnConfig.getTypeHandler() != null
            || columnConfig.getTenantId() != null
            || needGenColumnAnnotation
        ) {
            boolean needComma = false;
            if (entityConfig != null && entityConfig.isAlwaysGenColumnAnnotation()
                || !name.equalsIgnoreCase(StringUtil.camelToUnderline(property))) {
                columnAnnotation.append("value = \"").append(name).append("\"");
                needComma = true;
            }

            if (columnConfig.getOnInsertValue() != null) {
                addComma(columnAnnotation, needComma);
                columnAnnotation.append("onInsertValue = \"").append(columnConfig.getOnInsertValue()).append("\"");
                needComma = true;
            }
            if (columnConfig.getOnUpdateValue() != null) {
                addComma(columnAnnotation, needComma);
                columnAnnotation.append("onUpdateValue = \"").append(columnConfig.getOnUpdateValue()).append("\"");
                needComma = true;
            }
            if (columnConfig.getLarge() != null) {
                addComma(columnAnnotation, needComma);
                columnAnnotation.append("isLarge = ").append(columnConfig.getLarge());
                needComma = true;
            }
            if (columnConfig.getLogicDelete() != null) {
                addComma(columnAnnotation, needComma);
                columnAnnotation.append("isLogicDelete = ").append(columnConfig.getLogicDelete());
                needComma = true;
            }
            if (columnConfig.getVersion() != null) {
                addComma(columnAnnotation, needComma);
                columnAnnotation.append("version = ").append(columnConfig.getVersion());
                needComma = true;
            }
            if (columnConfig.getJdbcType() != null) {
                addComma(columnAnnotation, needComma);
                columnAnnotation.append("jdbcType = JdbcType.").append(columnConfig.getJdbcType().name());
                needComma = true;
            }
            if (columnConfig.getTypeHandler() != null) {
                addComma(columnAnnotation, needComma);
                columnAnnotation.append("typeHandler = ").append(columnConfig.getTypeHandler().getSimpleName()).append(".class");
                needComma = true;
            }
            if (Boolean.TRUE.equals(columnConfig.getTenantId())) {
                addComma(columnAnnotation, needComma);
                columnAnnotation.append("tenantId = true");
                needComma = true;
            }
            if (entityConfig != null && entityConfig.isColumnCommentEnable() && StringUtil.isNotBlank(comment)) {
                addComma(columnAnnotation, needComma);
                columnAnnotation.append("comment = \"")
                    .append(this.comment.replace("\n", "")
                        .replace("\"", "\\\"")
                        .trim())
                    .append("\"");
            }
            columnAnnotation.append(")");

            // @Column(value = "user_name") -> @Column("user_name")
            int index = columnAnnotation.indexOf(",");
            if (index == -1) {
                int start = columnAnnotation.indexOf("value");
                if (start != -1) {
                    columnAnnotation.delete(start, start + 8);
                }
            }

            annotations.append(columnAnnotation);
        }

        // @ColumnMask 注解
        String maskType = columnConfig.getMaskType();
        if (maskType != null) {
            if (annotations.length() != 0) {
                annotations.append("\n    ");
            }
            annotations.append("@ColumnMask(");
            if (MaskManager.getProcessorMap().containsKey(maskType)) {
                // @ColumnMask(Masks.MOBILE)
                annotations.append("Masks.").append(maskType.toUpperCase());
            } else {
                // @ColumnMask("custom")
                annotations.append("\"").append(maskType).append("\"");
            }
            annotations.append(")");
        }

        return annotations.toString();
    }

    public Set<String> getImportClasses() {
        Set<String> importClasses = new LinkedHashSet<>();

        addImportClass(importClasses, propertyType);

        if (isPrimaryKey || (columnConfig != null && columnConfig.isPrimaryKey())) {
            addImportClass(importClasses, Id.class.getName());
            if (isAutoIncrement || (columnConfig != null && columnConfig.getKeyType() != null)) {
                addImportClass(importClasses, KeyType.class.getName());
            }
        }

        if (columnConfig != null) {
            if (columnConfig.getPropertyType() != null) {
                addImportClass(importClasses, columnConfig.getPropertyType());
            }
            if (columnConfig.getMaskType() != null) {
                addImportClass(importClasses, ColumnMask.class.getName());
                if (MaskManager.getProcessorMap().containsKey(columnConfig.getMaskType())) {
                    addImportClass(importClasses, Masks.class.getName());
                }
            }

            if (columnConfig.getJdbcType() != null) {
                addImportClass(importClasses, "org.apache.ibatis.type.JdbcType");
            }

            if (columnConfig.getTypeHandler() != null) {
                addImportClass(importClasses, columnConfig.getTypeHandler().getName());
            }

            boolean needGenColumnAnnotation = (entityConfig != null && entityConfig.isAlwaysGenColumnAnnotation())
                || !name.equalsIgnoreCase(StringUtil.camelToUnderline(property))
                || (entityConfig != null && entityConfig.isColumnCommentEnable() && StringUtil.isNotBlank(this.comment));

            if (columnConfig.getOnInsertValue() != null
                || columnConfig.getOnUpdateValue() != null
                || columnConfig.getLarge() != null
                || columnConfig.getLogicDelete() != null
                || columnConfig.getVersion() != null
                || columnConfig.getJdbcType() != null
                || columnConfig.getTypeHandler() != null
                || Boolean.TRUE.equals(columnConfig.getTenantId())
                || needGenColumnAnnotation
            ) {
                addImportClass(importClasses, com.mybatisflex.annotation.Column.class.getName());
            }
        }

        return importClasses;
    }

    public boolean isDefaultColumn() {
        if (columnConfig == null) {
            return true;
        }
        boolean isLarge = columnConfig.getLarge() != null && columnConfig.getLarge();
        boolean isLogicDelete = columnConfig.getLogicDelete() != null && columnConfig.getLogicDelete();
        return !isLarge && !isLogicDelete;
    }

    @Override
    public String toString() {
        return "Column{" +
            "name='" + name + '\'' +
            ", className='" + propertyType + '\'' +
            ", remarks='" + comment + '\'' +
            ", isAutoIncrement=" + isAutoIncrement +
            '}';
    }

}
