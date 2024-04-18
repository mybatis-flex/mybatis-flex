# 数据缓存

MyBatis-Flex 是一个 MyBatis 增强框架，所以您可以使用 MyBatis 提供的二级缓存来作为数据缓存。但是它仍然有很多的缺点，比如不适用于分布式环境，在这里推荐使用 [Spring Cache](https://docs.spring.io/spring-framework/docs/5.2.24.RELEASE/spring-framework-reference/integration.html#cache) 模块来处理数据缓存。

## 使用方法

因为要用到 Spring Cache 模块，所以您的项目必须要使用 Spring Framework 框架，这里以 Spring Boot 项目作为例子，实现 MyBatis-Flex 项目将缓存数据存入 Redis 组件中。

1、引入 `spring-boot-starter-cache` 和 `spring-boot-starter-data-redis`模块

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
</dependencies>
```

2、设置 Redis 连接信息（全是默认的话可以跳过这步）

```yaml
spring:
  redis:
    port: 6379
    host: localhost
```

3、在 Spring Boot 配置类上启用 Spring Cache 缓存

```java
@EnableCaching
@Configuration
public class CacheConfig {
}
```

4、将 ServiceImpl 默认实现类换为 CacheableServiceImpl 实现类

```java
public interface AccountService extends IService<Account> {
}

@Service
public class AccountServiceImpl extends CacheableServiceImpl<AccountMapper, Account> implements AccountService {
}
```

5、最后即可使用 Spring Cache 的相关注解实现数据缓存到 Redis 中了

```java
// 设置统一的缓存名称
@Service
@CacheConfig(cacheNames = "account")
public class AccountServiceImpl extends CacheableServiceImpl<AccountMapper, Account> implements AccountService {

    // 根据主键缓存数据
    @Override
    @Cacheable(key = "#id")
    public Account getById(Serializable id) {
        return super.getById(id);
    }

    // 根据方法名加查询 SQL 语句缓存结果数据
    // 加上方法名是为了避免不同的方法使用一样的 QueryWrapper
    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<Account> list(QueryWrapper query) {
        return super.list(query);
    }

}
```

## 使用说明

MyBatis-Flex 在 IService 接口中做了方法调用链优化，所以您只需将缓存注解加到一些特定的方法上，即可实现所有相关的方法也可以进行数据缓存。完整的缓存方法见如下示例：

```java
@Service
@CacheConfig(cacheNames = "account")
public class AccountServiceImpl extends CacheableServiceImpl<MyAccountMapper, Account> {

    @Override
    @CacheEvict(allEntries = true)
    public boolean remove(QueryWrapper query) {
        return super.remove(query);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

    // 根据查询条件更新时，实体类主键可能为 null。
    @Override
    @CacheEvict(allEntries = true)
    public boolean update(Account entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Account entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Account> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Account getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Account getOne(QueryWrapper query) {
        return super.getOne(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> R getOneAs(QueryWrapper query, Class<R> asType) {
        return super.getOneAs(query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<Account> list(QueryWrapper query) {
        return super.list(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        return super.listAs(query, asType);
    }

    // 无法通过注解进行缓存操作
    @Override
    @Deprecated
    public List<Account> listByIds(Collection<? extends Serializable> ids) {
        return super.listByIds(ids);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public long count(QueryWrapper query) {
        return super.count(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #page.getPageSize() + ':' + #page.getPageNumber() + ':' + #query.toSQL()")
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        return super.pageAs(page, query, asType);
    }

}
```

如果您有非常多的缓存实现类，并且需要使用全部的缓存方法，可以使用 [代码生成器](../others/codegen.md) 辅助生成，设置如下代码即可：

```java
// 使用代码风格 1 生成
globalConfig.setServiceImplGenerateEnable(true);
globalConfig.setServiceImplSuperClass(CacheableServiceImpl.class);
globalConfig.setServiceImplCacheExample(true);

// 或者使用代码风格 2 生成
globleConfig.enableServiceImpl()
        .setSuperClass(CacheableServiceImpl.class)
        .setCacheExample(true);
```
