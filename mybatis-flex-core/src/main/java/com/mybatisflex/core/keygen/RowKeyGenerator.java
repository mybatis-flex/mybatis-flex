/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowCPI;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.CollectionUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;

import java.sql.Statement;
import java.util.*;

/**
 * 为 row 的主键生成器
 */
public class RowKeyGenerator implements KeyGenerator, IMultiKeyGenerator {

    private static final KeyGenerator[] NO_KEY_GENERATORS = new KeyGenerator[0];

    private final MappedStatement ms;
    private Set<String> autoKeyGeneratorNames;
    private KeyGenerator[] keyGenerators;


    public RowKeyGenerator(MappedStatement methodMappedStatement) {
        this.ms = methodMappedStatement;
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        Row row = (Row) ((Map<?, ?>) parameter).get(FlexConsts.ROW);
        // 重置 autoKeyGeneratorNames fix https://gitee.com/mybatis-flex/mybatis-flex/issues/ID64KB
        autoKeyGeneratorNames = null;
        keyGenerators = buildRowKeyGenerators(RowCPI.obtainsPrimaryKeys(row));
        for (KeyGenerator keyGenerator : keyGenerators) {
            keyGenerator.processBefore(executor, ms, stmt, parameter);
        }
    }


    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        for (KeyGenerator keyGenerator : keyGenerators) {
            keyGenerator.processAfter(executor, ms, stmt, parameter);
        }
    }


    private KeyGenerator[] buildRowKeyGenerators(RowKey[] rowKeys) {
        if (ArrayUtil.isEmpty(rowKeys)) {
            return NO_KEY_GENERATORS;
        }

        KeyGenerator[] keyGenerators = new KeyGenerator[rowKeys.length];
        for (int i = 0; i < rowKeys.length; i++) {
            KeyGenerator keyGenerator = createByRowKey(rowKeys[i]);
            keyGenerators[i] = keyGenerator;
        }
        return keyGenerators;
    }


    private KeyGenerator createByRowKey(RowKey rowKey) {
        if (rowKey == null || rowKey.getKeyType() == KeyType.None) {
            return NoKeyGenerator.INSTANCE;
        }

        String keyColumn = rowKey.getKeyColumn();
        if (rowKey.getKeyType() == KeyType.Auto) {
            if (autoKeyGeneratorNames == null) {
                autoKeyGeneratorNames = new LinkedHashSet<>();
            }
            autoKeyGeneratorNames.add(keyColumn);
            return new RowJdbc3KeyGenerator(keyColumn);
        }

        if (rowKey.getKeyType() == KeyType.Generator) {
            return new RowCustomKeyGenerator(rowKey);
        }
        //通过数据库的 sequence 生成主键
        else {
            String selectId = "row." + SelectKeyGenerator.SELECT_KEY_SUFFIX;
            String sequence = rowKey.getValue();
            SqlSource sqlSource = ms.getLang().createSqlSource(ms.getConfiguration(), sequence.trim(), Object.class);
            MappedStatement.Builder msBuilder = new MappedStatement.Builder(ms.getConfiguration(), selectId, sqlSource, SqlCommandType.SELECT)
                .resource(ms.getResource())
                .fetchSize(null)
                .timeout(null)
                .statementType(StatementType.PREPARED)
                .keyGenerator(NoKeyGenerator.INSTANCE)
                .keyProperty(FlexConsts.ROW + "." + keyColumn)
                .keyColumn(keyColumn)
                .databaseId(ms.getDatabaseId())
                .lang(ms.getLang())
                .resultOrdered(false)
                .resultSets(null)
                .resultMaps(new ArrayList<>())
                .resultSetType(null)
                .flushCacheRequired(false)
                .useCache(false)
                .cache(ms.getCache());

            MappedStatement keyMappedStatement = msBuilder.build();
            ms.getConfiguration().addMappedStatement(keyMappedStatement);

            //看到有的框架把 keyGenerator 添加到 mybatis 的当前配置里去，其实是完全没必要的
            //因为只有在 xml 解析的时候，才可能存在多一个 MappedStatement 拥有同一个 keyGenerator 的情况
            //当前每个方法都拥有一个自己的 keyGenerator 了，没必要添加
            //addKeyGenerator(selectId, keyGenerator)
            return new SelectKeyGenerator(keyMappedStatement, rowKey.isBefore());
        }

    }

    /**
     * 是否需要数据库生成主键
     *
     * @return true 需要生成
     */
    @Override
    public boolean hasGeneratedKeys() {
        return CollectionUtil.isNotEmpty(autoKeyGeneratorNames);
    }

    /**
     * 数据库主键定义的 key
     *
     * @return key 数组
     */
    @Override
    public String[] getKeyColumnNames() {
        return autoKeyGeneratorNames == null ? new String[0] : autoKeyGeneratorNames.toArray(new String[0]);
    }

}
