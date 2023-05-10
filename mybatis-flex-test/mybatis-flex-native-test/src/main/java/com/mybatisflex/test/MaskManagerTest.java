package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.mask.MaskManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

public class MaskManagerTest {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("db1")
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();

        MybatisFlexBootstrap.getInstance()
                .setDataSource(dataSource)
                .addMapper(AccountMapper.class)
                .start();

        //获取 mapper
        AccountMapper mapper = MybatisFlexBootstrap.getInstance().getMapper(AccountMapper.class);
        List<Account> accounts = mapper.selectAll();
//        List<Account> accounts = MaskManager.withoutMask(mapper::selectAll);
        System.out.println(accounts);
    }

}
