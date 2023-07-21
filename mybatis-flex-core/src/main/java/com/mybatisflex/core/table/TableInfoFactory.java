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

import com.mybatisflex.annotation.*;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import com.mybatisflex.core.util.Reflectors;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.apache.ibatis.util.MapUtil;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TableInfoFactory {

    private TableInfoFactory() {
    }

    static final Set<Class<?>> defaultSupportColumnTypes = CollectionUtil.newHashSet(
        int.class, Integer.class,
        short.class, Short.class,
        long.class, Long.class,
        float.class, Float.class,
        double.class, Double.class,
        boolean.class, Boolean.class,
        Date.class, java.sql.Date.class, Time.class, Timestamp.class,
        Instant.class, LocalDate.class, LocalDateTime.class, LocalTime.class, OffsetDateTime.class, OffsetTime.class, ZonedDateTime.class,
        Year.class, Month.class, YearMonth.class, JapaneseDate.class,
        byte[].class, Byte[].class, Byte.class,
        BigInteger.class, BigDecimal.class,
        char.class, String.class, Character.class
    );


    private static final Map<Class<?>, TableInfo> mapperTableInfoMap = new ConcurrentHashMap<>();
    private static final Map<Class<?>, TableInfo> entityTableMap = new ConcurrentHashMap<>();
    private static final Map<String, TableInfo> tableInfoMap = new ConcurrentHashMap<>();
    private static final Set<String> initedPackageNames = new HashSet<>();


    public synchronized static void init(String mapperPackageName) {
        if (!initedPackageNames.contains(mapperPackageName)) {
            ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
            resolverUtil.find(new ResolverUtil.IsA(BaseMapper.class), mapperPackageName);
            Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
            for (Class<? extends Class<?>> mapperClass : mapperSet) {
                ofMapperClass(mapperClass);
            }
            initedPackageNames.add(mapperPackageName);
        }
    }


    public static TableInfo ofMapperClass(Class<?> mapperClass) {
        return MapUtil.computeIfAbsent(mapperTableInfoMap, mapperClass, key -> {
            Class<?> entityClass = getEntityClass(mapperClass);
            if (entityClass == null) {
                return null;
            }
            return ofEntityClass(entityClass);
        });
    }


    public static TableInfo ofEntityClass(Class<?> entityClass) {
        return MapUtil.computeIfAbsent(entityTableMap, entityClass, aClass -> {
            TableInfo tableInfo = createTableInfo(entityClass);
            tableInfoMap.put(tableInfo.getTableNameWithSchema(), tableInfo);
            return tableInfo;
        });
    }


    public static TableInfo ofTableName(String tableName) {
        return StringUtil.isNotBlank(tableName) ? tableInfoMap.get(tableName) : null;
    }


    private static Class<?> getEntityClass(Class<?> mapperClass) {
        if (mapperClass == null || mapperClass == Object.class) {
            return null;
        }
        Type[] genericInterfaces = mapperClass.getGenericInterfaces();
        if (genericInterfaces.length == 1) {
            Type type = genericInterfaces[0];
            if (type instanceof ParameterizedType) {
                Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
                return actualTypeArgument instanceof Class ? (Class<?>) actualTypeArgument : null;
            } else if (type instanceof Class) {
                return getEntityClass((Class<?>) type);
            }
        }
        return getEntityClass(mapperClass.getSuperclass());
    }


    private static TableInfo createTableInfo(Class<?> entityClass) {

        TableInfo tableInfo = new TableInfo();
        tableInfo.setEntityClass(entityClass);
        Reflector reflector = Reflectors.of(entityClass);
        tableInfo.setReflector(reflector);

        //初始化表名
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null) {
            tableInfo.setTableName(table.value());
            tableInfo.setSchema(table.schema());
            tableInfo.setCamelToUnderline(table.camelToUnderline());

            if (table.onInsert().length > 0) {
                List<InsertListener> insertListeners = Arrays.stream(table.onInsert())
                    .filter(listener -> listener != NoneListener.class)
                    .map(ClassUtil::newInstance)
                    .collect(Collectors.toList());
                tableInfo.setOnInsertListeners(insertListeners);
            }

            if (table.onUpdate().length > 0) {
                List<UpdateListener> updateListeners = Arrays.stream(table.onUpdate())
                    .filter(listener -> listener != NoneListener.class)
                    .map(ClassUtil::newInstance)
                    .collect(Collectors.toList());
                tableInfo.setOnUpdateListeners(updateListeners);
            }

            if (table.onSet().length > 0) {
                List<SetListener> setListeners = Arrays.stream(table.onSet())
                    .filter(listener -> listener != NoneListener.class)
                    .map(ClassUtil::newInstance)
                    .collect(Collectors.toList());
                tableInfo.setOnSetListeners(setListeners);
            }

            if (StringUtil.isNotBlank(table.dataSource())) {
                tableInfo.setDataSource(table.dataSource());
            }
        } else {
            //默认为类名转驼峰下划线
            String tableName = StringUtil.camelToUnderline(entityClass.getSimpleName());
            tableInfo.setTableName(tableName);
        }

        //初始化字段相关
        List<ColumnInfo> columnInfoList = new ArrayList<>();
        List<IdInfo> idInfos = new ArrayList<>();

        Field idField = null;

        String logicDeleteColumn = null;
        String versionColumn = null;
        String tenantIdColumn = null;

        //数据插入时，默认插入数据字段
        Map<String, String> onInsertColumns = new HashMap<>();

        //数据更新时，默认更新内容的字段
        Map<String, String> onUpdateColumns = new HashMap<>();

        //大字段列
        Set<String> largeColumns = new LinkedHashSet<>();

        // 默认查询列
        Set<String> defaultQueryColumns = new LinkedHashSet<>();


        List<Field> entityFields = getColumnFields(entityClass);

        for (Field field : entityFields) {

            Column column = field.getAnnotation(Column.class);
            if (column != null && column.ignore()) {
                continue; // ignore
            }

            Class<?> fieldType = reflector.getGetterType(field.getName());

            //满足以下 3 种情况，不支持该类型
            if ((column == null || column.typeHandler() == UnknownTypeHandler.class) // 未配置 typeHandler
                && !fieldType.isEnum()   // 类型不是枚举
                && !defaultSupportColumnTypes.contains(fieldType) //默认的自动类型不包含该类型
            ) {
                // 集合嵌套
                if (Collection.class.isAssignableFrom(fieldType)) {
                    Type genericType = TypeParameterResolver.resolveFieldType(field, entityClass);
                    if (genericType instanceof ParameterizedType) {
                        Type actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
                        if (actualTypeArgument instanceof Class){
                            tableInfo.addCollectionType(field, (Class<?>) actualTypeArgument);
                        }
                    }
                }
                // 实体类嵌套
                else if (!Map.class.isAssignableFrom(fieldType)
                    && !fieldType.isArray()) {
                    tableInfo.addAssociationType(field.getName(), fieldType);
                }
                // 不支持的类型直接跳过
                continue;
            }

            //列名
            String columnName = getColumnName(tableInfo.isCamelToUnderline(), field, column);

            //逻辑删除字段
            if (column != null && column.isLogicDelete()) {
                if (logicDeleteColumn == null) {
                    logicDeleteColumn = columnName;
                } else {
                    throw FlexExceptions.wrap("The logic delete column of entity[%s] must be less then 2.", entityClass.getName());
                }
            }

            //乐观锁版本字段
            if (column != null && column.version()) {
                if (versionColumn == null) {
                    versionColumn = columnName;
                } else {
                    throw FlexExceptions.wrap("The version column of entity[%s] must be less then 2.", entityClass.getName());
                }
            }

            //租户ID 字段
            if (column != null && column.tenantId()) {
                if (tenantIdColumn == null) {
                    tenantIdColumn = columnName;
                } else {
                    throw FlexExceptions.wrap("The tenantId column of entity[%s] must be less then 2.", entityClass.getName());
                }
            }

            if (column != null && StringUtil.isNotBlank(column.onInsertValue())) {
                onInsertColumns.put(columnName, column.onInsertValue().trim());
            }


            if (column != null && StringUtil.isNotBlank(column.onUpdateValue())) {
                onUpdateColumns.put(columnName, column.onUpdateValue().trim());
            }


            if (column != null && column.isLarge()) {
                largeColumns.add(columnName);
            }

            if (column == null || !column.isLarge()) {
                defaultQueryColumns.add(columnName);
            }

            Id id = field.getAnnotation(Id.class);
            ColumnInfo columnInfo;
            if (id != null) {
                columnInfo = new IdInfo(id);
                idInfos.add((IdInfo) columnInfo);
            } else {
                columnInfo = new ColumnInfo();
                columnInfoList.add(columnInfo);
            }

            ColumnAlias columnAlias = null;
            // 属性上没有别名，查找 getter 方法上有没有别名
            Method getterMethod = ClassUtil.getFirstMethod(entityClass, m -> ClassUtil.isGetterMethod(m, field.getName()));
            if (getterMethod != null) {
                columnAlias = getterMethod.getAnnotation(ColumnAlias.class);
            }

            if (columnAlias == null) {
                columnAlias = field.getAnnotation(ColumnAlias.class);
            }

            if (columnAlias != null) {
                columnInfo.setAlias(columnAlias.value());
            }

            columnInfo.setColumn(columnName);
            columnInfo.setProperty(field.getName());
            columnInfo.setPropertyType(fieldType);

            if (column != null && column.typeHandler() != UnknownTypeHandler.class) {
                Class<?> typeHandlerClass = column.typeHandler();
                Configuration configuration = FlexGlobalConfig.getDefaultConfig().getConfiguration();
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                TypeHandler<?> typeHandler = typeHandlerRegistry.getInstance(columnInfo.getPropertyType(), typeHandlerClass);
                columnInfo.setTypeHandler(typeHandler);
            }

            ColumnMask columnMask = field.getAnnotation(ColumnMask.class);
            if (columnMask != null && StringUtil.isNotBlank(columnMask.value())) {
                if (String.class != fieldType) {
                    throw new IllegalStateException("@ColumnMask() only support for string type field. error: " + entityClass.getName() + "." + field.getName());
                }
                columnInfo.setMaskType(columnMask.value().trim());
            }

            if (column != null && column.jdbcType() != JdbcType.UNDEFINED) {
                columnInfo.setJdbcType(column.jdbcType());
            }

            if (FlexConsts.DEFAULT_PRIMARY_FIELD.equals(field.getName())) {
                idField = field;
            }
        }


        if (idInfos.isEmpty() && idField != null) {
            int index = -1;
            for (int i = 0; i < columnInfoList.size(); i++) {
                ColumnInfo columnInfo = columnInfoList.get(i);
                if (FlexConsts.DEFAULT_PRIMARY_FIELD.equals(columnInfo.getProperty())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                ColumnInfo removedColumnInfo = columnInfoList.remove(index);
                idInfos.add(new IdInfo(removedColumnInfo));
            }
        }

        tableInfo.setLogicDeleteColumn(logicDeleteColumn);
        tableInfo.setVersionColumn(versionColumn);
        tableInfo.setTenantIdColumn(tenantIdColumn);

        if (!onInsertColumns.isEmpty()) {
            tableInfo.setOnInsertColumns(onInsertColumns);
        }

        if (!onUpdateColumns.isEmpty()) {
            tableInfo.setOnUpdateColumns(onUpdateColumns);
        }

        if (!largeColumns.isEmpty()) {
            tableInfo.setLargeColumns(largeColumns.toArray(new String[0]));
        }

        if (!defaultQueryColumns.isEmpty()) {
            tableInfo.setDefaultQueryColumns(defaultQueryColumns.toArray(new String[0]));
        }

        tableInfo.setPrimaryKeyList(idInfos);
        tableInfo.setColumnInfoList(columnInfoList);


        return tableInfo;
    }

    static String getColumnName(boolean isCamelToUnderline, Field field, Column column) {
        if (column != null && StringUtil.isNotBlank(column.value())) {
            return column.value();
        }
        if (isCamelToUnderline) {
            return StringUtil.camelToUnderline(field.getName());
        }
        return field.getName();
    }


    public static List<Field> getColumnFields(Class<?> entityClass) {
        List<Field> fields = new ArrayList<>();
        doGetFields(entityClass, fields);
        return fields;
    }


    private static void doGetFields(Class<?> entityClass, List<Field> fields) {
        if (entityClass == null || entityClass == Object.class) {
            return;
        }

        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (Modifier.isStatic(declaredField.getModifiers())
                || existName(fields, declaredField)) {
                continue;
            }
            fields.add(declaredField);
        }

        doGetFields(entityClass.getSuperclass(), fields);
    }


    private static boolean existName(List<Field> fields, Field field) {
        for (Field f : fields) {
            if (f.getName().equalsIgnoreCase(field.getName())) {
                return true;
            }
        }
        return false;
    }

}
