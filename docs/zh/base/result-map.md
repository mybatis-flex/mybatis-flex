# 结果映射

很多情况下，我们都会遇到 `一对一`、`一对多`、`多对一`、`多对多` 的场景，通常会用到连表查询，如果是原生 MyBatis 的话，可以使用 `<resultMap>` 标签来构建结果映射，在 MyBatis-Flex 中提供了自动结果映射功能，这样您就可以只关注于 SQL 语句的构建。

## 代码示例

这里以用户和角色的 `多对多` 关系作为例子，首先有用户表和角色表，分别对应着用户类和角色类：

```java
@Table("sys_user")
public class User {

    @Id
    private Integer userId;
    private String userName;
    
}

@Table("sys_role")
public class Role {

    @Id
    private Integer roleId;
    private String roleKey;
    private String roleName;

}
```

现在需要查询所有用户，以及每个用户对应的角色信息，并通过 UserVO 对象返回：

```java
public class UserVO {

    private String userId;
    private String userName;
    private List<Role> roleList;

}
```

这个操作只需要连表查询即可完成，对于连表查询的结果映射，MyBatis-Flex 会自动帮您完成：

```java
@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelectList() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(USER.USER_ID, USER.USER_NAME, ROLE.ALL_COLUMNS)
                .from(USER.as("u"))
                .leftJoin(USER_ROLE).as("ur").on(USER_ROLE.USER_ID.eq(USER.USER_ID))
                .leftJoin(ROLE).as("r").on(USER_ROLE.ROLE_ID.eq(ROLE.ROLE_ID));
        List<UserVO> userVOS = userMapper.selectListByQueryAs(queryWrapper, UserVO.class);
        userVOS.forEach(System.err::println);
    }

}
```

构建的连表查询 SQL 语句为：

```sql
SELECT `u`.`user_id`,
       `u`.`user_name`,
       `r`.*
FROM `sys_user` AS `u`
LEFT JOIN `sys_user_role` AS `ur` ON `ur`.`user_id` = `u`.`user_id`
LEFT JOIN `sys_role` AS `r` ON `ur`.`role_id` = `r`.`role_id`;
```

最终自动映射的结果为：

```text
UserVO{userId='1', userName='admin', roleList=[Role{roleId=1, roleKey='admin', roleName='超级管理员'}]}
UserVO{userId='2', userName='ry', roleList=[Role{roleId=2, roleKey='common', roleName='普通角色'}]}
UserVO{userId='3', userName='test', roleList=[Role{roleId=1, roleKey='admin', roleName='超级管理员'}, Role{roleId=2, roleKey='common', roleName='普通角色'}]}
```