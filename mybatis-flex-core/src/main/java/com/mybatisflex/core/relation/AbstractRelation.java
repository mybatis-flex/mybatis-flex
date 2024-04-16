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
package com.mybatisflex.core.relation;

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.table.IdInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.FieldWrapper;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Field;
import java.util.*;

import static com.mybatisflex.core.query.QueryMethods.column;

public abstract class AbstractRelation<SelfEntity> {

    protected String name;
    protected String simpleName;
    protected Class<SelfEntity> selfEntityClass;
    protected Field relationField;
    protected FieldWrapper relationFieldWrapper;

    protected Field selfField;
    protected FieldWrapper selfFieldWrapper;

    protected String targetSchema;
    protected String targetTable;
    protected Field targetField;

    protected String valueField;
    protected boolean onlyQueryValueField;

    protected Class<?> targetEntityClass;
    protected TableInfo targetTableInfo;
    protected FieldWrapper targetFieldWrapper;

    protected String joinTable;
    protected String joinSelfColumn;
    protected String joinTargetColumn;

    protected String dataSource;

    protected String extraConditionSql;
    protected List<String> extraConditionParamKeys;

    protected QueryColumn conditionColumn;
    protected String[] selectColumns;

    public AbstractRelation(String selfField, String targetSchema, String targetTable, String targetField, String valueField,
                            String joinTable, String joinSelfColumn, String joinTargetColumn,
                            String dataSource, Class<SelfEntity> entityClass, Field relationField,
                            String extraCondition, String[] selectColumns
    ) {
        this.name = entityClass.getSimpleName() + "." + relationField.getName();
        this.simpleName = relationField.getName();
        this.selfEntityClass = entityClass;
        this.relationField = relationField;
        this.relationFieldWrapper = FieldWrapper.of(entityClass, relationField.getName());

        this.joinTable = joinTable;
        this.joinSelfColumn = joinSelfColumn;
        this.joinTargetColumn = joinTargetColumn;

        this.dataSource = dataSource;

        this.selfField = ClassUtil.getFirstField(entityClass, field -> field.getName().equals(selfField));
        this.selfFieldWrapper = FieldWrapper.of(entityClass, selfField);

        //以使用者注解配置为主
        this.targetTableInfo = StringUtil.isBlank(targetTable) ? TableInfoFactory.ofEntityClass(relationFieldWrapper.getMappingType()) : TableInfoFactory.ofTableName(targetTable);
        this.targetSchema = targetTableInfo != null ? targetTableInfo.getSchema() : targetSchema;
        this.targetTable = targetTableInfo != null ? targetTableInfo.getTableName() : targetTable;

        //当指定了 valueField 的时候，一般是 String Integer 等基本数据类型
        this.targetEntityClass = (StringUtil.isNotBlank(valueField) && targetTableInfo != null) ? targetTableInfo.getEntityClass() : relationFieldWrapper.getMappingType();

        this.targetField = ClassUtil.getFirstField(targetEntityClass, field -> field.getName().equals(targetField));
        this.targetFieldWrapper = FieldWrapper.of(targetEntityClass, targetField);

        this.valueField = valueField;
        this.onlyQueryValueField = StringUtil.isNotBlank(valueField);

        this.conditionColumn = targetTableInfo == null ? column(targetTable, StringUtil.camelToUnderline(this.targetField.getName()))
            : column(targetTable, targetTableInfo.getColumnByProperty(this.targetField.getName()));

        if (onlyQueryValueField) {
            //仅绑定字段时只需要查询关联列和该字段列即可
            this.selectColumns = new String[]{conditionColumn.getName(), targetTableInfo != null ? targetTableInfo.getColumnByProperty(this.valueField) : StringUtil.camelToUnderline(this.valueField)};
        } else {
            if (ArrayUtil.isNotEmpty(selectColumns)) {
                if (ArrayUtil.contains(selectColumns, conditionColumn.getName())) {
                    this.selectColumns = selectColumns;
                } else {
                    //需要追加 conditionColumn，因为进行内存 join 的时候，需要用到这个内容进行对比
                    this.selectColumns = ArrayUtil.concat(selectColumns, new String[]{conditionColumn.getName()});
                }
            }

        }

        initExtraCondition(extraCondition);
    }

    protected void initExtraCondition(String extraCondition) {
        if (StringUtil.isBlank(extraCondition)) {
            return;
        }


        List<String> sqlParamKeys = null;
        char[] chars = extraCondition.toCharArray();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(chars[0]);
        char prev, current;
        boolean keyStart = false;
        StringBuilder currentKey = null;
        for (int i = 1; i < chars.length; i++) {
            prev = chars[i - 1];
            current = chars[i];
            if (prev == ' ' && current == ':') {
                keyStart = true;
                currentKey = new StringBuilder();
            } else if (keyStart) {
                if (current != ' ' && current != ')') {
                    currentKey.append(current);
                } else {
                    if (sqlParamKeys == null) {
                        sqlParamKeys = new ArrayList<>();
                    }
                    sqlParamKeys.add(currentKey.toString());
                    sqlBuilder.append("?").append(current);
                    keyStart = false;
                    currentKey = null;
                }
            } else {
                sqlBuilder.append(current);
            }
        }
        if (keyStart && currentKey != null && currentKey.length() > 0) {
            if (sqlParamKeys == null) {
                sqlParamKeys = new ArrayList<>();
            }
            sqlParamKeys.add(currentKey.toString());
            sqlBuilder.append(" ?");
        }

        this.extraConditionSql = sqlBuilder.toString();
        this.extraConditionParamKeys = sqlParamKeys != null ? sqlParamKeys : Collections.emptyList();
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public Class<SelfEntity> getSelfEntityClass() {
        return selfEntityClass;
    }

    public void setSelfEntityClass(Class<SelfEntity> selfEntityClass) {
        this.selfEntityClass = selfEntityClass;
    }

    public Field getRelationField() {
        return relationField;
    }

    public void setRelationField(Field relationField) {
        this.relationField = relationField;
    }

    public FieldWrapper getRelationFieldWrapper() {
        return relationFieldWrapper;
    }

    public void setRelationFieldWrapper(FieldWrapper relationFieldWrapper) {
        this.relationFieldWrapper = relationFieldWrapper;
    }

    public Field getSelfField() {
        return selfField;
    }

    public void setSelfField(Field selfField) {
        this.selfField = selfField;
    }

    public FieldWrapper getSelfFieldWrapper() {
        return selfFieldWrapper;
    }

    public void setSelfFieldWrapper(FieldWrapper selfFieldWrapper) {
        this.selfFieldWrapper = selfFieldWrapper;
    }

    public Field getTargetField() {
        return targetField;
    }

    public void setTargetField(Field targetField) {
        this.targetField = targetField;
    }

    public Class<?> getTargetEntityClass() {
        return targetEntityClass;
    }

    public void setTargetEntityClass(Class<?> targetEntityClass) {
        this.targetEntityClass = targetEntityClass;
    }

    public TableInfo getTargetTableInfo() {
        return targetTableInfo;
    }

    public void setTargetTableInfo(TableInfo targetTableInfo) {
        this.targetTableInfo = targetTableInfo;
    }

    public FieldWrapper getTargetFieldWrapper() {
        return targetFieldWrapper;
    }

    public void setTargetFieldWrapper(FieldWrapper targetFieldWrapper) {
        this.targetFieldWrapper = targetFieldWrapper;
    }

    public String getTargetSchema() {
        return targetSchema;
    }

    public void setTargetSchema(String targetSchema) {
        this.targetSchema = targetSchema;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public boolean isOnlyQueryValueField() {
        return onlyQueryValueField;
    }

    public void setOnlyQueryValueField(boolean onlyQueryValueField) {
        this.onlyQueryValueField = onlyQueryValueField;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public void setJoinTable(String joinTable) {
        this.joinTable = joinTable;
    }

    public String getJoinSelfColumn() {
        return joinSelfColumn;
    }

    public void setJoinSelfColumn(String joinSelfColumn) {
        this.joinSelfColumn = joinSelfColumn;
    }

    public String getJoinTargetColumn() {
        return joinTargetColumn;
    }

    public void setJoinTargetColumn(String joinTargetColumn) {
        this.joinTargetColumn = joinTargetColumn;
    }

    public Set<Object> getSelfFieldValues(List<SelfEntity> selfEntities) {
        if (selfEntities == null || selfEntities.isEmpty()) {
            return Collections.emptySet();
        }
        Set<Object> values = new LinkedHashSet<>();
        selfEntities.forEach(self -> {
            Object value = selfFieldWrapper.get(self);
            if (value != null && !"".equals(value)) {
                values.add(value);
            }
        });
        return values;
    }


    public Class<?> getMappingType() {
        return relationFieldWrapper.getMappingType();
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getTargetTableWithSchema() {
        if (StringUtil.isNotBlank(targetTable)) {
            return StringUtil.isNotBlank(targetSchema) ? targetSchema + "." + targetTable : targetTable;
        } else {
            return targetTableInfo.getTableNameWithSchema();
        }
    }

    protected boolean isRelationByMiddleTable() {
        return StringUtil.isNotBlank(joinTable);
    }


    protected static Class<?> getTargetEntityClass(Class<?> entityClass, Field relationField) {
        return FieldWrapper.of(entityClass, relationField.getName()).getMappingType();
    }

    protected static String getDefaultPrimaryProperty(String key, Class<?> entityClass, String message) {
        if (StringUtil.isNotBlank(key)) {
            return key;
        }

        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        List<IdInfo> primaryKeyList = tableInfo.getPrimaryKeyList();
        if (primaryKeyList == null || primaryKeyList.size() != 1) {
            throw FlexExceptions.wrap(message);
        }

        return primaryKeyList.get(0).getProperty();
    }


    /**
     * 构建查询目标对象的 QueryWrapper
     *
     * @param targetValues 条件的值
     * @return QueryWrapper
     */
    public QueryWrapper buildQueryWrapper(Set<Object> targetValues) {
        QueryWrapper queryWrapper = QueryWrapper.create();

        if (ArrayUtil.isNotEmpty(selectColumns)) {
            queryWrapper.select(selectColumns);
        }

        queryWrapper.from(getTargetTableWithSchema());

        if (targetValues.size() > 1) {
            queryWrapper.where(conditionColumn.in(targetValues));
        } else {
            queryWrapper.where(conditionColumn.eq(targetValues.iterator().next()));
        }

        if (StringUtil.isNotBlank(extraConditionSql)) {
            queryWrapper.and(extraConditionSql, RelationManager.getExtraConditionParams(extraConditionParamKeys));
        }

        customizeQueryWrapper(queryWrapper);

        return queryWrapper;
    }


    /**
     * 方便子类追加自定义的条件
     *
     * @param queryWrapper 查询条件
     */
    public void customizeQueryWrapper(QueryWrapper queryWrapper) {
        //do thing
    }


    /**
     * @param selfEntities     当前的实体类列表
     * @param targetObjectList 查询到的结果
     * @param mappingRows      中间表的映射数据，非中间表查询的场景下，mappingRows 永远为 null
     */
    public abstract void join(List<SelfEntity> selfEntities, List<?> targetObjectList, List<Row> mappingRows);

}
