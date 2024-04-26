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

import com.mybatisflex.core.mybatis.TypeHandlerObject;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.SqlUtil;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL 审计详细消息。
 */
public class AuditMessage implements Serializable {

    /**
     * 平台，或者是运行的应用。
     */
    private String platform;

    /**
     * 应用模块。
     */
    private String module;

    /**
     * 执行这个 SQL 涉及的 URL 地址。
     */
    private String url;

    /**
     * 自定义业务 ID。
     */
    private String bizId;

    /**
     * 执行这个 SQL 涉及的平台用户。
     */
    private String user;

    /**
     * 执行这个 SQL 的平台用户 IP 地址。
     */
    private String userIp;

    /**
     * 执行这个 SQL 的服务器 IP 地址。
     */
    private String hostIp;

    /**
     * SQL 内容。
     */
    private String query;

    /**
     * SQL 参数。
     */
    private List<Object> queryParams;

    /**
     * SQL 查询出来数据的数量。
     */
    private int queryCount;

    /**
     * SQL 执行的时间点（当前时间，毫秒）。
     */
    private long queryTime;

    /**
     * SQL 执行的消耗时间（毫秒）。
     */
    private long elapsedTime;

    /**
     * 数据库名称。
     */
    private String dsName;

    /**
     * 其他扩展元信息。
     */
    private Map<String, Object> metas;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Object> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<Object> queryParams) {
        this.queryParams = queryParams;
    }

    public void addParams(Statement statement, Object... objects) {
        if (queryParams == null) {
            queryParams = new ArrayList<>();
        }

        for (Object object : objects) {
            if (object != null && ClassUtil.isArray(object.getClass())) {
                for (int i = 0; i < Array.getLength(object); i++) {
                    Object value = Array.get(object, i);
                    if (value instanceof Map) {
                        ((Map<?, ?>) value).values().forEach(e -> doAddParam(statement, e));
                    } else {
                        doAddParam(statement, value);
                    }
                }
            } else {
                doAddParam(statement, object);
            }
        }
    }

    private void doAddParam(Statement statement, Object object) {
        try {
            if (object instanceof TypeHandlerObject) {
                ((TypeHandlerObject) object).setParameter(createPreparedStatement(statement), 0);
            } else if (object instanceof java.sql.Array) {
                Object array = ((java.sql.Array) object).getArray();
                queryParams.add(array);
            } else {
                queryParams.add(object);
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    public String getFullSql() {
        List<Object> queryParams = getQueryParams();
        return SqlUtil.replaceSqlParams(getQuery(), queryParams == null ? null : queryParams.toArray());
    }

    private PreparedStatement createPreparedStatement(Statement statement) {
        return (PreparedStatement) Proxy.newProxyInstance(
            AuditMessage.class.getClassLoader(),
            new Class[]{PreparedStatement.class}, (proxy, method, args) -> {
                if (args != null && (args.length == 2 || args.length == 3)) {
                    doAddParam(statement, args[1]);
                } else if ("getConnection".equals(method.getName())) {
                    return statement.getConnection();
                }
                return null;
            });
    }

    public int getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(int queryCount) {
        this.queryCount = queryCount;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(long queryTime) {
        this.queryTime = queryTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Map<String, Object> getMetas() {
        return metas;
    }

    public void setMetas(Map<String, Object> metas) {
        this.metas = metas;
    }

    public void addMeta(String key, Object value) {
        if (metas == null) {
            metas = new HashMap<>();
        }
        metas.put(key, value);
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    @Override
    public String toString() {
        return "AuditMessage{" +
            "platform='" + platform + '\'' +
            ", module='" + module + '\'' +
            ", url='" + url + '\'' +
            ", bizId='" + bizId + '\'' +
            ", user='" + user + '\'' +
            ", userIp='" + userIp + '\'' +
            ", hostIp='" + hostIp + '\'' +
            ", query='" + query + '\'' +
            ", queryParams=" + queryParams +
            ", queryCount=" + queryCount +
            ", queryTime=" + queryTime +
            ", elapsedTime=" + elapsedTime +
            ", dsName=" + dsName +
            ", metas=" + metas +
            '}';
    }

}
