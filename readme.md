<h4 align="right"><strong>English</strong> | <a href="./readme_zh.md">简体中文</a></h4>

<p align="center">
    <img src="./docs/assets/images/logo_en.png"/>
</p>

# MyBatis-Flex is an elegant Mybatis Enhancement Framework.

<p align="center">
    <a target="_blank" href="https://search.maven.org/search?q=mybatis-flex%20mybatis-flex">
        <img src="https://img.shields.io/maven-central/v/com.mybatis-flex/parent?label=Maven%20Central" alt="Maven" />
    </a>
    <a target="_blank" href="https://www.apache.org/licenses/LICENSE-2.0.txt">
		<img src="https://img.shields.io/:license-Apache2-blue.svg" alt="Apache 2" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8-green.svg" alt="jdk-8" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-11-green.svg" alt="jdk-11" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-17-green.svg" alt="jdk-17" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-21-green.svg" alt="jdk-21" />
	</a>
    <a target="_blank" href="https://www.oracle.com/java/technologies/javase/jdk25-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-25-green.svg" alt="jdk-25" />
	</a>
    <br />
        <img src="https://img.shields.io/badge/SpringBoot-v2.x-blue">
        <img src="https://img.shields.io/badge/SpringBoot-v3.x-blue">
        <img src="https://img.shields.io/badge/SpringBoot-v4.x-blue">
        <a target="_blank" href='https://github.com/noear/solon'><img src="https://img.shields.io/badge/Solon-v2.x-blue"></a>
    <br />
    <a target="_blank" href='https://gitee.com/mybatis-flex/mybatis-flex'>
		<img src='https://gitee.com/mybatis-flex/mybatis-flex/badge/star.svg' alt='Gitee star'/>
	</a>
    <a target="_blank" href='https://github.com/mybatis-flex/mybatis-flex'>
		<img src="https://img.shields.io/github/stars/mybatis-flex/mybatis-flex.svg?logo=github" alt="Github star"/>
	</a>
</p>

## Features

1. MyBatis-Flex is very lightweight, and it only depends on Mybatis and no other third-party dependencies
2. Basic CRUD operator and paging query of Entity class
3. Row mapping support, you can add, delete, modify and query the database without entity classes
4. Support multiple databases, and expand through dialects flexibly
5. Support combined primary keys and different primary key content generation strategies
6. Extremely friendly SQL query, IDE automatically prompts and no worries about mistakes
7. More little surprises

## hello world(Without Spring)

**step 1: write entity class**

```java
@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;
    private Date birthday;
    private int sex;

    // getter setter
}
```

**step 2: write mapper class(it needs extends BaseMapper)**

```java
public interface AccountMapper extends BaseMapper<Account> {
    // only Mapper interface define
}
```

**step 3: start query data**

e.g. 1: query by primary key

```java
class HelloWorld {
    public static void main(String... args) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mybatis-flex");
        dataSource.setUsername("username");
        dataSource.setPassword("password");

        MybatisFlexBootstrap.getInstance()
                .setDataSource(dataSource)
                .addMapper(AccountMapper.class)
                .start();

        AccountMapper mapper = MybatisFlexBootstrap.getInstance()
                .getMapper(AccountMapper.class);


        // id = 100
        Account account = mapper.selectOneById(100);
    }
}
```

e.g.2: query list

```java
// use QueryWrapper to build query conditions
QueryWrapper query = QueryWrapper.create()
        .select()
        .from(ACCOUNT)
        .where(ACCOUNT.ID.ge(100))
        .and(ACCOUNT.USER_NAME.like("zhang").or(ACCOUNT.USER_NAME.like("li")));

// execute SQL：
// SELECT * FROM tb_account
// WHERE tb_account.id >=  100
// AND (tb_account.user_name LIKE '%zhang%' OR tb_account.user_name LIKE '%li%' )
List<Account> accounts = mapper.selectListByQuery(query);
```

e.g.3: paging query

```java
// use QueryWrapper to build query conditions
QueryWrapper query = QueryWrapper.create()
        .select()
        .from(ACCOUNT)
        .where(ACCOUNT.ID.ge(100))
        .and(ACCOUNT.USER_NAME.like("zhang").or(ACCOUNT.USER_NAME.like("li")))
        .orderBy(ACCOUNT.ID.desc());

// execute SQL：
// SELECT * FROM tb_account
// WHERE tb_account.id >=  100
// AND (tb_account.user_name LIKE '%zhang%' OR tb_account.user_name LIKE '%li%' )
// ORDER BY tb_account.id DESC
// LIMIT 40,10
Page<Account> accountPage = mapper.paginate(5, 10, query);
```

## QueryWrapper Samples

### select *

```java
QueryWrapper query = new QueryWrapper();
query.select().from(ACCOUNT)

// SQL:
// SELECT * FROM tb_account
```

### select columns

```java
QueryWrapper query = new QueryWrapper();
query.select(ACCOUNT.ID,ACCOUNT.USER_NAME).from(ACCOUNT)

// SQL:
// SELECT tb_account.id, tb_account.user_name
// FROM tb_account
```


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
 QueryWrapper query = new QueryWrapper()
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
QueryWrapper queryWrapper = QueryWrapper.create()
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
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(
        exists(
            selectOne().from(ARTICLE).where(ARTICLE.ID.ge(100))
        )
    );

// SQL:
// SELECT * FROM tb_account
// WHERE tb_account.id >=  ?
// AND EXIST (
//  SELECT 1 FROM tb_article WHERE tb_article.id >=  ?
// )
```

### and (...) or (...)

```java
QueryWrapper queryWrapper = QueryWrapper.create()
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
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .groupBy(ACCOUNT.USER_NAME);

// SQL:
// SELECT * FROM tb_account
// GROUP BY tb_account.user_name
```

### having

```java
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .groupBy(ACCOUNT.USER_NAME)
    .having(ACCOUNT.AGE.between(18,25));

// SQL:
// SELECT * FROM tb_account
// GROUP BY tb_account.user_name
// HAVING tb_account.age BETWEEN  ? AND ?
```


### orderBy

```java
QueryWrapper queryWrapper = QueryWrapper.create()
        .select()
        .from(ACCOUNT)
        .orderBy(ACCOUNT.AGE.asc(), ACCOUNT.USER_NAME.desc().nullsLast());

// SQL:
// SELECT * FROM tb_account
// ORDER BY age ASC, user_name DESC NULLS LAST
```


### join
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


### Questions？

**1. How to generate "ACCOUNT" class for QueryWrapper by Account.java ?**

Build the project by IDE, or execute maven build command: `mvn clean package`

![](./docs/assets/images/build_idea.png)

## More Samples

1. [Mybatis-Flex Only (Native)](./mybatis-flex-test/mybatis-flex-native-test)
2. [Mybatis-Flex with Spring](./mybatis-flex-test/mybatis-flex-spring-test)
3. [Mybatis-Flex with Spring boot](./mybatis-flex-test/mybatis-flex-spring-boot-test)

## Wechat Group

![](./docs/assets/images/wechat-group.jpg)


