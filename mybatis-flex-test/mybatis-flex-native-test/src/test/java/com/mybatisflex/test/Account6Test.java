package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.mapper.Account6Mapper;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;

/**
 * @author mofan
 * @date 2023/12/4 22:44
 */
public class Account6Test implements WithAssertions {

    private EmbeddedDatabase dataSource;
    private Account6Mapper mapper;

    private static final String DATA_SOURCE_KEY = "none_key";

    @BeforeClass
    public static void enableAudit() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
    }

    @Before
    public void init() {
        this.dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("none_key_schema.sql")
            .addScript("none_key_data.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = new MybatisFlexBootstrap()
            .setDataSource(DATA_SOURCE_KEY, this.dataSource)
            .setLogImpl(StdOutImpl.class)
            .addMapper(Account6Mapper.class)
            .start();

        DataSourceKey.use(DATA_SOURCE_KEY);

        mapper = bootstrap.getMapper(Account6Mapper.class);
    }

    @After
    public void destroy() {
        this.dataSource.shutdown();
        DataSourceKey.clear();
    }

    /**
     * issues https://gitee.com/mybatis-flex/mybatis-flex/issues/I873OZ
     */
    @Test
    public void testGiteeIssue_I873OZ() {
        Account6 account1 = new Account6();
        account1.setId(3L);
        account1.setUserName("michael");
        account1.setAge(5);

        assertThat(this.mapper.insertSelective(account1)).isEqualTo(1);

        Account6 account2 = new Account6();
        account2.setUserName("michael");
        account2.setAge(5);
        try {
            mapper.insertSelective(account2);
            // 没有 ID，插入失败
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("NULL not allowed for column \"ID\""));
        }

        List<Account6> list = mapper.selectAll();
        assertThat(list).hasSize(3);
    }
}
