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

package com.mybatisflex.core.logicdelete.impl;

import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.logicdelete.AbstractLogicDeleteProcessor;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableInfo;

/**
 * {@link java.time.LocalDateTime} 类型的属性对应的逻辑删除处理器。
 *
 * @author 王帅
 * @since 2023-06-20
 */
public class DateTimeLogicDeleteProcessor extends AbstractLogicDeleteProcessor {

    @Override
    public String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        return dialect.wrap(logicColumn) + " IS NULL";
    }

    @Override
    public void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo) {
        QueryColumn queryColumn = new QueryColumn(tableInfo.getSchema(), tableInfo.getTableName(), tableInfo.getLogicDeleteColumn());
        queryWrapper.and(queryColumn.isNull());
    }

    /**
     * 逻辑删除字段值为 {@code null} 表示数据未删除。
     */
    @Override
    public Object getLogicNormalValue() {
        return null;
    }

    /**
     * 逻辑删除字段值为 {@code NOW()} 表示数据删除，并记录删除时间。
     */
    @Override
    public Object getLogicDeletedValue() {
        return "NOW()";
    }

}
