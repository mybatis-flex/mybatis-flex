package com.mybatisflex.test.mapper;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.model.Account;
import com.mybatisflex.test.model.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CombinedMapperTest {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Test
    void testQuery() {

        for (int i = 0; i < 10; i++) {
            List<Account> accounts = accountMapper.selectListByQuery(QueryWrapper.create());
            List<Article> articles = articleMapper.selectListByQuery(QueryWrapper.create());
        }


        System.out.println(">>>>>>finished!!!");
    }


}
