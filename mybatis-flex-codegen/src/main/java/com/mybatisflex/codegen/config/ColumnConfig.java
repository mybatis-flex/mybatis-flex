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

import com.mybatisflex.annotation.KeyType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;

/**
 * 表字段的单独设置。
 */
public class ColumnConfig implements Serializable {

    /**
     * 字段名称。
     */
    private String columnName;

    /**
     * insert 的时候默认值，这个值会直接被拼接到 sql 而不通过参数设置。
     */
    private String onInsertValue;

    /**
     * update 的时候自动赋值，这个值会直接被拼接到 sql 而不通过参数设置。
     */
    private String onUpdateValue;

    /**
     * 是否是大字段，大字段 APT 不会生成到 DEFAULT_COLUMNS 里。
     */
    private Boolean isLarge;

    /**
     * 是否是逻辑删除字段，一张表中只能存在 1 一个逻辑删除字段。
     */
    private Boolean isLogicDelete;

    /**
     * 是否为乐观锁字段。
     */
    private Boolean version;

    /**
     * 配置的 jdbcType。
     */
    private JdbcType jdbcType;
    /**
     * 属性的类型。
     */
    private Class<?> propertyType;

    /**
     * 自定义 TypeHandler。
     */
    private Class<? extends TypeHandler> typeHandler;

    /**
     * 脱敏方式。
     */
    private String mask;

    /**
     * 字段是否为主键。
     */
    private boolean isPrimaryKey = false;

    /**
     * ID 生成策略。
     */
    private KeyType keyType;

    /**
     * ID 生成器值。
     */
    private String keyValue;

    /**
     * sequence 序列执行顺序。
     */
    private Boolean keyBefore;

    /**
     * 是否是租户 ID。
     */
    private Boolean tenantId;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getOnInsertValue() {
        return onInsertValue;
    }

    public void setOnInsertValue(String onInsertValue) {
        this.onInsertValue = onInsertValue;
    }

    public String getOnUpdateValue() {
        return onUpdateValue;
    }

    public void setOnUpdateValue(String onUpdateValue) {
        this.onUpdateValue = onUpdateValue;
    }

    public Boolean getLarge() {
        return isLarge;
    }

    public void setLarge(Boolean large) {
        isLarge = large;
    }

    public Boolean getLogicDelete() {
        return isLogicDelete;
    }

    public void setLogicDelete(Boolean logicDelete) {
        isLogicDelete = logicDelete;
    }

    public Boolean getVersion() {
        return version;
    }

    public void setVersion(Boolean version) {
        this.version = version;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public Class<? extends TypeHandler> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(Class<? extends TypeHandler> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public Boolean getKeyBefore() {
        return keyBefore;
    }

    public void setKeyBefore(Boolean keyBefore) {
        this.keyBefore = keyBefore;
    }

    public Boolean getTenantId() {
        return tenantId;
    }

    public void setTenantId(Boolean tenantId) {
        this.tenantId = tenantId;
    }
    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }
}
