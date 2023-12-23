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
import com.mybatisflex.coretest.auth.table.ProjectTableDef;
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

    @Test
    public void test() {
        // 1.单个删除
        assert "DELETE FROM `tb_project` WHERE `id` = ?  AND `insert_user_id` = 1"
            .equals(dialect.forDeleteById(PROJECT.getSchema(), PROJECT.getTableName(), new String[]{PROJECT.ID.getName()}));
        // 2.批量删除
        assert "DELETE FROM `tb_project` WHERE `id` = ?  AND `insert_user_id` = 1"
            .equals(dialect.forDeleteBatchByIds(PROJECT.getSchema(), PROJECT.getTableName(), new String[]{PROJECT.ID.getName()}, new Object[]{1L}));
        // 3.查询
        QueryWrapper deleteWrapper =
            QueryWrapper.create(new Project()).where(PROJECT.ID.eq(1));
        assert "DELETE FROM `tb_project` WHERE `id` = ? AND `insert_user_id` = ?"
            .equals(dialect.forDeleteByQuery(deleteWrapper));
        // 4.更新
        assert "UPDATE `tb_project` SET `name` = ?  WHERE `id` = ?  AND `insert_user_id` = 1"
            .equals(dialect.forUpdateById(PROJECT.getSchema(), PROJECT.getTableName(),
            Row.ofKey(RowKey.AUTO).set(PROJECT.NAME, "项目")));
        // 5.更新
        Row row = new Row();
        row.set(PROJECT.NAME, "项目");
        QueryWrapper updateWrapper =
            QueryWrapper.create(new Project()).where(PROJECT.ID.eq(1));
        assert "UPDATE `tb_project` SET `name` = ?  WHERE `id` = ? AND `insert_user_id` = ?"
            .equals(dialect.forUpdateByQuery(updateWrapper, row));
        // 6.ID查询
        assert "SELECT * FROM `tb_project` WHERE `id` = ?  AND `insert_user_id` = 1"
            .equals(dialect.forSelectOneById(PROJECT.getSchema(), PROJECT.getTableName(), new String[]{PROJECT.ID.getName()}, new Object[]{1L}));
        QueryWrapper queryWrapper = QueryWrapper.create(new Project()).where(PROJECT.ID.eq(1));
        // 7.query查询
        assert "SELECT `id`, `name`, `insert_user_id`, `is_delete` FROM `tb_project` WHERE `id` = ? AND `insert_user_id` = ?"
            .equals(dialect.forSelectByQuery(queryWrapper));
        // 8.删除
        assert "UPDATE `tb_project` SET `is_delete` = 1 WHERE `id` = ?  AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forDeleteEntityById(TableInfoFactory.ofEntityClass(Project.class)));
        // 9.批量删除
        assert "UPDATE `tb_project` SET `is_delete` = 1 WHERE (`id` = ? ) AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forDeleteEntityBatchByIds(TableInfoFactory.ofEntityClass(Project.class), new String[]{PROJECT.ID.getName()}));
        // 10.query删除
        assert "UPDATE `tb_project` SET `is_delete` = 1 WHERE `id` = ? AND `insert_user_id` = ? AND `insert_user_id` = ?"
            .equals(dialect.forDeleteEntityBatchByQuery(TableInfoFactory.ofEntityClass(Project.class), queryWrapper));
        // 11.更新
        Project project = new Project();
        project.setName("项目名称");
        assert "UPDATE `tb_project` SET `name` = ?  WHERE `id` = ?  AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forUpdateEntity(TableInfoFactory.ofEntityClass(Project.class), project, true));
        // 12.更新
        assert "UPDATE `tb_project` SET `name` = ?  WHERE `id` = ? AND `insert_user_id` = ? AND `insert_user_id` = ? AND `insert_user_id` = ?"
            .equals(dialect.forUpdateEntityByQuery(TableInfoFactory.ofEntityClass(Project.class), project, true, queryWrapper));
        // 13.ID查询
        assert "SELECT * FROM `tb_project` WHERE `id` = ?  AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forSelectOneEntityById(TableInfoFactory.ofEntityClass(Project.class)));
        // 14.查询
        assert "SELECT `id`, `name`, `insert_user_id`, `is_delete` FROM `tb_project` WHERE (`id` = ? ) AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forSelectEntityListByIds(TableInfoFactory.ofEntityClass(Project.class), new String[]{PROJECT.ID.getName()}));
    }

    @Test
    public void testWrapper() {
        // (为什么打印的sql是这样的 DELETE FROM  WHERE `id` = ? AND `insert_user_id` = ?)
        QueryWrapper deleteWrapper =
            QueryWrapper.create().where(ACCOUNT.ID.eq(1));
        System.out.println(dialect.forDeleteByQuery(deleteWrapper));
    }
}
