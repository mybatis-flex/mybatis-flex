# @As 注解的使用

MyBatis-Flex 提供了 `@As` 用来解决多表查询时列名重复的问题，以下是 `@As` 的代码定义：

```java
public @interface As {

    /**
     * 列的别名。
     *
     * @return 别名
     */
    String value();

}
```

## 使用示例

现在有用户表和角色表两个表，对应用户类和角色类，它们两个表的主键相同，对应类的属性也相同。假设一个用户只对应一个角色，现在要查出
id 的值为 1 的用户及其角色，并用 VO 对象返回。

由于嵌套对象也有 id 属性，所以要用 `@As` 注解起别名区分一下，因为只有两个一样的属性名，所以随便给其中一个设置别名就可以了，或者两个都设置也行。

::: tip 特别注意
如果是继承的属性，需要把 `@As` 注解放到对应的 `getter` 方法上。
:::

```java
@Table("sys_user")
public class User {
    @Id
    private Integer id;
    private String userName;
}


@Table("sys_role")
public class Role {
    @Id
    private Integer id;
    private String roleKey;
    private String roleName;
}

public class UserVO {
    @As("user_id")
    private String id;
    private String userName;
    private Role role;
}
```

这样在查询的时候使用 `AS` 关键字指定列的别名就可以了。

```java
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testAsAnnotation() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(USER.USER_ID.as("user_id"), // 注意别名一定要和配置的一模一样
                        USER.USER_NAME, 
                        ROLE.ALL_COLUMNS)
                .from(USER.as("u"))
                .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
                .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID))
                .where(USER.USER_ID.eq(1));
        System.out.println(queryWrapper.toSQL());
        UserVO userVO = userMapper.selectOneByQueryAs(queryWrapper, UserVO.class);
        System.err.println(userVO);
    }

}
```
