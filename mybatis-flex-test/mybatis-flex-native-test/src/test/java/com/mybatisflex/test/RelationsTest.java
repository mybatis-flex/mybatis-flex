/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.test;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.test.relation.mapper.BookMapper;
import com.mybatisflex.test.relation.mapper.MenuMapper;
import com.mybatisflex.test.relation.mapper.RelationAccountMapper;
import com.mybatisflex.test.relation.onetoone.AccountDTO;
import com.mybatisflex.test.relation.onetoone.Book;
import com.mybatisflex.test.relation.onetoone.Menu;
import com.mybatisflex.test.relation.onetoone.RelationAccount;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.mybatisflex.test.relation.onetoone.table.MenuTableDef.MENU;


public class RelationsTest implements WithAssertions {

    private RelationAccountMapper relationAccountMapper;
    private BookMapper bookMapper;
    private MenuMapper menuMapper;
    private EmbeddedDatabase dataSource;

    private static final String DATA_SOURCE_KEY = "relation-onetoone";
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    @BeforeClass
    public static void enableAudit() {
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(new ConsoleMessageCollector());
    }

    @Before
    public void init() {
        dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .addScript("relation/onetoone/schema.sql")
            .addScript("relation/onetoone/data.sql").setScriptEncoding("UTF-8")
            .build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
            .setDataSource(DATA_SOURCE_KEY, dataSource)
            .addMapper(RelationAccountMapper.class)
            .addMapper(BookMapper.class)
            .addMapper(MenuMapper.class)
            .start();

        DataSourceKey.use(DATA_SOURCE_KEY);

        relationAccountMapper = bootstrap.getMapper(RelationAccountMapper.class);
        bookMapper = bootstrap.getMapper(BookMapper.class);
        menuMapper = bootstrap.getMapper(MenuMapper.class);
    }

    @After
    public void destroy() {
        this.dataSource.shutdown();
        DataSourceKey.clear();
    }

    @Test
    @SneakyThrows
    public void testOneToOne() {
        List<RelationAccount> accounts = relationAccountMapper.selectAllWithRelations();
        assertThat(accounts).hasSize(5);
        assertRelationResult(accounts, "relation/result/account-relation-result.json");
    }

    @Test
    public void testManyToOne() {
        List<Book> books = bookMapper.selectAll();
        assertThat(books).hasSize(6)
            .extracting(Book::getId)
            .containsExactly(1L, 2L, 3L, 4L, 5L, 6L);

        RelationManager.queryRelations(bookMapper, books);
        assertRelationResult(books, "relation/result/book-relation-result.json");
    }

    @Test
    public void testManyToMany1() {
        List<RelationAccount> accounts = relationAccountMapper.selectAll();
        assertThat(accounts).hasSize(5)
            .extracting(RelationAccount::getId)
            .containsExactly(1L, 2L, 3L, 4L, 5L);

        RelationManager.queryRelations(relationAccountMapper, accounts);
        assertRelationResult(accounts, "relation/result/account-relation-result.json");
    }

    //    @Test
    public void testAsDto() {
        List<com.mybatisflex.test.relation.onetoone.AccountDTO> accounts = relationAccountMapper.selectListWithRelationsByQueryAs(QueryWrapper.create(), AccountDTO.class);
        assertRelationResult(accounts, "relation/result/accountDto-relation-result.json");
    }

    @Test
    public void testMenu() {
        QueryWrapper qw = QueryWrapper.create();
        qw.where(MENU.PARENT_ID.eq(0));

        List<Menu> menus = menuMapper.selectListWithRelationsByQuery(qw);
        assertRelationResult(menus, "relation/result/menu-relation-result.json");
    }

    @Test
    public void testMenuIgnoreParent() {
        QueryWrapper qw = QueryWrapper.create();
        qw.where(MENU.PARENT_ID.eq(0));

        RelationManager.addIgnoreRelations("parent");
        List<Menu> menus = menuMapper.selectListWithRelationsByQuery(qw);
        assertRelationResult(menus, "relation/result/menu-relation-ignore-parent-result.json");
    }

    @Test
    public void testPaginate() {
        Page<RelationAccount> accountPage = relationAccountMapper.paginateWithRelations(1, 2, QueryWrapper.create());
        assertRelationResult(accountPage.getRecords(), "relation/result/account-page-relation-result.json");
    }

    private void assertRelationResult(Object object, String classPath) {
        String resultJson = writeObject2String(object);
        String expectJson = getFileAsString(classPath);
        JsonAssertions.assertThatJson(resultJson).isEqualTo(expectJson);
    }

    @SneakyThrows
    private String writeObject2String(Object object) {
        return JSON_MAPPER.writeValueAsString(object);
    }

    @SneakyThrows
    private String getFileAsString(String classPath) {
        ClassPathResource resource = new ClassPathResource(classPath);
        return FileUtils.readFileToString(resource.getFile(), StandardCharsets.UTF_8);
    }
}
