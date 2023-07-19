package com.mybatisflex.test;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.ConsoleMessageCollector;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.mapper.Entity04Mapper;
import com.mybatisflex.test.table.Entity04TableDef;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.List;

public class QueryWrapperTest {

    public static void main(String[] args) {
        DataSource dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("schema04.sql")
                .addScript("data04.sql").build();

        MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance().setDataSource(dataSource)
                .addMapper(Entity04Mapper.class).start();

        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        MessageCollector collector = new ConsoleMessageCollector();
        AuditManager.setMessageCollector(collector);

        Entity04Mapper mapper = bootstrap.getMapper(Entity04Mapper.class);

        Entity04 entity04 = new Entity04();
        entity04.setId("1");
        entity04.setAge(200);

        mapper.insertSelective(entity04);

        System.out.println("--------------------selectListByQuery");
        QueryWrapper queryWrapper = QueryWrapper.create(entity04);
        List<Entity04> entity04s = mapper.selectListByQuery(queryWrapper);
        System.out.println(entity04s);

        System.out.println(Entity04TableDef.ENTITY04.AGE);
    }

}
