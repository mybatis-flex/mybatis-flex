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
在默认情况下，Mybatis 内置了一个名为：`EnumTypeHandler` 的处理器，用于处理这种场景。通过 `EnumTypeHandler` 处理后，数据库保存的是 `TypeEnum` 对应的属性名称，
是一个 String 类型。例如 `TypeEnum.TYPE1` 保存到数据库的内容为 `TYPE1` 这个字符串。

## @EnumValue 注解


但很多时候，我们希望保存到数据库的，是 `TypeEnum` 枚举的某个属性值，而非 `TYPE1` 字符串，那么，我就需要用到 Mybatis-Flex 提供的注解 `@EnumValue`，以下是示例代码：

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

通过注解 `@EnumValue` 为 `code` 属性标注后，当我们保存 Account 内容到数据库时，Mybatis-Flex 会自动使用 `code` 属性值进行保存，同时在读取数据库内容的时候，Mybatis-Flex 自动把数据库的值转换为
`TypeEnum` 枚举。

::: tip 注意事项
> @EnumValue 注解标识的属性，要求必须是 public 修饰，或者有 get 方法。
:::