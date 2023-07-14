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

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.util.CollectionUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * 多实体主键生成器，用于批量插入的场景
 */
public class MultiEntityKeyGenerator implements KeyGenerator {

    private final KeyGenerator keyGenerator;

    public MultiEntityKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        List<Object> entities = (List<Object>) ((Map) parameter).get(FlexConsts.ENTITIES);
        if (CollectionUtil.isNotEmpty(entities)) {
            for (Object entity : entities) {
                ((Map) parameter).put(FlexConsts.ENTITY, entity);
                keyGenerator.processBefore(executor, ms, stmt, parameter);
            }
        }
    }


    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // do nothing
        // 多条数据批量插入的场景下，不支持后设置主键
        // 比如 INSERT INTO `tb_account`(uuid,name,sex) VALUES (?, ?, ?), (?, ?, ?)
    }

}
