/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mybatisflex.coretest.auth;

import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;

import java.util.List;

import static com.mybatisflex.core.constant.SqlConsts.AND;
import static com.mybatisflex.core.constant.SqlConsts.EQUALS;
import static com.mybatisflex.coretest.auth.table.ProjectTableDef.PROJECT;

/**
 * 权限处理
 */
public class AuthDialectImpl extends CommonsDialectImpl {

    @Override
    public void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables == null || queryTables.isEmpty()) {
            return;
        }
        for (QueryTable queryTable : queryTables) {
            if (PROJECT.getName().equals(queryTable.getName())) {
                queryWrapper.and(PROJECT.INSERT_USER_ID.eq(1));
            }
        }
        super.prepareAuth(queryWrapper, operateType);
    }

    @Override
    public void prepareAuth(String schema, String tableName, StringBuilder sql, OperateType operateType) {
        if (PROJECT.getName().equals(tableName)) {
            sql.append(AND).append(wrap("insert_user_id")).append(EQUALS).append(1);
        }
        super.prepareAuth(schema, tableName, sql, operateType);
    }

    @Override
    public void prepareAuth(TableInfo tableInfo, StringBuilder sql, OperateType operateType) {
        if (PROJECT.getName().equals(tableInfo.getTableName())) {
            sql.append(AND).append(wrap("insert_user_id")).append(EQUALS).append(1);
        }
        super.prepareAuth(tableInfo, sql, operateType);
    }
}
