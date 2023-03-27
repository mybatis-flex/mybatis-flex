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
package com.mybatisflex.core.audit;

import com.mybatisflex.core.FlexConsts;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.reflection.ParamNameResolver;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public class AuditManager {

    private static AuditTimeCreator auditTimeCreator = System::currentTimeMillis;
    private static AuditMessageCreator auditMessageCreator = new DefaultAuditMessageCreator();
    private static AuditMessageCollector auditMessageCollector = new DefaultAuditMessageCollector();


    public static AuditTimeCreator getAuditTimeCreator() {
        return auditTimeCreator;
    }

    public static void setAuditTimeCreator(AuditTimeCreator auditTimeCreator) {
        AuditManager.auditTimeCreator = auditTimeCreator;
    }

    public static AuditMessageCreator getAuditMessageCreator() {
        return auditMessageCreator;
    }

    public static void setAuditMessageCreator(AuditMessageCreator auditMessageCreator) {
        AuditManager.auditMessageCreator = auditMessageCreator;
    }

    public static AuditMessageCollector getAuditMessageCollector() {
        return auditMessageCollector;
    }

    public static void setAuditMessageCollector(AuditMessageCollector auditMessageCollector) {
        AuditManager.auditMessageCollector = auditMessageCollector;
    }

    public static <T> T startAudit(AuditRunnable<T> supplier, BoundSql boundSql) throws SQLException {
        AuditMessage auditMessage = auditMessageCreator.create();
        if (auditMessage == null) {
            return supplier.execute();
        }
        auditMessage.setExtTime(auditTimeCreator.now());
        try {
            return supplier.execute();
        } finally {
            auditMessage.setElapsedTime(auditTimeCreator.now() - auditMessage.getExtTime());
            auditMessage.setQuery(boundSql.getSql());

            Object parameter = boundSql.getParameterObject();

            /** parameter 的组装请查看 getNamedParams 方法
             * @see ParamNameResolver#getNamedParams(Object[])
             */
            if (parameter instanceof Map) {
                if (((Map<?, ?>) parameter).containsKey(FlexConsts.SQL_ARGS)) {
                    auditMessage.addParams(((Map<?, ?>) parameter).get(FlexConsts.SQL_ARGS));
                } else if (((Map<?, ?>) parameter).containsKey("collection")) {
                    Collection collection = (Collection) ((Map<?, ?>) parameter).get("collection");
                    auditMessage.addParams(collection.toArray());
                } else if (((Map<?, ?>) parameter).containsKey("array")) {
                    auditMessage.addParams(((Map<?, ?>) parameter).get("array"));
                } else {
                    for (int i = 1; i <= 100; i++) {
                        if (((Map<?, ?>) parameter).containsKey(ParamNameResolver.GENERIC_NAME_PREFIX + i)) {
                            auditMessage.addParams(((Map<?, ?>) parameter).get(ParamNameResolver.GENERIC_NAME_PREFIX + i));
                        }
                    }
                }
            }
            auditMessageCollector.collect(auditMessage);
        }
    }


    @FunctionalInterface
    public interface AuditRunnable<T> {
        T execute() throws SQLException;
    }

}
