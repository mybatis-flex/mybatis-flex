![](./docs/assets/images/logo.png)


# Mybatis-Flex： 更灵活、更轻量、更好用


## 特征

- 1、很轻量，整个框架只依赖 Mybatis 再无其他第三方依赖
- 2、只增强，支持 Entity 的增删改查、及分页查询，但不丢失 Mybatis 原有功能
- 3、内置 Db + Row 工具，可以无需实体类对数据库进行增删改查
- 4、支持多种数据库类型，还可以通过方言持续扩展
- 5、支持多（联合）主键，以及不同的主键内容生成策略
- 6、支持逻辑删除设置、更新或插入的默认值配置以及大字段等设置
- 7、支持乐观锁字段配置，在数据更新时自动进行乐观锁检测
- 8、极其友好的 SQL 联动查询，IDE 自动提示不再担心出错
- 9、更多小惊喜

## QQ 群

群号： 532992631

![](./docs/assets/images/qq_group.png)

## 开始

- [快速开始](https://mybatis-flex.com/zh/getting-started.html)
- 示例 1：[Mybatis-Flex 原生（非 Spring）](./mybatis-flex-test/mybatis-flex-native-test)
- 示例 2：[Mybatis-Flex with Spring](./mybatis-flex-test/mybatis-flex-spring-test)
- 示例 3：[Mybatis-Flex with Spring boot](./mybatis-flex-test/mybatis-flex-spring-boot-test)
- 示例 4：[Db + Row](./mybatis-flex-test/mybatis-flex-native-test/src/main/java/com/mybatisflex/test/DbTestStarter.java)



## hello world（原生）

**第 1 步：编写 Entity 实体类**

```java

@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;
    private Date birthday;
    private int sex;

    //getter setter
}
```


**第 2 步：开始查询数据**

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

        AccountMapper mapper = MybatisFlexBootstrap.getInstance()
                .getMapper(AccountMapper.class);


        //示例1：查询 id=100 条数据
        Account account = mapper.selectOneById(100);
    }
}
```

> 以上的 `AccountMapper.class` 为 Mybatis-Flex 自动通过 APT 生成，无需手动编码。也可以关闭自动生成功能，手动编写 AccountMapper，更多查看 APT 文档。

示例2：查询列表

```java
//示例2：通过 QueryWrapper 构建条件查询数据列表
QueryWrapper query=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(ACCOUNT.USER_NAME.like("张").or(ACCOUNT.USER_NAME.like("李")));

// 执行 SQL：
// ELECT * FROM tb_account
// WHERE tb_account.id >=  100
// AND (tb_account.user_name LIKE '%张%' OR tb_account.user_name LIKE '%李%' )
List<Account> accounts = mapper.selectListByQuery(query);
```

示例3：分页查询

```java
// 示例3：分页查询
// 查询第 5 页，每页 10 条数据，通过 QueryWrapper 构建条件查询
QueryWrapper query=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(ACCOUNT.USER_NAME.like("张").or(ACCOUNT.USER_NAME.like("李")))
    .orderBy(ACCOUNT.ID.desc());

// 执行 SQL：
// ELECT * FROM tb_account
// WHERE id >=  100
// AND (user_name LIKE '%张%' OR user_name LIKE '%李%' )
// ORDER BY `id` DESC
// LIMIT 40,10
Page<Account> accounts = mapper.paginate(5,10,query);
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

简单示例：
```java
QueryWrapper query=new QueryWrapper();
query.select(ACCOUNT.ID,ACCOUNT.USER_NAME)
    .from(ACCOUNT)

// SQL: 
// SELECT id, user_name 
// FROM tb_account
```

多表查询（同时展现了功能强大的 `as` 能力）：
```java
QueryWrapper query = new QueryWrapper()
    .select(ACCOUNT.ID
        , ACCOUNT.USER_NAME
        , ARTICLE.ID.as("articleId")
        , ARTICLE.TITLE)
    .from(ACCOUNT.as("a"), ARTICLE.as("b"))
    .where(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID));

// SQL: 
// SELECT a.id, a.user_name, b.id AS articleId, b.title 
// FROM tb_account AS a, tb_article AS b 
// WHERE a.id = b.account_id
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
// SELECT id, user_name, 
// MAX(birthday), 
// AVG(sex) AS sex_avg 
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
// WHERE id >=  ?  
// AND user_name LIKE  ? 
```

### where 动态条件 1

```java
boolean flag = false;
QueryWrapper queryWrapper = QueryWrapper.create()
    .select().from(ACCOUNT)
    .where(flag ? ACCOUNT.ID.ge(100) : noCondition())
    .and(ACCOUNT.USER_NAME.like("michael"));

// SQL: 
// SELECT * FROM tb_account 
// WHERE user_name LIKE  ? 
```

### where 动态条件 2

```java
boolean flag = false;
QueryWrapper queryWrapper = QueryWrapper.create()
    .select().from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100).when(flag))
    .and(ACCOUNT.USER_NAME.like("michael"));

// SQL: 
// SELECT * FROM tb_account 
// WHERE user_name LIKE  ? 
```


### where select
```java
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(
       select(ARTICLE.ACCOUNT_ID).from(ARTICLE).where(ARTICLE.ID.ge(100))
    ));

// SQL: 
// SELECT * FROM tb_account
// WHERE id >= 
// (SELECT account_id FROM tb_article WHERE id >=  ? )
```

### exists, not exists

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(
        exist(  // or notExist(...)
            selectOne().from(ARTICLE).where(ARTICLE.ID.ge(100))
        )
    );

// SQL: 
// SELECT * FROM tb_account 
// WHERE id >=  ?  
// AND EXIST (
//    SELECT 1 FROM tb_article WHERE id >=  ? 
// )
```

### and (...) or (...)

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(ACCOUNT.SEX.eq(1).or(ACCOUNT.SEX.eq(2)))
    .or(ACCOUNT.AGE.in(18,19,20).and(ACCOUNT.USER_NAME.like("michael")));

// SQL: 
// SELECT * FROM tb_account 
// WHERE id >=  ?  
// AND (sex =  ? OR sex =  ? ) 
// OR (age IN (?,?,?) AND user_name LIKE ? )
```

### group by

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .groupBy(ACCOUNT.USER_NAME);

// SQL: 
// SELECT * FROM tb_account 
// GROUP BY user_name
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
// GROUP BY user_name 
// HAVING age BETWEEN  ? AND ?
```

### orderBy

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .orderBy(ACCOUNT.AGE.asc()
        , ACCOUNT.USER_NAME.desc().nullsLast());

// SQL: 
// SELECT * FROM tb_account
// ORDER BY age ASC, user_name DESC NULLS LAST
```

### join

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
    .innerJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
    .where(ACCOUNT.AGE.ge(10));

// SQL: 
// SELECT * FROM tb_account 
// LEFT JOIN tb_article ON tb_account.id = tb_article.account_id 
// INNER JOIN tb_article ON tb_account.id = tb_article.account_id 
// WHERE tb_account.age >=  ?
```


### limit... offset

```java
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .orderBy(ACCOUNT.ID.desc())
    .limit(10)
    .offset(20);

// MySql: 
// SELECT * FROM `tb_account` ORDER BY `id` DESC LIMIT 20, 10

// PostgreSQL: 
// SELECT * FROM "tb_account" ORDER BY "id" DESC LIMIT 20 OFFSET 10

// Informix: 
// SELECT SKIP 20 FIRST 10 * FROM "tb_account" ORDER BY "id" DESC

// Oracle: 
// SELECT * FROM (SELECT TEMP_DATAS.*, 
//  ROWNUM RN FROM (
//          SELECT * FROM "tb_account" ORDER BY "id" DESC) 
//      TEMP_DATAS WHERE  ROWNUM <=30) 
//  WHERE RN >20

// Db2: 
// SELECT * FROM "tb_account" ORDER BY "id" DESC 
// OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY

// Sybase: 
// SELECT TOP 10 START AT 21 * FROM "tb_account" ORDER BY "id" DESC

// Firebird: 
// SELECT * FROM "tb_account" ORDER BY "id" DESC ROWS 20 TO 30
```

> 在以上的 "limit... offset" 示例中，Mybatis-Flex 能够自动识别当前数据库，并生成不同的 SQL，用户也可以很轻易的通过
> `DialectFactory` 注册（新增或改写）自己的实现方言。


### 存在疑问？

**疑问 1：QueryWrapper 是否可以在分布式项目中通过 RPC 传输？**

答：可以。

**疑问 2：如何通过实体类 Account.java 生成 QueryWrapper 所需要的 "ACCOUNT" 类 ?**

答：Mybatis-Flex 使用了 APT（Annotation Processing Tool）技术，在项目编译的时候，会自动根据 Entity 类定义的字段帮你生成 "ACCOUNT" 类以及 Entity 对应的 Mapper 类，
通过开发工具构建项目（如下图），或者执行 maven 编译命令: `mvn clean package` 都可以自动生成。这个原理和 lombok 一致。

![](./docs/assets/images/build_idea.png)

> 更多关于 Mybatis-Flex APT 的配置，请点击 [这里](./docs/zh/others/02-apt.md)。

## Db + Row 工具类

Db + Row 工具类，提供了在 Entity 实体类之外的数据库操作能力。使用 Db + Row 时，无需对数据库表进行映射， Row 是一个 HashMap 的子类，相当于一个通用的 Entity。以下为 Db + Row
的一些示例：

```java
//使用原生 SQL 插入数据
String sql="insert into tb_account(id,name) value (?, ?)";
Db.insertBySql(sql,1,"michael");

//使用 Row 插入数据
Row account=new Row();
account.set("id",100);
account.set("name","Michael");
Db.insert("tb_account",account);


//根据主键查询数据
Row row=Db.selectOneById("tb_account","id",1);

//Row 可以直接转换为 Entity 实体类，且性能极高
Account account=row.toEntity(Account.class);


//查询所有大于 18 岁的用户
String listsql="select * from tb_account where age > ?"
List<Row> rows=Db.selectListBySql(sql,18);


//分页查询：每页 10 条数据，查询第 3 页的年龄大于 18 的用户
QueryWrapper query=QueryWrapper.create()
.where(ACCOUNT.AGE.ge(18));
Page<Row> rowPage=Db.paginate("tb_account",3,10,query);
```

> Db 工具类还提供了更多 增、删、改、查和分页查询等方法。
>
> 具体参考： [Db.java](./mybatis-flex-core/src/main/java/com/mybatisflex/core/row/Db.java) 。
>
> 更多关于 Row 插入时的**主键生成机制**、以及Db 的**事务管理**等，请点击 [这里](./docs/zh/core/04-db-row.md) 。

## Entity 部分字段更新

相比市面上的其他框架，这部分的功能应该也算是 MyBatis-Flex 的亮点之一。在 BaseMapper 中，Mybatis-Flex 提供了如下的方法：

```java
update(T entity)
```

有些场景下，我们可能希望只更新 几个 字段，而其中个别字段需要更新为 null。此时需要用到 `UpdateEntity` 工具类，以下是示例代码：

```java
Account account=UpdateEntity.of(Account.class);
account.setId(100);
account.setUserName(null);
account.setSex(1);

accountMapper.update(account);
```

以上的示例中，会把 id 为 100 这条数据中的 user_name 字段更新为 null，sex 字段更新为 1，其他字段不会被更新。也就是说，通过 `UpdateEntity`
创建的对象，只会更新调用了 setter 方法的字段，若不调用 setter 方法，不管这个对象里的属性的值是什么，都不会更新到数据库。

其生成的 sql 内容如下：

```sql
update tb_account
set user_name = ?, sex = ? where id = ? 
#params: null,1,100
```


## 自定义 TypeHandler

使用 @column 注解：

```java
@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    @Column(typeHandler = Fastjson2TypeHandler.class)
    private Map<String, Object> options;

    //getter setter
    
    public void addOption(String key, Object value) {
        if (options == null) {
            options = new HashMap<>();
        }
        options.put(key, value);
    }
    
}
```

插入数据：

```java
Account account = new Account();
account.setUserName("test");
account.addOption("c1", 11);
account.addOption("c2", "zhang");
account.addOption("c3", new Date());
```
mybatis 日志：
```
==>  Preparing: INSERT INTO tb_account (user_name, options) VALUES (?, ?)
==> Parameters: test(String), {"c3":"2023-03-17 09:10:16.546","c1":11,"c2":"zhang"}(String)
```

## 多主键

Mybatis-Flex 多主键就是在 Entity 类里有多个 `@Id` 注解标识而已，比如：

```java

@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Id(keyType = KeyType.Generator, value = "uuid")
    private String otherId;

    //getter setter
}
```

当我们保存数据的时候，Account 的 id 主键为自增，而 otherId 主键则通过 uuid 生成。

### 自定义主键生成器

第 1 步：编写一个类，实现 `IKeyGenerator` 接口，例如：

```java
public class UUIDKeyGenerator implements IKeyGenerator {

    @Override
    public Object generate(Object entity, String keyColumn) {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
```

第 2 步：注册 UUIDKeyGenerator

```java
KeyGeneratorFactory.register("myUUID",new UUIDKeyGenerator());
```

第 3 步：在 Entity 里使用 "myUUID" 生成器：

```java

@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Generator, value = "myUUID")
    private String otherId;

    //getter setter
}
```

### 使用数据库 Sequence 生成

```java

@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Sequence, value = "select SEQ_USER_ID.nextval as id from dual")
    private Long id;

}
```

> 更多关于主键的配置，请点击 [这里](./docs/zh/core/02-id.md)

## 更多文档

- [https://mybatis-flex.com](https://mybatis-flex.com)

## 还有问题？

加入 QQ 交流群： 532992631

![](./docs/assets/images/qq_group.png)

