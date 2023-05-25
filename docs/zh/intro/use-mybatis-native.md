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