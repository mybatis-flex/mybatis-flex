```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-solon-plugin</artifactId>
</dependency>
```

#### 1、描述

数据扩展插件，为 Solon Data 提供基于 mybatis-flex（[代码仓库](https://gitee.com/mybatis-flex/mybatis-flex)）的框架适配，以提供ORM支持。


#### 2、强调多数据源支持

> Solon 的 ORM 框架都是基于多数据源理念进行适配的。关于 Solon 数据源概念的描述，可参考：[多数据源与动态数据源](https://solon.noear.org/article/353)

* 强调多数据源的配置。例：demo.db1...，demo.db2...
* 强调带 name 的 DataSource Bean
* 强调使用 @Db("name") 的数据源注解


@Db 可注入类型：

| 支持类型 | 说明 | 
| -------- | -------- | 
| Mapper.class     | 注入 Mapper。例：`@Db("db1") UserMapper userMapper`     | 
| FlexConfiguration     | 注入 FlexConfiguration，一般仅用于配置。例：`@Db("db1") FlexConfiguration db1Cfg` | 
| FlexGlobalConfig     | 注入 FlexGlobalConfig，一般仅用于配置。例：`@Db("db1") FlexGlobalConfig db1Gc` | 
| SqlSessionFactory     | 注入 SqlSessionFactory。例：`@Db("db1") SqlSessionFactory db1` （不推荐直接使用） | 
| RowMapperInvoker | 注入 RowMapperInvoker。例：`@Db("db1") RowMapperInvoker rowMapper` |


#### 3、数据源配置


```yml
# 配置数据源
demo.db1:
  schema: rock
  jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
  driverClassName: com.mysql.cj.jdbc.Driver
  username: root
  password: 123456
  
# 配置数据源对应的 mybatis 信息（要与 DataSource bean 的名字对上）
mybatis.db1:
    typeAliases:    #支持包名 或 类名（大写开头 或 *）//支持 ** 或 * 占位符
        - "demo4021.model"
        - "demo4021.model.*" #这个表达式同上效果
    typeHandlers: #支持包名 或 类名（大写开头 或 *）//支持 ** 或 * 占位符
        - "demo4021.dso.mybaits.handler"
        - "demo4021.dso.mybaits.handler.*" #这个表达式同上效果
    mappers:        #支持包名 或 类名（大写开头 或 *）或 xml（.xml结尾）//支持 ** 或 * 占位符
        - "demo4021.**.mapper"
        - "demo4021.**.mapper.*" #这个表达式同上效果
        - "classpath:demo4035/**/mapper.xml"
        - "classpath:demo4035/**/mapping/*.xml" 
    configuration:  #扩展配置（要与 FlexConfiguration 类的属性一一对应）
        cacheEnabled: false
        mapUnderscoreToCamelCase: true
    globalConfig:   #全局配置（要与 FlexGlobalConfig 类的属性一一对应）//只是示例，别照抄
        printBanner: false
        keyConfig: 
            keyType: "Generator"
            value: "snowFlakeId"


#
#提示：使用 "**" 表达式时，范围要尽量小。不要用 "org.**"、"com.**" 之类的开头，范围太大了，会影响启动速度。
#
```

> configuration、globalConfig 没有对应属性时，可用代码处理

##### Mapper 配置注意事项：

* 通过 mapper 类包名配置。 xml 与 mapper 需同包同名

```yml
mybatis.db1.mappers: "demo4035.dso.mapper"
```

* 通过 xml 目录进行配置。xml 可以固定在一个资源目录下

```yml
mybatis.db1.mappers: "classpath:mybatis/db1/*.xml"
```


#### 4、代码应用

```java
//配置数据源
@Configuration
public class Config {
    //此下的 db1 与 mybatis.db1 将对应在起来 //可以用 @Db("db1") 注入mapper
    //typed=true，表示默认数据源。@Db 可不带名字注入 
    @Bean(value = "db1", typed = true)
    public DataSource db1(@Inject("${demo.db1}") HikariDataSource ds) {
        return ds;
    }

    //@Bean(value = "db2", typed = true)
    //public DataSource db2(@Inject("${demo.db2}") HikariDataSource ds) {
    //    return ds;
    //}
    
    //调整 db1 的配置（如：增加插件）// (配置可以解决的，不需要这块代码)
    //@Bean
    //public void db1_cfg(@Db("db1") FlexConfiguration cfg,
    //                    @Db("db1") FlexGlobalConfig globalConfig) {

    //    cfg.setCacheEnabled(false);

    //}
}

//应用
@ProxyComponent
public class AppService{
    //可用 @Db 或 @Db("db1") 注入
    @Db
    AppMapper appMapper; //xml sql mapper

    //可用 @Db 或 @Db("db1")  
    @Db
    BaseMapper<App> appBaseMapper; //base mapper
    
    public void test(){
        App app1 = appMapper.getAppById(12);
        App app2 = appBaseMapper.selectOneById(12);
    }
}
```

