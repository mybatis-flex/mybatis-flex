package com.mybatisflex.test.issue113.def;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class TUserTableDef extends TableDef {
    public static final TUserTableDef TB_USER = new TUserTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn NAME = new QueryColumn(this, "name");

    public TUserTableDef() {
        super("", "tb_user");
    }
}
