/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.test.relation.onetoone;

import com.alibaba.fastjson.JSON;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.test.relation.mapper.AccountMapper;
import com.mybatisflex.test.relation.mapper.BookMapper;
import com.mybatisflex.test.relation.mapper.MenuMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

import static com.mybatisflex.test.relation.onetoone.table.MenuTableDef.MENU;


public class RelationsTester {

    static AccountMapper accountMapper;
    static BookMapper bookMapper;
    static MenuMapper menuMapper;

    @BeforeClass
    public static void init() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("relation/onetoone/schema.sql")
                .addScript("relation/onetoone/data.sql")
                .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
                .setDataSource(dataSource)
                .addMapper(AccountMapper.class)
                .addMapper(BookMapper.class)
                .addMapper(MenuMapper.class)
                .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);

        accountMapper = bootstrap.getMapper(AccountMapper.class);
        bookMapper = bootstrap.getMapper(BookMapper.class);
        menuMapper = bootstrap.getMapper(MenuMapper.class);
    }


    @Test
    public void testOneToOne() {
        List<Account> accounts = accountMapper.selectAllWithRelations();
        System.out.println(JSON.toJSONString(accounts));
    }


    @Test
    public void testManyToOne() {
        List<Book> books = bookMapper.selectAll();
        System.out.println(">>>>>>1: " + books);
        RelationManager.queryRelations(bookMapper, books);
        System.out.println(">>>>>>2: " + books);
    }

    @Test
    public void testManyToMany1() {
        List<Account> accounts = accountMapper.selectAll();
        System.out.println(">>>>>>1: " + accounts);
        RelationManager.queryRelations(accountMapper, accounts);
        System.out.println(">>>>>>2: " + accounts);
    }

    @Test
    public void testMenu() {
        QueryWrapper qw = QueryWrapper.create();
        qw.where(MENU.PARENT_ID.eq(0));

        List<Menu> menus = menuMapper.selectListWithRelationsByQuery(qw);
        System.out.println( JSON.toJSONString(menus));
    }

    @Test
    public void testPaginate() {
        Page<Account> accountPage = accountMapper.paginateWithRelations(1, 2, QueryWrapper.create());
        System.out.println(accountPage);
    }


}
