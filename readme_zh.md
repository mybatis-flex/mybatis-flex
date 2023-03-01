
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

**第一步：编写 Entity 实体类**

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

**第二步，编写 Mapper 类，并继承 BaseMapper**

```java
public interface AccountMapper extends BaseMapper<Account> {
    //只需定义 Mapper 接口即可，可以无任何内容。
}
```

**第三步：开始查询数据**

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

## QueryWrapper 示例

### select *

```java
QueryWrapper query=new QueryWrapper();
query.select().from(ACCOUNT)

// SQL: 
// SELECT * FROM tb_account
```

### select columns

```java
QueryWrapper query=new QueryWrapper();
query.select(ACCOUNT.ID,ACCOUNT.USER_NAME).from(ACCOUNT)

// SQL: 
// SELECT tb_account.id, tb_account.user_name 
// FROM tb_account
```

```java
QueryWrapper query=new QueryWrapper();
query.select(ACCOUNT.ALL_COLUMNS).from(ACCOUNT)

// SQL: 
// SELECT tb_account.id, tb_account.user_name, tb_account.birthday, 
// tb_account.sex, tb_account.is_normal 
// FROM tb_account
```

### select functions

```java
 QueryWrapper query=new QueryWrapper()
        .select(
            ACCOUNT.ID,
            ACCOUNT.USER_NAME,
            max(ACCOUNT.BIRTHDAY),
            avg(ACCOUNT.SEX).as("sex_avg")
        ).from(ACCOUNT);


// SQL: 
// SELECT tb_account.id, tb_account.user_name, 
// MAX(tb_account.birthday), 
// AVG(tb_account.sex) AS sex_avg 
// FROM tb_account
```

### where

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(ACCOUNT.USER_NAME.like("michael"));

// SQL: 
// SELECT * FROM tb_account 
// WHERE tb_account.id >=  ?  
// AND tb_account.user_name LIKE  ? 
```

### exists, not exists

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(
        exist(
            selectOne().from(ARTICLE).where(ARTICLE.ID.ge(100))
        )
    );

// SQL: 
// SELECT * FROM tb_account 
// WHERE tb_account.id >=  ?  
// AND EXIST (
// SELECT 1 FROM tb_article WHERE tb_article.id >=  ? 
// )
```

### and (...) or (...)

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(ACCOUNT.SEX.eq(1).or(ACCOUNT.SEX.eq(2)))
    .or(ACCOUNT.AGE.in(18,19,20).or(ACCOUNT.USER_NAME.like("michael")));

// SQL: 
// SELECT * FROM tb_account 
// WHERE tb_account.id >=  ?  
// AND (tb_account.sex =  ?  OR tb_account.sex =  ? ) 
// OR (tb_account.age IN (?,?,?) OR tb_account.user_name LIKE  ? )
```

### group by

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .groupBy(ACCOUNT.USER_NAME);

// SQL: 
// SELECT * FROM tb_account 
// GROUP BY tb_account.user_name
```

### having

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .groupBy(ACCOUNT.USER_NAME)
    .having(ACCOUNT.AGE.between(18,25));

// SQL: 
// SELECT * FROM tb_account 
// GROUP BY tb_account.user_name 
// HAVING tb_account.age BETWEEN  ? AND ?
```

### jion
```java
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
    .where(ACCOUNT.AGE.ge(10));

// SQL: 
// SELECT * FROM tb_account 
// LEFT JOIN tb_article 
// ON tb_account.id = tb_article.account_id 
// WHERE tb_account.age >=  ? 

```

### 可能存在问题

**如何通过实体类 Account.java 生成 QueryWrapper 所需要的 "ACCOUNT" 类 ?**

答：通过开发工具构建项目（如下图），或者执行 maven 编译命令: `mvn clean package`

![](./docs/assets/images/build_idea.png)

## Db 工具类

Db 是一个极度轻量的数据库操作工具类，以下是一些示例：

```java
String sql = "insert into tb_account(id,name) value (?, ?)";
Db.insertBySql(sql,1,"michael");

Row account = new Row();
account.set("id",100);
account.set("name","Michael");
Db.insertRow("tb_account",account);
```
> Db 工具类还提供了更多 增、删、改、查和分页查询等方法。
> 
> 具体参考： [Db.java](./mybatis-flex-core/src/main/java/com/mybatisflex/core/row/Db.java) 。

## Entity 更新部分字段

这部分单独拿来强调一下，是因为 MyBatis-Flex 等和其他框架的方法不太一样。在 BaseMapper 中，Mybatis-Flex 提供了如下的方法：

```java
update(T entity,  boolean ignoreNulls)
```
- 第一个参数是 entity 的对象。
- 第二个参数是是否忽略 null 值。

但是有些场景下，我们可能希望值更新 几个 字段，甚至其中个别字段需要更新为 null。此时需要用到 UpdateEntity 工具类，以下是示例代码：

```java
Account account = UpdateEntity.of(Account.class);
account.setId(1);
account.setUserName(null);
account.setSex(1);

accountMapper.update(account,false);
```
以上的示例中，会把 id 为 1 的数据中的 user_name 字段更新为 null，sex 字段更新为 1，其他字段不会被更新。也就是说，通过 UpdateEntity
构建的对象，只会更新调用了 setter 方法的值，不调用 setter 方法的，不管这个对象里的属性的值是什么，都不会更新到数据库。

其生成的 sql 内容如下：

```sql
update tb_account set user_name = ? ,sex = ? where id = ?
#params: null,1,1
```


## 更多示例

- 1、[Mybatis-Flex 原生（无其他依赖）](./mybatis-flex-test/mybatis-flex-native-test)
- 2、[Mybatis-Flex with Spring](./mybatis-flex-test/mybatis-flex-spring-test)
- 3、[Mybatis-Flex with Spring boot](./mybatis-flex-test/mybatis-flex-spring-boot-test)


