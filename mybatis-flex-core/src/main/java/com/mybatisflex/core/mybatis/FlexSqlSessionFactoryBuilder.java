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
package com.mybatisflex.core.mybatis;

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DbTypeUtil;
import com.mybatisflex.core.exception.FlexExceptions;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

public class FlexSqlSessionFactoryBuilder extends SqlSessionFactoryBuilder {

    @Override
    public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
        try {
            // 需要 mybatis v3.5.13+
            // https://github.com/mybatis/mybatis-3/commit/d7826d71a7005a8b4d4e0e7a020db0f6c7e253a4
            XMLConfigBuilder parser = new XMLConfigBuilder(FlexConfiguration.class, reader, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", e);
        } finally {
            ErrorContext.instance().reset();
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                // Intentionally ignore. Prefer previous error.
            }
        }
    }


    @Override
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try {
            // 需要 mybatis v3.5.13+
            // https://github.com/mybatis/mybatis-3/commit/d7826d71a7005a8b4d4e0e7a020db0f6c7e253a4
            XMLConfigBuilder parser = new XMLConfigBuilder(FlexConfiguration.class, inputStream, environment, properties);
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
        DbType dbType = DbTypeUtil.getDbType(configuration.getEnvironment().getDataSource());

        //设置全局配置的 sessionFactory 和 dbType
        initGlobalConfig(configuration, sessionFactory, dbType);

        printBanner();

        return sessionFactory;
    }


    private void printBanner() {
        if (!FlexGlobalConfig.getDefaultConfig().isPrintBanner()) {
            return;
        }
        String banner = "  __  __       _           _   _       _____ _           \n" +
            " |  \\/  |_   _| |__   __ _| |_(_)___  |  ___| | _____  __\n" +
            " | |\\/| | | | | '_ \\ / _` | __| / __| | |_  | |/ _ \\ \\/ /\n" +
            " | |  | | |_| | |_) | (_| | |_| \\__ \\ |  _| | |  __/>  < \n" +
            " |_|  |_|\\__, |_.__/ \\__,_|\\__|_|___/ |_|   |_|\\___/_/\\_\\\n" +
            "         |___/ v" + FlexConsts.VERSION + " https://mybatis-flex.com";
        System.out.println(banner);
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


}
