# 多数据源

MyBaits-Flex 内置了功能完善的多数据源支持<Badge type="tip" text="^1.0.6" />，不需要借助第三方插件或者依赖，开箱即用，
支持包括 `druid`、`hikaricp`、`dbcp2`、`beecp` 在内的任何数据源，MyBatis-Flex 多数据源配置如下：

```yaml
mybatis-flex:
  datasource:
    ds1:
      url: jdbc:mysql://127.0.0.1:3306/db
      username: root
      password: 123456
    ds2:
      url: jdbc:mysql://127.0.0.1:3306/db2
      username: root
      password: 123456
```

在以上配置中，`ds1` 和 `ds2` 是由用户自定义的，我们可以理解为数据源的名称，或者数据源的 `key`，这个在动态切换数据库中非常有用。

在无 Spring 或 Solon 框架的场景下，代码如下：

```java
DataSource dataSource1 = new HikariDataSource();
dataSource1.setJdbcUrl(....)

DataSource dataSource2 = new HikariDataSource();
dataSource2.setJdbcUrl(....)

DataSource dataSource3 = new HikariDataSource();
dataSource3.setJdbcUrl(....)

MybatisFlexBootstrap.getInstance()
        .setDataSource("ds1", dataSource1)
        .addDataSource("ds2", dataSource2)
        .addDataSource("ds3", dataSource3)
        .start();
```
## 开始使用

默认使用第一个配置的数据源：

```java
List<Row> rows = Db.selectAll("tb_account");
System.out.println(rows);
```

通过编码的方式，切换到数据源 `ds2`：

```java 2,6
try{
    DataSourceKey.use("ds2")
    List<Row> rows = Db.selectAll("tb_account");
    System.out.println(rows);
}finally{
    DataSourceKey.clear();
}
```

或者

```java
List<Row> rows =  DataSourceKey.use("ds2"
    , () -> Db.selectAll("tb_account"));
```

## 数据源切换（设置）

MyBatis-Flex 提供了 4 种方式来配置数据源：
- 1、编码，使用`DataSourceKey.use` 方法。
- 2、`@UseDataSource("dataSourceName")` 在 Mapper 类上，添加注解，用于指定使用哪个数据源。
- 3、`@UseDataSource("dataSourceName")` 在 Mapper 方法上，添加注解，用于指定使用哪个数据源。
- 4、`@Table(dataSource="dataSourceName")` 在 Entity 类上添加注解，该 Entity 的增删改查请求默认使用该数据源。

> 在 SpringBoot 或 Solon 项目上，`@UseDataSource("dataSourceName")` 也可用于在 Controller 或者 Service 类上。若是 Spring 项目（非 SpringBoot）,
> 用户需要参考 `MultiDataSourceAutoConfiguration` 进行配置后才能使用。


`DataSourceKey.use` 示例：
```java 2,6
try{
    DataSourceKey.use("ds2")
    List<Row> rows = Db.selectAll("tb_account");
    System.out.println(rows);
}finally{
    DataSourceKey.clear();
}
```

`@UseDataSource("dataSourceName")` 示例：

```java 1
@UseDataSource("ds2")
interface AccountMapper extends BaseMapper{
    List<Account> myMethod();
}
```

或者
```java 3
interface AccountMapper extends BaseMapper{

    @UseDataSource("ds2")
    List<Account> myMethod();
}
```

`@UseDataSource(value = "#my_expression")`扩展数据源切换，表达式动态读取 key 值

- 自定义表达式扩展，对接口 DataSourceProcessor 进行实现，推荐约定动态表达式以 `#` 作为起始标识(当然并不强制做要求)
```java
// 自定义基于Session的表达式取值示例
public class InSessionDataSourceProcessor implements DataSourceProcessor {
    private static final String SESSION_PREFIX = "#session";
    @Override
    public String process(String dataSourceKey, Object mapper, Method method, Object[] arguments) {
        if (StrUtil.isBlank(dataSourceKey)) return null;
        if (!dataSourceKey.startsWith(SESSION_PREFIX)) return null;

        HttpServletRequest request = RequestContextHolder.get.....;
        if (null==request) return null;
        return request.getSession().getAttribute(dataSourceKey.substring(9)).toString();
    }
}
```

- 内置取值表达式取值解析处理器，作为示例参考
    - `DelegatingDataSourceProcessor`，对`DataSourceProcessor`结构进行扩大和增强的委托类，多处理器推荐使用该委托类进行注入，注意委托类内使用`List`委托管理多个解析处理器，执行时有先后顺序。
    - `ParamIndexDataSourceProcessor`，针对简单参数快速取值。
    - `SpelExpressionDataSourceProcessor`，`Spring` 模式下 `SPEL` 表达式取值。
- 注入动态数据源取值处理器

```java
// 注入顺序既为执行时的先后顺序(前面的处理器一旦正确处理，将不会再往下传递处理)
DelegatingDataSourceProcessor dataSourceProcessor = DelegatingDataSourceProcessor.with(
        // 参数索引快速取值的
        new ParamIndexDataSourceProcessor(),
        // Spel 表达式的
        new SpelExpressionDataSourceProcessor(),
        new .....等多个自行扩展表达式()
);
// 实例 setter 赋值
DataSourceManager.setDataSourceProcessor(dataSourceProcessor);
```

- 应用示例：
```java
@Mapper
public interface MyMapper extends BaseMapper<MyEntity >{
    // 取值第一个参数的值（arg0的值）
    @UseDataSource(value = "#first")
    MyEntity testFirst(String arg0, String arg1, String arg2);

    // 取值索引1(第二个参数 arg1 )的值
    @UseDataSource(value = "#index1")
    MyEntity testIndex(String arg0, String arg1, String arg2);

    // 取值最后一个参数的值(arg3的值)
    @UseDataSource(value = "#last")
    MyEntity testLast(String arg0, String arg1, String arg2, String arg3);

    // Spel 表达式取值数据源
    @UseDataSource(value = "#condition.getDsId()")
    MyEntity testSpel(MyDatasourceCondition condition);
}
```
 - 调用
```java
public void test(){
    // mapper中的注解 @UseDataSource(value = "#first")
    myMapper.testFirst("my-dskey1", "arg1", "arg2");

    // 取值索引1(第二个参数)的值；mapper中的注解 @UseDataSource(value = "#index1")
    myMapper.testIndex("arg0", "my-dskey2", "arg2");

    // mapper中的注解 @UseDataSource(value = "#last")
    myMapper.testLast("arg0", "arg1", "arg2","my-dskey4");

    MyDatasourceCondition condition = new MyDatasourceCondition();
    condition.setDsId("my-dskey5");
    // mapper中的注解 @UseDataSource(value = "#condition.getDsId()")
    myMapper.testSpel(condition);
}
```

- **注意：**
    - 1：使用区分`Spring模式`和`非Spring`模式，`Spring`模式下，处理逻辑`DataSourceInterceptor`优先级高于 `FlexMapperProxy`，
        所以`Spring模式下仅 DataSourceInterceptor 生效`(切面生效的前提下)。`非Spring` 模式下,`仅支持注解使用到 Mapper(Dao层)`,
        使用到其他层(如Service层)不支持注解解析。
    - 2：`Spring模式`下,不区分使用到程序的层(Controller、Service、Dao层都支持)，下层控制粒度细上层控制粒粗，使用时根据需要进行灵活应用。


`@Table(dataSource="dataSourceName")` 示例：
```java 1
@Table(value = "tb_account", dataSource = "ds2")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String userName;
}
```

::: tip 数据源配置的优先级
`DataSourceKey.use()` > `@UseDataSource()在方法上` > `@UseDataSource()在类上` >`@Table(dataSource="...")`
:::

## 数据源缺失处理器

当无法根据 `dataSourceKey` 找到数据源时，默认情况下会抛出 `IllegalStateException` 异常。
数据源缺失处理器（`DataSourceMissingHandler`）提供了更加灵活的处理方式，你可以通过它自定义处理逻辑（如：记录日志、抛出异常或主动初始化新的数据源）。

### 使用示例

我们推荐使用 `MyBatisFlexCustomizer` 来配置数据源缺失处理器，如下所示：

```java
@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        // ...

        // 配置数据源缺失处理器：此处演示的是以后备逻辑主动初始化数据源
        globalConfig.setDataSourceMissingHandler((dataSourceKey, dataSourceMap) -> {
            // 根据 key 获取数据源
            DataSource ds = customCreateDataSource(dataSourceKey);

            // 取不到的时候返回 null，后续代码逻辑仍然由 FlexDataSource 处理（即抛出异常）
            if (ds == null) return null;

            // 添加新的数据源，避免下次再次触发和创建
            dataSourceMap.put(dataSourceKey, ds);

            return dataSourceMap;
        });

        // ...
    }

}
```

## 更多的 Spring 或 Solon Yaml 配置支持
```yaml
mybatis-flex:
  datasource:
    ds1:
      url: jdbc:mysql://127.0.0.1:3306/db
      username: root
      password: 123456
    ds2:
      url: jdbc:mysql://127.0.0.1:3306/db2
      username: root
      password: 123456
```
在以上配置中，`ds1` 和 `ds2` 并未指定使用哪个数据连接池，MyBatis-Flex 会 **自动探测** 当前项目依赖了哪个连接池。
目前支持了 `druid`、`hikaricp`、`dbcp2`、`beecp` 的自动探测，如果项目中使用的不是这 4 种类型只有，需要添加 `type` 配置，如下所示：

```yaml 4,9
mybatis-flex:
  datasource:
    ds1:
      type: com.your.datasource.type1
      url: jdbc:mysql://127.0.0.1:3306/db
      username: root
      password: 123456
    ds2:
      type: com.your.datasource.type2
      url: jdbc:mysql://127.0.0.1:3306/db2
      username: root
      password: 123456
```

同时，项目若使用到了多个数据源类型，则也需要添加 `type` 来指定当前数据源的类型。


除了 `type`、`url`、`username`、`password` 的配置以外，MyBatis-Flex 支持该 `DataSource` 类型的所有参数配置，
例如，在 `DruidDataSource` 类中存在 `setAsyncInit` 方法，我们就可以添加 `asyncInit` 的配置，如下所示：

```yaml 8
mybatis-flex:
  datasource:
    ds1:
      type: druid
      url: jdbc:mysql://127.0.0.1:3306/db
      username: root
      password: 123456
      asyncInit: true
    ds2:
      type: com.your.datasource.type2
      url: jdbc:mysql://127.0.0.1:3306/db2
      username: root
      password: 123456
```

因此，只要该 `DataSource` 有 setter 方法，我们就可以在配置文件中进行配。与此相反的是：若该 `DataSource` 类型没有该属性，则不能使用这个配置。

::: tip 提示
在数据源的配置中，`type` 可以配置为某个 DataSource 的类名，也可以配置为别名，别名支持有：`druid`、
`hikari`、`hikaricp`、`bee`、`beecp`、`dbcp`、`dbcp2`。
:::

## 动态添加新的数据源

在多租户等某些场景下，我们可能需要用到动态的添加新的数据源，此时可以通过如下的方式进行添加。

```java
FlexDataSource flexDataSource = FlexGlobalConfig.getDefaultConfig()
                                .getDataSource();

//新的数据源
HikariDataSource newDataSource = new HikariDataSource();

flexDataSource.addDataSource("newKey", newDataSource);
```

**需要注意的是：** 通过 FlexGlobalConfig 去获取 FlexDataSource 时，需等待应用完全启动成功后，才能正常获取 FlexDataSource，
否则将会得到 null 值。

Spring 用户可以通过 `ApplicationListener` 去监听 `ContextRefreshedEvent` 事件，然后再去添加新的数据源，如下代码所示：

```java
public class DataSourceInitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FlexDataSource dataSource = FlexGlobalConfig.getDefaultConfig()
                                    .getDataSource();

        dataSource.addDataSource("....", new DruidDataSource("..."));
    }

}
```

## 多数据源负载均衡 <Badge type="tip" text="^1.5.4" />

数据源负载均衡指的是：在进行数据查询的时候，随机使用一个数据源。 这是的在高并发的场景下，起到负载的效果。

假设多数据源配置如下：

```yaml
mybatis-flex:
  datasource:
    ds1:
      type: druid
      url: jdbc:mysql://127.0.0.1:3306/db
      username: root
      password: 123456
      asyncInit: true
    ds2:
      type: com.your.datasource.type2
      url: jdbc:mysql://127.0.0.1:3306/db2
      username: root
      password: 123456
    other:
      type: com.your.datasource.type2
      url: jdbc:mysql://127.0.0.1:3306/db3
      username: root
      password: 123456
```

以上配置了三个数据源，分别为：`ds1`、`ds2`、`other`，假设我们想负载均衡使用 `ds1`、`ds2` ，那么代码如下：

```java 2,6
try{
    DataSourceKey.use("ds*");
    List<Row> rows = Db.selectAll("tb_account");
    System.out.println(rows);
}finally{
    DataSourceKey.clear();
}
```

`DataSourceKey.use("ds*")` 中的 `ds*` 指的是使用 `ds` 开头的任意一个数据源。`ds*` 必须以 "`*`" 结尾，
中间不能有空格，比如 "`ds  *`" 中间有空格是不行的。
