# 字段权限

字段权限，指的是在一张表中设计了许多字段，但是不同的用户（或者角色）查询，返回的字段结果是不一致的。
比如：tb_account 表中，有 user_name 和 password 字段，但是 password 字段只允许用户本人查询，
或者超级管理员查询，这种场景下，我们会用到 字段权限 的功能。

在 `@Table()` 注解中，有一个配置名为 `onSet`，用于设置这张表的 `设置` 监听，这里的 `设置` 监听指的是：
当我们使用 sql 、调用某个方法去查询数据，得到的数据内容映射到 entity 实体，mybatis 通过 setter 方法去设置 entity 的值时的监听。


以下是示例：

**step 1：** 为实体类编写一个 set 监听器（`SetListener`）

```java
public class AccountOnSetListener implements SetListener {
    @Override
    public Object onSet(Object entity, String property, Object value) {
        if (property.equals("password")){

            //去查询当前用户的权限
            boolean hasPasswordPermission = getPermission();
            
            //若没有权限，则把数据库查询到的 password 内容修改为 null
            if (!hasPasswordPermission){
                value = null;
            }
        }
        return value;
    }
}
```

**step 2：** 为实体类配置 `onSet` 监听

```java 1
@Table(value = "tb_account", onSet = AccountOnSetListener.class)
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;
    
    private String password;
    
    //getter setter
}
```

更多的 `onSet` 还可以参考 [这里](./table.md)。