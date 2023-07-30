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
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author michael
 */
@SuppressWarnings("unchecked")
public class Mappers {

    private static final Map<Class<?>, Class<?>> ENTITY_MAPPER_MAP = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Object> MAPPER_OBJECTS = new ConcurrentHashMap<>();


    static void addMapping(Class<?> entityClass, Class<?> mapperClass) {
        ENTITY_MAPPER_MAP.put(entityClass, mapperClass);
    }


    /**
     * 通过 entity Class 获取 Mapper 对象
     *
     * @param entityClass
     * @param <Entity>
     * @return mapper 对象
     */
    public static <Entity> BaseMapper<Entity> ofEntityClass(Class<Entity> entityClass) {
        Class<?> mapperClass = ENTITY_MAPPER_MAP.get(entityClass);
        if (mapperClass == null) {
            throw FlexExceptions.wrap("Can not find MapperClass by entity: " + entityClass.getName());
        }
        return (BaseMapper<Entity>) ofMapperClass(mapperClass);
    }


    /**
     * 通过 mapperClass 直接获取 mapper 对象执行
     *
     * @param mapperClass
     * @return mapperObject
     */
    public static <Mapper> Mapper ofMapperClass(Class<Mapper> mapperClass) {
        Object mapperObject = MapUtil.computeIfAbsent(MAPPER_OBJECTS, mapperClass, clazz ->
            Proxy.newProxyInstance(mapperClass.getClassLoader()
                , new Class[]{mapperClass}
                , new MapperHandler(mapperClass)));
        return (Mapper) mapperObject;
    }


    static class MapperHandler implements InvocationHandler {

        private Class<?> mapperClass;
        private final SqlSessionFactory sqlSessionFactory = FlexGlobalConfig.getDefaultConfig().getSqlSessionFactory();
        private final ExecutorType executorType = FlexGlobalConfig.getDefaultConfig().getConfiguration().getDefaultExecutorType();

        public MapperHandler(Class<?> mapperClass) {
            this.mapperClass = mapperClass;
        }

        private SqlSession openSession() {
            return sqlSessionFactory.openSession(executorType, true);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try (SqlSession sqlSession = openSession()) {
                Object mapper = sqlSession.getMapper(mapperClass);
                return method.invoke(mapper, args);
            }
        }
    }

}
