# MyBatis-Flex 和同类框架「性能」对比

本文主要是展示了 MyBatis-Flex 和 Mybaits-Plus 的「性能」对比。Mybaits-Plus 是一个非常优秀 Mybaits 增强框架，
其开源于 2016 年，有很多的成功案例。

本文只阐述了「性能」方面的对比，「功能」对比请参考 [这里](./comparison.md)。

## 测试方法
使用 h2 数据库，在初始化的时候分别为 mybatis-flex 和 mybatis-plus 创建两个不同的数据库， 但是完全一样的数据结构、数据内容和数据量（每个库 2w 条数据）。

开始之前先进行预热，之后通过打印时间戳的方式进行对比，谁消耗的时间越少，则性能越高（每次测试 10 轮）。

测试源码：
[https://gitee.com/mybatis-flex/mybatis-benchmark](https://gitee.com/mybatis-flex/mybatis-benchmark)

::: tip 测试说明
在以下的所有测试中，有可能因为每个人的电脑性能不同，测试的结果会有所不同。
:::

## 测试单条数据查询

MyBatis-Flex 的代码如下：

```java
QueryWrapper queryWrapper = new QueryWrapper();
queryWrapper.where(FLEX_ACCOUNT.ID.ge(100)
.or(FLEX_ACCOUNT.USER_NAME.eq("admin" + ThreadLocalRandom.current().nextInt(10000))));
mapper.selectOneByQuery(queryWrapper);
```

MyBatis-Plus 的代码如下：

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
>>>>>>>testFlexSelectOne:134
>>>>>>>testPlusSelectOneWithLambda:989
>>>>>>>testPlusSelectOne:830
---------------
>>>>>>>testFlexSelectOne:75
>>>>>>>testPlusSelectOneWithLambda:732
>>>>>>>testPlusSelectOne:795
---------------
>>>>>>>testFlexSelectOne:65
>>>>>>>testPlusSelectOneWithLambda:938
>>>>>>>testPlusSelectOne:714
---------------
>>>>>>>testFlexSelectOne:105
>>>>>>>testPlusSelectOneWithLambda:740
>>>>>>>testPlusSelectOne:669
---------------
>>>>>>>testFlexSelectOne:57
>>>>>>>testPlusSelectOneWithLambda:691
>>>>>>>testPlusSelectOne:773
---------------
>>>>>>>testFlexSelectOne:65
>>>>>>>testPlusSelectOneWithLambda:693
>>>>>>>testPlusSelectOne:695
---------------
>>>>>>>testFlexSelectOne:56
>>>>>>>testPlusSelectOneWithLambda:754
>>>>>>>testPlusSelectOne:665
---------------
>>>>>>>testFlexSelectOne:56
>>>>>>>testPlusSelectOneWithLambda:714
>>>>>>>testPlusSelectOne:717
---------------
>>>>>>>testFlexSelectOne:57
>>>>>>>testPlusSelectOneWithLambda:696
>>>>>>>testPlusSelectOne:671
---------------
>>>>>>>testFlexSelectOne:59
>>>>>>>testPlusSelectOneWithLambda:739
>>>>>>>testPlusSelectOne:659
```

::: tip 测试结论
> MyBatis-Flex 的查询单条数据的速度，大概是 MyBatis-Plus 的 5 ~ 10+ 倍。
:::

## 测试列表(List)数据查询

要求返回的数据为 10 条数据。

MyBatis-Flex 的代码如下：

```java
QueryWrapper queryWrapper = new QueryWrapper();
queryWrapper.where(FLEX_ACCOUNT.ID.ge(100).or(FLEX_ACCOUNT.USER_NAME
.eq("admin" + ThreadLocalRandom.current().nextInt(10000))))
.limit(10);
mapper.selectListByQuery(queryWrapper);
```

MyBatis-Plus 的代码如下：

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
>>>>>>>testFlexSelectTop10:90
>>>>>>>testPlusSelectTop10WithLambda:743
>>>>>>>testPlusSelectTop10:678
---------------
>>>>>>>testFlexSelectTop10:85
>>>>>>>testPlusSelectTop10WithLambda:692
>>>>>>>testPlusSelectTop10:684
---------------
>>>>>>>testFlexSelectTop10:84
>>>>>>>testPlusSelectTop10WithLambda:692
>>>>>>>testPlusSelectTop10:670
---------------
>>>>>>>testFlexSelectTop10:85
>>>>>>>testPlusSelectTop10WithLambda:737
>>>>>>>testPlusSelectTop10:667
---------------
>>>>>>>testFlexSelectTop10:85
>>>>>>>testPlusSelectTop10WithLambda:691
>>>>>>>testPlusSelectTop10:684
---------------
>>>>>>>testFlexSelectTop10:97
>>>>>>>testPlusSelectTop10WithLambda:760
>>>>>>>testPlusSelectTop10:666
---------------
>>>>>>>testFlexSelectTop10:80
>>>>>>>testPlusSelectTop10WithLambda:673
>>>>>>>testPlusSelectTop10:637
---------------
>>>>>>>testFlexSelectTop10:81
>>>>>>>testPlusSelectTop10WithLambda:653
>>>>>>>testPlusSelectTop10:639
---------------
>>>>>>>testFlexSelectTop10:82
>>>>>>>testPlusSelectTop10WithLambda:659
>>>>>>>testPlusSelectTop10:636
---------------
>>>>>>>testFlexSelectTop10:81
>>>>>>>testPlusSelectTop10WithLambda:654
>>>>>>>testPlusSelectTop10:656
```

::: tip 测试结论
MyBatis-Flex 的查询 10 条数据的速度，大概是 MyBatis-Plus 的 5~10 倍左右。
:::

## 分页查询


MyBatis-Flex 的代码如下：

```java
QueryWrapper queryWrapper = new QueryWrapper()
    .where(FLEX_ACCOUNT.ID.ge(100));
mapper.paginate(page, pageSize, 20000, queryWrapper);
```

MyBatis-Plus 的代码如下：

```java
LambdaQueryWrapper<PlusAccount> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.ge(PlusAccount::getId, 100);
    queryWrapper.eq(PlusAccount::getEmail, "michael@gmail.com");
Page<PlusAccount> p = Page.of(page, pageSize, 20000, false);
mapper.selectPage(p, queryWrapper);
```

10 轮的测试结果：

```
---------------
>>>>>>>testFlexPaginate:90
>>>>>>>testPlusPaginate:671
---------------
>>>>>>>testFlexPaginate:78
>>>>>>>testPlusPaginate:643
---------------
>>>>>>>testFlexPaginate:80
>>>>>>>testPlusPaginate:638
---------------
>>>>>>>testFlexPaginate:79
>>>>>>>testPlusPaginate:613
---------------
>>>>>>>testFlexPaginate:75
>>>>>>>testPlusPaginate:627
---------------
>>>>>>>testFlexPaginate:72
>>>>>>>testPlusPaginate:606
---------------
>>>>>>>testFlexPaginate:69
>>>>>>>testPlusPaginate:585
---------------
>>>>>>>testFlexPaginate:70
>>>>>>>testPlusPaginate:589
---------------
>>>>>>>testFlexPaginate:69
>>>>>>>testPlusPaginate:586
---------------
>>>>>>>testFlexPaginate:68
>>>>>>>testPlusPaginate:585
```

::: tip 测试结论
Mybatis-Flex 的分页查询速度，大概是 Mybatis-Plus 的 5~10 倍左右。
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
>>>>>>>testFlexUpdate:58
>>>>>>>testPlusUpdate:524
---------------
>>>>>>>testFlexUpdate:51
>>>>>>>testPlusUpdate:503
---------------
>>>>>>>testFlexUpdate:49
>>>>>>>testPlusUpdate:490
---------------
>>>>>>>testFlexUpdate:45
>>>>>>>testPlusUpdate:472
---------------
>>>>>>>testFlexUpdate:48
>>>>>>>testPlusUpdate:470
---------------
>>>>>>>testFlexUpdate:44
>>>>>>>testPlusUpdate:460
---------------
>>>>>>>testFlexUpdate:43
>>>>>>>testPlusUpdate:459
---------------
>>>>>>>testFlexUpdate:44
>>>>>>>testPlusUpdate:461
---------------
>>>>>>>testFlexUpdate:40
>>>>>>>testPlusUpdate:444
---------------
>>>>>>>testFlexUpdate:41
>>>>>>>testPlusUpdate:444
```

::: tip 测试结论
Mybatis-Flex 的数据更新速度，大概是 Mybatis-Plus 的 5~10+ 倍。
:::

## 更多的测试

想进一步进行更多测试的同学，可以到 [https://gitee.com/mybatis-flex/mybatis-benchmark](https://gitee.com/mybatis-flex/mybatis-benchmark)
下载源码后，添加其他方面的测试。
