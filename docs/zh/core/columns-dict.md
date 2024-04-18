# 字典回写

字典回写，指的是在一个实体类中，可能会有很多业务字段，当我们发现有某个数据库字段赋值后，主动去为业务赋值。

比如，在数据库存有一个字段为 sex，类型为 int，用于保存用户的性别，0 ：女，1 ：男，2 ：未知。 同时，在 entity 实体类中，还存在一个业务属性 sexString，用于在前台显示，那么我们使用如下的解决方案。


**step 1：** 为实体类编写一个 set 监听器（`SetListener`）

```java
public class AccountOnSetListener implements SetListener {
    @Override
    public Object onSet(Object entity, String property, Object value) {
        Account account = (Account) entity;
        if (property.equals("sex") && value != null){
            switch (value){
                case 0:
                    account.setSexString('女');
                    break;
                case 1:
                    account.setSexString('男');
                    break;
                default:
                    account.setSexString('未知');
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

    private int sex;

    @Column(ignore = true) //非数据库字段，需配置忽略该属性
    private String sexString;

    //getter setter
}
```

更多的 `onSet` 还可以参考 [这里](./table.md)。
