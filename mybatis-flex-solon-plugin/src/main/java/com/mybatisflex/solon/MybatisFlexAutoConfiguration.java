package com.mybatisflex.solon;

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.mybatis.FlexSqlSessionFactoryBuilder;
import com.mybatisflex.core.row.RowMapperInvoker;
import com.mybatisflex.solon.transaction.SolonManagedTransactionFactory;
import com.mybatisflex.solon.transaction.MybatisSessionTemplate;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;

/**
 * Mybatis-Flex 自动装配
 *
 * @author noear 2024/12/17 created
 */
@Configuration
public class MybatisFlexAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisFlexAutoConfiguration.class);

    private DataSource getDataSource() {
        return MybatisFlexBootstrap.getInstance().getDataSource();
    }

    @Inject
    private AppContext appContext;

    @Bean
    public FlexConfiguration configuration(MybatisFlexProperties flexProperties,
                                           @Inject(required = false) ConfigurationCustomizer configurationCustomizer) {
        TransactionFactory tf = new SolonManagedTransactionFactory();
        Environment environment = new Environment(FlexConsts.NAME, tf, getDataSource());

        FlexConfiguration configuration = new FlexConfiguration(environment);

        if (Utils.isNotEmpty(flexProperties.getConfigurationProperties())) {
            Utils.injectProperties(configuration, flexProperties.getConfigurationProperties());
        }

        if (flexProperties.getConfiguration() != null) {
            flexProperties.getConfiguration().applyTo(configuration);
        }

        if (configurationCustomizer != null) {
            configurationCustomizer.customize(configuration);
        }

        //增加事件总线扩展
        EventBus.publish(configuration);

        return configuration;
    }


    @Bean
    @Condition(onMissingBean = SqlSessionFactoryBuilder.class)
    public SqlSessionFactoryBuilder sqlSessionFactoryBuilder() {
        return new FlexSqlSessionFactoryBuilder();
    }

    @Bean
    public FlexGlobalConfig globalConfig(MybatisFlexProperties flexProperties,
                                         FlexConfiguration flexConfiguration,
                                         @Inject(required = false) MyBatisFlexCustomizer flexCustomizer) {

        FlexGlobalConfig globalConfig = FlexGlobalConfig.getDefaultConfig();

        if (flexProperties.getGlobalConfig() != null) {
            flexProperties.getGlobalConfig().applyTo(globalConfig);
        }

        if (globalConfig.getKeyConfig() == null) {
            //如果没有，给个默认值
            globalConfig.setKeyConfig(new FlexGlobalConfig.KeyConfig());
        }

        globalConfig.setConfiguration(flexConfiguration);

        if (flexCustomizer != null) {
            flexCustomizer.customize(globalConfig);
        }

        //增加事件总线扩展
        EventBus.publish(globalConfig);

        //绑定（不能少）
        FlexGlobalConfig.setConfig(flexConfiguration.getEnvironment().getId(), globalConfig, true);

        return globalConfig;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(MybatisFlexProperties flexProperties,
                                               FlexConfiguration flexConfiguration,
                                               FlexGlobalConfig globalConfig,
                                               SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
        appContext.subBeansOfType(Interceptor.class, bean -> {
            flexConfiguration.addInterceptor(bean);
        });

        if (isNotEmpty(flexProperties.getTypeAliasesPackage())) {
            Class<?> typeAliasesSuperType = flexProperties.getTypeAliasesSuperType();
            for (String val : flexProperties.getTypeAliasesPackage()) {
                //package || type class，转为类表达式
                for (Class<?> clz : ResourceUtil.scanClasses(appContext.getClassLoader(), val)) {
                    if (isTypeAliases(clz, typeAliasesSuperType)) {
                        flexConfiguration.getTypeAliasRegistry().registerAlias(clz);
                    }
                }
            }
        }

        if (isNotEmpty(flexProperties.getTypeHandlersPackage())) {
            for (String val : flexProperties.getTypeHandlersPackage()) {
                for (Class<?> clz : ResourceUtil.scanClasses(appContext.getClassLoader(), val)) {
                    if (TypeHandler.class.isAssignableFrom(clz)) {
                        flexConfiguration.getTypeHandlerRegistry().register(clz);
                    }
                }
            }
        }

        if (isNotEmpty(flexProperties.getMapperLocations())) {
            for (String val : flexProperties.getMapperLocations()) {
                if (ResourceUtil.hasClasspath(val)) {
                    //mapper xml， 新方法，替代旧的 *.xml （基于表达式；更自由，更语义化）
                    for (String uri : ResourceUtil.scanResources(val)) {
                        addMapperByXml(flexConfiguration, uri);
                    }
                } else {
                    //package || type class，转为类表达式
                    for (Class<?> clz : ResourceUtil.scanClasses(appContext.getClassLoader(), val)) {
                        if (clz.isInterface()) {
                            //no mapperVerifyEnabled ...
                            flexConfiguration.addMapper(clz);
                        }
                    }
                }
            }

            //如果有配置，但是没有 mapper 注册成功；说明有问题了
            if (flexConfiguration.getMapperRegistry().getMappers().size() == 0) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Property 'mapperLocations' was specified but matching resources are not found.");
                }
            }
        }

        //所有配置全准备好了（flexConfiguration、globalConfig），才能构建
        return sqlSessionFactoryBuilder.build(flexConfiguration);
    }

    @Bean
    public void mapperPublish(FlexConfiguration flexConfiguration,
                              FlexGlobalConfig globalConfig,
                              SqlSessionFactory sqlSessionFactory) {
        MybatisSessionTemplate sqlSessionTemplate = new MybatisSessionTemplate(sqlSessionFactory);

        for (Class<?> mapperClz : flexConfiguration.getMapperRegistry().getMappers()) {
            Object mapperProxy = sqlSessionTemplate.getMapper(mapperClz);

            //推入容器，之后可以被注入
            appContext.wrapAndPut(mapperClz, mapperProxy);
        }
    }

    @Bean
    public RowMapperInvoker rowMapperInvoker(SqlSessionFactory sqlSessionFactory) {
        return new RowMapperInvoker(sqlSessionFactory);
    }

    /////////////

    /**
     * 添加 xml mapper
     */
    private void addMapperByXml(FlexConfiguration flexConfiguration, String uri) {
        try {
            // resource 配置方式
            ErrorContext.instance().resource(uri);

            //读取mapper文件
            InputStream stream = Resources.getResourceAsStream(uri);

            //mapper映射文件都是通过XMLMapperBuilder解析
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(stream, flexConfiguration, uri, flexConfiguration.getSqlFragments());
            mapperParser.parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否为非空数组
     */
    private boolean isNotEmpty(String[] ary) {
        return ary != null && ary.length > 0;
    }

    /**
     * 是否为类型别名
     */
    private boolean isTypeAliases(Class<?> clz, Class<?> typeAliasesSuperType) {
        if (clz.isInterface()) {
            return false;
        }

        if (typeAliasesSuperType != null) {
            return typeAliasesSuperType.isAssignableFrom(clz);
        } else {
            return true;
        }
    }
}
