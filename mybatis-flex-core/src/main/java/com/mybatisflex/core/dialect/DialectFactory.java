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
package com.mybatisflex.core.dialect;


import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.core.dialect.impl.OracleDialect;
import com.mybatisflex.core.util.ObjectUtil;
import org.apache.ibatis.util.MapUtil;

import java.util.EnumMap;
import java.util.Map;

/**
 * 方言工厂类，用于创建方言
 */
public class DialectFactory {

    /**
     * 数据库类型和方言的映射关系，可以通过其读取指定的方言，亦可能通过其扩展其他方言
     * 比如，在 mybatis-flex 实现的方言中有 bug 或者 有自己的独立实现，可以添加自己的方言实现到
     * 此 map 中，用于覆盖系统的方言实现
     */
    private static Map<DbType, IDialect> dialectMap = new EnumMap<>(DbType.class);

    /**
     * 通过设置当前线程的数据库类型，以达到在代码执行时随时切换方言的功能
     */
    private static ThreadLocal<DbType> dbTypeThreadLocal = new ThreadLocal<>();


    /**
     * 获取方言
     *
     * @return IDialect
     */
    public static IDialect getDialect() {
        DbType dbType = ObjectUtil.requireNonNullElse(dbTypeThreadLocal.get(), FlexGlobalConfig.getDefaultConfig().getDbType());
        return MapUtil.computeIfAbsent(dialectMap, dbType, DialectFactory::createDialect);
    }

    /**
     * 设置当前线程的 dbType
     *
     * @param dbType
     */
    public static void setHintDbType(DbType dbType) {
        dbTypeThreadLocal.set(dbType);
    }

    /**
     * 获取当前线程的 dbType
     *
     * @return dbType
     */
    public static DbType getHintDbType() {
        return dbTypeThreadLocal.get();
    }


    /**
     * 清除当前线程的 dbType
     */
    public static void clearHintDbType() {
        dbTypeThreadLocal.remove();
    }


    /**
     * 可以为某个 dbType 注册（新增或覆盖）自己的方言
     *
     * @param dbType  数据库类型
     * @param dialect 方言的实现
     */
    public static void registerDialect(DbType dbType, IDialect dialect) {
        dialectMap.put(dbType, dialect);
    }


    private static IDialect createDialect(DbType dbType) {
        switch (dbType) {
            case MYSQL:
            case H2:
            case MARIADB:
            case GBASE:
            case OSCAR:
            case XUGU:
            case CLICK_HOUSE:
            case OCEAN_BASE:
            case CUBRID:
            case GOLDILOCKS:
            case CSIIDB:
                return new CommonsDialectImpl(KeywordWrap.BACKQUOTE, LimitOffsetProcesser.MYSQL);
            case ORACLE:
                return new OracleDialect(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.ORACLE);
            case DM:
            case GAUSS:
                return new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.ORACLE);
            case POSTGRE_SQL:
            case SQLITE:
            case HSQL:
            case KINGBASE_ES:
            case PHOENIX:
            case SAP_HANA:
            case IMPALA:
            case HIGH_GO:
            case VERTICA:
            case REDSHIFT:
            case OPENGAUSS:
            case TDENGINE:
            case UXDB:
                return new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.POSTGRESQL);
            case ORACLE_12C:
                return new OracleDialect(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.DERBY);
            case FIREBIRD:
                return new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.DERBY);
            case SQLSERVER:
                return new CommonsDialectImpl(KeywordWrap.SQUARE_BRACKETS, LimitOffsetProcesser.DERBY);
            case SQLSERVER_2005:
                return new CommonsDialectImpl(KeywordWrap.SQUARE_BRACKETS, LimitOffsetProcesser.DB2);
            case INFORMIX:
                return new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.INFORMIX);
            case DB2:
                return new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.DB2);
            case SYBASE:
                return new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.SYBASE);
            default:
                return new CommonsDialectImpl();
        }
    }
}
