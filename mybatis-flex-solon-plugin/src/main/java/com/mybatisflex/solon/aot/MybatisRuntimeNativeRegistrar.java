package com.mybatisflex.solon.aot;

import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.solon.MybatisFlexProperties;
import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.io.DefaultVFS;
import org.apache.ibatis.io.JBoss6VFS;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.RuntimeSupport;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.noear.solon.aot.NativeMetadataUtils;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * mybatis aot 注册 native 元数据
 *
 * @author songyinyin
 * @since 2.3
 * @link <a href="https://github.com/kazuki43zoo/mybatis-native-demo/blob/main/src/main/java/com/example/nativedemo/MyBatisNativeConfiguration.java">MyBatisNativeConfiguration</a>
 */
public class MybatisRuntimeNativeRegistrar implements RuntimeNativeRegistrar {

    @Override
    public void register(AppContext context, RuntimeNativeMetadata metadata) {
        Stream.of(RawLanguageDriver.class,
            XMLLanguageDriver.class,
            RuntimeSupport.class,
            ProxyFactory.class,
            Slf4jImpl.class,
            Log.class,
            JakartaCommonsLoggingImpl.class,
            Log4j2Impl.class,
            Jdk14LoggingImpl.class,
            StdOutImpl.class,
            NoLoggingImpl.class,
            SqlSessionFactory.class,
            PerpetualCache.class,
            FifoCache.class,
            LruCache.class,
            SoftCache.class,
            WeakCache.class,
            ArrayList.class,
            HashMap.class,
            TreeSet.class,
            HashSet.class
        ).forEach(x -> metadata.registerReflection(x, MemberCategory.values()));

        Stream.of(
            "org/apache/ibatis/builder/xml/.*.dtd",
            "org/apache/ibatis/builder/xml/.*.xsd"
        ).forEach(metadata::registerResourceInclude);

        metadata.registerJdkProxy(Executor.class);
        metadata.registerReflection(Executor.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerAllDeclaredMethod(Executor.class, ExecutableMode.INVOKE);

        metadata.registerJdkProxy(StatementHandler.class);
        metadata.registerReflection(StatementHandler.class, MemberCategory.INTROSPECT_PUBLIC_METHODS);
        metadata.registerAllDeclaredMethod(StatementHandler.class, ExecutableMode.INVOKE);

        metadata.registerReflection(BoundSql.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_DECLARED_METHODS, MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS);
        metadata.registerAllDeclaredMethod(BoundSql.class, ExecutableMode.INVOKE);

        metadata.registerReflection(Configuration.class, MemberCategory.DECLARED_FIELDS);
        metadata.registerAllDeclaredMethod(Configuration.class, ExecutableMode.INVOKE);

        metadata.registerReflection(JBoss6VFS.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(DefaultVFS.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);


        registerMybatisAdapter(context, metadata);

    }

    protected void registerMybatisAdapter(AppContext context, RuntimeNativeMetadata metadata) {
        MybatisFlexProperties flexProperties = context.getBean(MybatisFlexProperties.class);

        //注册 xml 资源
        if (flexProperties != null) {
            for (String res : flexProperties.getMapperLocations()) {
                if (ResourceUtil.hasClasspath(res)) {
                    res = ResourceUtil.remSchema(res);
                    res = res.replace("**", "*");
                    res = res.replace("*", ".*");
                    metadata.registerResourceInclude(res);
                }
            }
        }

        FlexConfiguration flexConfiguration = context.getBean(FlexConfiguration.class);
        if (flexConfiguration != null) {
            //注册 mapper 代理
            for (Class<?> clz : flexConfiguration.getMapperRegistry().getMappers()) {
                metadata.registerJdkProxy(clz);
                metadata.registerReflection(clz, MemberCategory.INTROSPECT_PUBLIC_METHODS);
                Method[] declaredMethods = clz.getDeclaredMethods();
                for (Method method : declaredMethods) {
                    MethodWrap methodWrap = context.methodGet(method);
                    NativeMetadataUtils.registerMethodAndParamAndReturnType(metadata, methodWrap);
                }
            }

            // 注册 entity
            for (Class<?> clz : flexConfiguration.getTypeAliasRegistry().getTypeAliases().values()) {
                metadata.registerReflection(clz, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
                metadata.registerDefaultConstructor(clz);
            }

            //注处 typeHandler
            for (TypeHandler typeHandler : flexConfiguration.getTypeHandlerRegistry().getTypeHandlers()) {
                metadata.registerReflection(typeHandler.getClass(), MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
                metadata.registerDefaultConstructor(typeHandler.getClass());
            }
        }
    }
}
