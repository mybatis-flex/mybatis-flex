# SQL 日志打印

## 内置方案

MyBatis-Flex 内置了 SQL 打印分析的功能，其是使用 SQL 审计模块进行完成的，开启 SQL 日志打印代码如下：

```java
//开启审计功能
AuditManager.setAuditEnable(true);

//设置 SQL 审计收集器
MessageCollector collector = new ConsoleMessageCollector();
AuditManager.setMessageCollector(collector);
```

通过以上代码，配置 `AuditManager` 的 `MessageCollector` 为 `ConsoleMessageCollector` 后，
每次执行 SQL 请求，控制台将输入内容如下：

```
Flex exec sql took 2 ms >>>  SELECT * FROM `tb_account` WHERE `id` = 1
Flex exec sql took 3 ms >>>  INSERT INTO `tb_account`(`user_name`, `age`, `birthday`)  VALUES ('lisi', 22, '2023-04-07 15:28:46')
```

控制台输出了完整的 SQL，以及 SQL 执行消耗时间，方便我们在开发的时候，对慢 SQL 进行排查和快速定位。

或者在 Spring 工程里，将 SQL 打印到日志中，可以通过配置日志级别控制是否输出 SQL ，通过配置日志 Appender 控制 SQL 输出目的地。
```java
import com.mybatisflex.core.audit.AuditManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisFlexConfiguration {

    private static final Logger logger = LoggerFactory
        .getLogger("mybatis-flex-sql");


    public MyBatisFlexConfiguration() {
        //开启审计功能
        AuditManager.setAuditEnable(true);

        //设置 SQL 审计收集器
        AuditManager.setMessageCollector(auditMessage ->
            logger.info("{},{}ms", auditMessage.getFullSql()
                , auditMessage.getElapsedTime())
        );
    }
}
```

## MyBatis 自带方案

### 非 Spring 项目

通过 `bootstrap.setLogImpl()` 方法来指定 MyBatis 输出日志：

```java 5
DataSource dataSource = ...;

MybatisFlexBootstrap bootstrap = MybatisFlexBootstrap.getInstance()
    .setDataSource(dataSource)
    .setLogImpl(StdOutImpl.class)
    .addMapper(AccountMapper.class)
    .start();
```

### SpringBoot 项目

通过自定义 `ConfigurationCustomizer` 来为 `configuration` 配置 `LogImpl`：

```java
@Configuration
public class MyConfigurationCustomizer implements ConfigurationCustomizer {

    @Override
    public void customize(FlexConfiguration configuration) {
        configuration.setLogImpl(StdOutImpl.class);
    }
}
```

## p6spy 方案

我们可以把数据源配置为 p6spy 数据源，使用 p6spy 的 SQL 输出功能进行 SQL 打印。更多文档参考 p6spy 官方文档：
[https://p6spy.readthedocs.io/en/latest/index.html](https://p6spy.readthedocs.io/en/latest/index.html)

使用 SpringBoot 的情况下，参考文档 [https://github.com/gavlyukovskiy/spring-boot-data-source-decorator](https://github.com/gavlyukovskiy/spring-boot-data-source-decorator)
