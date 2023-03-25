# E@Column 注解的使用

Mybatis-Flex 提供了 `@Column` 用来对字段进行更多的配置，以下是 `@Column` 的代码定义：

```java
public @interface Column {

    /**
     * 字段名称
     */
    String value() default "";

    /**
     * 是否忽略该字段，可能只是业务字段，而非数据库对应字段
     */
    boolean ignore() default false;

    /**
     * insert 的时候默认值，这个值会直接被拼接到 sql 而不通过参数设置
     */
    String onInsertValue() default "";

    /**
     * update 的时候自动赋值，这个值会直接被拼接到 sql 而不通过参数设置
     */
    String onUpdateValue() default "";

    /**
     * 是否是大字段，大字段 APT 不会生成到 ALL_COLUMNS 里
     */
    boolean isLarge() default false;

    /**
     * 是否是逻辑删除字段，一张表中只能存在 1 一个逻辑删除字段
     * 逻辑删除的字段，被删除时，会设置为 1，正常状态为 0
     */
    boolean isLogicDelete() default false;

    /**
     * 是否为乐观锁字段，若是乐观锁字段的话，数据更新的时候会去检测当前版本号，若更新成功的话会设置当前版本号 +1
     * 只能用于数值的字段
     */
    boolean version() default false;

}
```

## value 属性

value 是用来标识列名的，默认情况下， entity 中的字段转换为列名默认以下划线的方式进行转换， 例如，userName 对应的列名为 user_name。

```java
public class Account {

    private Long id;

    private String userName;

    @Column("birthday_datetime")
    private Date birthday;
}
```

以上代码中，3 个属性分别对应的字段为： id, user_name, birthday_datetime。

## ignore

当我们为了业务需要，在 entity 类中添加了某个字段，但是数据库却不存在该列时，使用 `@Column(ignore = true)` 修饰。


## onInsertValue

设置数据被插入时的默认值，例如：

```java

@Table("tb_article")
public class Article {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String title;

    @Column(onInsertValue = "now()")
    private Date created;
}
```

当数据被插入的时候，生成的 SQL 如下：

```sql
INSERT INTO `tb_article`(title, created)
VALUES (?, now())
```

需要注意的是，在 insert 中，`onInsertValue` 配置的内容会直接参与 SQL 拼接，而不是通过 JDBC 的 Statement 参数设置，需要开发者注意 `onInsertValue` 的内容，否则可能会造成 SQL
错误。

## onUpdateValue

当数据被更新时，设置的默认值。

```java

@Table("tb_article")
public class Article {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String title;

    @Column(onUpdateValue = "now()", onInsertValue = "now()")
    private Date modified;
}
```

当数据更新时，其执行的 SQL 如下：

```sql
UPDATE `tb_article`
SET `title`    = ?,
    `modified` = now()
WHERE `id` = ?
```

`onUpdateValue` 配置的内容 "now()" 会直接参与 SQL 的赋值拼接。

## isLarge

用于标识这个字段是否是大字段，比如说存放文章的文章字段，在一般的场景中是没必要对这个字段进行查询的， 若字段被表示为 `isLarge`，那么 APT 生成 "ARTICLE" 类时，默认不会存放在 DEFAULT_COLUMNS 中，以下
是 Article.java 以及 APT 生成的类：

```java

@Table("tb_article")
public class Article {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String title;

    @Column(isLarge = true)
    private String content;

    @Column(onInsertValue = "now()")
    private Date created;

    @Column(onUpdateValue = "now()", onInsertValue = "now()")
    private Date modified;
}
```

其生成的 ArticleTableDef 如下：

```java
public class Tables {
    public static final ArticleTableDef ARTICLE = new ArticleTableDef("tb_article");

    public static class ArticleTableDef extends TableDef {

        public QueryColumn ID = new QueryColumn(this, "id");
        public QueryColumn TITLE = new QueryColumn(this, "title");
        public QueryColumn CONTENT = new QueryColumn(this, "content");
        public QueryColumn CREATED = new QueryColumn(this, "created");
        public QueryColumn MODIFIED = new QueryColumn(this, "modified");

        //在 DEFAULT_COLUMNS 中是没有 content 字段。
        public QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, TITLE, CREATED, MODIFIED};
        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{ID, TITLE, CONTENT, CREATED, MODIFIED};
    }
}
```

一般的场景中，我们查询内容应该如下：
```java
QueryWrapper.create()
        //使用的是 DEFAULT_COLUMNS
        .select(ARTICLE.DEFAULT_COLUMNS)
        .form(DEFAULT_COLUMNS)
        .where(...)
```

## isLogicDelete

这部分的文档参考 [逻辑删除章节](./logic_delete.md)。

## version

这部分的文档参考 [乐观锁章节](./logic_delete.md)。