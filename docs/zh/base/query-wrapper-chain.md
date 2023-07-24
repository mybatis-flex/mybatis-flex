# QueryWrapperChain

`QueryWrapperChain.java` 是一个对 `QueryWrapper` 进行链式调用封装的一个类，在 Service 中，
我们可以调用 `service.queryChain()` 获得该实例。

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

若不是在 Service 中，我们也可以通过 `QueryWrapperChain.create` 方法，自己创建一个 `QueryWrapperChain` 实例，代码如下：

```java
List<Article> articles = QueryWrapperChain.create(mapper)
    .select(ARTICLE.ALL_COLUMNS)
    .from(ARTICLE)
    .where(ARTICLE.ID.ge(100))
    .list();
```

## QueryWrapperChain 的方法

- one()：获取一条数据
- list()：获取多条数据
- page()：分页查询
- obj()：当 SQL 查询只返回 1 列数据的时候，且只有 1 条数据时，可以使用此方法
- objList()：当 SQL 查询只返回 1 列数据的时候，可以使用此方法
- remove()：删除数据
- update(entity)：更新数据
- count()：查询数据条数
- exists()：是否存在，判断 count 是否大于 0

## 扩展方法

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
- listWithRelations()：查询数据列表极其关联数据
- listAs()：查询数据列表，并直接转换为 vo、dto 等
- listWithRelationsAs()：查询数据列表，及其关联数据，并直接转换为 vo、dto 等

### `page()` 系列方法

- page(page)：分页查询数据列表
- pageWithRelations(page)：分页查询数据列表极其关联数据
- pageAs(page)：分页查询数据列表，并直接转换为 vo、dto 等
- pageWithRelationsAs(page)：分页查询数据列表，及其关联数据，并直接转换为 vo、dto 等

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
        max(ARTICLE.comments).as(ArticleVo::maxCommments)
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

