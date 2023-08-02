package com.mybatisflex.test.issue113.def;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class TClassTableDef extends TableDef {
    public static final TClassTableDef TB_CLASS = new TClassTableDef();

    public final QueryColumn ID = new QueryColumn(this, "id");

    public final QueryColumn USER_ID = new QueryColumn(this, "user_id");

    public final QueryColumn CLASS_NAME = new QueryColumn(this, "class_name");

    public TClassTableDef() {
        super("", "tb_class");
    }
}
