package com.mybatisflex.test.model.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 *  表定义层。
 *
 * @author james
 * @since 2023-07-29
 */
public class UserOrderTableDef extends TableDef {

    /**
     *
     */
    public static final UserOrderTableDef USER_ORDER = new UserOrderTableDef("tb_user_order");


    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");


    public final QueryColumn ORDER_ID = new QueryColumn(this, "order_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{USER_ID, ORDER_ID};


    public UserOrderTableDef(String tableName) {
        super(tableName);
    }
}
