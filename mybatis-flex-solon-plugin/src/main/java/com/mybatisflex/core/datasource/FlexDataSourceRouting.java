package com.mybatisflex.core.datasource;

import org.noear.solon.data.datasource.RoutingDataSource;

import javax.sql.DataSource;

/**
 * 数据源路由工具（用于确定当前真实数据源）
 *
 * @author noear 2024/12/3 created
 */
public class FlexDataSourceRouting {
    public static DataSource determineCurrentTarget(DataSource original) {
        if (original instanceof FlexDataSource) {
            return determineCurrentTarget(((FlexDataSource) original).getDataSource());
        }

        if (original instanceof RoutingDataSource) {
            return determineCurrentTarget(((RoutingDataSource) original).determineCurrentTarget());
        }

        return original;
    }
}
