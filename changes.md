# MyBatis-Flex ChangeLog

查看 [全部代码贡献者](/zh/intro/what-is-mybatisflex.html#贡献者)。

## v1.11.5 20251224
* 新增：新增 `mybatis-flex-spring-boot4-starter` 模块，支持 Spring Boot 4
* 新增：Query 模块新增 `OrderType` 获取方法，感谢 @ruansheng8
* 新增：Core 模块新增 `getQueryColumn` 方法，感谢 @ruansheng8
* 优化：统一 Core 模块的代码风格（`-m core`），感谢 @ruansheng8
* 优化：删除语句中单主键条件的 SQL 拼接逻辑优化，感谢 @AXBest
* 修复：带子查询场景下，分页优化误删 JOIN 的问题，感谢 @cybzzz
* 修复：Base Entity 包路径替换逻辑不正确的问题，感谢 @LucasC
* 修复：`mybatis-flex-test/mybatis-flex-seata-test` 模块中 `${project.version}` 缺少右大括号导致的 Maven 依赖错误
* 调整：`mybatis-flex-solon-plugin` 中，当配置了 `mapperLocations` 但未注册 Mapper 时，日志级别由异常调整为 warn，感谢 @noear
* 构建：新增 `mybatis-spring` 依赖，并添加 `mvn17.sh` 构建脚本
* 构建：升级 Spring Boot 4 至 `v4.0.0`
* 构建：移除未使用的 `spring-boot4` 版本属性
* 构建：回滚 `maven-gpg-plugin` 的相关配置
* 构建：在 `mybatis-flex-dependencies` 模块中引入 `mybatis-flex-spring-boot4-starter`


## v1.11.4 20251113
- 修复：修复 EXISTS、NOT EXISTS 的子 select 语句无法自动附加逻辑删除的问题，感谢 @Arowa_Z
- 修复：@EnumValue 应用在接口方法且返回类型为泛型时失效的问题，感谢 @CShisan
- 修复：人大金仓方言类型判断不准确的问题，感谢 @younger
- 修复：RowKeyGenerator 的 autoKeyGeneratorNames 在某些情况下累积导致缓存污染的问题
- 文档：修正示例代码中的语法错误，感谢 @涛声依旧



## v1.11.3 20250926
- 修复：QueryColumn.between_(values) 不能为 null 的问题 #ID03CH



## v1.11.2 20250926
- 新增：添加 Assert 方便在执行 update 的时候进行断言
- 新增：添加新函数并增强数据库类型判断能力，感谢 @fangzhengjin
- 新增：添加 stringAgg 和 listAgg 函数的重载方法，感谢 @fangzhengjin
- 新增：添加新函数并增强数据库类型判断能力，感谢 @fangzhengjin
- 新增：AuditMessage 添加 stmtId ，感谢 @pbnoyz
- 优化：bom 框架版本统一管理，感谢 @ruansheng8
- 优化：bom 同步最新版本依赖，感谢 @ruansheng8
- 优化：优化关联查询使用逗号分割时排除空值，感谢 @ruansheng8
- 修复：修复自动关联查询功能：表信息查找根据Schema+表名，感谢 @ruansheng8
- 文档：更新相关文档 docs/zh/core/id.md，感谢 @java-coding



## v1.11.1 20250725
- 新增：添加数据源缺失处理器，方便项目启动后，再通过代码添加数据源
- 新增：between 条件遇到 一个参数为 null 时自动转换成 LE 或 GE 逻辑 #ICKPDB
- 新增：添加对 Spring Batch 的支持
- 修复：动态添加数据源第二次切换数据源无效的问题 #ICLQQ3
- 修复：Datasource lambda 的 orElse 是方法的话，会在执行 lambda 之前堆栈就调用了，那么 Optional 的逻辑就失去作用了
- 测试：添加对 Spring Batch 的单元测试



## v1.11.0 20250713
- 新增：代码生成器 Column 支持带有范型的类型，感谢 @benshi
- 新增：对于用户常用类，启用代码折叠支持，感谢 @hewei
- 新增：Column 添加 setProperty 方法的支持，感谢 @benshi
- 新增：增加 GBASE_8C、GBASE_8S_PG、GOLDENDB、SUNDB、VASTBASE、YASDB、PRESTO 驱动识别，感谢 @fangzhengjin
- 新增：添加 loveqq-framework 启动器，感谢 @kfyty725
- 优化：优化 `>=` 逻辑和 `<=` 逻辑保持一致，感谢 @fyh
- 优化：调整 sqlserver 2008 版本的 DbType 为 SQLSERVER_2005，修复分页语句不支持 offset 问题，感谢 @all-around-badass
- 修复：高斯数据库调整为无反义处理，避免严格大小写处理，感谢 @all-around-badass
- 修复：在 join 多次相同的表时, 构建租户条件没能取到正确的别名，感谢 @gzkemays
- 修复：在使用 UpdateChain 更新实体类字段时偶发 ClassCastException 异常问题，感谢 @iminifly
- 修复：多环境导入相同 Mapper 冲突问题，感谢 @wcc1433
- 文档：更新代码生成器的相关文档



## v1.10.9 20250319
- 新增: 代码生成器支持配置 entity 类 lombok 注解生成 (@NoArgsConstructor, @AllArgsConstructor)，感谢 @coder-xiaomo
- 新增：增加全局忽略 schema 配置的支持，感谢 @cui
- 修复：MultiEntityKeyGenerator 不支持 set 设置的问题，感谢 @zhb
- 修复：exists 条件中的表别名和父查询保持一致的问题，感谢 @cybzzz
- 修复：修复在 diaelct 中去调用 Db 工具时可能出现类型转换错误的问题



## v1.10.8 20250217
- 新增：代码生成器 GlobalConfig 添加 setEntityWithBaseClassEnable 设置，感谢 @coder-xiaomo
- 新增：代码生成器生成 entity base 时支持设置是否覆盖已有文件，感谢 @coder-xiaomo
- 新增：QueryWrapper 支持常量查询，感谢 @weichangming
- 优化：代码生成器 setAuthor 和 setSince 方法传入空字符串时，不添加注释 @author, @since 部分，感谢 @coder-xiaomo
- 修复：在不使用 mybatis-flex-processor 模块时，会出现 StrUtil 找不到的问题，感谢 @codetangxin
- 修复：TenantFactory.withoutTenantCondition 在修改时无效的问题，感谢 @codetangxin



## v1.10.7 20250210
- 修复：代码生成器 Controller 代码生成格式和详情无法生成文档的问题



## v1.10.6 20250206
- 新增：动态数据源 @UseDataSource 的value值扩展支持表达式解析处理
- 优化：代码生成器补充 solon 的 controller
- 优化：SqlServer2005 方言 Lis t查询 SQL 语句 BUG
- 修复：Doris 查询分离 lib 部署字段映射错误的问题 https://github.com/mybatis-flex/mybatis-flex/issues/472
- 修复：当 NetworkInterface.getNetworkInterfaces() 方法阻塞时，导致我整个应用该程序都卡住，无法正常启动 https://github.com/mybatis-flex/mybatis-flex/issues/470



## v1.10.5 20250110
- 新增：MyBatisFlexCustomizer 支持定义多个 bean 实例进行配置，感谢 @RexSheng
- 优化：引入 actuator 后日志出现告警的问题，感谢 @lin-mt
- 优化：进一步优化 mybatis-flex-solon-plugin 插件，感谢 @noear_admin
- 优化：优化 duckdb 方言配置，duckdb 方言总体类似于 PG 方言
- 修复：在某些情况下子线程中使用可能出现 NPE 的问题
- 修复：多数据源 + WithRelations 查询时导致后面查询的数据源选择异常的问题



## v1.10.4 20241228
- 修复：多 MybatisFlexBootstrap 实例时，`MybatisFlexBootstrap.getMapper(Class<T> mapperClass)` 方法只能获取最后的实例的 mapper 的问题，感谢 @wcc1433
- 修复：多 MybatisFlexBootstrap 实例时，`FlexGlobalConfig.getConfig(environmentId)` 获取的 config 永远是 defaultConfig，感谢 @wcc1433
- 优化：进一步优化 mybatis-flex-solon-plugin 插件，感谢 @noear_admin



## v1.10.3 20241220
- 新增：添加了duckdb 数据库支持，感谢 @PTmore
- 优化：优化 mybatis-flex-solon-plugin 插件，感谢 @noear_admin



## v1.10.2 20241128
- 优化：删除 DataSource 不必要的类型转换，集成第三方框架时，dataSource 可能为 FlexDataSource 的包装类，从而导致类型转换错误
- 优化：优化 If.java 添加更多的方法
- 优化：升级 mybatis 到 3.5.17 最新版本
- 修复：代码生成器主键定义在父实体类，可能出错的问题



## v1.10.1 20241112
- 修复：代码生成器 在禁用Service接口生成时，serviceImpl去除Service接口
- 修复：在使用 QueryColumn 的 ge 方法时, 大于等于会实际仅是大于的问题



## v1.10.0 20241104
- 新增：代码生成器添加  ColumnConfigFactory 配置的支持
- 修复：修复由于 1.9.9 修复方法名称后导致代码生成器出错的问题
- 优化：优化 unMappedColumnHandler 在 FlexGlobalConfig 的定义



## v1.9.9 20241031
- 优化：重构 QueryWrapper 使之优先使用用户设置的条件
- 优化：优化 BaseMapper.insertOrUpdate，使之在传入空字符串时走 save
- 优化：Spring 的 ServiceImpl#getMapper 返回约束泛型类型， 感谢 @aliothmoon
- 优化：修改 StringUtil 的 isBlank 为 noText, 以及 isNotBlank 为 hasText
- 修复：修复事务超时时间问题 & 提供更完善的Spring事务定义上下文，感谢 @aliothmoon
- 修复：子查询作为条件没有括号的问题
- 修复：子查询用到了外部查询的列时，两个列重名没有列名指向的问题
- 修复：分页在某些情况下，无法优化 COUNT 查询的问题
- 文档：修正文档的部分拼写错误，感谢 @kings



## v1.9.8 20241020
- 新增：新增 trino 方言，感谢 @chenjh3
- 新增：新增 OptimisticLockManager，用于处理跳过乐观锁的场景，感谢 @tiankafei
- 新增：添加 solon 事务支持，感谢 @noear_admin
- 新增: 增加未匹配列的自定义处理拓展接口，感谢 @somethingaw
- 新增：添加查询列和查询条件相互转换的适配器，感谢 @Suomm
- 新增：无法顺序读取数据源配置时，可以手动指定默认数据源，感谢 @Suomm
- 新增：使用实体类构建 QueryWrapper 时，可以使用实体类中属性标记的 TypeHandler 对值进行处理，感谢 @Suomm
- 新增：代码生成器添加表后缀处理，感谢 @Suomm
- 优化：mybatis-spring 升级同步升级 mybatis 依赖版本为 3.5.16
- 优化：废弃使用多个值的 SQL 连接符，感谢 @Suomm
- 优化：优化 SelectQueryColumn 类，感谢 @Suomm
- 优化：使 QueryColumn 子类可以自定义 SQL 生成，感谢 @Suomm
- 优化：优化 CustomKeyGenerator 类，感谢 @Suomm
- 优化：修正一些拼写错误，感谢 @Suomm
- 优化：如果更新数据时租户字段有值，则不覆盖，感谢 @Suomm
- 优化：FlexDefaultResultSetHandler 细节优化
- 优化：为 ModifyAttrsRecordProxyFactory 添加更加明确的异常信息，感谢 @Suomm
- 修复：使用 DbChain 的 as 出现 query table must not be empty 异常，感谢 @Suomm
- 修复：空数组/集合会查出所有数据的问题，感谢 @Suomm
- 修复：case when 中使用 SelectQueryColumn 字段会变成空的问题，感谢 @Suomm
- 修复：解决 XML 审计无法打印参数以及审计参数顺序问题，感谢 @Suomm
- 修复：使用 mapper.xml 的嵌套查询出现 NPE 的问题，感谢 @Suomm
- 修复：兼容 Lambda 的方式构建 SQL 为表起别名没有应用的问题，感谢 @Suomm
- 修复：代码生成器实体类有父类不添加 `@EqualsAndHashCode(callSuper = true)` 的问题，感谢 @Suomm
- 修复：UPDATE JOIN 表没有别名的错误，感谢 @Suomm
- 修复：修复 `@EnumValue` 应用在接口方法上失效的问题，感谢 @aliothmoon



## v1.9.7 20240828
- 新增：增加 updateBatch 方法是否忽略 null 字段的可配置性，感谢 @wqdTryBest
- 新增：支持在 APT 中通过表达式选择 package 层级
- 修复：修复使用 insertBatchSelective 方法抛异常的问题
- 修复：修复 union sql 的分页查询问题
- 修复：RelationManyToMany 注解同时通过 selectOneWithRelationsByIdAs 查询报错的问题



## v1.9.6 20240818
- 新增：Mapper 新增 insertBatchSelective 方法
- 新增：新增 SQL SERVER 返回当前日期和时间，感谢 @macy0122
- 优化：Mapper 的批量操作方法，由 List 修改为 Collection
- 修复：Sql Server 多表关联查询，主表去重，执行SQL异常，感谢 @macy0122
- 修复：processor 回退到 v1.9.3
- 修复：TableInfoFactory 在某些极端的使用场景可能出现 NPE 的问题
- 修复：修复分页包含 having 语句时，语法依然被优化问题，感谢 @cearnach
- 测试：优化一些测试的 DbType 冲突的问题



## v1.9.5 20240801
- 新增：ID 主键生成策略新增 ULID 算法，感谢 @dh-free
- 优化：SqlServer 方言转义 scheme、table、colums 根据 . 分割后分别包装，感谢 @macy0122
- 优化：SQL SERVER Limit 的细节优化 ，感谢 @macy0122
- 修复：驼峰转下划线规则不一致问题 @zuojl
- 修复：当使用 RowKey.AUTO 生成自增主键时，会导致 RowKeyGenerator 错误的问题 https://gitee.com/mybatis-flex/mybatis-flex/issues/IAFGDE
- 修复：SnowFlakeIDKeyGenerator 在某些极端情况下出现 UnknownHostException 的问题



## v1.9.4 20240722
- 新增：支持在 APT 中通过表达式选择 package 层级，感谢 @fangzhengjin
- 新增：添加 QueryWrapper.orderByUnSafely 支持自定义 SQL 进行 orderBy
- 新增：为 clickhouse 添加独立方言，进一步适配 clickhouse 更新和删除，感谢 @老唐
- 新增：添加分页时每页显示的数据数量最大限制，感谢 @Leo
- 优化：kotlin 代码生成器 entity 类定义及属性设置为 open，感谢 @lemonbx
- 优化：FieldWrapper 获取 Collection 泛型类型时增加友好错误提。，感谢 @lemonbx
- 修复：修复实体类的父类含有泛型时，转换报错 https://github.com/mybatis-flex/mybatis-flex/issues/354 感谢 @zuihou111
- 修复：解决 orderBy 时传入的变量参数时失效问题，感谢 @kamo-sama
- 修复：在某些情况下会出现 NotSerializableException 错误的问题 https://gitee.com/mybatis-flex/mybatis-flex/issues/IAAXMH
- 修复：达梦数据库某些场景下 SQL 关键字识别不正确的问题
- 文档：修正文档快速开始示例代码使用错误的方法名的问题，感谢 @Frank_HL
- 文档：添加APT设置增加表达式用法说明，感谢 @fangzhengjin
- 文档：更新use-in-kotlin.md对于插件配置、演示示例等提醒描述，感谢 @kamo-sama
- 文档：修正多租户下的一些文档错误问题，感谢 @lovealiang
- 文档：修正增删改下的一些文档错误问题，感谢 @dragon_haoge



## v1.9.3 20240618
- 新增：代码生成器添加生成 Java、Kotlin 两种代码的支持，感谢 @Suomm
- 新增：QueryMethods 添加 mysql date 函数，感谢 @bigtian99
- 优化：代码生成器自动检测父类是否包含泛型，感谢 @rainybx
- 优化：ClassUtil 优化抽取遍历父类的逻辑减少递归，感谢 @KAMO030
- 优化：优化 AbstractRelation，当配置错误时给出更加明确的错误信息
- 优化：APT 支持在 Resources 目录进行配置，感谢 @ruansheng8
- 修复：java 代码生成器 base 类缺少 import，感谢 @rainybx
- 修复：SQL Server 添加 KeywordWrap 无反义区分大小写处理
- 修复：修复多个 SqlSessionFactory 共存时，FlexGlobalConfig 被最后一个覆盖的问题，感谢 @witt-bit
- 文档：优化多数据源的示例代码，感谢 @jesee030



## v1.9.2 20240604
- 新增：代码生成器新增 PostgreSQL 方言的实现，感谢 @StringKe
- 修复：当 FunctionQueryColumn 中存在 table，会导致分页查询时 SQL 优化错误的问题，感谢 @98Kming
- 修复：修复 v1.9.0 由于重构 Mapper 导致 dsKey 获取可能出现错误的问题



## v1.9.1 20240602
- 修复：修复 v1.9.0 由于重构 Mapper 导致 solon 无法注册 mapper 的问题
- 优化：优化 FlexMapperProxy 数据源的处理逻辑



## v1.9.0 20240530
- 优化：重构 Mapper 的获取，使之减少一层代理从而获得更高性能
- 优化：优化 LambdaUtil 的性能
- 优化：优化代码生成器 Controller 代码生成的主键类型，感谢 @Suomm
- 优化：优化代码生成器的 JdbcTypeMapping
- 优化：优化 QueryColumn 的 between 对于数组支持的灵活性，感谢 @kamo-sama
- 优化：当 Entity 中的字段命名不规范时可能无法通过 Lambda 获取属性的问题 #I9P66C
- 优化：优化 StringUtil.camelToUnderline 用于处理某些字段命名不规范的问题
- 修复：在 Kotlin 中，LambdaUtil 无法通过 lambda 表达式正确获取到对象的属性值 #I9ONI4 ，感谢 @cnscoo



## v1.8.9 20240510
- 新增：QueryColumn 的 between 增加数组参数，感谢 @CrazyAirhead
- 新增：代码生成器生成的Controller配置@RequestMapping前缀路径，感谢 @yfxuwork
- 优化：调整 ServiceImpl#getMapper 返回 Mapper 类型特化，感谢 @Clownsw
- 修复：ClassUtil.newInstance 无法生成实例问题修复，感谢 @SnobbyVirus1973
- 修复：QueryWrapper.as() 可能在某些自定义生成 TableDef 下出错的问题
- 修复：修复 Map 传参审计打印 SQL 不正确的问题，感谢 @Suomm
- 修复：QueryCondition.create value传入List，执行select报错的问题 #I9JRAT
- 修复：未生效的 JOIN 跳过拼接逻辑删除的问题，感谢 @Suomm



## v1.8.8 20240418
- 新增：新增支持全局注册多个监听器的功能，感谢 @Suomm
- 修复：TableInfo 的 comment 构建错误的问题
- 修复：由 case 构建查询条件参数丢失问题，感谢 @Suomm
- 修复：TableRef 解析 VO 之后覆盖 tableInfoMap 缓存中原有 Entity 的问题，感谢 @Suomm
- 修复：重名字段查找不全问题，感谢 @Suomm #I9FW4O
- 优化：消除 Joiner 的 idea IDEA 警告，感谢 @Suomm
- 优化：TableInfo 提供实体类与数据库字段字段映射内容，感谢 @gswy
- 优化：调整关联查询相关功能模块访问级别，感谢 @ruansheng
- 优化：代码生成器移除模板中多余的前导空格，感谢 @cida
- 优化：代码生成器将 buildAnnotations 方法中，与模板统一格式，感谢 @cida
- 优化：代码生成器将 GeneratorFactory.generators 的类型替换为 LinkedHashMap，感谢 @cida
- 优化：在 Entity 无法初始化时给出异常原因，感谢 @Suomm #I9HL0K
- 文档：添加 VO 类重名映射说明，感谢 @Suomm



## v1.8.7 20240410
- 新增：添加 `@TableRef` 注解标记 VO 类和 Entity 绑定，方便关联查询直接转换为 VO，感谢 @Suomm
- 新增：Db + Row 支持 QueryWrapper 的原生 SQL 构建，感谢 @Suomm
- 优化：XML 分页 #{qwSql} 参数补充逻辑删除等参数，感谢 @Suomm
- 优化：`insertBatch(entities, size)` 和 `insertBatch(entities)` 对空 list 的处理不一致问题 #I9EGWA
- 修复: 修正使用 MyBatis 原生工具方法 PropertyNamer#methodToProperty 可能造成无法正确获取属性的问题，感谢 @Suomm
- 修复：修复当 APT 配置多个 ignoreSuffix 时，可能导致生成的类名不正确的问题 #I9ED9N
- 修复：QueryWrapper 通过 Map 构造查询条件，并传入 SqlOperators 时，Like 不能正确拼接 % 的问题 #I9F8HO
- 修复：代码生成器的 EnjoyTemplate 在并发下可能出现异常的问题
- 测试：添加 Db + Row 原生 SQL 子查询插入与更新测试，感谢 @Suomm



## v1.8.6 20240402
- 修复：在 QueryWrapper 中使用 is_null 时出现 sql 异常的问题，感谢 @Suomm



## v1.8.5 20240401:
- 新增：Join ON 增加 lambda 重载方法，简化使用，感谢 @robor.luo
- 新增：Db + Row 添加支持 MyBatis 原生 SQL 及参数的方法，感谢 @Suomm
- 新增：新增不忽略 null 值时拼接 null 参数的相关功能，感谢 @Suomm
- 新增：代码生成添加 “总是生成 @Column 注解” 选项，感谢 @Suomm
- 优化：EnumWrapper 的 getEnumValue 方法如果没有注解，则使用枚举 name 返回，感谢 @robor.luo
- 优化：代码生成器 EntityConfig 链式调用错误的优化，感谢 @Suomm
- 修复：代码生成器无法生成内置数据脱敏处理器键（Masks）的问题，感谢 @Suomm
- 修复：修复 ConvertUtil 等一些工具类的方法错误
- 文档：添加 Db + Row 添加支持 MyBatis 原生 SQL 的相关文档，感谢 @Suomm
- 文档：配置相关文档链接到 mybatis3 中文文档路径错误



## v1.8.4 20240325:
- 修复：修改 QueryWrapperAdapter 的 Join 方法返回的泛型错误的问题，感谢 @Suomm
- 优化：优化 ActiveRecord Join 自己，感谢 @Suomm



## v1.8.3 20240324:
- 新增：QueryWrapper 添加 hasCondition 方法，感谢 @Suomm
- 新增：添加 SqlOperator 忽略属性，用于在某些场景下忽略 entity 字段，感谢 @Suomm
- 新增：添加 selectCursorByQueryAs 重载方法，感谢 @Suomm
- 新增：添加 MapUtil 替代 MyBatis 官方的 MapUtil
- 新增：DynamicTableProcessor 的 process 方法添加 OperateType 参数，感谢 @wtj
- 新增：CPI 添加获取条件中参数的方法，感谢 @Suomm
- 新增：添加 RawValue 获取参数的方法，感谢 @Suomm
- 新增：多租户功能可以根据表名返回多租户条件，感谢 @Suomm
- 新增：支持使用 UpdateWrapper 自定义插入值，感谢 @Suomm
- 新增：代码生成器策略配置 StrategyConfig 添加支持通配符功能，感谢 @gongdonghui
- 新增：`@Table` 和 `@Column` 注解添加 comment 配置的支持
- 新增：代码生成器添加自动把 comment 添加到注解的相关功能支持
- 修复：修复 MultiDataSourceAutoConfiguration 下的 druid 路径错误的问题，感谢 @Suomm
- 修复：解决 p6spy 下多数据源获取 DbType 失败的问题，感谢 @ocoooo
- 修复：生成代码时, 某些情况下不会添加 `@EqualsAndHashCode(callSuper = true)` 的问题
- 修复：Relation 查询使用 Lambda 表达式无法指定类名问题，感谢 @Suomm
- 修复：使用默认数据源获取 dsName 为 null 的错误，感谢 @Suomm
- 优化：逻辑删除时，保证逻辑删除前面的条件被括号包裹，感谢 @swqxdba
- 优化：重构 使用 QueryTable 类替代 TableDef 类，感谢 @Suomm
- 优化：重命名 JdbcTypeMapping 的 mapper 为 typeMapper
- 优化：优化 Spring 下事务管理的自动配置，防止在某些场景下被其他管理器接管的问题
- 优化：优化 dependencies 模块的 pom.xml 方别独立发布
- 文档：优化 `@Table` 注解文档的错别字问题，感谢 @zhaoshuli1984
- 文档：优化事务相关文档的错别字问题，感谢 @lifejwang11
- 文档：添加关于 JdbcTypeMapping 的 typeMapper 使用文档



## v1.8.2 20240305:
- 新增：Maven 编译添加 Javadoc 插件生成 Javadoc 的支持，感谢 @Suomm
- 优化：优化对 SpringBoot3 下的 Druid 数据源适配，感谢 @Suomm #I94P5P
- 优化：dependencies 模块取消继承父模块的其他依赖管理版本，感谢 @Suomm #I94RVP
- 优化：分页在 count 之前先去掉 limit 参数，避免 count 查询错误，感谢 @ocoooo
- 优化：优化 SqlOperators 使之代码更加严谨
- 优化：代码生成器 Table.java 添加 containsColumn 方法的支持
- 优化：TypeHandlerObject 添加 value 的 getter 方法，方便拦截器在某些场景下获取，感谢 @ruansheng
- 修复：全局 TypeHandler 无法注册的问题，感谢 @Suomm
- 修复：ConvertUtil.java 无法正确转换 Serializable 参数的问题



## v1.8.1 20240302:
- 新增：添加 CommaSplitTypeHandler 用于对逗号分割存储映射到实体类 `List<String>` 的支持
- 新增：代码生成器 EntityConfig 添加 superClassFactory 配置的支持
- 优化：重构代码生成器模块的 JdbcTypeMapper，以支持更多的参数读取
- 优化：FlexTransactionAutoConfiguration 添加更多的 AutoConfigureBefore 设置，感谢 @wittplus #I930JB
- 优化：优化 EntityGenerator.java 的相关输出日志
- 优化：优化 TableInfo.newInstanceByRow 可能存在的性能问题 #I94D33
- 优化：优化 ToManyRelation 存在的性能问题，感谢 @swqxdba
- 修复：多数据源模式下，当设置了分片规则以后，不能设置正确的 DbType，感谢 @ccwilliam
- 修复：代码生成器设置类型为 java.util.List 时，import 导入不正确的问题
- 修复：ClassUtil.getAllMethods 方法处理枚举类型时逻辑有缺陷的问题 #I94749 #I94321
- 修复：@EnumValue 标记在方法上没有获取 propertyType 的问题，感谢 @Suomm
- 修复：设置全局的 TypeHandler 不起作用的问题，感谢 @Suomm
- 修复：被 transient 标记的字段不会被映射到数据库当中的问题，感谢 @Suomm #I953IS
- 文档：代码生成器添加设置 `java.util.List<String>` 类型的相关文档
- 文档：代码生成器添加关于无法获取注释的相关注意事项
- 文档：修正自动映射的相关示例代码，感谢 @zhangyx
- 文档：修正 db + row 的相关实例代码，感谢 @zhangyx



## v1.8.0 20240223:
- 优化：entityOrBase.tpl 中命令占位符被替换后，entity 里面多了一行空白行，感谢 @caohenghui
- 修复：在复杂的 VO 嵌套查询时，addResultMap 抛出异常的问题，感谢 @leizhiyou
- 修复：实体类实现多层级的接口时监听器无法匹配问题，感谢 @ruansheng
- 修复：使用 Mappers 执行语法时, 异常未被精确抛出问题，感谢 @ruansheng
- 修复：@EnumValue 在 get 方法上，查询报错的问题，感谢 @Suomm



## v1.7.9 20240204:
- 新增：ActiveRecord 模式新增 withRelations(columns) 的查询方法，感谢 @tangzc
- 新增：在不配置 genPath 时对增量编译的支持，感谢 @CloudPlayer
- 新增：通过 FlexConfiguration 预注册的自定义 typeHandler 的功能，感谢 @tanglh
- 优化：重构代码生成器生成 BaseEntity 的代码
- 修复：QueryConditionBuilder 的 ge 方法修改为 eq 的问题，感谢 @wittplus
- 修复：Db.insertBatchWithFirstRowColumns 当 row key 的顺序没保持一致时，出错的问题
- 修复：Db.insertBatchWithFirstRowColumns 不能自动填充主键字段的问题
- 修复：paginateWithRelationsAs 不及时清空 threadLocal 的 relation 配置的问题 #I90S5G
- 修复：在某些极端情况下 AbstractRelation 出现 NPE 的问题 #I90XTY
- 修复：在和 Apache CXF 整合时，出错的问题 #I90XE5
- 文档：优化 faq 的相关文档，感谢 @Suomm



## v1.7.8 20240201:
- 新增：在 DialectFactory 中添加全局方言设置的功能，感谢 @farukonfly
- 新增：SQL 审计的打印功能添加对当前数据源的输出，感谢 @hhggcon
- 新增：枚举映射注解 @EnumValue 添加在方法上进行支持的功能，感谢 @huangxy
- 新增：AbstractInsertListener 添加自动探测子类类型的支持，感谢 @luo_zhan
- 新增：代码生成器为的实体类增加 serialVersionUID 字段生成的支持，感谢 @hanjinfeng39
- 新增：代码生成器添加对 model、service、Controller 等自定义生成目录配置的支持
- 新增：代码生成器添加生成 Entity Base 类的支持 #I7JH7K
- 新增：SqlOperators 增加支持 QueryColumn 参数的重载，感谢 @robor.luo
- 优化：完善对 mybatis-flex-spring-boot3-starter 的 Maven 版本管理
- 优化：修正 Spring 下 BeanPostProcessorChecker 的警告问题，感谢 @Suomm
- 优化：进一步优化关联查询时的重名映射问题，感谢 @Suomm
- 优化：移除驼峰命名转化对 Row 类的影响
- 优化：优化 FieldWrapper 的异常信息输出
- 优化：优化 Page.java 防止传入为 0 的 pageSize 值
- 修复：修改 exist 在子select中出错的问题，感谢 @font-c
- 修复：修复 ColumnInfo 在某些情况下可能出现 NPE 的问题 #I8UTJC
- 修复：SQL审计正确返回变更行数结果不正确的问题，感谢 @RishChen
- 修复：关联查询时，当实体自身字段数据为 null 时，出现 NPE 的问题，感谢 @tangzc
- 修复：RelationManager.clearMaxDepth() 不能正常工作的问题，感谢 @tangzc
- 文档：更新关于使用 springboot3 的相关文档
- 文档：增加在方法上使用注解 EnumValue 文档，感谢 @huangxy
- 文档：代码生成器添加 setJdkVersion 的文档说明，感谢 @hanjinfeng39
- 文档：进一步完善对 QueryWrapper 的相关描述，感谢 @robor.luo
- 文档：添加 spring-data 整合警告解决方法的相关文档，感谢 @Suomm
- 文档：更新常见问题，感谢 @Suomm



## v1.7.7 20240104:
- 新增：添加 spring-boot3 新模块，用于 springboot v3 下使用，感谢 @Suomm
- 新增：QueryMethods 添加 NOT (column) 函数，感谢 @wittplus
- 优化：更新 Solon 下的 @ProxyComponent 为 @Component，感谢 @citysoft
- 优化：修改 com.mybatisflex.annotation.SetListener 的注释错误，感谢 @whzzone
- 优化：修改 GBase_8s 数据库类型 sql 语句无反义处理
- 优化：升级 MyBatis 到 3.5.15 最新版本 #I8PQLC
- 修复：IService.getOne 没有添加 limit 1 的问题
- 修复：Db.updateEntitiesBatch 更新部分字段时报错的问题，感谢 @617054137
- 文档：更新在 Kotlin 下使用的相关文档，感谢 @kamo-sama
- 文档：优化示例代码提交语言标识，感谢 @bf109f
- 文档：更新存在一个为 is_deleted 的字段中拼写错误，感谢 @shuangtao



## v1.7.6 20231223:
- 新增：Db.selectFirstAndSecondColumnsAsMap 方法：查询结果的第一列作为 key，第二列作为 value，感谢 @617054137
- 新增：方言添加添加权限处理统一入口 prepareAuth，感谢 @bf109f
- 优化：在数组异常时不显示数组为空异常信息的问题，感谢 @Suomm
- 优化：修改 QueryCondition 的类的相关错别字，感谢 @Suomm
- 优化：升级 MyBatis 相关依赖到最新版本，感谢 @tocken
- 修复：DB2 v10.5 不支持 offset 关键字进行分页的问题，感谢 @farukonfly
- 修复：DB2 v10.5 不支持 Nulls First 或 Nulls Last 语法的问题，感谢 @farukonfly
- 修复：FieldWrapper 对有泛型 Entity 进行部分更新时报错的问题，感谢 @617054137
- 修复：Db.updateEntitiesBatch 更新部分字段时，在某些场景下报错的问题，感谢 @617054137
- 修复：字段类型为 YearMonth,Year 等时更新出错的问题 #I8CGVM
- 修复：main 方法直接调用 updateChain 方法构建 sql 时调用 toSQL 出错的问题 #I8NF9T
- 测试：优化单元测试，移除 println，添加更多的断言，感谢 @mofan
- 文档：添加 SpringBoot 3.2 版本启动失败解决办法，感谢 @Suomm
- 文档：优化英文文档的相关内容，感谢 @mofan
- 文档：更新 table 中的示例代码错误，感谢 @jtxfd_admin



## v1.7.5 20231124:
- 修复：主键 ID 当传入空字符串时，调用 `insert` 方法不会依据 `@Id` 自动生成主键 id 的问题
- 修复：`FlexTransactionManager` 空指针异常的问题
- 修复：`@ColumnMask`，`typeHandler` 一起使用时冲突导致 `@ColumnMask` 不生效的问题



## v1.7.4 20231120:
- 新增：QueryMethods 添加 bracket 在极特殊场景下用于构建括号的支持，感谢 @Suomm
- 新增：QueryWrapper 添加取相反数的 SQL 构建，感谢 @Suomm
- 优化：重命名 RawFragment 为 RawQueryCondition，保持 Raw 的风格统一，感谢 @Suomm
- 优化：solon-plugin 升级 solon 升为 2.5.3，感谢 @noear_admin
- 修复：ArithmeticQueryColumn 算术运算无法拼接条件的问题，感谢 @uanmengyuan
- 修复：读写分离自定义分离策略不生效的问题 #I8FP0K
- 修复：使用 @Transactional 嵌套时，默认事务传播机制与预期不符 #202
- 修复：case when 别名丢失的问题，感谢 @x-core
- 文档：添加关于 QueryWrapper 克隆测试的相关文档，感谢 @Suomm
- 文档：添加关于 QueryWrapper 取相反数的 SQL 构建文档，感谢 @Suomm
- 文档：优化 QueryWrapper 的相关文档，感谢 @Suomm



## v1.7.3 20231027:
- 新增：添加 Delete SQL 多表关联查询删除的支持
- 新增：代码生成器添加 `Generator.getTables()` 方法，方便在某些场景下去获取所有表信息
- 优化：重写 CommonsDialectImpl.wrapColumnAlias 方法，SQL 不对 as 关键字进行关键字包裹
- 优化：代码生成器优化 IDialect，添加 AbstractJdbcDialect.java
- 优化：QueryModel 的一些泛型方法添加 @SafeVarargs，消除 IDE 警告，感谢 @guan-meng-yuan
- 修复：修复 fieldMapping 基础类型查询 null 的情况，感谢 @guan-meng-yuan
- 修复：多全局 Listener 配置可能缺失问题，感谢 @ice-samll
- 修复：表关联的从表配置 schema，查询从表时 schema 丢失问题，感谢 @font-c
- 测试：添加 filedMapping 的相关代码测试，感谢 @guan-meng-yuan
- 测试：添加全局 Listener 缺失修复的数据库插入测试，感谢 @ice-samll



## v1.7.2 20231018:
- 新增：QueryWrapper 添加对 MyBatis-Plus 的兼容 API，方便喜欢 MyBatis-Flex 的用户进行迁移
- 新增：QueryModel #select()方法 添加 Iterable queryColumns，感谢 @guan-meng-yuan
- 新增：QueryWrapper 添加 RawQueryTable 的支持，感谢 @Suomm
- 新增：RelationToOne 注解添加对 extraCondition 配置的支持 #I88MJU
- 新增：重构 DynamicSchemaProcessor，添加 table 参数，方便通过 table 获取 schema #I88REA
- 优化：在 insert 时，若 entity 有主键，则直接使用 entity 的主键，不再通过主键生成器来生成 #I88TX1
- 优化：RelationManager 前置数据源的设置，否则关联查询注解可能造成数据源设置冲突
- 修复：使用 Db 无法使用指定 Logger 打印日志问题，感谢 @aohanaohan #I88C41
- 修复：MyBatis-Flex 与 spring-data-jpa 混用时候，出现事务管理器冲突错误的问题，感谢 @tocken
- 修复：CPI.setWhereQueryCondition 传入 null 时，出现 NPE 的问题 #I88DFH
- 修复：代码生成器未做自定义配置时，生成代码时出现 NPE 的问题 #I88UIZ
- 测试：为测试添加更多的断言已保证测试安全
- 文档：QueryWrapper 添加关于自定义字符串列名的相关文档
- 文档：QueryWrapper 添加关于 MyBatis-Plus 兼容 API 的相关文档
- 文档：QueryWrapper 添加关于 Map 转化为 QueryWrapper 的相关文档



## v1.7.1 20231016:
- 新增：QueryColumnBehavior 增加内置的忽略规则，方便使用，感谢 @chenjh3
- 优化：代码生成器提供了 GlobalConfig 的 customConfig 的 getter/setter，感谢 @qq925966998
- 优化：代码生成器参数列表加入 customConfig，感谢 @qq925966998
- 修复：某些场景下group by 字段错误的增加了表别名，而导致生产的sql错误，github #184
- 修复：某些场景下 QueryWrapper 打印的 SQL 与 执行的 SQL 不一致的问题，感谢 @chenjh3
- 修复：当条件满足忽略规则，when 又设置为 true 时，NPE 异常的问题，感谢 @chenjh3 #I86T6H
- 测试：添加 Db + Row 的一些测试，感谢 @Suomm
- 测试：重置 dynamicTableProcessor 防止影响其他的测试用例，感谢 @chenjh3
- 测试：把测试代码中所有的测试用例都加上断言判断，感谢 @chenjh3
- 文档：QueryWrapper 中添加关于多主键查询和删除的相关文档，感谢 @wchopper
- 文档：修改一些已经移除的方法但是文档中还存着的问题，感谢 @HunnyOvO



## v1.7.0 20231009:
- 修复：紧急修复当上一个条件失效时，错误的使用上上个条件作为连接符的问题
- 测试：单元测试更加更多的断言代码



## v1.6.9 20231009:
- 新增：添加 SQL 审计模块的计数消息收集器，感谢 @Suomm
- 新增：QueryWrapper 的 Lambda 方式支持 allColumns 和 defaultColumns 构建功能，感谢 @Suomm
- 新增：Mapper 增加 Entity 作为入参进行查询与删除和查询的方法，这有利于对多主键实体类的删除和查询，感谢 @Watcher.Wang
- 新增：添加 ifNull 函数 lambda 下缺失的非 lambda elseColumn 参数的支持，感谢 @guanmengyuan
- 优化：重构抽象 SQL 审计模块的消息收集器，感谢 @Suomm
- 优化：SpringBoot-starter 模块的 autoconfigure-processor 默认添加 optional 配置，感谢 @Freeman Liu
- 修复：当 `@Relation` 注解使用 `valueField` 配置时，在某些情况下抛出 IllegalStateException 异常的问题
- 修复：在 FlexConfiguration 替换 ResultMap 时，替换了用户自定义的配置可能导致异常的问题
- 测试：测试环境添加一个 SQL 格式化的相关依赖，感谢 @Suomm
- 测试：添加关于 Lambda 方式 SQL 列构建测试，感谢 @Suomm
- 文档：添加在 where 部分使用 SQL 函数的相关文档，感谢 @shaoerkuai



## v1.6.8 20230928:
- 新增：`@RelationOneToMany` 添加支持 selfValueSplitBy 字符分割的配置支持，感谢 @ice-samll
- 新增：添加构建所有列方法 QueryMethods.allColumns()，感谢 @guanmengyuan
- 修复：修复 QueryMethods.replace 无法支持空格替换的问题
- 修复：ManyToOne 在某些场景下出错 IllegalArgumentException 错误的问题
- 测试：添加关于 `@RelationOneToMany` 的 selfValueSplitBy 测试代码，感谢 @ice-samll
- 文档：添加关于 `@RelationOneToMany` 的 selfValueSplitBy 的使用文档，感谢 @ice-samll
- 文档：修改将批量操作文档中的相关错别字，感谢 @dataprince



## v1.6.7 20230925:
- 新增：多数据源添加 DataSourceShardingStrategy 接口，用于读写分离
- 新增：Fastjson2TypeHandler 添加对接口或者抽象类的支持，感谢 @617054137
- 优化：DataSourceKey 移除不必要的属性定义
- 优化：代码生成器升级 enjoy 模块，以适配 JDK21
- 优化：Table 注解移除 @Inherited ，以解决 VO 等继承 model 的实体类中，生成多余的、或冲突的 tableDef
- 修复：APT 在类名和字段名相同的情况下，构建的 TableDef 出错的问题
- 修复：FlexConfiguration 在某些情况下替换 resultMap 时出错的问题
- 修复：AbstractRelation 在某些极端情况下出现 NPE 的问题
- 修复：查询条件 OperatorQueryCondition 参数值未检查 effective 的问题，感谢 @wanggaoquan
- 修复：查询条件 OperatorSelectCondition 参数值未检查 effective 的问题，感谢 @wanggaoquan
- 修复：QueryWrapper 在某些情况下构建的 SQL 会出现两次 as 的问题，感谢 @cnscoo
- 文档：优化 kotlin 文档的 git 链接顺序与完善文档内容，感谢 @kamo-sama
- 文档：多租户添加相关的代码示例
- 文档：添加读写分离的相关文档



## v1.6.6 20230922:
- 新增：UpdateChain.of 使用 Mapper 进行构造方便在批量操作使用的功能
- 新增：QueryWrapper.select(Iterable) 方法，方便 Kotlin 扩展
- 新增：Relation 注解新增 valueField 配置，当不为空串时值进行某个字段赋值，感谢 @ice-samll
- 优化：转驼峰方法多次转换保持结果一致，感谢 @617054137
- 优化：生成列别名规范，保持用户原始的列别名命名，感谢 @font-c
- 修复：QueryWrapper 在某些场景下构建 SQL 会出现两个 AS 关键字的问题
- 修复：Db 或 MyBatis 原生查询驼峰转换需处理不包含下划线的字段，感谢 @617054137
- 测试：增加 Relation 注解单字段赋值 Springboot 测试，感谢 @ice-samll
- 文档：添加 Relation 注解单字段赋值的相关文档，感谢 @ice-samll
- 文档：添加关于批量操作使用 UpdateChain 的相关示例



## v1.6.5 20230914:
- 新增：代码生成器为 Oracle 的 JdbcTypeMapping 类型 OracleBlob 添加映射处理
- 新增：LogicDeleteManager 和 TenantManager 添加 Runnable 无返回值重载，感谢 @Suomm
- 新增：RawQueryColumn 添加参数占位符的支持功能，感谢 @Suomm
- 新增：代码生成器添加关于 solon Controller 生成的代码模板，感谢 @ZhuHJay
- 新增：UpdateEntity 添加自动去除有忽略注解的字段的功能，感谢 @aqnghu
- 优化：代码生成器配置类添加 Serializable 接口实现的支持，方便自定义缓存保存，感谢 @zoufang162
- 优化：使用 lambda 优化部分写法，感谢 @handy-git
- 优化：使用 try-with-resources 释放 Connection，感谢 @handy-git
- 优化：DataSourceBuilder 出错时，吞掉原始的 exception 的 message 信息的问题 #I7YYRF
- 优化：`@Table` 注解增加 `@Inherited` 修饰，感谢 @jerryzhengsz1
- 修复：当工作流引擎 activti6 整合 MyBatis-Flex 可能出现 NPE 的问题
- 修复：通过 XML 自定义的 SQL 查询不兼容自定义枚举使用的问题，感谢 @lifejwang11
- 文档：更新关于 MyBatis-Flex-Admin 的相关文档
- 文档：添加关于 MyBatis-Flex-Kotlin 的相关文档 感谢 @kamo-sama
- 文档：修正自定义脱敏处理器的示例代码错误，感谢 @wang_yong_ji
- 文档：添加关于 MyBatis-Flex 与 activiti6 以及 Flowable 等工作流引擎集成的相关文档，感谢 @simple_wind
- 文档：修复逻辑删除文档的个别错别字，感谢 @cainiao3853
- 文档：修正自定义映射的相关代码示例错误，感谢 @tycms



## v1.6.4 20230903:
- 新增：QueryWrapper 添加动态排序功能的支持，感谢 @Suomm
- 优化：Solon 取消无必要的 FlexSqlSessionFactoryBuilder 注入，感谢 @noear_admin
- 修复：开启 mapUnderscoreToCamelCase 配置后，@Relations 关联查询可能无法正常查询的问题
- 文档：添加动态排序的相关文档，感谢 @Suomm



## v1.6.3 20230901:
- 修复：紧急修复 v1.6.2 分页查询无法查询数据列表的问题



## v1.6.2 20230831:
- 新增：添加一个 mysql 函数 GROUP_CONCAT，感谢 @shen_jun_feng
- 优化：优化 TableDefs 的多一次判断可能影响性能的问题
- 优化：代码生成器的 Builder 缺少的字段构建，感谢 @Suomm
- 修复：FlexMapWrapper 在开启 useCamelCaseMapping 时的一些错误问题
- 修复：无法配置自定义 EnumTypeHandler 的问题 #I7WNQQ
- 修复：自动映射无法对 List 进行自动忽略的问题 #I7X7G7 #I7XBQS
- 修复：实体类在不同包名下，无法初始化全部 TableDefs 的问题，感谢 @hezhijie
- 文档：优化多租户的忽略租户条件的相关文档
- 文档：修改 QueryWrapper 的 SQL 示例错误
- 文档：添加 select .. as 的使用注意事项
- 文档：优化多数据源的示例代码错误的问题，感谢 @CrazyAirhead
- 文档：修改 mybatis-flex-dependencies/pom.xml 中 groupId 拼写错误，感谢 @chaosforever



## v1.6.1 20230827:
- 新增：添加 QueryWrapper.create(entity,SqlOperators) 支持通过 Entity 转换为 QueryWrapper
- 优化：移动 SqlConnector 的包名
- 优化：优化 IService.exists() 的效率，感谢 @Suomm
- 修复：开启 mapUnderscoreToCamelCase = true 时， row 无法转换 entity 的问题
- 修复：使用 QueryColumn 无法构建 UPDATE SET 语句，感谢 @Suomm
- 修复：UpdateChain 不支持 @EnumValue 注解的问题，感谢 @Suomm
- 文档：添加 Entity 转换为 QueryWrapper 的相关文档
- 文档：添加 `@Column(isLarge = true)` 的其他使用方式，感谢 @Suomm
- 文档：优化 `@Column` 注解的一些错误描述，感谢 @Suomm
- 文档：添加关于 QueryColumnBehavior 配置的相关文档，感谢 @Suomm



## v1.6.0 20230825:
- 新增：添加 RelationManager.addQueryRelations() 方法用于只查询部分关联字段
- 优化：重构移除已经标识删除的方法或类 ！！！破坏性更新
- 修复：Oracle 下的批量操作时在某些情况下可能返回结果不正确的问题
- 修复：代码生成器在某些情况下出现 NPE 的问题，感谢 @Suomm
- 修复：v1.5.9 版本造成的 ignore 属性也被加入 defaultQueryColumns 默认查询列的问题，感谢 @Suomm
- 文档：添加 RelationManager.addQueryRelations() 使用的相关文档
- 文档：添加 mybatis-flex-dependencies 使用说明，感谢 @Suomm



## v1.5.9 20230824:
- 新增：添加对数据库 Hive SQL 和 Doris 的支持
- 新增：ActiveRecord 添加 saveOpt 方法，用户保存数据并返回内容
- 新增：数据查询添加对 ignore 字段自动映射的支持
- 优化：SnowFlakeIDKeyGenerator 添加更多的参数设置的方法
- 优化：重构 FlexSpringTransaction 使其拥有更高的性能
- 优化：对 Mappers 进行优化和添加必要的注释，感谢 @Suomm
- 优化：重构代码生成器，添加链式调用的支持以及必要的代码注释，感谢 @Suomm
- 优化：重构将不为 null 的判断统一到 QueryColumnBehavior 中，方便用户自定义逻辑，感谢 @Suomm
- 优化：修改 FlexSqlSessionFactoryBean 下的一些注释错误 ，感谢 @cainiao3853
- 修复：在某些场景下使用 as 时出现 NPE 的问题  #I7T00C
- 修复：Db + Row 的场景下，自定义 RowKey 时结果不正确的问题
- 修复：QueryMethods.dateFormat 使用出错的问题 #I7TEUM
- 修复：QueryMethods.substring 使用出错的问题 #I7TEUM
- 修复：返回 map 时，配置 map-underscore-to-camel-case 不起作用的问题
- 修复：数据源解密器通过 Bean 注解配置和自定义配置配置不统一的问题  #I7UHUN
- 修复：使用 Hutool 的 BeanUtil.copyProperties 拷贝 Page 失败的问题 #I7VBUE
- 修复：UpdateEntity 没有对 @Column(ignore = true) 的字段进行过滤的问题 #I7RE0J
- 修复：FlexSpringTransaction 在某些场景下出错的问题，感谢 @chenjh3
- 文档：修改 faq 下的一些错别字
- 文档：常见问题添加如何获取保存并获取主键的相关文档
- 文档：添加多逻辑删除时，同时更新删除人和删除时间的相关文档
- 文档：添加批量操作时，可能无用的错误注意事项
- 文档：优化代码生成器的相关文档，感谢 @Suomm
- 文档：ActiveRecord 添加 saveOpt 的相关文档，感谢 @Suomm
- 文档：修改动态表名的示例代码错误的问题，感谢 @Suomm
- 文档：修正 gradle 文档错误的问题，感谢 @tiansai
- 文档：修正 Db + Row 的一些描述错误的问题，感谢 @yang-zzu



## v1.5.8 20230820:
- 新增：Row 以及 Entity 在新增时，添加 setRaw 的支持
- 新增：数据库方言添加对 lealone 数据库的支持，感谢 @hopper
- 新增：QueryChain 添加新 of 方法，用于支持通过 Entity 创建 QueryChain 对象，感谢 @liibang
- 新增：提供多线程情况下子父线程获取切换数据源功能，感谢 @lifejwang11
- 优化：设置乐观锁未设置值的异常信息国际化
- 优化：设置 @Table 的名称配置支持 "schema.tableName" 配置
- 优化：重构移除乐观锁、逻辑删除、多租户的全局默认字段配置，用户需手动配置生效
- 优化：添加逻辑删除、乐观锁、多租户的全局配置的 spring 配置文件提示功能
- 优化：重构用户手写的条件中是否包含该表，感谢 @Suomm
- 优化：重构获取 join 连表信息时携带别名，感谢 @Suomm
- 优化：重构 QueryWrapper 的括号生成，以动态添加子 where 条件，感谢 @Suomm
- 优化：添加 Conditional 接口用于统一 Condition 的 api，感谢 @Suomm
- 修复：druid 数据源的某些场景下，数据加密无法使用的问题
- 修复：修复 MybatisKeyGeneratorUtil 类在某些情况下可能得空指针异常问题，感谢 @norkts
- 修复：SqlOperator 遗漏了 NOT_EQUALS 操作符 的问题，感谢 @Suomm
- 修复：Relation 注解查询，忽略字段使用的泛型错误的问题，感谢 @Suomm
- 修复：DbChain 的 setRaw 方法指向错误的问题，感谢 @Suomm
- 文档：添加 set 和 setRaw 区别的相关文档
- 文档：修改代码生成器的相关文档错别字，感谢 @PatrickSt
- 文档：改正了注解处理器中的内容，将其修改为正确的注解处理器，感谢 @CloudPlayer
- 文档：优化 Gradle 和 KAPT 的相关文档，感谢 @Suomm
- 文档：添加乐观锁、逻辑删除、多租户的全局配置的相关文档，感谢 @Suomm
- 文档：添加多线程情况下子父线程获取切换数据源功能的相关文档，感谢 @lifejwang11



## v1.5.7 20230812:
- 新增：QueryWrapper 添加对 delete 和 update 的 left join 支持
- 新增：RelationManager.addIgnoreRelations() 添加对 lambda 的支持
- 新增：添加 QueryColumnBehavior 用于自定义 QueryColumn 的某些行为特征
- 新增：typeHandler 添加对泛型自动支持的功能
- 新增：多数据源新增对 Seata 分布式事务的支持，感谢 @lifejwang11
- 新增：添加对 Kotlin 的扩展支持，感谢 @kamo-sama
- 新增：添加 saveOrUpdateBatch 方法的支持，感谢 @Suomm
- 新增：QueryModel 提供 as 方法的支持，感谢 @Suomm
- 新增：逻辑删除、乐观锁、多租户添加全局默认自动配置的功能，感谢 @Suomm
- 优化：移除 flex 自动把 id 属性设置为主键的功能
- 优化：重构链式调用的方法，统一链式调用和 ActiveRecord 的 API，感谢 @Suomm
- 优化：UpdateChian 支持设置 left join 的表数据的支持，感谢 @Suomm
- 修复：db2 方言的 KeywordWrap 错误的问题
- 修复：在某些场景下 count 查询没有被替换的问题，感谢 @Suomm
- 修复：QueryWrapper 的 or(consumer, condition) 方法逻辑错误，感谢 @Suomm
- 修复：QueryColumn 由于 Predicate 没有类型约束可能导致类型转换异常的问题，感谢 @Suomm
- 修复：OSGI 环境下，Lambda 通过 ClassLoader 获取不到类的问题，感谢 @2han9wen71an
- 文档：更新视频教程的文档链接
- 文档：添加 SpringBoot 最低版本的说明文档，感谢 @Suomm
- 文档：Seata 分布式事务的相关文档，感谢 @lifejwang11
- 文档：增加了使用gradle构建时的文档说明，感谢 @CloudPlayer
- 文档：增加了在Kotlin中使用注解处理器的说明，感谢 @CloudPlayer
- 文档：常见问题添加代码生成器获取不到注释说明，感谢 @Suomm
- 文档：常见问题添加 Spring Devtools 造成的类转换异常的相关文档
- 文档：常见问题添加 Nacos 集成启动出错的相关文档



## v1.5.6 20230804:
- 新增：代码生成器重构并新增对 Solon 框架的代码生成功能，感谢 @Suomm
- 新增：添加新的默认的达梦方言，之前使用 Oracle，感谢 @qimincow
- 优化：优化 QueryWrapper.toSQL() 的性能
- 优化：添加 "未配置事务生成器" 时的异常信息国际化支持
- 优化：重构 KeywordWrap.java 使之代码逻辑更加清晰
- 优化：重构代码生成器的 Generator，使之在 web 中在线生成时，保证链接正常关闭
- 优化：代码生成器的 Column 添加关于数据库类型和长度的相关属性
- 优化：优化 QueryWrapper.exists() 的性能，感谢 @gongzhongqiang
- 优化：修改 FlexIDKeyGenerator 注释描述错误的问题，感谢 @duxlei
- 修复：新增 spring-devtools.properties 已解决 Spring 类转换异常的问题
- 修复：QueryWrapper 同时 left join 两个同一个表的时候，逻辑删除条件不正确的问题 #I7QD29
- 修复：RelationManager 在某些场景先可能出现 NPE 的问题
- 修复：`@UseDataSource` 注解在某些 Spring 场景下不生效的问题，感谢 @Suomm
- 修复：某些场景下，多数据源使用 JdbcTemplate 事务下使用报错的问题，感谢 @lifejwang11
- 修复：FieldQueryManager.java 在某些极端场景下出现 NPE 的问题，感谢 @loong0306
- 修复：同表连接查询，别名匹配不正确的问题，感谢 @qimincow
- 修复：QueryMethods.column 等构建列使用 as 方法设置别名无效的问题，感谢 @Suomm
- 文档：添加 QueryWrapper join 自身的相关示例
- 文档：常见问题添加启动失败说明列表，感谢 @Suomm
- 文档：优化关于 ActiveRecord 的相关文档，感谢 @Suomm
- 文档：修改 Auto-Mapper 的一些错误文档，感谢 @Suomm
- 文档：修改 APT 的配置描述错误的文档，感谢 @Suomm
- 文档：修改 APT generateEnable 描述错误的问题，感谢 @cijie
- 文档：修改代码生成器的示例代码错误的文档，感谢 @Suomm
- 文档：更新 MyBatis-Flex-Helper 的相关文档和截图
- 文档：优化 MyBatis 原生使用的相关文档，感谢 @pioneer-sun
- 文档：修改关于 FAQ 的相关描述错误问题，感谢 @wlf213



## v1.5.5 20230801:
- 新增：添加对 xml 分页查询的支持
- 新增：逻辑删除添加列默认值为 null 值时的构建功能，感谢 @Suomm
- 新增：QueryWrapper 添加 `clear()` 方法以复用，感谢 @yuanbaolong
- 优化：添加更多的 QueryWrapper.select() 方法
- 优化：添加全表更新或全部删除时的异常信息国际化
- 优化：移除 Mappers.java 一些无用的代码
- 优化：重构 ModifyAttrsRecordProxyFactory 以提升性能
- 优化：重构 Page.java，默认使用 long 统一相关参数
- 优化：对 ClassUtil/ConvertUtil 等一些工具类进行优化，感谢 @xinjump
- 优化：代码生成器 ignoreColumns 变更为只对Entity生成有效果，感谢 @jerryzhengsz1
- 优化：完善 Gitee 的 issue 模板配置，感谢 @Suomm
- 优化：Relations 附加条件参数值修改为 Object 类型，感谢 @Suomm
- 优化：重构 ActiveRecord 的关于关联查询的链式操作方法，感谢 @Suomm
- 修复：修复游标查询时，配置 `@Column(typeHandler =xxx)` 不生生效的问题 #I7PNUL
- 修复：修复达梦数据库数据大小写敏感的问题 #I7OYMN
- 修复：UpdateChain.toSQL() 在某些情况下出错的问题
- 修复：动态表名在 updateByQuery 中无效的问题
- 修复：多数据源、且数据源加密的情况下，无法正确解析 jdbcUrl 的问题
- 修复：数据解密器在某些场景下会多次触发解密的问题
- 修复：逻辑删除配置为字符串时，多添加单引号的问题，感谢 @Suomm
- 文档：添加自动映射的相关文档描述
- 文档：打印 SQL 的相关文档添加 MyBatis 原生配置的方式
- 文档：优化逻辑删除文档的一些代码展示示例有误的问题
- 文档：添加 xml 分页查询的相关文档和示例
- 文档：添加补充 Active Record 多种方式关联查询的文档，感谢 @Suomm
- 文档：优化多数据源的代码配置展示，感谢 @lhzsdnu
- 文档：修改链式操作的代码示例错误，感谢 @eltociear
- 文档：修改 SQL 审计的相关错别字，感谢 @cijie
- 文档：修改 MyBatisFlexCustomizer 文档的一些错别字，感谢 @q-alex



## v1.5.4 20230729:
- 新增：UpdateChain.of(entity) 方法，方便直接传入 entity
- 新增：TableInfo.buildQueryColumn() 方法，用于在泛型下构建 QueryColumn
- 新增：添加新的 Db.executeBatch() 方法，方便直接传入集合进行批量操作
- 新增：QueryWrapper 添加 where(consumer) 方法
- 新增：多数据源功能添加负载均衡的能力
- 新增：QueryWrapper 的.and() .or() 方法, 增加一个 condition 参数的方法，感谢 @jerryzhengsz
- 新增：添加 BaseMapper.selectOneWithRelationsByIdAs() 方法，感谢 @jerryzhengsz1
- 新增：添加异常信息国际化的支持，感谢 @Suomm
- 新增：添加主键逻辑删除处理器的支持，感谢 @Suomm
- 新增：在 Service 中可以直接获取链式更新 UpdateChain 方法，感谢 @Suomm
- 新增：ActiveRecord 添加 join 查询和 Relations 查询的功能，感谢 @Suomm
- 新增：代码生成器添加对 ActiveRecord 生成的支持，感谢 @Suomm
- 优化：修改 getPropertySimpleType 方法实现， 防止出现找不到类的问题，感谢 @dcrpp
- 优化：重构将 assertAreNotNull 从 FlexExceptions 移动到 FlexAssert 中，感谢 @Suomm
- 优化：重构 ActiveRecord 的一些方法，避免被 JSON 框架解析，感谢 @Suomm
- 优化：标记 BaseMapper.updateNumberAddByQuery() 为删除，在未来 v1.6.0 将会从项目里删除
- 优化：Maven 的 "artifact xxx has been relocated" 警告的问题，感谢 [@sppan24](https://github.com/sppan24)
- 优化：优化主键逻辑删除处理器逻辑，感谢 @Suomm
- 修复：代码生成器多次调用是出错的问题，感谢 @Suomm
- 修复：TableInfo 相关处理时无法获取 defaultConfig 的问题，感谢 @noear_admin @lhzsdnu
- 修复：代码生成器中的 mapper xml 默认生成路径错误的问题，感谢 @Suomm
- 修复：逻辑删除在设置字符串时，多添加了单引号的问题，感谢 @Suomm
- 修复：多数据源注解嵌套使用时，可能某些配置不生效的问题，感谢 @barql
- 文档：修复字段脱敏中的一些错别字的问题，感谢 @winnerself93586
- 文档：添加关于多数据源负载均衡的相关文档
- 文档：修复 Db + Row 的一些代码示例错误的问题，感谢 @w-mgitee
- 文档：修复链式操作相关的一些错别字的问题，感谢 @luyyyyy
- 文档：修复 apt 的一些描述错误的问题，感谢 @djxchi
- 文档：添加关于 ActiveRecord 关联查询的相关文档，感谢 @Suomm
- 文档：更新内置逻辑删除处理器文档，感谢 @Suomm
- 文档：修改 Query 查询的代码示例中的 Page 定义错误的问题，感谢 @eafonyoung



## v1.5.3 20230725:
- 新增：添加 UpdateChain 方便用于对数据进行更新
- 新增：添加对 ActiveRecord 设计模式的支持，感谢 @Suomm
- 新增：代码生成器 ColumnConfig 增加 propertyType. 可以用于自定通用属性的类型。感谢 @jerryzhengsz1
- 新增：添加 selectOneWithRelationsById(根据主表主键来查询 1 条数据) 方法，感谢 @barql
- 新增：QueryWrapper.groupBy 支持 Lambda 表达式的功能，感谢 @Suomm
- 新增：QueryWrapper 添加 `not like` 构建的支持
- 优化：重命名 QueryWrapperChain 为 QueryChain，保存和 UpdateChain 统一
- 修复：`@Relation` 关联查询注解，在指定 selectColumns 出错的问题，感谢 @zhy_balck
- 修复：代码生成器配置 `camelToUnderline` 属性时 entity 生成后编译错误的问题，感谢 @genomics_zcg
- 文档：优化 APT 的文档描述有错别字的问题，感谢 @zhangjx1992
- 文档：添加关于 ActiveRecord 的相关文档，感谢 @Suomm
- 文档：修改代码生成器对 EnjoyTemplate 的描述错误的问题
- 文档：添加更多关于链式查询的相关文档
- 文档：重构文档链接，链式操作的相关文档
- 文档：修改代码生成器对 EnjoyTemplate 的描述错误的问题



## v1.5.2 20230723:
- 新增：添加 QueryWrapperChain 用于链式调用查询或者操作数据，感谢 @Suomm
- 新增：添加 DbChain 链式调用 Db + Row 的相关方法和功能，感谢 @Suomm
- 新增：Relation 关联查询添加 `selectColumns` 配置，用于自定义查询指定列名
- 新增：代码生成器添加支持 Springdoc 的支持，感谢 @dgmico
- 新增：QueryWrapper 的 orderBy 添加 lambda 参数的支持
- 优化：重构 IService 统一批量操作方式，感谢 @Suomm
- 优化：Field Query 中 QueryWrapper 返回 null 值时，应该不进行属性查询，感谢 @Suomm
- 优化：移除 ConvertUtil.java 一些未用到的代码
- 优化：修改 Db.selectObjectList() 方法返回数据类型不明确的问题，感谢 @Suomm
- 修复：left join 多个条件，且多次引用相同表，后续的条件中别名无效的问题 #I7MI4O
- 修复：在某些场景下，在 Spring Controller 使用 `@UseDataSource` 设置当前数据源不生效的问题
- 修复：当 Entity 或者 VO 中定义数据类型为 `List<Map`> 时，映射出错的问题
- 修复：在 kotlin 下，使用枚举类作为条件参数时，sql 执行异常的问题，感谢 @liibang
- 修复：逻辑删除使用时间类型时，正常值初始化错误的问题，感谢 @Suomm
- 修复：在 kotlin 下，apt 无法正确找到 mybatis-flex.config 配置的问题
- 修复：代码生成器在多次调用生成时，可能出现 IllegalArgumentException 错误的问题
- 修复：嵌套事务下，且传播方式为 REQUIRED 时，当子事务抛出异常时出错的问题 #I7N8A1
- 文档：更新多表 `@Relation` 关联查询的相关文档
- 文档：添加关于 QueryWrapperChain 的相关文档
- 文档：修改逻辑删除的相关链接引用错误，感谢 @Suomm
- 文档：添加关于 DbChain 使用的相关文档，感谢 @Suomm



## v1.5.1 20230719:
- 新增：添加 IService.saveBatchSelective() 批量保存的方法
- 新增：添加 Relation 默认查询深度在 spring 的 application.yml 进行配置支持 #I7LLRU
- 新增：添加 FlexGlobalConfig.getDataSource() 方法，方便获取数据源
- 新增：QueryColumn.java 增加不含百分号的 like 方法，感谢 @aqnghu
- 新增：QueryWrapper.create 可以通过 Entity 进行实例化的功能，感谢 @yasser
- 优化：对 Page.java 的默认值优化，防止前缀不传值是查询 SQL 错误的问题，感谢 @Suomm
- 优化：修改 FlexGlobalConfig.java 的注释文字错别字的问题，感谢 @lhzsdnu
- 优化：修改 DbType 和 LimitOffsetProcessor 下的 sinodb 文字错误的问题，感谢 @wujl
- 修复：多次 left join 相同表，设置的别名无效的问题 #I7M3ZW
- 修复：通过 join 查询数据，在某些情况下主键值无法赋值的问题
- 修复：在 Solon 下对 FlexGlobalConfig 进行初始化时，DbType 没有赋值的问题
- 修复：banner 配置无法根据配置显示隐藏的问题，感谢 @wu-zhihao
- 修复：Relation 注解在通过中间表询时，在某些场景下出错的问题，感谢 @Suomm
- 文档：修改代码生成器的 TableDef 默认值描述错误的问题，感谢 @lhzsdnu
- 文档：修改代码生成器的 TableDef 默认值描述错误的问题，感谢 @lhzsdnu



## v1.5.0 20230716:
- 新增：ToMany 的 Relation 注解添加对 map 数据类型的支持 #I7KW9U
- 新增：代码生成器添加 EntityConfig 关于数据的自定义配置，感谢 @Suomm
- 新增：APT 和代码生成器添加辅助类字段注释的功能，感谢 @Suomm
- 新增：Field Query 添加对嵌套字段的查询支持，感谢 @Suomm
- 新增：代码生成器添加对 Swagger 注解自动配置的支持，感谢 @Suomm
- 新增：代码生成器添加自定义 comments 格式化的支持，感谢 @Suomm
- 新增：APT 新增 processor.mapper.annotation 配置是否生成 @Mapper 注解，感谢 @cainiao3853
- 优化：统一 GlobalConfig 中每部分 setter/getter 命名，感谢 @Suomm
- 优化：RelationManager 添加关于自动清除配置的相关功能
- 修复：Oracle 方言下，代码生成器无法正常获取字段注释的问题，感谢 @galvin_chen
- 修复：insertSelectiveWithPk 方法给主键赋值后会拼接两次id的问题 #I7L6DF
- 修复：修改 APT 和代码生成器的单词拼写错误的问题，感谢 @lhzsdnu
- 文档：修改多对多的注解描述错误的问题，感谢 @jl_0417
- 文档：更新关于一对一和一对多的中间表查询文档
- 文档：添加关于 `processor.mapper.annotation` 的相关文档，感谢 @cainiao3853
- 文档：代码生成器添加关于自定义方言的相关文档
- 文档：关联查询添加对 map 类型的相关文档



## v1.4.9 20230713:
- 新增：Relation 注解支持递归查询的功能及递归深度设置的功能
- 新增：Relation 注解添加对 vo、dto 等进行关联查询时，可以指定表的配置支持
- 新增：Relation 注解支持通过中间表进行 join 场景的支持
- 新增：Relation 注解添加自定义条件 extraCondition 配置的支持
- 新增：Relation 注解添加可以忽略某个注解查询的支持
- 新增：数据方言新增对 星瑞格数据库 的支持，感谢 @wujl
- 新增：BaseMapper.updateByMap() 方法，感谢 @Suomm
- 新增：QueryWrapper 构建添加 ture、false 常量函数，感谢 @Suomm
- 优化：静态常量使用 final 修饰，感谢 @meng.liu3
- 优化：为项目添加 .editorconfig 文件，统一不同作者协助的代码格式化
- 优化：UpdateWrapper 添加 of 方法，用于替代强转更新
- 优化：重构 BaseMapper 的文档生成器，感谢 @Suomm
- 优化：添加码生成器 tableDef 字段排序，感谢 @Suomm
- 修复：使用 SqlServer 2005 方言查询时出错的问题
- 修复：selectListWithRelationsByQueryAs 出现数据类型转换异常的问题
- 修复：Relations 注解在用户定义包装类型时可能导致结果出错的问题
- 修复：代码生成器配置 setListenerClass 时，结果不正确的问题，感谢 @cainiao3853
- 修复：APT 功能在使用 gradle 时无法读取配置文件的问题，感谢 @Suomm
- 文档：更新 Relations 注解的相关文档
- 文档：调整 ChangeLog 放到首页菜单
- 文档：完善代码生成器的相关配置文档，感谢 @Suomm



## v1.4.8 20230709:
- 新增：`@RelationXXX` 相关注解添加可以指定 dataSource 的配置功能
- 新增：添加 BaseMapper.insertSelectiveWithPk() 方法
- 新增：QueryWrapper 构建的 SQL 添加 If, IfNull 支持，感谢 @Suomm
- 新增：添加 FlexAssert 类，用于常见的异常条件断言，感谢 @Suomm
- 优化：为 APT 添加 final 关键字以及字段排序的功能，感谢 @Suomm
- 优化：APT 的 large 字段或者 logicDelete 字段不应被添加在 default_column 中，感谢 @Suomm
- 优化：移动 BaseMapper.doPaginate 到 MapperUtil，感谢 @Suomm
- 优化：完善 EntitySqlProvider、RowSqlProvider 等的相关 javadoc 内容，感谢 @Suomm
- 优化：重构逻辑删除和多租户代码，在必要时其条件应该往前移动 #I7IVCR
- 优化：移除 RowSqlProvider 不必要的重复的方法
- 修复：执行 updateByQuery() 未调用 Entity 的 OnUpdate 监听的问题 #I7JDJ2
- 修复：QueryWrapper 在构建 `属性.in(select from...)` 时出错的问题
- 文档：补充增删改查新添加的 BaseMapper 的相关方法



## v1.4.7 20230707:
- 新增：`@RelationManyToMany` `@RelationManyToOne` `@RelationOneToMany` `@RelationOneToOne` 4 个注解用于关联查询
- 新增：为 QueryMethods 添加更多的 SQL 函数重载，感谢 @Suomm
- 新增：代码生成器添加 @Mapper 主键的启用配置，感谢 @Suomm
- 新增：BaseMapper 添加 selectRowsByQuery() 方法
- 优化：重构 selectCountByQuery 方法，移除不必要的实现类，感谢 @Suomm
- 优化：重构 TableInfo.buildResultMap()，防止在某些极端情况出现赋值错误的问题
- 修复：代码生成器 ControllerGenerator 的 OverwriteEnable 配置不生效的问题
- 文档：添加关于 QueryMethods 的一些用法和示例，感谢 @Suomm
- 文档：修改 apt 配置错误的问题，感谢 @Suomm
- 文档：添加关于 sql 函数的一些函数说明
- 文档：添加关于关联查询注解的相关文档



## v1.4.6 20230704:
- 新增：新增 UpdateWrapper 类，可以通过 entity 转换为 UpdateWrapper 进行数据更新
- 新增：添加 QueryWrapper.select(QueryColumn[]... queryColumns) 方法
- 新增：添加 BaseMapper.updateNumberAddByQuery 重载方法，感谢 @Suomm
- 新增：添加 110+ SQL 常见的函数构建，满足日常开发所需，感谢 @Suomm
- 优化：对分页查询的 groupby 和 distinct 进一步优化，感谢 @Suomm
- 优化：对 Row 进行优化，使之更加简单、好用
- 修复：有父子继承时，如果有相同属性，生成的类中也会有相同的2个属性的问题，感谢 @piggsoft
- 修复：在某些极端的情况下，由于 MappedStatement 缓存造成的类型转换异常的问题
- 修复：逻辑删除有 groupBy 和 join 的分页查询时，计算的总量包含已删除数据的问题 #I7HVXT
- 文档：优化 Field Query 的相关文档
- 文档：添加关于 UpdateWrapper 使用的相关文档
- 文档：更新关于 SQL 函数的相关文档



## v1.4.5 20230703:
- 新增：添加 sql "CONCAT" 函数的使用支持，感谢 @Suomm
- 新增：添加 BaseMapper.selectObjectByQueryAs 方法，感谢 @Suomm
- 新增：添加 @ColumnAlias 注解，并解决在 join query 下可能出现的错误赋值的问题，感谢 @Suomm
- 新增：添加 MybatisFlexAdminAutoConfiguration 对自动进行 SQL 审计发送的支持，感谢 @Suomm
- 优化：BaseMapper.selectOne 添加对 map、condition 等自动添加 limit 1 的支持
- 优化：重构 FunctionQueryColumn 已适配更多的函数，感谢 @Suomm
- 优化：在某些情况下，执行 SQL 出错没有抛出原生错误的问题
- 优化：移动 DefaultMessageFactory.getHostIp 到 HttpUtil，感谢 @Suomm
- 优化：重构 APT 的配置名称。 !!! 破坏性更新
- 文档：优化由于版本造成的一些错误文档
- 文档：添加列计算的使用文档和示例
- 文档：修改某些细节错别字

**APT 配置名称修改如下：**
- processor.allInTables ---> processor.allInTables.enable
- processor.tablesPackage ---> processor.allInTables.package
- processor.tablesClassName ----> processor.allInTables.className
- processor.mappersGenerateEnable ----> processor.mapper.generateEnable
- processor.baseMapperClass ---> processor.mapper.baseClass
- processor.mappersPackage ---> processor.mapper.package
- processor.tablesNameStyle ---> processor.tableDef.propertiesNameStyle
- processor.tablesDefSuffix ---> processor.tableDef.instanceSuffix
- processor.entity.ignoreSuffixes ---> processor.tableDef.ignoreEntitySuffixes


## v1.4.4 20230628:
- 新增：在 SpringBoot 下添加 `@UseDataSource` 对 IService 和 Controller 支持的功能，感谢 @Suomm
- 优化：IService 中的 saveOrUpdate 保存逻辑应该与同类 save 逻辑保持一致； #I7G8MC
- 优化：为更多的 Java 代码完善代码注释已经添加  package-info.java，感谢 @Suomm
- 修复：当使用 ArrayTypeHandler 配置时，出现 NPE 的问题
- 修复：Brackets 的 containsTable 方法验证错误，导致分页时 count 优化不准确的问题，感谢 @Suomm
- 文档：优化多数据源的相关文档



## v1.4.3 20230624:
- 修复：紧急修复 v1.4.2 Push 到中央仓库无法同步的问题



## v1.4.2 20230624:
- 新增：新增数据源加密内容的解密器配置功能
- 新增：QueryWrapper 添加 with 的 SQL 构建方法;  #I7E19B
- 新增：新增 MyBatisFlexCustomizer.java 方便用户对 MyBatisFlex 进行初始化配置
- 新增：AbstractLogicDeleteProcessor 接口，方便用户自定义逻辑删除处理器，感谢 @Suomm
- 新增：添加 Boolean/Integer/Datetime/TimeStamp 类型的逻辑删除功能，感谢 @Suomm
- 新增：FlexGlobalConfig 支持通过 Spring 的 application.yml 进行配置的功能，感谢 @Suomm
- 新增：动态表名添加对 Spring @Configuration 自动配置的支持
- 新增：多租户 TenantFactory 添加对 Spring @Configuration 自动配置的支持
- 新增：逻辑删除处理器添加对 Spring @Configuration 自动配置的支持
- 优化：APT 功能的配置文件由 `mybatis-flex.properties` 移动到根目录下的 `mybatis-flex.config`, !!!破坏性更新
- 优化：移除部分无用的、注释掉的代码块以及一些未使用的常量等，感谢 @Suomm
- 优化：完善接口、类或方法相关注释，感谢 @Suomm
- 优化：maven 编辑添加 latten-maven-plugin + reversion 机制
- 优化：代码生成器生成辅助类与 APT 生成的类名保持一致，感谢 @Suomm
- 优化：重构 apt 功能，以及修复可能存在的 NPE 问题，感谢 @Suomm
- 修复：逻辑删除、多租户等子表过滤应该在 join 不应该在 where 的问题，#I7F03L
- 修复：禁止全表更新失效的问题，感谢 @Suomm
- 修复：代码生成器生成策略中的 schema 和 tableConfig 中的 schema 冲突问题，感谢 @Suomm
- 文档：使用 Spring Boot 项目构建快速开始示例。
- 文档：更新 pagehelper 配合 mybatis-flex 使用的相关文档，感谢 @Suomm
- 文档：添加关于 SpringBoot 配置的相关文档，感谢 @Suomm
- 文档：添加关于数据源加密使用的相关文档



## v1.4.1 20230620:
- 新增：分页查询 Page 对象添加否优化 count 查询的选项，感谢 @王帅
- 新增：添加 LogicDeleteProcessor.java 用于构建自定义的逻辑删除功能
- 优化：完善后添加类的相关注释、版权信息等内容，感谢 @王帅
- 优化：QueryColumn 添加构造方法 QueryColumn(String tableName, String name)
- 修复：Oracle 下的 as 关键字错误的问题
- 修复：多表联查时，逻辑删除字段和租户字段，只过滤主表，未过滤子表的问题 #I7EV67
- 修复：低版本 spring-cloud 使用 bootstrap.yml 拉取不到 nacos 中多数据源配置的问题，感谢 @王帅
- 修复：使用 druid-spring-boot-starter 时 mybatis-flex 多数据源不生效，感谢 @王帅
- 文档：更新 APT 文档，添加实体类不在同一个包说明，感谢 @王帅
- 文档：添加找不到依赖 faq 说明，感谢 @王帅
- 文档：逻辑删除添加关于 LogicDeleteProcessor 的相关文档



## v1.4.0 20230618:
- 新增：LogicDeleteManager，用于处理跳过逻辑删除的场景
- 新增：BaseMapper 新增 insertWithPk 方法，用于插入带有主键的 Entity 数据
- 新增：left join 等添加对基本类型集合属性的支持，感谢 @王帅
- 优化：重命名 MaskManager.withoutMask() 方法为 "execWithoutMask"
- 修复：通过方言动态添加 queryWrapper 条件，出现 No value specified for param 错误的问题
- 修复：GroupBy FunctionColumns 出现 sql 构建错误的问题
- 修复：CacheableServiceImpl 无法获取正确的 TableInfo 的问题
- 修复：远程数据源配置（比如 Nacos）无法正确拉取的问题，感谢 @王帅



## v1.3.9 20230617:
- 新增：Db.txWithResult 方法，用于执行有返回结果的事务
- 优化：更新 OracleDialect 的相关关键字
- 优化：Page.java 允许传入小于 0 TotalRow 数据
- 修复：SqlConsts 函数名出错的问题，感谢 @王帅
- 修复：当 Entity 有泛型的 BaseEntity 时，表构建出错的问题，感谢 @王帅
- 修复：当 Entity 在不同的包里，APT 生成在一个包的 bug，感谢 @王帅
- 文档：更新关于事务相关的文档
- 文档：更新关于数据权限的相关文档
- 文档：更新关于多表查询的相关文档
- 文档：新增关于 QueryWrapper 函数构建的相关文档



## v1.3.8 20230614:
- 新增：添加 FlexDataSource.removeDatasource() 方法，#I7CQU9
- 新增：代码生成器添加 Schema 配置的支持，感谢 @Font_C
- 新增：SQL 构建添加 year/month/day 等更多的函数方法 #I7A7ZA
- 新增：添加 package-info.java 方便开发者理解包内容，感谢 @王帅
- 优化：重构 FlexResultSetHandler，使代码更加轻量易懂
- 优化：重构 TableInfo.buildResultMap，使之在 left join 等场景下减少错误的可能性
- 优化：likeLeft 和 likeRight 行为替换，使之更加符合查询直觉。!!! 破坏性更新
- 优化：移除 TableInfo 的 joinTypes 内容
- 优化：添加 SQL 常量字符串 SqlConsts，对方言等 SQL 进行优化，感谢 @王帅
- 修复：某些分页场景下出现 "Every derived table must have its own alias" 错误的问题，#I7CUE8
- 修复：在某些场景下 EnumWrapper.java 出现 NPE 的问题, #I7CWTE
- 修复：selectListByQueryAs 使用实体类对象导致查询不出来数据，感谢 @王帅
- 修复：实体类不支持父类为泛型主键的问题，感谢 @王帅 #I7CZGP
- 修复：代码生成器当配置空的 coment 导致 setTableCommentFormat 出错的问题，感谢 @Font_C
- 修复：QueryWrapper.clone 并为对 elseValue 进行深度克隆的问题，感谢 @王帅
- 修复：数据库字段已下划线 _ 开头或者结尾时，生成的 entity 字段配置不正确的问题
- 文档：添加游标查询的相关文档
- 文档：faq 新增更多的常见问题
- 文档：添加更多代码生成器相关的文档



## v1.3.7 20230612:
- 新增：添加 QueryWrapper.select(String) 和 QueryWrapper.select(lambda) 方法
- 新增：添加 UseDataSource 注解对 mapper 类作用的支持
- 新增：QueryWrapper 的 clone() 方法，方便用于深度复制当前的 QueryWrapper，感谢 @王帅
- 优化：在 id 没有 setter 的情况下，错误信息不明确的问题，感谢 @chenjian835
- 优化：修改 CPI 注释内容错别字的问题，感谢 @Alex
- 优化：修改 QueryCondition 的 "before" 属性为 "prev"
- 优化：重构 case when 的构建功能，方便其进行序列化
- 优化：DistinctQueryColumn 构建 SQL 多出一个空格的问题
- 优化：使用 QueryWrapper.select() 无参方法时，方法调用不明确的问题，感谢 @王帅
- 优化：代码生成器提取 lombok 的配置到模板，方便用户自定义模板功能，感谢 @王帅
- 修复：field query 方式不支持集合子类型的问题，感谢 @王帅
- 修复：分页查询中有 group by 和 distinct 的 count 查询有误的问题，感谢 @王帅
- 修复：在 select ... from(select...) 中，子查询没有括号导致 sql 解析错误的问题，感谢 @王帅
- 修复：updateNumberAddByQuery 在传入小数点数据时，出错的问题
- 文档：修改 QueryWrapper 的部分错别字，感谢 @锟斤拷
- 文档：更新一对多、多对一的相关文档
- 文档：更新多数据源的相关文档



## v1.3.6 20230609:
- 新增：QueryWrapper 添加对 Lambda 构建的支持
- 新增：APT 添加 processor.tablesDefSuffix 配置 Table 后缀的支持
- 新增：多表查询支持自动映射实体类中的 association 和 collection 类型，感谢 @王帅
- 新增：BaseMapper.selectCursorByQuery() 用于游标查询的支持
- 优化：QueryColumn 添加一个 QueryTable 参数的构造器
- 优化：移动 BaseMapper.__queryFields() 方法到 MapperUtil
- 优化：修改 QueryWrapper 的 leftJoinIf 方法为 leftJoin
- 修复：代码生成器 EnjoyTemplate 出现 IllegalAccessException 错误的问题
- 修复：修复 Oracle 批量新增出现 SQL 语法错误的问题
- 修复：QueryWrapper 不能使用超过一次的问题 https://github.com/mybatis-flex/mybatis-flex/issues/49
- 文档：QueryWrapper 文档添加关于 APT 的说明
- 文档：QueryWrapper 文档添加 Lambda 构建的代码示例
- 文档：一对多添加关于自动映射的相关文档，感谢 @王帅



## v1.3.5 20230606:
- 新增：代码生成器添加生成 spring-cache 缓存模板配置。感谢 @王帅
- 新增：Db + Row 添加 schema 的支持。感谢 @Font_C
- 新增：IService 添加 pageAs 方法。感谢 @王帅
- 新增：添加更多 column 工具方法
- 优化：Page.java 在传入为 0 的 pageNumber 时添加错误提示
- 优化：RawValue.java 修改为 RawFragment 以适应更多场景
- 优化：修改 WrapperUtil.getChildSelect() 方法为 getChildQueryWrapper
- 优化：修改 BaseMapper.selectListByQueryAs 以支持获取 `List<Number>` 和 `List<String>`
- 优化：工具类添加 private 构造方法。感谢 @王帅
- 修复：在修改和删除时，schema 配置无效的问题
- 修复：update 方法应该替换 CachePut 注解为 CacheEvict。感谢 @王帅
- 修复：代码生成器生成的缓存 Service 缓存 key 错误的问题。感谢 @王帅
- 修复：错别字 taked 修改为 took。感谢 @王帅
- 文档：完善代码生成器的相关文档。感谢 @王帅
- 文档：同步 IService 最新的文档，删除旧版本的一些方法。感谢 @王帅
- 文档：修复 sqlSessionTemplate 错别字的问题。感谢 @阿志同学
- 文档：添加关于 QueryWrapper 序列化的相关文档
- 文档：继续完善 "常见问题" 的相关文档



## v1.3.4 20230601:
- 新增：添加 Spring 下的 CacheableServiceImpl 实现类，方便用户缓存。感谢 @王帅
- 优化：重构 IService，使之有更好的方法服用。感谢 @王帅
- 优化：重命名 QueryWrapper 的 "toDebugSQL()" 方法名为 "toSQL()"
- 修复：当使用 QueryWrapper 未指定 from 时，schema 出错的问题
- 修复：当配置 config-location 时，出现 IllegalArgumentException 错误的问题
- 修复：case when else 中出现的 column 无法转换的问题 #I7A0B0
- 文档：添加关于 CacheableServiceImpl 使用的一些文档。感谢 @王帅



## v1.3.3 20230530:
- 新增：添加动态表名和动态 Schema 的支持 #I6VRMF #I6WS3E #I72X21
- 新增：添加 @Table(schema=xxx) 配置的支持 #I6VD6U
- 新增：Left join 等关联查询时，支持直接赋值对象属性的功能
- 新增：添加 QueryWrapper.toDebugSQL() 方法，方便用于调试
- 优化：Entity 父子类定义了相同字段时，应已子类优先
- 优化：对逻辑删除定义的非 Number 和 Bool 类型的 SQL 构建优化
- 优化：升级 MyBatis 到 3.5.13 最新版本
- 优化：升级 MyBatis 后，删除冗余的 FlexXMLConfigBuilder 实现
- 优化：升级 solon 版本，以支持原生编译功能，感谢 @西东
- 修复：分页查询在 left join 优化是，错误移除的问题
- 文档：优化 多表查询 的相关文档
- 文档：添加枚举属性构建 QueryWrapper 的相关文档
- 文档：添加自定义 MyBatis 的 Configuration 的文档
- 文档：添加动态表名和动态 Schema 的相关文档



## v1.3.2 20230528:
- 新增：select (field1 * field2 * 100) as xxx from ... 的 SQL 构建场景
- 优化：ClassUtil.wrap 方法修改为 getWrapType
- 优化：重构 BaseMapper.selectListByQueryAs() 方法，使其更加通用
- 优化：重构 EnumWrapper.java，使之方法和变量更加明确易读
- 优化：WrapperUtil.buildAsAlias 方法，使之在多种场景下有统一行为
- 优化：优化 Row，添加 prepareAttrs() 等方法
- 修复：EnumWrapper 对其 getter 方法判断错误的问题
- 文档：更新一对多、多对一的子查询相关文档



## v1.3.1 20230526:
- 新增：分页查询添加关联字段查询功能；
- 新增：Mapper 添加 updateNumberAddByQuery 方法，用于 update table set xxx = xxx + 1 的场景；
- 优化：添加 FieldWrapper 使得关联字段查询拥有更高的性能
- 优化：优化 EnumWrapper 使之逻辑更加清晰简单
- 优化：字段子查询不在需要配置 type
- 优化：代码生成器 remarks 修改为 comment; 感谢 @王帅
- 优化：代码生成器 GlobalConfig 拆分，使之更加直观; 感谢 @王帅
- 优化：代码生成器新增注释生成配置； 感谢 @王帅
- 优化：代码生成器新增 mapper xml 生成功能； 感谢 @王帅
- 优化：代码生成器新增 package-info.java 生成功能； 感谢 @王帅
- 优化：代码生成器新增 Controller 生成功能； 感谢 @王帅
- 优化：代码生成器每个生成的文件，单独支持是否覆盖已生成的文件； 感谢 @王帅
- 优化：代码生成器新增设置模板文件位置，可以使用指定的模板生成文件； 感谢 @王帅
- 修复：select max(select(...)) 等函数内部有参数时，无法获取的问题；
- 文档：更新代码生成器文档。感谢 @王帅
- 文档：优化 QueryWrapper 的相关文档



## v1.3.0 20230525:
- 新增：新增 一对多、多对一 查询功能
- 新增：为 SqlServer 添加独立的 LimitOffset 处理器
- 新增：QueryWrapper 新增 "for update" 的 SQL 构建支持
- 新增：QueryWrapper 新增 select convert(...) 的 SQL 构建支持
- 新增：QueryWrapper 新增 select case when ... then 的 SQL 构建支持
- 优化：APT 默认生成独立文件，之前所有 APT 生成在同一个文件修改为可配置
- 修复：Table.camelToUnderline 注解在 APT 上配置不生效的问题
- 修复：多租户设置 TenantId 时，在某些极端情况下出现异常的问题
- 文档：新增 一对多、多对一 的相关文档
- 文档：新增 select case when ... then 的 QueryWrapper 示例
- 文档：添加关于 hint 的相关描述



## v1.2.9 20230523:
- 新增：字段添加对 Byte.class 类型的支持 #I76GNW
- 修复："select * ,(select ...) from ..." 第二个 select 有参数时出错的问题
- 修复：QueryCondition 在 left join 查询是出现 NPE 的问题
- 修复：修改 QueryWrapper 添加 limit(1) 时，SQLServer 方言构建 SQL 出错的问题
- 修复：修改 TDENGINE 方言出错的问题
- 修复：baseMapper.selectOneByQueryAs() 查询出错的问题
- 修复：当自定义 TypeHandler 时，通过 UpdateEntity 更新时，TypeHandler 无效的问题
- 优化：重构 TableInfo.appendConditions() 代码
- 优化：修改错别字 "那些" 为 "哪些"
- 文档：更新 "Mybatis" 为 "MyBatis" #I72DWO
- 文档：添加使用 druid-spring-boot-starter 出错到常见问题里



## v1.2.8 20230522:
- 新增：新增 select id,(select...) from 的支持
- 优化：Solon 插件增加 RowMapperInvoker 注入和 FlexGlobalConfig 可事件扩展的支持，感谢 @西东
- 优化：分页的 count 查询默认去掉 left join 和 order by 等
- 优化：APT 的 ALL_COLUMNS 修改 table.*
- 文档：添加 hint 的相关文档
- 文档：优化 mybatis-flex-solon-plugin 的使用文档
- 文档：优化 queryWrapper 的相关文档



## v1.2.7 20230520:
- 新增：添加 solon 关于 ServiceImpl 的实现
- 新增：left join 等 join 查询添加 as(lambda) 的支持
- 优化：优化 EnumWrapper.java 使之具有更高的性能
- 优化：迁移 IService 到 core 目录
- 优化：重命名 Db.updateBatchEntity 为 Db.updateEntitiesBatch
- 修复：逻辑删除设置 bool 类型在 postgresql 下出错的问题
- 文档：添加批量操作的相关文档说明
- 文档：添加关联查询的相关文档



## v1.2.6 20230518:
- 新增：IService 添加 updateBatch 方法，感谢 @Saoforest
- 优化：findById 默认返回 isLarge 的字段 #I73SJY
- 优化：WrapperUtil.getValues() 并直接读取枚举内容
- 修复：ClassUtil 修复无法正确读取 JDK 动态代理超类问题，感谢 @Saoforest
- 修复：批量执行每一个批次会少 1 条数据的问题，感谢 @笨小孩
- 文档：添加数据权限的相关文档



## v1.2.5 20230517:
- 新增：Db.executeBatch 方法，用于批量操作
- 新增：Db 工具类添加基于 Entity 的 updateBatch 方法，感谢 @黄沐鸿
- 新增：KeyGenerators.java 方便进行主键生成策略配置
- 新增：APT 的 mybatis-flex.properties 文件添加使用 ClassLoader 读取，方便读取 jar 的内容，感谢 @XiaoLin
- 新增：QueryWrapper 新增 hash join 的支持
- 新增：QueryWrapper 新增 sql hint 的支持
- 优化：添加 configuration-processor，实现 yaml 配置自动提示，感谢 @tan90
- 修复：v1.2.4 新增的 paginateAs 异常问题 #I73BP6
- 修复：v1.2.4 版本造成的 Db.paginate 出错的问题
- 文档：优化 id 主键生成器的相关文档



## v1.2.4 20230515:
- 新增：Db.updateBatch() 方法，用于批量修改或者插入等场景
- 新增：通过雪花算法生成数据库主键，内置雪花算法，感谢 @王帅
- 新增：QueryCondition 可以直接传入枚举变量，自动读取 @EnumValue
- 新增：代码生成器添加 Service、ServiceImpl、Controller 的生成功能，感谢 @王帅
- 新增：Db 和 BaseMapper 添加 selectObject 和 selectObjectList 等方法
- 新增：BaseMapper 添加 selectOneByQueryAs 等方法，方便在 left join 等场景直接转换为 dto vo 等;
- 优化：不变属性添加 final 关键字，感谢 @庄佳彬
- 优化：修改拼写错误，processer->processor， 感谢 @庄佳彬
- 优化：Page.of() 方法
- 优化：为 IService.java 添加常用的方法
- 优化：修改 SqlUtil.retBool 为 SqlUtil.toBool
- 修复：orderBy 参数里传入 null 或者 空值，就会抛出异常的问题 #23
- 文档：优化 mybatis-flex.com 的目录结构
- 文档：新增内置主键生成器说明文档，感谢 @王帅
- 文档：修正数据脱敏中的一些描述错误
- 文档：IService 文档添加代码示例
- 文档：优化 "部分字段更新" 的相关文档
- 文档：更新代码生成器的相关文档，感谢 @王帅
- 文档：添加关于 BaseMapper 进行关联查询的相关文档
- 文档：添加在 Spring 的场景下使用 @Transactional 注解的注意事项



## v1.2.3 20230511:
- 新增：MaskManager 添加 withoutMask 模板方法，感谢 @pengpeng
- 新增：TenantManager 添加 withoutTenantCondition 模板方法，感谢 @pengpeng
- 新增：代码生成器添加 GlobalConfig.others 属性，方便自定义 generator 的配置
- 修复：v1.2.2 使用 @Transactional 出现的 Can't call commit 的问题 #I71X6X
- 文档：纠正错别字 "事物" 为 "事务"
- 文档：更新代码生成器的相关文档



## v1.2.2 20230510:
- 新增：增强 Service 及其实现类，感谢 @王帅
- 修复：where子句无任何符合的条件时，逻辑删除字段处理错误 #I70OIA
- 修复：在使用事务的情况下，无法切换数据源的问题 #I70QWU
- 修复：Entity 有配置 typeHandler 时，通过 RowUtil.toEntity 转换异常的问题 #I70XGX
- 文档：代码生成器添加生成其他产物的相关文档
- 文档：新增顶级 Service 接口说明文档，感谢 @王帅
- 文档：常见问题添加关于 PageHelper 集成的相关文档



## v1.2.1 20230506:
- 新增：代码生成器添加 tableDef 的生成功能；感谢 @笨小孩
- 新增：增指定批次批量删除的方法，防止请求时间过长异常；感谢 @笨小孩
- 优化：Mapper 配置所对应的 XML 文件 增加默认值；感谢 @lhzsdnu
- 优化：拆分APT模块, 优化APT配置说明；感谢 @snow
- 优化：添加  ClassUtil.isArray  方法用于重构
- 优化：重命名 UpdateEntity.wrap 为 UpdateEntity.of #I6Z7HK
- 优化：重构 OracleDialect 默认不加双引号；感谢 @MyronLi
- 优化：重构代码生成器模块，新增 IGenerator 接口
- 优化：修改 BaseMapper 的一些注释错误
- 修复：Oracle 下查询报错的问题 #I6Z7K4
- 修复：insertSelective 报错的问题 #12
- 修复：全局监听器父类注册是接口时无法触发监听器 #17
- 修复：因 @NotBlank(message = "xxx") 到 QueryColumn 无法生成的问题 #I6ZPD1
- 修复：不支持自己扩展 mapper 的问题 #I6ZTS3
- 修复：Db + Row 工具的使用枚举映射异常问题 #I6ZW2G
- 修复：批量新增逻辑删除字段有问题 #I6ZX5N
- 修复：left join (select ...) 子查询参数解析错误 #I709S1



## v1.2.0 20230426:
- 新增：FlexGlobalConfig.setDefaultConfig 方法，使其扩展更加灵活，用于适配 solon
- 新增：BaseMapper.insertBatch(entities,size) 方法，自定义分批插入；感谢 @庄佳彬
- 新增：Entity 的监听器可以配置为父类 BaseEntity 的支持
- 新增：Apt 添加自定义 字段名命名风格 的支持 #I6YGRG
- 修复：processor.tablesClassName 配置不起作用的问题；感谢 @玩具猫
- 修复：left join 关联查询 on 有第二个条件赋值错误的问题； #I6YT2R
- 修复：审计日志中赋值异常的问题 #I6Z1R8
- 修复：修复 createIdKeyGenerator 对 keyType.None 判断错误的问题
- 优化：修改 @Table 注解，删除其 @Inherited
- 文档：重构文档结构、使之更加清晰；感谢 @snow
- 文档：同步新版本 apt 功能文档



## v1.1.9 20230424:
- 新增：根据 id 查询数据时，返回默认字段而非全部字段；感谢 @wnp
- 新增：APT 添加忽略 entity 后缀的配置支持
- 修复：where 中第一个括号处理不正确的问题  #I6XXWR
- 修复：批量插入且配置 onInsert 时，出现错误的问题
- 修复：批量插入 row，且为 row 设置主键时，出错的问题 #I6Y8ZU
- 修复：Oracle 在某些情况下出现 SQLSyntaxErrorException 错误的问题 #I6Y6MZ
- 修复：RowUtil.printPretty 在某些情况下显示数据不正确的问题
- 修复：APT 无法生成 @NotBlank 注解字段的问题
- 优化：更新 apt 的 mappersGenerateEnable 默认值为 false
- 文档：优化 APT 的相关文档
- 文档：优化 快速开始 的相关文档
- 文档：添加 column 全局配置的相关文档



## v1.1.8 20230422:
- 新增：添加 RowUtil.printPretty() 方法，用于调试
- 新增：代码生成器生成代码默认添加注释的功能
- 新增：添加 BaseEntity.insertSelective() 方法  #I6XS9Z
- 修复：子查询时，逻辑删除字段的未添加逻辑删除字段 #I6X4U8
- 修复：TransactionTemplate.execute 失效的问题 #I6XSHH
- 修复："EXIST" 关键字错误的问题 #I6XTVB
- 优化：Row 添加列序号的功能，数据库返回结果相同列不再覆盖
- 文档：添加 使用 Mybatis 原生功能的相关文档
- 文档：添加 支持的数据库类型 的相关文档
- 文档：Db + Row 添加 left join 转换 entity 的文档
- 文档：优化 快速开始 的相关文档
- 文档：APT 文档添加关于 Gradle 构建的一些内容



## v1.1.7 20230421:
- 优化：将 ConsoleMessageCollector 的 getFullSql 方法移入 AuditMessage，方便重用 感谢 @pengpeng
- 修复：配置 mybatis-flex.mapper-locations 在某些场景下会出现 NPE 的问题 #I6X59V
- 修复：子查询时，子 SQL 的逻辑删除字段未添加 #I6X4U8
- 修复：启用乐观锁 和 逻辑删除时，在某些极端情况下会出现 argument type mismatch 异常的问题 感谢 @pengpeng
- 文档：APT 添加和 Lombok、Mapstruct 整合的文档
- 文档：完善补充 "打印SQL" 的相关文档



## v1.1.6 20230419:
- 新增：QueryCondition 添加 when(Predicate) 方法，感谢 @落羽er
- 新增：代码生成器 ColumnConfig 添加 tenantId 字段配置，用于代码生成时添加 @Column(tenantId=true) 注解，感谢 @pengpeng
- 新增：代码生成器 TableConfig 添加 mapperGenerateEnable 配置，感谢 @pengpeng
- 新增：代码生成器添加 IDialect.MYSQL 方言
- 优化：FlexEnumTypeHandler.java 完善对 public field 的读取
- 优化：代码生成器生成 java 文件时，打印生成目录
- 修复：flex 和 lombok 以及 mapstruct 同时使用时，APT 配置文件不生效的问题。 #I6WTN6
- 修复：使用 QueryMethods.count() 时出错的问题。#I6X2M6
- 文档：修改 SQL 审计模块的文档错误，感谢 @lhzsdnu



## v1.1.5 20230418:
- 新增：增加对 @Table 自动生成 Mapper 的控制属性 mapperGenerateEnable。感谢 @草语
- 新增：QueryCondition 自动忽略 null 值的功能 #I6WCS9
- 新增：增加 APT Mapper 自动生成时，可通过 mybatis-flex.properties 自定义父类的支持。感谢 @草语
- 新增：代码生成添加自定义 BaseMapper 的支持
- 优化：重构移除 RowSessionManager
- 优化：重命名 DbAutoConfiguration 为 FlexTransactionAutoConfiguration
- 优化：优化 FlexEnumTypeHandler 未找到 getter 方法时的错误提示
- 优化：优化自定义逻辑删除内容可能存在错误的一些问题
- 修复：在某些场景下，动态条件时，生成的 SQL 错误  #I6W89G
- 修复：在某些情况下，枚举属性出现异常的问题 #I6WGSA
- 文档：优化事务相关的文档
- 文档：添加枚举属性的相关文档
- 文档：修改 SQL 打印的文档错误
- 文档：APT 添加关于关闭 mapper 生成的文档
- 文档：queryWrapper 添加特别注意事项的内容



## v1.1.4 20230417:
- 新增：添加逻辑删除的自定义内容配置功能
- 新增：Entity 添加枚举属性的支持
- 新增：BaseMapper 新增 insertOrUpdate() 方法
- 新增：事务添加 Propagation 参数配置的支持
- 新增：为 Spring 适配 @Transactional 注解的支持
- 修复：租户模式下，deleteBatchByIds 报错的问题
- 修复：ProviderUtil NPE 的问题
- 修复：APT 和 TableInfo 构建多出静态变量的问题
- 文档：完善逻辑删除的相关文档
- 文档：修改 ignoreNulls 描述错误的问题



## v1.1.3 20230414:
- 新增：代码生成器生成的 entity 可以支持配置父类和实现的接口
- 修复：在某些场景下出现 Recursive update 错误的问题
- 修复：Entity 继承父类，但是 APT 生成的代码没有父类字段的问题
- 修复：QueryWrapper unionAll 报错的问题
- 修复：修复多租户的场景下 union 的子查询不添加租户 ID 的问题



## v1.1.2 20230413:
- 新增：QueryCondition 的 and(string) 和 or(string) 方法
- 新增：Page.map() 方法用于 Page 数据转换
- 新增：SQL 审计新增数据返回行数和自定义业务ID的支持
- 新增：Row 新增 Row.toObject() 方法，用于转换为 VO 对象
- 新增：RowUtil.java 工具类，用于 Row List 数据转换
- 新增：代码生成器添加配置 Mapper 前后缀配置的功能
- 新增：代码生成器添加配置 entity 前后缀配置的功能
- 优化：重构 MultiDataSourceAutoConfiguration.java
- 优化：重命名 Page.list 为 Page.records
- 优化：当配置了错误的数据库账号和密码，错误提示不友好的问题
- 优化：重命名 "MessageCreator" 为 "MessageFactory"
- 优化：重命名 RawValue.context 为 content
- 修复：ClassUtil 无法实例化被 lombok @Data() 修饰的 entity 的问题
- 修复：MybatisKeyGeneratorUtil.java 空指针的问题
- 修复：Springboot3 NestedIOException 找不到的问题
- 修复：QueryCondition 的值为 QueryColumn 或 RawValue 时错处的问题
- 修复：在某些场景下可能出现 Recursive update 的问题
- 文档：新增 Mybatis-Flex 和其他框架性能对比的文档
- 文档：添加整合 Springboot3 可能出现的问题帮助



## v1.1.0 20230412:
- 新增：Entity 的 onSet、onUpdate、onInsert 添加全局监听器的配置
- 优化：移除 QueryColumn.isNull 和 isNotNull 的参数
- 优化：重构 CustomKeyGenerator 的部分代码
- 修复：UpdateEntity 工具类在某些场景下出错的问题
- 修复：审计消息 AuditMessage 在 entity 配置 typeHandler 时，获取的参数不正确的问题
- 文档：添加 entity 全局监听器的相关文档



## v1.0.9 20230410:
- 新增：新增 多租户 使用的相关模块
- 新增：BaseMapper 添加 deleteByCondition 和 updateByCondition 方法
- 新增：添加 paginate 的更简单易用的相关方法
- 新增：QueryMethods 添加 column() 方法
- 新增：ConsoleMessageCollector 用于在控制台输出 SQL 及其执行时间
- 新增：QueryWrapper 添加 union 和 union all 的支持
- 新增：mybatis-flex-solon-plugin 插件，方便在 solon 框架下使用
- 修复：@Table(onSet) 配置在某些场景无法使用的问题
- 修复：Postgresql 的 limit offset 方言出错的问题
- 修复：多数据源的场景下，通过 @Table(dataSource) 配置无效的问题
- 优化：修改错别字 processer 为 processor
- 优化：优化 DbAutoConfiguration 未正确配置数据源时的错误信息
- 优化：Row 添加 getString()/getInt() 等等相关方法
- 优化：代码生成器通过 SqlServer 生成出错的问题
- 文档：优化 QueryWrapper 的相关文档
- 文档：优化 SQL 审计的相关文档
- 文档：添加 SQL 控制台打印输出的相关文档
- 文档：添加多租户的相关文档
- 文档：优化逻辑删除的相关文档



## v1.0.7 20230406:
- 新增：BaseMapper 添加可以直接根据 Condition 查询的方法，更加方便
- 新增：Db 添加可以直接根据 Condition 查询的方法，更加方便
- 新增：代码生成器添加 @Table(onSet) 的配置
- 新增：添加 HttpMessageReporter，用于可以往服务器发送审计日志
- 优化：APT 生成的 java 属性默认使用下划线的方式
- 优化：优化 APT 生成 mapper 的默认存放路径
- 优化：重命名 QueryEntityProcesser 为 QueryEntityProcessor
- 文档：添加字段权限的相关文档
- 文档：添加字典回写的相关文档
- 文档：添加字段加密的相关文档
- 文档：添加 Mybatis-Flex 与同类框架对比的文档



## v1.0.6 20230403:
- 新增：新增多数据源的支持
- 新增：Db.tx() 的事务提交方法
- 新增：RawValue 类，用于一些不需要进行 Sql 编译的场景
- 新增：@Table() 注解，添加 onSet 配置，用于监听 entity 被设置内容
- 新增：添加独创的 FlexIDKeyGenerator ID 生成器，用于分布式 ID 生成
- 优化：重命名 TableInfos 为 TableInfoFactory
- 优化：重命名审计日志的 "extTime" 为 "queryTime"
- 修复：AuditManager 在某些场景下会出现空指针的问题
- 文档：添加多数据源的相关文档
- 文档：添加事务管理的相关文档
- 文档：添加与 Spring 整合的相关文档
- 文档：优化 Db + Row 的相关文档
- 文档：QueryWrapper 添加关于 join 的更多示例
- 文档：添加关于 APT 使用的相关文档
- 文档：优化 readme 和 快速开始的相关文档



## v1.0.5 20230330:
- 新增：新增 SQL 审计模块，默认关闭
- 新增：代码生成器新增 lombok 配置的支持
- 新增：代码生成器新增可以配置多个表前缀的支持
- 新增：代码生成器添加 SqliteDialect 方言的支持
- 新增：代码生成器添加自定义 Entity 属性类型的配置支持
- 优化：修改方法 "deleteByByMap" 为 "deleteByMap"
- 优化：修改方法 "Db.insertRow()" 为 "Db.insert()"
- 修复：在某些场景下，Row.toEntity() 出现类型转换错误的问题
- 修复：queryWrapper 的 "in" 逻辑在某些场景下错误的问题
- 修复：代码生成器对 Mysql 的 "blob" 类型生成错误的问题
- 文档：新增关于代码生成器的相关文档
- 文档：新增 SQL 审计模块的相关文档
- 文档：优化 queryWrapper 的相关文档
- 文档：优化 分页查询 的相关文档



## v1.0.4 20230326:
- 新增：@ColumnMask() 注解用于数据脱敏，内置 9 中脱敏方式
- 新增：BaseMapper.selectAll() 方法
- 新增：BaseMapper.selectListByMap(Map whereConditions, int count) 方法
- 新增：添加 resource 配置，使得生成的代码自动集成到 classpath 中。 感谢 @piggsoft
- 新增：mybatis-flex-codegen 模块，用于 数据库-> Java 的代码生成
- 新增：@Table() 注解添加 OnInsert 和 OnUpdate 的配置支持
- 文档：新文档站点 https://mybatis-flex.com 上线



## v1.0.3 20230321:
- 新增：APT 自动生成 Mapper 代码的功能，无需在手动编写
- 新增：APT 新增 "processer.mappersGenerateEnable" 配置，用于开启是否自动生成 Mapper 类
- 修复：condition.when(flag) 的 value 值有返回的问题
- 文档：添加 where 动态条件文档示例，同步 APT 的文档



## v1.0.2 20230317:
- 新增：添加自定义字段 typeHandler @Column(typeHandler=xxx) 的配置
- 新增：内置默认的  fastjson fastjson2 gson jackson 的 TypeHandler，方便开发者直接使用
- 增强：entity 查询支持通过 QueryWrapper 传入表名以实现更灵活的需求
- 优化：对方言 CommonsDialectImpl.forInsertEntity 添加字段 wrap
- 优化：重构 TableInfo 的方法名，使其更加明确
- 优化：代码生成和 columns 构建支持更多默认的数据类型
- 优化：优化源码的中文注释描述
- 文档：优化 maven 依赖相关描述



## v1.0.0 20230312:
主要对 beta 和 rc 版本进行大量的重构和测试



## v1.0.0-rc.1 20230306:
- 优化：对 RowSqlProvider.java 的一些方法名进行重构
- 优化：QueryEntityProcesser 添加对 DEFAULT_COLUMNS 属性的生成
- 优化：RowKey.java 移除其 set 方法
- 优化：Entity 数据在被插入时，会自动设置逻辑删除的默认值
- 优化：添加主键生成的全局配置，可以不用为每个 Entity 单独配置主键内容
- 文档：添加 "逻辑删除" 的相关文档
- 文档：添加 "乐观锁" 的相关文档
- 文档：添加 entity 插入和更新的默认值设置的文档
- 文档：添加 OrderBy 的 QueryWrapper 示例



## v1.0.0-beta.2 20230303:
- 优化：当只查询一张表时，SQL生成的字段不添加表前缀
- 优化：完善对 @Column(onUpdateValue=xxx,onInsertValue=xxx) 的支持
- 优化：完善对 @Column(version = true) 的支持
- 优化：重命名 BaseMapper 的 insertBatchWithFirstEntityColumns 为 insertBatch
- 优化：重命名 DialectFactory 的 createDialectByDbType 为 createDialect
- 优化：为逻辑删除的默认值功能添加常量
- 修复：createDialectByDbType 方法中pg库的 KeywordWrap 错误
- 文档：优化文档



## v1.0.0-beta.1:
init mybatis-flex
