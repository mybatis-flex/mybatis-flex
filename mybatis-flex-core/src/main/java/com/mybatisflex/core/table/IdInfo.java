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
package com.mybatisflex.core.table;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;

public class IdInfo extends ColumnInfo {

    /**
     * id 生成策略
     */
    private KeyType keyType;

    /**
     * 1、若 keyType 类型是 sequence， value 则代表的是
     * sequence 序列的 sql 内容
     * 例如：select SEQ_USER_ID.nextval as id from dual
     * <p>
     * 2、若 keyType 是 Generator，value 则代表的是使用的那个 keyGenerator 的名称
     */
    private String value;


    /**
     * sequence 序列内容执行顺序
     *
     * @see org.apache.ibatis.executor.keygen.SelectKeyGenerator
     */
    private Boolean before;


    public IdInfo(ColumnInfo columnInfo) {
        this.setColumn(columnInfo.getColumn());
        this.setAlias(columnInfo.getAlias());
        this.setProperty(columnInfo.getProperty());
        this.setPropertyType(columnInfo.getPropertyType());

        //当 id 的类型为数值时，默认设置为自增的方式
        if (Number.class.isAssignableFrom(columnInfo.getPropertyType())) {
            keyType = KeyType.Auto;
        } else {
            keyType = KeyType.None;
        }
    }

    public IdInfo(Id id) {
        this.keyType = id.keyType();
        this.value = id.value();
        this.before = id.before();
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getBefore() {
        return before;
    }

    public void setBefore(Boolean before) {
        this.before = before;
    }
}
