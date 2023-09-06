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


import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.exception.locale.LocalizedFormats;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.regex.Pattern;

/**
 * DbType 解析 工具类
 */
public class DbTypeUtil {

    private DbTypeUtil() {
    }

    /**
     * 获取当前配置的 DbType
     */
    public static DbType getDbType(DataSource dataSource) {
        String jdbcUrl = getJdbcUrl(dataSource);

        if (StringUtil.isNotBlank(jdbcUrl)) {
            return parseDbType(jdbcUrl);
        }

        throw new IllegalStateException("Can not get dataSource jdbcUrl: " + dataSource.getClass().getName());
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
        if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (jdbcUrl.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (jdbcUrl.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (jdbcUrl.contains(":sqlserver2012:")) {
            return DbType.SQLSERVER;
        } else if (jdbcUrl.contains(":sqlserver:") || jdbcUrl.contains(":microsoft:")) {
            return DbType.SQLSERVER_2005;
        } else if (jdbcUrl.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (jdbcUrl.contains(":hsqldb:")) {
            return DbType.HSQL;
        } else if (jdbcUrl.contains(":db2:")) {
            return DbType.DB2;
        } else if (jdbcUrl.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (jdbcUrl.contains(":h2:")) {
            return DbType.H2;
        } else if (isMatchedRegex(":dm\\d*:", jdbcUrl)) {
            return DbType.DM;
        } else if (jdbcUrl.contains(":xugu:")) {
            return DbType.XUGU;
        } else if (isMatchedRegex(":kingbase\\d*:", jdbcUrl)) {
            return DbType.KINGBASE_ES;
        } else if (jdbcUrl.contains(":phoenix:")) {
            return DbType.PHOENIX;
        } else if (jdbcUrl.contains(":zenith:")) {
            return DbType.GAUSS;
        } else if (jdbcUrl.contains(":gbase:")) {
            return DbType.GBASE;
        } else if (jdbcUrl.contains(":gbasedbt-sqli:") || jdbcUrl.contains(":informix-sqli:")) {
            return DbType.GBASE_8S;
        } else if (jdbcUrl.contains(":ch:") || jdbcUrl.contains(":clickhouse:")) {
            return DbType.CLICK_HOUSE;
        } else if (jdbcUrl.contains(":oscar:")) {
            return DbType.OSCAR;
        } else if (jdbcUrl.contains(":sybase:")) {
            return DbType.SYBASE;
        } else if (jdbcUrl.contains(":oceanbase:")) {
            return DbType.OCEAN_BASE;
        } else if (jdbcUrl.contains(":highgo:")) {
            return DbType.HIGH_GO;
        } else if (jdbcUrl.contains(":cubrid:")) {
            return DbType.CUBRID;
        } else if (jdbcUrl.contains(":goldilocks:")) {
            return DbType.GOLDILOCKS;
        } else if (jdbcUrl.contains(":csiidb:")) {
            return DbType.CSIIDB;
        } else if (jdbcUrl.contains(":sap:")) {
            return DbType.SAP_HANA;
        } else if (jdbcUrl.contains(":impala:")) {
            return DbType.IMPALA;
        } else if (jdbcUrl.contains(":vertica:")) {
            return DbType.VERTICA;
        } else if (jdbcUrl.contains(":xcloud:")) {
            return DbType.XCloud;
        } else if (jdbcUrl.contains(":firebirdsql:")) {
            return DbType.FIREBIRD;
        } else if (jdbcUrl.contains(":redshift:")) {
            return DbType.REDSHIFT;
        } else if (jdbcUrl.contains(":opengauss:")) {
            return DbType.OPENGAUSS;
        } else if (jdbcUrl.contains(":taos:") || jdbcUrl.contains(":taos-rs:")) {
            return DbType.TDENGINE;
        } else if (jdbcUrl.contains(":informix")) {
            return DbType.INFORMIX;
        } else if (jdbcUrl.contains(":sinodb")) {
            return DbType.SINODB;
        } else if (jdbcUrl.contains(":uxdb:")) {
            return DbType.UXDB;
        } else if (jdbcUrl.contains(":greenplum:")) {
            return DbType.GREENPLUM;
        } else if (jdbcUrl.contains(":lealone:")) {
            return DbType.LEALONE;
        }  else if (jdbcUrl.contains(":hive2:")) {
            return DbType.HIVE;
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
