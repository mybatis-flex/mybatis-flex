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

