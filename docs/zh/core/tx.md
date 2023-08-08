# 事务管理

MyBatis-Flex 提供了一个名为 `Db.tx()` 的方法<Badge type="tip" text="^1.0.6" />，用于进行事务管理，若使用 Spring 框架的场景下，也可使用 `@Transactional` 注解进行事务管理。

`Db.tx()` 方法定义如下：

```java
boolean tx(Supplier<Boolean> supplier);
boolean tx(Supplier<Boolean> supplier, Propagation propagation);

<T> T txWithResult(Supplier<T> supplier);
<T> T txWithResult(Supplier<T> supplier, Propagation propagation);
```
方法：
- tx：返回结果为 Boolean，返回 `null` 或者 `false` 或者 抛出异常，事务回滚
- txWithResult：返回结果由 `Supplier` 参数决定，只有抛出异常时，事务回滚

参数：
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
- 2、支持多数据源

> 注意：在多数据源的情况下，所有数据源的数据库请求（Connection）会执行相同的 commit 或者 rollback，但并非原子操作。例如：

```java
@Transactional
public void doSomething(){

    try{
        DataSourceKey.use("ds1");
        Db.updateBySql("update ....");
    }finally{
        DataSourceKey.clear()
    }

    try{
        DataSourceKey.use("ds2");
        Db.updateBySql("update ...");
    }finally{
        DataSourceKey.clear()
    }

    //抛出异常
    int x = 1/0;
}
```

在以上的例子中，两次 `Db.update(...)` 虽然是两个不同的数据源，但它们都在同一个事务 `@Transactional` 里，因此，当抛出异常的时候，
它们都会进行回滚（rollback）。

以上提到的 `并非原子操作`，指的是：

>假设在回滚的时候，恰好其中一个数据库出现了异常（比如 网络问题，数据库崩溃），此时，可能只有一个数据库的数据正常回滚（rollback）。
> 但无论如何，MyBatis-Flex 都会保证在同一个 `@Transactional` 中的多个数据源，保持相同的 commit 或者 rollback 行为。

## Seata 分布式事务

Seata 是一款开源的分布式事务解决方案，致力于在微服务架构下提供高性能和简单易用的分布式事务服务。
官方网站：https://seata.io/zh-cn/index.html

1. 首先，先了解事务的基础(自行百度)，
2. 在了解seata事务的项目 官网地址：https://seata.io/zh-cn/docs
3. 然后根据官方的[快速开始](https://seata.io/zh-cn/docs/user/quickstart.html)在下载最新版的seata-server并在本地跑起一个
4. seata-server服务
5. 然后使用[mybatis-flex-test](https://gitee.com/mybatis-flex/mybatis-flex/tree/main/mybatis-flex-test)模块 下面的mybatis-flex-spring-boot-seata进行测试
>此demo只是一个纯演示的demo，模仿的是[官方事例](https://github.com/seata/seata-samples/tree/master/springboot-mybatis)
进行整合，微服务合并成一个多数据源进行测试，当然，这种方法是不提倡的，seata事务并且本地多数据源的方式可能本身就存在设计思路问题，可能存在过度设计，此demo只是作为一个演示。
### 注意事项
>使用seata的时候必须数据源代理
`seata.enable-auto-data-source-proxy: false`

`pom`自行引入[seata-spring-boot-starter](https://mvnrepository.com/artifact/io.seata/seata-spring-boot-starter)依赖,

application.yml需要配置如下参数
1. `mybatis-flex.seata-config.enable`

    配置含义：seata事务是否开启，true开启，默认false关闭
2. `mybatis-flex.seata-config.seata-mode`

    默认启动的事务类型，目前只支持XA或者AT,默认AT
