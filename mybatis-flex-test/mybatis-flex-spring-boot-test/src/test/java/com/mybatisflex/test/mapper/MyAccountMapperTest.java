package com.mybatisflex.test.mapper;

import com.mybatisflex.test.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author 庄佳彬
 * @since 2023/4/24 19:37
 */
@SpringBootTest
class MyAccountMapperTest {
    @Autowired
    private MyAccountMapper mapper;

    @Test
    void insertBatch() {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < 3_3334; i++) {
            Account account = new Account();
            account.setBirthday(new Date());
            account.setAge(i % 60);
            account.setUserName(String.valueOf(i));
            accounts.add(account);
        }
        //删除初始化数据
        mapper.deleteById(1);
        mapper.deleteById(2);
        try {
            mapper.insertBatch(accounts);
        } catch (Exception e) {
            System.out.println("异常");
        }
        int i = mapper.insertBatch(accounts, 1000);
        assertEquals(33334, i);
    }

}