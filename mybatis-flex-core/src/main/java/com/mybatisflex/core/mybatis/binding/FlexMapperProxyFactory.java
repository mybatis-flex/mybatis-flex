/*
 *    Copyright 2009-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.mybatisflex.core.mybatis.binding;

import com.mybatisflex.core.mybatis.FlexConfiguration;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lasse Voss
 * @author Michael Yang
 */
public class FlexMapperProxyFactory<T> {

    private final Class<T> mapperInterface;
    private final Map<Method, MybatisMapperProxy.MapperMethodInvoker> methodCache = new ConcurrentHashMap<>();

    public FlexMapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    public Map<Method, MybatisMapperProxy.MapperMethodInvoker> getMethodCache() {
        return methodCache;
    }

    @SuppressWarnings("unchecked")
    protected T newInstance(FlexMapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface}, mapperProxy);
    }

    public T newInstance(SqlSession sqlSession, FlexConfiguration configuration) {
        final FlexMapperProxy<T> mapperProxy = new FlexMapperProxy<>(sqlSession, mapperInterface, methodCache, configuration);
        return newInstance(mapperProxy);
    }

}
