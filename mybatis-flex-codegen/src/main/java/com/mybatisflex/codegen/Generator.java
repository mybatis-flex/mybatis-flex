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

public class Generator {

    protected DataSource dataSource;
    protected GlobalConfig globalConfig;
    protected IDialect dialect = IDialect.DEFAULT;

    protected Connection conn = null;
    protected DatabaseMetaData dbMeta = null;


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
        try {
            conn = dataSource.getConnection();
            dbMeta = conn.getMetaData();

            List<Table> tables = buildTables();

            for (Table table : tables) {
                Collection<IGenerator> generators = GeneratorFactory.getGenerators();
                for (IGenerator generator : generators) {
                    generator.generate(table, globalConfig);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void buildPrimaryKey(Table table) throws SQLException {
        try (ResultSet rs = dbMeta.getPrimaryKeys(conn.getCatalog(), null, table.getName())) {
            while (rs.next()) {
                String primaryKey = rs.getString("COLUMN_NAME");
                table.addPrimaryKey(primaryKey);
            }
        }
    }


    private List<Table> buildTables() throws SQLException {
        StrategyConfig strategyConfig = globalConfig.getStrategyConfig();
        List<Table> tables = new ArrayList<>();
        try (ResultSet rs = getTablesResultSet()) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!strategyConfig.isSupportGenerate(tableName)) {
                    continue;
                }

                Table table = new Table();
                table.setGlobalConfig(globalConfig);
                table.setTableConfig(strategyConfig.getTableConfig(tableName));

                table.setName(tableName);

                String remarks = rs.getString("REMARKS");
                table.setComment(remarks);


                buildPrimaryKey(table);

                dialect.buildTableColumns(table, globalConfig, dbMeta, conn);

                tables.add(table);
            }
        }
        return tables;
    }


    protected ResultSet getTablesResultSet() throws SQLException {
        if (globalConfig.getStrategyConfig().isGenerateForView()) {
            return dialect.getTablesResultSet(dbMeta, conn, new String[]{"TABLE", "VIEW"});
        } else {
            return dialect.getTablesResultSet(dbMeta, conn, new String[]{"TABLE"});
        }
    }
}
