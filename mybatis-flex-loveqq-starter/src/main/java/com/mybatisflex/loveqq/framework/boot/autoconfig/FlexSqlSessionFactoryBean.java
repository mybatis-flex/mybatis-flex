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

import com.kfyty.loveqq.framework.boot.data.orm.mybatis.autoconfig.SqlSessionFactoryBean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.event.ContextRefreshedEvent;
import com.kfyty.loveqq.framework.core.support.Pair;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.mybatis.FlexSqlSessionFactoryBuilder;
import com.mybatisflex.loveqq.framework.boot.autoconfig.transaction.FlexTransactionFactory;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * <p>源于 {@link SqlSessionFactoryBean}，主要是用于构建 {@link com.mybatisflex.core.mybatis.FlexConfiguration }，而不是使用原生的 {@link Configuration}。
 *
 * @author kfyty725
 */
public class FlexSqlSessionFactoryBean extends SqlSessionFactoryBean {
    @Autowired
    private MybatisFlexProperties mybatisFlexProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // nothing
    }

    @Override
    protected Class<? extends Configuration> obtainConfigurationClass() {
        return FlexConfiguration.class;
    }

    @Override
    protected Pair<Configuration, XMLConfigBuilder> buildConfiguration() {
        Pair<Configuration, XMLConfigBuilder> configPair = super.buildConfiguration();

        MybatisFlexProperties.CoreConfiguration coreConfiguration = this.mybatisFlexProperties.getConfiguration();
        if (coreConfiguration != null && coreConfiguration.getDefaultEnumTypeHandler() != null) {
            configPair.getKey().setDefaultEnumTypeHandler(coreConfiguration.getDefaultEnumTypeHandler());
        }

        return configPair;
    }

    @Override
    protected void buildEnvironment(Configuration configuration) {
        FlexDataSource flexDataSource;

        if (this.getDataSource() instanceof FlexDataSource) {
            flexDataSource = (FlexDataSource) this.getDataSource();
        } else {
            flexDataSource = new FlexDataSource(FlexConsts.NAME, this.getDataSource());
        }

        configuration.setEnvironment(
            new Environment(
                FlexConsts.NAME,
                this.getTransactionFactory() == null ? new FlexTransactionFactory() : this.getTransactionFactory(),
                flexDataSource
            )
        );
    }

    @Override
    protected void buildMapperLocations(Configuration configuration) {
        // mybatis-flex 要延迟加载
    }

    /**
     * 需先构建 sqlSessionFactory，再去初始化 mapperLocations
     * 因为 xmlMapperBuilder.parse() 用到 FlexGlobalConfig， FlexGlobalConfig 的初始化是在 sqlSessionFactory 的构建方法里进行的
     * fixed https://gitee.com/mybatis-flex/mybatis-flex/issues/I6X59V
     */
    @Override
    protected SqlSessionFactory build(Configuration configuration) {
        SqlSessionFactory sqlSessionFactory = new FlexSqlSessionFactoryBuilder().build(configuration);
        super.buildMapperLocations(configuration);
        return sqlSessionFactory;
    }
}
