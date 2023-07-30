# SQL 审计

SQL 审计是一项非常重要的工作，是企业数据安全体系的重要组成部分，通过 SQL 审计功能为数据库请求进行全程记录，为事后追溯溯源提供了一手的信息，同时可以通过可以对恶意访问及时警告管理员，为防护策略优化提供数据支撑。

同时、提供 SQL 访问日志长期存储，满足等保合规要求。

## 开启审计功能<Badge type="tip" text="^1.0.5" />

Mybaits-Flex 的 SQL 审计功能，默认是关闭的，若开启审计功能，需添加如下配置。

```java
AuditManager.setAuditEnable(true)
```

默认情况下，Mybaits-Flex 的审计消息（日志）只会输出到控制台，如下所示：

```
>>>>>>Sql Audit: {platform='mybatis-flex', module='null', url='null', user='null', userIp='null', hostIp='192.168.3.24', query='SELECT * FROM `tb_account` WHERE `id` = ?', queryParams=[1], queryTime=1679991024523, elapsedTime=1}
>>>>>>Sql Audit: {platform='mybatis-flex', module='null', url='null', user='null', userIp='null', hostIp='192.168.3.24', query='SELECT * FROM `tb_account` WHERE `id` = ?', queryParams=[1], queryTime=1679991024854, elapsedTime=3}
>>>>>>Sql Audit: {platform='mybatis-flex', module='null', url='null', user='null', userIp='null', hostIp='192.168.3.24', query='SELECT * FROM `tb_account` WHERE `id` = ?', queryParams=[1], queryTime=1679991025100, elapsedTime=2}
```

Mybaits-Flex 消息包含了如下内容：

- **platform**：平台，或者是运行的应用
- **module**：应用模块
- **url**：执行这个 SQL 涉及的 URL 地址
- **user**：执行这个 SQL 涉及的 平台用户
- **userIp**：执行这个 SQL 的平台用户 IP 地址
- **hostIp**：执行这个 SQL 的服务器 IP 地址
- **query**：SQL 内容
- **queryParams**：SQL 参数
- **queryTime**：SQL 执行的时间点（当前时间）
- **elapsedTime**：SQL 执行的消耗时间（毫秒）
- **metas**：其他扩展元信息

::: tip 提示
通过以上的消息内容可知：每个 SQL 的执行，都包含了：哪个访问用户、哪个 IP 地址访问，访问的是哪个 URL 地址，这个 SQL 的参数是什么，执行的时间是什么，执行 
花费了多少时间等等。这样，通过 MyBatis-flex 的 SQL 审计功能，我们能全盘了解到每个 SQL 的执行情况。
:::


## 自定义 SQL 审计内容

MyBatis-Flex 内置了一个名为 `MessageFactory` 的接口，我们只需实现该接口，并为 `AuditManager` 配置新的 `MessageFactory` 即可，如下所示：

```java
public class MyMessageFactory implements MessageFactory {
    
    @Override
    public AuditMessage create() {
        AuditMessage message = new AuditMessage();
       
        // 在这里
        // 设置 message 的基础内容，包括 platform、module、url、user、userIp、hostIp 内容
        // 剩下的 query、queryParams、queryCount、queryTime、elapsedTime 为 mybatis-flex 设置
        
        return message;
    }
}
```

并为 `AuditManager` 配置新写的 `MyMessageFactory`：

```java
MessageFactory creator = new MyMessageFactory();
AuditManager.setMessageFactory(creator);
```



## 自定义 MessageReporter

`MessageReporter` 负责把 Mybaits-Flex 收集的 SQL 审计日志发送到指定位置，在 MyBatis-Flex 中只内置两个
`MessageReporter`，他们分别是：

- `ConsoleMessageReporter` 用于把 SQL 审计日志发送到控制台。
- `HttpMessageReporter` 用于把 SQL 审计日志发动到指定服务器。


`ConsoleMessageReporter` 代码如下：

```java
public class ConsoleMessageReporter implements MessageReporter {

    @Override
    public void sendMessages(List<AuditMessage> messages) {
        for (AuditMessage message : messages) {
            System.out.println(">>>>>>Sql Audit: " + message.toString());
        }
    }

}
```

我们也可以自己去实现 `MessageReporter` 接口，示例代码如下：

```java
public class MyMessageReporter implements MessageReporter {

    @Override
    public void sendMessages(List<AuditMessage> messages) {
        //在这里把 messages 审计日志发送到指定位置
        //比如 
        // 1、通过 http 协议发送到指定服务器
        // 2、通过日志工具发送到日志平台
        // 3、通过 Kafka 等 MQ 发送到指定平台
    }

}
```

编写好 `MyMessageReporter` ，在应用启动的时候，为 `AuditManager` 配置新的 `MessageReporter`，示例如下：

```java
MessageReporter reporter = new MyMessageReporter();
AuditManager.setMessageReporter(reporter);
```

## 自定义 MessageCollector

MyBatis-Flex 内置了两个 Collector，他们分别是：

- **ScheduledMessageCollector** 定时把消息通过 MessageReporter 发送到指定位置。
- **ConsoleMessageCollector** 使用其把消息输出到控制台。

::: tip 提示
`ConsoleMessageCollector` 和 `ConsoleMessageReporter` 都能把 SQL 审计日志发送到控制台打印，
区别是 `ConsoleMessageCollector` 是实时打印；`ConsoleMessageReporter` 是通过 `ScheduledMessageCollector`
进行定时打印（默认情况下：每 10s 打印一次日志）。
:::

## SQL 调试输出

使用 `ConsoleMessageCollector` 实时输出 SQL 日志，代码如下：

```java
MessageCollector collector = new ConsoleMessageCollector();
AuditManager.setMessageCollector(collector);
AuditManager.setAuditEnable(true);
```
通过以上代码，配置 `AuditManager` 的 `MessageCollector` 为 `ConsoleMessageCollector` 后，
每次执行 sql 请求，控制台将输入内容如下：

```
Flex exec sql took 2 ms >>>  SELECT * FROM `tb_account` WHERE `id` = 1
Flex exec sql took 3 ms >>>  INSERT INTO `tb_account`(`user_name`, `age`, `birthday`)  VALUES ('lisi', 22, '2023-04-07 15:28:46')
```