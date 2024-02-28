package com.mybatisflex.test;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.datasource.DataSourceKey;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowUtil;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * @author life
 */
public class MultiThreadsTest {

    public static void main(String[] args) {
        DataSourceKey.setAnnotationKeyThreadLocal(new TransmittableThreadLocal<>());
        DataSourceKey.setManualKeyThreadLocal(new TransmittableThreadLocal<>());
        DataSource dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("db1")
            .addScript("schema.sql")
            .addScript("data.sql")
                .setScriptEncoding("UTF-8")
            .build();

        HikariDataSource dataSource2 = new HikariDataSource();
        dataSource2.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/flex_test?characterEncoding=utf-8");
        dataSource2.setUsername("root");
        dataSource2.setPassword("131496");

        MybatisFlexBootstrap.getInstance()
            .setDataSource(dataSource)
            .addMapper(AccountMapper.class)
            .addDataSource("ds2", dataSource2)
            .start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);

        //默认查询 db1
        System.out.println("\n------ds1");
        List<Row> rows1 = Db.selectAll(null, "tb_account");
        RowUtil.printPretty(rows1);

        System.out.println("\n------ds2");
        DataSourceKey.use("ds2");
        new Thread(() -> {
            //查询数据源 ds2
            System.out.println("\n------Thread-ds2");
            List<Row> rows = Db.selectAll(null, "tb_account");
            RowUtil.printPretty(rows);
        }).start();



    }

}
