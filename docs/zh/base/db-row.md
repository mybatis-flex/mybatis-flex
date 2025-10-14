# Db + Row 工具的使用

Db + Row 工具类，提供了在 Entity 实体类之外的数据库操作能力。使用 Db + Row 时，无需对数据库表进行映射， Row 是一个 HashMap 的子类，相当于一个通用的 Entity。以下为 Db + Row 的一些示例：


```java
//使用原生 SQL 插入数据
String sql = "insert into tb_account(id,name) value (?, ?)";
Db.insertBySql(sql,1,"michael");

//使用 Row 插入数据
Row account = new Row();
account.set("id",100);
account.set(ACCOUNT.USER_NAME,"Michael");
Db.insert("tb_account",account);


//根据主键查询数据
Row row = Db.selectOneById("tb_account","id",1);

//Row 可以直接转换为 Entity 实体类，且性能极高
Account account = row.toEntity(Account.class);


//查询所有大于 18 岁的用户
String listsql = "select * from tb_account where age > ?"
List<Row> rows = Db.selectListBySql(listsql,18);


//查询所有大于 18 岁用户的Id和用户名对应的Map
Map map = Db.selectFirstAndSecondColumnsAsMap("select id,user_name from tb_account where age >?",18);

//分页查询：每页 10 条数据，查询第 3 页的年龄大于 18 的用户
QueryWrapper query=QueryWrapper.create()
    .where(ACCOUNT.AGE.ge(18));
Page<Row> rowPage=Db.paginate("tb_account",3,10,query);
```

使用 MyBatis `#{}` `${}` 的方式传参 <Badge type="tip" text="v1.8.5" />

```java
Map<String, Integer> map = Collections.singletonMap("age", 18);
List<Row> rowList = Db.selectListBySql("select * from tb_account where age > #{age}", map);
RowUtil.printPretty(rowList);
```

> Db 工具类还提供了更多 增、删、改、查和分页查询等方法。
>
>
具体参考： [Db.java](https://gitee.com/mybatis-flex/mybatis-flex/blob/main/mybatis-flex-core/src/main/java/com/mybatisflex/core/row/Db.java) 。

## DbChain 链式 Db 调用

使用 `DbChain` 之后无需将 `QueryWrapper` 与 `Row` 的构建分离，直接即可进行操作。

```java
// 新增 Row 构建
DbChain.table("tb_account")
    .setId(RowKey.AUTO)
    .set("user_name","zhangsan")
    .set("age",18)
    .set("birthday",new Date())
    .save();

// 查询 QueryWrapper 构建
DbChain.table("tb_account")
    .select("id","user_name","age","birthday")
    .where("age > ?",18)
    .list()
    .forEach(System.out::println);
```

## Row.toEntity()

`Row.toEntity(Entity.class)` 方法主要是用于可以把 Row 转换为 entity 实体类。通过这个方法，可以把 Entity 里的
`@Column()` 配置的列名和 Row 里的 key 进行自动关联。

代码示例：

```java
Row row=Db.selectOneBySql("select * from ....");
Account entity = row.toEntity(Account.class);
```

需要注意的是，当我们进行 join 关联查询时，返回的结果如果出现重复字段，Row 会自动添加上 字段序号。

例如：

```sql
CREATE TABLE IF NOT EXISTS `tb_account`
(
    `id`        INTEGER PRIMARY KEY auto_increment,
    `user_name` VARCHAR(100),
    `age`       Integer,
    `is_delete` Integer
);


CREATE TABLE IF NOT EXISTS `tb_article`
(
    `id`         INTEGER PRIMARY KEY auto_increment,
    `account_id` Integer,
    `title`      VARCHAR(100),
    `content`    text,
    `is_delete` Integer
);

INSERT INTO tb_account
VALUES (1, '张三' ,18, 0),
       (2, '王麻子叔叔' ,19, 0);


INSERT INTO tb_article
VALUES (1, 1,'标题1', '内容1',0),
       (2, 2,'标题2', '内容2',0);
```
在以上的数据中，我们通过如下的 left join 查询文章和用户表：

```java
QueryWrapper query = new QueryWrapper();
query.select().from(ACCOUNT).leftJoin(ARTICLE)
        .on(ACCOUNT.ID.eq(ARTICLE.ACCOUNT_ID));

List<Row> rows = Db.selectListByQuery(query);
```

返回的 Row 集合如下：
```
|ID    |USER_NAME    |AGE    |IS_DELETE    |ID$1    |ACCOUNT_ID    |TITLE      |CONTENT    |IS_DELETE$1    |
|1     |张三          |18     |0            |1       |1             |标题1      |内容1       |0              |
|2     |王麻子叔叔     |19     |0            |2       |2             |标题2      |内容2       |0              |
```

前面 4 列属于 `Account` 的数据，后面 5 列属于 `Article` 的数据。在后面的 `Article` 表中，有 `id`、`is_delete` 和 Account 的表的列名重复。
此时，重复的列名会自动添加上 `$序号` ，而非数据库返回的真正列名。

因此，我们进行 toEntity 数据转换的时候，需要添加上序号，例如：

```java
List<Account> accounts = RowUtil.toEntityList(rows, Account.class);
System.out.println(accounts);

//添加上序号 1
List<Article> articles = RowUtil.toEntityList(rows, Article.class, 1);
System.out.println(articles);
```



## Row.toObject()

`Row.toObject(Other.class)` 和 `Row.toEntity(Entity.class)` 相似。不一样的地方在于 `Row.toObject(Other.class)` 是通过去查找
`Other.class` 的 `setter` 方法去匹配 Row 的 key 进行赋值的。

例如 `Other.class` 的代码如下：

```java
public class Other {
    private String id;
    private String userName;

    //getter setter
}
```

那么，当我们去通过 SQL 查询得到 Row 的时候，Row 里的 `key` 为 `userName`、`UserName`、`USERNAME`、`user_name`、`USER_NAME` 等
都能自动适配到 `Other.userName` 属性。这个方法常用于把 Row 直接转换为 VO 的场景。

> PS：我们可以通过调用 `RowUtil.registerMapping(clazz, columnSetterMapping)` 去让更多的 `字段` 名称和 `属性` 进行匹配。


代码示例：

```java
Row row = Db.selectOneBySql("select * from ....");
Other other = row.toObject(Other.class);
```

在以上代码中，如果出现了 `left join` 等情况下，需要添加上序号。

## Row 字段转化为驼峰风格

```java
Row row = Db.selectOneBySql("select * from ....");
Map result = row.toCamelKeysMap();
```

## Row 字段转换为下划线风格

```java
Row row = Db.selectOneBySql("select * from ....");
Map result = row.toUnderlineKeysMap();
```

## Row 插入时，设置主键生成方式

**ID 自增**

```java
// ID 自增
Row row = Row.ofKey(RowKey.ID_AUTO);
row.set(ACCOUNT.USER_NAME,"Michael");

Db.insert("tb_account",row);
```

**ID 为 UUID**

```java
// ID 为 uuid
Row row = Row.ofKey(RowKey.ID_UUID);
row.set(ACCOUNT.USER_NAME,"Michael");

Db.insert("tb_account",row);
```
**自定义 Row 主键生成方式**

```java
// 自定义 Row Key，整个应用定义一个常量即可
RowKey myRowKey = RowKey.of("id", KeyType.Generator, "uuid", true);

// 使用自定义的 RowKey
Row row = Row.ofKey(myRowKey);
row.set(ACCOUNT.USER_NAME,"Michael");

Db.insert("tb_account",row);
```

## RowUtil 工具类

`RowUtil` 工具类是用于帮助用户快速的把 `Row` 或者 `List<Row>` 转换为 VO 的工具类。其提供的方法如下：

- `RowUtil.toObject(row, objectClass)`
- `RowUtil.toObjectList(rows, objectClass)`
- `RowUtil.toEntity(row, entityClass)`
- `RowUtil.toEntityList(rows, entityClass)`
- `RowUtil.registerMapping(clazz, columnSetterMapping)` 用于注册数据库 `字段` 名称和 Class 属性的映射关系。
- `RowUtil.printPretty(rows)` 打印 `Row` 或者 `List<Row>` 数据到控制台，一般用户调试。


`RowUtil.printPretty` 输入内容如下：
```
Total Count: 12
|ID     |USER_NAME     |AGE     |SEX     |BIRTHDAY     |IS_DELETE     |
|1      |张三           |18      |0       |2020-01-1... |0             |
|2      |王麻子叔叔      |19      |1       |2021-03-2... |0             |
|3      |zhang0        |18      |null    |2023-04-2... |null          |
|4      |zhang1        |18      |null    |2023-04-2... |null          |
|5      |zhang2        |18      |null    |2023-04-2... |null          |
|6      |zhang3        |18      |null    |2023-04-2... |null          |
|7      |zhang4        |18      |null    |2023-04-2... |null          |
|8      |zhang5        |18      |null    |2023-04-2... |null          |
|9      |zhang6        |18      |null    |2023-04-2... |null          |
|10     |zhang7        |18      |null    |2023-04-2... |null          |
|11     |zhang8        |18      |null    |2023-04-2... |null          |
|12     |zhang9        |18      |null    |2023-04-2... |null          |
```
