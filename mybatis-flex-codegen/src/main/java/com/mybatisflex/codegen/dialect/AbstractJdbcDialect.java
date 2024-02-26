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
package com.mybatisflex.codegen.dialect;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.entity.Column;
import com.mybatisflex.codegen.entity.Table;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认方言抽象类。
 *
 * @author michael
 */
public abstract class AbstractJdbcDialect implements IDialect {

    @Override
    public void buildTableColumns(String schemaName, Table table, GlobalConfig globalConfig, DatabaseMetaData dbMeta, Connection conn) throws SQLException {
        Map<String, String> columnRemarks = buildColumnRemarks(schemaName, table, dbMeta, conn);

        String sql = forBuildColumnsSql(table.getSchema(), table.getName());
        try (Statement stm = conn.createStatement(); ResultSet rs = stm.executeQuery(sql)) {

            ResultSetMetaData columnMetaData = rs.getMetaData();
            int columnCount = columnMetaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                Column column = new Column();
                column.setName(columnMetaData.getColumnName(i));

                column.setRawType(columnMetaData.getColumnTypeName(i));
                column.setRawLength(columnMetaData.getColumnDisplaySize(i));

                column.setAutoIncrement(columnMetaData.isAutoIncrement(i));

                column.setNullable(columnMetaData.isNullable(i));
                //注释
                column.setComment(columnRemarks.get(column.getName()));

                String jdbcType = columnMetaData.getColumnClassName(i);
                column.setPropertyType(JdbcTypeMapping.getType(jdbcType, table, column));

                table.addColumn(column);
            }
        }
    }

    private Map<String, String> buildColumnRemarks(String schemaName, Table table, DatabaseMetaData dbMeta, Connection conn) {
        Map<String, String> columnRemarks = new HashMap<>();
        try (ResultSet colRs = forRemarks(schemaName, table, dbMeta, conn)) {
            while (colRs.next()) {
                columnRemarks.put(colRs.getString("COLUMN_NAME"), colRs.getString("REMARKS"));
            }
        } catch (Exception e) {
            System.err.println("无法获取字段的备注内容：" + e.getMessage());
        }
        return columnRemarks;
    }


    @Override
    public ResultSet getTablesResultSet(DatabaseMetaData dbMeta, Connection conn, String schema, String[] types) throws SQLException {
        return dbMeta.getTables(conn.getCatalog(), schema, null, types);
    }


    /**
     * 构建 remarks 的 ResultSet
     *
     * @param schemaName
     * @param table
     * @param dbMeta
     * @param conn
     * @return
     * @throws SQLException
     */
    protected ResultSet forRemarks(String schemaName, Table table, DatabaseMetaData dbMeta, Connection conn) throws SQLException {
        return dbMeta.getColumns(conn.getCatalog(), null, table.getName(), null);
    }


    /**
     * 构建查询所有数据的 SQL 语句。
     *
     * @param schema    模式
     * @param tableName 表名
     * @return 全量查询 SQL 语句
     */
    protected abstract String forBuildColumnsSql(String schema, String tableName);


}
