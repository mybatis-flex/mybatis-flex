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
package com.mybatisflex.core.datasource;

import com.mybatisflex.core.util.StringUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RoutingDataSource extends AbstractDataSource {

    private static Map<String, DataSource> dataSourceMap = new HashMap<>();

    private DataSource delegate;

    public RoutingDataSource(String environmentId, DataSource delegate) {
        this.delegate = delegate;
        dataSourceMap.put(environmentId, delegate);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getDataSource().getConnection(username, password);
    }

    private DataSource getDataSource() {
        DataSource dataSource = delegate;
        if (dataSourceMap.size() > 1) {
            String environmentId = DataSourceKey.get();
            if (StringUtil.isNotBlank(environmentId)) {
                dataSource = dataSourceMap.get(environmentId);
                if (dataSource == null) {
                    throw new IllegalStateException("Cannot get target DataSource for environmentId [" + environmentId + "]");
                }
            }
        }
        return dataSource;
    }

    public DataSource getDelegate() {
        return delegate;
    }

}
