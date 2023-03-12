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
package com.mybatisflex.core;

import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.annotation.KeyType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局配置文件
 */
public class FlexGlobalConfig {

    /**
     * 默认使用 Mysql 数据库类型
     */
    private DbType dbType = DbType.MYSQL;

    /**
     * 创建好的 sqlSessionFactory
     */
    private SqlSessionFactory sqlSessionFactory;


    /**
     * 全局的 ID 生成策略配置，当 @Id 未配置 或者 配置 KeyType 为 None 时
     * 使用当前全局配置
     */
    private KeyConfig keyConfig;


    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public KeyConfig getKeyConfig() {
        return keyConfig;
    }

    public void setKeyConfig(KeyConfig keyConfig) {
        this.keyConfig = keyConfig;
    }

    /**
     * 对应的是 注解 {@link com.mybatisflex.annotation.Id} 的配置
     */
    public static class KeyConfig {
        private KeyType keyType;
        private String value;
        private boolean before = true;

        public KeyType getKeyType() {
            return keyType;
        }

        public void setKeyType(KeyType keyType) {
            this.keyType = keyType;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isBefore() {
            return before;
        }

        public void setBefore(boolean before) {
            this.before = before;
        }
    }


    /////static factory methods/////
    private static ConcurrentHashMap<String, FlexGlobalConfig> globalConfigs = new ConcurrentHashMap();
    private static FlexGlobalConfig defaultConfig;

    public static FlexGlobalConfig getDefaultConfig() {
        return defaultConfig;
    }

    public static void setDefaultConfig(FlexGlobalConfig config) {
        defaultConfig = config;
    }

    public static FlexGlobalConfig getConfig(Configuration configuration) {
        return globalConfigs.get(configuration.getEnvironment().getId());
    }
    
    public static FlexGlobalConfig getConfig(String environmentId) {
        return globalConfigs.get(environmentId);
    }

    public static void setConfig(String id, FlexGlobalConfig config) {
        if (getDefaultConfig() == null) {
            setDefaultConfig(config);
        }
        globalConfigs.put(id, config);
    }


}
