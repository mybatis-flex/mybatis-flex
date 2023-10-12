# 链式操作

在 MyBatis-Flex 中，内置了 `QueryChain.java` 、  `UpdateChain.java` 以及 `DbChain.java` 用于对数据进行链式查询操作和链式操作（修改和删除）。

- **QueryChain**：链式查询
- **UpdateChain**：链式更新
- **DbChain**：链式调用 [Db + Row](./db-row.md)


## QueryChain 示例

例如，查询文章列表代码如下：

```java

@SpringBootTest
class ArticleServiceTest {

    @Autowired
    ArticleService articleService;

    @Test
    void testChain() {
        List<Article> articles = articleService.queryChain()
            .select(ARTICLE.ALL_COLUMNS)
            .from(ARTICLE)
            .where(ARTICLE.ID.ge(100))
            .list();
    }
}
```

若不是在 Service 中，我们也可以通过 `QueryChain.of(mapper)` 方法，自己创建一个 `QueryChain` 实例，代码如下：

```java
List<Article> articles = QueryChain.of(mapper)
    .select(ARTICLE.ALL_COLUMNS)
    .from(ARTICLE)
    .where(ARTICLE.ID.ge(100))
    .list();
```

## UpdateChain 示例

假设我们要更新 `Account` 的 `userName` 为 "`张三`"，更新年龄在之前的基础上加 1，更新代码如下：

```java
@Test
public void testUpdateChain1() {
    UpdateChain.of(Account.class)
        .set(Account::getUserName, "张三")
        .setRaw(Account::getAge, "age + 1")
        .where(Account::getId).eq(1)
        .update();
}
```
以上方法调用时，MyBatis-Flex 内部执行的 SQL 如下：

```sql
UPDATE `tb_account` SET `user_name` = '张三' , `age` = age + 1
WHERE `id` = 1
```

**另一个示例：**

```java
@Test
public void testUpdateChain2() {

    //更新数据
    UpdateChain.of(Account.class)
        .set(Account::getAge, ACCOUNT.AGE.add(1))
        .where(Account::getId).ge(100)
        .and(Account::getAge).eq(18)
        .update();

    //查询所有数据并打印
    QueryChain.of(accountMapper)
        .where(Account::getId).ge(100)
        .and(Account::getAge).eq(18)
        .list()
        .forEach(System.out::println);
}
```
通过 `UpdateChain` 进行 `update()`，其执行的 SQL 如下：

```sql
UPDATE `tb_account` SET `age` = `age` + 1
WHERE  `id` >= 100 AND `age` = 18
```



## QueryChain 的方法

- one()：获取一条数据
- list()：获取多条数据
- page()：分页查询
- obj()：当 SQL 查询只返回 1 列数据的时候，且只有 1 条数据时，可以使用此方法
- objList()：当 SQL 查询只返回 1 列数据的时候，可以使用此方法
- count()：查询数据条数
- exists()：是否存在，判断 count 是否大于 0

## QueryChain 扩展方法

### `one()` 系列方法

- one()：获取一条数据
- oneAs(asType)：查询数据，并直接转换为 vo、dto 等
- oneWithRelations：查询一条数据及其关联数据
- oneWithRelationsAs：查询一条数据及其关联数据，并直接转换为 vo、dto 等
- oneOpt：返回 Optional 类型，获取一条数据
- oneAsOpt(asType)：返回 Optional 类型，查询数据，并直接转换为 vo、dto 等
- oneWithRelationsOpt：返回 Optional 类型，查询一条数据及其关联数据
- oneWithRelationsAsOpt：返回 Optional 类型，查询一条数据及其关联数据，并直接转换为 vo、dto 等

### `list()` 系列方法

- list()：查询数据列表
- listWithRelations()：查询数据列表及其关联数据
- listAs()：查询数据列表，并直接转换为 vo、dto 等
- listWithRelationsAs()：查询数据列表，及其关联数据，并直接转换为 vo、dto 等

### `page()` 系列方法

- page(page)：分页查询数据列表
- pageAs(page)：分页查询数据列表，并直接转换为 vo、dto 等

### `obj()` 系列方法

- obj()：查询第一列，且第一条数据
- objAs(asType)：查询第一列，且第一条数据并转换为指定类型，比如 Long, String 等
- objOpt()：返回 Optional 类型，查询第一列，且第一条数据
- objAsOpt(asType)：返回 Optional 类型，查询第一列，且第一条数据并转换为指定类型，比如 Long, String 等

### `objList()` 系列方法

- objList()：查询第一列
- objListAs(asType)：查询第一列，并转换为指定类型，比如 Long, String 等

## 代码实战示例

### 示例 1：查询 Entity 列表

```java
List<Article> articles = articleService.queryChain()
    .select(ARTICLE.ALL_COLUMNS)
    .from(ARTICLE)
    .where(ARTICLE.ID.ge(100))
    .list();
```

### 示例 2：查询 1 条 Entity 数据

```java
Article article = articleService.queryChain()
    .select(ARTICLE.ALL_COLUMNS)
    .from(ARTICLE)
    .where(ARTICLE.ID.ge(100))
    .limit(1)
    .one();
```

### 示例 3：查询 VO 数据（ArticleVo）

ArticleVo.java
```java
public class ArticleVo {

    private Long id;
    private Long accountId;
    private String title;
    private String content;

    //评论量最多的内容
    private Long maxComments;

    //getter setter
}
```

查询代码：

```java
ArticleVo articleVo = articleService.queryChain()
    .select(
        ARTICLE.ALL_COLUMNS,
        max(ARTICLE.comments).as(ArticleVo::maxComments)
    ).from(ARTICLE)
    .where(ARTICLE.ID.ge(100))
    .limit(1)
    .oneAs(ArticleVo.class);
```
### 示例 4：多对多关联查询 VO 数据（ArticleVo）

ArticleVo.java 及其 **文章分类** 定义：

```java
public class ArticleVo {

    private Long id;
    private Long accountId;
    private String title;
    private String content;

    //文章和分类的 多对多 关系配置
    @RelationManyToMany(
        joinTable = "tb_article_category_mapping", // 中间表
        selfField = "id", joinSelfColumn = "article_id",
        targetField = "id", joinTargetColumn = "category_id"
    )
    private List<ArticleCategory> categories;

    //getter setter
}
```

查询代码：

```java
ArticleVo articleVo = articleService.queryChain()
    .select()
    .from(ARTICLE)
    .where(ARTICLE.ID.ge(100))
    .limit(1)
    .oneWithRelationsAs(ArticleVo.class);
```

> 通过 `oneWithRelationsAs` 方法查询 `ArticleVo` 及其关联数据（多对多的文章分类）。
> 更多关于关联查询的内容请参考章节：[《关联查询》](/zh/base/relations-query.html)。


## DbChain 示例

使用 `DbChain` 之后无需将 `QueryWrapper` 与 `Row` 的构建分离，直接即可进行操作。

```java
// 新增 Row 构建
DbChain.table("tb_account")
    .setId(RowKey.AUTO)
    .set("user_name", "zhang san")
    .set("age", 18)
    .set("birthday", new Date())
    .save();

// 查询 QueryWrapper 构建
DbChain.table("tb_account")
    .select("id", "user_name", "age", "birthday")
    .where("age > ?", 18)
    .list()
    .forEach(System.out::println);
```
