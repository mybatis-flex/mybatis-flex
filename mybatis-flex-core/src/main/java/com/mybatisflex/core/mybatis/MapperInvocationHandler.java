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
package com.mybatisflex.core.mybatis;

import com.mybatisflex.annotation.UseDataSource;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MapperInvocationHandler implements InvocationHandler {

    private Object mapper;
    private Configuration configuration;

    public MapperInvocationHandler(Object mapper, Configuration configuration) {
        this.mapper = mapper;
        this.configuration = configuration;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        boolean clearDsKey = false;
        boolean clearDbType = false;
        try {
            //获取用户动态指定，由用户指定数据源，则应该有用户清除
            String environmentId = DataSourceKey.get();

            //通过 方法 的注解去获取
            if (StringUtil.isBlank(environmentId)) {
                environmentId = getMethodEnvironmentId(method);
                if (StringUtil.isNotBlank(environmentId)) {
                    DataSourceKey.use(environmentId);
                    clearDsKey = true;
                }
            }

            if (StringUtil.isBlank(environmentId)) {
                environmentId = configuration.getEnvironment().getId();
            }

            //优先获取用户自己配置的 dbType
            DbType dbType = DialectFactory.getHintDbType();
            if (dbType == null) {
                dbType = FlexGlobalConfig.getConfig(environmentId).getDbType();
                DialectFactory.setHintDbType(dbType);
                clearDbType = true;
            }
            return method.invoke(mapper, args);
        } finally {
            if (clearDbType) {
                DialectFactory.clearHintDbType();
            }
            if (clearDsKey) {
                DataSourceKey.clear();
            }
        }
    }


    private String getMethodEnvironmentId(Method method) {
        UseDataSource useDataSource = method.getAnnotation(UseDataSource.class);
        return useDataSource != null ? useDataSource.value() : null;
    }

}
