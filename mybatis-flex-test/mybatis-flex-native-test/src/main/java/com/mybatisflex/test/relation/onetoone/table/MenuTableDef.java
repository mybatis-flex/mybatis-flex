package com.mybatisflex.test.relation.onetoone.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class MenuTableDef extends TableDef {

    public static final MenuTableDef MENU = new MenuTableDef("tb_menu");


    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");




    public MenuTableDef(String tableName) {
        super(tableName);
    }

}
