/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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
package com.mybatisflex.core.query;

import com.mybatisflex.core.mybatis.TypeHandlerObject;
import com.mybatisflex.core.table.ColumnInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.type.TypeHandler;

/**
 * 为 {@link QueryWrapper} 的条件参数应用实体类 {@code @Column(typeHandler = ...)} 配置的类型处理器。
 *
 * <p>条件参数走的是 {@code 预编译 SQL + 参数数组} 这条链路，参数在拼接 SQL 时便与列脱离了关系，
 * 因此 {@code SqlArgsParameterHandler} 只能按参数的运行时类型去 {@code TypeHandlerRegistry}
 * 里取类型处理器，列上配置的 {@code typeHandler} 不会生效。对于字段加密一类的场景，
 * 这会导致以明文作为条件查询密文列时静默查不到数据。
 *
 * <p>本类在收集条件参数时，通过 {@link QueryColumn} 反查其所属的 {@link ColumnInfo}，
 * 把参数包装为 {@link TypeHandlerObject}，使条件参数与 insert、update 的行为保持一致。
 *
 * @author michael
 * @since 1.11.9
 */
class ConditionTypeHandlers {

    private ConditionTypeHandlers() {
    }

    /**
     * 使用列上配置的类型处理器包装条件参数。
     *
     * @param column 条件所属的列
     * @param value  条件参数（不为 {@code null}）
     * @return 包装后的参数，列上未配置类型处理器时返回原始参数
     */
    static Object wrap(QueryColumn column, Object value) {
        if (!QueryColumnBehavior.isApplyConditionTypeHandler()
            || value instanceof TypeHandlerObject) {
            return value;
        }

        ColumnInfo columnInfo = getColumnInfo(column);
        if (columnInfo == null || !isPropertyTypeMatched(columnInfo, value)) {
            return value;
        }

        TypeHandler<?> typeHandler = columnInfo.buildTypeHandler(null);
        // 只处理用户显式配置的类型处理器，枚举、日期等类型交由 TypeHandlerRegistry 按运行时类型处理，
        // 与 QueryWrapper 此前的行为保持一致
        return typeHandler == null || !columnInfo.hasCustomTypeHandler()
            ? value
            : new TypeHandlerObject(typeHandler, value, columnInfo.getJdbcType());
    }

    /**
     * 反查列所属实体类的列信息。
     *
     * <p>仅处理普通列：函数列、原生列、子查询列一类的列没有明确的实体类字段与之对应。
     */
    private static ColumnInfo getColumnInfo(QueryColumn column) {
        if (column == null
            || ClassUtil.getUsefulClass(column.getClass()) != QueryColumn.class
            || !StringUtil.hasText(column.getName())) {
            return null;
        }

        QueryTable table = column.getTable();
        if (table == null) {
            return null;
        }

        TableInfo tableInfo = TableInfoFactory.ofTableName(table.getNameWithSchema());
        return tableInfo == null ? null : tableInfo.getColumnInfo(column.getName());
    }

    /**
     * 校验参数类型与实体类属性类型是否匹配。
     *
     * <p>类型不匹配时说明使用者传入的并非该属性的值（例如以 {@code LIKE} 拼接的通配符片段），
     * 此时套用列的类型处理器会得到错误的结果，因此保持原始参数不变。
     */
    private static boolean isPropertyTypeMatched(ColumnInfo columnInfo, Object value) {
        Class<?> propertyType = ClassUtil.getWrapType(columnInfo.getPropertyType());
        return propertyType != null && propertyType.isAssignableFrom(value.getClass());
    }

}
