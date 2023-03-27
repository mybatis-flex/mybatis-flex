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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AuditMessage implements Serializable {

    private String platform;
    private String module;
    private String url;

    private String user;
    private String userIP;
    private String hostIp;

    private String query;
    private List<Object> queryParams;

    private long extTime;     // Sql 执行的当前时间，单位毫秒
    private long elapsedTime; // Sql 执行消耗的时间，单位毫秒


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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserIP() {
        return userIP;
    }

    public void setUserIP(String userIP) {
        this.userIP = userIP;
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

    public void addParams(Object... objects) {
        if (queryParams == null) {
            queryParams = new ArrayList<>();
        }
        for (Object object : objects) {
            if (object != null && (object.getClass().isArray()
                    || object.getClass() == int[].class
                    || object.getClass() == long[].class
                    || object.getClass() == short[].class
                    || object.getClass() == float[].class
                    || object.getClass() == double[].class)
            ) {
                for (int i = 0; i < Array.getLength(object); i++) {
                    addParams(Array.get(object, i));
                }
            } else {
                queryParams.add(object);
            }
        }
    }

    public long getExtTime() {
        return extTime;
    }

    public void setExtTime(long extTime) {
        this.extTime = extTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }


    @Override
    public String toString() {
        return "AuditMessage{" +
                "platform='" + platform + '\'' +
                ", module='" + module + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", userIP='" + userIP + '\'' +
                ", hostIp='" + hostIp + '\'' +
                ", query='" + query + '\'' +
                ", queryParams=" + queryParams +
                ", extTime=" + extTime +
                ", elapsedTime=" + elapsedTime +
                '}';
    }
}
