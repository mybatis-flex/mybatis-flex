package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;

public class UpdateChainTest {

    static AccountMapper accountMapper;

    @BeforeClass
    public static void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("schema.sql")
            .addScript("data.sql")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .setLogImpl(StdOutImpl.class)
            .addMapper(AccountMapper.class)
            .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);

        accountMapper = bootstrap.getMapper(AccountMapper.class);

    }

    @Test
    public void testUpdateChain() {
        UpdateChain.of(Account.class)
            .set(Account::getUserName,"张三")
            .setRaw(Account::getAge,"age + 1")
            .where(Account::getId).eq(1)
            .update();

        Account account = accountMapper.selectOneById(1);
        System.out.println(account);
    }

    @Test
    public void testUpdateChain1() {
        UpdateChain.of(Account.class)
            .set(Account::getAge, ACCOUNT.AGE.add(1))
            .where(Account::getId).ge(100)
            .and(Account::getAge).eq(18)
            .update();

        QueryChain.of(accountMapper)
            .list()
            .forEach(System.out::println);
    }
}
