# Mybatis-Flex 和同类框架对比

## 功能对比

MyBatis-Flex 主要是和 `MyBatis-Plus` 与 `Fluent-Mybatis` 对比，内容来源其官网、git 或者 网络文章，若有错误欢迎指正。

- MyBatis-Plus：老版的 MyBatis 增强框架
- Fluent-Mybatis：阿里开发的 Mybatis 增强框架（是阿里开发的吗？）

| 功能或特点 | MyBatis-Flex     | MyBatis-Plus    | Fluent-Mybatis     |
| -------- | -------- | -------- | -------- |
| 对 entity 的基本增删改查 | ✅ | ✅ | ✅ |
| 分页查询 | ✅ | ✅ | ✅ |
| 分页查询之总量缓存 | ✅ | ❌ | ❌ |
| 分页查询无SQL解析设计（更轻量） | ✅ | ❌ | ✅ |
| 多表查询： from 多张表 | ✅ | ❌ | ❌ |
| 多表查询： left join、inner join 等等 | ✅ | ❌ | ✅ |
| 单主键配置 | ✅ | ✅ | ✅ |
| 多种 id 生成策略 | ✅ | ✅ | ✅ |
| 支持多主键、复合主键 | ✅ | ❌ | ❌ |
| 字段的 typeHandler 配置 | ✅ | ✅ | ✅ |
| 除了 Mybatis，无其他第三方依赖（更轻量） | ✅ | ❌ | ❌ |
| 逻辑删除 | ✅ | ✅ | ✅ |
| 乐观锁 | ✅ | ✅ | ✅ |
| SQL 审计 | ✅ | ❌ | ❌ |
| 数据填充 | ✅ | ✅（收费） | ✅ |
| 数据脱敏 | ✅ | ✅（收费） | ❌ |
| 字段权限 | ✅ | ✅（收费） | ❌ |
| 字段加密 | ✅ | ✅（收费） | ❌ |
| 字典回显 | ✅ | ✅（收费） | ❌ |
| Db + Row | ✅ | ❌ | ❌ |
| Entity 监听 | ✅ | ❌ | ❌ |
| 多数据源支持 | ✅ | ✅ | ❌ |

> 以上内容来自第三方相关产品的官方文档或第三方平台，若有错误，欢迎指正。



## 基础查询

**MyBatis-Flex：**

````java
QueryCondition condition = EMPLOYEE.LAST_NAME.like("B")
        .and(EMPLOYEE.GENDER.eq(1))
        .and(EMPLOYEE.AGE.gt(24));
List<Employee> employees = employeeMapper.selectListByCondition(condition);
````
**MyBatis-Plus：**

````java
QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
queryWrapper
        .like("last_name","B")
        .eq("gender",1)
        .gt("age",24);
List<Employee> employees = employeeMapper.selectList(queryWrapper);
````


**Fluent-MyBatis：**

````java
EmployeeQuery query = new EmployeeQuery()
    .where.lastName().like("B")
    .and.gender().eq(1)
    .and.age().gt(24)
    .end();
List<Employee> employees = employeeMapper.listEntity(query);
````

> 总结：MyBatis-Flex 和 Fluent-MyBatis 的字段有 IDE 自动提示，不担心写错，同时在后续版本升级和重构时，更好的利用 IDE 的重构功能,
> 字段错误在项目编译期间就能发现及时纠正。
> 不过在设计上，Fluent-MyBatis 使用 "方法" 来代替字段，同时还设计有多余的 `end()` 方法，明显不太符合 sql 直觉。

## 查询集合函数

**MyBatis-Flex：**

````java
QueryWrapper query = new QueryWrapper()
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
QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
queryWrapper
    .select(
        "id"
        ,"user_name"
        ,"max(birthday)"
        ,"avg(birthday) as sex_avg"
    );
List<Employee> employees = employeeMapper.selectList(queryWrapper);
````
> 缺点：字段硬编码，容易错处。无法使用 ide 的字段进行重构，无法使用 IDE 自动提示，发生错误不能及时发现。


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
    .or(ACCOUNT.AGE.in(18,19,20).and(ACCOUNT.USER_NAME.like("michael")));
```

**MyBatis-Plus：**

```java
QueryWrapper<Employee> query = new QueryWrapper<>();
queryWrapper.ge("id",100)
        .or(i->i.eq("sex",1).or(x->x.eq("sex",2)))
        .and(i->i.in("age",{18,19,20}).like("user_name","michael"));
```

**Fluent-Mybatis：**

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
> 缺点：一堆的 `.end()` 方法调用，容易出错（或者是我写错了？欢迎指正）。


## 多表查询
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
