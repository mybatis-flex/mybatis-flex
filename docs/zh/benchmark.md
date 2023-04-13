# Mybatis-Flex 和同类框架「性能」对比

本文主要是展示了 Mybatis-Flex 和 Mybaits-Plus 的「性能」对比。Mybaits-Plus 是一个非常优秀 Mybaits 增强框架，
其开源于 2016 年，有很多的成功案例。

本文只阐述了「性能」方面的对比，「功能」对比请参考 [这里](./comparison.md)。

## 测试方法
使用 h2 数据库，在初始化的时候分别为 mybatis-flex 和 mybatis-plus 创建两个不同的数据库， 但是完全一样的数据结构、数据内容和数据量（每个库 2w 条数据）。

开始之前先进行预热，之后通过打印时间戳的方式进行对比，谁消耗的时间越少，则性能越高（每次测试 10 轮）。

测试源码：
[https://gitee.com/mybatis-flex/mybatis-benchmark](https://gitee.com/mybatis-flex/mybatis-benchmark)

::: tip 测试说明
> 在以下的所有测试中，有可能因为每个人的电脑性能不同，测试的结果会有所不同。
:::

## 测试单条数据查询

Mybatis-Flex 的代码如下：

```java
QueryWrapper queryWrapper = new QueryWrapper();
queryWrapper.where(FLEX_ACCOUNT.ID.ge(100)
.or(FLEX_ACCOUNT.USER_NAME.eq("admin" + ThreadLocalRandom.current().nextInt(10000))));
mapper.selectOneByQuery(queryWrapper);
```

Mybatis-Plus 的代码如下：

```java
QueryWrapper queryWrapper = new QueryWrapper();
queryWrapper.ge("id", 100);
queryWrapper.or();
queryWrapper.eq("user_name", "admin" + ThreadLocalRandom.current().nextInt(10000));
queryWrapper.last("limit 1");
mapper.selectOne(queryWrapper);
```

10 轮的测试结果：

```
---------------
>>>>>>>testFlexSelectOne:26
>>>>>>>testPlusSelectOneWithLambda:109
>>>>>>>testPlusSelectOne:119
---------------
>>>>>>>testFlexSelectOne:19
>>>>>>>testPlusSelectOneWithLambda:104
>>>>>>>testPlusSelectOne:98
---------------
>>>>>>>testFlexSelectOne:15
>>>>>>>testPlusSelectOneWithLambda:94
>>>>>>>testPlusSelectOne:95
---------------
>>>>>>>testFlexSelectOne:16
>>>>>>>testPlusSelectOneWithLambda:90
>>>>>>>testPlusSelectOne:87
---------------
>>>>>>>testFlexSelectOne:15
>>>>>>>testPlusSelectOneWithLambda:93
>>>>>>>testPlusSelectOne:55
---------------
>>>>>>>testFlexSelectOne:10
>>>>>>>testPlusSelectOneWithLambda:60
>>>>>>>testPlusSelectOne:48
---------------
>>>>>>>testFlexSelectOne:8
>>>>>>>testPlusSelectOneWithLambda:54
>>>>>>>testPlusSelectOne:51
---------------
>>>>>>>testFlexSelectOne:8
>>>>>>>testPlusSelectOneWithLambda:57
>>>>>>>testPlusSelectOne:56
---------------
>>>>>>>testFlexSelectOne:9
>>>>>>>testPlusSelectOneWithLambda:69
>>>>>>>testPlusSelectOne:55
---------------
>>>>>>>testFlexSelectOne:7
>>>>>>>testPlusSelectOneWithLambda:56
>>>>>>>testPlusSelectOne:55
```

::: tip 测试结论
> Mybatis-Flex 的查询单条数据的速度，大概是 Mybatis-Plus 的 5 ~ 8 倍。
:::

## 测试列表(List)数据查询

要求返回的数据为 10 条数据。

Mybatis-Flex 的代码如下：

```java
QueryWrapper queryWrapper = new QueryWrapper();
queryWrapper.where(FLEX_ACCOUNT.ID.ge(100).or(FLEX_ACCOUNT.USER_NAME
.eq("admin" + ThreadLocalRandom.current().nextInt(10000))))
.limit(10);
mapper.selectListByQuery(queryWrapper);
```

Mybatis-Plus 的代码如下：

```java
QueryWrapper queryWrapper = new QueryWrapper();
queryWrapper.ge("id", 100);
queryWrapper.or();
queryWrapper.eq("user_name", "admin" + ThreadLocalRandom.current().nextInt(10000));
queryWrapper.last("limit 10");
mapper.selectList(queryWrapper);
```

10 轮的测试结果：

```
---------------
>>>>>>>testFlexSelectTop10:12
>>>>>>>testPlusSelectTop10WithLambda:56
>>>>>>>testPlusSelectTop10:53
---------------
>>>>>>>testFlexSelectTop10:10
>>>>>>>testPlusSelectTop10WithLambda:57
>>>>>>>testPlusSelectTop10:56
---------------
>>>>>>>testFlexSelectTop10:9
>>>>>>>testPlusSelectTop10WithLambda:51
>>>>>>>testPlusSelectTop10:47
---------------
>>>>>>>testFlexSelectTop10:9
>>>>>>>testPlusSelectTop10WithLambda:50
>>>>>>>testPlusSelectTop10:48
---------------
>>>>>>>testFlexSelectTop10:8
>>>>>>>testPlusSelectTop10WithLambda:51
>>>>>>>testPlusSelectTop10:47
---------------
>>>>>>>testFlexSelectTop10:9
>>>>>>>testPlusSelectTop10WithLambda:50
>>>>>>>testPlusSelectTop10:47
---------------
>>>>>>>testFlexSelectTop10:8
>>>>>>>testPlusSelectTop10WithLambda:50
>>>>>>>testPlusSelectTop10:49
---------------
>>>>>>>testFlexSelectTop10:7
>>>>>>>testPlusSelectTop10WithLambda:50
>>>>>>>testPlusSelectTop10:47
---------------
>>>>>>>testFlexSelectTop10:6
>>>>>>>testPlusSelectTop10WithLambda:46
>>>>>>>testPlusSelectTop10:49
---------------
>>>>>>>testFlexSelectTop10:8
>>>>>>>testPlusSelectTop10WithLambda:48
>>>>>>>testPlusSelectTop10:77
```

::: tip 测试结论
> Mybatis-Flex 的查询 10 条数据的速度，大概是 Mybatis-Plus 的 5 倍左右。
:::

## 分页查询


Mybatis-Flex 的代码如下：

```java
QueryWrapper queryWrapper = new QueryWrapper()
    .where(FLEX_ACCOUNT.ID.ge(100))
    .or(FLEX_ACCOUNT.USER_NAME.like("admin"))
    .or(FLEX_ACCOUNT.NICKNAME.eq("Michael"))
    .or(FLEX_ACCOUNT.EMAIL.eq("michael@gmail.com"));
mapper.paginate(page, pageSize, 10000, queryWrapper);
```

Mybatis-Plus 的代码如下：

```java
LambdaQueryWrapper<PlusAccount> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.ge(PlusAccount::getId, 100);
    queryWrapper.or();
    queryWrapper.like(PlusAccount::getUserName, "admin");
    queryWrapper.or();
    queryWrapper.eq(PlusAccount::getNickname, "Michael");
    queryWrapper.or();
    queryWrapper.eq(PlusAccount::getEmail, "michael@gmail.com");
Page<PlusAccount> p = Page.of(page,pageSize,10000, false);
mapper.selectPage(p, queryWrapper);
```

10 轮的测试结果：

```
---------------
>>>>>>>testFlexPaginate:63
>>>>>>>testPlusPaginate:181
---------------
>>>>>>>testFlexPaginate:47
>>>>>>>testPlusPaginate:197
---------------
>>>>>>>testFlexPaginate:37
>>>>>>>testPlusPaginate:115
---------------
>>>>>>>testFlexPaginate:31
>>>>>>>testPlusPaginate:113
---------------
>>>>>>>testFlexPaginate:29
>>>>>>>testPlusPaginate:103
---------------
>>>>>>>testFlexPaginate:27
>>>>>>>testPlusPaginate:111
---------------
>>>>>>>testFlexPaginate:24
>>>>>>>testPlusPaginate:102
---------------
>>>>>>>testFlexPaginate:23
>>>>>>>testPlusPaginate:102
---------------
>>>>>>>testFlexPaginate:23
>>>>>>>testPlusPaginate:104
---------------
>>>>>>>testFlexPaginate:21
>>>>>>>testPlusPaginate:101
```

::: tip 测试结论
> Mybatis-Flex 的分页查询速度，大概是 Mybatis-Plus 的 3~5 倍。
:::



## 数据更新


Mybatis-Flex 的代码如下：

```java
FlexAccount flexAccount = new FlexAccount();
flexAccount.setUserName("testInsert" + i);
flexAccount.setNickname("testInsert" + i);
flexAccount.addOption("key1", "value1");
flexAccount.addOption("key2", "value2");
flexAccount.addOption("key3", "value3");
flexAccount.addOption("key4", "value4");
flexAccount.addOption("key5", "value5");

QueryWrapper queryWrapper = QueryWrapper.create()
    .where(FLEX_ACCOUNT.ID.ge(9200))
    .and(FLEX_ACCOUNT.ID.le(9300))
    .and(FLEX_ACCOUNT.USER_NAME.like("admin"))
    .and(FLEX_ACCOUNT.NICKNAME.like("admin"));

mapper.updateByQuery(flexAccount, queryWrapper);
```

Mybatis-Plus 的代码如下：

```java
PlusAccount plusAccount = new PlusAccount();
plusAccount.setUserName("testInsert" + i);
plusAccount.setNickname("testInsert" + i);
plusAccount.addOption("key1", "value1");
plusAccount.addOption("key2", "value2");
plusAccount.addOption("key3", "value3");
plusAccount.addOption("key4", "value4");
plusAccount.addOption("key5", "value5");

LambdaUpdateWrapper<PlusAccount> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.ge(PlusAccount::getId, 9000);
updateWrapper.le(PlusAccount::getId, 9100);
updateWrapper.like(PlusAccount::getUserName, "admin");
updateWrapper.like(PlusAccount::getNickname, "admin");

mapper.update(plusAccount, lambdaUpdateWrapper);
```

10 轮的测试结果：

```
---------------
>>>>>>>testFlexUpdate:11
>>>>>>>testPlusUpdate:61
---------------
>>>>>>>testFlexUpdate:10
>>>>>>>testPlusUpdate:49
---------------
>>>>>>>testFlexUpdate:6
>>>>>>>testPlusUpdate:39
---------------
>>>>>>>testFlexUpdate:5
>>>>>>>testPlusUpdate:40
---------------
>>>>>>>testFlexUpdate:5
>>>>>>>testPlusUpdate:36
---------------
>>>>>>>testFlexUpdate:5
>>>>>>>testPlusUpdate:34
---------------
>>>>>>>testFlexUpdate:6
>>>>>>>testPlusUpdate:33
---------------
>>>>>>>testFlexUpdate:4
>>>>>>>testPlusUpdate:32
---------------
>>>>>>>testFlexUpdate:4
>>>>>>>testPlusUpdate:34
---------------
>>>>>>>testFlexUpdate:5
>>>>>>>testPlusUpdate:32
```

::: tip 测试结论
> Mybatis-Flex 的数据更新速度，大概是 Mybatis-Plus 的 5~10 倍。
:::

## 更多的测试

想进一步进行更多测试的同学，可以到 [https://gitee.com/mybatis-flex/mybatis-benchmark](https://gitee.com/mybatis-flex/mybatis-benchmark)
下载源码后，添加其他方面的测试。
