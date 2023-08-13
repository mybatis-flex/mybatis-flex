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
import com.mybatisflex.core.FlexGlobalConfig;
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

    private MybatisKeyGeneratorUtil() {
    }

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
        FlexGlobalConfig flexGlobalConfig = FlexGlobalConfig.getConfig(ms.getConfiguration());

        if(flexGlobalConfig == null){
            return NoKeyGenerator.INSTANCE;
        }

        FlexGlobalConfig.KeyConfig globalKeyConfig = flexGlobalConfig.getKeyConfig();
        KeyType keyType = getKeyType(idInfo, globalKeyConfig);

        if (keyType == null || keyType == KeyType.None) {
            return NoKeyGenerator.INSTANCE;
        }

        //自增主键
        if (keyType == KeyType.Auto) {
            return Jdbc3KeyGenerator.INSTANCE;
        }

        //通过 java 生成的主键
        if (keyType == KeyType.Generator) {
            return new CustomKeyGenerator(ms.getConfiguration(), tableInfo, idInfo);
        }

        //通过序列生成的注解
        String sequence = getKeyValue(idInfo, globalKeyConfig);
        if (StringUtil.isBlank(sequence)) {
            throw FlexExceptions.wrap("Please config sequence by @Id(value=\"...\") for field: %s in class: %s"
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
        //addKeyGenerator(selectId, keyGenerator)
        return new SelectKeyGenerator(keyMappedStatement, isKeyBefore(idInfo, globalKeyConfig));
    }


    private static List<ResultMap> createIdResultMaps(Configuration configuration,
                                                      String statementId, Class<?> resultType, List<ResultMapping> resultMappings) {
        ResultMap resultMap = new ResultMap.Builder(configuration, statementId, resultType, resultMappings, null)
            .build();
        return Arrays.asList(resultMap);
    }


    /**
     * 获取主键的 keyType，优先通过 @id 获取，获取不到通过全局配置获取
     */
    public static KeyType getKeyType(IdInfo idInfo, FlexGlobalConfig.KeyConfig globalKeyConfig) {
        KeyType keyType = idInfo.getKeyType();
        if (keyType != KeyType.None) {
            return keyType;
        }

        if (globalKeyConfig != null) {
            return globalKeyConfig.getKeyType();
        }

        return keyType;
    }


    public static String getKeyValue(IdInfo idInfo, FlexGlobalConfig.KeyConfig globalKeyConfig) {
        String value = idInfo.getValue();
        if (StringUtil.isBlank(value) && globalKeyConfig != null) {
            value = globalKeyConfig.getValue();
        }
        return value;
    }


    public static boolean isKeyBefore(IdInfo idInfo, FlexGlobalConfig.KeyConfig globalKeyConfig) {
        Boolean before = idInfo.getBefore();
        if (before == null && globalKeyConfig != null) {
            return globalKeyConfig.isBefore();
        } else {
            return before == null || before;
        }
    }

}
