/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.table;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.javassist.ModifyAttrsRecord;
import com.mybatisflex.core.mybatis.TypeHandlerObject;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;

import java.util.*;

public class TableInfo {

    private String schema; //schema
    private String tableName; //表名
    private Class<?> entityClass; //实体类
    private boolean camelToUnderline = true;
    private String dataSource;

    //逻辑删除数据库列名
    private String logicDeleteColumn;

    //乐观锁字段
    private String versionColumn;

    //数据插入时，默认插入数据字段
    private Map<String, String> onInsertColumns;

    //数据更新时，默认更新内容的字段
    private Map<String, String> onUpdateColumns;

    //大字段列
    private String[] largeColumns = new String[0];

    // 所有的字段，但除了主键的列
    private String[] columns = new String[0];

    //主键字段
    private String[] primaryKeys = new String[0];

    //在插入数据的时候，支持主动插入的主键字段
    //通过自定义生成器生成 或者 Sequence 在 before 生成的时候，是需要主动插入数据的
    private String[] insertPrimaryKeys;

    private List<ColumnInfo> columnInfoList;
    private List<IdInfo> primaryKeyList;

    //column 和 java 属性的称的关系映射
    private Map<String, ColumnInfo> columnInfoMapping = new HashMap<>();
    private Map<String, String> propertyColumnMapping = new HashMap<>();

    private InsertListener onInsertListener;
    private UpdateListener onUpdateListener;
    private SetListener onSetListener;


    private final ReflectorFactory reflectorFactory = new BaseReflectorFactory() {
        @Override
        public Reflector findForClass(Class<?> type) {
            return getReflector();
        }
    };
    private Reflector reflector; //反射工具

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public String[] getColumns() {
        return columns;
    }


    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    public String[] getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(String[] primaryKeys) {
        this.primaryKeys = primaryKeys;
    }


    public InsertListener getOnInsertListener() {
        return onInsertListener;
    }

    public void setOnInsertListener(InsertListener onInsertListener) {
        this.onInsertListener = onInsertListener;
    }

    public UpdateListener getOnUpdateListener() {
        return onUpdateListener;
    }

    public void setOnUpdateListener(UpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public SetListener getOnSetListener() {
        return onSetListener;
    }

    public void setOnSetListener(SetListener onSetListener) {
        this.onSetListener = onSetListener;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }


    void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
        this.columns = new String[columnInfoList.size()];
        for (int i = 0; i < columnInfoList.size(); i++) {
            ColumnInfo columnInfo = columnInfoList.get(i);
            columns[i] = columnInfo.getColumn();
            columnInfoMapping.put(columnInfo.column, columnInfo);
            propertyColumnMapping.put(columnInfo.property, columnInfo.column);
        }
    }


    public List<IdInfo> getPrimaryKeyList() {
        return primaryKeyList;
    }

    void setPrimaryKeyList(List<IdInfo> primaryKeyList) {
        this.primaryKeyList = primaryKeyList;
        this.primaryKeys = new String[primaryKeyList.size()];

        List<String> insertIdFields = new ArrayList<>();
        for (int i = 0; i < primaryKeyList.size(); i++) {
            IdInfo idInfo = primaryKeyList.get(i);
            primaryKeys[i] = idInfo.getColumn();

            if (idInfo.getKeyType() != KeyType.Auto && (idInfo.getBefore() != null && idInfo.getBefore())) {
                insertIdFields.add(idInfo.getColumn());
            }

            columnInfoMapping.put(idInfo.column, idInfo);
            propertyColumnMapping.put(idInfo.property, idInfo.column);
        }
        this.insertPrimaryKeys = insertIdFields.toArray(new String[0]);
    }


    /**
     * 插入（新增）数据时，获取所有要插入的字段
     *
     * @return 字段列表
     */
    public String[] obtainInsertColumns() {
        return ArrayUtil.concat(insertPrimaryKeys, columns);
    }


    /**
     * 构建 insert 的 Sql 参数
     *
     * @param entity 从 entity 中获取
     * @return 数组
     */
    public Object[] buildInsertSqlArgs(Object entity) {
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        String[] insertColumns = obtainInsertColumns();

        List<Object> values = new ArrayList<>(insertColumns.length);
        for (String insertColumn : insertColumns) {
            if (onInsertColumns == null || !onInsertColumns.containsKey(insertColumn)) {
                Object value = buildColumnSqlArg(metaObject, insertColumn);
                values.add(value);
            }
        }
        return values.toArray();
    }


    /**
     * 获取要修改的值
     *
     * @param entity
     * @param ignoreNulls
     * @return
     */
    public Set<String> obtainUpdateColumns(Object entity, boolean ignoreNulls, boolean includePrimary) {
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        Set<String> columns = new LinkedHashSet<>(); //需使用 LinkedHashSet 保证 columns 的顺序
        if (entity instanceof ModifyAttrsRecord) {
            Set<String> properties = ((ModifyAttrsRecord) entity).obtainModifyAttrs();
            if (properties.isEmpty()) {
                return Collections.emptySet();
            }
            for (String property : properties) {
                String column = propertyColumnMapping.get(property);
                if (onUpdateColumns != null && onUpdateColumns.containsKey(column)) {
                    continue;
                }

                //过滤乐观锁字段
                if (Objects.equals(column, versionColumn)) {
                    continue;
                }

                if (!includePrimary && ArrayUtil.contains(primaryKeys, column)) {
                    continue;
                }
                Object value = getPropertyValue(metaObject, property);
                if (ignoreNulls && value == null) {
                    continue;
                }
                columns.add(column);
            }
        }
        //not ModifyAttrsRecord
        else {
            for (String column : this.columns) {
                if (onUpdateColumns != null && onUpdateColumns.containsKey(column)) {
                    continue;
                }

                //过滤乐观锁字段
                if (Objects.equals(column, versionColumn)) {
                    continue;
                }

                Object value = buildColumnSqlArg(metaObject, column);
                if (ignoreNulls && value == null) {
                    continue;
                }
                columns.add(column);
            }

            // 普通 entity（非 ModifyAttrsRecord） 忽略 includePrimary 的设置
//            if (includePrimary) {
//                for (String column : this.primaryKeys) {
//                    Object value = getColumnValue(metaObject, column);
//                    if (ignoreNulls && value == null) {
//                        continue;
//                    }
//                    columns.add(column);
//                }
//            }
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
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        List<Object> values = new ArrayList<>();
        if (entity instanceof ModifyAttrsRecord) {
            Set<String> properties = ((ModifyAttrsRecord) entity).obtainModifyAttrs();
            if (properties.isEmpty()) {
                return values.toArray();
            }
            for (String property : properties) {
                String column = propertyColumnMapping.get(property);
                if (onUpdateColumns != null && onUpdateColumns.containsKey(column)) {
                    continue;
                }
                //过滤乐观锁字段
                if (Objects.equals(column, versionColumn)) {
                    continue;
                }

                if (!includePrimary && ArrayUtil.contains(primaryKeys, column)) {
                    continue;
                }

                Object value = getPropertyValue(metaObject, property);

                // ModifyAttrsRecord 忽略 ignoreNulls 的设置，
                // 当使用 ModifyAttrsRecord 时，可以理解为要对字段进行 null 值进行更新，否则没必要使用 ModifyAttrsRecord
                //  if (ignoreNulls && value == null) {
                //     continue;
                //  }
                values.add(value);
            }
        }
        // normal entity. not ModifyAttrsRecord
        else {
            for (String column : this.columns) {
                if (onUpdateColumns != null && onUpdateColumns.containsKey(column)) {
                    continue;
                }

                //忽略乐观锁字段，乐观锁字段会直接通过 sql 对其操作
                if (Objects.equals(column, versionColumn)) {
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
        Object[] values = new Object[primaryKeys.length];
        for (int i = 0; i < primaryKeys.length; i++) {
            values[i] = buildColumnSqlArg(metaObject, primaryKeys[i]);
        }
        return values;
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

    public ResultMap buildResultMap(Configuration configuration) {
        String resultMapId = entityClass.getName();
        List<ResultMapping> resultMappings = new ArrayList<>();

        for (ColumnInfo columnInfo : columnInfoList) {
            ResultMapping mapping = new ResultMapping.Builder(configuration, columnInfo.getProperty(),
                    columnInfo.getColumn(), columnInfo.getPropertyType())
                    .jdbcType(columnInfo.getJdbcType())
                    .typeHandler(columnInfo.buildTypeHandler())
                    .build();
            resultMappings.add(mapping);
        }

        for (IdInfo idInfo : primaryKeyList) {
            ResultMapping mapping = new ResultMapping.Builder(configuration, idInfo.getProperty(),
                    idInfo.getColumn(), idInfo.getPropertyType())
                    .flags(CollectionUtil.newArrayList(ResultFlag.ID))
                    .jdbcType(idInfo.getJdbcType())
                    .typeHandler(idInfo.buildTypeHandler())
                    .build();
            resultMappings.add(mapping);
        }

        return new ResultMap.Builder(configuration, resultMapId, entityClass, resultMappings).build();
    }


    private Object buildColumnSqlArg(MetaObject metaObject, String column) {
        ColumnInfo columnInfo = columnInfoMapping.get(column);
        Object value = getPropertyValue(metaObject, columnInfo.property);

        TypeHandler typeHandler = columnInfo.buildTypeHandler();
        if (value != null && typeHandler != null) {
            return new TypeHandlerObject(typeHandler, value, columnInfo.getJdbcType());
        }

        return value;
    }


    public Object buildColumnSqlArg(Object entityObject, String column) {
        MetaObject metaObject = EntityMetaObject.forObject(entityObject, reflectorFactory);
        return buildColumnSqlArg(metaObject, column);
    }


    private Object getPropertyValue(MetaObject metaObject, String property) {
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
    public <T> T newInstanceByRow(Row row) {
        Object instance = ClassUtil.newInstance(entityClass);
        MetaObject metaObject = EntityMetaObject.forObject(instance, reflectorFactory);
        for (String column : row.keySet()) {
            ColumnInfo columnInfo = columnInfoMapping.get(column);
            if (columnInfo != null && metaObject.hasSetter(columnInfo.property)) {
                Object value = ConvertUtil.convert(row.get(column), metaObject.getSetterType(columnInfo.property));
                if (onSetListener != null) {
                    value = onSetListener.onSet(instance, columnInfo.property, value);
                }
                metaObject.setValue(columnInfo.property, value);
            }
        }
        return (T) instance;
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
        Object columnValue = buildColumnSqlArg(entityObject, versionColumn);
        if (columnValue == null) {
            metaObject.setValue(columnInfoMapping.get(versionColumn).property, 0);
        }
    }

    /**
     * 初始化逻辑删除的默认值
     *
     * @param entityObject
     */
    public void initLogicDeleteValueIfNecessary(Object entityObject) {
        if (StringUtil.isBlank(logicDeleteColumn)) {
            return;
        }

        MetaObject metaObject = EntityMetaObject.forObject(entityObject, reflectorFactory);
        Object columnValue = buildColumnSqlArg(entityObject, logicDeleteColumn);
        if (columnValue == null) {
            metaObject.setValue(columnInfoMapping.get(logicDeleteColumn).property, 0);
        }
    }


    public void invokeOnInsert(Object entity) {
        if (onInsertListener != null) {
            onInsertListener.onInsert(entity);
        }
    }

    public void invokeUpUpdate(Object entity) {
        if (onUpdateListener != null) {
            onUpdateListener.onUpdate(entity);
        }
    }
}
