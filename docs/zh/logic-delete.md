# 逻辑删除

## 逻辑删除简介

逻辑删除指的是在删除数据的时候，并非真正的去删除，而是将表中列所对应的状态字段（status）做修改操作，
实际上并未删除目标数据。

我们可以在做表的字段设计的时候，用一个列表标识该数据的 "删除状态"，在 mybatis-flex 中，正常状态的值为 0， 已删除
的值为 1。

## Mybatis-Flex 逻辑删除示例

假设在 tb_account 表中，存在一个为 is_deleted 的字段，用来标识该数据的逻辑删除，那么 tb_account 表
对应的 "Account.java" 实体类应该配置如下：

```java

@Table("tb_account")
public class Account {

    @Column(isLogicDelete = true)
    private Boolean isDelete;
    
    //Getter Setter...
}
```

此时，当我们执行如下的删除代码是：

```java
accountMapper.deleteById(1);
```
Mybatis 执行的 SQL 如下：

```sql
UPDATE `tb_account` SET `is_delete` = 1 
WHERE `id` = ? AND `is_delete` = 0
```
可以看出，当执行 deleteById 时，Mybatis 只是进行了 update 操作，而非 delete 操作。

## 注意事项

当 "tb_account" 的数据被删除时（ is_delete = 1 时），我们通过 Mybatis-Flex 的 selectOneById 去查找数据时，会查询不到数据。
原因是 `selectOneById` 会自动添加上 `is_delete = 0` 条件，执行的 sql 如下：

```java
SELECT * FROM tb_account where id = ? and is_delete = 0
```

不仅仅是 selectOneById 方法会添加 `is_delete = 0` 条件，BaseMapper 的以下方法也都会添加该条件：

- selectOneBy**
- selectListBy**
- selectCountBy**
- paginate