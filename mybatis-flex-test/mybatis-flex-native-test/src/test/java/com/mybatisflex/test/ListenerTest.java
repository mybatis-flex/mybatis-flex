package com.mybatisflex.test;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.MybatisFlexBootstrap;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.Date;

/**
 * 监听器测试
 *
 * @author snow
 * @since 2023/4/28
 */
public class ListenerTest implements WithAssertions {

    // 注册父类接口监听器
    @Test
    public void onInsertInterface() throws Exception {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .build();
        // 注册全局监听器
        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();
        defaultConfig.registerInsertListener(new AgeHandleListener(), AgeAware.class);

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
                .setLogImpl(StdOutImpl.class)
                .setDataSource(dataSource)
                .addMapper(AccountMapper.class)
                .start();

        AccountMapper accountMapper = bootstrap.getMapper(AccountMapper.class);
        Account account = new Account();
        account.setAge(-2);
        account.setUserName("on insert");
        account.setBirthday(new Date());
        accountMapper.insert(account);

        Account one = accountMapper.selectOneById(account.getId());
        System.out.println(one);
//        assertThat(one.getAge()).isEqualTo(1);
    }
}
