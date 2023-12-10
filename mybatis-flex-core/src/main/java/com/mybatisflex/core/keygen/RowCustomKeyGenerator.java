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
package com.mybatisflex.core.keygen;

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Statement;
import java.util.Map;

/**
 * 通过 java 编码的方式生成主键
 * 当主键类型配置为 KeyType#Generator 时，使用此生成器生成
 * {@link KeyType#Generator}
 */
public class RowCustomKeyGenerator implements KeyGenerator {

    protected RowKey rowKey;
    protected IKeyGenerator keyGenerator;


    public RowCustomKeyGenerator(RowKey rowKey) {
        this.rowKey = rowKey;
        this.keyGenerator = KeyGeneratorFactory.getKeyGenerator(rowKey.getValue());

        ensuresKeyGeneratorNotNull();
    }

    private void ensuresKeyGeneratorNotNull() {
        if (keyGenerator == null) {
            throw FlexExceptions.wrap("The name of \"%s\" key generator not exist.", rowKey.getValue());
        }
    }


    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        Row row = (Row) ((Map) parameter).get(FlexConsts.ROW);
        try {
            Object existId = row.get(rowKey.getKeyColumn());
            // 若用户主动设置了主键，则使用用户自己设置的主键，不再生成主键
            // 只有主键为 null 或者 空字符串时，对主键进行设置
            if (existId == null || (existId instanceof String && StringUtil.isBlank((String) existId))) {
                Object generateId = keyGenerator.generate(row, rowKey.getKeyColumn());
                row.put(rowKey.getKeyColumn(), generateId);
            }
        } catch (Exception e) {
            throw FlexExceptions.wrap(e);
        }
    }


    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        //do nothing
    }

}
