package com.mybatisflex.coretest.auth;

import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;

import static com.mybatisflex.core.constant.SqlConsts.AND;
import static com.mybatisflex.core.constant.SqlConsts.EQUALS;
import static com.mybatisflex.coretest.table.AccountTableDef.ACCOUNT;

/**
 * 权限处理
 */
public class AuthDialectImpl extends CommonsDialectImpl {

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        queryWrapper.and(ACCOUNT.INSERT_USER_ID.eq(1));
        super.prepareAuth(queryWrapper, operateType);
    }

    @Override
    public void prepareAuth(String schema, String tableName, StringBuilder sql, OperateType operateType) {
        sql.append(AND).append(wrap("insert_user_id")).append(EQUALS).append(1);
        super.prepareAuth(schema, tableName, sql, operateType);
    }

    @Override
    public void prepareAuth(TableInfo tableInfo, StringBuilder sql, OperateType operateType) {
        sql.append(AND).append(wrap("insert_user_id")).append(EQUALS).append(1);
        super.prepareAuth(tableInfo, sql, operateType);
    }
}
