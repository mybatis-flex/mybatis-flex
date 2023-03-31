# 事务管理

MyBatis-Flex 提供了一个名为 `Db.tx()` 的方法<Badge type="tip" text="^1.0.6" />，用于进行事务管理，以下是示例代码：

```java
 Db.tx(new Supplier<Boolean>() {
    @Override
    public Boolean get() {

        //进行事务操作
        
        return true;
    }
});
```

若 `tx()` 方法抛出异常，或者返回 false，或者返回 null，则回滚事务。只有正常返回 true 的时候，正常进行事务提交。


## 嵌套事务

示例代码：

```java
Db.tx(() -> {

    //进行事务操作

    boolean success = Db.tx(() -> {
        //另一个事务的操作
        return true;
    });
    
    if(success)...
        
    return true;
});
```

支持无限极嵌套；

## 特征

- 1、支持嵌套事务
- 2、支持多数据源（注意：在多数据源的情况下，所有数据源的请求（Connection）会执行相同的 commit 或者 rollback，但并非原子操作。）