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
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.util.StringUtil;

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


    public IdInfo(Id id) {
        this.keyType = id.keyType();
        this.value = id.value();
        this.before = id.before();
        this.comment = id.comment();

        initDefaultKeyType();
    }

    /**
     * 用户未配置 keyType 是，配置默认的 key Type
     */
    private void initDefaultKeyType() {
        if (this.keyType == null || this.keyType == KeyType.None) {
            FlexGlobalConfig.KeyConfig defaultKeyConfig = FlexGlobalConfig.getDefaultConfig().getKeyConfig();
            if (defaultKeyConfig != null) {
                if (defaultKeyConfig.getKeyType() != null) {
                    this.keyType = defaultKeyConfig.getKeyType();
                    this.before = defaultKeyConfig.isBefore();
                }
                if (StringUtil.isBlank(this.value) && StringUtil.isNotBlank(defaultKeyConfig.getValue())) {
                    this.value = defaultKeyConfig.getValue();
                }
            }
        }
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
