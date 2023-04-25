# Mybatis-Flex 乐观锁

## 使用场景

用于当有多个用户（或者多场景）去同时修改同一条数据的时候，只允许有一个修改成功。

## 实现原理

使用一个字段，用于记录数据的版本，当修改数据的时候，会去检测当前版本是否是正在修改的版本，同时修改成功后会把
版本号 + 1。

更新数据时，执行的 SQL 如下：

```sql
 UPDATE account SET nickname = ?, version = version + 1 
 WHERE id = ? AND version = ?
```

在以上的 SQL 中，若两个场景同时读取的是同一个 version 的内容，那么必然只最优先执行的 SQL 执行成功。


以下是 示例代码：

```java

@Table("tb_account")
public class Account {

    @Column(version = true)
    private Long version;
    
    //Getter Setter...
}
```
需要注意的是：

- 1、在同一张表中，只能有一个被 `@Column(version = true)` 修饰的字段。
- 2、Account 在插入数据时，若 version 未设置值，那么会自动被 Mybatis-Flex 设置为 0。