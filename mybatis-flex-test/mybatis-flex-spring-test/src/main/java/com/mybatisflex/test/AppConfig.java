package com.mybatisflex.test;

import com.mybatisflex.core.row.Db;
import com.mybatisflex.spring.FlexSqlSessionFactoryBean;
import com.mybatisflex.spring.SpringRowSessionManager;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.mybatisflex.test.mapper")
public class AppConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        // SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        SqlSessionFactoryBean factoryBean = new FlexSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        return factoryBean.getObject();
    }

    @EventListener(classes = {ContextStartedEvent.class})
    public void handleContextStartedEvent() {
        System.out.println("handleContextStartedEvent listener invoked!");

        // 为 Db 设置默认的 SqlSession
        Db.invoker().setRowSessionManager(new SpringRowSessionManager());
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("onApplicationEvent");
        // 为 Db 设置默认的 SqlSession
        Db.invoker().setRowSessionManager(new SpringRowSessionManager());
    }
}
