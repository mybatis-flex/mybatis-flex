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
package com.mybatisflex.core.mybatis;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.exception.FlexExceptions;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import com.mybatisflex.core.util.MapUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取 {@link BaseMapper} 对象。
 *
 * @author michael
 * @author 王帅
 */
@SuppressWarnings("unchecked")
public class Mappers {

    private Mappers() {
    }

    private static final Map<Class<?>, Object> MAPPER_OBJECTS = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Class<?>> ENTITY_MAPPER_MAP = new ConcurrentHashMap<>();

    /**
     * 添加 实体类 与 {@link BaseMapper} 接口实现接口 对应，两者皆为非动态代理类。
     *
     * @param entityClass 实体类
     * @param mapperClass {@link BaseMapper} 实现接口
     */
    static void addMapping(Class<?> entityClass, Class<?> mapperClass) {
        ENTITY_MAPPER_MAP.put(entityClass, mapperClass);
    }

    /**
     * 通过 实体类 获取对应 {@link BaseMapper} 对象。
     *
     * @param entityClass 实体类
     * @param <E>         实体类类型
     * @return {@link BaseMapper} 对象
     */
    public static <E> BaseMapper<E> ofEntityClass(Class<E> entityClass) {
        Class<?> mapperClass = ENTITY_MAPPER_MAP.get(entityClass);
        if (mapperClass == null) {
            throw FlexExceptions.wrap("Can not find MapperClass by entity: " + entityClass.getName());
        }
        return (BaseMapper<E>) ofMapperClass(mapperClass);
    }

    /**
     * 通过 {@link BaseMapper} 接口实现的 Class 引用直接获取 {@link BaseMapper} 代理对象。
     *
     * @param mapperClass {@link BaseMapper} 接口实现
     * @return {@link BaseMapper} 对象
     */
    public static <M> M ofMapperClass(Class<M> mapperClass) {
        Object mapperObject = MapUtil.computeIfAbsent(MAPPER_OBJECTS, mapperClass, clazz ->
            Proxy.newProxyInstance(mapperClass.getClassLoader()
                , new Class[]{mapperClass}
                , new MapperHandler(mapperClass)));
        return (M) mapperObject;
    }

    private static class MapperHandler implements InvocationHandler {

        private final Class<?> mapperClass;
        private final ExecutorType executorType;
        private final SqlSessionFactory sqlSessionFactory;

        public MapperHandler(Class<?> mapperClass) {
            this.mapperClass = mapperClass;
            this.executorType = FlexGlobalConfig.getDefaultConfig()
                .getConfiguration()
                .getDefaultExecutorType();
            this.sqlSessionFactory = FlexGlobalConfig.getDefaultConfig()
                .getSqlSessionFactory();
        }

        private SqlSession openSession() {
            return sqlSessionFactory.openSession(executorType, true);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try (SqlSession sqlSession = openSession()) {
                Object mapper = sqlSession.getMapper(mapperClass);
                return method.invoke(mapper, args);
            } catch (Throwable throwable) {
                throw ExceptionUtil.unwrapThrowable(throwable);
            }
        }

    }

}
