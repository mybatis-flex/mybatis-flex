package com.mybatisflex.test;

import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.test.mapper.AccountMapper;
import com.mybatisflex.test.model.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class AccountTest {

    @Autowired
    AccountMapper accountMapper;

    @Test
    public void testSelectOne() {
        Account account = accountMapper.selectOneById(1);
        System.out.println(account);
    }

    @Test
    public void testSelectOneByRow() {
        Row row = Db.selectOneById("tb_account", "id", 1);
        System.out.println(row);
    }

}
