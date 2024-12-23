

```xml
<dependency>
    <groupId>com.mybatis-flex</groupId>
    <artifactId>mybatis-flex-solon-plugin</artifactId>
</dependency>
```

### 1、描述

数据扩展插件，为 Solon Data 提供基于 mybatis-flex（[代码仓库](https://gitee.com/mybatis-flex/mybatis-flex)）的框架适配，以提供ORM支持。


可注入类型：

| 支持类型 | 说明                                                                           |
| -------- |------------------------------------------------------------------------------|
| Mapper.class     | 注入 Mapper。例：`@Inject UserMapper userMapper`                                  |
| FlexConfiguration     | 注入 FlexConfiguration，一般仅用于配置。例：`@Inject FlexConfiguration flexConfiguration` |
| FlexGlobalConfig     | 注入 FlexGlobalConfig，一般仅用于配置。例：`@Inject FlexGlobalConfig flexGlobalConfig`    |
| SqlSessionFactory     | 注入 SqlSessionFactory。例：`@Inject SqlSessionFactory sessionFactory` （不推荐直接使用）  |
| RowMapperInvoker | 注入 RowMapperInvoker。例：`@Inject RowMapperInvoker rowMapper`                   |


### 3、数据源配置

`mybatis-flex` 配置对应在的实体为： MybatisFlexProperties

```yml
# 配置数据源（或者使用 solon.dataSources 配置数据源，效果一样）
mybatis-flex.datasource:
  db1:
      jdbcUrl: jdbc:mysql://localhost:3306/rock?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true
      driverClassName: com.mysql.cj.jdbc.Driver
      username: root
      password: 123456

# 配置数据源对应的 mybatis 信息（要与 DataSource bean 的名字对上）
mybatis-flex:
    type-aliases-package:    #支持包名 或 类名（大写开头 或 *）//支持 ** 或 * 占位符
        - "demo4021.model"
        - "demo4021.model.*" #这个表达式同上效果
    type-handlers-package: #支持包名 或 类名（大写开头 或 *）//支持 ** 或 * 占位符
        - "demo4021.dso.mybaits.handler"
        - "demo4021.dso.mybaits.handler.*" #这个表达式同上效果
    mapper-locations:        #支持包名 或 类名（大写开头 或 *）或 xml（.xml结尾）//支持 ** 或 * 占位符
        - "demo4021.**.mapper"
        - "demo4021.**.mapper.*" #这个表达式同上效果
        - "classpath:demo4035/**/mapper.xml"
        - "classpath:demo4035/**/mapping/*.xml"
    configuration:  #扩展配置（要与 FlexConfiguration 类的属性一一对应）
        cacheEnabled: false
        mapUnderscoreToCamelCase: true
    global-config:   #全局配置（要与 FlexGlobalConfig 类的属性一一对应）//只是示例，别照抄
        printBanner: false
        keyConfig:
            keyType: "Generator"
            value: "snowFlakeId"


#
#提示：使用 "**" 表达式时，范围要尽量小。不要用 "org.**"、"com.**" 之类的开头，范围太大了，会影响启动速度。
#
```

#### Mapper 配置注意事项：

* 通过 mapper 类包名配置。 xml 与 mapper 需同包同名

```yml
mybatis-flex.mapper-locations: "demo4035.dso.mapper"
```

* 通过 xml 目录进行配置。xml 可以固定在一个资源目录下

```yml
mybatis-flex.mapper-locations: "classpath:mybatis/db1/*.xml"
```


### 4、代码应用

```java
//配置 mf （如果配置不能满足需求，可以进一步代助代码）
@Component
public class MyBatisFlexCustomizerImpl implements MyBatisFlexCustomizer, ConfigurationCustomizer {
    @Override
    public void customize(FlexGlobalConfig globalConfig) {

    }

    @Override
    public void customize(FlexConfiguration configuration) {

    }
}

//应用
@Component
public class AppService {
    @Inject
    AppMapper appMapper; //xml sql mapper

    @Inject
    BaseMapper<App> appBaseMapper; //base mapper

    public void test0() {
        App app1 = appMapper.getAppById(12);
        App app2 = appBaseMapper.selectOneById(12);
    }

    @UseDataSource("db1")
    public void test1() {
        App app1 = appMapper.getAppById(12);
        App app2 = appBaseMapper.selectOneById(12);
    }

    public void test2() {
        try {
            DataSourceKey.use("db1");
            App app1 = appMapper.getAppById(12);
            App app2 = appBaseMapper.selectOneById(12);
        } finally {
            DataSourceKey.clear();
        }
    }
}
```
