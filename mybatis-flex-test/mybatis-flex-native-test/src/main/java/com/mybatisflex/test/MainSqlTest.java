package com.mybatisflex.test;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.core.table.TableManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.mapper.ArticleMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class MainSqlTest {

    public static void main(String[] args) {




        FlexGlobalConfig globalConfig = FlexGlobalConfig.getDefaultConfig();


        Environment environment = new Environment("test", new JdbcTransactionFactory(), new HikariDataSource());
        FlexConfiguration configuration = new FlexConfiguration(environment);
        globalConfig.setConfiguration(configuration);
        FlexGlobalConfig.setConfig("test", globalConfig, true);


        configuration.addMapper(ArticleMapper.class);






//        ArticleMapper mapper = (ArticleMapper) Proxy.newProxyInstance(MainSqlTest.class.getClassLoader(),
//            new Class[]{ArticleMapper.class}, new InvocationHandler() {
//                @Override
//                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                    return null;
//                }
//            });
//
//
//        String sql1 = QueryChain.of(mapper)
//            .where(Article::getId).eq(100)
//            .toSQL();
//
//        System.out.println(sql1);


        TableManager.setHintTableMapping("tb_article","tb_article1");


        String sql2 = UpdateChain.of(Article.class)
            .set("xxxx", "xxxx")
            .where(Article::getId).ge(100)
            .toSQL();

        System.out.println(sql2);
    }
}
