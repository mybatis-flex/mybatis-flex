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

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.keygen.impl.UUIDv7KeyGenerator;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * 验证真实主键生命周期，不连接数据库。
 */
public class UUIDv7KeyGeneratorIntegrationTest {

    private static final String ENVIRONMENT_ID = UUIDv7KeyGeneratorIntegrationTest.class.getName();
    private CustomKeyGenerator keyGenerator;
    private MappedStatement statement;

    @Before
    public void setUp() {
        Configuration configuration = new Configuration(new Environment(ENVIRONMENT_ID,
            new JdbcTransactionFactory(), new UnpooledDataSource()));
        FlexGlobalConfig.setConfig(ENVIRONMENT_ID, new FlexGlobalConfig(), false);
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(UUIDv7Entity.class);
        keyGenerator = new CustomKeyGenerator(configuration, tableInfo, tableInfo.getPrimaryKeyList().get(0));
        statement = new MappedStatement.Builder(configuration, ENVIRONMENT_ID + ".insert",
            new StaticSqlSource(configuration, "INSERT INTO uuidv7_keygen_test (id) VALUES (?)"),
            SqlCommandType.INSERT).build();
    }

    @After
    public void tearDown() {
        FlexGlobalConfig.getGlobalConfigs().remove(ENVIRONMENT_ID);
        TableInfoFactory.evict(UUIDv7Entity.class);
    }

    @Test
    public void registersBuiltInGenerator() {
        assertTrue(KeyGeneratorFactory.getKeyGenerator(KeyGenerators.uuidv7) instanceof UUIDv7KeyGenerator);
    }

    @Test
    public void fillsNullAndBlankIds() {
        for (String initial : new String[]{null, "", "   "}) {
            UUIDv7Entity entity = new UUIDv7Entity();
            entity.setId(initial);
            processBefore(entity);
            assertUUID(entity.getId(), 7);
        }
    }

    @Test
    public void preservesAssignedId() {
        UUIDv7Entity entity = new UUIDv7Entity();
        entity.setId("assigned-id");
        processBefore(entity);
        assertEquals("assigned-id", entity.getId());
    }

    @Test
    public void fillsMultipleEntities() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            UUIDv7Entity entity = new UUIDv7Entity();
            processBefore(entity);
            assertUUID(entity.getId(), 7);
            assertTrue(ids.add(entity.getId()));
        }
    }

    @Test
    public void keepsExistingUUIDv4Strategy() {
        String value = (String) KeyGeneratorFactory.getKeyGenerator(KeyGenerators.uuid).generate(null, null);
        assertUUID(value, 4);
    }

    private void processBefore(UUIDv7Entity entity) {
        keyGenerator.processBefore(null, statement, null, Collections.singletonMap(FlexConsts.ENTITY, entity));
    }

    private static void assertUUID(String value, int version) {
        assertNotNull(value);
        assertTrue(value.matches("[0-9a-f]{32}"));
        UUID uuid = UUID.fromString(value.substring(0, 8) + "-" + value.substring(8, 12)
            + "-" + value.substring(12, 16) + "-" + value.substring(16, 20)
            + "-" + value.substring(20));
        assertEquals(version, uuid.version());
        assertEquals(2, uuid.variant());
    }

}
