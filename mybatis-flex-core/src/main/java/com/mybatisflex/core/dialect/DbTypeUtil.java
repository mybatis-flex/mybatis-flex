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
package com.mybatisflex.core.dialect;


import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.exception.locale.LocalizedFormats;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.logging.LogFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

/**
 * DbType 解析 工具类
 */
public class DbTypeUtil {

    private DbTypeUtil() {
    }

    /**
     * 获取当前数据库类型
     * <p>首先从全局配置中获取数据库类型，如果全局配置中未设置，则尝试从方言工厂中获取线程局部变量设置的数据库类型
     *
     * @return 当前数据库类型，可能为null
     */
    public static DbType getCurrentDbType() {
        DbType dbType = FlexGlobalConfig.getDefaultConfig().getDbType();
        if (dbType == null) {
            dbType = DialectFactory.getHintDbType();
        }
        return dbType;
    }

    /**
     * 获取当前配置的 DbType
     */
    public static DbType getDbType(DataSource dataSource) {
        String jdbcUrl = getJdbcUrl(dataSource);
        if (StringUtil.hasText(jdbcUrl)) {
            //FIX [Bug]: sqlserver2022下方言识别不对，手动set也无效  https://gitee.com/mybatis-flex/mybatis-flex/issues/IBIHW3
            if (jdbcUrl.contains(":sqlserver:")) {
                DbType sqlserverDbType = getSqlserverDbType(dataSource);
                if (sqlserverDbType != null) {
                    return sqlserverDbType;
                }
            }
            return parseDbType(jdbcUrl);
        }

        throw new IllegalStateException("Can not get dataSource jdbcUrl: " + dataSource.getClass().getName());
    }

    /**
     * 通过数据源获取 SQLserver 版本
     *
     * @return DbType
     */
    private static DbType getSqlserverDbType(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT @@VERSION");
                ResultSet resultSet = preparedStatement.executeQuery()) {
            //SELECT @@VERSION 查询返回信息：
            /*
            Microsoft SQL Server 2019 (RTM) - 15.0.2000.5 (X64)
            Sep 24 2019 13:48:23
            Copyright (C) 2019 Microsoft Corporation
            Enterprise Edition (64-bit) on Windows Server 2019 Datacenter 10.0 <X64> (Build 17763: ) (Hypervisor)
            */
            if (resultSet.next()) {
                String version = resultSet.getString(1);
                if (StringUtil.hasText(version)) {
                    String year = version.substring(21, 25);
                    if (StringUtil.hasText(year) && year.compareTo("2008") <= 0) {
                        return DbType.SQLSERVER_2005;
                    }
                }
            }
            return DbType.SQLSERVER;
        } catch (Exception e) {
            LogFactory.getLog(DbTypeUtil.class).warn("Failed to get SQLServer version. parse by url. " + e);
            return null;
        }
    }

    /**
     * 通过数据源中获取 jdbc 的 url 配置
     * 符合 HikariCP, druid, c3p0, DBCP, beecp 数据源框架 以及 MyBatis UnpooledDataSource 的获取规则
     * UnpooledDataSource 参考 @{@link UnpooledDataSource#getUrl()}
     *
     * @return jdbc url 配置
     */
    public static String getJdbcUrl(DataSource dataSource) {
        String[] methodNames = new String[]{"getUrl", "getJdbcUrl"};
        for (String methodName : methodNames) {
            try {
                Method method = dataSource.getClass().getMethod(methodName);
                return (String) method.invoke(dataSource);
            } catch (Exception e) {
                //ignore
            }
        }

        try (Connection connection = dataSource.getConnection()) {
            return connection.getMetaData().getURL();
        } catch (Exception e) {
            throw FlexExceptions.wrap(e, LocalizedFormats.DATASOURCE_JDBC_URL);
        }
    }


    /**
     * 参考 druid  和 MyBatis-plus 的 JdbcUtils
     * {@link com.alibaba.druid.util.JdbcUtils#getDbType(String, String)}
     * {@link com.baomidou.mybatisplus.extension.toolkit.JdbcUtils#getDbType(String)}
     *
     * @param jdbcUrl jdbcURL
     * @return 返回数据库类型
     */
    public static DbType parseDbType(String jdbcUrl) {
        jdbcUrl = jdbcUrl.toLowerCase();
        if (jdbcUrl.contains(":ch:") || jdbcUrl.contains(":clickhouse:")) {
            return DbType.CLICK_HOUSE;
        } else if (jdbcUrl.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (jdbcUrl.contains(":csiidb:")) {
            return DbType.CSIIDB;
        } else if (jdbcUrl.contains(":cubrid:")) {
            return DbType.CUBRID;
        } else if (jdbcUrl.contains(":db2:")) {
            return DbType.DB2;
        } else if (jdbcUrl.contains(":derby:")) {
            return DbType.DERBY;
        } else if (isMatchedRegex(":dm\\d*:", jdbcUrl)) {
            return DbType.DM;
        } else if (jdbcUrl.contains(":duckdb:")) {
            return DbType.DUCKDB;
        } else if (jdbcUrl.contains(":firebirdsql:")) {
            return DbType.FIREBIRD;
        } else if (jdbcUrl.contains(":gaussdb:") || jdbcUrl.contains(":zenith:")) {
            return DbType.GAUSS;
        } else if (jdbcUrl.contains(":gbase:")) {
            return DbType.GBASE;
        } else if (jdbcUrl.contains(":gbase8c:")) {
            return DbType.GBASE_8C;
        } else if (jdbcUrl.contains(":gbase8s-pg:")) {
            return DbType.GBASE_8S_PG;
        } else if (jdbcUrl.contains(":gbasedbt-sqli:") || jdbcUrl.contains(":informix-sqli:")) {
            return DbType.GBASE_8S;
        } else if (jdbcUrl.contains(":goldendb:")) {
            return DbType.GOLDENDB;
        } else if (jdbcUrl.contains(":goldilocks:")) {
            return DbType.GOLDILOCKS;
        } else if (jdbcUrl.contains(":greenplum:")) {
            return DbType.GREENPLUM;
        } else if (jdbcUrl.contains(":h2:")) {
            return DbType.H2;
        } else if (jdbcUrl.contains(":highgo:")) {
            return DbType.HIGH_GO;
        } else if (jdbcUrl.contains(":hive2:") || jdbcUrl.contains(":inceptor2:")) {
            return DbType.HIVE;
        } else if (jdbcUrl.contains(":hsqldb:")) {
            return DbType.HSQL;
        } else if (jdbcUrl.contains(":impala:")) {
            return DbType.IMPALA;
        } else if (jdbcUrl.contains(":informix")) {
            return DbType.INFORMIX;
        } else if (isMatchedRegex(":kingbase\\d*:", jdbcUrl)) {
            return DbType.KINGBASE_ES;
        } else if (jdbcUrl.contains(":lealone:")) {
            return DbType.LEALONE;
        } else if (jdbcUrl.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (jdbcUrl.contains(":mysql:")) {
            return DbType.MYSQL;
        } else if (jdbcUrl.contains(":oceanbase:")) {
            return DbType.OCEAN_BASE;
        } else if (jdbcUrl.contains(":opengauss:")) {
            return DbType.OPENGAUSS;
        } else if (jdbcUrl.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (jdbcUrl.contains(":oscar:")) {
            return DbType.OSCAR;
        } else if (jdbcUrl.contains(":phoenix:")) {
            return DbType.PHOENIX;
        } else if (jdbcUrl.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (jdbcUrl.contains(":presto:")) {
            return DbType.PRESTO;
        } else if (jdbcUrl.contains(":redshift:")) {
            return DbType.REDSHIFT;
        } else if (jdbcUrl.contains(":sap:")) {
            return DbType.SAP_HANA;
        } else if (jdbcUrl.contains(":sinodb")) {
            return DbType.SINODB;
        } else if (jdbcUrl.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (jdbcUrl.contains(":sqlserver:")) {
            return DbType.SQLSERVER_2005;
        } else if (jdbcUrl.contains(":sqlserver2012:")) {
            return DbType.SQLSERVER;
        } else if (jdbcUrl.contains(":sundb:")) {
            return DbType.SUNDB;
        } else if (jdbcUrl.contains(":sybase:")) {
            return DbType.SYBASE;
        } else if (jdbcUrl.contains(":taos:") || jdbcUrl.contains(":taos-rs:")) {
            return DbType.TDENGINE;
        } else if (jdbcUrl.contains(":trino:")) {
            return DbType.TRINO;
        } else if (jdbcUrl.contains(":uxdb:")) {
            return DbType.UXDB;
        } else if (jdbcUrl.contains(":vastbase:")) {
            return DbType.VASTBASE;
        } else if (jdbcUrl.contains(":vertica:")) {
            return DbType.VERTICA;
        } else if (jdbcUrl.contains(":xcloud:")) {
            return DbType.XCloud;
        } else if (jdbcUrl.contains(":xugu:")) {
            return DbType.XUGU;
        } else if (jdbcUrl.contains(":yasdb:")) {
            return DbType.YASDB;
        } else {
            return DbType.OTHER;
        }
    }

    /**
     * 正则匹配，验证成功返回 true，验证失败返回 false
     */
    public static boolean isMatchedRegex(String regex, String jdbcUrl) {
        if (null == jdbcUrl) {
            return false;
        }
        return Pattern.compile(regex).matcher(jdbcUrl).find();
    }

}
