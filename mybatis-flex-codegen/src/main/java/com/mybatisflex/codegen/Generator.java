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
import com.mybatisflex.codegen.dialect.IDialect;
import com.mybatisflex.codegen.entity.Column;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.template.ITemplate;

import javax.sql.DataSource;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Generator {

    protected DataSource dataSource;
    protected GlobalConfig globalConfig;
    protected IDialect dialect = IDialect.MYSQL;

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

            ITemplate templateEngine = globalConfig.getTemplateEngine();
            for (Table table : tables) {

                String entityPackagePath = globalConfig.getEntityPackage().replace(".", "/");
                File entityJavaFile = new File(globalConfig.getSourceDir(), entityPackagePath + "/" + table.buildEntityClassName() + ".java");
                if (!entityJavaFile.getParentFile().exists()) {
                    if (!entityJavaFile.getParentFile().mkdirs()) {
                        throw new IllegalStateException("Can not mkdirs by dir: " + entityJavaFile.getParentFile());
                    }
                }

                templateEngine.generateEntity(globalConfig, table, entityJavaFile);


                if (globalConfig.isMapperGenerateEnable()) {
                    String mapperPackagePath = globalConfig.getMapperPackage().replace(".", "/");
                    File mapperJavaFile = new File(globalConfig.getSourceDir(), mapperPackagePath + "/" + table.buildEntityClassName() + "Mapper.java");
                    if (!mapperJavaFile.getParentFile().exists()) {
                        if (!mapperJavaFile.getParentFile().mkdirs()) {
                            throw new IllegalStateException("Can not mkdirs by dir: " + mapperJavaFile.getParentFile());
                        }
                    }
                    templateEngine.generateMapper(globalConfig, table, mapperJavaFile);
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


    private void buildTableColumns(Table table) throws SQLException {
        Map<String, String> columnRemarks = buildColumnRemarks(table);

        String sql = dialect.forBuildColumns(table.getName());
        try (Statement stm = conn.createStatement(); ResultSet rs = stm.executeQuery(sql)) {

            ResultSetMetaData columnMetaData = rs.getMetaData();
            int columnCount = columnMetaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                Column column = new Column();
                column.setName(columnMetaData.getColumnName(i));
                column.setPropertyType(columnMetaData.getColumnClassName(i));
                column.setAutoIncrement(columnMetaData.isAutoIncrement(i));

                //主键
                if (table.getPrimaryKeys() != null && table.getPrimaryKeys().contains(column.getName())) {
                    column.setPrimaryKey(true);
                }

                //注释
                column.setRemarks(columnRemarks.get(column.getName()));

                column.setColumnConfig(globalConfig.getColumnConfig(table.getName(), column.getName()));
                table.addColumn(column);
            }
        }
    }


    private Map<String, String> buildColumnRemarks(Table table) throws SQLException {
        Map<String, String> columnRemarks = new HashMap<>();
        ResultSet colRs = null;
        try {
            colRs = dbMeta.getColumns(conn.getCatalog(), null, table.getName(), null);
            while (colRs.next()) {
                columnRemarks.put(colRs.getString("COLUMN_NAME"), colRs.getString("REMARKS"));
            }
        } catch (Exception e) {
            System.err.println("无法获取字段的备注内容：" + e.getMessage());
        } finally {
            if (colRs != null) {
                colRs.close();
            }
        }
        return columnRemarks;
    }


    private List<Table> buildTables() throws SQLException {
        List<Table> tables = new ArrayList<>();
        try (ResultSet rs = getTablesResultSet()) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!globalConfig.isSupportGenerate(tableName)) {
                    continue;
                }

                Table table = new Table();
                table.setGlobalConfig(globalConfig);
                table.setTableConfig(globalConfig.getTableConfig(tableName));

                table.setName(tableName);

                String remarks = rs.getString("REMARKS");
                table.setRemarks(remarks);


                buildPrimaryKey(table);
                buildTableColumns(table);

                tables.add(table);
            }
        }
        return tables;
    }


    protected ResultSet getTablesResultSet() throws SQLException {
        if (globalConfig.isGenerateForView()) {
            return dialect.getTablesResultSet(dbMeta, conn, new String[]{"TABLE", "VIEW"});
        } else {
            return dialect.getTablesResultSet(dbMeta, conn, new String[]{"TABLE"});
        }
    }
}
