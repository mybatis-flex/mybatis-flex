# 灵活的 QueryWrapper
在 [增删改](./add-delete-update) 和 [查询和分页](./query) 章节中，我们随时能看到 QueryWrapper 的身影，QueryWrapper 是用于构造 Sql 的
强有力工具，也是 MyBatis-Flex 的亮点和特色。

::: tip 提示
QueryWrapper 可以被序列化通过 RPC 进行传输，因此，在微服务项目中，我们可以在客户端（网关、Controller 层等）构造出 QueryWrapper，传给
Provider 层进行查询返回数据。
:::

## QueryWrapper 的使用

以下代码是一个完整 Spring Controller 的示例：

```java
@RestController
public class AccountController {

    @Autowired
    AccountMapper accountMapper;
    
    @GetMapping("/accounts")
    List<Account> selectList() {
        
        //构造 QueryWrapper，也支持使用 QueryWrapper.create() 构造，效果相同
        QueryWrapper query = new QueryWrapper();
        query.where(ACCOUNT.ID.ge(100));

        //通过 query 查询数据列表返回
        return accountMapper.selectListByQuery(query);
    }
}
```
在以上的示例中，其核心代码如下所示：

```java
//构造 QueryWrapper
QueryWrapper query = new QueryWrapper();
query.where(ACCOUNT.ID.ge(100));

//通过 query 查询数据列表
accountMapper.selectListByQuery(query);
```
以上代码执行的 Sql 如下：

```sql
select * from tb_account
where id >= 100
```


## select *

```java
QueryWrapper query = new QueryWrapper();
query.select(ACCOUNT.ID, ACCOUNT.USER_NAME)
    .from(ACCOUNT)
```

其查询生成的 Sql 如下：

```sql
SELECT id, user_name FROM tb_account
```

## select ... as

```java
QueryWrapper query = new QueryWrapper()
    .select(
          ACCOUNT.ID.as("accountId")
        , ACCOUNT.USER_NAME)
    .from(ACCOUNT.as("a"));
```

其查询生成的 Sql 如下：

```sql
SELECT a.id as accountId, a.user_name
FROM tb_account AS a
```

## select 多张表

```java
QueryWrapper query = new QueryWrapper()
    .select(
          ACCOUNT.ID
        , ACCOUNT.USER_NAME
        , ARTICLE.ID.as("articleId")
        , ARTICLE.TITLE)
    .from(ACCOUNT.as("a"), ARTICLE.as("b"))
    .where(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID));
```

其查询生成的 Sql 如下：

```sql
SELECT a.id, a.user_name, b.id AS articleId, b.title
FROM tb_account AS a, tb_article AS b
WHERE a.id = b.account_id
```

## select function

```java
QueryWrapper query=new QueryWrapper()
    .select(
        ACCOUNT.ID,
        ACCOUNT.USER_NAME,
        max(ACCOUNT.BIRTHDAY),
        avg(ACCOUNT.SEX).as("sex_avg")
    ).from(ACCOUNT);
```

其查询生成的 Sql 如下：

```sql
SELECT id, user_name, MAX(birthday), AVG(sex) AS sex_avg
FROM tb_account
```

## select case...when

**示例 1：**

```java
QueryWrapper wrapper = QueryWrapper.create()
        .select(
             ACCOUNT.ID
            ,case_()
                .when(ACCOUNT.ID.ge(2)).then("x2")
                .when(ACCOUNT.ID.ge(1)).then("x1")
                .else_("x100")
                .end().as("xName")
        )
```

其查询生成的 Sql 如下：

```sql
 SELECT `id`, 
        (CASE WHEN `id` >=  2  THEN 'x2' 
            WHEN `id` >=  1  THEN 'x1' 
            ELSE 'x100' 
            END) AS `xName` 
 FROM `tb_account`
```

SQL 执行的结果如下：

```
|id     |xName     |
|1      |x1        |
|2      |x2        |
```


**示例 2：**

```java
QueryWrapper queryWrapper = QueryWrapper.create()
        .select(
                ACCOUNT.ALL_COLUMNS,
                case_(ACCOUNT.ID)
                    .when(100).then(100)
                    .when(200).then(200)
                    .else_(300).end().as("result")
        )
        .from(ACCOUNT)
        .where(ACCOUNT.USER_NAME.like("michael"));
```

其查询生成的 Sql 如下：

```sql
SELECT *, 
       (CASE `id` 
           WHEN 100 THEN 100 
           WHEN 200 THEN 200 
           ELSE 300 END) AS `result` 
FROM `tb_account` 
WHERE `user_name` LIKE  ?
```

::: tip 提示
在以上示例中，由于 `case` 和 `else` 属于 Java 关键字，无法使用其进行方法命名，因此会添加一个下划线小尾巴 `"_"` 变成 `case_` 和 `else_`，这是无奈之举。
在以后的 QueryWrapper 构建中，遇到 java 关键字也会采用类型的解决方法。
:::

## where

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(ACCOUNT.USER_NAME.like("michael"));
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
WHERE id >=  ?
AND user_name LIKE  ? 
```

## where 动态条件 1

```java 1,4
boolean flag = false;
QueryWrapper queryWrapper = QueryWrapper.create()
    .select().from(ACCOUNT)
    .where(flag ? ACCOUNT.ID.ge(100) : noCondition())
    .and(ACCOUNT.USER_NAME.like("michael"));
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
WHERE user_name LIKE  ? 
```

## where 动态条件 2

```java 1,4
boolean flag = false;
QueryWrapper queryWrapper = QueryWrapper.create()
    .select().from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100).when(flag)) // when....
    .and(ACCOUNT.USER_NAME.like("michael"));
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
WHERE user_name LIKE  ? 
```

## where 动态条件 3

```java 1,5
String name = null;
QueryWrapper queryWrapper = QueryWrapper.create()
    .select().from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100)) // when....
    .and(ACCOUNT.USER_NAME.like(name).when(StringUtil::isNotBlank));
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
WHERE id >= ? 
```

## where select
```java
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(
        select(ARTICLE.ACCOUNT_ID).from(ARTICLE).where(ARTICLE.ID.ge(100))
    ));
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
WHERE id >=
(SELECT account_id FROM tb_article WHERE id >=  ? )
```

## where exists, not exists
```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(
        exists(  // or notExists(...)
            selectOne().from(ARTICLE).where(ARTICLE.ID.ge(100))
        )
    );
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
WHERE id >=  ?
AND EXISTS (
    SELECT 1 FROM tb_article WHERE id >=  ?
)
```

## and (...) or (...)

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .where(ACCOUNT.ID.ge(100))
    .and(ACCOUNT.SEX.eq(1).or(ACCOUNT.SEX.eq(2)))
    .or(ACCOUNT.AGE.in(18,19,20).and(ACCOUNT.USER_NAME.like("michael")));
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
WHERE id >=  ?
AND (sex =  ? OR sex =  ? )
OR (age IN (?,?,?) AND user_name LIKE ? )
```

## group by

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .groupBy(ACCOUNT.USER_NAME);
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
GROUP BY user_name
```

## having

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .groupBy(ACCOUNT.USER_NAME)
    .having(ACCOUNT.AGE.between(18,25));
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
GROUP BY user_name
HAVING age BETWEEN  ? AND ?
```

## orderBy

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .orderBy(ACCOUNT.AGE.asc(), ACCOUNT.USER_NAME.desc().nullsLast());
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
ORDER BY age ASC, user_name DESC NULLS LAST
```

## hint

Hint 是数据库厂商（比如 Oracle、MySQL、达梦等）提供的一种 SQL语法，它允许用户在 SQL 语句中插入相关的语法，从而影响 SQL 的执行方式。
它是一种【非常规】的直接影响优化器、指定执行计划的 SQL 优化手段。



```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select().hint("INDEX_DESC")
    .from(ACCOUNT)
    .orderBy(ACCOUNT.AGE.asc(), ACCOUNT.USER_NAME.desc().nullsLast());
```

其查询生成的 Sql 如下：

```sql
SELECT /*+ INDEX_DESC */  * FROM tb_account
ORDER BY age ASC, user_name DESC NULLS LAST
```


## join（left join，inner join...）

```java
QueryWrapper queryWrapper=QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
    .innerJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
    .where(ACCOUNT.AGE.ge(10));
```


其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
LEFT JOIN tb_article ON tb_account.id = tb_article.account_id
INNER JOIN tb_article ON tb_account.id = tb_article.account_id
WHERE tb_account.age >=  ?
```

## join on 多个条件

```java
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .leftJoin(ARTICLE).on(
        ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID).and(ACCOUNT.AGE.eq(18))
    )
    .where(ACCOUNT.AGE.ge(10));
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account LEFT JOIN tb_article 
ON tb_account.id = tb_article.account_id AND tb_account.age =  ?  
WHERE tb_account.age >=  ? 
```

## join select

```java
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .leftJoin(
        select().from(ARTICLE).where(ARTICLE.ID.ge(100))
    ).as("a").on(
        ACCOUNT.ID.eq(raw("a.id"))
    )
    .where(ACCOUNT.AGE.ge(10));

```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account 
LEFT JOIN (SELECT * FROM tb_article WHERE id >=  ? ) AS a
ON tb_account.id = a.id 
WHERE tb_account.age >=  ? 
```

## union, union all

```java
QueryWrapper query = new QueryWrapper()
    .select(ACCOUNT.ID)
    .from(ACCOUNT)
    .orderBy(ACCOUNT.ID.desc())
    .union(select(ARTICLE.ID).from(ARTICLE))
    .unionAll(select(ARTICLE.ID).from(ARTICLE));
```

其查询生成的 Sql 如下：

```sql
(SELECT id FROM tb_account ORDER BY id DESC) 
UNION (SELECT id FROM tb_article) 
UNION ALL (SELECT id FROM tb_article)
```


## limit... offset

::: tip 提示
在 "limit... offset" 的示例中，MyBatis-Flex 能够自动识别当前数据库👍，并根据数据库的类型生成不同的 SQL，用户也可以很轻易的通过 DialectFactory 注册（新增或改写）自己的实现方言。
:::


```java
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .orderBy(ACCOUNT.ID.desc())
    .limit(10)
    .offset(20);
```

MySQL 下执行的代码如下：
```sql
SELECT * FROM `tb_account` ORDER BY `id` DESC LIMIT 20, 10
```

PostgreSQL 下执行的代码如下：
```sql
SELECT * FROM "tb_account" ORDER BY "id" DESC LIMIT 20 OFFSET 10
```
Informix 下执行的代码如下：
```sql
SELECT SKIP 20 FIRST 10 * FROM "tb_account" ORDER BY "id" DESC
```

Oracle 下执行的代码如下：
```sql
SELECT * FROM (SELECT TEMP_DATAS.*,
    ROWNUM RN FROM (
        SELECT * FROM "tb_account" ORDER BY "id" DESC)
    TEMP_DATAS WHERE  ROWNUM <=30)
WHERE RN >20
```

Db2 下执行的代码如下：
```sql
SELECT * FROM "tb_account" ORDER BY "id" DESC
OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY
```

Sybase 下执行的代码如下：
```sql
SELECT TOP 10 START AT 21 * FROM "tb_account" ORDER BY "id" DESC
```

Firebird 下执行的代码如下：
```sql
SELECT * FROM "tb_account" ORDER BY "id" DESC ROWS 20 TO 30
```

## 存在疑问？

**疑问1：示例代码中的 QueryWrapper 所需要的 "ACCOUNT" 从哪里来的？**

答：MyBatis-Flex 使用了 APT（Annotation Processing Tool）在项目编译的时候，
会自动根据 Entity 类定义的字段生成 "ACCOUNT" 类以及 Entity 对应的 Mapper 类， 通过开发工具构建项目（如下图），
或者执行 maven 编译命令: `mvn clean package` 都可以自动生成。这个原理和 lombok 一致。

![](../../assets/images/build_idea.png)

> 更多关于 APT 的配置，请进入 [APT 配置章节](../others/apt.md) 了解。

## 特别注意事项!!!
在 QueryWrapper 的条件构建中，如果传入 null 值，则自动忽略该条件，这有许多的好处，不需要额外的通过 `when()` 方法判断。但是也带来一些额外的知识记忆点，
因此，正对这一点需要特别注意一下。

例如：

```java
String userName = null;
Integer id = null;
QueryWrapper query1 = QueryWrapper.create()
    .where(ACCOUNT.AGE.ge(18))
    .and(ACCOUNT.USER_NAME.like(userName))
    .and(ACCOUNT.ID.ge(id));

QueryWrapper query2 = QueryWrapper.create()
    .where(ACCOUNT.AGE.ge(18));
```
在以上的 `query1` 和 `query2` 中，它们构建出来的 SQL 条件是完全一致的，因为 MyBatis-Flex 会自动忽略 null 值的条件。