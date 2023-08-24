# MyBatis-Flex 支持的数据库类型

MyBatis-Flex 支持的数据库类型，如下表格所示，我们还可以通过自定义方言的方式，持续添加更多的数据库支持。


| 数据库           | 描述                |
|---------------|-------------------|
| mysql         | MySQL 数据库         |
| mariadb       | MariaDB 数据库       |
| oracle        | Oracle11g 及以下数据库  |
| oracle12c     | Oracle12c 及以上数据库  |
| db2           | DB2 数据库           |
| H2            | H2 数据库            |
| hsql          | HSQL 数据库          |
| sqlite        | SQLite 数据库        |
| postgresql    | PostgreSQL 数据库    |
| sqlserver2005 | SQLServer2005 数据库 |
| sqlserver     | SQLServer 数据库     |
| dm            | 达梦数据库             |
| xugu          | 虚谷数据库             |
| kingbasees    | 人大金仓数据库           |
| phoenix       | Phoenix HBase 数据库 |
| gauss         | Gauss 数据库         |
| clickhouse    | ClickHouse 数据库    |
| gbase         | 南大通用(华库)数据库       |
| gbase-8s      | 南大通用数据库 GBase 8s  |
| oscar         | 神通数据库             |
| sybase        | Sybase ASE 数据库    |
| OceanBase     | OceanBase 数据库     |
| Firebird      | Firebird 数据库      |
| derby         | Derby 数据库         |
| highgo        | 瀚高数据库             |
| cubrid        | CUBRID 数据库        |
| goldilocks    | GOLDILOCKS 数据库    |
| csiidb        | CSIIDB 数据库        |
| hana          | SAP_HANA 数据库      |
| impala        | Impala 数据库        |
| vertica       | Vertica 数据库       |
| xcloud        | 行云数据库             |
| redshift      | 亚马逊 redshift 数据库  |
| openGauss     | 华为 openGauss 数据库  |
| TDengine      | TDengine 数据库      |
| informix      | Informix 数据库      |
| greenplum     | Greenplum 数据库     |
| uxdb          | 优炫数据库             |
| Doris         | Doris数据库          |
| Hive SQL      | Hive 数据库          |
| lealone       | Lealone 数据库       |
| sinodb        | 星瑞格数据库            |


## 数据库方言

在某些场景下，比如用户要实现自己的 SQL 生成逻辑时，我们可以通过实现自己的方言达到这个目的，实现方言分为两个步骤：

- 1、编写自己的方言类，实现 `IDialect` 接口
- 2、通过 `DialectFactory.registerDialect()` 方法注册自己的方言
