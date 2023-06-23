# 数据源加密

数据源加密指的是我们对数据库的链接信息进行加密，目前 MyBatis-Flex 支持加密的内容有
- 数据源 URL
- 数据源用户名
- 数据源用户密码

通过数据源加密功能，我们可以有效的保证数据库安全，减少数据盗用风险。

## 简介

在 MyBatis-Flex 中，我们并不关注数据库信息的加密方式，换一句话也就是说：**MyBatis-Flex 支持任何类型的加密方式**。
在 MyBatis-Flex 中内置了一个名为 `DataSourceDecipher` 的接口，其作用是去解密用户配置的加密内容，从而真实的配置信息。

## 开始使用
通过 `DataSourceManager` 配置 `DataSourceDecipher`。以下是示例代码：

```java
DruidDataSource dataSource = new DruidDataSource();
dataSource.setUrl("jdbc:mysql://localhost:3306/flex_test");
dataSource.setUsername("root123"); // 真实的账号应该是 root
dataSource.setPassword("123456---0000"); // 真实的密码应该是 123456
        
//配置数据源解密器：DataSourceDecipher
DataSourceManager.setDecipher(new DataSourceDecipher() {
    @Override
    public String decrypt(DataSourceProperty property, String value) {
        //解密用户名，通过编码支持任意加密方式的解密
        if (property == DataSourceProperty.USERNAME) {
            return value.substring(0, 4);
        } 
        //解密密码
        else if (property == DataSourceProperty.PASSWORD) {
            return value.substring(0, 6);
        }
        return value;
    }
});
        
MybatisFlexBootstrap.getInstance()
        .setDataSource(dataSource)
        .addMapper(TenantAccountMapper.class)
        .start();

List<Row> rowList = Db.selectAll("tb_account");
RowUtil.printPretty(rowList);
```

> 需要注意的是：`DataSourceManager.setDecipher()` 的配置，需要在 MyBatis-Flex 初始化之前进行。


## SpringBoot 支持
在 SpringBoot 项目下，直接通过 `@Configuration` 即可使用：

```java
@Configuration
public class MyConfiguration {
    
    @Bean
    public DataSourceDecipher decipher(){
        DataSourceDecipher decipher = new ....;
        return decipher;
    }
    
}
```