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
package com.mybatisflex.core.mybatis;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DbTypeUtil;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

public class FlexSqlSessionFactoryBuilder extends SqlSessionFactoryBuilder {

    @Override
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try {
            FlexXMLConfigBuilder parser = new FlexXMLConfigBuilder(inputStream, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }


    @Override
    public SqlSessionFactory build(Configuration configuration) {
        if (!FlexConfiguration.class.isAssignableFrom(configuration.getClass())) {
            throw FlexExceptions.wrap("only support FlexMybatisConfiguration.");
        }

        SqlSessionFactory sessionFactory = super.build(configuration);

        DbType dbType = getDbType(configuration);

        //设置全局配置的 sessionFactory 和 dbType
        initGlobalConfig(configuration, sessionFactory, dbType);

        return sessionFactory;
    }


    /**
     * 设置全局配置
     *
     * @param config
     * @param sessionFactory
     */
    private void initGlobalConfig(Configuration config, SqlSessionFactory sessionFactory, DbType dbType) {
        FlexGlobalConfig flexGlobalConfig = new FlexGlobalConfig();
        flexGlobalConfig.setSqlSessionFactory(sessionFactory);
        flexGlobalConfig.setDbType(dbType);
        flexGlobalConfig.setConfiguration(config);

        String environmentId = config.getEnvironment().getId();
        FlexGlobalConfig.setConfig(environmentId, flexGlobalConfig);
    }


    /**
     * 获取当前配置的 DbType
     */
    private DbType getDbType(Configuration configuration) {
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        String jdbcUrl = getJdbcUrl(dataSource);
        if (StringUtil.isNotBlank(jdbcUrl)){
            return DbTypeUtil.parseDbType(jdbcUrl);
        }

        if ("org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory$EmbeddedDataSourceProxy"
                .equals(dataSource.getClass().getName())){
            return DbType.H2;
        }

        return null;
    }

    /**
     * 通过数据源中获取 jdbc 的 url 配置
     * 符合 HikariCP, druid, c3p0, DBCP, beecp 数据源框架 以及 MyBatis UnpooledDataSource 的获取规则
     * UnpooledDataSource 参考 @{@link UnpooledDataSource#getUrl()}
     * TODO: 2023/2/18 可能极个别特殊的数据源无法获取 JDBC 配置的 URL
     *
     * @return jdbc url 配置
     */
    private String getJdbcUrl(DataSource dataSource) {
        String[] methodNames = new String[]{"getUrl", "getJdbcUrl"};
        for (String methodName : methodNames) {
            try {
                Method method = dataSource.getClass().getMethod(methodName);
                return (String) method.invoke(dataSource);
            } catch (Exception e) {
                //ignore
            }
        }
        return null;
    }
}
