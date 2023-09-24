# 多租户

**多租户技术**（英语：multi-tenancy technology），是一种软件架构技术，它是在探讨与实现如何于多用户的环境下共用相同的系统或程序组件，并且仍可确保各用户间数据的隔离性。

多租户简单来说是指一个单独的实例可以为多个用户（或组织）服务。多租户技术要求所有用户共用同一个数据中心，但能提供多个客户端相同甚至可定制化的服务，并且仍然可以保障客户的数据隔离。

多租户的数据隔离有许多种方案，但最为常见的是以列进行隔离的方式。MyBatis-Flex 内置的正是通过指定的列（租户ID `tenant_id`）进行隔离的方案。

## 开始使用

MyBatis-Flex 使用多租户需要 2 个步骤：

- step 1：通过 `@Column(tenantId = true)` 标识租户列。
- step 2：为 `TenantManager` 配置 `TenantFactory`。

> TenantFactory 是用于生产租户ID的，或者说是用于获取当前租户ID的。

以下是代码示例：

```java 7
@Table("tb_article")
public class Article {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(tenantId = true)
    private Long tenantId;
}
```

> 通过 `@Column(tenantId = true)` 表示租户ID。

```java
TenantManager.setTenantFactory(new TenantFactory() {
    @Override
    public Object[] getTenantIds() {
        //通过这里返回当前租户 ID
        return new Object[]{100};
    }
});
```

## TenantFactory 说明

`TenantFactory` 接口的主要作用，适用于获取当前的租户ID，并在数据新增、删除、修改 和 查询的时候，会自动带上 TenantFactory "生产" 的数据。

`TenantFactory` 源码如下：

```java
public interface TenantFactory {
    Object[] getTenantIds();
}
```

`getTenantIds` 要求返回一个数组，原因有如下场景：

- **场景1**：租户对自己的数据进行增删改查，返回的 `Object[]` 数组只有租户自己的 ID 就可以了。
- **场景2**：租户可以对自己，以及其他租户（比如下级租户）的数据进行增删改查，那么要求返回的 `Object[]` 必须包含其他租户的 ID。比如某个数据列表，
除了显示租户自己的数据以外，还包含下级租户的数据，这种场景则要求 `getTenantIds` 返回多个值。
- **场景3**：忽略租户条件，由代码自定义条件查询，此项要求 `getTenantIds` 返回 null 或者 空数组。

**注意！注意！注意！**
> 在整个应用中，应该只有一个 `TenantFactory` 实例，然后再通过其 `getTenantIds()` 方法里去获取当前的租户 ID，在 Spring 常见中，我们可以通过在
> RequestContextHolder 中去获取当前的租户 ID。在其他框架中，我们可以通过自定义 ThreadLocal 去获取 TenantId。

## 示例代码

```java
public class MyTenantFactory implements TenantFactory {

    public Object[] getTenantIds(){
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        Long tenantId =  attributes.getAttribute("tenantId", RequestAttributes.SCOPE_REQUEST);

        return new Object[]{tenantId};
    }
}
```

当然，`MyTenantFactory` 需要正常工作，我们需要在 Spring 拦截器里，需要通过 request 去获取当前的租户 ID，并设置到 request 的 attribute，如下代码所示：

```java
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request
        , HttpServletResponse response, Object handler) throws Exception {

        //通过 request 去获取租户 ID
        Long tenantId = getTenantIdByReuqest(request);

        //设置租户ID到 request 的 attribute
        request.setAttribute("tenantId", tenantId);

        return true;
    }
}
```

同时，在 `WebMvcConfigurer` 中，通过重写 `addInterceptors` 方法添加一下我们自定义的多租户拦截器：`TenantInterceptor`。

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantInterceptor());
    }
}
```


## SpringBoot 支持
在 SpringBoot 项目下，直接通过 `@Configuration` 即可使用：

```java
@Configuration
public class MyConfiguration {
    @Bean
    public TenantFactory tenantFactory(){
        TenantFactory tenantFactory = new MyTenantFactory();
        return tenantFactory;
    }

}
```



## 忽略租户条件

在某些场景下，在增删改查等操作，我们可能需要忽略租户条件，
此时可以使用TenantManager的`withoutTenantCondition`、`ignoreTenantCondition`、`restoreTenantCondition`三个方法。

推荐使用`withoutTenantCondition`方法，该方法使用了模版方法设计模式，保障忽略 tenant 条件并执行相关逻辑后自动恢复 tenant 条件。

`withoutTenantCondition`方法实现如下：
```java
public static <T> T withoutTenantCondition(Supplier<T> supplier) {
    try {
        ignoreTenantCondition();
        return supplier.get();
    } finally {
        restoreTenantCondition();
    }
}
```
使用方法：
```java
TenantAccountMapper mapper = ...;
List<TenantAccount> tenantAccounts = TenantManager.withoutTenantCondition(mapper::selectAll);
System.out.println(tenantAccounts);
```

`ignoreTenantCondition`和`restoreTenantCondition`方法需配套使用，推荐使用`try{...}finally{...}`模式，如下例所示。
使用这两个方法可以自主控制忽略 tenant 条件和恢复 tenant 条件的时机。
当忽略 tenant 条件和恢复 tenant 条件无法放在同一个方法中时，可以使用这两个方法。
此时需要仔细处理代码分支及异常，以防止忽略 tenant 条件后未恢复 tenant 条件，导致数据异常。

```java
try {
    TenantManager.ignoreTenantCondition();

    //此处操作的数据不会带有 tenant_id 的条件
    accountMapper.selectListByQuery(...);
} finally {
    TenantManager.restoreTenantCondition();
}
```

当然，除此之外，`TenantFactory` 返回空数据，也会忽略 tenant 条件。

## 全局配置多租户字段

在 `MyBatis-Flex` 中，可以使用 `FlexGlobalConfig` 在 `MyBatis-Flex` 启动之前，指定项目中的多租户列的列名。

```java
FlexGlobalConfig.getDefaultConfig().setTenantColumn("tenant_id");
```

这样就可以省略实体类属性上的 `@Column(tenantId = true)` 注解了。

```java
public class Account {

    // @Column(tenantId = true)
    private Integer tenantId;

}
```



## 注意事项

### 新增数据时注意事项

```java 7
@Table("tb_article")
public class Article {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column(tenantId = true)
    private Long tenantId;
}
```

在以上的代码中，我们 **新增** Article 的时候，无论 `Article` 设置 `tenantId` 的值是什么，都会被 `TenantFactory` 返回的内容进行覆盖，
若 `TenantFactory` 返回多个 `tenantId`，则默认使用第一个为 `Article.tenantId` 赋值。若 `TenantFactory` 返回的内容为 null 或者 空数组，
则保留 `Article.tenantId` 设置的值。

以下是代码示例：

```java
Article article = new Article();
article.setTenantId(100);

articleMapper.insert(article);
```

- 若 `TenantFactory` 返回的有值，`tenantId` 的值为 `TenantFactory` 返回数组的第一个值。
- 若 `TenantFactory` 返回的数组为 `null` 或者 空数组，`tenantId` 的值为 `100`；

### 删除、修改和查询注意事项

当 Entity 被 `@Column(tenantId = true)` 标识租户列后，所有通过 `BaseMapper` 进行 删除、修改 和 查询，都会带上租户的条件。

比如根据 ID 删除，那么执行的 SQL 如下：

```sql
DELETE FROM tb_article where id = ? and tenant_id = ?
```
当 `TenantFactory` 返回多个租户 ID 的时候，执行的 SQL 如下：

```sql
DELETE FROM tb_article where id = ? and tenant_id in (?, ?, ?)
```

同理，修改和查询，也都会带有 `tenant_id` 条件。
