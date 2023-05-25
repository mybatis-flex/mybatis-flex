# MyBatis-Flex 的查询和分页

## 基础查询

在 MyBatis-Flex 的 `BaseMapper` 中，提供了如下的功能用于查询数据库的数据：

- **selectOneById(id)**：根据主键 id 查询数据
- **selectOneByMap(map)**：根据 `map<字段名，值>` 组成的条件查询 1 条数据，若命中多条数据，则只返回第一条数据。
- **selectOneByCondition(condition)**：根据 condition 组成的条件查询 1 条数据，若命中多条数据，则只返回第一条数据。
- **selectOneByQuery(query)**：根据 QueryWrapper 组成的条件查询 1 条数据，若命中多条数据，则只返回第一条数据。
- **selectOneByQueryAs(query, asType)**：和 `selectOneByQuery` 方法类似，但是在某些场景下，`query` 可能包含了 `left join` 等多表查询，返回的数据和 entity 字段不一致时，
可以通过 `asType` 参数来指定接收的数据类型（通常是 dto、vo 等）。
- **selectListByIds(idList)**：根据多个 id 查询，返回多条数据
- **selectListByMap(map)**：根据  `map<字段名，值>` 组成的条件查询数据。
- **selectListByMap(map, count)**：根据  `map<字段名，值>` 组成的条件查询数据，只取前 count 条。
- **selectListByCondition(condition)**：根据 condition 组成的条件查询数据。
- **selectListByCondition(condition, count)**：根据  condition 组成的条件查询数据，只取前 count 条。
- **selectListByQuery(query)**： 根据  QueryWrapper 组成的条件查询数据。
- **selectListByQueryAs(query, asType)**： 和 `selectListByQuery` 方法类似，但是在某些场景下，`query` 可能包含了 `left join` 等多表查询，返回的数据和 entity 字段不一致时，
  可以通过 `asType` 参数来指定接收的数据类型（通常是 dto、vo 等）。
- **selectAll**：查询所有数据。
- **selectObjectByQuery(query)**：查询只返回 1 列，并只有 1 条数据的场景。
- **selectObjectListByQuery(query)**：查询只返回 1 列场景，比如 `QueryWrapper.create().select(ACCOINT.ID).from(...)`。
- **selectObjectListByQueryAs(query, asType)**：对 `selectObjectListByQuery` 进行封装，并转换为特定的类型。
- **selectCountByCondition**：根据 QueryWrapper 查询数据量。
- **selectCountByQuery**：根据 QueryWrapper 查询数据量。

## 关联查询（或多表查询）

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

当我们进行关联查询时，可以通过如下代码进行：

1、定义 `ArticleDTO` 类
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


**注意事项：**

关联查询（`selectOneByQueryAs`、`selectListByQueryAs` 、`paginateAs` 等方法）中的 `asType` 参数类型（比如：`ArticleDTO`），
一样支持使用 `@Column`、`@ColumnMask` 注解以及 `@Table` 的 `onInsert`、`onUpdate`、`onSet` 配置。

同时，要求 `asType` 中的类型，必须定义属性来映射查询到的数据集，因此，以下的使用示例是 **错误** 的：

```java
QueryWrapper query = QueryWrapper.create()
        .select(ACCOUNT.id)
        .from(ACCOUNT);

List<Long> results = mapper.selectOneByQueryAs(query, Long.class);
```
在以上的示例中， **错误** 的原因是因为 `Long` 这个类型，并没有名称为 `id` 的属性，用来映射查询的结果集。

在以上的场景中，可以使用如下的方法（以下代码示例是正确的）：
```java
QueryWrapper query = QueryWrapper.create()
        .select(ACCOUNT.id)
        .from(ACCOUNT);

List<Long> results = mapper.selectObjectListByQueryAs(query, Long.class);
```

## 分页查询

在 MyBatis-Flex 的 BaseMapper 中，提供了如下的分页查询功能：

```java
Page<T> paginate(int pageNumber, int pageSize, QueryWrapper queryWrapper);
Page<T> paginate(int pageNumber, int pageSize, int totalRow, QueryWrapper queryWrapper);

Page<T> paginate(int pageNumber, int pageSize, QueryCondition condition);
Page<T> paginate(int pageNumber, int pageSize, int totalRow, QueryCondition condition);
```
- pageNumber： 当前页码，从 1 开始
- pageSize： 每 1 页的数据量
- totalRow： 非必须值，若传入该值，mybatis-flex 则不再去查询总数据量（若传入小于 0 的数值，也会去查询总量）。
- queryWrapper： 查询条件
- QueryCondition： 查询条件

::: tip totalRow 的说明
在一般的分页场景中，只有第一页的时候有必要去查询数据总量，第二页以后是没必要的（因为第一页已经拿到总量了），因此，
第二页的时候，我们可以带入 `totalRow`，这样能提高程序的查询效率。
:::

paginate 的返回值为 Page 对象，Page 类的定义如下：

```java
public class Page<T> implements Serializable {
    private List<T> list;                // list result of this page
    private int pageNumber;              // page number
    private int pageSize;                // result amount of this page
    private long totalPage;              // total page
    private long totalRow;               // total row
}
```


