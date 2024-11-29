/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
import com.mybatisflex.core.update.UpdateWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mybatisflex.core.constant.SqlConsts.EQUALS;
import static com.mybatisflex.core.constant.SqlConsts.SINGLE_QUOTE;

/**
 * 逻辑删除处理器抽象类。
 *
 * @author 王帅
 * @since 2023-06-20
 */
public abstract class AbstractLogicDeleteProcessor implements LogicDeleteProcessor {

    @Override
    public String buildLogicNormalCondition(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        return dialect.wrap(logicColumn) + EQUALS + prepareValue(getLogicNormalValue());
    }

    @Override
    public String buildLogicDeletedSet(String logicColumn, TableInfo tableInfo, IDialect dialect) {
        String sql = dialect.wrap(logicColumn) + EQUALS + prepareValue(getLogicDeletedValue());
        return invokeOnLogicDeleteListener(sql, tableInfo, dialect);
    }

    @Override
    public void buildQueryCondition(QueryWrapper queryWrapper, TableInfo tableInfo, String joinTableAlias) {
        QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName()).as(joinTableAlias);
        QueryColumn queryColumn = new QueryColumn(queryTable, tableInfo.getLogicDeleteColumn());
        queryWrapper.and(queryColumn.eq(getLogicNormalValue()));
    }

    protected static Object prepareValue(Object value) {
        if (value instanceof Number || value instanceof Boolean) {
            return value;
        }
        return SINGLE_QUOTE + value + SINGLE_QUOTE;
    }

    /**
     * 调用逻辑删除监听器，返回最终的 SQL
     */
    protected String invokeOnLogicDeleteListener(String sql, TableInfo tableInfo, IDialect dialect) {
        // 获取逻辑删除的实体代理对象，并执行监听器
        UpdateWrapper<?> entity = UpdateWrapper.of(tableInfo.getEntityClass());
        tableInfo.invokeOnLogicDeleteListener(entity);
        // 获取需要更新的列
        Map<String, Object> updates = entity.getUpdates();
        if (updates.isEmpty()) {
            return sql;
        }
        // 构建 SET 语句
        List<String> setColumnsSql = new ArrayList<>(updates.size() + 1);
        // 逻辑删除列
        setColumnsSql.add(sql);
        // 其他列
        updates.forEach((k, v) -> {
            setColumnsSql.add(dialect.wrap(tableInfo.getColumnByProperty(k)) + EQUALS + prepareValue(v));
        });
        return String.join(",", setColumnsSql);
    }
}


