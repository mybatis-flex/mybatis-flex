/*
 *  Copyright (c) 2022-2024, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.loveqq.framework.boot.autoconfig;

import com.kfyty.loveqq.framework.aop.Advisor;
import com.kfyty.loveqq.framework.aop.support.annotated.AnnotationPointcutAdvisor;
import com.kfyty.loveqq.framework.boot.data.orm.mybatis.autoconfig.MybatisProperties;
import com.kfyty.loveqq.framework.boot.data.orm.mybatis.autoconfig.SqlSessionFactoryBean;
import com.kfyty.loveqq.framework.core.autoconfig.InitializingBean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Configuration;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.ConditionalOnMissingBean;
import com.kfyty.loveqq.framework.core.exception.ResolvableException;
import com.kfyty.loveqq.framework.core.utils.IOUtil;
import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceBuilder;
import com.mybatisflex.core.datasource.DataSourceDecipher;
import com.mybatisflex.core.datasource.DataSourceManager;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.table.DynamicSchemaProcessor;
import com.mybatisflex.core.table.DynamicTableProcessor;
import com.mybatisflex.core.table.TableManager;
import com.mybatisflex.core.tenant.TenantFactory;
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.core.util.MapUtil;
import com.mybatisflex.loveqq.framework.boot.autoconfig.annotation.ConditionalOnMybatisFlexDatasource;
import com.mybatisflex.loveqq.framework.boot.autoconfig.customize.ConfigurationCustomizer;
import com.mybatisflex.loveqq.framework.boot.autoconfig.customize.MyBatisFlexCustomizer;
import com.mybatisflex.loveqq.framework.boot.autoconfig.datasource.DataSourceInterceptor;
import com.mybatisflex.loveqq.framework.boot.autoconfig.transaction.FlexTransactionFactory;
import com.mybatisflex.loveqq.framework.boot.autoconfig.transaction.FlexTransactionManager;
import org.apache.ibatis.transaction.TransactionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Mybatis-Flex 的核心配置。
 *
 * @author kfyty725
 */
@Configuration
public class MybatisFlexAutoConfiguration implements InitializingBean {
    @Autowired
    protected MybatisProperties mybatisProperties;

    @Autowired
    protected MybatisFlexProperties mybatisFlexProperties;

    /**
     * 数据源解密器
     */
    @Autowired(required = false)
    protected DataSourceDecipher dataSourceDecipher;

    /**
     * 动态表名
     */
    @Autowired(required = false)
    protected DynamicTableProcessor dynamicTableProcessor;

    /**
     * 动态 schema 处理器
     */
    @Autowired(required = false)
    protected DynamicSchemaProcessor dynamicSchemaProcessor;

    /**
     * 多租户
     */
    @Autowired(required = false)
    protected TenantFactory tenantFactory;

    /**
     * 自定义逻辑删除处理器
     */
    @Autowired(required = false)
    protected LogicDeleteProcessor logicDeleteProcessor;

    /**
     * 配置监听
     */
    @Autowired(required = false)
    protected List<ConfigurationCustomizer> configurationCustomizers;

    /**
     * 初始化监听
     */
    @Autowired(required = false)
    protected List<MyBatisFlexCustomizer> mybatisFlexCustomizers;

    @Override
    public void afterPropertiesSet() {
        // 检测 MyBatis 原生配置文件是否存在
        this.checkConfigFileExists();

        // 添加 MyBatis-Flex 全局配置
        if (mybatisFlexProperties.getGlobalConfig() != null) {
            mybatisFlexProperties.getGlobalConfig().applyTo(FlexGlobalConfig.getDefaultConfig());
        }

        //数据源解密器
        if (dataSourceDecipher != null) {
            DataSourceManager.setDecipher(dataSourceDecipher);
        }

        // 动态表名配置
        if (dynamicTableProcessor != null) {
            TableManager.setDynamicTableProcessor(dynamicTableProcessor);
        }

        // 动态 schema 处理器配置
        if (dynamicSchemaProcessor != null) {
            TableManager.setDynamicSchemaProcessor(dynamicSchemaProcessor);
        }

        //多租户
        if (tenantFactory != null) {
            TenantManager.setTenantFactory(tenantFactory);
        }

        //逻辑删除处理器
        if (logicDeleteProcessor != null) {
            LogicDeleteManager.setProcessor(logicDeleteProcessor);
        }

        //初始化监听器
        if (mybatisFlexCustomizers != null) {
            mybatisFlexCustomizers.forEach(myBatisFlexCustomizer -> myBatisFlexCustomizer.customize(FlexGlobalConfig.getDefaultConfig()));
        }
    }

    @ConditionalOnMybatisFlexDatasource
    @Bean(resolveNested = false, independent = true)
    public DataSource flexDataSource() {
        FlexDataSource flexDataSource = null;

        Map<String, Map<String, String>> dataSourceProperties = this.mybatisFlexProperties.getDatasource();

        if (dataSourceDecipher != null) {
            DataSourceManager.setDecipher(dataSourceDecipher);
        }

        if (this.mybatisFlexProperties.getDefaultDatasourceKey() != null) {
            Map<String, String> map = dataSourceProperties.remove(this.mybatisFlexProperties.getDefaultDatasourceKey());
            if (map != null) {
                flexDataSource = this.addDataSource(MapUtil.entry(this.mybatisFlexProperties.getDefaultDatasourceKey(), map), flexDataSource);
            } else {
                throw FlexExceptions.wrap("没有找到默认数据源 \"%s\" 对应的配置，请检查您的多数据源配置。", this.mybatisFlexProperties.getDefaultDatasourceKey());
            }
        }

        for (Map.Entry<String, Map<String, String>> entry : dataSourceProperties.entrySet()) {
            flexDataSource = this.addDataSource(entry, flexDataSource);
        }

        return flexDataSource;
    }

    /**
     * 事务管理器
     *
     * @param dataSource 数据源
     * @return 事务管理器
     */
    @ConditionalOnMybatisFlexDatasource
    @Bean(resolveNested = false, independent = true)
    public PlatformTransactionManager flexDataSourceTransactionManager(DataSource dataSource) {
        return new FlexTransactionManager();
    }

    /**
     * {@link com.mybatisflex.annotation.UseDataSource} 注解切换数据源切面。
     */
    @Bean(resolveNested = false, independent = true)
    @ConditionalOnMissingBean(name = "flexDataSourceAdvisor")
    public Advisor flexDataSourceAdvisor() {
        return new AnnotationPointcutAdvisor(UseDataSource.class, new DataSourceInterceptor());
    }

    @ConditionalOnMissingBean
    @Bean(resolveNested = false, independent = true)
    public TransactionFactory flexTransactionFactory() {
        return new FlexTransactionFactory();
    }

    @Bean
    public SqlSessionFactoryBean flexSqlSessionFactoryBean() {
        SqlSessionFactoryBean factory = new FlexSqlSessionFactoryBean();

        this.mybatisFlexProperties.applyTo(this.mybatisProperties);

        this.applyConfiguration(factory);

        return factory;
    }

    protected void applyConfiguration(SqlSessionFactoryBean factory) {
        FlexConfiguration configuration = null;
        MybatisFlexProperties.CoreConfiguration coreConfiguration = this.mybatisFlexProperties.getConfiguration();
        if (coreConfiguration != null || !StringUtils.hasText(this.mybatisFlexProperties.getConfigLocation())) {
            configuration = new FlexConfiguration();
        }
        if (configuration != null && coreConfiguration != null) {
            coreConfiguration.applyTo(configuration);
        }
        if (!CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }

    private void checkConfigFileExists() {
        if (this.mybatisFlexProperties.isCheckConfigLocation() && StringUtils.hasText(this.mybatisFlexProperties.getConfigLocation())) {
            try (InputStream loaded = IOUtil.load(this.mybatisProperties.getConfigLocation())) {
                Assert.state(loaded != null, "Cannot find config location: " + this.mybatisProperties.getConfigLocation() + " (please add config file or check your Mybatis configuration)");
            } catch (IOException e) {
                throw new ResolvableException(e);
            }
        }
    }

    private FlexDataSource addDataSource(Map.Entry<String, Map<String, String>> entry, FlexDataSource flexDataSource) {
        DataSource dataSource = new DataSourceBuilder(entry.getValue()).build();

        DataSourceManager.decryptDataSource(dataSource);

        if (flexDataSource == null) {
            flexDataSource = new FlexDataSource(entry.getKey(), dataSource, false);
        } else {
            flexDataSource.addDataSource(entry.getKey(), dataSource, false);
        }
        return flexDataSource;
    }
}
