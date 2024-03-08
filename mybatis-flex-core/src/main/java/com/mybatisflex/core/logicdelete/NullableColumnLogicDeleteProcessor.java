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

/**
 * 数据列默认值为 {@code null} 值的逻辑删除处理器。
 *
 * @author 王帅
 * @since 2023-07-30
 */
public abstract class NullableColumnLogicDeleteProcessor extends AbstractLogicDeleteProcessor {

    @Override
    public String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        return dialect.wrap(logicColumn) + " IS NULL";
    }

    @Override
    public void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo, String joinTableAlias) {
        QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName()).as(joinTableAlias);
        QueryColumn queryColumn = new QueryColumn(queryTable, tableInfo.getLogicDeleteColumn());
        queryWrapper.and(queryColumn.isNull());
    }

    /**
     * 逻辑删除字段值为 {@code null} 表示数据未删除。
     */
    @Override
    public Object getLogicNormalValue() {
        return null;
    }

}
