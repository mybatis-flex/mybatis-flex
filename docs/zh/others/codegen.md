# MyBatis-Flex 代码生成器

在 mybatis-flex 的模块 `mybatis-flex-codegen` 中，提供了可以通过数据库表，生成 Entity 类和 Mapper 类的功能。当我们把数据库表设计完成
后可以使用其快速生成 Entity 和 Mapper 的 java 类。

在使用前先添加 `mybatis-flex-codegen` 的 Maven 依赖：

```xml

<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-codegen</artifactId>
    <version>1.3.2</version>
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
<groupId>mysql</groupId>
<artifactId>mysql-connector-java</artifactId>
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
        globalConfig.setGenerateTable("account", "account_session");

        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setWithLombok(true);

        //设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);

        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("tenant_id");
        columnConfig.setLarge(true);
        columnConfig.setVersion(true);
        globalConfig.setColumnConfig("account", columnConfig);

        return globalConfig;
    }
    
    public static GlobalConfig createGlobalConfigUseStyle2() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage("com.test");

        //设置表前缀和只生成哪些表
        globalConfig.getStrategyConfig()
                .setTablePrefix("tb_")
                .setGenerateTable("account", "account_session");

        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true);

        //设置生成 mapper
        globalConfig.enableMapper();

        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("tenant_id");
        columnConfig.setLarge(true);
        columnConfig.setVersion(true);
        globalConfig.getStrategyConfig()
                .setColumnConfig("account", columnConfig);

        return globalConfig;
    }
}
```

注意：由于 MyBatis-Flex 的 APT 功能会自动帮我们生成了 Mapper 的 Java 类，如果我们在代码生成器中选择生成 Mapper，
则建议把 APT 的 Mapper 生成功能给关闭掉，否则系统中会存在两份一样功能的 Mapper。

关闭 APT 的 Mapper 类文件生成，请参考：[APT 设置章节](../others/apt.md)

## 使用介绍

在 Mybatis-Flex 的代码生成器中，支持如下 8 种类型的的产物生成：

- Entity 实体类
- Mapper 映射类
- TableDef 表定义辅助类
- Service 服务类
- ServiceImpl 服务实现类
- Controller 控制类
- MapperXml 文件
- package-info.java 文件

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
        .setSupperClass(BaseEntity.class);
```

## 全局配置 `GlobalConfig`

> 可以像先前一样直接使用 `setXxx()` 进行配置，也可以使用 `getXxxConfig().setXxx()` 进行分类配置。

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
        .setSupperClass(BaseEntity.class);
```

## 注释配置 `JavadocConfig`

| 配置                              | 描述              | 默认值                             |
|---------------------------------|-----------------|---------------------------------|
| setAuthor(String)               | 作者              | System.getProperty("user.name") |
| setSince(String)                | 自               | 日期（yyyy-MM-dd）                  |
| setTableCommentFormat(Function) | 表名格式化           | 原表名                             |
| setEntityPackage(String)        | Entity 包注释      | "实体类层（Entity）软件包。"              |
| setMapperPackage(String)        | Mapper 包注释      | "映射层（Mapper）软件包。"               |
| setServicePackage(String)       | Service 包注释     | "服务层（Service）软件包。"              |
| setServiceImplPackage(String)   | ServiceImpl 包注释 | "服务层实现（ServiceImpl）软件包。"        |
| setControllerPackage(String)    | Controller 包注释  | "控制层（Controller）软件包。"           |
| setTableDefPackage(String)      | TableDef 包注释    | "表定义层（TableDef）软件包。"            |

```java
globalConfig.getJavadocConfig()
        .setAuthor("Your Name")
        .setSince("1.0.1");
```

## 包配置 `PackageConfig`

| 配置                            | 描述             | 默认值                                               |
|-------------------------------|----------------|---------------------------------------------------|
| setSourceDir(String)          | 文件输出目录         | System.getProperty("user.dir") + "/src/main/java" |
| setBasePackage(String)        | 根包名            | "com.mybatisflex"                                 |
| setEntityPackage(String)      | Entity 包名      | getBasePackage() + ".entity"                      |                      |
| setMapperPackage(String)      | Mapper 包名      | getBasePackage() + ".mapper"                      |                      |
| setServicePackage(String)     | Service 包名     | getBasePackage() + ".service"                     |                      |
| setServiceImplPackage(String) | ServiceImpl 包名 | getBasePackage() + ".service.impl"                |                      |
| setControllerPackage(String)  | Controller 包名  | getBasePackage() + ".controller"                  |                      |
| setTableDefPackage(String)    | TableDef 包名    | getEntityPackage() + ".tables"                    |                      |
| setMapperXmlPath(String)      | MapperXml 路径   | getSourceDir() + "/resources/mapper"              |                      |

```java
globalConfig.getPackageConfig()
        .setSourceDir("D://files/java")
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
| setGenerateTables(String...)   | 生成哪些表，白名单              | null  |
| setUnGenerateTables(String...) | 不生成哪些表，黑名单             | null  |

```java
globalConfig.getStrategyConfig()
        .setTablePrefix("sys_")
        .setGenerateTables("sys_user","sys_dept");
```

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
        .setEntity("D:\your-template-file\my-entity.tpl");
```

## Entity 生成配置 `EntityConfig`

| 配置                          | 描述                               | 默认值                |
|-----------------------------|----------------------------------|--------------------|
| setClassPrefix(String)      | Entity 类的前缀                      | ""                 |
| setClassSuffix(String)      | Entity 类的后缀                      | ""                 |
| setSupperClass(Class)       | Entity 类的父类，可以自定义一些 BaseEntity 类 | null               |
| setOverwriteEnable(boolean) | 是否覆盖之前生成的文件                      | false              |
| setImplInterfaces(Class[])  | Entity 默认实现的接口                   | Serializable.class |
| setWithLombok(boolean)      | Entity 是否使用 Lombok 注解            | false              |

```java
globalConfig.getEntityConfig()
        .setWithLombok(true)
        .setClassPrefix("My")
        .setClassSuffix("Entity")
        .setSupperClass(BaseEntity.class);
```

## Mapper 生成配置 `MapperConfig`

| 配置                          | 描述          | 默认值              |
|-----------------------------|-------------|------------------|
| setClassPrefix(String)      | Mapper 类的前缀 | ""               |
| setClassSuffix(String)      | Mapper 类的后缀 | "Mapper"         |
| setSupperClass(Class)       | Mapper 类的父类 | BaseMapper.class |
| setOverwriteEnable(boolean) | 是否覆盖之前生成的文件 | false            |

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
| setSupperClass(Class)       | Service 类的父类 | IService.class |
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
| setSupperClass(Class)       | ServiceImpl 类的父类 | ServiceImpl.class |
| setOverwriteEnable(boolean) | 是否覆盖之前生成的文件      | false             |

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
| setClassSuffix(String)      | Controller 类的后缀     | "Controller" |
| setSupperClass(Class)       | Controller 类的父类     | null         |
| setOverwriteEnable(boolean) | 是否覆盖之前生成的文件         | false        |
| setRestStyle(boolean)       | REST 风格的 Controller | true         |

```java
globalConfig.getControllerConfig()
        .setClassPrefix("My")
        .setClassSuffix("Controller")
        .setSuperClass(BaseController.class);
```

## TableDef 生成配置 `TableDefConfig`

| 配置                          | 描述            | 默认值   |
|-----------------------------|---------------|-------|
| setClassPrefix(String)      | TableDef 类的前缀 | ""    |
| setClassSuffix(String)      | TableDef 类的后缀 | "Def" |
| setOverwriteEnable(boolean) | 是否覆盖之前生成的文件   | false |

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

    private String tableName;

    /**
     * 数据库的 schema
     */
    private String schema;

    /**
     * 默认为 驼峰属性 转换为 下划线字段
     */
    private Boolean camelToUnderline;


    private Class<? extends InsertListener> insertListenerClass;


    private Class<? extends UpdateListener> updateListenerClass;

    // 是否启用ATP生成Mapper
    private Boolean mapperGenerateEnable = Boolean.TRUE;
}
```

## 列配置 `ColumnConfig`

ColumnConfig 支持的配置如下：

```java
public class ColumnConfig implements Serializable {

    private String onInsertValue;
    private String onUpdateValue;

    private Boolean isLarge;
    private Boolean isLogicDelete;
    private Boolean version;

    private JdbcType jdbcType;
    private Class<? extends TypeHandler> typeHandler;

    private String mask;

    private boolean isPrimaryKey = false;
    private KeyType keyType;
    private String keyValue;
    private Boolean keyBefore;

    // 是否是租户列
    private Boolean tenantId;
}
```

## 自定义属性类型

MyBatis-Flex 内置了一个名为：`JdbcTypeMapping` 的 java 类，我们可以用其配置映射 Jdbc 驱动的数据类型为自定义的
数据类型，在开始生成代码之前，可以先调用其进行配置，例如：

```java
JdbcTypeMapping.registerMapping(LocalDateTime.class,Date.class);
```

那么，当我们生成代码的时候，发现 JDBC 驱动的数据类型为 `LocalDateTime`，则 Entity 对应的属性类型为 `Date`。

## 自定义代码模板

通过 `GlobalConfig`（全局配置）的 `setTemplateEngine()` 方法，可以配置自己的模板引擎以及模板，以下是内置的 `EnjoyTemplate`
的代码示例：

```java
public class EnjoyTemplate implements ITemplate {

    private Engine engine;

    public EnjoyTemplate() {
        engine = Engine.create("mybatis-flex", engine -> {
            engine.setToClassPathSourceFactory();
            engine.addSharedMethod(StringUtil.class);
        });
        Engine.addFieldGetterToFirst(new FieldGetters.IsMethodFieldGetter());
    }

    /**
     * 生成 entity 的方法实现
     */
    @Override
    public void generateEntity(GlobalConfig globalConfig, Table table, File entityJavaFile) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("globalConfig", globalConfig);
        params.put("table", table);


        FileOutputStream fileOutputStream = new FileOutputStream(entityJavaFile);
        engine.getTemplate("/templates/enjoy/entity.tpl").render(params, fileOutputStream);
    }


    /**
     * 生成 mapper 的方法实现
     */
    @Override
    public void generateMapper(GlobalConfig globalConfig, Table table, File mapperJavaFile) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("globalConfig", globalConfig);
        params.put("table", table);


        FileOutputStream fileOutputStream = new FileOutputStream(mapperJavaFile);
        engine.getTemplate("/templates/enjoy/mapper.tpl").render(params, fileOutputStream);
    }
}
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