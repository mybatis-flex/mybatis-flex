package com.mybatisflex.solon.integration;

import com.mybatisflex.core.datasource.FlexDataSource;
import org.noear.solon.data.datasource.RoutingDataSource;

import javax.sql.DataSource;

/**
 * 用与 solon 的事务对接
 *
 * @author noear
 * @since 2.8
 */
public class FlexDataSourcePlus extends FlexDataSource implements RoutingDataSource {
    public FlexDataSourcePlus(String dataSourceKey, DataSource dataSource) {
        super(dataSourceKey, dataSource);
    }

    public FlexDataSourcePlus(String dataSourceKey, DataSource dataSource, boolean needDecryptDataSource) {
        super(dataSourceKey, dataSource, needDecryptDataSource);
    }

    @Override
    public DataSource determineCurrentTarget() {
        return getDataSource();
    }
}
