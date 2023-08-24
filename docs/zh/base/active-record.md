# Active Record <Badge type="tip" text="v1.5.3" />

[Active Record](http://www.martinfowler.com/eaaCatalog/activeRecord.html) 模式出自 Martin Fowler
写的《[企业应用架构模式](https://book.douban.com/subject/4826290/)》书中。在 Active Record
模式中，对象中既有持久存储的数据，也有针对数据的操作。Active Record 模式把数据存取逻辑作为对象的一部分，处理对象的用户知道如何把数据写入数据库，还知道如何从数据库中读出数据。

在 MyBatis-Flex 中实现 Active Record
功能十分简单，只需让 Entity 类继承 [Model](https://gitee.com/mybatis-flex/mybatis-flex/blob/main/mybatis-flex-core/src/main/java/com/mybatisflex/core/activerecord/Model.java)
即可。

::: tip 注意事项
- 使用 Active Record 功能时，项目中必须注入对应实体类的 BaseMapper 对象。
- 如果不想手动创建 Mapper 接口，可以使用 [代码生成器](../others/codegen.md) 或 [APT](../others/apt.md#配置文件和选项) 辅助生成。
  :::

## 使用示例

在以下示例当中，使用了 [Lombok](https://www.projectlombok.org/) 对实体类进行了增强，以便我们全链式调用：

1. `@Table` 标记了实体类对应的数据表。
2. `@Data` 为我们生成了 setter/getter、toString、equals、hashCode 等方法，
   其中 `staticConstructor = "create"` 为我们创建了一个 `create()` 静态方法用于链式调用。
3. `@Accessors(chain = true)` 为我们开启了 `return this;` 这样既可以被序列化，又可以链式调用。

```java

@Table("tb_account")
@Accessors(chain = true)
@Data(staticConstructor = "create")
public class Account extends Model<Account> {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;
    private Integer age;
    private Date birthday;

}
```

这样我们就可以流畅的使用 Active Record 功能了：

```java
@RestController
@RequestMapping("/account")
public class AccountController {

    @PostMapping("save")
    public boolean save(@RequestBody Account account) {
        return account.save();
    }

}
```

## 保存数据

`Model` 提供了 `save` 方法来保存数据，调用该方法前需要将保存数据填充：

```java
Account.create()
    .setUserName("张三")
    .setAge(18)
    .setBirthday(new Date())
    .save();
```

## 删除数据

`Model` 提供了 `remove` 方法来删除数据：

- 根据主键删除

```java
Account.create()
    .setId(1L)
    .removeById();
```

- 根据条件删除

```java
Account.create()
    .where(Account::getId).eq(1L)
    .remove();
```

## 更新数据

`Model` 提供了 `update` 方法来更新数据，调用该方法前需要将更新数据填充：

- 根据主键更新

```java
Account.create()
    .setId(1L)
    .setAge(100)
    .updateById();
```

- 根据条件更新

```java
Account.create()
    .setAge(100)
    .where(Account::getId).eq(1L)
    .update();
```

## 查询数据

### 查询一条数据

`Model` 提供了 `one` 方法来查询一条数据：

```java
Account.create()
    .where(Account::getId).eq(1L)
    .one();
```

### 查询多条数据

`Model` 提供了 `list` 方法来查询多条数据：

```java
Account.create()
    .where(Account::getAge).ge(18)
    .list();
```

### 查询单列数据

`Model` 提供了 `obj` 方法来查询单列数据：

```java
Account.create()
    .select(Account::getUserName) // 只查询 user_name 列数据
    .where(Account::getAge).ge(18)
    .objList();
```

### 查询分页数据

`Model` 提供了 `page` 方法来查询分页数据：

```java
Account.create()
    .where(Account::getAge).ge(18)
    .page(Page.of(1,10));
```

## 关联查询 <Badge type="tip" text="v1.5.5" />

`Model` 提供了三种方式实现多表关联查询，例如：用户与角色的关系：

- （默认）通过 [Joins Query](./relations-query.md#方案-3-join-query) 联表方式查询数据：

```java
User.create()
    .select(USER.ALL_COLUMNS,ROLE.ALL_COLUMNS)
    .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
    .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
    .where(USER.USER_ID.eq(1))
    .one();
```

> 获取更多关于 `Joins Query` 的信息，请点击 [这里](./relations-query.md#方案-3-join-query)

- 通过 [Relations Query](./relations-query.md#方案-1relations-注解) 的方式查询数据：

```java
User.create()
    .where(USER.USER_ID.eq(1))
    .withRelations() // 使用 Relations Query 的方式进行关联查询。
    .maxDepth(3) // 设置父子关系查询中，默认的递归查询深度。
    .ignoreRelations("orderList") // 忽略查询部分 Relations 注解标记的属性。
    .extraConditionParam("id", 100) // 添加额外的 Relations 查询条件。
    .one();
```

> 获取更多关于 `Relations Query` 的信息，请点击 [这里](./relations-query.md#方案-1relations-注解)

- 通过 [Fields Query](./relations-query.md#方案-1relations-注解) 的方式查询数据：

```java
User.create()
    .where(USER.USER_ID.eq(1))
    .withFields() // 使用 Fields Query 的方式进行关联查询。
    .fieldMapping(User::getRoleList, user -> // 设置属性对应的 QueryWrapper 查询。
        QueryWrapper.create()
            .select()
            .from(ROLE)
            .where(ROLE.ROLE_ID.in(
                QueryWrapper.create()
                .select(USER_ROLE.ROLE_ID)
                .from(USER_ROLE)
                .where(USER_ROLE.USER_ID.eq(user.getUserId()))
        )))
    .one();
```

> 获取更多关于 `Fields Query` 的信息，请点击 [这里](./relations-query.md#方案-2field-query)

## 回调方法 <Badge type="tip" text="v1.5.9" />

有些情况下，我们在操作完数据库后，还需要继续使用实体类的内容，例如：获取插入数据后返回的主键。此时这些返回值为 `boolean` 的方法就不满足我们的需求了，
应该使用 `xxxOpt` 方法，在操作数据库执行成功之后返回 `Optional.of(this)`，执行失败返回 `Optional.empty()`。这样就可以进行链式的调用了。

```java
// 插入成功之后返回主键信息
Account.create()
    .setUserName("张三")
    .setAge(18)
    .setBirthday(new Date())
    .saveOpt()
    .orElseThrow(RuntimeException::new) // 保存失败抛出异常
    .getId();
```

