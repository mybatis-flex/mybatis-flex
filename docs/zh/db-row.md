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
List<Row> rows = Db.selectListBySql(sql,18);


//分页查询：每页 10 条数据，查询第 3 页的年龄大于 18 的用户
QueryWrapper query = QueryWrapper.create()
        .where(ACCOUNT.AGE.ge(18));
Page<Row> rowPage = Db.paginate("tb_account",3,10,query);
```

> Db 工具类还提供了更多 增、删、改、查和分页查询等方法。
>
> 具体参考： [Db.java](./mybatis-flex-core/src/main/java/com/mybatisflex/core/row/Db.java) 。

## Row.toEntity()

`Row.toEntity(Entity.class)` 方法主要是用于可以把 Row 转换为 entity 实体类。通过这个方法，可以把 Entity 里的
`@Column()` 配置的列名和 Row 里的 key 进行自动关联。

代码示例：

```java
Row row = Db.selectOneBySql("select * from ....");
Account entity = row.toEntity(Account.class);
```

## Row.toObject()

`Row.toObject(Other.class)` 和 `Row.toEntity(Entity.class)` 和相似。不一样的地方在于 `Row.toObject(Other.class)` 是通过去查找
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

## Row 字段转化为驼峰风格

```java
Row row = Db..selectOneBySql("select * from ....");
Map result = row.toCamelKeysMap();
```

## Row 字段转换为下划线风格

```java
Row row = Db..selectOneBySql("select * from ....");
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