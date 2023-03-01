
# Mybatis-Flex is an elegant Mybatis Enhancement Framework.

## Features

- 1、Mybatis-Flex is very lightweight, and it only depends on Mybatis and no other third-party dependencies
- 2、Basic CRUD operator and paging query of Entity class
- 3、Row mapping support, you can add, delete, modify and query the database without entity classes
- 4、Support multiple databases, and expand through dialects flexibly.
- 5、Support combined primary keys and different primary key content generation strategies
- 6、Extremely friendly SQL query, IDE automatically prompts and no worries about mistakes
- 7、More little surprises

## hello world

**Entity Class**

```java

@Table("tb_account")
public class Account {

    @Id()
    private Long id;
    private String userName;
    private Date birthday;
    private int sex;

    //getter setter
}
```

**AccountMapper Class**

```java
public interface AccountMapper extends BaseMapper<Account> {
    //only Mapper interface define.
}
```

**Hello world**

e.g. 1： query 1 data
```java
class HelloWorld {
    public static void main(String... args) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mybatis-flex");
        dataSource.setUsername("username");
        dataSource.setPassword("password");

        MybatisFlexBootstrap.getInstance()
                .setDatasource(dataSource)
                .addMapper(AccountMapper.class)
                .start();


        //id=100
        Account account = MybatisFlexBootstrap.getInstance()
                .execute(AccountMapper.class, mapper ->
                        mapper.selectOneById(100)
                );
    }
}
```

e.g.2: query list

```java
class HelloWorld {
    public static void main(String... args) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mybatis-flex");
        dataSource.setUsername("username");
        dataSource.setPassword("password");

        MybatisFlexBootstrap.getInstance()
                .setDatasource(dataSource)
                .addMapper(AccountMapper.class)
                .start();
        
        //use QueryWrapper to build query conditions
        QueryWrapper query = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100))
                .and(ACCOUNT.USER_NAME.like("zhang").or(ACCOUNT.USER_NAME.like("li")));

        // execute SQL：
        // ELECT * FROM `tb_account`
        // WHERE `tb_account`.`id` >=  100
        // AND (`tb_account`.`user_name` LIKE '%zhang%' OR `tb_account`.`user_name` LIKE '%li%' )
        List<Account> accounts = MybatisFlexBootstrap.getInstance()
                .execute(AccountMapper.class, mapper ->
                        mapper.selectListByQuery(query)
                );
        
    }
}
```

e.g.3: paging query

```java
class HelloWorld {
    public static void main(String... args) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mybatis-flex");
        dataSource.setUsername("username");
        dataSource.setPassword("password");

        MybatisFlexBootstrap.getInstance()
                .setDatasource(dataSource)
                .addMapper(AccountMapper.class)
                .start();

        //use QueryWrapper to build query conditions
        QueryWrapper query = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100))
                .and(ACCOUNT.USER_NAME.like("张").or(ACCOUNT.USER_NAME.like("李")))
                .orderBy(ACCOUNT.ID.desc());

        // execute SQL：
        // ELECT * FROM `tb_account`
        // WHERE `tb_account`.`id` >=  100
        // AND (`tb_account`.`user_name` LIKE '%张%' OR `tb_account`.`user_name` LIKE '%李%' )
        // ORDER BY `tb_account`.`id` DESC
        // LIMIT 40,10
        Page<Account> accounts = MybatisFlexBootstrap.getInstance()
                .execute(AccountMapper.class, mapper ->
                        mapper.paginate(5, 10, query)
                );

    }
}
```

## More Samples

- 1、[Mybatis-Flex Only (Native)](./mybatis-flex-test/mybatis-flex-native-test)
- 2、[Mybatis-Flex with Spring](./mybatis-flex-test/mybatis-flex-spring-test)
- 3、[Mybatis-Flex with Spring boot](./mybatis-flex-test/mybatis-flex-spring-boot-test)
