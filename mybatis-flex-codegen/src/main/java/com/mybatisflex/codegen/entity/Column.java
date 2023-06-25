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
import com.mybatisflex.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

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
     * 是否为主键。
     */
    private boolean isPrimaryKey = false;

    /**
     * 是否自增。
     */
    private boolean isAutoIncrement;

    /**
     * 是否需要生成 @Column 注解。
     */
    private boolean needGenColumnAnnotation = false;

    /**
     * 字段配置。
     */
    private ColumnConfig columnConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.property = buildPropertyName();
        this.needGenColumnAnnotation = !name.equalsIgnoreCase(StringUtil.camelToUnderline(property));
    }

    public String getProperty() {
        return property;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public String getPropertySimpleType() {
        return propertyType.substring(propertyType.lastIndexOf(".") + 1);
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

    public ColumnConfig getColumnConfig() {
        return columnConfig;
    }

    public void setColumnConfig(ColumnConfig columnConfig) {
        this.columnConfig = columnConfig;
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
            }

            if (annotations.length() == 4) {
                annotations.deleteCharAt(annotations.length() - 1);
            } else {
                annotations.append(")");
            }
        }

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
            annotations.append("@Column(");
            boolean needComma = false;
            if (needGenColumnAnnotation) {
                annotations.append("value = \"").append(name).append("\"");
                needComma = true;
            }

            if (columnConfig.getOnInsertValue() != null) {
                addComma(annotations, needComma);
                annotations.append("onInsertValue = \"").append(columnConfig.getOnInsertValue()).append("\"");
                needComma = true;
            }
            if (columnConfig.getOnUpdateValue() != null) {
                addComma(annotations, needComma);
                annotations.append("onUpdateValue = \"").append(columnConfig.getOnUpdateValue()).append("\"");
                needComma = true;
            }
            if (columnConfig.getLarge() != null) {
                addComma(annotations, needComma);
                annotations.append("isLarge = ").append(columnConfig.getLarge());
                needComma = true;
            }
            if (columnConfig.getLogicDelete() != null) {
                addComma(annotations, needComma);
                annotations.append("isLogicDelete = ").append(columnConfig.getLogicDelete());
                needComma = true;
            }
            if (columnConfig.getVersion() != null) {
                addComma(annotations, needComma);
                annotations.append("version = ").append(columnConfig.getVersion());
                needComma = true;
            }
            if (columnConfig.getJdbcType() != null) {
                addComma(annotations, needComma);
                annotations.append("jdbcType = JdbcType.").append(columnConfig.getJdbcType().name());
                needComma = true;
            }
            if (columnConfig.getTypeHandler() != null) {
                addComma(annotations, needComma);
                annotations.append("typeHandler = ").append(columnConfig.getTypeHandler().getSimpleName()).append(".class");
                needComma = true;
            }
            if (Boolean.TRUE.equals(columnConfig.getTenantId())) {
                addComma(annotations, needComma);
                annotations.append("tenantId = true");
            }
            annotations.append(")");
        }

        //@ColumnMask 注解
        if (columnConfig.getMask() != null) {
            annotations.append("@ColumnMask(\"").append(columnConfig.getMask()).append("\")");
        }

        String result = annotations.toString();
        if (!result.isEmpty()) {
            result += "\n    ";
        }
        return result;
    }

    private void addComma(StringBuilder annotations, boolean needComma) {
        if (needComma) {
            annotations.append(", ");
        }
    }

    public List<String> getImportClasses() {
        List<String> importClasses = new ArrayList<>();

        //lang 包不需要显式导入
        if (!propertyType.startsWith("java.lang.")
                && !"byte[]".equals(propertyType)
                && !"Byte[]".equals(propertyType)
        ) {
            importClasses.add(propertyType);
        }


        if (isPrimaryKey || (columnConfig != null && columnConfig.isPrimaryKey())) {
            importClasses.add(Id.class.getName());
            if (isAutoIncrement || (columnConfig != null && columnConfig.getKeyType() != null)) {
                importClasses.add(KeyType.class.getName());
            }
        }

        if (columnConfig != null) {
            if (columnConfig.getMask() != null) {
                importClasses.add(ColumnMask.class.getName());
            }

            if (columnConfig.getJdbcType() != null) {
                importClasses.add("org.apache.ibatis.type.JdbcType");
            }

            if (columnConfig.getTypeHandler() != null) {
                importClasses.add(columnConfig.getTypeHandler().getName());
            }

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
                importClasses.add(com.mybatisflex.annotation.Column.class.getName());
            }
        }

        return importClasses;
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
