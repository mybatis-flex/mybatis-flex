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
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.row.RowMapperInvoker;
import com.mybatisflex.solon.aot.MybatisRuntimeNativeRegistrar;
import org.apache.ibatis.session.SqlSessionFactory;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.Props;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.TmplUtil;
import org.noear.solon.data.datasource.DsUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 配置 MyBatis-Flex 插件。
 *
 * @author noear
 * @since 2.2
 */
public class XPluginImpl implements Plugin {
    private static final String CONFIG_PREFIX = "mybatisFlex";
    private static final String CONFIG_DS_PREFIX = "mybatisFlex.datasource";

    private static MybatisAdapterFlex adapterFlex;

    public static MybatisAdapterFlex getAdapterFlex() {
        return adapterFlex;
    }

    @Override
    public void start(AppContext context) throws Throwable {
        // 注册动态数据源的事务路由
        //TranManager.routing(FlexDataSource.class, new FlexDataSourceRouting());

        // 订阅数据源
        context.subWrapsOfType(DataSource.class, bw -> {
            loadDs(context, bw);
        });

        // aot
        if (NativeDetector.isAotRuntime() && ClassUtil.hasClass(() -> RuntimeNativeRegistrar.class)) {
            context.wrapAndPut(MybatisRuntimeNativeRegistrar.class);
        }


        // 构建 mf 配置的数据源
        Class<?> dsDefClz = ClassUtil.loadClass("com.zaxxer.hikari.HikariDataSource");
        Props dsProps = context.cfg().getProp(CONFIG_DS_PREFIX);
        if (dsProps.size() > 0) {
            Map<String, DataSource> dsMap = DsUtils.buildDsMap(dsProps, dsDefClz);

            for (Map.Entry<String, DataSource> entry : dsMap.entrySet()) {
                String dsName = entry.getKey();
                DataSource ds = entry.getValue();
                BeanWrap bw = context.wrap(dsName, ds);
                loadDs(context, bw);
            }
        }
    }

    private void loadDs(AppContext context, BeanWrap bw) {
        boolean isInit = MybatisFlexBootstrap.getInstance().getDataSource() == null;
        MybatisFlexBootstrap.getInstance().addDataSource(bw.name(), bw.raw());

        if (isInit) {
            initDo(context);
        }
    }

    private void initDo(AppContext context) {
        BeanWrap dsBw = context.wrap(FlexConsts.NAME, MybatisFlexBootstrap.getInstance().getDataSource(), true);
        MybatisAdapterFlex dsFlex = new MybatisAdapterFlex(dsBw, context.cfg().getProp(CONFIG_PREFIX));
        dsFlex.mapperPublish();
        adapterFlex = dsFlex;

        // 注册到管理器（aot 时会用到）
        //MybatisAdapterManager.register(dsBw, dsFlex);

        //绑定到容器
        context.beanInjectorAdd(Inject.class, FlexGlobalConfig.class, ((vh, anno) -> {
            dsFlex.injectTo(vh);
        }));


        context.beanInjectorAdd(Inject.class, FlexConfiguration.class, ((vh, anno) -> {
            dsFlex.injectTo(vh);
        }));

        context.beanInjectorAdd(Inject.class, RowMapperInvoker.class, ((vh, anno) -> {
            dsFlex.injectTo(vh);
        }));

        context.beanInjectorAdd(Inject.class, SqlSessionFactory.class, ((vh, anno) -> {
            dsFlex.injectTo(vh);
        }));

        context.beanInterceptorAdd(UseDataSource.class, inv -> {
            UseDataSource anno = inv.getMethodAnnotation(UseDataSource.class);

            if (anno == null) {
                anno = inv.getTargetAnnotation(UseDataSource.class);
            }

            if (anno == null) {
                return inv.invoke();
            } else {
                //备份
                String backup = DataSourceKey.get();

                try {
                    String dsName = TmplUtil.parse(anno.value(), inv);

                    DataSourceKey.use(dsName);
                    return inv.invoke();
                } finally {
                    //还原
                    DataSourceKey.use(backup);
                }
            }
        });
    }
}
