# 事务管理

MyBatis-Flex 提供了一个名为 `Db.tx()` 的方法<Badge type="tip" text="^1.0.6" />，用于进行事务管理，若使用 Spring 框架的场景下，也可使用 `@Transactional` 注解进行事务管理。

`Db.tx()` 方法定义如下：

```java
boolean tx(Supplier<Boolean> supplier);
boolean tx(Supplier<Boolean> supplier, Propagation propagation);
```
- **supplier**：要执行的内容（代码）
- **propagation**：事务传播属性

事务传播属性 `propagation` 是一个枚举类，其枚举内容如下：

```java
//若存在当前事务，则加入当前事务，若不存在当前事务，则创建新的事务
REQUIRED(0),

//若存在当前事务，则加入当前事务，若不存在当前事务，则已非事务的方式运行
SUPPORTS(1),

//若存在当前事务，则加入当前事务，若不存在当前事务，则抛出异常
MANDATORY(2),

//始终以新事务的方式运行，若存在当前事务，则暂停（挂起）当前事务。
REQUIRES_NEW(3),

//以非事务的方式运行，若存在当前事务，则暂停（挂起）当前事务。
NOT_SUPPORTED(4),

//以非事务的方式运行，若存在当前事务，则抛出异常。
NEVER(5),

//暂时不支持
NESTED(6),
```

`Db.tx()` 代码示例：

```java
Db.tx(() -> {

    //进行事务操作

    return true;
});
```

若 `tx()` 方法抛出异常，或者返回 false，或者返回 null，则回滚事务。只有正常返回 true 的时候，进行事务提交。


## 嵌套事务

示例代码：

```java
Db.tx(() -> {

    //进行事务操作

    boolean success = Db.tx(() -> {
        //另一个事务的操作
        return true;
    });
    
        
    return true;
});
```

支持无限极嵌套，默认情况下，嵌套事务直接的关系是：`REQUIRED`（若存在当前事务，则加入当前事务，若不存在当前事务，则创建新的事务）。

## @Transactional 

MyBatis-Flex 已支持 Spring 框架的 `@Transactional`，在使用 SpringBoot 的情况下，可以直接使用 `@Transactional` 进行事务管理。 
同理，使用 Spring 的 `TransactionTemplate` 进行事务管理也是没问题的。

> 注意：若项目未使用 SpringBoot，只用到了 Spring，需要参考 MyBatis-Flex 的 [FlexTransactionAutoConfiguration](https://gitee.com/mybatis-flex/mybatis-flex/blob/main/mybatis-flex-spring-boot-starter/src/main/java/com/mybatisflex/spring/boot/FlexTransactionAutoConfiguration.java)
> 进行事务配置，才能正常使用 `@Transactional` 注解。

## 特征

- 1、支持嵌套事务
- 2、支持多数据源（注意：在多数据源的情况下，所有数据源的数据库请求（Connection）会执行相同的 commit 或者 rollback，但并非原子操作。）