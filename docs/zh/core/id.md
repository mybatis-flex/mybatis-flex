# @Id 主键的使用

在 Entity 类中，MyBatis-Flex 是使用 `@Id` 注解来标识主键的，如下代码所示：

```java 5
@Table("tb_account")
public class Account {

    // id 为自增主键
    @Id(keyType = KeyType.Auto)
    private Long id;

    //getter setter
}
```

`@Id` 注解的内容如下：

```java
public @interface Id {

    /**
     * ID 生成策略，默认为 none
     *
     * @return 生成策略
     */
    KeyType keyType() default KeyType.None;

    /**
     * 若 keyType 类型是 sequence， value 则代表的是
     * sequence 序列的 sql 内容
     * 例如：select SEQ_USER_ID.nextval as id from dual
     *
     * 若 keyType 是 Generator，value 则代表的是使用的那个 keyGenerator 的名称
     *
     */
    String value() default "";


    /**
     * sequence 序列执行顺序
     * 是在 entity 数据插入之前执行，还是之后执行，之后执行的一般是数据主动生成的 id
     *
     * @return 执行之前还是之后
     */
    boolean before() default true;
}
```

keyType 为主键的生成方式，KeyType 有 4 种类型：

```java
public enum KeyType {

    /**
     * 自增的方式
     */
    Auto,

    /**
     * 通过执行数据库 sql 生成
     * 例如：select SEQ_USER_ID.nextval as id from dual
     */
    Sequence,

    /**
     * 通过 IKeyGenerator 生成器生成
     */
    Generator,

    /**
     * 其他方式，比如在代码层用户手动设置
     */
    None,
}
```

## 多主键、复合主键

MyBatis-Flex 多主键就是在 Entity 类里有多个 `@Id` 注解标识而已，比如：

```java
@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String otherId;

    //getter setter
}
```
当我们保存数据的时候，Account 的 id 主键为自增，而 otherId 主键则通过 uuid 生成。

## 内置主键生成器

MyBatis-Flex 内置了三种主键生成器，他们的名称都定义在 `KeyGenerators` 类里：

- **uuid**：通过 `UUIDKeyGenerator` 生成 UUID 作为数据库主键。
- **flexId**：独创的 FlexID 算法生成数据库主键（了解更多信息请参阅[源码](https://gitee.com/mybatis-flex/mybatis-flex/blob/main/mybatis-flex-core/src/main/java/com/mybatisflex/core/keygen/impl/FlexIDKeyGenerator.java))。
- **snowFlakeId**：通过雪花算法（`SnowFlakeIDKeyGenerator`）生成数据库主键。

这些主键生成器为 MyBatis-Flex 内置的，可直接使用：

```java 4
@Table("tb_account")
public class Account {

    @Id(keyType=KeyType.Generator, value=KeyGenerators.flexId)
    private Long id;
    
    //getter setter
}
```

## 自定义主键生成器

第 1 步：编写一个类，实现 `IKeyGenerator` 接口，例如：

```java
public class UUIDKeyGenerator implements IKeyGenerator {

    @Override
    public Object generate(Object entity, String keyColumn) {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
```

第 2 步：注册 UUIDKeyGenerator

```java
KeyGeneratorFactory.register("myUUID", new UUIDKeyGenerator());
```

第 3 步：在 Entity 里使用 "myUUID" 生成器：

```java
@Table("tb_account")
public class Account {
    
    @Id(keyType=KeyType.Generator, value="myUUID")
    private String otherId;

    //getter setter
}
```


## 使用序列 Sequence 生成

```java
@Table("tb_account")
public class Account {

    @Id(keyType=KeyType.Sequence, value="select SEQ_USER_ID.nextval as id from dual")
    private Long id;
    
}
```

## 全局配置

一般的项目中，通常是许多的 Entity 使用同一个数据库，同时使用一种主键生成方式，比如都使用 自增，
或者都使用通过序列（Sequence）生成，此时，我们是没有必要为每个 Entity 单独配置一样内容的。

MyBatis-Flex 提供了一种全局配置的方式，代码如下：

```java
FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
keyConfig.setKeyType(KeyType.Sequence);
keyConfig.setValue("select SEQ_USER_ID.nextval as id from dual")
keyConfig.setBefore(true);

FlexGlobalConfig.getDefaultConfig().setKeyConfig(keyConfig);
```

此时，Entity 类 Account.java 只需要如下配置即可。

```java
@Table("tb_account")
public class Account {

    @Id
    private Long id;
    
}
```
