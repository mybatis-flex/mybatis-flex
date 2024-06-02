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
package com.mybatisflex.core.audit;

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 审计管理器，统一执行如何和配置入口
 */
public class AuditManager {


    private AuditManager() {
    }


    private static MessageFactory messageFactory = new DefaultMessageFactory();

    private static boolean auditEnable = false;
    private static Clock clock = System::currentTimeMillis;
    private static MessageCollector messageCollector = new ScheduledMessageCollector();

    public static boolean isAuditEnable() {
        return auditEnable;
    }

    public static void setAuditEnable(boolean auditEnable) {
        AuditManager.auditEnable = auditEnable;
    }

    public static Clock getClock() {
        return clock;
    }

    public static void setClock(Clock clock) {
        AuditManager.clock = clock;
    }

    public static MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public static void setMessageFactory(MessageFactory messageFactory) {
        AuditManager.messageFactory = messageFactory;
    }

    public static MessageCollector getMessageCollector() {
        return messageCollector;
    }


    public static void setMessageReporter(MessageReporter messageReporter) {
        MessageCollector newMessageCollector = new ScheduledMessageCollector(10, messageReporter);
        setMessageCollector(newMessageCollector);
    }

    public static void setMessageCollector(MessageCollector messageCollector) {
        MessageCollector temp = AuditManager.messageCollector;
        AuditManager.messageCollector = messageCollector;
        releaseScheduledMessageCollector(temp);

    }

    private static void releaseScheduledMessageCollector(MessageCollector messageCollector) {
        if (messageCollector instanceof ScheduledMessageCollector) {
            ((ScheduledMessageCollector) messageCollector).release();
        }
    }

    @SuppressWarnings("rawtypes")
    public static <T> T startAudit(AuditRunnable<T> supplier, Statement statement, BoundSql boundSql, Configuration configuration) throws SQLException {
        AuditMessage auditMessage = messageFactory.create();
        if (auditMessage == null) {
            return supplier.execute();
        }
        String key = DataSourceKey.get();
        if (StringUtil.isBlank(key)) {
            key = FlexGlobalConfig.getDefaultConfig()
                .getDataSource()
                .getDefaultDataSourceKey();
        }
        auditMessage.setDsName(key);
        auditMessage.setQueryTime(clock.getTick());
        try {
            T result = supplier.execute();
            if (result instanceof Collection) {
                auditMessage.setQueryCount(((Collection) result).size());
            } else if (result instanceof Number) {
                auditMessage.setQueryCount(((Number) result).intValue());
            } else if (result != null) {
                auditMessage.setQueryCount(1);
            }
            return result;
        } finally {
            auditMessage.setElapsedTime(clock.getTick() - auditMessage.getQueryTime());
            auditMessage.setQuery(boundSql.getSql());
            Object parameter = boundSql.getParameterObject();

            /** parameter 的组装请查看 getNamedParams 方法
             * @see ParamNameResolver#getNamedParams(Object[])
             */
            if (parameter instanceof Map) {
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                if (((Map<?, ?>) parameter).containsKey(FlexConsts.SQL_ARGS)) {
                    auditMessage.addParams(statement, ((Map<?, ?>) parameter).get(FlexConsts.SQL_ARGS));
                } else if (((Map<?, ?>) parameter).containsKey("collection")) {
                    Collection collection = (Collection) ((Map<?, ?>) parameter).get("collection");
                    auditMessage.addParams(statement, collection.toArray());
                } else if (((Map<?, ?>) parameter).containsKey("array")) {
                    auditMessage.addParams(statement, ((Map<?, ?>) parameter).get("array"));
                } else {
                    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
                    for (ParameterMapping parameterMapping : parameterMappings) {
                        if (parameterMapping.getMode() != ParameterMode.OUT) {
                            Object value;
                            String propertyName = parameterMapping.getProperty();
                            if (boundSql.hasAdditionalParameter(propertyName)) {
                                value = boundSql.getAdditionalParameter(propertyName);
                            } else if (typeHandlerRegistry.hasTypeHandler(parameter.getClass())) {
                                value = parameter;
                            } else {
                                MetaObject metaObject = configuration.newMetaObject(parameter);
                                value = metaObject.getValue(propertyName);
                            }
                            auditMessage.addParams(statement, value);
                        }
                    }
                }
            }
            messageCollector.collect(auditMessage);
        }
    }


    @FunctionalInterface
    public interface AuditRunnable<T> {
        T execute() throws SQLException;
    }

}
