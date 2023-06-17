# 数据填充

数据填充指的是，当 Entity 数据被插入 或者 更新的时候，会为字段进行一些默认的数据设置。这个非常有用，比如当某个 entity 被插入时候
会设置一些数据插入的时间、数据插入的用户 id，多租户的场景下设置当前租户信息等等。

MyBatis-Flex 提供了两种方式，帮助开发者进行数据填充。

- 1、通过 `@Table` 注解的 `onInsert` 和 `onUpdate` 配置进行操作。这部分可以参考 [@Table 注解章节](./table) 。
- 2、通过 `@Column` 注解的 `onInsertValue` 和 `onUpdateValue` 配置进行操作。这部分可以参考 [@Column 注解章节](./column)。



## 疑惑点
**1、`@Table` 注解的 `onInsert` 和  `@Column` 注解的 `onInsertValue` 有什么区别？**

答：`@Table` 注解的 `onInsert` 主要是在 Java 应用层面进行数据设置，而 `@Column` 注解的 `onInsertValue` 则是在数据库层面进行数据设置。

例如：

```java 9
@Table("tb_article")
public class Article {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String title;
    
    @Column(onInsertValue = "now()")
    private Date created;
}
```

当数据被插入时，其执行的 Sql 如下：

```sql
INSERT INTO `tb_article`(title, created)
VALUES (?, now())
```

`@Column(onInsertValue = "now()")` 中的 `now()` 是 Sql 的一部分（一个函数），我们可以配置更加复杂，例如：

```java 9
@Table("tb_article")
public class Article {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String title;
    
    @Column(onUpdateValue = "version + 1")
    private int version;
}
```

当数据被 update 的时候，其执行的 sql 如下：

```sql
update tb_article set title = ?,version = version + 1
```

更复杂的场景，我们可以配置如下：

```java 9
@Table("tb_article")
public class Article {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String title;
    
    @Column(onUpdateValue = "(select xxx from other_table where ...)")
    private int version;
}
```