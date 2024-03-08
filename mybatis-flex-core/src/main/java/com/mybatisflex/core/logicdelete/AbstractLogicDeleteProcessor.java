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
package com.mybatisflex.core.logicdelete;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.query.*;
import com.mybatisflex.core.table.TableInfo;

import static com.mybatisflex.core.constant.SqlConsts.EQUALS;

/**
 * 逻辑删除处理器抽象类。
 *
 * @author 王帅
 * @since 2023-06-20
 */
public abstract class AbstractLogicDeleteProcessor implements LogicDeleteProcessor {

    @Override
    public String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        return dialect.wrap(logicColumn) + EQUALS + getLogicNormalValue();
    }

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        return dialect.wrap(logicColumn) + EQUALS + getLogicDeletedValue();
    }

    @Override
    public void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo, String joinTableAlias) {
        QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName()).as(joinTableAlias);
        QueryColumn queryColumn = new QueryColumn(queryTable, tableInfo.getLogicDeleteColumn());
        queryWrapper.and(queryColumn.eq(getLogicNormalValue()));
    }

}


