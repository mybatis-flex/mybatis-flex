# 字段加密

字段加密，指的是数据库在存入了明文内容，但是当我们进行查询时，返回的内容为加密内容，而非明文内容。

以下是 MyBatis-Flex 字段加密示例：

**step 1：** 为实体类编写一个 set 监听器（`SetListener`）

```java
public class AccountOnSetListener implements SetListener {
    @Override
    public Object onSet(Object entity, String property, Object value) {
        
        if (value != null){
            //对字段内容进行加密
            value = encrypt(value);
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