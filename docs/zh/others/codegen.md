# MyBatis-Flex 代码生成器

## 简介

在 mybatis-flex 中，有了一个名称为  `mybatis-flex-codegen` 的模块，提供了可以通过数据库表，生成代码的功能。当我们把数据库表设计完成后，
就可以使用其快速生成 Entity、 Mapper、 Service、 Controller 等产物。

除此之外，我们还可以通过扩展生成更多的产物，文档参考 [#添加其他产物的生成](#添加其他产物的生成)。

### AI 代码生成功能
> 另外：MyBatis-Flex 也提供了一个在线的 AI 代码生成器，可以通过您的产品（或项目）需求描述，自动帮你生成完整的
> SpringBoot + MyBatisFlex 项目代码以及 SQL 脚本，下载导入到开发工具即可使用。
>
> 内测地址：https://ai.mybatis-flex.com

## 快速开始

在使用之前，我们需要先添加 `mybatis-flex-codegen` 的 Maven 依赖：

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-codegen</artifactId>
    <version>1.11.4</version>
</dependency>
```

同时需要添加数据源的 Maven 依赖和 jdbc 驱动依赖：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.32</version>
</dependency>
```

然后，编写一个任意带有 main 方法的类，如下所示：

```java
public class Codegen {

    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/your-database?characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("******");

        //创建配置内容，两种风格都可以。
        GlobalConfig globalConfig = createGlobalConfigUseStyle1();
        //GlobalConfig globalConfig = createGlobalConfigUseStyle2();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle1() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.setBasePackage("com.test");

        //设置表前缀和只生成哪些表
        globalConfig.setTablePrefix("tb_");
        globalConfig.setGenerateTable("tb_account", "tb_account_session");

        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);
        //设置项目的JDK版本，项目的JDK为14及以上时建议设置该项，小于14则可以不设置
        globalConfig.setEntityJdkVersion(17);

        //设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);

        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("tenant_id");
        columnConfig.setLarge(true);
        columnConfig.setVersion(true);
        globalConfig.setColumnConfig("tb_account", columnConfig);

        return globalConfig;
    }

    public static GlobalConfig createGlobalConfigUseStyle2() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage("com.test");

        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setTablePrefix("tb_")
                .setGenerateTable("tb_account", "tb_account_session");

        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(17);

        //设置生成 mapper
        globalConfig.enableMapper();

        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("tenant_id");
        columnConfig.setLarge(true);
        columnConfig.setVersion(true);
        globalConfig.getStrategyConfig()
                .setColumnConfig("tb_account", columnConfig);

        return globalConfig;
    }
}
```

注意：由于 MyBatis-Flex 的 APT 功能会自动帮我们生成了 Mapper 的 Java 类，如果我们在代码生成器中选择生成 Mapper，
则建议把 APT 的 Mapper 生成功能给关闭掉，否则系统中会存在两份一样功能的 Mapper。

关闭 APT 的 Mapper 类文件生成，请参考：[APT 设置章节](../others/apt.md)

## 功能介绍

在 Mybatis-Flex 的代码生成器中，支持如下 8 种类型的的产物生成：

- Entity 实体类
- Mapper 映射类
- TableDef 表定义辅助类
- Service 服务类
- ServiceImpl 服务实现类
- Controller 控制类
- MapperXml 文件
- package-info.java 文件

> 除此之外，我们可以添加扩展生成更多类型的产物，文档参考 [#添加其他产物的生成](#添加其他产物的生成)。

启用或关闭某种类型产物的生成，代码如下：

```java
// 开启 Entity 的生成
globalConfig.enableEntity();
// 关闭 Entity 的生成
globalConfig.disableEntity();
```

所有方法均支持链式调用配置，代码如下：

```java
// 设置生成 Entity 并启用 Lombok、设置父类
globalConfig.enableEntity()
        .setWithLombok(true)
        .setSuperClass(BaseEntity.class);
```

## 全局配置 `GlobalConfig`

### 详细配置

| 获取配置                   | 描述               |
|------------------------|------------------|
| getJavadocConfig()     | 注释配置             |
| getPackageConfig()     | 包配置              |
| getStrategyConfig()    | 策略配置             |
| getTemplateConfig()    | 模板配置             |
| getEntityConfig()      | Entity 生成配置      |
| getMapperConfig()      | Mapper 生成配置      |
| getServiceConfig()     | Service 生成配置     |
| getServiceImplConfig() | ServiceImpl 生成配置 |
| getControllerConfig()  | Controller 生成配置  |
| getTableDefConfig()    | TableDef 生成配置    |
| getMapperXmlConfig()   | MapperXml 生成配置   |

```java
globalConfig.getPackageConfig()
        .setSourceDir("D://files/java")
        .setBasePackage("com.your.domain");
```

| 启用生成                | 描述                |
|---------------------|-------------------|
| enableEntity()      | 启用 Entity 生成      |
| enableMapper()      | 启用 Mapper 生成      |
| enableService()     | 启用 Service 生成     |
| enableServiceImpl() | 启用 ServiceImpl 生成 |
| enableController()  | 启用 Controller 生成  |
| enableTableDef()    | 启用 TableDef 生成    |
| enableMapperXml()   | 启用 MapperXml 生成   |

启用生成之后可以继续链式进行配置，例如：

```java
// 设置生成 Entity 并启用 Lombok、设置父类
globalConfig.enableEntity()
        .setWithLombok(true)
        .setSuperClass(BaseEntity.class);
```

## 注释配置 `JavadocConfig`

| 配置                               | 描述              | 默认值                             |
|----------------------------------|-----------------|---------------------------------|
| setAuthor(String)                | 作者（可填写日期、版本号等，设置为 `""` 则不添加 `@author`）  | System.getProperty("user.name") |
| setSince(String)                 | 自（可填写日期、版本号等，设置为 `""` 则不添加 `@since`） | `yyyy-MM-dd` 格式的日期              |
| setTableCommentFormat(Function)  | 表注释格式化          | 原表注释                            |
| setColumnCommentFormat(Function) | 字段注释格式化         | 原字段注释                           |
| setEntityPackage(String)         | Entity 包注释      | "实体类层（Entity）软件包。"              |
| setMapperPackage(String)         | Mapper 包注释      | "映射层（Mapper）软件包。"               |
| setServicePackage(String)        | Service 包注释     | "服务层（Service）软件包。"              |
| setServiceImplPackage(String)    | ServiceImpl 包注释 | "服务层实现（ServiceImpl）软件包。"        |
| setControllerPackage(String)     | Controller 包注释  | "控制层（Controller）软件包。"           |
| setTableDefPackage(String)       | TableDef 包注释    | "表定义层（TableDef）软件包。"            |

```java
globalConfig.getJavadocConfig()
        .setAuthor("Your Name")
        .setSince("1.0.1");
```

## 包配置 `PackageConfig`

| 配置                            | 描述             | 默认值                                                           |
|-------------------------------|----------------|---------------------------------------------------------------|
| setSourceDir(String)          | 文件输出目录         | System.getProperty("user.dir") + "/src/main/java"             |
| setBasePackage(String)        | 根包名            | "com.mybatisflex"                                             |
| setEntityPackage(String)      | Entity 包名      | getBasePackage() + ".entity"                                  |                      |
| setMapperPackage(String)      | Mapper 包名      | getBasePackage() + ".mapper"                                  |                      |
| setServicePackage(String)     | Service 包名     | getBasePackage() + ".service"                                 |                      |
| setServiceImplPackage(String) | ServiceImpl 包名 | getBasePackage() + ".service.impl"                            |                      |
| setControllerPackage(String)  | Controller 包名  | getBasePackage() + ".controller"                              |                      |
| setTableDefPackage(String)    | TableDef 包名    | getEntityPackage() + ".tables"                                |                      |
| setMapperXmlPath(String)      | MapperXml 路径   | System.getProperty("user.dir") + "/src/main/resources/mapper" |                      |

```java
globalConfig.getPackageConfig()
        .setSourceDir("D:\\files\\java")
        .setBasePackage("com.your.domain");
```

## 策略配置 `StrategyConfig`

| 配置                             | 描述                     | 默认值   |
|--------------------------------|------------------------|-------|
| setTablePrefix(String)         | 数据库表前缀，多个前缀用英文逗号（,） 隔开 | null  |
| setLogicDeleteColumn(String)   | 逻辑删除的默认字段名称            | null  |
| setVersionColumn(String)       | 乐观锁的字段名称               | null  |
| setGenerateForView(boolean)    | 是否生成视图映射               | false |
| setTableConfig(TableConfig)    | 单独为某张表添加独立的配置          | null  |
| setColumnConfig(ColumnConfig)  | 设置某个列的全局配置             | null  |
| setGenerateSchema(String)      | 生成哪个schema下的表          | null  |
| setGenerateTables(String...)   | 生成哪些表，白名单              | null  |
| setUnGenerateTables(String...) | 不生成哪些表，黑名单             | null  |
| setIgnoreColumns(String...)    | 需要忽略的列，父类定义的字段         | null  |

```java
globalConfig.getStrategyConfig()
        .setGenerateSchema("schema")
        .setTablePrefix("sys_")
        .setGenerateTables("sys_user","sys_dept");
```

> `setGenerateTables` 和 `setUnGenerateTables` 未配置时，生成所有表。

## 模板配置 `TemplateConfig`

| 配置                     | 描述               | 默认值                                |
|------------------------|------------------|------------------------------------|
| setTemplate(ITemplate) |                  |                                    |
| setEntity(String)      | Entity 模板路径      | "/templates/enjoy/entity.tpl"      |
| setMapper(String)      | Mapper 模板路径      | "/templates/enjoy/mapper.tpl"      |
| setService(String)     | Service 模板路径     | "/templates/enjoy/service.tpl"     |
| setServiceImpl(String) | ServiceImpl 模板路径 | "/templates/enjoy/serviceImpl.tpl" |
| setController(String)  | Controller 模板路径  | "/templates/enjoy/controller.tpl"  |
| setTableDef(String)    | TableDef 模板路径    | "/templates/enjoy/tableDef.tpl"    |
| setMapperXml(String)   | MapperXml 模板路径   | "/templates/enjoy/mapperXml.tpl"   |

```java
globalConfig.getTemplateConfig()
        .setTemplate(new FreeMarkerTemplate())
        .setEntity("D:\\your-template-file\\my-entity.tpl");
```

## Entity 生成配置 `EntityConfig`

| 配置                                           | 描述                                                | 默认值                |
|----------------------------------------------|---------------------------------------------------|--------------------|
| setEntityWithBaseClassEnable(boolean)          | 当开启这个配置后，Entity 会生成两个类，自动生成的 getter setter 字段等都在 Base 类里，而开发者可以在 Account.java 中添加自己的业务代码  | false                 |
| setClassPrefix(String)                       | Entity 类的前缀                                       | ""                 |
| setClassSuffix(String)                       | Entity 类的后缀                                       | ""                 |
| setSuperClass(Class)                         | Entity 类的父类，可以自定义一些 BaseEntity 类                  | null               |
| setSuperClassFactory(Function<Table, Class>) | Entity 类的父类工厂，可以用于对特定的 Class 设置父类，而非全部 Entity 的父类 | null               |
| setOverwriteEnable(boolean)                  | 是否覆盖之前生成的文件                                       | false              |
| setEntityBaseOverwriteEnable(boolean)        | 生成Base类时是否覆盖之前生成的文件                           | false              |
| setImplInterfaces(Class[])                   | Entity 默认实现的接口                                    | Serializable.class |
| setWithLombok(boolean)                       | Entity 是否使用 Lombok 注解                             | false              |
| lombokNoArgsConstructorEnable(boolean)       | 当开启 Lombok 注解且不使用 Active Record 时，是否生成 Entity @NoArgsConstructor 注解 | true              |
| lombokAllArgsConstructorEnable(boolean)      | 当开启 Lombok 注解且不使用 Active Record 时，是否生成 Entity @AllArgsConstructor 注解 | true              |
| setWithSwagger(boolean)                      | Entity 是否使用 Swagger 注解                            | false              |
| setSwaggerVersion(EntityConfig.SwaggerVersion) | Swagger 注解版本                                      | SwaggerVersion.FOX |
| setWithActiveRecord(boolean)                 | 是否生成 Active Record 模式的 Entity                     | false              |
| setDataSource(String)                        | 统一使用的数据源                                          | null               |
| setJdkVersion(int)                           | 设置项目的jdk版本                                        | 0                  |

```java
globalConfig.getEntityConfig()
        .setWithLombok(true)
        .setClassPrefix("My")
        .setClassSuffix("Entity")
        .setSuperClass(BaseEntity.class);
```

**注意：** `setSuperClassFactory(Function<Table, Class>)` 的优先级要大于 `setSuperClass(Class)`，当两者同时配置时，`setSuperClass(Class)`
的配置无效。


**setEntitySuperClassFactory** 示例代码：

```java
globalConfig.setEntitySuperClassFactory(table -> {

    // 在这里，可以通过 table 来指定对应 SuperClass
    // 返回 null，则表示不需要设置父类
    return null;
});
```

## Mapper 生成配置 `MapperConfig`

| 配置                           | 描述              | 默认值              |
|------------------------------|-----------------|------------------|
| setClassPrefix(String)       | Mapper 类的前缀     | ""               |
| setClassSuffix(String)       | Mapper 类的后缀     | "Mapper"         |
| setSuperClass(Class)         | Mapper 类的父类     | BaseMapper.class |
| setOverwriteEnable(boolean)  | 是否覆盖之前生成的文件     | false            |
| setMapperAnnotation(boolean) | 是否生成 @Mapper 注解 | false            |

```java
globalConfig.getMapperConfig()
        .setClassPrefix("My")
        .setClassSuffix("Mapper")
        .setSuperClass(BaseMapper.class);
```

## Service 生成配置 `ServiceConfig`

| 配置                          | 描述           | 默认值            |
|-----------------------------|--------------|----------------|
| setClassPrefix(String)      | Service 类的前缀 | ""             |
| setClassSuffix(String)      | Service 类的后缀 | "Service"      |
| setSuperClass(Class)        | Service 类的父类 | IService.class |
| setOverwriteEnable(boolean) | 是否覆盖之前生成的文件  | false          |

```java
globalConfig.getServiceConfig()
        .setClassPrefix("My")
        .setClassSuffix("Service")
        .setSuperClass(IService.class);
```

## ServiceImpl 生成配置 `ServiceImplConfig`

| 配置                          | 描述               | 默认值               |
|-----------------------------|------------------|-------------------|
| setClassPrefix(String)      | ServiceImpl 类的前缀 | ""                |
| setClassSuffix(String)      | ServiceImpl 类的后缀 | "ServiceImpl"     |
| setSuperClass(Class)        | ServiceImpl 类的父类 | ServiceImpl.class |
| setOverwriteEnable(boolean) | 是否覆盖之前生成的文件      | false             |
| setCacheExample(boolean)    | 是否添加缓存示例代码       | false             |

```java
globalConfig.getServiceImplConfig()
        .setClassPrefix("My")
        .setClassSuffix("ServiceImpl")
        .setSuperClass(ServiceImpl.class);
```

## Controller 生成配置 `ControllerConfig`

| 配置                          | 描述                  | 默认值          |
|-----------------------------|---------------------|--------------|
| setClassPrefix(String)      | Controller 类的前缀     | ""           |
| setControllerRequestMappingPrefix(String)| @RequestMapping注解的前缀 | null |
| setClassSuffix(String)      | Controller 类的后缀     | "Controller" |
| setSuperClass(Class)        | Controller 类的父类     | null         |
| setOverwriteEnable(boolean) | 是否覆盖之前生成的文件         | false        |
| setRestStyle(boolean)       | REST 风格的 Controller | true         |

```java
globalConfig.getControllerConfig()
        .setClassPrefix("My")
        .setClassSuffix("Controller")
        .setSuperClass(BaseController.class);
```

## TableDef 生成配置 `TableDefConfig`

| 配置                                               | 描述             | 默认值                                       |
|--------------------------------------------------|----------------|-------------------------------------------|
| setClassPrefix(String)                           | TableDef 类的前缀  | ""                                        |
| setClassSuffix(String)                           | TableDef 类的后缀  | "TableDef"                                |
| setOverwriteEnable(boolean)                      | 是否覆盖之前生成的文件    | false                                     |
| setPropertiesNameStyle(TableDefConfig.NameStyle) | 生成辅助类的字段风格     | TableDefConfig.NameStyle.LOWER_CAMEL_CASE |
| setInstanceSuffix(String)                        | 生成辅助类常量对应的变量后缀 | ""                                        |

```java
globalConfig.getTableDefConfig()
        .setClassPrefix("My")
        .setClassSuffix("Def");
```

## MapperXml 生成配置 `MapperXmlConfig`

| 配置                          | 描述              | 默认值      |
|-----------------------------|-----------------|----------|
| setFilePrefix(String)       | MapperXml 文件的前缀 | ""       |
| setFileSuffix(String)       | MapperXml 文件的后缀 | "Mapper" |
| setOverwriteEnable(boolean) | 是否覆盖之前生成的文件     | false    |

```java
globalConfig.getMapperXmlConfig()
        .setFilePrefix("My")
        .setFileSuffix("Mapper");
```

## 表配置 `TableConfig`

TableConfig 支持的配置如下：

```java
public class TableConfig {

    /**
     * 表名。
     */
    private String tableName;

    /**
     * 数据库的 schema（模式）。
     */
    private String schema;

    /**
     * 默认为 驼峰属性 转换为 下划线字段。
     */
    private Boolean camelToUnderline;

    /**
     * 监听 entity 的 insert 行为。
     */
    private Class<? extends InsertListener> insertListenerClass;

    /**
     * 监听 entity 的 update 行为。
     */
    private Class<? extends UpdateListener> updateListenerClass;

    /**
     * 监听 entity 的查询数据的 set 行为。
     */
    private Class<? extends SetListener> setListenerClass;

    /**
     * 对应列的配置。
     */
    private Map<String, ColumnConfig> columnConfigMap;

    /**
     * 是否开启 Mapper 生成。
     */
    private Boolean mapperGenerateEnable = Boolean.TRUE;

}
```

## 列配置 `ColumnConfig`

ColumnConfig 支持的配置如下：

```java
public class ColumnConfig implements Serializable {

    /**
     * 字段名称。
     */
    private String columnName;

    /**
     * insert 的时候默认值，这个值会直接被拼接到 sql 而不通过参数设置。
     */
    private String onInsertValue;

    /**
     * update 的时候自动赋值，这个值会直接被拼接到 sql 而不通过参数设置。
     */
    private String onUpdateValue;

    /**
     * 是否是大字段，大字段 APT 不会生成到 DEFAULT_COLUMNS 里。
     */
    private Boolean isLarge;

    /**
     * 是否是逻辑删除字段，一张表中只能存在 1 一个逻辑删除字段。
     */
    private Boolean isLogicDelete;

    /**
     * 是否为乐观锁字段。
     */
    private Boolean version;

    /**
     * 配置的 jdbcType。
     */
    private JdbcType jdbcType;

    /**
     * <p>属性的类型。
     *
     * <p>原始类型直接写类型名称，例如：int/long/float/double/boolean<br/>
     * 对象类型请写对应类的全限定名，例如：java.lang.String/com.example.enums.Gender
     */
    private String propertyType;

    /**
     * 属性的默认值，例如：long 类型默认值：0L，枚举类型默认值：Gender.MALE。
     */
    private String propertyDefaultValue;

    /**
     * 自定义 TypeHandler。
     */
    private Class<? extends TypeHandler> typeHandler;

    /**
     * 脱敏方式。
     */
    private String mask;

    /**
     * 字段是否为主键。
     */
    private boolean isPrimaryKey = false;

    /**
     * ID 生成策略。
     */
    private KeyType keyType;

    /**
     * ID 生成器值。
     */
    private String keyValue;

    /**
     * sequence 序列执行顺序。
     */
    private Boolean keyBefore;

    /**
     * 是否是租户 ID。
     */
    private Boolean tenantId;

}
```


## 自定义 Entity 的属性类型

**方式 1：通过 JdbcTypeMapping**


MyBatis-Flex 内置了一个名为：`JdbcTypeMapping` 的 java 类，我们可以用其配置映射 Jdbc 驱动的数据类型为自定义的
数据类型，在开始生成代码之前，可以先调用其进行配置，例如：

```java
JdbcTypeMapping.registerMapping(LocalDateTime.class, Date.class);
```

那么，当我们生成代码的时候，发现 JDBC 驱动的数据类型为 `LocalDateTime`，则 Entity 对应的属性类型为 `Date`。


**方式 2：通过 JdbcTypeMapper**

示例代码如下：

```java
JdbcTypeMapping.setTypeMapper(new JdbcTypeMapping.JdbcTypeMapper() {
    @Override
    public String getType(String jdbcType, Table table, Column column) {
        if (table.getName().equals("tb_sys_permission")
            && column.getName().equals("type")){
            return PermissionType.class.getName();
        }
        return null;
    }
});
```
在以上的示例中，如果表名为 `tb_sys_permission` 且 列名为 `type`，生成的 Entity 的属性类型为 `PermissionType`；

> 注意，通过 JdbcTypeMapper 设置的优先级要高于 `JdbcTypeMapping.registerMapping` 设置的内容。


**方式 3：使用 ColumnConfig 定义**

如下方示例代码所示：

```java 4
ColumnConfig columnConfig = new ColumnConfig();

//定义该属性的类型为 java.util.List<String>
columnConfig.setPropertyType("java.util.List<String>");
columnConfig.setTypeHandler(CommaSplitTypeHandler.class);
columnConfig.setColumnName("your_column_name");

GlobalConfig globalConfig = new GlobalConfig();
globalConfig.setColumnConfig("your_table_name", columnConfig);

Generator generator = new Generator(dataSource, globalConfig);
generator.generate();
```


## 自定义代码模板

通过 `GlobalConfig`（全局配置）的 `setTemplateEngine()` 方法，可以配置自己的模板引擎以及模板，以下是内置的 `EnjoyTemplate`
的代码示例：

```java
public class EnjoyTemplate implements ITemplate {

    private Engine engine;

    public EnjoyTemplate() {
        Engine engine = Engine.use(engineName);
        if (engine == null) {
            engine = Engine.create(engineName, e -> {
                e.addSharedStaticMethod(StringUtil.class);
                e.setSourceFactory(new FileAndClassPathSourceFactory());
            });
        }
        this.engine = engine;

        // 以下配置将支持 user.girl 表达式去调用 user 对象的 boolean isGirl() 方法
        Engine.addFieldGetterToFirst(new FieldGetters.IsMethodFieldGetter());
    }

    @Override
    public void generate(Map<String, Object> params, String templateFilePath, File generateFile) {
        if (!generateFile.getParentFile().exists() && !generateFile.getParentFile().mkdirs()) {
            throw new IllegalStateException("Can not mkdirs by dir: " + generateFile.getParentFile());
        }
        // 开始生成文件
        try (FileOutputStream fileOutputStream = new FileOutputStream(generateFile)) {
            engine.getTemplate(templateFilePath).render(params, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 自定义数据方言
在 MyBatis-Flex 的代码生成器中，已经内置了 4 种方言，他们分别是：

- 默认方言
- MySQL 方言
- Oracle 方言
- SQLite 方言

方言可以通过如下的方式进行使用：

```java 3
Generator generator = new Generator(dataSource
    , globalConfig
    , IDialect.ORACLE); //使用哪个方言

generator.generate();
```
> 不传入方言的情况下，使用默认方言。

针对不同的数据库，我们也可以通过自定义方言来实现代码生成，例如：

MyDialect.java
```java
class MyDialect implements IDialect {
   //重写相关构建方法
}
```
开始使用 MyDialect
```java 3
Generator generator = new Generator(dataSource
    , globalConfig
    , new MyDialect()); //使用哪个方言

generator.generate();
```

## 添加其他产物的生成

通过实现 `IGenerator` 来实现，比如 Entity 实体类的代码如下：

```java
public class EntityGenerator implements IGenerator {

    private String templatePath = "/templates/enjoy/entity.tpl";

    @Override
    public void generate(Table table, GlobalConfig globalConfig) {

        if (!globalConfig.isEntityGenerateEnable()) {
            return;
        }

        PackageConfig packageConfig = globalConfig.getPackageConfig();
        EntityConfig entityConfig = globalConfig.getEntityConfig();

        String entityPackagePath = packageConfig.getEntityPackage().replace(".", "/");
        File entityJavaFile = new File(packageConfig.getSourceDir(), entityPackagePath + "/" +
                table.buildEntityClassName() + ".java");


        if (entityJavaFile.exists() && !entityConfig.isOverwriteEnable()) {
            return;
        }


        Map<String, Object> params = new HashMap<>(4);
        params.put("table", table);
        params.put("entityConfig", entityConfig);
        params.put("packageConfig", packageConfig);
        params.put("javadocConfig", globalConfig.getJavadocConfig());

        globalConfig.getTemplateConfig().getTemplate().generate(params, templatePath, entityJavaFile);
    }
}
```

如果我们想生成其他产物，比如 `html` ，可以通过编写自己的类，来实现 IGenerator 接口，例如：

```java
public class HtmlGenerator implements IGenerator {

    @Override
    public void generate(Table table, GlobalConfig globalConfig) {
        //在这里生成 html 代码
    }
}
```

最后，通过 `GeneratorFactory` 来注册 `HtmlGenerator` 即可：

```java
GeneratorFactory.registerGenerator("html",new HtmlGenerator());
```

## 注意事项！！！

在 MySQL 或者 Oracle 的某些版本中，代码生成器可能无法获取 `表` 或者 `字段` 的注释内容，我们在数据源配置时，注意添加如下的配置信息：

**MySQL**

JdbcUrl 上注意添加 `useInformationSchema=true` 配置，如下代码所示：

```java
HikariDataSource dataSource = new HikariDataSource();

//注意：url 需添加上 useInformationSchema=true 才能正常获取表的注释
dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/db?useInformationSchema=true&characterEncoding=utf-8");
dataSource.setUsername("username");
dataSource.setPassword("password");
```



**Oracle**

JdbcUrl 上注意添加 `remarksReporting=true` 配置，如下代码所示：
```java
HikariDataSource dataSource = new HikariDataSource();

//注意：url 需添加上 remarksReporting=true 才能正常获取表的注释
dataSource.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:orcl?remarksReporting=true");
dataSource.setUsername("username");
dataSource.setPassword("password");
```
