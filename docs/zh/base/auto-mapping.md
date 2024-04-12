# 自动映射

在 MyBatis-Flex 中，内置了非常智能的 **自动映射** 功能，能够使得我们在查询数据的时候，从数据结果集绑定到实体类（或者 VO、DTO
等）变得极其简单易用。

## 数据假设

假设在我们的项目中，有如下的表结构和实体类：

账户表（tb_account）：
```sql
CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`        INTEGER auto_increment,
    `user_name` VARCHAR(100),
    `age`       Integer
);
```

图书表（tb_book）：

```sql
CREATE TABLE IF NOT EXISTS `tb_book`
(
    `id`        INTEGER auto_increment,
    `account_id` Integer,
    `title`      VARCHAR(100),
    `content`    text
);
```

> 图书和账户的关系是多对一的关系：一个账户可以拥有多本书。

角色表（tb_role）：

```sql
CREATE TABLE IF NOT EXISTS `tb_role`
(
    `id`        INTEGER auto_increment,
    `name`      VARCHAR(100)
);
```

账户和角色的 **多对多** 关系映射表（tb_role_mapping）：

```sql
CREATE TABLE IF NOT EXISTS `tb_role_mapping`
(
    `account_id`        INTEGER ,
    `role_id`      INTEGER
);
```

## 基础映射

基础映射指的是，定义的实体类和表结构是一一对应的关系，例如：

```java
@Table(value = "tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;
    private int age;

    //getter setter
}
```
`Account.java `与表 `tb_account` 是字段和属性是一一对应关系的。此时，我们在查询数据的时候，可以通过
`AccountMapper` 方法直接查询，例如：

```java
QueryWrapper qw = new QueryWrapper();
qw.select(ACCOUNT.ALL_COLUMNS)
    .where(ACCOUNT.ID.ge(100));

List<Account> accounts = accountMapper.selectListByQuery(qw);
```

或者使用如下的链式查询，都可以直接得到 `List<Account>` 结果：`accounts`。

```java
QueryChain.of(accountMapper)
    .select(ACCOUNT.ALL_COLUMNS)
    .where(ACCOUNT.ID.ge(100))
    .list();
```

## AS 映射


假设我们在 `Account.java` 中多定义了一些其他属性，如下所示：

```java
@Table(value = "tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;
    private int age;

    //最大年龄
    private int maxAge;

    //平均年龄
    private int avgAge;

    //getter setter
}
```
那么，我们在查询的时候，就可以通过 `as` 进行映射关联，查询代码如下：

```java 4,5
QueryChain.of(accountMapper)
    .select(
        ACCOUNT.ALL_COLUMNS,
        max(ACCOUNT.AGE).as("maxAge"),
        avg(ACCOUNT.AGE).as("avgAge")
    ).where(ACCOUNT.ID.ge(100))
    .groupBy(ACCOUNT.AGE)
    .list();
```
或者：

```java 4,5
QueryChain.of(accountMapper)
    .select(
        ACCOUNT.ALL_COLUMNS,
        max(ACCOUNT.AGE).as("max_age"),
        avg(ACCOUNT.AGE).as("avg_age")
    ).where(ACCOUNT.ID.ge(100))
    .groupBy(ACCOUNT.AGE)
    .list();
```

或者使用 lambda：

```java 4,5
QueryChain.of(accountMapper)
    .select(
        ACCOUNT.ALL_COLUMNS,
        max(ACCOUNT.AGE).as(Account::getMaxAge),
        avg(ACCOUNT.AGE).as(Account::getAvgAge)
    ).where(ACCOUNT.ID.ge(100))
    .groupBy(Account::getAge)
    .list();
```

以上代码执行的 SQL 如下：

```sql
select tb_account.*
     , max(tb_account.age) as maxAge
     , avg(tb_account.age) as avgAge
where tb_account.id >= 100
group by tb_account.age
```


## 多表映射

假设我们定义了一个 `BootVo.java`，其中包含了图书的基本信息，也包含了图书归属的用户信息，例如：

```java
public class BookVo {

    //图书的基本字段
    private Long id;
    private Long accountId;
    private String title;
    private String content;

    //用户表的字段
    private String userName;
    private int userAge;
}
```
此时，我们再进行 `left join` 多表查询时，代码如下：

```java
List<BookVo> bookVos = QueryChain.of(bookMapper)
    .select(
        BOOK.ALL_COLUMNS, //图书的所有字段
        ACCOUNT.USER_NAME, //用户表的 user_name 字段
        ACCOUNT.AGE.as("userAge") //用户表的 age 字段， as "userAge"
    ).from(BOOK)
    .leftJoin(ACCOUNT).on(BOOK.ACCOUNT_ID.eq(ACCOUNT.ID))
    .where(ACCOUNT.ID.ge(100))
    .listAs(BookVo.class);
```

或者，我们也可以直接在 BookVo 中，定义 `Account` 对象，例如：

```java
public class BookVo {

    //图书的基本字段
    private Long id;
    private Long accountId;
    private String title;
    private String content;

    //用户
    private Account account;
}
```

查询代码如下：

```java
List<BookVo> bookVos = QueryChain.of(bookMapper)
    .select(
        BOOK.DEFAULT_COLUMNS,
        ACCOUNT.DEFAULT_COLUMNS,
     )
    .from(BOOK)
    .leftJoin(ACCOUNT).on(BOOK.ACCOUNT_ID.eq(ACCOUNT.ID))
    .where(ACCOUNT.ID.ge(100))
    .listAs(BookVo.class);
```

## 高级映射

在以上的表结构中，一个账户可以有多本图书，那么我们假设定义的 `AccountVo.java` 的结构如下：

```java
public class AccountVO {

    private Long id;
    private String userName;
    private int age;

    //账户拥有的 图书列表
    private List<Book> books;
}
```

```java
List<AccountVO> bookVos = QueryChain.of(accountMapper)
    .select() // 不传入参数等同于 SQL 的 select *
    .from(ACCOUNT)
    .leftJoin(BOOK).on(ACCOUNT.ID.eq(BOOK.ACCOUNT_ID))
    .where(ACCOUNT.ID.ge(100))
    .listAs(AccountVO.class);
```

亦或者指定查询参数：

```java
List<AccountVO> bookVos = QueryChain.of(accountMapper)
    .select(
        ACCOUNT.ID,
        ACCOUNT.USER_NAME,
        ACCOUNT.AGE,
        BOOK.TITLE,
        BOOK.CONTENT,
     )
    .from(ACCOUNT)
    .leftJoin(BOOK).on(ACCOUNT.ID.eq(BOOK.ACCOUNT_ID))
    .where(ACCOUNT.ID.ge(100))
    .listAs(AccountVO.class);
```

高级映射的场景中，我们还可以通过注解 `@RelationManyToOne` 进行查询，
详情请点击 [这里](./relations-query#%E4%B8%80%E5%AF%B9%E5%A4%9A-relationonetomany)。


## 重名映射

在很多类型嵌套的场景下，可能会出现字段名定义重复的情况，例如：

```java
@TableRef(Account.class)
public class AccountVO {

    private Long id;
    private String name;
    private int age;

    //账户拥有的 图书列表
    private List<Book> book;
}
```

```java
public class Book {
    private Long id;
    private Long accountId;
    private String name;
}
```

在以上的嵌套定义中， `AccountVO` 以及 `Book` 都包含了 `id` 和 `name` 的定义，假设我们查询的方法如下：

```java
List<AccountVO> bookVos = QueryChain.of(accountMapper)
    .select(
        ACCOUNT.ID,
        ACCOUNT.NAME,
        ACCOUNT.AGE,
        BOOK.ID,
        BOOK.NAME,
     )
    .from(ACCOUNT)
    .leftJoin(BOOK).on(ACCOUNT.ID.eq(BOOK.ACCOUNT_ID))
    .where(ACCOUNT.ID.ge(100))
    .listAs(AccountVO.class);
```

其执行的 SQL 如下：

```sql
select tb_account.id   as tb_account$id,
       tb_account.name as tb_account$name,
       tb_account.age,
       tb_book.id      as tb_book$id,  -- Flex 发现有重名时，会自动添加上 as 别名
       tb_book.name    as tb_book$name -- Flex 发现有重名时，会自动添加上 as 别名
from tb_account
         left join tb_book on tb_account.id = tb_book.account_id
where tb_account.id >= 100
```

此时，查询的数据可以正常映射到 `AccountVO` 类。

::: tip 注意事项
- 在查询 VO 类当中有重名字段时，需要给 VO 类标记 `@TableRef` 注解，指定其对应的实体类，以正确添加别名。
- 在 QueryWrapper 的 `select(...)` 中，MyBatis-Flex 在 **多表查询** 的情况下，且有相同的字段名时，MyBatis-Flex 内部会主动帮助用户添加上 as 别名，默认为：`表名$字段名`。

**错误的情况：**

若我们修改查询代码如下：

```sql
List<AccountVO> bookVos = QueryChain.of(accountMapper)
    .select()
    .from(ACCOUNT)
    .leftJoin(BOOK).on(ACCOUNT.ID.eq(BOOK.ACCOUNT_ID))
    .where(ACCOUNT.ID.ge(100))
    .listAs(AccountVO.class);
```

那么，其执行的 SQL 如下：

```sql
select * from tb_account
left join tb_book on tb_account.id = tb_book.account_id
where tb_account.id >= 100
```
此时，查询的结果集中，会有多个 `id` 和 `name` 列，程序无法知道 `id` 和 `name` 对应的应该是 `AccountVO` 的还是 `Book`
的，因此，可能会出现数据错误赋值的情况。

所以，若程序中出现包裹对象有重名属性的情况时，`QueryWrapper` 的 `select(...)` 方法必须传入具体的字段才能保证数据正常赋值。

如下的代码也是没问题的：

```java
List<AccountVO> bookVos = QueryChain.of(accountMapper)
    .select(
        ACCOUNT.DEFAULT_COLUMNS,
        BOOK.DEFAULT_COLUMNS
     )
    .from(ACCOUNT)
    .leftJoin(BOOK).on(ACCOUNT.ID.eq(BOOK.ACCOUNT_ID))
    .where(ACCOUNT.ID.ge(100))
    .listAs(AccountVO.class);
```

**`@ColumnAlias` 注解：**

`@ColumnAlias` 注解的作用是用于定义在 entity 查询时，默认的 SQL 别名名称，可以取代自动生成的别名，例如：

```java
public class Book {

    @ColumnAlias("bookId")
    private Long id;

    private Long accountId;

    @ColumnAlias("bookName")
    private String name;
}
```

那么，假设我们的查询代码如下：

```java 6,7
List<AccountVO> bookVos = QueryChain.of(accountMapper)
    .select(
        ACCOUNT.ID,
        ACCOUNT.NAME,
        ACCOUNT.AGE,
        BOOK.ID,
        BOOK.NAME,
     )
    .from(ACCOUNT)
    .leftJoin(BOOK).on(ACCOUNT.ID.eq(BOOK.ACCOUNT_ID))
    .where(ACCOUNT.ID.ge(100))
    .listAs(AccountVO.class);
```
其执行的 SQL 为：

```sql 2,3
select tb_account.id, tb_account.name, tb_account.age,
    tb_book.id as bookId, -- @ColumnAlias("bookId")
    tb_book.name as bookName  -- @ColumnAlias("bookName")
from tb_account
left join tb_book on tb_account.id = tb_book.account_id
where tb_account.id >= 100
```

此时，数据也是可以正常映射。
