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
package com.mybatisflex.core.dialect;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableManager;

import java.util.List;

/**
 * @author michael
 */
public interface IDialect {

    String wrap(String keyword);

    String wrapColumnAlias(String keyword);

    default String getRealTable(String table, OperateType operateType) {
        return TableManager.getRealTable(table, operateType);
    }

    default String getRealSchema(String schema, String table, OperateType operateType) {
        return TableManager.getRealSchema(schema, table, operateType);
    }

    String forHint(String hintString);

    String forInsertRow(String schema, String tableName, Row row);

    String forInsertBatchWithFirstRowColumns(String schema, String tableName, List<Row> rows);

    String forDeleteById(String schema, String tableName, String[] primaryKeys);

    String forDeleteBatchByIds(String schema, String tableName, String[] primaryKeys, Object[] ids);

    String forDeleteByQuery(QueryWrapper queryWrapper);

    String forUpdateById(String schema, String tableName, Row row);

    String forUpdateByQuery(QueryWrapper queryWrapper, Row data);

    String forUpdateBatchById(String schema, String tableName, List<Row> rows);

    String forSelectOneById(String schema, String tableName, String[] primaryKeys, Object[] primaryValues);

    String forSelectByQuery(QueryWrapper queryWrapper);

    String buildSelectSql(QueryWrapper queryWrapper);

    String buildNoSelectSql(QueryWrapper queryWrapper);

    String buildDeleteSql(QueryWrapper queryWrapper);

    String buildWhereConditionSql(QueryWrapper queryWrapper);


    //////for entity /////
    String forInsertEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls);

    String forInsertEntityWithPk(TableInfo tableInfo, Object entity, boolean ignoreNulls);

    String forInsertEntityBatch(TableInfo tableInfo, List<?> entities);

    String forDeleteEntityById(TableInfo tableInfo);

    String forDeleteEntityBatchByIds(TableInfo tableInfo, Object[] primaryValues);

    String forDeleteEntityBatchByQuery(TableInfo tableInfo, QueryWrapper queryWrapper);

    String forUpdateEntity(TableInfo tableInfo, Object entity, boolean ignoreNulls);

    String forUpdateEntityByQuery(TableInfo tableInfo, Object entity, boolean ignoreNulls, QueryWrapper queryWrapper);

    String forSelectOneEntityById(TableInfo tableInfo);

    String forSelectEntityListByIds(TableInfo tableInfo, Object[] primaryValues);

    /**
     * 权限处理
     *
     * @param queryWrapper queryWrapper
     * @param operateType  操作类型
     */
    default void prepareAuth(QueryWrapper queryWrapper, OperateType operateType) {
    }

    /**
     * 权限处理
     *
     * @param schema      schema
     * @param tableName   表名
     * @param sql         sql
     * @param operateType 操作类型
     */
    default void prepareAuth(String schema, String tableName, StringBuilder sql, OperateType operateType) {
    }

    /**
     * 权限处理
     *
     * @param tableInfo   tableInfo
     * @param sql         sql
     * @param operateType 操作类型
     */
    default void prepareAuth(TableInfo tableInfo, StringBuilder sql, OperateType operateType) {
    }
}
