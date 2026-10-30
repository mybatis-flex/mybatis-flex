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
package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.query.QueryColumnBehavior;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.mapper.SecretUserMapper;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.Arrays;
import java.util.List;

import static com.mybatisflex.test.table.SecretUserTableDef.SECRET_USER;

/**
 * 字段加密场景下，以明文作为条件查询密文列的测试。
 *
 * @see CipherTypeHandler
 */
public class SecretUserTest implements WithAssertions {

    private static final String DATA_SOURCE_KEY = "secret_user";

    private static final String CERT_NO = "110101199001011234";

    private EmbeddedDatabase dataSource;
    private SecretUserMapper mapper;

    @BeforeClass
    public static void enableAudit() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
    }

    @Before
    public void init() {
        this.dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("secret_user_schema.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = new MybatisFlexBootstrap()
            .setDataSource(DATA_SOURCE_KEY, this.dataSource)
            .setLogImpl(StdOutImpl.class)
            .addMapper(SecretUserMapper.class)
            .start();

        DataSourceKey.use(DATA_SOURCE_KEY);
        this.mapper = bootstrap.getMapper(SecretUserMapper.class);

        SecretUser user = new SecretUser();
        user.setCertNo(CERT_NO);
        user.setUserName("michael");
        this.mapper.insert(user);
    }

    @After
    public void destroy() {
        QueryColumnBehavior.setApplyConditionTypeHandler(true);
        this.dataSource.shutdown();
        DataSourceKey.clear();
    }

    @Test
    public void testColumnStoredAsCipherText() {
        // 数据库中存放的是密文，而非明文
        assertThat(countBySql(CipherTypeHandler.encrypt(CERT_NO))).isEqualTo(1L);
        assertThat(countBySql(CERT_NO)).isZero();
    }

    private long countBySql(String certNo) {
        Object count = Db.selectObject("select count(*) from tb_secret_user where cert_no = ?", certNo);
        return ((Number) count).longValue();
    }

    @Test
    public void testSelectByPlainText() {
        // 直接以明文构建条件即可查到数据
        SecretUser user = mapper.selectOneByQuery(QueryWrapper.create()
            .where(SECRET_USER.CERT_NO.eq(CERT_NO)));

        assertThat(user).isNotNull();
        assertThat(user.getCertNo()).isEqualTo(CERT_NO);
    }

    @Test
    public void testSelectByPlainTextIn() {
        List<SecretUser> users = mapper.selectListByQuery(QueryWrapper.create()
            .where(SECRET_USER.CERT_NO.in(Arrays.asList(CERT_NO, "not-exists"))));

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getCertNo()).isEqualTo(CERT_NO);
    }

    @Test
    public void testUpdateByPlainText() {
        SecretUser user = new SecretUser();
        user.setUserName("michael2");

        int result = mapper.updateByQuery(user, QueryWrapper.create()
            .where(SECRET_USER.CERT_NO.eq(CERT_NO)));

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void testDeleteByPlainText() {
        int result = mapper.deleteByQuery(QueryWrapper.create()
            .where(SECRET_USER.CERT_NO.eq(CERT_NO)));

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void testPaginateByPlainText() {
        assertThat(mapper.selectCountByQuery(QueryWrapper.create()
            .where(SECRET_USER.CERT_NO.eq(CERT_NO)))).isEqualTo(1);
    }

    @Test
    public void testCipherTextConditionAfterDisabled() {
        QueryColumnBehavior.setApplyConditionTypeHandler(false);

        // 关闭后，条件参数不再应用类型处理器，需要自行加密
        assertThat(mapper.selectOneByQuery(QueryWrapper.create()
            .where(SECRET_USER.CERT_NO.eq(CERT_NO)))).isNull();
        assertThat(mapper.selectOneByQuery(QueryWrapper.create()
            .where(SECRET_USER.CERT_NO.eq(CipherTypeHandler.encrypt(CERT_NO))))).isNotNull();
    }

}
