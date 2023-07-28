# MyBatis-Flex 和同类框架「功能」对比

MyBatis-Flex 主要是和 `MyBatis-Plus` 与 `Fluent-MyBatis` 对比，内容来源其官网、git 或者 网络文章，若有错误欢迎纠正。

- MyBatis-Plus：老牌的 MyBatis 增强框架，开源于 2016 年。
- Fluent-MyBatis：阿里云开发的 MyBatis 增强框架（来自于阿里云·云效产品团队）

> 本文只阐述了「功能」方面的对比，**「性能」** 对比请参考 [这里](./benchmark.md)。
> 若发现对比中有错误，请加入 MyBatis-Flex QQ 交流群：532992631，然后联系群主纠正。

## 功能对比


| 功能或特点 | MyBatis-Flex     | MyBatis-Plus    | Fluent-MyBatis     |
| -------- | -------- | -------- | -------- |
| 对 entity 的基本增删改查 | ✅ | ✅ | ✅ |
| 分页查询 | ✅ | ✅ | ✅ |
| 分页查询之总量缓存 | ✅ | ✅ | ❌ |
| 分页查询无 SQL 解析设计（更轻量，及更高性能） | ✅ | ❌ | ✅ |
| 多表查询： from 多张表 | ✅ | ❌ | ❌ |
| 多表查询： left join、inner join 等等 | ✅ | ❌ | ✅ |
| 多表查询： union，union all | ✅ | ❌ | ✅ |
| 单主键配置 | ✅ | ✅ | ✅ |
| 多种 id 生成策略 | ✅ | ✅ | ✅ |
| 支持多主键、复合主键 | ✅ | ❌ | ❌ |
| 字段的 typeHandler 配置 | ✅ | ✅ | ✅ |
| 除了 MyBatis，无其他第三方依赖（更轻量） | ✅ | ❌ | ❌ |
| QueryWrapper 是否支持在微服务项目下进行 RPC 传输 | ✅ | ❌ | 未知 |
| 逻辑删除 | ✅ | ✅ | ✅ |
| 乐观锁 | ✅ | ✅ | ✅ |
| SQL 审计 | ✅ | ❌ | ❌ |
| 数据填充 | ✅ |  ✅ | ✅ |
| 数据脱敏 | ✅ |  ✔️ **（收费）** | ❌ |
| 字段权限 | ✅ |  ✔️ **（收费）** | ❌ |
| 字段加密 | ✅ |  ✔️ **（收费）** | ❌ |
| 字典回写 | ✅ |  ✔️ **（收费）** | ❌ |
| Db + Row | ✅ | ❌ | ❌ |
| Entity 监听 | ✅ | ❌ | ❌ |
| 多数据源支持 | ✅ | 借助其他框架或收费 | ❌ |
| 多数据源是否支持 Spring 的事务管理，比如 `@Transactional` 和 `TransactionTemplate` 等 | ✅ | ❌ | ❌ |
| 多数据源是否支持 "非Spring" 项目 | ✅ | ❌ | ❌ |
| 多租户 | ✅ | ✅ | ❌ |
| 动态表名 | ✅ | ✅ | ❌ |
| 动态 Schema  | ✅ | ❌ | ❌ |

> 以上内容来自第三方相关产品的官方文档或第三方平台，若有错误，欢迎纠正。



## 基础查询

**MyBatis-Flex：**

````java
QueryWrapper query = QueryWrapper.create()
        .where(EMPLOYEE.LAST_NAME.like(searchWord)) //条件为null时自动忽略
        .and(EMPLOYEE.GENDER.eq(1))
        .and(EMPLOYEE.AGE.gt(24));
List<Employee> employees = employeeMapper.selectListByQuery(query);
````

**MyBatis-Plus：**

````java
QueryWrapper<Employee> queryWrapper = Wrappers.query()
        .like(searchWord != null, "last_name", searchWord)
        .eq("gender", 1)
        .gt("age", 24);
List<Employee> employees = employeeMapper.selectList(queryWrapper);
````
或者 MyBatis-Plus 的 lambda 写法：

```java
LambdaQueryWrapper<Employee> queryWrapper = Wrappers.<Employee>lambdaQuery()
        .like(StringUtils.isNotEmpty(searchWord), Employee::getUserName,"B")
        .eq(Employee::getGender, 1)
        .gt(Employee::getAge, 24);
List<Employee> employees = employeeMapper.selectList(queryWrapper);
```


**Fluent-MyBatis：**

````java
EmployeeQuery query = new EmployeeQuery()
    .where.lastName().like(searchWord, If::notNull)
    .and.gender().eq(1)
    .and.age().gt(24)
    .end();
List<Employee> employees = employeeMapper.listEntity(query);
````



## 查询集合函数

**MyBatis-Flex：**

````java
QueryWrapper query = QueryWrapper.create()
    .select(
        ACCOUNT.ID,
        ACCOUNT.USER_NAME,
        max(ACCOUNT.BIRTHDAY),
        avg(ACCOUNT.SEX).as("sex_avg")
    );
List<Employee> employees = employeeMapper.selectListByQuery(query);
````
**MyBatis-Plus：**

````java
QueryWrapper<Employee> queryWrapper = Wrappers.query()
    .select(
        "id",
        "user_name",
        "max(birthday)",
        "avg(birthday) as sex_avg"
    );
List<Employee> employees = employeeMapper.selectList(queryWrapper);
````
> 缺点：字段硬编码，容易拼错。无法使用 ide 的字段进行重构，无法使用 IDE 自动提示，发生错误不能及时发现。


**Fluent-MyBatis：**

````java
EmployeeQuery query = new EmployeeQuery()
        .select
        .id()
        .userName()
        .max.birthday()
        .avg.sex("sex_avg")
        .end()
List<Employee> employees = employeeMapper.listEntity(query);
````
>缺点：编写内容不符合 sql 直觉。


## and(...) 和 or(...)

假设我们要构建如下的 SQL 进行查询（需要在 SQL 中添加括号）。

```sql
SELECT * FROM tb_account
WHERE id >= 100
AND (sex = 1 OR sex = 2)
OR (age IN (18,19,20) AND user_name LIKE "%michael%" )
```

**MyBatis-Flex：**

```java
QueryWrapper query = QueryWrapper.create()
    .where(ACCOUNT.ID.ge(100))
    .and(ACCOUNT.SEX.eq(1).or(ACCOUNT.SEX.eq(2)))
    .or(ACCOUNT.AGE.in(18, 19, 20).and(ACCOUNT.USER_NAME.like("michael")));
```

**MyBatis-Plus：**

```java
QueryWrapper<Employee> query = Wrappers.query()
        .ge("id", 100)
        .and(i -> i.eq("sex", 1).or(x -> x.eq("sex", 2)))
        .or(i -> i.in("age", 18, 19, 20).like("user_name", "michael"));
// or lambda
LambdaQueryWrapper<Employee> query = Wrappers.<Employee>lambdaQuery()
        .ge(Employee::getId, 100)
        .and(i -> i.eq(Employee::getSex, 1).or(x -> x.eq(Employee::getSex, 2)))
        .or(i -> i.in(Employee::getAge, 18, 19, 20).like(Employee::getUserName, "michael"));
```

**Fluent-MyBatis：**

```java
AccountQuery query = new AccountQuery()
    .where.id().ge(100)
    .and(
            new AccountQuery().where.sex().eq(1).or(
                new AccountQuery().where.sex().eq(2).end()
    ).end())
    .or(
        new AccountQuery().where.age.in(18,19,20)
            .and.userName().like("michael").end()
    )
    .end();
```
> 缺点：许多 `.end()` 方法调用，容易忘记出错（或者写错了？欢迎纠正）。


## 多表查询 1
**MyBatis-Flex：**
````java
QueryWrapper query = QueryWrapper.create()
    .select().from(ACCOUNT)
    .leftJoin(ARTICLE).on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID))
    .where(ACCOUNT.AGE.ge(10));

List<Account> accounts = mapper.selectListByQuery(query);
````
**MyBatis-Plus：**

````java
// 不支持~~~~
````

**Fluent-MyBatis：**

````java
StudentQuery leftQuery = new StudentQuery("a1").selectAll()
    .where.age().eq(34)
    .end();
HomeAddressQuery rightQuery = new HomeAddressQuery("a2")
    .where.address().like("address")
    .end();

IQuery query = leftQuery
    .join(rightQuery)
    .on(l -> l.where.homeAddressId(), r -> r.where.id()).endJoin()
    .build();

List<StudentEntity> entities = this.mapper.listEntity(query);
````
>缺点：编写内容不符合 sql 直觉。同时在编写 `end()` 和 `endJoin()` 容易忘记。


## 多表查询 2
假设查询的 SQL 如下：

```sql
SELECT a.id, a.user_name, b.id AS articleId, b.title
FROM tb_account AS a, tb_article AS b
WHERE a.id = b.account_id
```

**MyBatis-Flex：**
````java
QueryWrapper query = new QueryWrapper()
.select(
      ACCOUNT.ID
    , ACCOUNT.USER_NAME
    , ARTICLE.ID.as("articleId")
    , ARTICLE.TITLE)
.from(ACCOUNT.as("a"), ARTICLE.as("b"))
.where(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID));
````

**MyBatis-Plus：**

````java
// 不支持~~~~
````

**Fluent-MyBatis：**

````java
// 不支持~~~~
````
>PS：也有可能是笔者自己不知道如何支持，而非 Fluent-MyBatis 原因，有知道的同学可以给下示例代码。


## 部分字段更新
假设一个实体类 Account 中，我们要更新其内容如下：

- `userName` 为 "michael"
- `age` 为 "18"
- `birthday` 为 null

其他字段保持数据库原有内容不变，要求执行的 SQL 如下：

```sql
update tb_account
set user_name = "michael", age = 18, birthday = null
where id = 100
```

**MyBatis-Flex** 代码如下：

```java
Account account = UpdateEntity.of(Account.class);
account.setId(100); //设置主键
account.setUserName("michael");
account.setAge(18);
account.setBirthday(null);

accountMapper.update(account);
```

**MyBatis-Plus** 代码如下（或可使用 MyBatis-Plus 的 `LambdaUpdateWrapper`，但性能没有 `UpdateWrapper` 好）：

```java
UpdateWrapper<Account> updateWrapper = new UpdateWrapper<>();
updateWrapper.eq("id", 100);
updateWrapper.set("user_name", "michael");
updateWrapper.set("age", 18);
updateWrapper.set("birthday", null);

accountMapper.update(null, updateWrapper);
```

**Fluent-MyBatis** 代码如下：

```java
AccountUpdate update = new AccountUpdate()
.update.userName().is("michael")
.age().is(18)
.birthday().is(null)
.end()
.where.id().eq(100)
.end();
accountMapper.updateBy(update);
```
