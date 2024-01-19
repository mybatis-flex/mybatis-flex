# 枚举属性

在某些场景下，我们希望在 Entity 中定义的属性是某一个枚举类，而非基本的数据类型，例如：

```java 7
@Table(value = "tb_account")
public class Account{

    @Id(keyType = KeyType.Auto)
    private Long id;

    private TypeEnum typeEnum;
}
```
在默认情况下，MyBatis 内置了一个名为：`EnumTypeHandler` 的处理器，用于处理这种场景。通过 `EnumTypeHandler` 处理后，数据库保存的是 `TypeEnum` 对应的属性名称，
是一个 String 类型。例如 `TypeEnum.TYPE1` 保存到数据库的内容为 `TYPE1` 这个字符串。

## @EnumValue 注解


但很多时候，我们希望保存到数据库的，是 `TypeEnum` 枚举的某个属性值，而非 `TYPE1` 字符串，那么，我就需要用到 MyBatis-Flex 提供的注解 `@EnumValue`，以下是示例代码：

```java 8,9
public enum TypeEnum {
    TYPE1(1, "类型1"),
    TYPE2(2, "类型2"),
    TYPE3(3, "类型3"),

    ;

    @EnumValue
    private int code;

    private String desc;

    TypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    //getter
}
```

```java

@AllArgsConstructor
public enum EnableEnum {
    /**
     * 启用
     */
    ENABLE(1, "启用"),
    /**
     * 禁用
     */
    DISABLE(0, "禁用"),
    ;
    private final int code;
    private final String desc;

    @EnumValue
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

```

通过注解 `@EnumValue` 为 `code` 属性标注后，当我们保存 Account 内容到数据库时，MyBatis-Flex 会自动使用 `code` 属性值进行保存，同时在读取数据库内容的时候，MyBatis-Flex 自动把数据库的值转换为
`TypeEnum` 枚举。

**注意事项**

- 1、@EnumValue 注解标识的属性，要求必须是 public 修饰，或者有 get 方法。
- 2、@EnumValue 注解标识支持在 get 方法使用注解。
- 3、枚举注解优先级。 优先取字段上的注解。如果字段没有注解，则会找方法上的注解，如果枚举类当前方法没有注解，则会去找父类的方法寻找存在注解的方法。

- 4、当配置了 @EnumValue 时，QueryWrapper 构建时，传入枚举，自动使用该值进行 SQL 参数拼接。例如：

```java
QueryWrapper query = QueryWrapper.create();

query.select().from(ACCOUNT)
.where(ACCOUNT.TYPE_ENUM.eq(TypeEnum.TYPE1))
```
其生成的 SQL 为：

```sql
select * from tb_account
where type_enum = 1
```


