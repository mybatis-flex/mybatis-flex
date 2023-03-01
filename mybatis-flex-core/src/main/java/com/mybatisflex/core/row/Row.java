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
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfos;
import com.mybatisflex.core.util.ArrayUtil;

import java.util.HashMap;
import java.util.Set;

public class Row extends HashMap<String, Object> implements ModifyAttrsRecord {
    private static final Object[] NULL_ARGS = new Object[0];

    //主键，多个主键用英文逗号隔开
    private RowKey[] primaryKeys;

    public static Row of(String key, Object value) {
        Row row = new Row();
        return row.set(key, value);
    }


    public static Row ofKey(String primaryKey, Object value) {
        Row row = new Row();
        String[] primaryKeyStrings = primaryKey.split(",");
        row.primaryKeys = new RowKey[primaryKeyStrings.length];

        for (int i = 0; i < primaryKeyStrings.length; i++) {
            row.primaryKeys[i] = RowKey.of(primaryKeyStrings[i].trim());
        }

        if (primaryKeyStrings.length > 0 && !value.getClass().isArray()) {
            throw new IllegalArgumentException("the type of value[\"" + value + "\"] must be an array.");
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

    public static Row ofKey(RowKey ...rowKeys) {
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


    public Row set(String key, Object value) {
        put(key, value);
        boolean isPrimaryKey = false;
        if (this.primaryKeys != null){
            for (RowKey rowKey : primaryKeys) {
                if (rowKey.getKeyColumn().equals(key)){
                    isPrimaryKey = true;
                    break;
                }
            }
        }

        if (!isPrimaryKey){
            addModifyAttr(key);
        }

        return this;
    }


    public Object get(Object key, Object defaultValue) {
        Object result = super.get(key);
        return result != null ? result : defaultValue;
    }


    @Override
    public Object remove(Object key) {
        removeModifyAttr(key.toString());
        return super.remove(key);
    }


    public <T> T toEntity(Class<T> entityClass) {
        TableInfo tableInfo = TableInfos.ofEntityClass(entityClass);
        return tableInfo.newInstanceByRow(this);
    }


    public void keep(Set<String> attrs) {
        if (attrs == null) {
            throw new NullPointerException("attrs is null.");
        }

        clearModifyFlag();
        modifyAttrs.addAll(attrs);
    }

    /**
     * 获取修改的值，值需要保持顺序
     * 返回的内容不包含主键的值
     *
     * @return values 数组
     */
    public Object[] obtainModifyValues() {
        Object[] values = new Object[modifyAttrs.size()];
        int index = 0;
        for (String modifyAttr : modifyAttrs) {
            values[index++] = get(modifyAttr);
        }
        return values;
    }


    public String[] obtainsPrimaryKeyStrings() {
        String[] returnKeys = new String[primaryKeys.length];
        for (int i = 0; i < primaryKeys.length; i++) {
            returnKeys[i] = primaryKeys[i].keyColumn;
        }
        return returnKeys;
    }


    public RowKey[] obtainsPrimaryKeys() {
        return this.primaryKeys;
    }


    public Object[] obtainsPrimaryValues() {
        if (ArrayUtil.isEmpty(primaryKeys)) {
            return NULL_ARGS;
        }
        Object[] values = new Object[primaryKeys.length];
        for (int i = 0; i < primaryKeys.length; i++) {
            values[i] = get(primaryKeys[i].keyColumn);
        }
        return values;
    }


    public Object[] obtainModifyValuesAndPrimaryValues() {
        return ArrayUtil.concat(obtainModifyValues(), obtainsPrimaryValues());
    }

}
