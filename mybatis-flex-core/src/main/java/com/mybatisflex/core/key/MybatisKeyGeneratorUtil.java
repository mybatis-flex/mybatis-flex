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
package com.mybatisflex.core.key;

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.enums.KeyType;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.table.IdInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MybatisKeyGeneratorUtil {


    public static KeyGenerator createTableKeyGenerator(TableInfo tableInfo, MappedStatement ms) {
        List<IdInfo> primaryKeyList = tableInfo.getPrimaryKeyList();

        //无主键
        if (primaryKeyList == null || primaryKeyList.isEmpty()) {
            return NoKeyGenerator.INSTANCE;
        }

        //多主键的
        if (primaryKeyList.size() > 1) {
            return new MultiPrimaryKeyGenerator(ms, tableInfo, primaryKeyList);
        }

        return createIdKeyGenerator(tableInfo, ms, primaryKeyList.get(0));
    }


    public static KeyGenerator createIdKeyGenerator(TableInfo tableInfo, MappedStatement ms, IdInfo idInfo) {
        if (idInfo.getKeyType() == KeyType.None) {
            return NoKeyGenerator.INSTANCE;
        }

        //自增主键
        if (idInfo.getKeyType() == KeyType.Auto) {
            return Jdbc3KeyGenerator.INSTANCE;
        }

        //通过 java 生成的主键
        if (idInfo.getKeyType() == KeyType.Generator) {
            return new CustomKeyGenerator(ms.getConfiguration(), tableInfo, idInfo);
        }

        //通过序列生成的注解
        String sequence = idInfo.getValue();
        if (StringUtil.isBlank(sequence)) {
            throw FlexExceptions.wrap("please config @Id(value=\"...\") for field: %s in class: %s"
                    , idInfo.getProperty()
                    , tableInfo.getEntityClass().getName());
        }


        String selectId = ms.getId() + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        SqlSource sqlSource = ms.getLang().createSqlSource(ms.getConfiguration(), sequence.trim(), idInfo.getPropertyType());
        MappedStatement.Builder msBuilder = new MappedStatement.Builder(ms.getConfiguration(), selectId, sqlSource, SqlCommandType.SELECT)
                .resource(ms.getResource())
                .fetchSize(null)
                .timeout(null)
                .statementType(StatementType.PREPARED)
                .keyGenerator(NoKeyGenerator.INSTANCE)
                .keyProperty(FlexConsts.ENTITY + "." + idInfo.getProperty())
                .keyColumn(idInfo.getColumn())
                .databaseId(ms.getDatabaseId())
                .lang(ms.getLang())
                .resultOrdered(false)
                .resultSets(null)
                .resultMaps(createIdResultMaps(ms.getConfiguration(), selectId + "-Inline", idInfo.getPropertyType(), new ArrayList<>()))
                .resultSetType(null)
                .flushCacheRequired(false)
                .useCache(false)
                .cache(ms.getCache());

        MappedStatement keyMappedStatement = msBuilder.build();
        ms.getConfiguration().addMappedStatement(keyMappedStatement);

        //看到有的框架把 keyGenerator 添加到 mybatis 的当前配置里去，其实是完全没必要的
        //因为只有在 xml 解析的时候，才可能存在多一个 MappedStatement 拥有同一个 keyGenerator 的情况
        //当前每个方法都拥有一个自己的 keyGenerator 了，没必要添加
        //this.addKeyGenerator(selectId, keyGenerator);

        return new SelectKeyGenerator(keyMappedStatement, idInfo.isBefore());
    }


    private static List<ResultMap> createIdResultMaps(Configuration configuration,
                                                      String statementId, Class<?> resultType, List<ResultMapping> resultMappings) {
        ResultMap resultMap = new ResultMap.Builder(configuration, statementId, resultType, resultMappings, null)
                .build();
        return Arrays.asList(resultMap);
    }

}
