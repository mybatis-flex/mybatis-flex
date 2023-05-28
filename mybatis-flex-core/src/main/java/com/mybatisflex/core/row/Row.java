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
package com.mybatisflex.core.row;

import com.mybatisflex.core.javassist.ModifyAttrsRecord;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.ConvertUtil;
import com.mybatisflex.core.util.SqlUtil;
import com.mybatisflex.core.util.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class Row extends LinkedHashMap<String, Object> implements ModifyAttrsRecord {

    private static final Object[] NULL_ARGS = new Object[0];

    //主键，多个主键用英文逗号隔开
    private RowKey[] primaryKeys;

    public static Row of(String key, Object value) {
        Row row = new Row();
        return row.set(key, value);
    }


    private final Set<String> modifyAttrs = new LinkedHashSet<>();

    @Override
    public Set<String> getModifyAttrs() {
        return modifyAttrs;
    }


    public static Row ofKey(String primaryKey, Object value) {
        Row row = new Row();
        String[] primaryKeyStrings = primaryKey.split(",");
        row.primaryKeys = new RowKey[primaryKeyStrings.length];

        for (int i = 0; i < primaryKeyStrings.length; i++) {
            row.primaryKeys[i] = RowKey.of(primaryKeyStrings[i].trim());
        }

        if (primaryKeyStrings.length > 1 && !value.getClass().isArray()) {
            throw new IllegalArgumentException("The type of \"" + value + "\" must be an array.");
        }

        if (primaryKeyStrings.length == 1) {
            row.put(primaryKey.trim(), value);
        } else {
            Object[] values = (Object[]) value;
            for (int i = 0; i < primaryKeyStrings.length; i++) {
                row.put(primaryKeyStrings[i].trim(), values[i]);
            }
        }
        return row;
    }

    public static Row ofKey(RowKey... rowKeys) {
        Row row = new Row();
        row.primaryKeys = rowKeys;
        return row;
    }


    public static Row ofKey(RowKey rowKey, Object value) {
        Row row = new Row();
        row.primaryKeys = new RowKey[]{rowKey};
        row.put(rowKey.keyColumn, value);
        return row;
    }


    public static Row ofKey(RowKey[] rowKeys, Object[] value) {
        Row row = new Row();
        row.primaryKeys = rowKeys;
        for (int i = 0; i < rowKeys.length; i++) {
            row.put(rowKeys[i].keyColumn, value[i]);
        }
        return row;
    }


    public Row set(String column, Object value) {
        if (StringUtil.isBlank(column)) {
            throw new IllegalArgumentException("key column not be null or empty.");
        }

        SqlUtil.keepColumnSafely(column);

        //覆盖 put
        super.put(column, value);

        boolean isPrimaryKey = false;
        if (this.primaryKeys != null) {
            for (RowKey rowKey : primaryKeys) {
                if (rowKey.getKeyColumn().equals(column)) {
                    isPrimaryKey = true;
                    break;
                }
            }
        }

        if (!isPrimaryKey) {
            addModifyAttr(column);
        }

        return this;
    }

    public Row set(QueryColumn queryColumn, Object value) {
        return set(queryColumn.getName(), value);
    }


    public Object get(String key, Object defaultValue) {
        Object result = super.get(key);
        return result != null ? result : defaultValue;
    }


    @Override
    public Object put(String key, Object value) {
        if (!containsKey(key)) {
            return super.put(key, value);
        } else {
            for (int i = 1; i < 100; i++) {
                String newKey = key + RowUtil.INDEX_SEPARATOR + 1;
                if (!containsKey(newKey)) {
                    return super.put(newKey, value);
                }
            }
        }
        return super.put(key, value);
    }


    public String getString(String key) {
        Object s = super.get(key);
        return s != null ? s.toString() : null;
    }


    public String getString(String key, String defaultValue) {
        Object s = super.get(key);
        if (s == null) {
            return defaultValue;
        }
        String r = s.toString();
        return r.trim().length() == 0 ? defaultValue : r;
    }

    public Integer getInt(String key) {
        return ConvertUtil.toInt(super.get(key));
    }

    public Integer getInt(String key, Integer defaultValue) {
        Integer r = ConvertUtil.toInt(super.get(key));
        return r != null ? r : defaultValue;
    }

    public Long getLong(String key) {
        return ConvertUtil.toLong(super.get(key));
    }

    public Long getLong(String key, Long defaultValue) {
        Long r = ConvertUtil.toLong(super.get(key));
        return r != null ? r : defaultValue;
    }

    public Double getDouble(String key) {
        return ConvertUtil.toDouble(super.get(key));
    }

    public Double getDouble(String key, Double defaultValue) {
        Double r = ConvertUtil.toDouble(super.get(key));
        return r != null ? r : defaultValue;
    }


    public Float getFloat(String key, Float defaultValue) {
        Float r = ConvertUtil.toFloat(super.get(key));
        return r != null ? r : defaultValue;
    }

    public Float getFloat(String key) {
        return ConvertUtil.toFloat(super.get(key));
    }


    public Short getShort(String key, Short defaultValue) {
        Short r = ConvertUtil.toShort(super.get(key));
        return r != null ? r : defaultValue;
    }

    public Short getShort(String key) {
        return ConvertUtil.toShort(super.get(key));
    }

    public BigInteger getBigInteger(String key) {
        return ConvertUtil.toBigInteger(super.get(key));
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        BigInteger r = ConvertUtil.toBigInteger(super.get(key));
        return r != null ? r : defaultValue;
    }

    public BigDecimal getBigDecimal(String key) {
        return ConvertUtil.toBigDecimal(super.get(key));
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        BigDecimal r = ConvertUtil.toBigDecimal(super.get(key));
        return r != null ? r : defaultValue;
    }

    public Boolean getBoolean(String key) {
        return ConvertUtil.toBoolean(super.get(key));
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Boolean r = ConvertUtil.toBoolean(super.get(key));
        return r != null ? r : defaultValue;
    }

    public Date getDate(String key) {
        return ConvertUtil.toDate(super.get(key));
    }

    public Date getDate(String key, Date defaultValue) {
        Date r = ConvertUtil.toDate(super.get(key));
        return r != null ? r : defaultValue;
    }

    public LocalDateTime getLocalDateTime(String key) {
        return ConvertUtil.toLocalDateTime(super.get(key));
    }

    public LocalDateTime getLocalDateTime(String key, LocalDateTime defaultValue) {
        LocalDateTime r = ConvertUtil.toLocalDateTime(super.get(key));
        return r != null ? r : defaultValue;
    }

    public Time getTime(String key) {
        return (Time) super.get(key);
    }

    public Time getTime(String key, Time defaultValue) {
        Time r = (Time) super.get(key);
        return r != null ? r : defaultValue;
    }

    public Timestamp getTimestamp(String key) {
        return (Timestamp) super.get(key);
    }

    public Timestamp getTimestamp(String key, Timestamp defaultValue) {
        Timestamp r = (Timestamp) super.get(key);
        return r != null ? r : defaultValue;
    }

    public Byte getByte(String key) {
        return ConvertUtil.toByte(super.get(key));
    }

    public byte[] getBytes(String key) {
        return (byte[]) super.get(key);
    }

    @Override
    public Object remove(Object key) {
        removeModifyAttr(key.toString());
        return super.remove(key);
    }

    public <T> T toEntity(Class<T> entityClass) {
        return RowUtil.toEntity(this, entityClass);
    }

    public <T> T toObject(Class<T> objectClass) {
        return RowUtil.toObject(this, objectClass);
    }

    public Map<String, Object> toCamelKeysMap() {
        Map<String, Object> ret = new HashMap<>();
        for (String key : keySet()) {
            ret.put(StringUtil.underlineToCamel(key), get(key));
        }
        return ret;
    }

    public Map<String, Object> toUnderlineKeysMap() {
        Map<String, Object> ret = new HashMap<>();
        for (String key : keySet()) {
            ret.put(StringUtil.camelToUnderline(key), get(key));
        }
        return ret;
    }

    public void prepareAttrsByKeySet(){
        this.modifyAttrs.clear();
        this.modifyAttrs.addAll(keySet());

        if (this.primaryKeys != null){
            for (RowKey primaryKey : primaryKeys) {
                this.modifyAttrs.removeIf(s -> s.equalsIgnoreCase(primaryKey.getKeyColumn()));
            }
        }
    }


    public void prepareAttrsByKeySet(RowKey ... primaryKeys){
        this.modifyAttrs.clear();
        this.modifyAttrs.addAll(keySet());
        this.primaryKeys = primaryKeys;

        for (RowKey primaryKey : primaryKeys) {
            this.modifyAttrs.removeIf(s -> s.equalsIgnoreCase(primaryKey.getKeyColumn()));
        }
    }

    public void prepareAttrs(Collection<String> attrs) {
        if (attrs == null) {
            throw new NullPointerException("attrs is null.");
        }
        clearModifyFlag();
        modifyAttrs.addAll(attrs);
    }

    public RowKey[] getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(RowKey... primaryKeys) {
        this.primaryKeys = primaryKeys;
        for (RowKey primaryKey : primaryKeys) {
            this.modifyAttrs.removeIf(s -> s.equalsIgnoreCase(primaryKey.getKeyColumn()));
        }
    }

    /**
     * 获取修改的值，值需要保持顺序，返回的内容不包含主键的值
     */
    Object[] obtainModifyValues() {
        Object[] values = new Object[modifyAttrs.size()];
        int index = 0;
        for (String modifyAttr : modifyAttrs) {
            values[index++] = get(modifyAttr);
        }
        return values;
    }


    String[] obtainsPrimaryKeyStrings() {
        String[] returnKeys = new String[primaryKeys.length];
        for (int i = 0; i < primaryKeys.length; i++) {
            returnKeys[i] = primaryKeys[i].keyColumn;
        }
        return returnKeys;
    }


    RowKey[] obtainsPrimaryKeys() {
        return this.primaryKeys;
    }


    Object[] obtainsPrimaryValues() {
        if (ArrayUtil.isEmpty(primaryKeys)) {
            return NULL_ARGS;
        }
        Object[] values = new Object[primaryKeys.length];
        for (int i = 0; i < primaryKeys.length; i++) {
            values[i] = get(primaryKeys[i].keyColumn);
        }
        return values;
    }


    Object[] obtainAllModifyValues() {
        return ArrayUtil.concat(obtainModifyValues(), obtainsPrimaryValues());
    }



}
