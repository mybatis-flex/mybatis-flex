package com.mybatisflex.solon.mybtais;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Props;
import org.noear.solon.core.VarHolder;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mybatis 适配器默认实现
 *
 * @author noear
 * @since 1.1
 */
public class MybatisAdapterDefault {
    protected static final Logger log = LoggerFactory.getLogger(MybatisAdapterDefault.class);

    protected final BeanWrap dsWrap;
    protected final Props dsProps;

    //mapper 注解验证启用？
    protected final boolean mapperVerifyEnabled;

    protected Configuration config;
    protected SqlSessionFactory factory;
    protected List<String> mappers = new ArrayList<>();
    protected SqlSessionFactoryBuilder factoryBuilder;

    /**
     * 构建Sql工厂适配器，使用默认的 typeAliases 和 mappers 配置
     */
    protected MybatisAdapterDefault(BeanWrap dsWrap) {
        this(dsWrap, Solon.cfg().getProp("mybatis"));
    }

    /**
     * 构建Sql工厂适配器，使用属性配置
     */
    protected MybatisAdapterDefault(BeanWrap dsWrap, Props dsProps) {
        this.dsWrap = dsWrap;
        if (dsProps == null) {
            this.dsProps = new Props();
        } else {
            this.dsProps = dsProps;
        }

        this.mapperVerifyEnabled = dsProps.getBool("configuration.mapperVerifyEnabled", false);
        this.factoryBuilder = new SqlSessionFactoryBuilder();

        DataSource dataSource = getDataSource();
        String dataSourceId = dsWrap.name();
        if (Utils.isEmpty(dataSourceId)) {
            dataSourceId = "_main";
        }

        TransactionFactory tf = new SolonManagedTransactionFactory();
        Environment environment = new Environment(dataSourceId, tf, dataSource);

        initConfiguration(environment);

        //加载插件（通过Bean）
        dsWrap.context().lifecycle(LifecycleIndex.PLUGIN_BEAN_USES, () -> {
            dsWrap.context().beanForeach(bw -> {
                if (bw.raw() instanceof Interceptor) {
                    config.addInterceptor(bw.raw());
                }
            });
        });

        //1.分发事件，推给扩展处理
        EventBus.publish(config);

        //2.初始化（顺序不能乱）
        initDo();

        dsWrap.context().getBeanAsync(SqlSessionFactoryBuilder.class, bean -> {
            factoryBuilder = bean;
        });
    }

    public List<String> getMappers() {
        return mappers;
    }

    protected DataSource getDataSource() {
        return dsWrap.raw();
    }

    protected void initConfiguration(Environment environment) {
        config = new Configuration(environment);

        //for configuration section
        Props cfgProps = dsProps.getProp("configuration");
        if (cfgProps.size() > 0) {
            Utils.injectProperties(config, cfgProps);
        }
    }

    protected boolean isTypeAliasesType(Class<?> type) {
        return true;
    }

    protected boolean isTypeAliasesKey(String key){
        return key.startsWith("typeAliases[") || key.equals("typeAliases");
    }

    protected boolean isTypeHandlersKey(String key){
        return key.startsWith("typeHandlers[") || key.equals("typeHandlers");
    }

    protected boolean isMappersKey(String key){
        return key.startsWith("mappers[") || key.equals("mappers");
    }

    protected void initDo() {
        //for typeAliases & typeHandlers section
        dsProps.forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                String key = (String) k;
                String valStr = (String) v;

                if (isTypeAliasesKey(key)) {
                    for (String val : valStr.split(",")) {
                        val = val.trim();
                        if (val.length() == 0) {
                            continue;
                        }

                        //package || type class，转为类表达式
                        for (Class<?> clz : ResourceUtil.scanClasses(dsWrap.context().getClassLoader(), val)) {
                            if (clz.isInterface() == false) {
                                if (isTypeAliasesType(clz)) {
                                    getConfiguration().getTypeAliasRegistry().registerAlias(clz);
                                }
                            }
                        }
                    }
                }

                if (isTypeHandlersKey(key)) {
                    for (String val : valStr.split(",")) {
                        val = val.trim();
                        if (val.length() == 0) {
                            continue;
                        }

                        //package || type class，转为类表达式
                        for (Class<?> clz : ResourceUtil.scanClasses(dsWrap.context().getClassLoader(), val)) {
                            if (TypeHandler.class.isAssignableFrom(clz)) {
                                getConfiguration().getTypeHandlerRegistry().register(clz);
                            }
                        }
                    }
                }
            }
        });

        //todo: 上面的完成后，才能做下面这个

        //for mappers section
        dsProps.forEach((k, v) -> {
            if (k instanceof String && v instanceof String) {
                String key = (String) k;
                String valStr = (String) v;

                if (isMappersKey(key)) {
                    for (String val : valStr.split(",")) {
                        val = val.trim();
                        if (val.length() == 0) {
                            continue;
                        }

                        mappers.add(val);

                        if (ResourceUtil.hasClasspath(val)) {
                            //mapper xml， 新方法，替代旧的 *.xml （基于表达式；更自由，更语义化）
                            for (String uri : ResourceUtil.scanResources(val)) {
                                addMapperByXml(uri);
                            }

                            //todo: 兼容提醒:
                            compatibilityTipsOfXml(val);
                        } else {
                            //package || type class，转为类表达式
                            for (Class<?> clz : ResourceUtil.scanClasses(dsWrap.context().getClassLoader(), val)) {
                                if (clz.isInterface()) {
                                    if (mapperVerifyEnabled) {
                                        if (isMapper(clz)) {
                                            getConfiguration().addMapper(clz);
                                        }
                                    } else {
                                        getConfiguration().addMapper(clz);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        if (mappers.size() == 0) {
            if (Utils.isEmpty(dsWrap.name())) {
                log.warn("Mybatis: Missing mappers configuration!");
            } else {
                log.warn("Mybatis: Missing mappers configuration. name='{}'", dsWrap.name());
            }
            //throw new IllegalStateException("Please add the mappers configuration!");
        } else {
            //如果有配置，但是没有 mapper 注册成功；说明有问题了
            if (config.getMapperRegistry().getMappers().size() == 0) {
                //log.warn("Mybatis: Missing mapper registration, please check the mappers configuration!");
                if (Utils.isEmpty(dsWrap.name())) {
                    throw new IllegalStateException("Missing mapper registration, please check the mappers configuration!");
                } else {
                    throw new IllegalStateException("Missing mapper registration, please check the mappers configuration. name='" + dsWrap.name() + "'");
                }
            }
        }

        //for plugins section
        List<Interceptor> interceptors = MybatisPluginUtils.resolve(dsProps, "plugins");
        for (Interceptor itp : interceptors) {
            getConfiguration().addInterceptor(itp);
        }
    }

    protected boolean isMapper(Class<?> clz) {
        return clz.isAnnotationPresent(Mapper.class);
    }

    /**
     * 获取配置器
     */
    public Configuration getConfiguration() {
        return config;
    }

    /**
     * 获取会话工厂
     */
    public SqlSessionFactory getFactory() {
        if (factory == null) {
            factory = factoryBuilder.build(getConfiguration());//new SqlSessionFactoryProxy(factoryBuilder.build(config));
        }

        return factory;
    }

    Map<Class<?>, Object> mapperCached = new HashMap<>();

    public <T> T getMapper(Class<T> mapperClz) {
        Object mapper = mapperCached.get(mapperClz);

        if (mapper == null) {
            synchronized (mapperClz) {
                mapper = mapperCached.get(mapperClz);
                if (mapper == null) {
                    MybatisMapperInterceptor handler = new MybatisMapperInterceptor(getFactory(), mapperClz);

                    mapper = Proxy.newProxyInstance(
                            mapperClz.getClassLoader(),
                            new Class[]{mapperClz},
                            handler);
                    mapperCached.put(mapperClz, mapper);
                }
            }
        }

        return (T) mapper;
    }

    public void injectTo(VarHolder vh) {
        //@Db("db1") SqlSessionFactory factory;
        if (SqlSessionFactory.class.isAssignableFrom(vh.getType())) {
            vh.setValue(this.getFactory());
            return;
        }

        //@Db("db1") Configuration cfg;
        if (Configuration.class.isAssignableFrom(vh.getType())) {
            vh.setValue(this.getConfiguration());
            return;
        }

        //@Db("db1") UserMapper userMapper;
        if (vh.getType().isInterface()) {
            Object mapper = this.getMapper(vh.getType());

            vh.setValue(mapper);
            return;
        }
    }

    protected void addMapperByXml(String uri) {
        try {
            // resource 配置方式
            ErrorContext.instance().resource(uri);

            //读取mapper文件
            InputStream stream = Resources.getResourceAsStream(uri);

            //mapper映射文件都是通过XMLMapperBuilder解析
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(stream, getConfiguration(), uri, getConfiguration().getSqlFragments());
            mapperParser.parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void compatibilityTipsOfXml(String val) {
        //todo: 兼容提醒:
        //if (val.endsWith("*.xml") && val.indexOf("*") == val.indexOf("*.xml")) {
        //@Deprecated //弃用提示
        //    log.warn("Mybatis-新文件表达式提示：'" + val + "' 不包括深度子目录；如有需要可增加'/**/'段");
        //}
    }


    public void mapperPublish() {
        for (Class<?> clz : getConfiguration().getMapperRegistry().getMappers()) {
            mapperPublishDo(clz);
        }
    }

    private void mapperPublishDo(Class<?> clz) {
        if (clz != null && clz.isInterface()) {
            Object mapper = getMapper(clz);

            //进入容器，用于 @Inject 注入
            dsWrap.context().wrapAndPut(clz, mapper);
        }
    }
}
