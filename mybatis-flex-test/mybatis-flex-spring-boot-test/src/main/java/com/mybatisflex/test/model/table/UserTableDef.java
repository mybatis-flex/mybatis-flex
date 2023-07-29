package com.mybatisflex.test.model.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 *  表定义层。
 *
 * @author james
 * @since 2023-07-29
 */
public class UserTableDef extends TableDef {

    /**
     *
     */
    public static final UserTableDef USER = new UserTableDef("tb_user");


    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");


    public final QueryColumn PASSWORD = new QueryColumn(this, "password");


    public final QueryColumn USER_NAME = new QueryColumn(this, "user_name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{USER_ID, USER_NAME, PASSWORD};


    public UserTableDef(String tableName) {
        super(tableName);
    }
}
