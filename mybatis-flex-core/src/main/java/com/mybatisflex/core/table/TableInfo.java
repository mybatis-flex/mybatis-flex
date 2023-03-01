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

import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.enums.KeyType;
import com.mybatisflex.core.javassist.ModifyAttrsRecord;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.Configuration;

import java.util.*;

public class TableInfo {

    private String schema; //schema
    private String tableName; //表名
    private Class<?> entityClass; //实体类
    private boolean useCached = false;
    private boolean camelToUnderline = true;


    private String[] columns = new String[0];  // 所有的字段，但除了主键的列
    private String[] primaryKeys = new String[0]; //主键字段

    //在插入数据的时候，支持主动插入的主键字段
    //排除主键为 auto 和 before 为 false 的主键字段
    private String[] insertPrimaryKeys;

    private List<ColumnInfo> columnInfoList;
    private List<IdInfo> primaryKeyList;

    //column 和 java 属性的称的关系映射
    private Map<String, String> columnPropertyMapping = new HashMap<>();
    private Map<String, String> propertyColumnMapping = new HashMap<>();

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

    public boolean isUseCached() {
        return useCached;
    }

    public void setUseCached(boolean useCached) {
        this.useCached = useCached;
    }

    public boolean isCamelToUnderline() {
        return camelToUnderline;
    }

    public void setCamelToUnderline(boolean camelToUnderline) {
        this.camelToUnderline = camelToUnderline;
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


    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }


    void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
        this.columns = new String[columnInfoList.size()];
        for (int i = 0; i < columnInfoList.size(); i++) {
            ColumnInfo columnInfo = columnInfoList.get(i);
            columns[i] = columnInfo.getColumn();
            columnPropertyMapping.put(columnInfo.column, columnInfo.property);
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

            if (idInfo.getKeyType() != KeyType.Auto && idInfo.isBefore()) {
                insertIdFields.add(idInfo.getColumn());
            }

            columnPropertyMapping.put(idInfo.column, idInfo.property);
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
     * 根据 插入字段 获取所有插入的值
     *
     * @param entity 从 entity 中获取
     * @return 数组
     */
    public Object[] obtainInsertValues(Object entity) {
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        String[] insertColumns = obtainInsertColumns();
        Object[] values = new Object[insertColumns.length];
        for (int i = 0; i < insertColumns.length; i++) {
            Object value = getColumnValue(metaObject, insertColumns[i]);
            values[i] = value;
        }

        return values;
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
                String column = getColumnByProperty(property);
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
                Object value = getColumnValue(metaObject, column);
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
    public Object[] obtainUpdateValues(Object entity, boolean ignoreNulls, boolean includePrimary) {
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        List<Object> values = new ArrayList<>();
        if (entity instanceof ModifyAttrsRecord) {
            Set<String> properties = ((ModifyAttrsRecord) entity).obtainModifyAttrs();
            if (properties.isEmpty()) {
                return values.toArray();
            }
            for (String property : properties) {
                String column = getColumnByProperty(property);
                if (!includePrimary && ArrayUtil.contains(primaryKeys, column)) {
                    continue;
                }
                Object value = getPropertyValue(metaObject, property);
                if (ignoreNulls && value == null) {
                    continue;
                }
                values.add(value);
            }
        }
        // not ModifyAttrsRecord
        else {
            for (String column : this.columns) {
                Object value = getColumnValue(metaObject, column);
                if (ignoreNulls && value == null) {
                    continue;
                }
                values.add(value);
            }
            // 普通 entity 忽略 includePrimary 的设置
//            if (includePrimary) {
//            }
        }

        return values.toArray();
    }


    public Object[] obtainPrimaryValues(Object entity) {
        MetaObject metaObject = EntityMetaObject.forObject(entity, reflectorFactory);
        Object[] values = new Object[primaryKeys.length];
        for (int i = 0; i < primaryKeys.length; i++) {
            values[i] = getColumnValue(metaObject, primaryKeys[i]);
        }
        return values;
    }


    public String getMappedStatementKeyProperties() {
        StringJoiner joiner = new StringJoiner(",");
        for (IdInfo value : primaryKeyList) {
            joiner.add(FlexConsts.ENTITY + "." + value.getProperty());
        }
        return joiner.toString();
    }


    public String getMappedStatementKeyColumns() {
        StringJoiner joiner = new StringJoiner(",");
        for (IdInfo value : primaryKeyList) {
            joiner.add(FlexConsts.ENTITY + "." + value.getColumn());
        }
        return joiner.toString();
    }

    public ResultMap buildResultMap(Configuration configuration) {
        String resultMapId = entityClass.getName();
        List<ResultMapping> resultMappings = new ArrayList<>();

        for (ColumnInfo columnInfo : columnInfoList) {
            ResultMapping mapping = new ResultMapping.Builder(configuration, columnInfo.getProperty(),
                    columnInfo.getColumn(), columnInfo.getPropertyType()).build();
            resultMappings.add(mapping);
        }
        for (IdInfo idInfo : primaryKeyList) {
            ResultMapping mapping = new ResultMapping.Builder(configuration, idInfo.getProperty(),
                    idInfo.getColumn(), idInfo.getPropertyType())
                    .flags(CollectionUtil.newArrayList(ResultFlag.ID))
//                    .typeHandler()
                    .build();
            resultMappings.add(mapping);
        }

        return new ResultMap.Builder(configuration, resultMapId, entityClass, resultMappings).build();

    }


    private Object getColumnValue(MetaObject metaObject, String column) {
        return getPropertyValue(metaObject, columnPropertyMapping.get(column));
    }


    private Object getPropertyValue(MetaObject metaObject, String property) {
        if (property != null && metaObject.hasGetter(property)) {
            return metaObject.getValue(property);
        }
        return null;
    }


    public String getColumnByProperty(String property) {
        return propertyColumnMapping.get(property);
    }


    /**
     * 通过 row 实例类转换为一个 entity
     * @return entity
     */
    public <T> T newInstanceByRow(Row row) {
        Object instance = ClassUtil.newInstance(entityClass);
        MetaObject metaObject = EntityMetaObject.forObject(instance, reflectorFactory);
        for (String column : row.keySet()) {
            String property = columnPropertyMapping.get(column);
            if (metaObject.hasSetter(property)) {
                metaObject.setValue(property, row.get(column));
            }
        }
        return (T) instance;
    }
}
