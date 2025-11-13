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

package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.coretest.auth.AuthDialectImpl;
import com.mybatisflex.coretest.auth.Project;
import org.junit.Before;
import org.junit.Test;

import static com.mybatisflex.coretest.auth.table.ProjectTableDef.PROJECT;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

/**
 * 权限测试
 * @author zhang
 * @since 2023/12/17
 */
public class AuthTest {

    private IDialect dialect;

    @Before
    public void init() {
        DialectFactory.registerDialect(DbType.MYSQL, new AuthDialectImpl());
        dialect = DialectFactory.getDialect();
    }

    private boolean containsAllParts(String sql, String expectedParts, String delimiter) {
        String[] parts = expectedParts.split(delimiter);
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (!trimmedPart.isEmpty() && !sql.contains(trimmedPart)) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void test() {
        // 1.单个删除
        String sql1 = dialect.forDeleteById(PROJECT.getSchema(), PROJECT.getName(), new String[]{PROJECT.ID.getName()});
        assert sql1.contains("DELETE FROM `tb_project` WHERE ") && containsAllParts(sql1, "`id` = ? AND `insert_user_id` = 1", " AND ");

        // 2.批量删除
        String sql2 = dialect.forDeleteBatchByIds(PROJECT.getSchema(), PROJECT.getName(), new String[]{PROJECT.ID.getName()}, new Object[]{1L});
        assert sql2.contains("DELETE FROM `tb_project` WHERE ") && containsAllParts(sql2, "`id` = ? AND `insert_user_id` = 1", " AND ");

        // 3.查询
        QueryWrapper deleteWrapper
                = QueryWrapper.create(new Project()).where(PROJECT.ID.eq(1));
        String sql3 = dialect.forDeleteByQuery(deleteWrapper);
        assert sql3.contains("DELETE FROM `tb_project` WHERE ") && containsAllParts(sql3, "`id` = ? AND `insert_user_id` = ?", " AND ");

        // 4.更新
        String sql4 = dialect.forUpdateById(PROJECT.getSchema(), PROJECT.getName(),
                Row.ofKey(RowKey.AUTO).set(PROJECT.NAME, "项目"));
        assert sql4.contains("UPDATE `tb_project` SET `name` = ?  WHERE ") && containsAllParts(sql4, "`id` = ? AND `insert_user_id` = 1", " AND ");

        // 5.更新
        Row row = new Row();
        row.set(PROJECT.NAME, "项目");
        QueryWrapper updateWrapper
                = QueryWrapper.create(new Project()).where(PROJECT.ID.eq(1));
        String sql5 = dialect.forUpdateByQuery(updateWrapper, row);
        assert sql5.contains("UPDATE `tb_project` SET `name` = ?  WHERE ") && containsAllParts(sql5, "`id` = ? AND `insert_user_id` = ?", " AND ");

        // 6.ID查询
        String sql6 = dialect.forSelectOneById(PROJECT.getSchema(), PROJECT.getName(), new String[]{PROJECT.ID.getName()}, new Object[]{1L});
        assert sql6.contains("SELECT * FROM `tb_project` WHERE ") && containsAllParts(sql6, "`id` = ? AND `insert_user_id` = 1", " AND ");

        QueryWrapper queryWrapper = QueryWrapper.create(new Project()).where(PROJECT.ID.eq(1));
        // 7.query查询
        String sql7 = dialect.forSelectByQuery(queryWrapper);
        assert sql7.startsWith("SELECT ") && containsAllParts(sql7, "`id`, `name`, `insert_user_id`, `is_delete`", ", ")
                && sql7.contains(" FROM `tb_project` WHERE ") && containsAllParts(sql7, "`id` = ? AND `insert_user_id` = ?", " AND ");

        // 8.删除
        String sql8 = dialect.forDeleteEntityById(TableInfoFactory.ofEntityClass(Project.class));
        assert sql8.contains("UPDATE `tb_project` SET `is_delete` = 1 WHERE ") && containsAllParts(sql8, "`id` = ? AND `is_delete` = 0 AND `insert_user_id` = 1", " AND ");

        // 9.批量删除
        String sql9 = dialect.forDeleteEntityBatchByIds(TableInfoFactory.ofEntityClass(Project.class), new String[]{PROJECT.ID.getName()});
        assert sql9.contains("UPDATE `tb_project` SET `is_delete` = 1 WHERE ") && containsAllParts(sql9, "(`id` = ? ) AND `is_delete` = 0 AND `insert_user_id` = 1", " AND ");

        // 10.query删除
        String sql10 = dialect.forDeleteEntityBatchByQuery(TableInfoFactory.ofEntityClass(Project.class), queryWrapper);
        assert sql10.contains("UPDATE `tb_project` SET `is_delete` = 1 WHERE ") && containsAllParts(sql10, "`id` = ? AND `insert_user_id` = ?", " AND ");

        // 11.更新
        Project project = new Project();
        project.setName("项目名称");
        String sql11 = dialect.forUpdateEntity(TableInfoFactory.ofEntityClass(Project.class), project, true);
        assert sql11.contains("UPDATE `tb_project` SET `name` = ?  WHERE ") && containsAllParts(sql11, "`id` = ? AND `is_delete` = 0 AND `insert_user_id` = 1", " AND ");

        // 12.更新
        String sql12 = dialect.forUpdateEntityByQuery(TableInfoFactory.ofEntityClass(Project.class), project, true, queryWrapper);
        assert sql12.contains("UPDATE `tb_project` SET `name` = ?  WHERE ") && containsAllParts(sql12, "`id` = ? AND `insert_user_id` = ?", " AND ");

        // 13.ID查询
        String sql13 = dialect.forSelectOneEntityById(TableInfoFactory.ofEntityClass(Project.class));
        assert sql13.contains("SELECT * FROM `tb_project` WHERE ") && containsAllParts(sql13, "`id` = ? AND `is_delete` = 0 AND `insert_user_id` = 1", " AND ");

        // 14.查询
        String sql14 = dialect.forSelectEntityListByIds(TableInfoFactory.ofEntityClass(Project.class), new String[]{PROJECT.ID.getName()});
        assert sql14.startsWith("SELECT ") && containsAllParts(sql14, "`id`, `name`, `insert_user_id`, `is_delete`", ", ")
                && sql14.contains(" FROM `tb_project` WHERE ") && containsAllParts(sql14, "(`id` = ? ) AND `is_delete` = 0 AND `insert_user_id` = 1", " AND ");
    }

    @Test
    public void testWrapper() {
        // (为什么打印的sql是这样的 DELETE FROM  WHERE `id` = ? AND `insert_user_id` = ?)
        QueryWrapper deleteWrapper =
            QueryWrapper.create().where(ACCOUNT.ID.eq(1));
        System.out.println(dialect.forDeleteByQuery(deleteWrapper));
    }
}