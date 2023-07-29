package com.mybatisflex.test.model.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 *  表定义层。
 *
 * @author james
 * @since 2023-07-29
 */
public class UserRoleTableDef extends TableDef {

    /**
     *
     */
    public static final UserRoleTableDef USER_ROLE = new UserRoleTableDef("tb_user_role");


    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");


    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{USER_ID, ROLE_ID};


    public UserRoleTableDef(String tableName) {
        super(tableName);
    }
}
