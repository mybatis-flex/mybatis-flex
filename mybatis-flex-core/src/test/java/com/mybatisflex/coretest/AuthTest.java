package com.mybatisflex.coretest;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.row.RowUtil;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.coretest.auth.AuthDialectImpl;
import org.junit.Before;
import org.junit.Test;

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
        assert "DELETE FROM `tb_account` WHERE `id` = ?  AND `insert_user_id` = 1"
            .equals(dialect.forDeleteById(ACCOUNT.getSchema(), ACCOUNT.getTableName(), new String[]{ACCOUNT.ID.getName()}));
        // 2.批量删除
        assert "DELETE FROM `tb_account` WHERE `id` = ?  AND `insert_user_id` = 1"
            .equals(dialect.forDeleteBatchByIds(ACCOUNT.getSchema(), ACCOUNT.getTableName(), new String[]{ACCOUNT.ID.getName()}, new Object[]{1L}));
        // 3.查询
        QueryWrapper deleteWrapper =
            QueryWrapper.create(new Account()).where(ACCOUNT.ID.eq(1));
        assert "DELETE FROM `tb_account` WHERE `id` = ? AND `sex` = ? AND `is_normal` = ? AND `insert_user_id` = ?"
            .equals(dialect.forDeleteByQuery(deleteWrapper));
        // 4.更新
        assert "UPDATE `tb_account` SET `age` = ?  WHERE `id` = ?  AND `insert_user_id` = 1"
            .equals(dialect.forUpdateById(ACCOUNT.getSchema(), ACCOUNT.getTableName(),
            Row.ofKey(RowKey.AUTO).set(ACCOUNT.AGE, 18)));
        // 5.更新
        Row row = new Row();
        row.set(ACCOUNT.AGE, 18);
        QueryWrapper updateWrapper =
            QueryWrapper.create(new Account()).where(ACCOUNT.ID.eq(1));
        assert "UPDATE `tb_account` SET `age` = ?  WHERE `id` = ? AND `sex` = ? AND `is_normal` = ? AND `insert_user_id` = ?"
            .equals(dialect.forUpdateByQuery(updateWrapper, row));
        // 6.ID查询
        assert "SELECT * FROM `tb_account` WHERE `id` = ?  AND `insert_user_id` = 1"
            .equals(dialect.forSelectOneById(ACCOUNT.getSchema(), ACCOUNT.getTableName(), new String[]{ACCOUNT.ID.getName()}, new Object[]{1L}));
        QueryWrapper queryWrapper =
            QueryWrapper.create().select(ACCOUNT.ALL_COLUMNS).where(ACCOUNT.ID.eq(1));
        // 7.query查询
        assert "SELECT * FROM  WHERE `id` = ? AND `insert_user_id` = ?"
            .equals(dialect.forSelectByQuery(queryWrapper));
        // 8.删除
        assert "UPDATE `tb_account` SET `is_delete` = 1 WHERE `id` = ?  AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forDeleteEntityById(TableInfoFactory.ofEntityClass(Account.class)));
        // 9.批量删除
        assert "UPDATE `tb_account` SET `is_delete` = 1 WHERE (`id` = ? ) AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forDeleteEntityBatchByIds(TableInfoFactory.ofEntityClass(Account.class), new String[]{ACCOUNT.ID.getName()}));
        // 10.query删除
        assert "UPDATE `tb_account` SET `is_delete` = 1 WHERE `id` = ? AND `insert_user_id` = ? AND `insert_user_id` = ?"
            .equals(dialect.forDeleteEntityBatchByQuery(TableInfoFactory.ofEntityClass(Account.class), queryWrapper));
        // 11.更新
        Account account = new Account();
        account.setAge(18);
        assert "UPDATE `tb_account` SET `sex` = ? , `age` = ? , `is_normal` = ?  WHERE `id` = ?  AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forUpdateEntity(TableInfoFactory.ofEntityClass(Account.class), account, true));
        // 12.更新
        assert "UPDATE `tb_account` SET `sex` = ? , `age` = ? , `is_normal` = ?  WHERE `id` = ? AND `insert_user_id` = ? AND `insert_user_id` = ? AND `insert_user_id` = ?"
            .equals(dialect.forUpdateEntityByQuery(TableInfoFactory.ofEntityClass(Account.class), account, true, queryWrapper));
        // 13.ID查询
        assert "SELECT * FROM `tb_account` WHERE `id` = ?  AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forSelectOneEntityById(TableInfoFactory.ofEntityClass(Account.class)));
        // 14.查询
        assert "SELECT `id`, `user_name`, `birthday`, `sex`, `age`, `is_normal`, `is_delete`, `insert_user_id` FROM `tb_account` WHERE (`id` = ? ) AND `is_delete` = 0 AND `insert_user_id` = 1"
            .equals(dialect.forSelectEntityListByIds(TableInfoFactory.ofEntityClass(Account.class), new String[]{ACCOUNT.ID.getName()}));
    }

    @Test
    public void testWrapper() {
        // (为什么打印的sql是这样的 DELETE FROM  WHERE `id` = ? AND `insert_user_id` = ?)
        QueryWrapper deleteWrapper =
            QueryWrapper.create().where(ACCOUNT.ID.eq(1));
        System.out.println(dialect.forDeleteByQuery(deleteWrapper));
    }
}
