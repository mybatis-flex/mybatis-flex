# MyBatis-Flex 的查询和分页

## 基础查询

在 MyBatis-Flex 的 `BaseMapper` 中，提供了如下的功能用于查询数据库的数据：

<!--@include:./parts/base-mapper-query-methods.md-->

**select..As 使用注意事项：**

假设项目中有 `User.java` 的 Entity 类以及 `UserVo.java` 两个类。而 `User.java` 的代码如下

```java
public class User {

    @Column(typeHandler=xxxHandler.class)
    private String attr1;

    //getter setter
}
```

`User.java` 的 `attr1` 属性配置了 `typeHandler`，当我们通过 `userMapper.select...As(UserVo.class)` 查询得到 `UserVo` 的时候，
也同样需要在 `UserVo` 的 `attr1` 属性中也配置上 `@Column(typeHandler=xxxHandler.class)`，两者才能得到相同的结果。

## 游标查询

我们对大量数据进行处理时，为防止方法内存泄漏情况，应该使用游标（Cursor）方式进行数据查询并处理数据。
在 `BaseMapper` 中，存在如下的游标查询方法：

```java
Cursor<T> selectCursorByQuery(QueryWrapper queryWrapper);
```
其使用方法如下：

```java
Db.tx(() -> {
    Cursor<Account> accounts = accountMapper.selectCursorByQuery(query);
    for (Account account : accounts) {
        System.out.println(account);
    }
    return true;
});
```

以上的示例中，数据库并**不是**把所有的数据一次性返回给应用，而是每循环 1 次才会去数据库里拿 1 条数据，这样，就算有 100w 级数据，也不会导致我们应用内存溢出，同时，在 for 循环中，
我们可以随时终止数据读取。

但由于游标查询是在 for 循环的时候，才去数据库拿数据。因此必须保证 `selectCursorByQuery` 方法及其处理必须是在事务中进行，才能保证其链接并未与数据库断开。

**以下场景经常需要用到游标查询功能：**

- 1、数据查询并写入到缓存
- 2、Excel 导出等

## 查询 Map 集合

```java
List<Row> selectRowsByQuery(QueryWrapper queryWrapper);
```

## Relations 注解查询

Relations 注解查询指的是用于查询带有注解 `@RelationOneToOne`，`@RelationOneToMany`，`@RelationManyToOne`，`@RelationManyToMany` 的查询。

<!--@include:./parts/base-mapper-relation-methods.md-->


## 多表查询（关联查询）

在 `BaseMapper` 中，提供了 `selectOneByQueryAs`、`selectListByQueryAs` 、`paginateAs` 等方法，用于处理关联查询的场景。

假设有 `tb_account` 用户表和 `tb_article` 文章表，他们的字段分别如下：

```sql
CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`        INTEGER PRIMARY KEY auto_increment,
    `user_name` VARCHAR(100),
    `age`       Integer,
    `birthday`  DATETIME
);

CREATE TABLE IF NOT EXISTS `tb_article`
(
    `id`         INTEGER PRIMARY KEY auto_increment,
    `account_id` Integer,
    `title`      VARCHAR(100),
    `content`    text
);
```

当我们进行关联查询时，可以通过如下 3 种方式进行。

### 方式 1

1、定义 `ArticleDTO` 类，`ArticleDTO` 里定义 `tb_account` 表的字段映射。
```java
public class ArticleDTO {

    private Long id;
    private Long accountId;
    private String title;
    private String content;

    //以下用户相关字段
    private String userName;
    private int age;
    private Date birthday;
}
```

2、使用 `QueryWrapper` 构建 `left join` 查询，查询结果通过 `ArticleDTO` 类型接收。
```java
QueryWrapper query = QueryWrapper.create()
        .select(ARTICLE.ALL_COLUMNS)
        .select(ACCOUNT.USER_NAME,ACCOUNT.AGE,ACCOUNT.BIRTHDAY)
        .from(ARTICLE)
        .leftJoin(ACCOUNT).on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
        .where(ACCOUNT.ID.ge(0));

List<ArticleDTO> results = mapper.selectListByQueryAs(query, ArticleDTO.class);
System.out.println(results);
```

### 方式 2

假设 `ArticleDTO` 定义的属性和 SQL 查询的字段不一致时，例如：

```java
public class ArticleDTO {

  private Long id;
  private Long accountId;
  private String title;
  private String content;

  //以下用户字段 和 用户表定义的列不一致，表定义的列为 user_name
  private String authorName;
  private int authorAge;
  private Date birthday;
}
```

那么， `QueryWrapper` 需要添加 `as`，修改如下：

```java 3,4
QueryWrapper query = QueryWrapper.create()
    .select(ARTICLE.ALL_COLUMNS)
    .select(ACCOUNT.USER_NAME.as(ArticleDTO::getAuthorName)
            ,ACCOUNT.AGE.as(ArticleDTO::getAuthorAge)
            ,ACCOUNT.BIRTHDAY
    )
    .from(ARTICLE)
    .leftJoin(ACCOUNT).on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
    .where(ACCOUNT.ID.ge(0));

List<ArticleDTO> results = mapper.selectListByQueryAs(query, ArticleDTO.class);
System.out.println(results);
```

### 方式 3 <Badge type="tip" text="^ v1.3.3" />

1、定义 `ArticleDTO` 类， 在 `ArticleDTO` 定义 `Account` 实体类属性。 例如：

```java
public class ArticleDTO {

    private Long id;
    private Long accountId;
    private String title;
    private String content;

    //直接定义 Account 对象
    private Account account;
}
```

2、使用 `QueryWrapper` 构建 `left join` 查询，查询结果通过 `ArticleDTO` 类型接收。

```java
QueryWrapper query = QueryWrapper.create()
        .select(ARTICLE.ALL_COLUMNS)
        .select(ACCOUNT.USER_NAME,ACCOUNT.AGE,ACCOUNT.BIRTHDAY)
        .from(ARTICLE)
        .leftJoin(ACCOUNT).on(ARTICLE.ACCOUNT_ID.eq(ACCOUNT.ID))
        .where(ACCOUNT.ID.ge(0));

List<ArticleDTO> results = mapper.selectListByQueryAs(query, ArticleDTO.class);
System.out.println(results);
```



**其他注意事项：**

> 关联查询（`selectOneByQueryAs`、`selectListByQueryAs` 、`paginateAs` 等方法）中的 `asType` 参数类型（比如：`ArticleDTO`），
> 一样支持使用 `@Column`、`@ColumnMask` 注解以及 `@Table` 的 `onInsert`、`onUpdate`、`onSet` 配置。


## 分页查询

在 MyBatis-Flex 的 BaseMapper 中，提供了如下的分页查询功能：

<!--@include:./parts/base-mapper-paginate-methods.md-->

**参数说明：**

- pageNumber： 当前页码，从 1 开始
- pageSize： 每 1 页的数据量
- totalRow： 非必须值，若传入该值，mybatis-flex 则不再去查询总数据量（若传入小于 0 的数值，也会去查询总量）。
- queryWrapper： 查询条件
- queryCondition： 查询条件

::: tip totalRow 的说明
在一般的分页场景中，只有第一页的时候有必要去查询数据总量，第二页以后是没必要的（因为第一页已经拿到总量了），因此，
第二页的时候，我们可以带入 `totalRow`，这样能提高程序的查询效率。
:::

paginate 的返回值为 Page 对象，Page 类的定义如下：

```java
public class Page<T> implements Serializable {
    private List<T> records;                // list result of this page
    private int pageNumber;              // page number
    private int pageSize;                // result amount of this page
    private long totalPage;              // total page
    private long totalRow;               // total row
}
```


