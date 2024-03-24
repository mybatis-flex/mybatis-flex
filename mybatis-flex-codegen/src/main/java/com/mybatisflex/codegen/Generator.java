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
package com.mybatisflex.codegen;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.StrategyConfig;
import com.mybatisflex.codegen.dialect.IDialect;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.GeneratorFactory;
import com.mybatisflex.codegen.generator.IGenerator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 代码生成器。
 *
 * @author michael
 */
public class Generator {

    protected DataSource dataSource;
    protected GlobalConfig globalConfig;
    protected IDialect dialect = IDialect.DEFAULT;

    public Generator(DataSource dataSource, GlobalConfig globalConfig) {
        this.dataSource = dataSource;
        this.globalConfig = globalConfig;
    }

    public Generator(DataSource dataSource, GlobalConfig globalConfig, IDialect dialect) {
        this.dataSource = dataSource;
        this.globalConfig = globalConfig;
        this.dialect = dialect;
    }

    public void generate() {
        generate(getTables());
    }

    public void generate(List<Table> tables) {
        if (tables == null || tables.isEmpty()) {
            System.err.printf("table %s not found.%n", globalConfig.getGenerateTables());
            return;
        } else {
            System.out.printf("find tables: %s%n", tables.stream().map(Table::getName).collect(Collectors.toSet()));
        }

        for (Table table : tables) {
            Collection<IGenerator> generators = GeneratorFactory.getGenerators();
            for (IGenerator generator : generators) {
                generator.generate(table, globalConfig);
            }
        }
        System.out.println("Code is generated successfully.");
    }


    public List<Table> getTables() {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData dbMeta = conn.getMetaData();
            return buildTables(dbMeta, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void buildPrimaryKey(DatabaseMetaData dbMeta, Connection conn, Table table) throws SQLException {
        try (ResultSet rs = dbMeta.getPrimaryKeys(conn.getCatalog(), null, table.getName())) {
            while (rs.next()) {
                String primaryKey = rs.getString("COLUMN_NAME");
                table.addPrimaryKey(primaryKey);
            }
        }
    }

    protected List<Table> buildTables(DatabaseMetaData dbMeta, Connection conn) throws SQLException {
        StrategyConfig strategyConfig = globalConfig.getStrategyConfig();
        String schemaName = strategyConfig.getGenerateSchema();
        List<Table> tables = new ArrayList<>();
        try (ResultSet rs = getTablesResultSet(dbMeta, conn, schemaName)) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!strategyConfig.isSupportGenerate(tableName)) {
                    continue;
                }

                Table table = new Table();
                table.setGlobalConfig(globalConfig);
                table.setTableConfig(strategyConfig.getTableConfig(tableName));
                table.setEntityConfig(globalConfig.getEntityConfig());

                table.setSchema(schemaName);
                table.setName(tableName);

                String remarks = rs.getString("REMARKS");
                table.setComment(remarks);


                buildPrimaryKey(dbMeta, conn, table);

                dialect.buildTableColumns(schemaName, table, globalConfig, dbMeta, conn);

                tables.add(table);
            }
        }
        return tables;
    }


    protected ResultSet getTablesResultSet(DatabaseMetaData dbMeta, Connection conn, String schema) throws SQLException {
        if (globalConfig.getStrategyConfig().isGenerateForView()) {
            return dialect.getTablesResultSet(dbMeta, conn, schema, new String[]{"TABLE", "VIEW"});
        } else {
            return dialect.getTablesResultSet(dbMeta, conn, schema, new String[]{"TABLE"});
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Generator setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public Generator setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }

    public IDialect getDialect() {
        return dialect;
    }

    public Generator setDialect(IDialect dialect) {
        this.dialect = dialect;
        return this;
    }

}
