# 顶级 Service 接口

MyBatis-Flex 提供了一个名为 `IService` 的接口，及其默认实现类 `ServiceImpl` ，用于简化在 「Service」 层重复定义 「Mapper」 层的方法。

> `IService` 接口只是提供了简单且常用的 “增删改查” 方法，更多细节以及复杂的业务，还是需要使用 `Mapper` 进行处理。


## 示例代码

接口：

```java
public interface IAccountService extends IService<Account> {
    //你的自定义方法
    List<Account> customMethod();
}
```

实现类：

```java
@Component
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> 
        implements IAccountService {
    
    @Override
    public List<Account> customMethod() {
       // 返回 id >= 100 的数据
       return list(ACCOUNT.ID.ge(100));
    }
    
}
```

## 保存数据

`IService` 的接口提供了 save、saveOrUpdate、saveBatch 方法，用于保存数据：

- **save(entity)**：保存一条数据，忽略 null 值的字段。
- **saveOrUpdate(entity)**：保存一条数据，如果数据存在则更新数据。
- **saveBatch(entities)**：批量保存多条数据。
- **saveBatch(entities, size)**：批量保存多条数据，按指定数量切分。


## 删除数据

`IService` 的接口提供了 remove、removeById、removeByIds、removeByMap 方法，用于删除数据：

- **remove(query)**：根据 `QueryWrapper` 构建的条件来删除数据。
- **remove(condition)**：根据 `QueryCondition` 构建的条件来删除数据。
- **removeById(id)**：根据主键删除数据，复合主键需要传入一个数组。
- **removeById(entity)**：根据实体主键删除数据，相比removeById(id)，此方法更便于对复合主键实体类的删除。。
- **removeByIds(ids)**：根据主键的集合，批量删除多条数据。
- **removeByMap(map)**：根据 `Map<字段名，值>` 组成的条件删除数据，字段名和值的关系为相等的关系；同时，防止 "不小心" 全表删除数据，Map 的值不允许为 null 或者空数据。


## 更新数据

`IService` 的接口提供了 update、updateById、updateBatch 方法，用于更新数据：

- **updateById(entity)**：根据主键更新数据，要求主键值不能为空，否则会抛出异常；同时，数据为 null 的字段不会更新到数据库。
- **updateById(entity, ignoreNulls)**：根据主键更新数据，要求主键值不能为空，否则会抛出异常；同时，设置是否将数据为 null 的字段更新到数据库。
- **update(entity, map)**：根据 `Map<字段名，值>` 组成的条件更新数据，实体类可以没有主键（如果有也会被忽略），实体类的 null 属性，会自动被忽略。
- **update(entity, query)**：根据 `QueryWrapper` 构建的条件更新数据，实体类可以没有主键（如果有也会被忽略），实体类的 null 属性，会自动被忽略。
- **update(entity, condition)**：根据 `QueryCondition` 构建的条件更新数据，实体类可以没有主键（如果有也会被忽略），实体类的 null 属性，会自动被忽略。
- **updateBatch(entities)**：批量保存多条数据，要求主键值不能为空，否则会抛出异常；同时，数据为 null 的字段不会更新到数据库。
- **updateBatch(entities, size)**：批量保存多条数据，按指定数量切分，要求主键值不能为空，否则会抛出异常；同时，数据为 null 的字段不会更新到数据库。。


## 查询数据

### 查询一条数据

`IService` 的接口提供了 getById、getByIdOpt、getOne、getOneOpt、getOneAs、getOneAsOpt 方法，用于查询一条数据：

- **getById(id)**：根据主键查询数据。
- **getByIdOpt(id)**：根据主键查询数据，并封装为 `Optional` 返回。
- **getOneByEntityId(entity)**：根据实体主键查询数据。
- **getByEntityIdOpt(entity)**：根据实体主键查询数据，并封装为 `Optional` 返回。
- **getOne(query)**: 根据 `QueryWrapper` 构建的条件查询一条数据。
- **getOne(condition)**: 根据 `QueryCondition` 构建的条件查询一条数据。
- **getOneOpt(query)**: 根据`QueryWrapper` 构建的条件查询一条数据，并封装为 `Optional` 返回。
- **getOneOpt(condition)**: 根据 `QueryCondition` 构建的条件查询一条数据，并封装为 `Optional` 返回。
- **getOneAs(query, asType)**: 根据 `QueryWrapper` 构建的条件查询一条数据，并通过 asType 进行接收。
- **getOneAsOpt(query, asType)**: 根据`QueryWrapper` 构建的条件查询一条数据，并通过 asType 进行接收，封装为 `Optional` 返回。

### 查询多条数据

`IService` 的接口提供了 list、listAs、listByIds、listByMap 方法，用于查询多条数据：

- **list()**：查询所有数据。
- **list(query)**：根据 `QueryCondition` 或 `QueryWrapper` 构建的条件查询多条数据。
- **list(condition)**：根据 `QueryCondition` 构建的条件查询多条数据。
- **listAs(query, asType)**：根据 `QueryWrapper` 构建的条件查询多条数据，并通过 asType 进行接收。
- **listByIds(ids)**：根据主键的集合查询多条数据。
- **listByMap(map)**：根据 `Map<字段名，值>` 组成的条件查询多条数据。

### 查询数据数量

`IService` 的接口提供了 exists、count 方法，用于查询数据数量；

- **count()**：查询所有数据数量。
- **count(query)**：根据 `QueryWrapper` 构建的条件查询数据数量。
- **count(condition)**：根据 `QueryCondition` 构建的条件查询数据数量。
- **exist(query)**：根据 `QueryWrapper` 构建的条件判断数据是否存在。
- **exist(condition)**：根据 `QueryCondition` 构建的条件判断数据是否存在。

### 分页查询数据

`IService` 的接口提供了 page、pageAs 方法，用于分页查询数据：

- **page(page)**：分页查询所有数据。
- **page(page, query)**：根据 `QueryWrapper` 构建的条件分页查询数据。
- **page(page, condition)**：根据 `QueryCondition` 构建的条件分页查询数据。
- **pageAs(page, query, asType)**：根据 `QueryWrapper` 构建的条件分页查询数据，并通过 asType 进行接收。

## 其他方法

- **getMapper()**：获取对应的 `BaseMapper` 接口。
- **query()**：获取默认的 `QueryWrapper` 类。