
# Mybatis-Flex 一个优雅的 Mybatis 增强框架

## 特征

- 1、很轻量，整个框架只依赖 Mybatis 再无其他第三方依赖
- 2、Entity 类的基本增删改查、以及分页查询
- 3、Row 通用映射支持，可以无需实体类对数据库进行增删改查
- 4、支持多种数据库类型，自由通过方言持续扩展
- 5、支持联合主键，以及不同的主键内容生成策略
- 6、极其友好的 SQL 联动查询，IDE 自动提示不再担心出错
- 7、更多小惊喜

## hello world

**实体类**

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

**AccountMapper 类**

```java
public interface AccountMapper extends BaseMapper<Account> {
    //只需定义 Mapper 接口即可，可以无任何内容。
}
```

**Hello world**

示例 1：查询 1 条数据
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


        //示例1：查询 id=100 条数据
        Account account = MybatisFlexBootstrap.getInstance()
                .execute(AccountMapper.class, mapper ->
                        mapper.selectOneById(100)
                );
    }
}
```

示例2：查询列表

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
        
        //示例2：通过 QueryWrapper 构建条件查询数据列表
        QueryWrapper query = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100))
                .and(ACCOUNT.USER_NAME.like("张").or(ACCOUNT.USER_NAME.like("李")));

        // 执行 SQL：
        // ELECT * FROM `tb_account`
        // WHERE `tb_account`.`id` >=  100
        // AND (`tb_account`.`user_name` LIKE '%张%' OR `tb_account`.`user_name` LIKE '%李%' )
        List<Account> accounts = MybatisFlexBootstrap.getInstance()
                .execute(AccountMapper.class, mapper ->
                        mapper.selectListByQuery(query)
                );
        
    }
}
```

示例3：分页查询
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

        // 示例3：分页查询
        // 查询第 5 页，每页 10 条数据，通过 QueryWrapper 构建条件查询
        QueryWrapper query = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.ge(100))
                .and(ACCOUNT.USER_NAME.like("张").or(ACCOUNT.USER_NAME.like("李")))
                .orderBy(ACCOUNT.ID.desc());

        // 执行 SQL：
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

## 更多示例

- 1、[Mybatis-Flex 原生（无其他依赖）](./mybatis-flex-test/mybatis-flex-native-test)
- 2、[Mybatis-Flex with Spring](./mybatis-flex-test/mybatis-flex-spring-test)
- 3、[Mybatis-Flex with Spring boot](./mybatis-flex-test/mybatis-flex-spring-boot-test)