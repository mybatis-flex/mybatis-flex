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
            .getList();
    }
}
```

若不是在 Service 中，我们也可以通过 `QueryWrapperChain.create` 方法，自己创建一个 `QueryWrapperChain` 实例，代码如下：

```java
List<Article> articles = QueryWrapperChain.create(mapper)
    .select(ARTICLE.ALL_COLUMNS)
    .from(ARTICLE)
    .where(ARTICLE.ID.ge(100))
    .getList();
```

## QueryWrapperChain 的方法

- getOne()：获取一条数据
- getList()：获取多条数据
- getPage()：分页查询
- getObj()：当 SQL 查询只返回 1 列数据的时候，且只有 1 条数据时，可以使用此方法
- getObjList()：当 SQL 查询只返回 1 列数据的时候，可以使用此方法
- remove()：删除数据
- update(entity)：更新数据
- count()：查询数据条数
- exists()：是否存在，判断 count 是否大于 0

## 扩展方法

### `getOne()` 系列方法

- getOne()：获取一条数据
- getOneAs(asType)：查询数据，并直接转换为 vo、dto 等
- getOneWithRelations：查询一条数据及其关联数据
- getOneWithRelationsAs：查询一条数据及其关联数据，并直接转换为 vo、dto 等
- getOneOpt：返回 Optional 类型，获取一条数据
- getOneAsOpt(asType)：返回 Optional 类型，查询数据，并直接转换为 vo、dto 等
- getOneWithRelationsOpt：返回 Optional 类型，查询一条数据及其关联数据
- getOneWithRelationsAsOpt：返回 Optional 类型，查询一条数据及其关联数据，并直接转换为 vo、dto 等


### `getList()` 系列方法

- getList()：查询数据列表
- getListWithRelations()：查询数据列表极其关联数据
- getListAs()：查询数据列表，并直接转换为 vo、dto 等
- getListWithRelationsAs()：查询数据列表，及其关联数据，并直接转换为 vo、dto 等


### `getPage()` 系列方法

- getPage(page)：分页查询数据列表
- getPageWithRelations(page)：分页查询数据列表极其关联数据
- getPageAs(page)：分页查询数据列表，并直接转换为 vo、dto 等
- getPageWithRelationsAs(page)：分页查询数据列表，及其关联数据，并直接转换为 vo、dto 等

### `getObj()` 系列方法

- getObj()：查询第一列，且第一条数据
- getObjAs(asType)：查询第一列，且第一条数据并转换为指定类型，比如 Long, String 等
- getObjOpt()：返回 Optional 类型，查询第一列，且第一条数据
- getObjAsOpt(asType)：返回 Optional 类型，查询第一列，且第一条数据并转换为指定类型，比如 Long, String 等

### `getObjList()` 系列方法

- getObjList()：查询第一列
- getObjListAs(asType)：查询第一列，并转换为指定类型，比如 Long, String 等
- getObjListOpt()：返回 Optional 类型，查询第一列
- getObjListAsOpt(asType)：返回 Optional 类型，查询第一列，并转换为指定类型，比如 Long, String 等






