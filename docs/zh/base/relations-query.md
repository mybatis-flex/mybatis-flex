# 关联查询

在 MyBatis-Flex 中，我们内置了 3 种方案，帮助用户进行关联查询，比如 `一对多`、`一对一`、`多对一`、`多对多`等场景，他们分别是：

- 方案1：Relations 注解
- 方案2：Field Query
- 方案3：Join Query

## 方案 1：Relations 注解

在 MyBatis-Flex 中，提供了 4 个 Relations 注解，他们分别是：

- **RelationOneToOne**：用于一对一的场景
- **RelationOneToMany**：用于一对多的场景
- **RelationManyToOne**：用于多对一的场景
- **RelationManyToMany**：用于多对多的场景

添加了以上配置的实体类，在通过 `BaseMapper` 的方法查询数据时，需要调用 select*****WithRelations**() 方法，Relations 注解才能生效。
否则 MyBatis-Flex 自动忽略 Relations 注解。

BaseMapper 提供的 withRelations 方法列表，详情点击[这里](/zh/base/query.html#relations-注解查询)。

## 一对一 `@RelationOneToOne`

假设有一个账户，账户有身份证，账户和身份证的关系是一对一的关系，代码如下所示：

Account.java :
```java 8
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    @RelationOneToOne(selfField = "id", targetField = "accountId")
    private IDCard idCard;

    //getter setter
}
```

IDCard.java :

```java
@Table(value = "tb_idcard")
public class IDCard implements Serializable {

    private Long accountId;
    private String cardNo;
    private String content;

    //getter setter
}
```

`@RelationOneToOne` 配置描述：

- **selfField** 当前实体类的属性
- **targetField** 目标对象的关系实体类的属性

> PS: 若 **selfField** 是主键，且当前表只有 1 个主键时，可以不填写。因此，以上的配置可以简化为 `@RelationOneToOne(targetField = "accountId")`

假设数据库 5 条 Account 数据，然后进行查询：

```java
List<Account> accounts = accountMapper.selectAllWithRelations();
System.out.println(accounts);
```

其执行的 SQL 如下：

```sql
SELECT `id`, `user_name`, `age` FROM `tb_account`

SELECT `account_id`, `card_no`, `content` FROM `tb_idcard`
WHERE account_id IN (1, 2, 3, 4, 5)
```

查询打印的结果如下：

```txt
 [
 Account{id=1, userName='孙悟空', age=18, idCard=IDCard{accountId=1, cardNo='0001', content='内容1'}},
 Account{id=2, userName='猪八戒', age=19, idCard=IDCard{accountId=2, cardNo='0002', content='内容2'}},
 Account{id=3, userName='沙和尚', age=19, idCard=IDCard{accountId=3, cardNo='0003', content='内容3'}},
 Account{id=4, userName='六耳猕猴', age=19, idCard=IDCard{accountId=4, cardNo='0004', content='内容4'}},
 Account{id=5, userName='王麻子叔叔', age=19, idCard=IDCard{accountId=5, cardNo='0005', content='内容5'}}
 ]
```

**注意事项 1：**

在以上的 `@RelationOneToOne` 注解中，若 `IDCard.java` 是 VO、DTO 等，而不是一个带有 `@Table` 注解的 Entity 类，
则需要在 `@RelationOneToOne` 配置上 `targetTable` 用于指定查询的表名。


例如：
```java 10
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    // 假设 IDCard 类是 vo 或者 dto，需要配置 targetTable
    @RelationOneToOne(selfField = "id", targetField = "accountId"
        , targetTable = "tb_idcard")
    private IDCard idCard;

    //getter setter
}
```

**注意事项 2：**

在 `Account.java` 和 `IDCard.java` 示例中，若他们的关联关系是通过 **中间表** 的方式进行关联，则需要添加
`joinTable`、 `joinSelfColumn`、 `joinTargetColumn` 配置，如下所示：

```java 9,10,11
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    @RelationOneToOne(
         joinTable = "tb_idcard_mapping"
        ,joinSelfColumn = "account_id"
        ,joinTargetColumn = "idcard_id"
        ,selfField = "id"
        ,targetField = "accountId")
    private IDCard idCard;

    //getter setter
}
```

其他场景：一对多（`@RelationOneToMany`）、多对一（`@RelationManyToOne`）、多对多（`@RelationManyToMany`） 也是如此。



## 一对多 `@RelationOneToMany`

假设一个账户有很多本书籍，一本书只能归属一个账户所有；账户和书籍的关系是一对多的关系，代码如下：

Account.java :
```java 8
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    @RelationOneToMany(selfField = "id", targetField = "accountId")
    private List<Book> books;

    //getter setter
}
```

Book.java :

```java
@Table(value = "tb_book")
public class Book implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long accountId;
    private String title;

    //getter setter
}
```

`@RelationOneToMany` 配置描述：

- **selfField** 当前实体类的属性
- **targetField** 目标对象的关系实体类的属性

> PS: 若 **selfField** 是主键，且当前表只有 1 个主键时，可以不填写。因此，以上的配置可以简化为 `@RelationOneToOne(targetField = "accountId")`



假设数据库 5 条 Account 数据，然后进行查询：

```java
List<Account> accounts = accountMapper.selectAllWithRelations();
System.out.println(accounts);
```

其执行的 SQL 如下：

```sql
SELECT `id`, `user_name`, `age` FROM `tb_account`

SELECT `id`, `account_id`, `title`, `content` FROM `tb_book`
WHERE account_id IN (1, 2, 3, 4, 5)
```

**Map 映射**

若 `Account.books` 是一个 `Map`，而非 `List`，那么，我们需要通过配置 `mapKeyField` 来指定使用 `Book` 的那个列来充当 `Map` 的 `Key`，
如下代码所示：

```java 9
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    @RelationOneToMany(selfField = "id", targetField = "accountId"
        , mapKeyField = "id") //使用 Book 的 id 来填充这个 map 的 key
    private Map<Long, Book> books;


    //注意 map 的 key 的类型，可以和 Book 的 id 类型不一致也是支持的
    //比如：
    //private Map<String, Book> books;

    //getter setter
}
```

> 多对多注解 `@RelationManyToMany` 也是如此。

**selfValueSplitBy 分割查询** <Badge type="tip" text="^ v1.6.8" />

若 `selfField` 的值是一个 `由字符拼接而成的列表（如: "1,2,3" )`，那么，我们可以通过配置 `selfValueSplitBy` 来指定使用 `selfField` 的值根据字符切割后查询，
如下代码所示：

```java 8
@Table(value = "tb_patient")
public class PatientVO1 implements Serializable {
    private static final long serialVersionUID = -2298625009592638988L;

    /**
     * ID
     */
    @Id
    private Integer patientId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 所患病症(对应字符串类型) 英文逗号 分割
     */
    private String diseaseIds;

    /**
     * 患者标签(对应数字类型) / 分割
     */
    private String tagIds;

    @RelationOneToMany(
        selfField = "diseaseIds",
        selfValueSplitBy = ",", //使用 "," 对 diseaseIds 的值进行分割
        targetTable = "tb_disease", //只获取某个字段值需要填入目标表名
        targetField = "diseaseId", //测试目标字段是字符串类型是否正常转换
        valueField = "name" //测试只获取某个字段值是否正常
    )
    private List<String> diseaseNameList;

    @RelationOneToMany(
        selfField = "tagIds",
        selfValueSplitBy = "/", //使用 "/" 对 tagIds 的值进行分割
        targetField = "tagId" //测试目标字段是数字类型是否正常转换
    )
    private List<Tag> tagList;

    @RelationOneToMany(
        selfField = "diseaseIds",
        selfValueSplitBy = ",", //使用 "," 对 diseaseIds 的值进行分割
        targetField = "diseaseId", //测试目标字段是字符串类型是否正常转换
        mapKeyField = "diseaseId" //测试Map映射
    )
    private Map<String, Disease> diseaseMap;

    //getter setter toString
}
```

进行查询
```java
QueryWrapper qw = QueryWrapper.create().orderBy(PatientVO1::getPatientId, false).limit(1)
PatientVO1 patientVO1 = patientMapper.selectOneWithRelationsByQueryAs(qw, PatientVO1.class);
System.out.println(JSON.toJSONString(patientVO1));
```

其执行的 SQL 如下：

```sql
SELECT `patient_id`, `name`, `disease_ids`, `tag_ids` FROM `tb_patient` ORDER BY `patient_id` DESC LIMIT 1;

SELECT disease_id, name FROM `tb_disease` WHERE `disease_id` IN ('1', '2', '3', '4');
SELECT `tag_id`, `name` FROM `tb_tag` WHERE `tag_id` IN (1, 2, 3);
SELECT `disease_id`, `name` FROM `tb_disease` WHERE `disease_id` IN ('1', '2', '3', '4');
```

查询结果：
```json
{
  "patientId": 4,
  "name": "赵六",
  "diseaseIds": "1,2,3,4",
  "tagIds": "1/2/3",
  "diseaseNameList": [
    "心脑血管疾病",
    "消化系统疾病",
    "神经系统疾病",
    "免疫系统疾病"
  ],
  "tagList": [
    {
      "name": "VIP",
      "tagId": 1
    },
    {
      "name": "JAVA开发",
      "tagId": 2
    },
    {
      "name": "Web开发",
      "tagId": 3
    }
  ],
  "diseaseMap": {
    "1": {
      "diseaseId": "1",
      "name": "心脑血管疾病"
    },
    "2": {
      "diseaseId": "2",
      "name": "消化系统疾病"
    },
    "3": {
      "diseaseId": "3",
      "name": "神经系统疾病"
    },
    "4": {
      "diseaseId": "4",
      "name": "免疫系统疾病"
    }
  }
}
```

## 多对一 `@RelationManyToOne`

假设一个账户有很多本书籍，一本书只能归属一个账户所有；账户和书籍的关系是一对多的关系，书籍和账户的关系为多对一的关系，代码如下：

Account.java:
```java 8
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    //getter setter
}
```

Book.java 多对一的配置:

```java 9
@Table(value = "tb_book")
public class Book implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private Long accountId;
    private String title;

    @RelationManyToOne(selfField = "accountId", targetField = "id")
    private Account account;

    //getter setter
}
```

`@RelationManyToOne` 配置描述：

- **selfField** 当前实体类的属性
- **targetField** 目标对象的关系实体类的属性

> PS: 若 **targetField** 目标对象的是主键，且目标对象的表只有 1 个主键时，可以不填写。因此，以上的配置可以简化为
> `@RelationManyToOne(selfField = "accountId")`



## 多对多 `@RelationManyToMany`

假设一个账户可以有多个角色，一个角色也可以有多个账户，他们是多对多的关系，需要通过中间件表 `tb_role_mapping` 来维护：

`tb_role_mapping` 的表结构如下：

```sql
CREATE TABLE  `tb_role_mapping`
(
    `account_id`  INTEGER ,
    `role_id`  INTEGER
);
```

Account.java 多对多的配置:
```java {7-11}
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;

    @RelationManyToMany(
            joinTable = "tb_role_mapping", // 中间表
            selfField = "id", joinSelfColumn = "account_id",
            targetField = "id", joinTargetColumn = "role_id"
    )
    private List<Role> roles;

    //getter setter
}
```

Role.java 多对多的配置:

```java {7-11}
@Table(value = "tb_role")
public class Role implements Serializable {

    private Long id;
    private String name;

    //getter setter
}
```

`@RelationManyToMany` 配置描述：

- selfField 当前实体类的属性
- targetField 目标对象的关系实体类的属性
- joinTable 中间表
- joinSelfColumn 当前表和中间表的关系字段
- joinTargetColumn 目标表和中间表的关系字段

> 注意：selfField 和 targetField 配置的是类的属性名，joinSelfColumn 和 joinTargetColumn 配置的是中间表的字段名。
>
> 若 **selfField** 和 **targetField** 分别是两张关系表的主键，且表只有 1 个主键时，可以不填写。因此，以上配置可以简化如下：
```java {7-11}
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userName;

    @RelationManyToMany(
            joinTable = "tb_role_mapping", // 中间表
            joinSelfColumn = "account_id",
            joinTargetColumn = "role_id"
    )
    private List<Role> roles;

    //getter setter
}
```

## 只查询一个字段值 <Badge type="tip" text="v1.6.6" />

`RelationOneToOne`、`RelationOneToMany`、`RelationManyToOne`、`RelationManyToMany`新增属性`valueField`
```java 7
/**
 * 目标对象的关系实体类的属性绑定
 * <p>
 * 当字段不为空串时,只进行某个字段赋值(使用对应字段类型接收)
 * @return 属性名称
 */
String valueField() default "";
```
> 注解其他属性配置使用不变，当配置了`valueField`值时，只提取目标对象关系实体类的该属性
>
> **使用场景**：例如，操作日志中有个 `createBy` (操作人)字段，此时在日志信息中需要显示操作人名称，且只需要这一个字段，此时使用实体接收会导致不必要的字段出现，接口文档也会变得混乱。



假设一个账户实体类 `UserVO5.java`
- 每个账户有一个唯一对应的`id_number`列在表`tb_id_card`中
- 一个账户可以有多个角色，一个角色也可以分配给多个账户，他们通过中间表`tb_user_role`进行关系映射

```java {12,21,29}
@Table("tb_user")
public class UserVO5 {
    @Id
    private Integer userId;
    private String userName;
    private String password;

    @RelationOneToOne(
            selfField = "userId",
            targetTable = "tb_id_card",
            targetField = "id",
            valueField = "idNumber"
    )
    //该处可以定义其他属性名，不一定要是目标对象的字段名
    private String idNumberCustomFieldName;


    @RelationManyToMany(
            selfField = "userId",
            targetTable = "tb_role",
            targetField = "roleId",
            valueField = "roleName",
            joinTable = "tb_user_role",
            joinSelfColumn = "user_id",
            joinTargetColumn = "role_id"
    )
    private List<String> roleNameList;

    //getter setter toString
}
```
进行查询
```java
List<UserVO5> userVO5List = userMapper.selectListWithRelationsByQueryAs(QueryWrapper.create(), UserVO5.class);
System.out.println(JSON.toJSONString(userVO5List));
```
输出结果
```json {6,7,13,14,20,21}
[
    {
        userId = 1,
        userName = '张三',
        password = '12345678',
        idNumberCustomFieldName = 'F281C807-C40B-472D-82F5-6130199C6328',
        roleNameList = [普通用户]
    },
    {
        userId = 2,
        userName = '李四',
        password = '87654321',
        idNumberCustomFieldName = '6176E9AD-36EF-4201-A5F7-CCE89B254952',
        roleNameList = [普通用户, 贵族用户]
    },
    {
        userId = 3,
        userName = '王五',
        password = '09897654',
        idNumberCustomFieldName = 'A038E6EA-1FDE-4191-AA41-06F78E91F6C2',
        roleNameList = [普通用户, 贵族用户, 超级贵族用户]
    }
]
```

## 父子关系查询

比如在一些系统中，比如菜单会有一些父子关系，例如菜单表如下：

```sql
CREATE TABLE `tb_menu`
(
    `id`        INTEGER auto_increment,
    `parent_id`        INTEGER,
    `name`      VARCHAR(100)
);
```

Menu.java 定义如下：

```java
@Table(value = "tb_menu")
public class Menu implements Serializable {

    private Long id;

    private Long parentId;

    private String name;

    @RelationManyToOne(selfField = "parentId", targetField = "id")
    private Menu parent;

    @RelationOneToMany(selfField = "id", targetField = "parentId")
    private List<Menu> children;

    //getter setter
}
```

查询顶级菜单：

```java
QueryWrapper qw = QueryWrapper.create();
qw.where(MENU.PARENT_ID.eq(0));

List<Menu> menus = menuMapper.selectListWithRelationsByQuery(qw);
System.out.println(JSON.toJSONString(menus));
```

SQL 执行如下：

```sql
SELECT `id`, `parent_id`, `name` FROM `tb_menu` WHERE `parent_id` = 0
SELECT `id`, `parent_id`, `name` FROM `tb_menu` WHERE id = 0
SELECT `id`, `parent_id`, `name` FROM `tb_menu` WHERE parent_id IN (1, 2, 3)
```

JSON 输出内容如下：

```json
[
  {
    "children": [
      {
        "id": 4,
        "name": "子菜单",
        "parentId": 1
      },
      {
        "id": 5,
        "name": "子菜单",
        "parentId": 1
      }
    ],
    "id": 1,
    "name": "顶级菜单1",
    "parentId": 0
  },
  {
    "children": [],
    "id": 2,
    "name": "顶级菜单2",
    "parentId": 0
  },
  {
    "children": [
      {
        "id": 6,
        "name": "子菜单",
        "parentId": 3
      },
      {
        "id": 7,
        "name": "子菜单",
        "parentId": 3
      },
      {
        "id": 8,
        "name": "子菜单",
        "parentId": 3
      }
    ],
    "id": 3,
    "name": "顶级菜单3",
    "parentId": 0
  }
]
```

在以上的父子关系查询中，默认的递归查询深度为 3 个层级，若需要查询指定递归深度，需要添加如下配置：

```java
QueryWrapper qw = QueryWrapper.create();
qw.where(MENU.PARENT_ID.eq(0));

//设置递归查询深度为 10 层
RelationManager.setMaxDepth(10);
List<Menu> menus = menuMapper.selectListWithRelationsByQuery(qw);
```

>`RelationManager.setMaxDepth(10)` 的配置，只在当前第一次查询有效，查询后会清除设置。

## 忽略部分 Relation 注解

在很多场景中，一个类里可能会有多个 `@RelationXXX` 注解配置的属性，例如：

```java
@Table(value = "tb_account")
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    @RelationOneToOne(targetField = "accountId")
    private IDCard idCard;


    @RelationOneToMany(targetField = "accountId")
    private List<Book> books;


    @RelationManyToMany(
            joinTable = "tb_role_mapping",
            joinSelfColumn = "account_id",
            joinTargetColumn = "role_id"
    )
    private List<Role> roles;

    //getter setter
}
```

默认情况下，我们通过 `BaseMapper` 的 withRelation 方法查询时，会查询 Account 所有带有 `@RelationXXX` 注解的属性：
`idCard`  `books` `roles`。但是可能在我们的个别业务中，不需要那么多的关联数据，比如假设我们只需要查询 `roles`，而忽略掉
`idCard`  `books`，此时，代码如下：

```java
RelationManager.addIgnoreRelations("idCard","books");
List<Account> accounts = accountMapper.selectAllWithRelations();
```

>`addIgnoreRelations()` 方法的配置，只在当前第一次查询有效，查询后会清除设置。另外需要注意的是：
> `addIgnoreRelations()` 的设置，是会影响其所有嵌套的 Relations 配置的。在嵌套的场景中，如果存在同名的属性，
> 比如 `class A ` 和 `class B` 都有相同的属性 `x`，假设我们想忽略 `class A` 中的 `x` 而 `class B` 的 `x` 不忽略，
> 那么我们需要添加上类名的前缀，例如：`addIgnoreRelations("A.x")`。


## 只查询部分 Relation 注解

和【忽略部分注解】相反，如下代码中配置了多个 `@Relation***` 修饰的字段：

```java
@Table(value = "tb_account")
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    @RelationOneToOne(targetField = "accountId")
    private IDCard idCard;


    @RelationOneToMany(targetField = "accountId")
    private List<Book> books;


    @RelationManyToMany(
            joinTable = "tb_role_mapping",
            joinSelfColumn = "account_id",
            joinTargetColumn = "role_id"
    )
    private List<Role> roles;

    //getter setter
}
```

假设我们只想查询 `books` 和 `roles` 字段，而忽略其他所有  `@Relation***` 修饰的字段，可以通过如下的配置：

```java
RelationManager.addQueryRelations("books","roles");
List<Account> accounts = accountMapper.selectAllWithRelations();
```

这个有一个好处是：以后 Account 代码无论如何变动，比如添加了新的 `@Relation***` 修饰的字段，那么都不会影响到原来的业务。

**注意：**

> `RelationManager` 的 `addIgnoreRelations` （忽略）配置优先于 `addQueryRelations`（查询），假设 `addIgnoreRelations` 和 `addQueryRelations`
> 都配置了相同的字段，那么这个字段将会被忽略。


## 配置额外的附加条件

在一对多（`@RelationOneToMany`）、多对多（`@RelationManyToMany`） 的场景中，除了通过其关联字段查询结果以外，可能还会要求添加一些额外的条件。
此时，我们可以通过添加 `extraCondition` 配置来满足这种场景，例如：

```java 13
@Table(value = "tb_account")
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    @RelationManyToMany(
            joinTable = "tb_role_mapping",
            joinSelfColumn = "account_id",
            joinTargetColumn = "role_id",
            extraCondition = "(name like '%2%' or id > 1)"
    )
    private List<Role> roles;

    //getter setter
}
```
以上配置查询的 SQL 如下：

```sql
SELECT `id`, `name` FROM `tb_role`
WHERE id IN (1, 2, 3) AND (name like '%2%' or id > 1)
```

**动态参数：**

若 `extraCondition` 配置的条件里，需要通过外面传入参数，可以配置如下：

```java 13
@Table(value = "tb_account")
public class Account implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;

    @RelationManyToMany(
            joinTable = "tb_role_mapping",
            joinSelfColumn = "account_id",
            joinTargetColumn = "role_id",
            extraCondition = "(name like :name or id > :id)"
    )
    private List<Role> roles;

    //getter setter
}
```
以上的 `:name` 和  `:id` 相当于占位符，用于接受外部参数，那么外部参数如何传入呢？代码如下：

```java
RelationManager.addExtraConditionParam("name","%myName%");
RelationManager.addExtraConditionParam("id",100);

List<Account> accounts = accountMapper.selectAllWithRelations();
System.out.println(JSON.toJSONString(accounts));
```

以上配置查询的 SQL 如下：

```sql
SELECT `id`, `name` FROM `tb_role`
WHERE id IN (1, 2, 3) AND (name like '%myName%' or id > 100)
```


## 方案 2：Field Query

以下是文章的 `多对多` 示例，一篇文章可能归属于多个分类，一个分类可能有多篇文章，需要用到中间表 `article_category_mapping`。

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

```java {9-13}
QueryWrapper queryWrapper = QueryWrapper.create()
        .select().from(ARTICLE)
        .where(ARTICLE.id.ge(100));

List<Article> articles = mapper.selectListByQuery(queryWrapper
    , fieldQueryBuilder -> fieldQueryBuilder
        .field(Article::getCategories) // 或者 .field("categories")
        .queryWrapper(article -> QueryWrapper.create()
            .select().from(CATEGORY)
            .where(CATEGORY.id.in(
                    select("category_id").from("article_category_mapping")
                    .where("article_id = ?", article.getId())
            )
        )
    );
```

通过以上代码可以看出，`Article.categories` 字段的结果，来源于  `queryWrapper()` 方法构建的 `QueryWrapper`。

其原理是：MyBatis-Flex 的内部逻辑是先查询出 `Article` 的数据，然后再根据 `Article` 构建出新的 SQL，查询分类，并赋值给 `Article.categories` 属性，
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

## Field Query 知识点

相对 `Relations 注解` ，Field Query 的学习成本是非常低的，在构建子查询时，只需要明白为哪个字段、通过什么样的 SQL 查询就可以了，以下是示例：

```java 3,4
List<Article> articles = mapper.selectListByQuery(query
    , fieldQueryBuilder -> fieldQueryBuilder
        .field(...)        // 为哪个字段查询的？
        .queryWrapper(...) // 通过什么样的 SQL 查询的？
    );
```

因此，在 MyBatis-Flex 的设计中，无论是一对多、多对一、多对多... 还是其他任何一种场景，其逻辑都是一样的。


## Field Query 更多场景

通过以上内容看出，`Article` 的任何属性，都是可以通过传入 `FieldQueryBuilder` 来构建 `QueryWrapper` 进行再次查询，
这些不仅仅只适用于  `一对多`、`一对一`、`多对一`、`多对多`等场景。任何 `Article` 对象里的属性，需要二次查询赋值的，都是可以通过这种方式进行，比如一些统计的场景。


## 方案 3：Join Query

Join Query 是通过 QueryWrapper 构建 `Left Join` 等方式进行查询，其原理是 MyBatis-Flex 自动构建了 MyBatis 的 `<resultMap>`
，我们只需要关注 MyBatis-Flex 的 SQL 构建即可。

## Join Query 代码示例

这里以 **用户** 和 **角色** 的 `多对多` 关系作为例子，用户表和角色表，分别对应着用户类和角色类：

```java
@Table("sys_user")
public class User {
    @Id
    private Integer userId;
    private String userName;
}


@Table("sys_role")
public class Role {
    @Id
    private Integer roleId;
    private String roleKey;
    private String roleName;
}
```

现在需要查询所有用户，以及每个用户对应的角色信息，并通过 UserVO 对象返回：

```java
public class UserVO {
    private String userId;
    private String userName;
    private List<Role> roleList;
}
```

这个操作只需要联表查询即可完成，对于联表查询的结果映射，MyBatis-Flex 会自动帮您完成：

```java
QueryWrapper queryWrapper = QueryWrapper.create()
        .select(USER.USER_ID, USER.USER_NAME, ROLE.ALL_COLUMNS)
        .from(USER.as("u"))
        .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
        .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID));
List<UserVO> userVOS = userMapper.selectListByQueryAs(queryWrapper, UserVO.class);
userVOS.forEach(System.err::println);
```

构建的联表查询 SQL 语句为：

```sql
SELECT `u`.`user_id`,
       `u`.`user_name`,
       `r`.*
FROM `sys_user` AS `u`
LEFT JOIN `sys_user_role` AS `ur` ON `ur`.`user_id` = `u`.`user_id`
LEFT JOIN `sys_role` AS `r` ON `ur`.`role_id` = `r`.`role_id`;
```

最终自动映射的结果为：

```txt
UserVO{userId='1', userName='admin', roleList=[Role{roleId=1, roleKey='admin', roleName='超级管理员'}]}
UserVO{userId='2', userName='ry', roleList=[Role{roleId=2, roleKey='common', roleName='普通角色'}]}
UserVO{userId='3', userName='test', roleList=[Role{roleId=1, roleKey='admin', roleName='超级管理员'}, Role{roleId=2, roleKey='common', roleName='普通角色'}]}
```
