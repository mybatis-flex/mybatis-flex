package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.keygen.KeyGeneratorFactory;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.mybatisflex.mapper.Account7Mapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.List;
import java.util.OptionalLong;

/**
 * @author mofan
 * @date 2023/12/4 23:06
 */
public class Account7Test implements WithAssertions {

    private EmbeddedDatabase dataSource;
    private Account7Mapper mapper;

    private static final String DATA_SOURCE_KEY = "generate_key";

    @BeforeClass
    public static void enableAudit() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
        // 设置主键生成策略
        KeyGeneratorFactory.register("test", new TestKeyGenerator());
    }

    @Before
    public void init() {
        this.dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("generate_key_schema.sql")
            .addScript("generate_key_data.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = new MybatisFlexBootstrap()
            .setDataSource(DATA_SOURCE_KEY, this.dataSource)
            .setLogImpl(StdOutImpl.class)
            .addMapper(Account7Mapper.class)
            .start();

        DataSourceKey.use(DATA_SOURCE_KEY);

        mapper = bootstrap.getMapper(Account7Mapper.class);
    }

    @After
    public void destroy() {
        this.dataSource.shutdown();
        DataSourceKey.clear();
    }

    /**
     * issues https://gitee.com/mybatis-flex/mybatis-flex/issues/I88TX1
     */
    @Test
    public void testGiteeIssue_I88TX1() {
        List<Account7> list = this.mapper.selectAll();
        OptionalLong maxIdOpt = list.stream().mapToLong(Account7::getId).max();
        if (!maxIdOpt.isPresent()) {
            Assert.fail();
        }

        Account7 account1 = new Account7();
        account1.setId(maxIdOpt.getAsLong() + 1);
        account1.setUserName("michael");
        account1.setAge(5);

        int result1 = this.mapper.insert(account1);
        assertThat(result1).isEqualTo(1);
        assertThat(account1).extracting(Account7::getId).isEqualTo(3L);


        Account7 account2 = new Account7();
        account2.setUserName("michael");
        account2.setAge(5);

        int result2 = this.mapper.insert(account2);
        assertThat(result2).isEqualTo(1);
        assertThat(account2).extracting(Account7::getId)
            .asInstanceOf(LONG)
            // 组件通过时间戳 / 1000 获取
            .isGreaterThanOrEqualTo(10000L);
    }


    /**
     * https://gitee.com/mybatis-flex/mybatis-flex/issues/I9DRC4
     */
    @Test
    public void testGiteeIssuI9DRC4(){
        Account7 account7 = this.mapper.selectOneByQuery(QueryWrapper.create().select().where(Account7::getId).eq("1"));
        Assert.assertNotNull(account7);
        System.out.println(account7);
    }
}
