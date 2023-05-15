/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.dialect;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.table.TableInfo;

import java.util.List;

public interface IDialect {

    String wrap(String keyword);

    String forInsertRow(String tableName, Row row);

    String forInsertBatchWithFirstRowColumns(String tableName, List<Row> rows);

    String forDeleteById(String tableName, String[] primaryKeys);

    String forDeleteBatchByIds(String tableName, String[] primaryKeys, Object[] ids);

    String forDeleteByQuery(QueryWrapper queryWrapper);

    String forUpdateById(String tableName, Row row);

    String forUpdateByQuery(QueryWrapper queryWrapper, Row data);

    String forUpdateBatchById(String tableName, List<Row> rows);

    String forSelectOneById(String tableName, String[] primaryKeys, Object[] primaryValues);

    String forSelectByQuery(QueryWrapper queryWrapper);

    String buildSelectSql(QueryWrapper queryWrapper);

    String buildDeleteSql(QueryWrapper queryWrapper);

    String buildWhereConditionSql(QueryWrapper queryWrapper);



    //////for entity /////
    String forInsertEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls);

    String forInsertEntityBatch(TableInfo tableInfo, List<Object> entities);

    String forDeleteEntityById(TableInfo tableInfo);

    String forDeleteEntityBatchByIds(TableInfo tableInfo, Object[] primaryValues);

    String forDeleteEntityBatchByQuery(TableInfo tableInfo, QueryWrapper queryWrapper);

    String forUpdateEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls);

    String forUpdateEntityByQuery(TableInfo tableInfo, Object entity, boolean ignoreNulls, QueryWrapper queryWrapper);

    String forSelectOneEntityById(TableInfo tableInfo);

    String forSelectEntityListByIds(TableInfo tableInfo, Object[] primaryValues);

}
