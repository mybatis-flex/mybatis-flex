package com.mybatisflex.test;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.test.mapper.AccountMapper;
import com.mybatisflex.test.model.Account;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.mybatisflex.test.model.table.AccountTableDef.ACCOUNT;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class AccountTest implements WithAssertions {

    @Autowired
    AccountMapper accountMapper;

    @Test
    public void testSelectOne() {
        Account account = accountMapper.selectOneById(1);
        assertThat(account).isNotNull()
                .satisfies(a -> assertThat(a.getId()).isEqualTo(1));
    }

    @Test
    public void testSelectByQuery() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .where(ACCOUNT.AGE.eq(18));
        List<Account> accounts = accountMapper.selectListByQuery(queryWrapper);
        assertThat(accounts.size()).isEqualTo(1);
        assertThat(accounts.get(0).getAge()).isEqualTo(18);
    }

    @Test
    public void testSelectOneByRow() {
        Row row = Db.selectOneById(null,"tb_account", "id", 1);
        System.out.println(row);
    }

}
