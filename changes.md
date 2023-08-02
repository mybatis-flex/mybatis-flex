# MyBatis-Flex ChangeLog



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
- 新增：添加异常信息国际哈的支持，感谢 @Suomm
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
