# SpringBoot 配置文件

SpringBoot 配置文件（`application.yml` 等）主要是用于对 MyBatis 原生以及 MyBatis-Flex 的 `FlexGlobalConfig` 进行配置。

示例如下：

```yaml
mybatis-flex:
  #......
  datasource:
    #......
  configuration:
    #......
  global-config:
    #......
  admin-config:
    #......
  seata-config:
    #......
```

## mybatis-flex

### datasource

- 类型：`Map<String, Map<String, String>>`
- 默认值：`null`

MyBatis-Flex 多数据源配置，参考 [多数据源配置](../core/multi-datasource.md#更多的-spring-yaml-配置支持)。

### config-location

- 类型：`String`
- 默认值：`null`

MyBatis 配置文件位置，如果有单独的 MyBatis 配置，需要将其路径配置到 `configLocation` 中。MyBatis Configuration
的具体内容请参考 [MyBatis 官方文档](https://www.mybatis.org/mybatis-3/zh_CN/configuration.html)。

### mapper-locations

- 类型：`String[]`
- 默认值：`["classpath*:/mapper/**/*.xml"]`

MyBatis Mapper 所对应的 XML 文件位置，如果在 Mapper 中有自定义的方法（XML 中有自定义的实现），需要进行该配置，指定 Mapper
所对应的 XML 文件位置。

### type-aliases-package

- 类型：`String`
- 默认值：`null`

MyBaits 别名包扫描路径，通过该属性可以给包中的类注册别名，注册后在 Mapper 对应的 XML 文件中可以直接使用类名，而不用使用全限定的类名（即
XML 中调用的时候不用包含包名）。

### type-aliases-super-type

- 类型：`Class<?>`
- 默认值：`null`

该配置请和 [typeAliasesPackage](#type-aliases-package) 一起使用，如果配置了该属性，则仅仅会扫描路径下以该类作为父类的域对象。

### type-handlers-package

- 类型：`String`
- 默认值：`null`

TypeHandler 扫描路径，如果配置了该属性，SqlSessionFactoryBean 会把该包下面的类注册为对应的 TypeHandler 处理器。

### check-config-location

- 类型：`boolean`
- 默认值：`false`

启动时检查是否存在 MyBatis XML 文件，默认不检查。

### executor-type

- 类型：`ExecutorType`
- 默认值：`simple`

通过该属性可指定 MyBatis 的执行器，MyBatis 的执行器总共有三种：

- `ExecutorType.SIMPLE`：该执行器类型不做特殊的事情，为每个语句的执行创建一个新的预处理语句（PreparedStatement）。
- `ExecutorType.REUSE`：该执行器类型会复用预处理语句（PreparedStatement）。
- `ExecutorType.BATCH`：该执行器类型会批量执行所有的更新语句。

### defaults-scripting-language-driver

- 类型：`Class<? extends LanguageDriver>`
- 默认值：`null`

指定默认的脚本语言驱动器。

### configuration-properties

- 类型：`Properties`
- 默认值：`null`

指定外部化 MyBatis Properties 配置，通过该配置可以抽离配置，实现不同环境的配置部署。

## configuration

本部分（Configuration）的配置都为 MyBatis
原生支持的配置，有关配置请参考 [MyBatis Configuration](https://mybatis.org/mybatis-3/zh_CN/configuration.html#%E8%AE%BE%E7%BD%AE%EF%BC%88settings%EF%BC%89)。

## global-config

### print-banner

- 类型：`boolean`
- 默认值：`true`

是否控制台打印 MyBatis-Flex 的 LOGO 及版本号。

### key-config

- 类型：`com.mybatisflex.core.FlexGlobalConfig.KeyConfig`
- 默认值：`null`

全局的 ID 生成策略配置，当 `@Id` 未配置或者配置 `KeyType` 为 `None` 时 使用当前全局配置。

### normal-value-of-logic-delete

- 类型：`java.lang.Object`
- 默认值：`0`

逻辑删除数据存在标记值。

### deleted-value-of-logic-delete

- 类型：`java.lang.Object`
- 默认值：`1`

逻辑删除数据删除标记值。

### logic-delete-column

- 类型：`java.lang.String`
- 默认值：`del_flag`

默认的逻辑删除字段。

### tenant-column

- 类型：`java.lang.String`
- 默认值：`tenant_id`

默认的多租户字段。

### version-column

- 类型：`java.lang.String`
- 默认值：`version`

默认的乐观锁字段。

## admin-config

通过对 admin-config 的配置，可以把当前应用执行的 SQL，发送到 **MyBatis-Flex-Admin** 进行审计，详情点击 [这里](/zh/awesome-things.html#mybatis-flex-admin)。

### enable

- 类型：`boolean`
- 默认值：`false`

是否启用 MyBatis-Flex-Admin 连接。

### endpoint

- 类型：`String`
- 默认值：`null`

MyBatis-Flex-Admin 连接端点。

### secret-key

- 类型：`String`
- 默认值：`null`

MyBatis-Flex-Admin 连接密钥。

---
示例代码：

```yaml
mybatis-flex:
  admin-config:
    enable: true
    endpoint: http://admin-host:8080/message/collect
    secret-key: secretKey
```

> secret-key 配置的内容，必须要和 MyBatis-Flex-Admin 项目配置的 `mybatis-flex.admin.message-secret-key` 完全一致，才能正常收集 SQL 执行日志。

## seata-config

### enable

- 类型：`boolean`
- 默认值：`false`

是否启用 Seata 代理数据源。

### seata-mode

- 类型：`com.mybatisflex.spring.boot.MybatisFlexProperties.SeataMode`
- 默认值：`AT`

使用 Seata AT 模式代理数据源。
