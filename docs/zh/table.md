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
     * 默认使用哪个数据源，若系统找不到该指定的数据源时，默认使用第一个数据源
     */
    String dataSource() default "";

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

    /**
     * 监听 entity 的查询数据的 set 行为，用户主动 set 不会触发
     */
    Class<? extends SetListener> onSet() default NoneListener.class;
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


## onSet

onSet 可以用于配置：查询数据 entity （或者 entity 列表、分页等）时，对 entity 的属性设置的监听，这种场景
可以用于一些数据转换，业务字段赋值、字段权限等场景。

示例代码如下：

```java 2
//配置  onSet = MySetListener.class
@Table(value = "tb_account", onSet = MySetListener.class)
public class Account {

}
```

```java
public class MySetListener implements SetListener {

    @Override
    public Object onSet(Object entity, String property, Object value){
        //场景1：用于检测当前账户是否拥有该字段权限，
        //      有正常返回 value，没有权限返回 null
        
        
        //场景2：entity 中可能定义某个业务值
        //      当监听到某个字段被赋值了，这
        //      里可以主动去给另外的其他字段赋值
        
        
        //场景3：内容转换和二次加工，对 value 值进行修改后返回
        
        return value;
    }
}
```

> 注意：若 entity 的属性配置了 `typeHandler`，`typeHandler` 的执行顺序高于 `SetListener`。