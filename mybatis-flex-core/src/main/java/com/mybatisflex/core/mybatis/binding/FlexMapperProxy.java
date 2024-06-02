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
package com.mybatisflex.core.mybatis.binding;

import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.datasource.FlexDataSource;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.row.RowMapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.MapUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlexMapperProxy<T> extends MybatisMapperProxy<T> {
    private static final String NULL_KEY = "@NK@";
    private static final Map<Method, String> methodDsKeyCache = new ConcurrentHashMap<>();
    private final FlexDataSource dataSource;

    public FlexMapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethodInvoker> methodCache,
                           FlexConfiguration configuration) {
        super(sqlSession, mapperInterface, methodCache);
        this.dataSource = (FlexDataSource) configuration.getEnvironment().getDataSource();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }

        boolean needClearDsKey = false;
        boolean needClearDbType = false;

        //由用户指定的数据
        String userDsKey = DataSourceKey.get();
        //最终使用的数据源
        String finalDsKey = userDsKey;

        try {
            if (StringUtil.isBlank(finalDsKey)) {
                finalDsKey = getMethodDsKey(method, proxy);
            }

            //通过自定义分配策略去获取最终的数据源
            finalDsKey = DataSourceKey.getShardingDsKey(finalDsKey, proxy, method, args);

            if (StringUtil.isNotBlank(finalDsKey) && !finalDsKey.equals(userDsKey)) {
                needClearDsKey = true;
                DataSourceKey.use(finalDsKey);
            }

            DbType hintDbType = DialectFactory.getHintDbType();
            if (hintDbType == null) {
                if (finalDsKey != null && dataSource != null) {
                    hintDbType = dataSource.getDbType(finalDsKey);
                }

                if (hintDbType == null) {
                    hintDbType = FlexGlobalConfig.getDefaultConfig().getDbType();
                }

                needClearDbType = true;
                DialectFactory.setHintDbType(hintDbType);
            }
            return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
        } catch (Throwable e) {
            throw ExceptionUtil.unwrapThrowable(e);
        } finally {
            if (needClearDbType) {
                DialectFactory.clearHintDbType();
            }
            if (needClearDsKey) {
                if (userDsKey != null) {
                    //恢复用户设置的数据源，并由用户主动去清除
                    DataSourceKey.use(userDsKey);
                } else {
                    DataSourceKey.clear();
                }
            }
        }
    }


    private static String getMethodDsKey(Method method, Object proxy) {
        String result = MapUtil.computeIfAbsent(methodDsKeyCache, method, m -> {
            UseDataSource methodAnno = method.getAnnotation(UseDataSource.class);
            if (methodAnno != null && StringUtil.isNotBlank(methodAnno.value())) {
                return methodAnno.value();
            }

            Class<?>[] interfaces = proxy.getClass().getInterfaces();
            for (Class<?> anInterface : interfaces) {
                UseDataSource classAnno = anInterface.getAnnotation(UseDataSource.class);
                if (classAnno != null && StringUtil.isNotBlank(classAnno.value())) {
                    return classAnno.value();
                }
            }

            if (interfaces[0] != RowMapper.class) {
                TableInfo tableInfo = TableInfoFactory.ofMapperClass(interfaces[0]);
                if (tableInfo != null) {
                    String tableDsKey = tableInfo.getDataSource();
                    if (StringUtil.isNotBlank(tableDsKey)) {
                        return tableDsKey;
                    }
                }
            }
            return NULL_KEY;
        });

        return NULL_KEY.equals(result) ? null : result;
    }

}
