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
import com.mybatisflex.codegen.dialect.impl.DefaultJdbcDialect;
import com.mybatisflex.codegen.dialect.impl.MySqlJdbcDialect;
import com.mybatisflex.codegen.dialect.impl.OracleJdbcDialect;
import com.mybatisflex.codegen.dialect.impl.SqliteDialect;
import com.mybatisflex.codegen.entity.Table;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 方言接口。
 * @author michael
 * @author Suomm
 */
public interface IDialect {

    /**
     * 默认方言。
     */
    IDialect DEFAULT = new DefaultJdbcDialect();

    /**
     * MySQL 方言。
     */
    IDialect MYSQL = new MySqlJdbcDialect();

    /**
     * Oracle 方言。
     */
    IDialect ORACLE = new OracleJdbcDialect();

    /**
     * Sqlite 方言。
     */
    IDialect SQLITE = new SqliteDialect();

    /**
     * 构建表和列的信息。
     *
     * @param schemaName
     * @param table        存入的表对象
     * @param globalConfig 全局配置
     * @param dbMeta       数据库元数据
     * @param conn         连接
     * @throws SQLException 发生 SQL 异常时抛出
     */
    void buildTableColumns(String schemaName, Table table, GlobalConfig globalConfig, DatabaseMetaData dbMeta, Connection conn) throws SQLException;

    /**
     * 获取表的描述信息。
     *
     * @param dbMeta 数据库元数据
     * @param conn   连接
     * @param schema 模式
     * @param types  结果集类型
     * @return 结果集
     * @throws SQLException 发生 SQL 异常时抛出
     */
    ResultSet getTablesResultSet(DatabaseMetaData dbMeta, Connection conn, String schema, String[] types) throws SQLException;

}
