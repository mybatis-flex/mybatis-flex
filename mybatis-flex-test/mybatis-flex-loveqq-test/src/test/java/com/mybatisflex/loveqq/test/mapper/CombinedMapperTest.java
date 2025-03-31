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

package com.mybatisflex.loveqq.test.mapper;

import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.loveqq.test.LoveqqExtension;
import com.mybatisflex.loveqq.test.model.Account;
import com.mybatisflex.loveqq.test.model.Article;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@Component
@ExtendWith(LoveqqExtension.class)
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
