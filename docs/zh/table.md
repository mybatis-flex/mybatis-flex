# @Table 注解的使用

在 Mybatis-Flex 中，`@Table` 主要是用于给 Entity 实体类添加标识，用于描述 实体类 和 数据库表 的关系，以及对实体类进行的一些
功能辅助。


`@Table` 的定义如下：

```java
public @interface Table {

    /**
     * 显式指定表名称
     */
    String value();

    /**
     * 数据库的 schema
     */
    String schema() default "";

    /**
     * 默认为 驼峰属性 转换为 下划线字段
     */
    boolean camelToUnderline() default true;

    /**
     * 监听 entity 的 insert 行为
     */
    Class<? extends InsertListener> onInsert() default NoneListener.class;

    /**
     * 监听 entity 的 update 行为
     */
    Class<? extends UpdateListener> onUpdate() default NoneListener.class;
}
```

其使用方式如下：

```java 1
@Table(value = "tb_account", onUpdate = MyUpdateListener.class)
public class Account {

}
```

## value

用于配置指定 实体类 与 表名 的映射关系。

## camelToUnderline

默认值为 ture，用于指定当前 实体类 的字段 与 表的列是否是 **驼峰转下划线** 的关系，比如：实体类中定义的 userName 属性，对应的表字段为 user_name。

若 camelToUnderline 配置为 false，那么，实体类中定义的 userName 属性，对应的表字段为 userName（除非使用 `@Column` 注解另行指定）。

## onInsert

用于监听 Entity 实体类数据被新增到数据库，我们可以在实体类被新增时做一些前置操作。比如：

- 数据填充。
- 数据修改。

示例代码如下：

```java 2
//配置  onInsert = MyInsertListener.class
@Table(value = "tb_account", onInsert = MyInsertListener.class)
public class Account {

}
```

```java
public class MyInsertListener implements InsertListener {

    @Override
    public void onInsert(Object entity) {
        Account account = (Account)entity;
        
        //设置 account 被新增时的一些默认数据
        account.setInsertTime(new Date());
        account.setInsertUserId("...");
        
        //多租户的场景下，设置当前 租户 ID ..
        account.setTenantId("....");
    }
}
```

> 需要注意的是：onInsert 监听中，通过 mybatis 的 xml mapper 插入数据，或者通过 Db + Row 中插入数据，并不会触发 onInsert 行为，只有通过
> AccountMapper 进行插入数据才会触发。

## onUpdate

使用方式同 onInsert 一致，用于在数据被更新的时候，设置一些默认数据。