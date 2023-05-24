# 一对多、多对一

在很多场景下，我们可能会用到 `一对多`、`一对一`、`多对一`等场景的关联查询，MyBatis-Flex 内置了相关的方法，用于支持此类场景。

## 代码示例

以下是文章的示例，一篇文章可能归属于多个分类。

```java
public class Article {
    private Long id;
    private String title;
    private String content;

    //文章的归属分类，可能是 1 个或者多个
    private List<Category> categories;

    //getter setter
}
```

查询代码如下：

```java {10-13}
QueryWrapper queryWrapper = QueryWrapper.create()
        .select().form(ARTICLE)
        .where(ARTICLE.id.ge(100));

List<Article> articles = mapper.selectListByQuery(queryWrapper, fieldQueryBuilder -> {
        fieldQueryBuilder
        .field(Article::getCategories) // 或者 .field("categories")
        .type(Category.class) //非集合，可以不指定 type
        .queryWrapper(article -> QueryWrapper.create()
            .select().from(CATEGORY)
            .where(CATEGORY.id.in(
                    select("category_id").from("article_category_mapping")
                    .where("article_id = ?", article.getId())
            )
        ));
    });
```

通过以上代码可以看出，`Article.categories` 字段的结果，来源于  `queryWrapper()` 方法构建的 `QueryWrapper`。

其原理是：MyBatis-Flex 的内部逻辑是先查询出 `Article` 的数据，然后再根据 `Article` 构建出新的 SQL，查询分类，并赋值给 `Article.categories`，
假设 `Article` 有 10 条数据，那么最终会进行 11 次数据库查询。

查询的 SQL 大概如下：

```sql
select * from tb_article where id  >= 100;

-- 以上 SQL 得到结果后，再执行查询分类的 SQL，如下：
select * from tb_category where id in 
(select category_id from article_category_mapping where article_id = 100);

select * from tb_category where id in
(select category_id from article_category_mapping where article_id = 101);

select * from tb_category where id in
(select category_id from article_category_mapping where article_id = 102);

select * from tb_category where id in
(select category_id from article_category_mapping where article_id = 103);

select * from tb_category where id in
(select category_id from article_category_mapping where article_id = 104);

select * from tb_category where id in
(select category_id from article_category_mapping where article_id = 105);

select * from tb_category where id in
(select category_id from article_category_mapping where article_id = 106);

select * from tb_category where id in
(select category_id from article_category_mapping where article_id = 107);

select * from tb_category where id in
(select category_id from article_category_mapping where article_id = 108);

select * from tb_category where id in
(select category_id from article_category_mapping where article_id = 109);
```

## 其他场景

通过以上内容看出，`Article` 的任何属性，都是可以通过传入 `FieldQueryBuilder` 来构建 `QueryWrapper` 进行再次查询，
这些不仅仅只适用于  `一对多`、`一对一`、`多对一`等场景。任何 `Article` 对象里的属性，需要二次查询赋值的，都是可以通过这种方式进行。