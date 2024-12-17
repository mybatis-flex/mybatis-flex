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

package com.mybatisflex.solon.integration;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.mybatis.FlexSqlSessionFactoryBuilder;
import com.mybatisflex.core.row.RowMapperInvoker;
import com.mybatisflex.solon.MybatisFlexProperties;
import com.mybatisflex.solon.mybtais.MybatisAdapterDefault;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;

import javax.sql.DataSource;

/**
 * MyBatis-Flex 适配器。
 *
 * @author noear
 * @since 2.2
 */
public class MybatisAdapterFlex extends MybatisAdapterDefault {
    private FlexSqlSessionFactoryBuilder factoryBuilderPlus;
    private FlexGlobalConfig globalConfig;
    private RowMapperInvoker rowMapperInvoker;
    private Class<?> typeAliasesBaseType;
    private MybatisFlexProperties flexProperties;

    protected MybatisAdapterFlex(BeanWrap dsWrap, Props flexProps, MybatisFlexProperties flexProperties) {
        this.factoryBuilderPlus = new FlexSqlSessionFactoryBuilder();
        this.flexProperties = flexProperties;

        //初始化开始
        initStart(dsWrap, flexProps);

        //初始化之前
        initAfter(dsWrap);
    }

    protected void initAfter(BeanWrap dsWrap) {
        globalConfig.setSqlSessionFactory(getFactory());
    }

    @Override
    protected DataSource getDataSource() {
        return dsWrap.raw();
    }

    @Override
    protected void initConfiguration(Environment environment) {
        //for configuration section
        config = new FlexConfiguration(environment);

        if (Utils.isNotEmpty(flexProperties.getConfigurationProperties())) {
            Utils.injectProperties(config, flexProperties.getConfigurationProperties());
        }

        if (flexProperties.getConfiguration() != null) {
            flexProperties.getConfiguration().applyTo(config);
        }

        //for globalConfig section
        if (dsWrap.typed()) {
            globalConfig = FlexGlobalConfig.getDefaultConfig();
        } else {
            globalConfig = new FlexGlobalConfig();
        }

        if (flexProperties.getGlobalConfig() != null) {
            flexProperties.getGlobalConfig().applyTo(globalConfig);
        }

        if (globalConfig.getKeyConfig() == null) {
            //如果没有，给个默认值
            globalConfig.setKeyConfig(new FlexGlobalConfig.KeyConfig());
        }

        globalConfig.setConfiguration(config);

        FlexGlobalConfig.setConfig(environment.getId(), globalConfig, false);

        //增加事件扩展机制
        EventBus.publish(globalConfig);
    }

    @Override
    protected void loadConfiguration() {
        typeAliasesBaseType = flexProperties.getTypeAliasesSuperType();
        super.loadConfiguration();
    }

    /**
     * 获取全局配置
     */
    public FlexGlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = factoryBuilderPlus.build(getConfiguration());
        }

        return factory;
    }

    @Override
    public void injectTo(VarHolder varH) {
        super.injectTo(varH);

        // @Db("db1") FlexGlobalConfig globalConfig
        if (FlexGlobalConfig.class.isAssignableFrom(varH.getType())) {
            varH.setValue(this.getGlobalConfig());
            return;
        }

        // @Db("db1") RowMapperInvoker rowMapper
        if (RowMapperInvoker.class.equals(varH.getType())) {
            if (rowMapperInvoker == null) {
                rowMapperInvoker = new RowMapperInvoker(getFactory());
            }
            varH.setValue(rowMapperInvoker);
        }
    }

    @Override
    protected boolean isTypeAliasesType(Class<?> type) {
        //typeAliasesSuperType
        if (typeAliasesBaseType == null) {
            return true;
        } else {
            return typeAliasesBaseType.isAssignableFrom(type);
        }
    }

    @Override
    protected boolean isTypeAliasesKey(String key) {
        return super.isTypeAliasesKey(key) || key.startsWith("typeAliasesPackage[");
    }

    @Override
    protected boolean isTypeHandlersKey(String key) {
        return super.isTypeHandlersKey(key) || key.startsWith("typeHandlersPackage[");
    }

    @Override
    protected boolean isMappersKey(String key) {
        return super.isMappersKey(key) || key.startsWith("mapperLocations[");
    }
}
