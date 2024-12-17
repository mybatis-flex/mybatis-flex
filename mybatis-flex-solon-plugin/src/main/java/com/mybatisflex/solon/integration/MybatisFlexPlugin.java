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

import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.datasource.*;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.logicdelete.LogicDeleteProcessor;
import com.mybatisflex.core.table.DynamicSchemaProcessor;
import com.mybatisflex.core.table.DynamicTableProcessor;
import com.mybatisflex.core.table.TableManager;
import com.mybatisflex.core.tenant.TenantFactory;
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.solon.MybatisFlexAutoConfiguration;
import com.mybatisflex.solon.MybatisFlexProperties;
import com.mybatisflex.solon.annotation.UseDataSourceInterceptor;
import com.mybatisflex.solon.aot.MybatisRuntimeNativeRegistrar;
import org.noear.solon.Utils;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 配置 MyBatis-Flex 插件。
 *
 * @author noear
 * @since 2.2
 */
public class MybatisFlexPlugin implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        // 注册动态数据源的事务路由
        //TranManager.routing(FlexDataSource.class, new FlexDataSourceRouting());

        MybatisFlexProperties flexProperties = context.beanMake(MybatisFlexProperties.class).get();

        // 构建 flexProperties 配置的数据源（并推入应用上下文，可供其它工具使用）
        if (Utils.isNotEmpty(flexProperties.getDatasource())) {
            for (Map.Entry<String, Map<String, String>> entry : flexProperties.getDatasource().entrySet()) {
                String dsName = entry.getKey();
                DataSource ds = new DataSourceBuilder(entry.getValue()).build();
                BeanWrap bw = context.wrap(dsName, ds, dsName.equals(flexProperties.getDefaultDatasourceKey()));
                context.putWrap(dsName, bw);
                if (bw.typed()) {
                    context.putWrap(DataSource.class, bw);
                }
                context.wrapPublish(bw);
            }
        }

        // UseDataSource 拦截器(aop)
        context.beanInterceptorAdd(UseDataSource.class, new UseDataSourceInterceptor());

        // 数据源解密器
        context.getBeanAsync(DataSourceDecipher.class, bean -> {
            DataSourceManager.setDecipher(bean);
        });

        // 动态表名配置
        context.getBeanAsync(DynamicTableProcessor.class, bean -> {
            TableManager.setDynamicTableProcessor(bean);
        });

        // 动态 schema 处理器配置
        context.getBeanAsync(DynamicSchemaProcessor.class, bean -> {
            TableManager.setDynamicSchemaProcessor(bean);
        });

        //多租户
        context.getBeanAsync(TenantFactory.class, bean -> {
            TenantManager.setTenantFactory(bean);
        });

        //逻辑删除处理器
        context.getBeanAsync(LogicDeleteProcessor.class, bean -> {
            LogicDeleteManager.setProcessor(bean);
        });

        // 订阅数据源
        context.subWrapsOfType(DataSource.class, bw -> {
            loadDs(context, bw);
        });

        // aot
        if (NativeDetector.isAotRuntime() && ClassUtil.hasClass(() -> RuntimeNativeRegistrar.class)) {
            context.wrapAndPut(MybatisRuntimeNativeRegistrar.class);
        }
    }

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private void loadDs(AppContext context, BeanWrap bw) {
        MybatisFlexBootstrap.getInstance().addDataSource(bw.name(), bw.raw());

        if (bw.typed()) {
            //控制默认数据源
            FlexDataSource flexDataSource = (FlexDataSource) MybatisFlexBootstrap.getInstance().getDataSource();
            flexDataSource.setDefaultDataSource(bw.name());
        }

        if (initialized.compareAndSet(false, true)) {
            //有数据源后，再启动自动装配
            context.beanMake(MybatisFlexAutoConfiguration.class);
        }
    }
}
