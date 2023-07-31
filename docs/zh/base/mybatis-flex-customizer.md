# MyBatisFlexCustomizer

`MyBatisFlexCustomizer` 是 MyBatis-Flex 为了方便 `SpringBoot` 用户对 MyBatis-Flex 进行初始化而产生的接口。

通过在 `@Configuration` 去实现 `MyBatisFlexCustomizer` 接口，我们可以对 MyBatis-Flex 进行一系列的初始化配置。这些配置可能包含如下的内容：

- 1、FlexGlobalConfig 的全局配置
- 2、自定义主键生成器
- 3、多租户配置
- 4、动态表名配置
- 5、逻辑删除处理器配置
- 6、自定义脱敏规则
- 7、SQL 审计配置
- 8、SQL 打印配置
- 9、数据源解密器配置
- 10、自定义数据方言配置
- 11、...

## 代码示例

```java

@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig globalConfig) {
        //我们可以在这里进行一些列的初始化配置
    }

}
```