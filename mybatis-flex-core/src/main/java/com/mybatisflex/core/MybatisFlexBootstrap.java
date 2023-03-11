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
package com.mybatisflex.core;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.mybatis.FlexSqlSessionFactoryBuilder;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * MybatisFlex 的启动类
 *
 * <code>
 * MybatisFlexBootstrap.getInstance()
 * .setDatasource(...)
 * .addMapper(...)
 * .start();
 * <p>
 * <p>
 * MybatisFlexBootstrap.getInstance()
 * .execute(...)
 * </code>
 */
public class MybatisFlexBootstrap {

    private final AtomicBoolean started = new AtomicBoolean(false);

    private String environmentId = "mybatis-flex";
    private TransactionFactory transactionFactory;

    private DataSource dataSource;
    private Configuration configuration;
    private List<Class<?>> mappers;

    private DbType dbType;
    private SqlSessionFactory sqlSessionFactory;


    private static volatile MybatisFlexBootstrap instance;

    public static MybatisFlexBootstrap getInstance() {
        if (instance == null) {
            synchronized (MybatisFlexBootstrap.class) {
                if (instance == null) {
                    instance = new MybatisFlexBootstrap();
                }
            }
        }
        return instance;
    }






    public <T> MybatisFlexBootstrap addMapper(Class<T> type) {
        if (this.mappers == null) {
            mappers = new ArrayList<>();
        }
        mappers.add(type);
        return this;
    }




    public MybatisFlexBootstrap start() {
        if (started.compareAndSet(false, true)) {
            if (dataSource == null) {
                throw new IllegalStateException("dataSource can not be null.");
            }

            //init configuration
            if (configuration == null) {

                if (transactionFactory == null) {
                    transactionFactory = new JdbcTransactionFactory();
                }

                Environment environment = new Environment(environmentId, transactionFactory, dataSource);
                configuration = new FlexConfiguration(environment);
            }

            //init mappers
            if (mappers != null) {
                mappers.forEach(configuration::addMapper);
            }

            //init sqlSessionFactory
            this.sqlSessionFactory = new FlexSqlSessionFactoryBuilder().build(configuration);

            //init dbType
            this.dbType = FlexGlobalConfig.getConfig(environmentId).getDbType();

            LogFactory.getLog(MybatisFlexBootstrap.class).debug("Mybatis-Flex has started.");
        }

        return this;
    }


    public <R, T> R execute(Class<T> mapperClass, Function<T, R> function) {
        try (SqlSession sqlSession = openSession()) {
            DialectFactory.setHintDbType(dbType);
            T mapper = sqlSession.getMapper(mapperClass);
            return function.apply(mapper);
        } finally {
            DialectFactory.clearHintDbType();
        }
    }


    private SqlSession openSession() {
        return sqlSessionFactory.openSession(configuration.getDefaultExecutorType());
    }


    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(FlexConfiguration configuration) {
        this.configuration = configuration;
    }

    public List<Class<?>> getMappers() {
        return mappers;
    }

    public void setMappers(List<Class<?>> mappers) {
        this.mappers = mappers;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
}
