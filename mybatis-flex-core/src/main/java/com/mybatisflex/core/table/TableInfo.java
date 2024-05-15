/*
 *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mybatisflex.core.table;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.constant.SqlOperator;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.dialect.OperateType;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.exception.locale.LocalizedFormats;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.mybatis.TypeHandlerObject;
import com.mybatisflex.core.query.Brackets;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.Join;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.SelectQueryColumn;
import com.mybatisflex.core.query.SelectQueryTable;
import com.mybatisflex.core.query.SqlOperators;
import com.mybatisflex.core.query.UnionWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.tenant.TenantManager;
import com.mybatisflex.core.update.RawValue;
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.ConvertUtil;
import com.mybatisflex.core.util.EnumWrapper;
import com.mybatisflex.core.util.FieldWrapper;
import com.mybatisflex.core.util.MapUtil;
import com.mybatisflex.core.util.ObjectUtil;
import com.mybatisflex.core.util.SqlUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mybatisflex.core.constant.SqlConsts.AND;
import static com.mybatisflex.core.constant.SqlConsts.EQUALS_PLACEHOLDER;
import static com.mybatisflex.core.constant.SqlConsts.IN;

public class TableInfo {

    // column 和 java 属性的称的关系映射
    private final Map<String, ColumnInfo> columnInfoMapping = new HashMap<>();
    // property:column
    private final Map<String, String> propertyColumnMapping = new LinkedHashMap<>();
    private String schema; // schema
    private boolean camelToUnderline = true;
    private String dataSource;

    private String comment;
    private String tableName; // 表名
    private Class<?> entityClass; // 实体类
    // 逻辑删除数据库列名
    private String logicDeleteColumn;
    // 乐观锁字段
    private String versionColumn;
    // 租户ID 字段
    private String tenantIdColumn;
    // 数据插入时，默认插入数据字段
    private Map<String, String> onInsertColumns;

    private String[] allColumns = new String[0];
    // 数据更新时，默认更新内容的字段
    private Map<String, String> onUpdateColumns;
    // 大字段列
    private String[] largeColumns = new String[0];

    // 默认查询列，排除 large 等字段
    private String[] defaultQueryColumns = new String[0];
    // 所有的字段，但除了主键的列
    private String[] columns = new String[0];

    private List<ColumnInfo> columnInfoList;
    private List<IdInfo> primaryKeyList;
    // 主键字段
    private String[] primaryColumns = new String[0];
    private final Map<String, QueryColumn> columnQueryMapping = new HashMap<>();
    // 在插入数据的时候，支持主动插入的主键字段，自增字段不需要主动插入
    // 但通过自定义生成器生成 或者 Sequence 在 before 生成的时候，是需要主动插入数据的
    private String[] insertPrimaryKeys;

    private List<InsertListener> onInsertListeners;
    private List<UpdateListener> onUpdateListeners;
    private List<SetListener> onSetListeners;

    /**
     * 对应 MapperXML 配置文件中 {@code <resultMap>} 标签下的 {@code <association>} 标签。
     */
    private Map<String, Class<?>> associationType;

    /**
     * 对应 MapperXML 配置文件中 {@code <resultMap>} 标签下的 {@code <collection>} 标签。
     */
    private Map<Field, Class<?>> collectionType;


    private final ReflectorFactory reflectorFactory = new BaseReflectorFactory() {
        @Override
        public Reflector findForClass(Class<?> type) {
            return getReflector();
        }
    };
    private Reflector reflector; // 反射工具

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Map<String, String> getPropertyColumnMapping() {
        return this.propertyColumnMapping;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableNameWithSchema() {
        return StringUtil.buildSchemaWithTable(schema, tableName);
    }

    public String getWrapSchemaAndTableName(IDialect dialect, OperateType operateType) {
        if (StringUtil.isNotBlank(schema)) {
            String table = dialect.getRealTable(tableName, operateType);
            return dialect.wrap(dialect.getRealSchema(schema, table, operateType)) + "." + dialect.wrap(table);
        } else {
            return dialect.wrap(dialect.getRealTable(tableName, operateType));
        }
    }

    public void setTableName(String tableName) {
        int indexOf = tableName.indexOf(".");
        if (indexOf > 0) {
            if (StringUtil.isBlank(schema)) {
                this.schema = tableName.substring(0, indexOf);
                this.tableName = tableName.substring(indexOf + 1);
            } else {
                this.tableName = tableName;
            }
        } else {
            this.tableName = tableName;
        }
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public boolean isCamelToUnderline() {
        return camelToUnderline;
    }

    public void setCamelToUnderline(boolean camelToUnderline) {
        this.camelToUnderline = camelToUnderline;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLogicDeleteColumnOrSkip() {
        return LogicDeleteManager.getLogicDeleteColumn(logicDeleteColumn);
    }

    public String getLogicDeleteColumn() {
        return logicDeleteColumn;
    }

    public void setLogicDeleteColumn(String logicDeleteColumn) {
        this.logicDeleteColumn = logicDeleteColumn;
    }

    public String getVersionColumn() {
        return versionColumn;
    }

    public void setVersionColumn(String versionColumn) {
        this.versionColumn = versionColumn;
    }

    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    public void setTenantIdColumn(String tenantIdColumn) {
        this.tenantIdColumn = tenantIdColumn;
    }

    public Map<String, String> getOnInsertColumns() {
        return onInsertColumns;
    }

    public void setOnInsertColumns(Map<String, String> onInsertColumns) {
        this.onInsertColumns = onInsertColumns;
    }

    public Map<String, String> getOnUpdateColumns() {
        return onUpdateColumns;
    }

    public void setOnUpdateColumns(Map<String, String> onUpdateColumns) {
        this.onUpdateColumns = onUpdateColumns;
    }

    public String[] getLargeColumns() {
        return largeColumns;
    }

    public void setLargeColumns(String[] largeColumns) {
        this.largeColumns = largeColumns;
    }

    public String[] getDefaultQueryColumns() {
        return defaultQueryColumns;
    }

    public void setDefaultQueryColumns(String[] defaultQueryColumns) {
        this.defaultQueryColumns = defaultQueryColumns;
    }

    public String[] getInsertPrimaryKeys() {
        return insertPrimaryKeys;
    }

    public void setInsertPrimaryKeys(String[] insertPrimaryKeys) {
        this.insertPrimaryKeys = insertPrimaryKeys;
    }

    public Reflector getReflector() {
        return reflector;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public void setReflector(Reflector reflector) {
        this.reflector = reflector;
    }

    public String[] getAllColumns() {
        return allColumns;
    }

    public void setAllColumns(String[] allColumns) {
        this.allColumns = allColumns;
    }

    public String[] getColumns() {
        return columns;
    }


    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public String[] getPrimaryColumns() {
        return primaryColumns;
    }

    public void setPrimaryColumns(String[] primaryColumns) {
        this.primaryColumns = primaryColumns;
    }


    public List<InsertListener> getOnInsertListeners() {
        return onInsertListeners;
    }

    public void setOnInsertListeners(List<InsertListener> onInsertListeners) {
        this.onInsertListeners = onInsertListeners;
    }

    public List<UpdateListener> getOnUpdateListeners() {
        return onUpdateListeners;
    }

    public void setOnUpdateListeners(List<UpdateListener> onUpdateListeners) {
        this.onUpdateListeners = onUpdateListeners;
    }

    public List<SetListener> getOnSetListeners() {
        return onSetListeners;
    }

    public void setOnSetListeners(List<SetListener> onSetListeners) {
        this.onSetListeners = onSetListeners;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public String getColumnByProperty(String property) {
        String column = propertyColumnMapping.get(property);
        // 用于兼容字段MM不规范的情况
        // fix https://gitee.com/mybatis-flex/mybatis-flex/issues/I9PDYO
        if (column == null) {
            for (Map.Entry<String, String> entry : propertyColumnMapping.entrySet()) {
                if (property.equalsIgnoreCase(entry.getKey())) {
                    column = entry.getValue();
                    break;
                }
            }
        }
        return StringUtil.isNotBlank(column) ? column : property;
    }

    public Map<String, Class<?>> getAssociationType() {
        return associationType;
    }

    public void setAssociationType(Map<String, Class<?>> associationType) {
        this.associationType = associationType;
    }

    public void addAssociationType(String fieldName, Class<?> clazz) {
        if (associationType == null) {
            associationType = new HashMap<>();
        }
        associationType.put(fieldName, clazz);
    }

    public Map<Field, Class<?>> getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(Map<Field, Class<?>> collectionType) {
        this.collectionType = collectionType;
    }

    public void addCollectionType(Field field, Class<?> genericClass) {
        if (collectionType == null) {
            collectionType = new HashMap<>();
        }
        collectionType.put(field, genericClass);
    }

    void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
        List<String> columnNames = new ArrayList<>();
        for (int i = 0; i < columnInfoList.size(); i++) {
            ColumnInfo columnInfo = columnInfoList.get(i);
            // 真正的字段（没有做忽略标识）
            if (!columnInfo.isIgnore()) {
                columnNames.add(columnInfo.column);

                columnInfoMapping.put(columnInfo.column, columnInfo);
                propertyColumnMapping.put(columnInfo.property, columnInfo.column);

                String[] alias = columnInfo.getAlias();
                columnQueryMapping.put(columnInfo.column, new QueryColumn(schema, tableName, columnInfo.column, alias != null && alias.length > 0 ? alias[0] : null));
            }
        }

        this.columns = columnNames.toArray(new String[]{});
        this.allColumns = ArrayUtil.concat(allColumns, columns);
    }


    public List<IdInfo> getPrimaryKeyList() {
        return primaryKeyList;
    }

    void setPrimaryKeyList(List<IdInfo> primaryKeyList) {
        this.primaryKeyList = primaryKeyList;
        this.primaryColumns = new String[primaryKeyList.size()];

        List<String> insertIdFields = new ArrayList<>();

        for (int i = 0; i < primaryKeyList.size(); i++) {
            IdInfo idInfo = primaryKeyList.get(i);
            primaryColumns[i] = idInfo.getColumn();

            if (idInfo.getKeyType() != KeyType.Auto
                && (idInfo.getBefore() != null && idInfo.getBefore())
            ) {
                insertIdFields.add(idInfo.getColumn());
            }

            columnInfoMapping.put(idInfo.column, idInfo);
            propertyColumnMapping.put(idInfo.property, idInfo.column);

            String[] alias = idInfo.getAlias();
            columnQueryMapping.put(idInfo.column, new QueryColumn(schema, tableName, idInfo.column, alias != null && alias.length > 0 ? alias[0] : null));
        }
        this.allColumns = ArrayUtil.concat(allColumns, primaryColumns);
        this.insertPrimaryKeys = insertIdFields.toArray(new String[0]);
    }


    /**
     * 构建 insert 的 Sql 参数
     *
     * @param entity      从 entity 中获取
     * @param ignoreNulls 是否忽略 null 值
     * @return 数组
     */
    public Object[] buildInsertSqlArgs(Object entity, boolean ignoreNulls) {
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        String[] insertColumns = obtainInsertColumns(entity, ignoreNulls);

        Map<String, RawValue> rawValueMap = obtainUpdateRawValueMap(entity);

        List<Object> values = new ArrayList<>();
        for (String insertColumn : insertColumns) {
            if (onInsertColumns == null || !onInsertColumns.containsKey(insertColumn)) {
                if (rawValueMap.containsKey(insertColumn)) {
                    values.addAll(Arrays.asList(rawValueMap.remove(insertColumn).getParams()));
                    continue;
                }
                Object value = buildColumnSqlArg(metaObject, insertColumn);
                if (ignoreNulls && value == null) {
                    continue;
                }
                values.add(value);
            }
        }
        values.addAll(rawValueMap.values()
            .stream()
            .flatMap(e -> Arrays.stream(e.getParams()))
            .collect(Collectors.toList()));
        return values.toArray();
    }

    /**
     * 插入（新增）数据时，获取所有要插入的字段
     *
     * @param entity
     * @param ignoreNulls
     * @return 字段列表
     */
    public String[] obtainInsertColumns(Object entity, boolean ignoreNulls) {
        if (!ignoreNulls) {
            return ArrayUtil.concat(insertPrimaryKeys, columns);
        }
        // 忽略 null 字段，
        else {
            MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
            List<String> retColumns = new ArrayList<>();
            for (String insertColumn : allColumns) {
                if (onInsertColumns != null && onInsertColumns.containsKey(insertColumn)) {
                    retColumns.add(insertColumn);
                } else {
                    Object value = buildColumnSqlArg(metaObject, insertColumn);
                    if (value == null) {
                        continue;
                    }
                    retColumns.add(insertColumn);
                }
            }
            return retColumns.toArray(new String[0]);
        }
    }


    public Object[] buildInsertSqlArgsWithPk(Object entity, boolean ignoreNulls) {
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        String[] insertColumns = obtainInsertColumnsWithPk(entity, ignoreNulls);

        List<Object> values = new ArrayList<>(insertColumns.length);
        for (String insertColumn : insertColumns) {
            if (onInsertColumns == null || !onInsertColumns.containsKey(insertColumn)) {
                Object value = buildColumnSqlArg(metaObject, insertColumn);
                if (ignoreNulls && value == null) {
                    continue;
                }
                values.add(value);
            }
        }
        return values.toArray();
    }


    /**
     * 插入（新增）数据时，获取所有要插入的字段
     *
     * @param entity
     * @param ignoreNulls
     * @return 字段列表
     */
    public String[] obtainInsertColumnsWithPk(Object entity, boolean ignoreNulls) {
        if (!ignoreNulls) {
            return allColumns;
        } else {
            MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
            List<String> retColumns = new ArrayList<>();
            for (String primaryKey : primaryColumns) {
                Object value = buildColumnSqlArg(metaObject, primaryKey);
                if (value == null) {
                    throw new IllegalArgumentException("Entity Primary Key value must not be null.");
                }
                retColumns.add(primaryKey);
            }
            for (String insertColumn : columns) {
                if (onInsertColumns != null && onInsertColumns.containsKey(insertColumn)) {
                    retColumns.add(insertColumn);
                } else {
                    Object value = buildColumnSqlArg(metaObject, insertColumn);
                    if (value == null) {
                        continue;
                    }
                    retColumns.add(insertColumn);
                }
            }
            return retColumns.toArray(new String[0]);
        }
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map<String, RawValue> obtainUpdateRawValueMap(Object entity) {
        if (!(entity instanceof UpdateWrapper)) {
            return Collections.emptyMap();
        }

        Map<String, Object> updates = ((UpdateWrapper) entity).getUpdates();
        if (updates.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, RawValue> map = new HashMap<>();
        updates.forEach((key, value) -> {
            if (value instanceof RawValue) {
                String column = getColumnByProperty(key);
                map.put(column, (RawValue) value);
            }
        });

        return map;
    }

    /**
     * 获取要修改的值
     *
     * @param entity
     * @param ignoreNulls
     */
    public Set<String> obtainUpdateColumns(Object entity, boolean ignoreNulls, boolean includePrimary) {
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        Set<String> columns = new LinkedHashSet<>(); // 需使用 LinkedHashSet 保证 columns 的顺序
        if (entity instanceof UpdateWrapper) {
            Map<String, Object> updates = ((UpdateWrapper) entity).getUpdates();
            if (updates.isEmpty()) {
                return Collections.emptySet();
            }
            for (String property : updates.keySet()) {

                String column = getColumnByProperty(property);

                if (onUpdateColumns != null && onUpdateColumns.containsKey(column)) {
                    continue;
                }

                // 过滤乐观锁字段 和 租户字段
                if (ObjectUtil.equalsAny(column, versionColumn, tenantIdColumn)) {
                    continue;
                }

                if (!includePrimary && ArrayUtil.contains(primaryColumns, column)) {
                    continue;
                }

//                Object value = updates.get(property);
                // ModifyAttrsRecord 忽略 ignoreNulls 的设置
                // Object value = getPropertyValue(metaObject, property);
                // if (ignoreNulls && value == null) {
                //     continue;
                // }
                columns.add(column);
            }
        }
        // not ModifyAttrsRecord
        else {
            for (String column : this.columns) {
                if (onUpdateColumns != null && onUpdateColumns.containsKey(column)) {
                    continue;
                }

                // 过滤乐观锁字段 和 租户字段
                if (ObjectUtil.equalsAny(column, versionColumn, tenantIdColumn)) {
                    continue;
                }

                Object value = buildColumnSqlArg(metaObject, column);
                if (ignoreNulls && value == null) {
                    continue;
                }

                columns.add(column);
            }
        }
        return columns;
    }

    /**
     * 获取所有要修改的值，默认为全部除了主键以外的字段
     *
     * @param entity 实体对象
     * @return 数组
     */
    public Object[] buildUpdateSqlArgs(Object entity, boolean ignoreNulls, boolean includePrimary) {

        List<Object> values = new ArrayList<>();
        if (entity instanceof UpdateWrapper) {
            Map<String, Object> updates = ((UpdateWrapper) entity).getUpdates();
            if (updates.isEmpty()) {
                return FlexConsts.EMPTY_ARRAY;
            }
            for (String property : updates.keySet()) {

                String column = getColumnByProperty(property);

                if (onUpdateColumns != null && onUpdateColumns.containsKey(column)) {
                    continue;
                }
                // 过滤乐观锁字段 和 租户字段
                if (ObjectUtil.equalsAny(column, versionColumn, tenantIdColumn)) {
                    continue;
                }

                if (!includePrimary && ArrayUtil.contains(primaryColumns, column)) {
                    continue;
                }

                Object value = updates.get(property);
                if (value instanceof RawValue) {
                    values.addAll(Arrays.asList(((RawValue) value).getParams()));
                    continue;
                }

                if (value != null) {
                    ColumnInfo columnInfo = columnInfoMapping.get(column);
                    if (columnInfo != null) {
                        TypeHandler typeHandler = columnInfo.buildTypeHandler(null);
                        if (typeHandler != null) {
                            value = new TypeHandlerObject(typeHandler, value, columnInfo.getJdbcType());
                        }
                    }

                    // fixed: https://gitee.com/mybatis-flex/mybatis-flex/issues/I7TFBK
                    if (value.getClass().isEnum()) {
                        EnumWrapper enumWrapper = EnumWrapper.of(value.getClass());
                        value = enumWrapper.getEnumValue((Enum) value);
                    }
                }

                // ModifyAttrsRecord 忽略 ignoreNulls 的设置，
                // 当使用 ModifyAttrsRecord 时，可以理解为要对字段进行 null 值进行更新，否则没必要使用 ModifyAttrsRecord
                // if (ignoreNulls && value == null) {
                //    continue;
                // }
                values.add(value);
            }
        }
        // normal entity. not ModifyAttrsRecord
        else {
            MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);

            for (String column : this.columns) {
                if (onUpdateColumns != null && onUpdateColumns.containsKey(column)) {
                    continue;
                }

                // 过滤乐观锁字段 和 租户字段
                if (ObjectUtil.equalsAny(column, versionColumn, tenantIdColumn)) {
                    continue;
                }

                // 普通 entity 忽略 includePrimary 的设置，
                // 因为 for 循环中的 this.columns 本身就不包含有主键
                // if (includePrimary) {
                // }

                Object value = buildColumnSqlArg(metaObject, column);
                if (ignoreNulls && value == null) {
                    continue;
                }

                values.add(value);
            }
        }

        return values.toArray();
    }


    /**
     * 构建主键的 sql 参数数据
     *
     * @param entity
     */
    public Object[] buildPkSqlArgs(Object entity) {
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        Object[] values = new Object[primaryColumns.length];
        for (int i = 0; i < primaryColumns.length; i++) {
            values[i] = buildColumnSqlArg(metaObject, primaryColumns[i]);
        }
        return values;
    }

    public Object getValue(Object entity, String property) {
        FieldWrapper fieldWrapper = FieldWrapper.of(entityClass, property);
        return fieldWrapper.get(entity);
    }

    /**
     * 获取主键值
     *
     * @param entity
     * @return 主键值，有多个主键时返回数组
     */
    public Object getPkValue(Object entity) {
        // 绝大多数情况为 1 个主键
        if (primaryColumns.length == 1) {
            MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
            ColumnInfo columnInfo = columnInfoMapping.get(primaryColumns[0]);
            return getPropertyValue(metaObject, columnInfo.property);
        }
        // 多个主键
        else if (primaryColumns.length > 1) {
            MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
            Object[] values = new Object[primaryColumns.length];
            for (int i = 0; i < primaryColumns.length; i++) {
                ColumnInfo columnInfo = columnInfoMapping.get(primaryColumns[i]);
                values[i] = getPropertyValue(metaObject, columnInfo.property);
            }
            return values;
        }
        // 无主键
        else {
            return null;
        }
    }


    public Object[] buildTenantIdArgs() {
        if (StringUtil.isBlank(tenantIdColumn)) {
            return null;
        }

        return TenantManager.getTenantIds(tableName);
    }


    public String buildTenantCondition(String sql, Object[] tenantIdArgs, IDialect dialect) {
        if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
            if (tenantIdArgs.length == 1) {
                return sql + AND + dialect.wrap(tenantIdColumn) + EQUALS_PLACEHOLDER;
            } else {
                return sql + AND + dialect.wrap(tenantIdColumn) + IN + SqlUtil.buildSqlParamPlaceholder(tenantIdArgs.length);
            }
        } else {
            return sql;
        }
    }

    public void buildTenantCondition(StringBuilder sql, Object[] tenantIdArgs, IDialect dialect) {
        if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
            if (tenantIdArgs.length == 1) {
                sql.append(AND).append(dialect.wrap(tenantIdColumn)).append(EQUALS_PLACEHOLDER);
            } else {
                sql.append(AND).append(dialect.wrap(tenantIdColumn)).append(IN).append(SqlUtil.buildSqlParamPlaceholder(tenantIdArgs.length));
            }
        }
    }


    public void buildTenantCondition(QueryWrapper queryWrapper) {
        Object[] tenantIdArgs = buildTenantIdArgs();
        if (ArrayUtil.isNotEmpty(tenantIdArgs)) {
            if (tenantIdArgs.length == 1) {
                queryWrapper.where(QueryCondition.create(schema, tableName, tenantIdColumn, SqlConsts.EQUALS, tenantIdArgs[0]));
            } else {
                queryWrapper.where(QueryCondition.create(schema, tableName, tenantIdColumn, SqlConsts.IN, tenantIdArgs));
            }
        }
    }


    private static final String APPEND_CONDITIONS_FLAG = "appendConditions";

    public void appendConditions(Object entity, QueryWrapper queryWrapper) {

        Object appendConditions = CPI.getContext(queryWrapper, APPEND_CONDITIONS_FLAG);
        if (Boolean.TRUE.equals(appendConditions)) {
            return;
        } else {
            CPI.putContext(queryWrapper, APPEND_CONDITIONS_FLAG, Boolean.TRUE);
        }

        // select xxx.id,(select..) from xxx
        List<QueryColumn> selectColumns = CPI.getSelectColumns(queryWrapper);
        if (selectColumns != null && !selectColumns.isEmpty()) {
            for (QueryColumn queryColumn : selectColumns) {
                if (queryColumn instanceof SelectQueryColumn) {
                    QueryWrapper selectColumnQueryWrapper = CPI.getQueryWrapper((SelectQueryColumn) queryColumn);
                    doAppendConditions(entity, selectColumnQueryWrapper);
                }
            }
        }

        // select * from (select ... from ) 中的子查询处理
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables != null && !queryTables.isEmpty()) {
            for (QueryTable queryTable : queryTables) {
                if (queryTable instanceof SelectQueryTable) {
                    QueryWrapper selectQueryWrapper = ((SelectQueryTable) queryTable).getQueryWrapper();
                    doAppendConditions(entity, selectQueryWrapper);
                }
            }
        }

        // 添加乐观锁条件，只有在 update 的时候进行处理
        if (StringUtil.isNotBlank(versionColumn) && entity != null) {
            Object versionValue = buildColumnSqlArg(entity, versionColumn);
            if (versionValue == null) {
                throw FlexExceptions.wrap(LocalizedFormats.ENTITY_VERSION_NULL, entity);
            }
            queryWrapper.and(QueryCondition.create(schema, tableName, versionColumn, SqlConsts.EQUALS, versionValue));
        }

        // 逻辑删除
        if (StringUtil.isNotBlank(getLogicDeleteColumnOrSkip())) {
            // 逻辑删除时 保证前面的条件被括号包裹
            // fix:https://gitee.com/mybatis-flex/mybatis-flex/issues/I9163G
            QueryCondition whereCondition = CPI.getWhereQueryCondition(queryWrapper);
            if (whereCondition != null && !(whereCondition instanceof Brackets)) {
                QueryCondition wrappedCondition = new Brackets(whereCondition);
                CPI.setWhereQueryCondition(queryWrapper, wrappedCondition);
            }

            String joinTableAlias = CPI.getContext(queryWrapper, "joinTableAlias");
            LogicDeleteManager.getProcessor().buildQueryCondition(queryWrapper, this, joinTableAlias);
        }

        // 多租户
        buildTenantCondition(queryWrapper);


        // 子查询
        List<QueryWrapper> childSelects = CPI.getChildSelect(queryWrapper);
        if (CollectionUtil.isNotEmpty(childSelects)) {
            for (QueryWrapper childQueryWrapper : childSelects) {
                doAppendConditions(entity, childQueryWrapper);
            }
        }


        // join
        List<Join> joins = CPI.getJoins(queryWrapper);
        if (CollectionUtil.isNotEmpty(joins)) {
            for (Join join : joins) {
                if (!join.checkEffective()) {
                    continue;
                }
                QueryTable joinQueryTable = CPI.getJoinQueryTable(join);

                // join select
                if (joinQueryTable instanceof SelectQueryTable) {
                    QueryWrapper childQuery = ((SelectQueryTable) joinQueryTable).getQueryWrapper();
                    doAppendConditions(entity, childQuery);
                }
                // join table
                else {
                    String nameWithSchema = joinQueryTable.getNameWithSchema();
                    if (StringUtil.isNotBlank(nameWithSchema)) {
                        TableInfo tableInfo = TableInfoFactory.ofTableName(nameWithSchema);
                        if (tableInfo != null) {
                            QueryCondition joinQueryCondition = CPI.getJoinQueryCondition(join);
                            QueryWrapper newWrapper = QueryWrapper.create()
                                .where(joinQueryCondition);
                            CPI.putContext(newWrapper, "joinTableAlias", joinQueryTable.getAlias());
                            tableInfo.appendConditions(entity, newWrapper);
                            QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(newWrapper);
                            CPI.setJoinQueryCondition(join, whereQueryCondition);
                        }
                    }
                }
            }
        }

        // union
        List<UnionWrapper> unions = CPI.getUnions(queryWrapper);
        if (CollectionUtil.isNotEmpty(unions)) {
            for (UnionWrapper union : unions) {
                QueryWrapper unionQueryWrapper = union.getQueryWrapper();
                doAppendConditions(entity, unionQueryWrapper);
            }
        }
    }


    private void doAppendConditions(Object entity, QueryWrapper queryWrapper) {
        List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
        if (queryTables != null && !queryTables.isEmpty()) {
            for (QueryTable queryTable : queryTables) {
                if (queryTable instanceof SelectQueryTable) {
                    QueryWrapper childQuery = ((SelectQueryTable) queryTable).getQueryWrapper();
                    doAppendConditions(entity, childQuery);
                } else {
                    String nameWithSchema = queryTable.getNameWithSchema();
                    if (StringUtil.isNotBlank(nameWithSchema)) {
                        TableInfo tableInfo = TableInfoFactory.ofTableName(nameWithSchema);
                        if (tableInfo != null) {
                            tableInfo.appendConditions(entity, queryWrapper);
                        }
                    }
                }
            }
        }
    }


    public QueryWrapper buildQueryWrapper(Object entity, SqlOperators operators) {
        QueryColumn[] queryColumns = new QueryColumn[defaultQueryColumns.length];
        for (int i = 0; i < defaultQueryColumns.length; i++) {
            queryColumns[i] = columnQueryMapping.get(defaultQueryColumns[i]);
        }

        QueryWrapper queryWrapper = QueryWrapper.create();

        String tableNameWithSchema = getTableNameWithSchema();
        queryWrapper.select(queryColumns).from(tableNameWithSchema);

        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        propertyColumnMapping.forEach((property, column) -> {
            if (column.equals(logicDeleteColumn)) {
                return;
            }
            Object value = metaObject.getValue(property);
            if (value != null && !"".equals(value)) {
                QueryColumn queryColumn = Arrays.stream(queryColumns)
                    .filter(e -> e.getName().equals(column))
                    .findFirst()
                    .orElse(QueryMethods.column(getTableNameWithSchema(), column));
                if (operators != null) {
                    SqlOperator operator = operators.get(property);
                    if (operator == null) {
                        operator = SqlOperator.EQUALS;
                    } else if (operator == SqlOperator.IGNORE) {
                        return;
                    }
                    if (operator == SqlOperator.LIKE || operator == SqlOperator.NOT_LIKE) {
                        value = "%" + value + "%";
                    } else if (operator == SqlOperator.LIKE_LEFT || operator == SqlOperator.NOT_LIKE_LEFT) {
                        value = value + "%";
                    } else if (operator == SqlOperator.LIKE_RIGHT || operator == SqlOperator.NOT_LIKE_RIGHT) {
                        value = "%" + value;
                    }
                    queryWrapper.and(QueryCondition.create(queryColumn, operator, value));
                } else {
                    queryWrapper.and(queryColumn.eq(value));
                }
            }
        });
        return queryWrapper;
    }

    public String getKeyProperties() {
        StringJoiner joiner = new StringJoiner(",");
        for (IdInfo value : primaryKeyList) {
            joiner.add(FlexConsts.ENTITY + "." + value.getProperty());
        }
        return joiner.toString();
    }


    public String getKeyColumns() {
        StringJoiner joiner = new StringJoiner(",");
        for (IdInfo value : primaryKeyList) {
            joiner.add(value.getColumn());
        }
        return joiner.toString();
    }

    public List<QueryColumn> getDefaultQueryColumn() {
        return Arrays.stream(defaultQueryColumns)
            .map(columnQueryMapping::get)
            .collect(Collectors.toList());
    }

    private void getCombinedColumns(List<Class<?>> existedEntities, Class<?> entityClass, List<String> combinedColumns) {
        if (existedEntities.contains(entityClass)) {
            return;
        }

        existedEntities.add(entityClass);

        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);

        combinedColumns.addAll(Arrays.asList(tableInfo.allColumns));

        if (tableInfo.collectionType != null) {
            tableInfo.collectionType.values()
                .forEach(e -> getCombinedColumns(existedEntities, e, combinedColumns));
        }

        if (tableInfo.associationType != null) {
            tableInfo.associationType.values()
                .forEach(e -> getCombinedColumns(existedEntities, e, combinedColumns));
        }
    }

    public ResultMap buildResultMap(Configuration configuration) {
        List<String> combinedColumns = new ArrayList<>();

        getCombinedColumns(new ArrayList<>(), entityClass, combinedColumns);

        // 预加载所有重复列，用于判断重名属性
        List<String> existedColumns = combinedColumns.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .filter(e -> e.getValue().intValue() > 1)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        return doBuildResultMap(configuration, new HashSet<>(), existedColumns, false, getTableNameWithSchema());
    }

    private ResultMap doBuildResultMap(Configuration configuration, Set<String> resultMapIds, List<String> existedColumns, boolean isNested, String nestedPrefix) {

        String resultMapId = isNested ? "nested-" + nestedPrefix + ":" + entityClass.getName() : entityClass.getName();

        // 是否有循环引用
        boolean withCircularReference = resultMapIds.contains(resultMapId) || resultMapIds.contains(entityClass.getName());
        if (withCircularReference) {
            return null;
        }

        resultMapIds.add(resultMapId);

        if (configuration.hasResultMap(resultMapId)) {
            return configuration.getResultMap(resultMapId);
        }

        List<ResultMapping> resultMappings = new ArrayList<>();

        // <resultMap> 标签下的 <id> 标签映射
        for (IdInfo idInfo : primaryKeyList) {
            doBuildColumnResultMapping(configuration, resultMappings, existedColumns, idInfo, CollectionUtil.newArrayList(ResultFlag.ID), isNested);
        }

        // <resultMap> 标签下的 <result> 标签映射
        for (ColumnInfo columnInfo : columnInfoList) {
            doBuildColumnResultMapping(configuration, resultMappings, existedColumns, columnInfo, Collections.emptyList(), isNested);
        }

        // <resultMap> 标签下的 <association> 标签映射
        if (associationType != null) {
            associationType.forEach((fieldName, fieldType) -> {
                // 获取嵌套类型的信息，也就是 javaType 属性
                TableInfo tableInfo = TableInfoFactory.ofEntityClass(fieldType);
                // 构建嵌套类型的 ResultMap 对象，也就是 <association> 标签下的内容
                ResultMap nestedResultMap = tableInfo.doBuildResultMap(configuration, resultMapIds, existedColumns, true, nestedPrefix);
                if (nestedResultMap != null) {
                    resultMappings.add(new ResultMapping.Builder(configuration, fieldName)
                        .javaType(fieldType)
                        .nestedResultMapId(nestedResultMap.getId())
                        .build());
                }
            });
        }

        // <resultMap> 标签下的 <collection> 标签映射
        if (collectionType != null) {
            collectionType.forEach((field, genericClass) -> {
                if (TableInfoFactory.defaultSupportColumnTypes.contains(genericClass)) {
                    // List<String> List<Integer> 等
                    String columnName = TableInfoFactory.getColumnName(camelToUnderline, field, field.getAnnotation(Column.class));
                    // 映射 <result column="..."/>
                    ResultMapping resultMapping = new ResultMapping.Builder(configuration, null)
                        .column(columnName)
                        .typeHandler(configuration.getTypeHandlerRegistry().getTypeHandler(genericClass))
                        .build();
                    String nestedResultMapId = entityClass.getName() + "." + field.getName();
                    ResultMap nestedResultMap;
                    if (configuration.hasResultMap(nestedResultMapId)) {
                        nestedResultMap = configuration.getResultMap(nestedResultMapId);
                    } else {
                        nestedResultMap = new ResultMap.Builder(configuration, nestedResultMapId, genericClass, Collections.singletonList(resultMapping)).build();
                        configuration.addResultMap(nestedResultMap);
                    }
                    // 映射 <collection property="..." ofType="genericClass">
                    resultMappings.add(new ResultMapping.Builder(configuration, field.getName())
                        .javaType(field.getType())
                        .nestedResultMapId(nestedResultMap.getId())
                        .build());
                } else {
                    // 获取集合泛型类型的信息，也就是 ofType 属性
                    TableInfo tableInfo = TableInfoFactory.ofEntityClass(genericClass);
                    // 构建嵌套类型的 ResultMap 对象，也就是 <collection> 标签下的内容
                    ResultMap nestedResultMap = tableInfo.doBuildResultMap(configuration, resultMapIds, existedColumns, true, nestedPrefix);
                    if (nestedResultMap != null) {
                        resultMappings.add(new ResultMapping.Builder(configuration, field.getName())
                            .javaType(field.getType())
                            .nestedResultMapId(nestedResultMap.getId())
                            .build());
                    }
                }
            });
        }

        ResultMap resultMap = new ResultMap.Builder(configuration, resultMapId, entityClass, resultMappings).build();
        configuration.addResultMap(resultMap);
        resultMapIds.add(resultMapId);
        return resultMap;
    }

    private void doBuildColumnResultMapping(Configuration configuration, List<ResultMapping> resultMappings
        , List<String> existedColumns, ColumnInfo columnInfo, List<ResultFlag> flags, boolean isNested) {

        if (!isNested) {
            if (existedColumns.contains(columnInfo.column)) {
                // userName -> tb_user$user_name
                resultMappings.add(new ResultMapping.Builder(configuration
                    , columnInfo.property
                    , tableName + "$" + columnInfo.column
                    , columnInfo.propertyType)
                    .jdbcType(columnInfo.getJdbcType())
                    .flags(flags)
                    .typeHandler(columnInfo.buildTypeHandler(configuration))
                    .build());
            }
            buildDefaultResultMapping(configuration, resultMappings, columnInfo, flags);
        } else {
            if (existedColumns.contains(columnInfo.column)) {
                // userName -> tb_user$user_name
                resultMappings.add(new ResultMapping.Builder(configuration
                    , columnInfo.property
                    , tableName + "$" + columnInfo.column
                    , columnInfo.propertyType)
                    .jdbcType(columnInfo.getJdbcType())
                    .flags(flags)
                    .typeHandler(columnInfo.buildTypeHandler(configuration))
                    .build());
            } else {
                buildDefaultResultMapping(configuration, resultMappings, columnInfo, flags);
            }
        }

        if (ArrayUtil.isNotEmpty(columnInfo.alias)) {
            for (String alias : columnInfo.alias) {
                // userName -> alias
                resultMappings.add(new ResultMapping.Builder(configuration
                    , columnInfo.property
                    , alias
                    , columnInfo.propertyType)
                    .jdbcType(columnInfo.getJdbcType())
                    .flags(flags)
                    .typeHandler(columnInfo.buildTypeHandler(configuration))
                    .build());
            }
        }

    }

    private void buildDefaultResultMapping(Configuration configuration, List<ResultMapping> resultMappings, ColumnInfo columnInfo, List<ResultFlag> flags) {
        // userName -> user_name
        resultMappings.add(new ResultMapping.Builder(configuration
            , columnInfo.property
            , columnInfo.column
            , columnInfo.propertyType)
            .jdbcType(columnInfo.getJdbcType())
            .flags(flags)
            .typeHandler(columnInfo.buildTypeHandler(configuration))
            .build());

        if (!Objects.equals(columnInfo.column, columnInfo.property)) {
            // userName -> userName
            resultMappings.add(new ResultMapping.Builder(configuration
                , columnInfo.property
                , columnInfo.property
                , columnInfo.propertyType)
                .jdbcType(columnInfo.getJdbcType())
                .flags(flags)
                .typeHandler(columnInfo.buildTypeHandler(configuration))
                .build());
        }
    }


    private Object buildColumnSqlArg(MetaObject metaObject, String column) {
        ColumnInfo columnInfo = columnInfoMapping.get(column);
        Object value = getPropertyValue(metaObject, columnInfo.property);
        if (value != null) {
            TypeHandler<?> typeHandler = columnInfo.buildTypeHandler(null);
            if (typeHandler != null) {
                return new TypeHandlerObject(typeHandler, value, columnInfo.getJdbcType());
            }
        }
        return value;
    }


    public Object buildColumnSqlArg(Object entityObject, String column) {
        MetaObject metaObject = EntityMetaObject.forObject(entityObject, reflectorFactory);
        return buildColumnSqlArg(metaObject, column);
    }


    public Object getPropertyValue(MetaObject metaObject, String property) {
        if (property != null && metaObject.hasGetter(property)) {
            return metaObject.getValue(property);
        }
        return null;
    }


    /**
     * 通过 row 实例类转换为一个 entity
     *
     * @return entity
     */
    public <T> T newInstanceByRow(Row row, int index) {
        Object instance = ClassUtil.newInstance(entityClass);
        MetaObject metaObject = EntityMetaObject.forObject(instance, reflectorFactory);
        Set<String> rowKeys = row.keySet();
        columnInfoMapping.forEach((column, columnInfo) -> {
            if (index <= 0) {
                String replace = column.replace("_", "");
                for (String rowKey : rowKeys) {
                    // 修复: 开启 mapUnderscoreToCamelCase = true 时， row 无法转换 entity 的问题
                    if (rowKey.equalsIgnoreCase(column) || rowKey.equalsIgnoreCase(replace)) {
                        setInstancePropertyValue(row, instance, metaObject, columnInfo, rowKey);
                    }
                }
            } else {
                for (int i = index; i >= 0; i--) {
                    String newColumn = i <= 0 ? column : column + "$" + i;
                    boolean fillValue = false;
                    String replace = column.replace("_", "");
                    for (String rowKey : rowKeys) {
                        // 修复: 开启 mapUnderscoreToCamelCase = true 时， row 无法转换 entity 的问题
                        if (rowKey.equalsIgnoreCase(newColumn) || rowKey.equalsIgnoreCase(replace)) {
                            setInstancePropertyValue(row, instance, metaObject, columnInfo, rowKey);
                            fillValue = true;
                            break;
                        }
                    }
                    if (fillValue) {
                        break;
                    }
                }
            }
        });
        // noinspection unchecked
        return (T) instance;
    }


    private void setInstancePropertyValue(Row row, Object instance, MetaObject metaObject, ColumnInfo columnInfo, String rowKey) {
        Object rowValue = row.get(rowKey);
        TypeHandler<?> typeHandler = columnInfo.buildTypeHandler(null);
        if (typeHandler != null) {
            try {
                // 通过 typeHandler 转换数据
                rowValue = typeHandler.getResult(getResultSet(rowValue), 0);
            } catch (SQLException e) {
                // ignore
            }
        }
        if (rowValue != null && !metaObject.getSetterType(columnInfo.property).isAssignableFrom(rowValue.getClass())) {
            rowValue = ConvertUtil.convert(rowValue, metaObject.getSetterType(columnInfo.property), true);
        }
        rowValue = invokeOnSetListener(instance, columnInfo.property, rowValue);
        metaObject.setValue(columnInfo.property, rowValue);
    }


    private ResultSet getResultSet(Object value) {
        return (ResultSet) Proxy.newProxyInstance(TableInfo.class.getClassLoader(),
            new Class[]{ResultSet.class}, (proxy, method, args) -> value);
    }


    /**
     * 初始化乐观锁版本号
     *
     * @param entityObject
     */
    public void initVersionValueIfNecessary(Object entityObject) {
        if (StringUtil.isBlank(versionColumn)) {
            return;
        }

        MetaObject metaObject = EntityMetaObject.forObject(entityObject, reflectorFactory);
        Object columnValue = getPropertyValue(metaObject, columnInfoMapping.get(versionColumn).property);
        if (columnValue == null) {
            String name = columnInfoMapping.get(versionColumn).property;
            Class<?> clazz = metaObject.getSetterType(name);
            metaObject.setValue(name, ConvertUtil.convert(0L, clazz));
        }
    }

    /**
     * 设置租户id
     *
     * @param entityObject
     */
    public void initTenantIdIfNecessary(Object entityObject) {
        if (StringUtil.isBlank(tenantIdColumn)) {
            return;
        }

        MetaObject metaObject = EntityMetaObject.forObject(entityObject, reflectorFactory);
        Object[] tenantIds = TenantManager.getTenantIds(tableName);
        if (tenantIds == null || tenantIds.length == 0) {
            return;
        }

        // 默认使用第一个作为插入的租户ID
        Object tenantId = tenantIds[0];
        if (tenantId != null) {
            String property = columnInfoMapping.get(tenantIdColumn).property;
            Class<?> setterType = metaObject.getSetterType(property);
            metaObject.setValue(property, ConvertUtil.convert(tenantId, setterType));
        }
    }

    /**
     * 初始化逻辑删除的默认值
     *
     * @param entityObject
     */
    public void initLogicDeleteValueIfNecessary(Object entityObject) {
        if (StringUtil.isBlank(getLogicDeleteColumnOrSkip())) {
            return;
        }

        MetaObject metaObject = EntityMetaObject.forObject(entityObject, reflectorFactory);
        ColumnInfo logicDeleteColumn = columnInfoMapping.get(this.logicDeleteColumn);
        Object columnValue = getPropertyValue(metaObject, logicDeleteColumn.property);
        if (columnValue == null) {
            Object normalValueOfLogicDelete = LogicDeleteManager.getProcessor().getLogicNormalValue();
            if (normalValueOfLogicDelete != null) {
                String property = logicDeleteColumn.property;
                Class<?> setterType = metaObject.getSetterType(property);
                metaObject.setValue(property, ConvertUtil.convert(normalValueOfLogicDelete, setterType));
            }
        }
    }


    private static final Map<Class<?>, List<InsertListener>> insertListenerCache = new ConcurrentHashMap<>();

    public void invokeOnInsertListener(Object entity) {
        List<InsertListener> listeners = MapUtil.computeIfAbsent(insertListenerCache, entityClass, aClass -> {
            List<InsertListener> globalListeners = FlexGlobalConfig.getDefaultConfig()
                .getSupportedInsertListener(entityClass);
            List<InsertListener> allListeners = CollectionUtil.merge(onInsertListeners, globalListeners);
            Collections.sort(allListeners);
            return allListeners;
        });
        listeners.forEach(insertListener -> insertListener.onInsert(entity));
    }


    private static final Map<Class<?>, List<UpdateListener>> updateListenerCache = new ConcurrentHashMap<>();

    public void invokeOnUpdateListener(Object entity) {
        List<UpdateListener> listeners = MapUtil.computeIfAbsent(updateListenerCache, entityClass, aClass -> {
            List<UpdateListener> globalListeners = FlexGlobalConfig.getDefaultConfig()
                .getSupportedUpdateListener(entityClass);
            List<UpdateListener> allListeners = CollectionUtil.merge(onUpdateListeners, globalListeners);
            Collections.sort(allListeners);
            return allListeners;
        });
        listeners.forEach(insertListener -> insertListener.onUpdate(entity));
    }


    private static final Map<Class<?>, List<SetListener>> setListenerCache = new ConcurrentHashMap<>();

    public Object invokeOnSetListener(Object entity, String property, Object value) {
        List<SetListener> listeners = MapUtil.computeIfAbsent(setListenerCache, entityClass, aClass -> {
            List<SetListener> globalListeners = FlexGlobalConfig.getDefaultConfig()
                .getSupportedSetListener(entityClass);
            List<SetListener> allListeners = CollectionUtil.merge(onSetListeners, globalListeners);
            Collections.sort(allListeners);
            return allListeners;
        });
        for (SetListener setListener : listeners) {
            value = setListener.onSet(entity, property, value);
        }
        return value;
    }

    public QueryColumn getQueryColumnByProperty(String property) {
        String column = getColumnByProperty(property);
        return columnQueryMapping.get(column);
    }

}
