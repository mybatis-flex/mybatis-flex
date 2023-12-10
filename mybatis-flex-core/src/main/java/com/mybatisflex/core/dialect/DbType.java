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


public enum DbType {

    /**
     * MYSQL
     */
    MYSQL("mysql", "MySql 数据库"),

    /**
     * MARIADB
     */
    MARIADB("mariadb", "MariaDB 数据库"),

    /**
     * ORACLE
     */
    ORACLE("oracle", "Oracle11g 及以下数据库"),

    /**
     * oracle12c
     */
    ORACLE_12C("oracle12c", "Oracle12c 及以上数据库"),

    /**
     * DB2
     */
    DB2("db2", "DB2 数据库"),
    DB2_1005("db2_1005", "DB2 10.5版本数据库"),

    /**
     * H2
     */
    H2("h2", "H2 数据库"),

    /**
     * HSQL
     */
    HSQL("hsql", "HSQL 数据库"),

    /**
     * SQLITE
     */
    SQLITE("sqlite", "SQLite 数据库"),

    /**
     * POSTGRE
     */
    POSTGRE_SQL("postgresql", "PostgreSQL 数据库"),

    /**
     * SQLSERVER
     */
    SQLSERVER("sqlserver", "SQLServer 数据库"),

    /**
     * SqlServer 2005 数据库
     */
    SQLSERVER_2005("sqlserver_2005", "SQLServer 数据库"),

    /**
     * DM
     */
    DM("dm", "达梦数据库"),

    /**
     * xugu
     */
    XUGU("xugu", "虚谷数据库"),

    /**
     * Kingbase
     */
    KINGBASE_ES("kingbasees", "人大金仓数据库"),

    /**
     * Phoenix
     */
    PHOENIX("phoenix", "Phoenix HBase 数据库"),

    /**
     * Gauss
     */
    GAUSS("gauss", "Gauss 数据库"),

    /**
     * ClickHouse
     */
    CLICK_HOUSE("clickhouse", "clickhouse 数据库"),

    /**
     * GBase
     */
    GBASE("gbase", "南大通用(华库)数据库"),

    /**
     * GBase-8s
     */
    GBASE_8S("gbase-8s", "南大通用数据库 GBase 8s"),

    /**
     * Oscar
     */
    OSCAR("oscar", "神通数据库"),

    /**
     * Sybase
     */
    SYBASE("sybase", "Sybase ASE 数据库"),

    /**
     * OceanBase
     */
    OCEAN_BASE("oceanbase", "OceanBase 数据库"),

    /**
     * Firebird
     */
    FIREBIRD("Firebird", "Firebird 数据库"),

    /**
     * derby
     */
    DERBY("derby", "Derby 数据库"),

    /**
     * HighGo
     */
    HIGH_GO("highgo", "瀚高数据库"),

    /**
     * CUBRID
     */
    CUBRID("cubrid", "CUBRID 数据库"),

    /**
     * GOLDILOCKS
     */
    GOLDILOCKS("goldilocks", "GOLDILOCKS 数据库"),

    /**
     * CSIIDB
     */
    CSIIDB("csiidb", "CSIIDB 数据库"),

    /**
     * CSIIDB
     */
    SAP_HANA("hana", "SAP_HANA 数据库"),

    /**
     * Impala
     */
    IMPALA("impala", "impala 数据库"),

    /**
     * Vertica
     */
    VERTICA("vertica", "vertica数据库"),

    /**
     * 东方国信 xcloud
     */
    XCloud("xcloud", "行云数据库"),

    /**
     * redshift
     */
    REDSHIFT("redshift", "亚马逊 redshift 数据库"),

    /**
     * openGauss
     */
    OPENGAUSS("openGauss", "华为 openGauss 数据库"),

    /**
     * TDengine
     */
    TDENGINE("TDengine", "TDengine 数据库"),

    /**
     * Informix
     */
    INFORMIX("informix", "Informix 数据库"),

    /**
     * sinodb
     */
    SINODB("sinodb", "SinoDB 数据库"),

    /**
     * uxdb
     */
    UXDB("uxdb", "优炫数据库"),

    /**
     * greenplum
     */
    GREENPLUM("greenplum", "greenplum 数据库"),

    /**
     * lealone
     */
    LEALONE("lealone", "lealone 数据库"),

    /**
     * Hive SQL
     */
    HIVE("Hive", "Hive SQL"),

    /**
     * Doris 兼容 Mysql，使用 MySql 驱动和协议
     */
    DORIS("doris", "doris 数据库"),

    /**
     * UNKNOWN DB
     */
    OTHER("other", "其他数据库");

    /**
     * 数据库名称
     */
    private final String name;

    /**
     * 描述
     */
    private final String remarks;


    DbType(String name, String remarks) {
        this.name = name;
        this.remarks = remarks;
    }

    public String getName() {
        return name;
    }
}
