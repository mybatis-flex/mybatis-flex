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

## 多数据源注意事项

注意：在多数据源的情况下，所有数据源的数据库请求（Connection）会执行相同的 `commit` 或者 `rollback`，MyBatis-Flex 只保证了程序端的原子操作，
但并不能保证多个数据源之间的原子操作。例如：

```java 1,6,13,19
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
        DataSourceKey.clear();
    }

    //抛出异常
    int x = 1/0;
}
```

在以上的例子中，执行了两次 `Db.updateBySql(...)`，它们是两个不同的数据源，但它们都在同一个事务 `@Transactional` 里，因此，当抛出异常的时候，
它们都会进行回滚（rollback）。

以上提到的 `并非原子操作`，指的是：

>假设在回滚的时候，恰好其中一个数据库出现了异常（比如 网络问题，数据库崩溃），此时，可能只有一个数据库的数据正常回滚（rollback）。
> 但无论如何，MyBatis-Flex 都会保证在同一个 `@Transactional` 中的多个数据源，保持相同的 commit 或者 rollback 行为。

## Seata 分布式事务

Seata 是一款开源的分布式事务解决方案，致力于提供高性能和简单易用的分布式事务服务。
Seata 将为用户提供了 AT、TCC、SAGA 和 XA 事务模式，为用户打造一站式的分布式解决方案。
官方网站：https://seata.io/zh-cn/index.html


### 开始使用

**第 1 步：在 `application.yml` 配置开启 Seata 分布式事务功能：**

```yaml
mybatis-flex:
  seata-config:
    enable: true
    seata-mode: XA # 支持 XA 或者 AT
```
- XA：指的是: 分布式事务协议（X/Open Distributed Transaction Processing），它是一种由 X/Open 组织制定的分布式事务标准，
XA 使用两阶段提交（2PC，Two-Phase Commit）来保证所有资源同时提交或回滚任何特定的事务。
目前，几乎所有主流的数据库都对 XA 规范 提供了支持，是 Seata 默认使用的模式。

- AT： 是一种无侵入的分布式事务解决方案。在 AT 模式下，用户只需关注自己的 “`业务SQL`”，
用户的 “`业务SQL`” 作为一阶段，Seata 会根据 SQL 内容，自动生成事务的二阶段提交和回滚操作。

**第 2 步：在 `application.yml` 添加 Seata 的相关配置：**

```yaml
seata:
  enabled: true
  application-id: business-service
  tx-service-group: my_test_tx_group
  enable-auto-data-source-proxy: false  #必须
#  use-jdk-proxy: false
  client:
    rm:
      async-commit-buffer-limit: 1000
      report-retry-count: 5
      table-meta-check-enable: false
      report-success-enable: false
      lock:
        retry-interval: 10
        retry-times: 30
        retry-policy-branch-rollback-on-conflict: true
    tm:
      commit-retry-count: 5
      rollback-retry-count: 5
    undo:
      data-validation: true
      log-serialization: jackson
      log-table: undo_log
    log:
      exceptionRate: 100
  service:
    vgroup-mapping:
      my_test_tx_group: default
    grouplist:
      default: 127.0.0.1:8091
    #enable-degrade: false
    #disable-global-transaction: false
  transport:
    shutdown:
      wait: 3
    thread-factory:
      boss-thread-prefix: NettyBoss
      worker-thread-prefix: NettyServerNIOWorker
      server-executor-thread-prefix: NettyServerBizHandler
      share-boss-worker: false
      client-selector-thread-prefix: NettyClientSelector
      client-selector-thread-size: 1
      client-worker-thread-prefix: NettyClientWorkerThread
      worker-thread-size: default
      boss-thread-size: 1
    type: TCP
    server: NIO
    heartbeat: true
    serialization: seata
    compressor: none
    enable-client-batch-send-request: true
  config:
    type: file
  registry:
    type: file
```

> 以上配置的含义，请参考 Seata 官方网站：https://seata.io/zh-cn/docs/user/configurations.html

**3、通过使用 `@GloabalTransactional` 开始 Seata 分布式事务。**

```java 1
@GlobalTransactional
public void purchase(String userId, String commodityCode, int orderCount) {
    LOGGER.info("purchase begin ... xid: " + RootContext.getXID());
    stockClient.deduct(commodityCode, orderCount);
    orderClient.create(userId, commodityCode, orderCount);
}
```

> 更多关于 Seata 的知识，请异步 Seata 官方网站了解：https://seata.io/zh-cn/docs ，也可以参考 Seata
> 的官方示例快速开始：https://seata.io/zh-cn/docs/user/quickstart.html

### 注意事项
1.使用`seata-spring-boot-starter`的时候请关闭自动代理
```yaml
seata:
  enable-auto-data-source-proxy: false
```
2.使用 `seata-all` 请不要使用 `@EnableAutoDataSourceProxy`

3.如果是 SpringBoot 项目需要引入相关 Maven 依赖，例如：

```xml
<dependency>
    <groupId>io.seata</groupId>
    <artifactId>seata-spring-boot-starter</artifactId>
    <version>1.7.0</version>
</dependency>
```
### 示例

[mybatis-flex-spring-boot-seata-demo](https://gitee.com/mybatis-flex/mybatis-flex-samples/tree/master/mybatis-flex-spring-boot-seata-demo) : Seata 官方 demo 与 flex 结合。


## 子父线程同时访问一个ThreadLocal访问的场景
考虑到某些场景下，子父线程会同时访问父线程进行传递值进行切换数据源的场景，提供了以下的支持
比如如下的代码：
```java
public static void main(String[]args){
        //线程1
        //进行数据库操作读取 ds1
        //切换数据源2
        DataSourceKey.use("ds2");
        new Thread(() -> {
            //查询数据源 ds2
            //实际在线程2并不是ds2而是ds1
        }).start();
}
```
此类场景进行如下的方案进行修改
1. 使用可以跨越线程池的`ThreadLocal`比如阿里的`transmittable-thread-local`
导入如下的包
```xml
     <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>transmittable-thread-local</artifactId>
            <version>2.14.2</version>
        </dependency>
```
2. 对切换的源码进行修改
```java
public static void main(String[]args){
        DataSourceKey.setAnnotationKeyThreadLocal(new TransmittableThreadLocal<>());
        DataSourceKey.setManualKeyThreadLocal(new TransmittableThreadLocal<>());
        //线程1
        //进行数据库操作读取 ds1
        //切换数据源2
        DataSourceKey.use("ds2");
        new Thread(() -> {
            //查询数据源 ds2
            //实际在线程2使用的就是ds2
        }).start();
}
```
### 扩展阅读
[transmittable-thread-local](https://github.com/alibaba/transmittable-thread-local) 可以不侵入进行代码原线程的替换
