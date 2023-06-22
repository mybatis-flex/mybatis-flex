package com.mybatisflex.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.datasource.DataSourceDecipher;
import com.mybatisflex.core.datasource.DataSourceProperty;
import com.mybatisflex.core.datasource.DataSourceManager;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowUtil;
import com.mybatisflex.mapper.TenantAccountMapper;

import java.util.List;

public class DataSourceDecipherTester {

    public static void main(String[] args) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/flex_test");
        dataSource.setUsername("root123");
        dataSource.setPassword("123456---0000");

        DataSourceManager.setDecipher(new DataSourceDecipher() {
            @Override
            public String decrypt(DataSourceProperty property, String value) {
                if (property == DataSourceProperty.USERNAME) {
                    return value.substring(0, 4);
                } else if (property == DataSourceProperty.PASSWORD) {
                    return value.substring(0, 6);
                }
                return value;
            }
        });

        MybatisFlexBootstrap.getInstance()
                .setDataSource(dataSource)
                .addMapper(TenantAccountMapper.class)
                .start();

        List<Row> rowList = Db.selectAll("tb_account");
        RowUtil.printPretty(rowList);
    }

}
