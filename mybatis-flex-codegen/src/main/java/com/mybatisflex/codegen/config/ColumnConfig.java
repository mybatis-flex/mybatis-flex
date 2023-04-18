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
package com.mybatisflex.codegen.config;

import com.mybatisflex.annotation.KeyType;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;

public class ColumnConfig implements Serializable {

    private String columnName;

    private String onInsertValue;
    private String onUpdateValue;

    private Boolean isLarge;
    private Boolean isLogicDelete;
    private Boolean version;

    private JdbcType jdbcType;
    private Class<? extends TypeHandler> typeHandler;

    private String mask;

    private boolean isPrimaryKey = false;
    private KeyType keyType;
    private String keyValue;
    private Boolean keyBefore;

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
}
