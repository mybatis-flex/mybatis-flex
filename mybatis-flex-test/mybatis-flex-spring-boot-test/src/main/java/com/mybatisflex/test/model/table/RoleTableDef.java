package com.mybatisflex.test.model.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 *  表定义层。
 *
 * @author james
 * @since 2023-07-29
 */
public class RoleTableDef extends TableDef {

    /**
     *
     */
    public static final RoleTableDef ROLE = new RoleTableDef("tb_role");


    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");


    public final QueryColumn ROLE_KEY = new QueryColumn(this, "role_key");


    public final QueryColumn ROLE_NAME = new QueryColumn(this, "role_name");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ROLE_ID, ROLE_KEY, ROLE_NAME};


    public RoleTableDef(String tableName) {
        super(tableName);
    }
}
