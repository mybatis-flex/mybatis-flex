# 灵活的 QueryWrapper
在 [增删改](./add-delete-update) 和 [查询和分页](./query) 章节中，我们随时能看到 QueryWrapper 的身影，QueryWrapper 是用于构造 Sql 的
强有力工具，也是 Mybatis-Flex 的亮点和特色。

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
        
        //构造 QueryWrapper
        QueryWrapper query = new QueryWrapper();
        query.where(ACCOUNT.ID.ge(100));

        //通过 query 查询数据列表返回
        return accountMapper.selectListByQuery(query);
    }
}
```
在以上的示例中，其核心代码为：构造 QueryWrapper，通过 Mapper 查询，如下所示：

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
QueryWrapper query=new QueryWrapper();
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
        , ACCOUNT.USER_NAME
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
        exist(  // or notExist(...)
            selectOne().from(ARTICLE).where(ARTICLE.ID.ge(100))
        )
    );
```

其查询生成的 Sql 如下：

```sql
SELECT * FROM tb_account
WHERE id >=  ?
AND EXIST (
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

## limit... offset

::: tip 提示
在 "limit... offset" 的示例中，Mybatis-Flex 能够自动识别当前数据库，并根据数据库的类型生成不同的 SQL，用户也可以很轻易的通过 DialectFactory 注册（新增或改写）自己的实现方言。
:::


```java
QueryWrapper queryWrapper = QueryWrapper.create()
    .select()
    .from(ACCOUNT)
    .orderBy(ACCOUNT.ID.desc())
    .limit(10)
    .offset(20);
```

MySql 下执行的代码如下：
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


