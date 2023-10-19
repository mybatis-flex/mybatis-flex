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
package com.mybatisflex.codegen.dialect.impl;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.IDialect;
import com.mybatisflex.codegen.entity.Column;
import com.mybatisflex.codegen.entity.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;

/**
 * Sqlite 方言实现。
 * @author michael
 */
public class SqliteDialect implements IDialect {

    @Override
    public void buildTableColumns(String schemaName, Table table, GlobalConfig globalConfig, DatabaseMetaData dbMeta, Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("pragma table_info(" + table.getName() + ")");

        //cid  name       type     notnull  dflt_value  pk
        //---  ---------  -------  -------  ----------  --
        //0    EventId    INTEGER  0                    1
        //1    EventName           0                    0
        //2    StartDate           0                    0
        //3    EndDate             0                    0

        while (rs.next()) {
            Column column = new Column();
            column.setName(rs.getString(2));

            String type = rs.getString(3);
            column.setPropertyType(type2ClassName(type));

            table.addColumn(column);
        }
    }

    @Override
    public ResultSet getTablesResultSet(DatabaseMetaData dbMeta, Connection conn, String schema, String[] types) throws SQLException {
        return dbMeta.getTables(conn.getCatalog(), schema, null, types);
    }

    private String type2ClassName(String type) {
        int indexOf = type.indexOf("(");
        if (indexOf > 0) {
            type = type.substring(0, indexOf);
        }
        type = type.toLowerCase();
        switch (type) {
            case "integer":
            case "int":
            case "int2":
            case "int8":
            case "tinyint":
            case "smallint":
            case "mediumint":
                return Integer.class.getName();
            case "bigint":
            case "unsigned bigint":
                return BigInteger.class.getName();
            case "character":
            case "varchar":
            case "nchar":
            case "nvarchar":
            case "varying character":
            case "native character":
            case "text":
            case "clob":
                return String.class.getName();
            case "double":
            case "numeric":
            case "real":
                return Double.class.getName();
            case "float":
                return Float.class.getName();
            case "decimal":
                return BigDecimal.class.getName();
            default:
                return String.class.getName();
        }
    }

}
