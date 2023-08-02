# 使用 MyBatis 原生功能

我们使用 MyBatis-Flex 作为 MyBatis 的增强框架进行代码开发，并不会影响原有的 MyBatis 的任何功能。

## 使用 `@Select` 等 MyBatis 原生注解

MyBatis 提供了 `@Insert` 、`@Delete` 、`@Update` 、`@Select` 4 个注解，用于对 Mapper 的方法进行配置，用于原生编写原生 SQL 进行增删改查，
在 MyBatis-Flex 我们一样可以使用这些注解。例如：

```java
public interface MyAccountMapper extends BaseMapper<Account> {

    @Select("select * from tb_account where id = #{id}")
    Account selectById(@Param("id") Object id);
}
```

`@Insert` 、`@Delete` 、`@Update` 等注解也是一样的，也就是说，原有的 MyBatis 功能如何使用，在 MyBatis-Flex 就如何使用。

`@InsertProvider`、`@DeleteProvider`、`@UpdateProvider`、`@SelectProvider` 等还是和原生 MyBatis 一样的用法。


## 使用 xml 的方式

在开始使用 xml 之前，我们需要添加如下配置，告知 mybatis 的 xml 存放路径。

```yaml
mybatis-flex:
  mapper-locations:
    - classpath*:/mapper/*.xml
```

配置完成后，我们就可以编写自己的 xml 和 mapper 代码了，如下所示:

mapper:

```java
public interface MyAccountMapper extends BaseMapper<Account> {
    Account selectByName(@Param("name") String name);
}
```

xml:
```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.test.mapper.MyAccountMapper">

    <!-- selectByName -->
    <select id="selectByName" resultType="com.test.model.Account">
        select * from `tb_account` where `user_name` = #{name}
    </select>

</mapper>
```

## XML 分页<Badge type="tip" text="^ v1.5.5" />

XML 分页是 MyBatis-Flex 在 v1.5.5 开始提供的一个 XML 分页解决方案，方便用户在使用 XML 时，对数据进行分页查询。

示例代码如下：

```java
QueryWrapper qw = QueryWrapper.create()
    .where(Account::getAge).eq(18)
    .and(Account::getId).ge(0);

Page<Account> accountPage = ccountMapper
    .xmlPaginate("selectByName", Page.of(1, 10), qw);
```

> 参数 `selectByName` 指的是在 XML 里定义的 select 节点的 id 的名称。`selectByName` 也可以全写为： `com.mybatisflex.test.mapper.AccountMapper.selectByName`。
>
> 此时，需要也在 XML 里定义名称为 `selectByName_COUNT` 的 select 节点，用于查询数据总量。

XML 代码如下：

```xml
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.mybatisflex.test.mapper.AccountMapper">

    <!-- selectByName -->
    <select id="selectByName" resultType="com.mybatisflex.test.model.Account">
        select * from `tb_account` ${qwSql} limit ${pageOffset}, ${pageSize}
    </select>

    <select id="selectByName_COUNT" resultType="long">
        select count(*) from `tb_account` ${qwSql}
    </select>

</mapper>
```

其执行的 SQL 如下：

```sql
-- 查询数据量
select count(*) from `tb_account`  WHERE `age` = 18 AND `id` >= 0

-- 查询数据
select * from `tb_account`  WHERE `age` = 18 AND `id` >= 0 limit 0, 10
```


**XML 参数解释：**

- **${qwSql}**: 传入的 QueryWrapper 生成的 where 部分的 SQL，带有 "`where`" 关键字
- **${pageOffset}**: sql offset 的值
- **${pageSize}**: 需要查询的数据量
- **${pageNumber}**: 当前的页码
- **${dbType}**: 当前用户配置的数据库类型，用户切换数据源也有可能造成数据库类型发生变化。我们可以通过不同的 dbType 来编写不同的 SQL，以适配不同的数据库类型。

**其他自定义参数：**

```java 6,7
QueryWrapper qw = QueryWrapper.create()
    .where(Account::getAge).eq(18)
    .and(Account::getId).ge(0);

//设置其他参数
Map<String,Object> otherParams = new HashMap<>();
otherParams.put("otherName", "michael");

Page<Account> accountPage = ccountMapper
    .xmlPaginate("selectByName", Page.of(1, 10), qw, otherParams);
```

此时，我们在 XML 中可以直接使用自定义的参数，例如：

```xml 8,14
<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.mybatisflex.test.mapper.AccountMapper">

    <!-- selectByName -->
    <select id="selectByName" resultType="com.mybatisflex.test.model.Account">
        select * from `tb_account` ${qwSql}
        and user_name = #{otherName}
        limit ${pageOffset}, ${pageSize}
    </select>

    <select id="selectByName_COUNT" resultType="long">
        select count(*) from `tb_account` ${qwSql}
        and user_name = #{otherName}
    </select>

</mapper>
```
