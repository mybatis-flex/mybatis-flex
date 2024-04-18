# 数据脱敏

## 数据脱敏是什么

随着《网络安全法》的颁布施行，对个人隐私数据的保护已经上升到法律层面。 数据脱敏是指对某些敏感信息通过脱敏规则进行数据的变形，
实现敏感隐私数据的可靠保护。在涉及客户安全数据或者一些商业性敏感数据的情况下，在不违反系统规则条件下，对真实数据进行改造并提供使用，
如身份证号、手机号、卡号、客户号等个人信息都需要进行数据脱敏。

## @ColumnMask

MyBatis-Flex 提供了 `@ColumnMask()` 注解，以及内置的9种脱敏规则，帮助开发者方便的进行数据脱敏。例如：

```java
@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @ColumnMask(Masks.CHINESE_NAME)
    private String userName;
}
```

以上的示例中，使用了 `CHINESE_NAME` 的脱敏规则，其主要用于处理 "中文名字" 的场景。当我们查询到 userName 为 `张三丰` 的时候，其内容自动被处理成 `张**`。

除此之外，MyBatis-Flex 还提供了如下的8种脱敏规则（共9种），方便开发者直接使用：

- 手机号脱敏
- 固定电话脱敏
- 身份证号脱敏
- 车牌号脱敏
- 地址脱敏
- 邮件脱敏
- 密码脱敏
- 银行卡号脱敏

## 自定义脱敏规则

当 Mybaits-Flex 内置的9种脱敏规则无法满足要求时，我们还可以自定义脱敏规则，其步骤如下：

1、通过 `MaskManager` 注册新的脱敏规则：

```java
MaskManager.registerMaskProcessor("自定义规则名称"
        , data -> {
            return data;
        })
```

2、使用自定义的脱敏规则
```java
@Table("tb_account")
public class Account {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @ColumnMask("自定义规则名称")
    private String userName;
}
```

## 取消脱敏处理

在某些场景下，程序希望查询得到的数据是原始数据，而非脱敏数据。比如要去查询用户的手机号，然后给用户发送短信。又或者说，我们进入编辑页面编辑用户数据，
如果编辑页面展示的是脱敏数据，然后再次点击保存，那么数据库的真实数据也会被脱敏覆盖。

因此，MaskManager 提供了 `execWithoutMask`、`skipMask`、`restoreMask` 三个方法来处理这种场景：

推荐使用`execWithoutMask`方法，该方法使用了模版方法设计模式，保障跳过脱敏处理并执行相关逻辑后自动恢复脱敏处理。

`execWithoutMask`方法实现如下：

```java
public static <T> T execWithoutMask(Supplier<T> supplier) {
    try {
        skipMask();
        return supplier.get();
    } finally {
        restoreMask();
    }
}
```

使用方法：

```java
AccountMapper mapper = ...;
List<Account> accounts = MaskManager.execWithoutMask(mapper::selectAll);
System.out.println(accounts);
```

`skipMask`和`restoreMask`方法需配套使用，推荐使用`try{...}finally{...}`模式，如下例所示。
使用这两个方法可以自主控制跳过脱敏处理和恢复脱敏处理的时机。
当跳过脱敏处理和恢复脱敏处理无法放在同一个方法中时，可以使用这两个方法。
此时需要仔细处理代码分支及异常，以防止跳过脱敏处理后未恢复脱敏处理，导致安全隐患。

```java
try {
    MaskManager.skipMask();

    //此处查询到的数据不会进行脱敏处理
    accountMapper.selectListByQuery(...);
} finally {
    MaskManager.restoreMask();
}
```

::: tip 提示
在具体的应用中，我们通常会把`execWithoutMask`、`skipMask()` 和 `restoreMask()` 放到统一的拦截器里，对某一类业务进行统一拦截和处理。
:::
