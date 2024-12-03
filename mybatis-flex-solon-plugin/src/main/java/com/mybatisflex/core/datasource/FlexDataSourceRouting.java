package com.mybatisflex.core.datasource;

import org.noear.solon.data.datasource.RoutingDataSource;

import javax.sql.DataSource;

/**
 * @author noear 2024/12/3 created
 */
public class FlexDataSourceRouting {
    public static DataSource determineCurrentTarget(DataSource original) {
        if (original instanceof FlexDataSource) {
            return ((FlexDataSource) original).getDataSource();
        }

        if (original instanceof RoutingDataSource) {
            return ((RoutingDataSource) original).determineCurrentTarget();
        }

        return original;
    }
}
