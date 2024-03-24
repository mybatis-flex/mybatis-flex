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

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.mask.CompositeMaskTypeHandler;
import com.mybatisflex.core.mask.MaskTypeHandler;
import com.mybatisflex.core.util.ArrayUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.util.Date;

public class ColumnInfo {

    private static final Class<?>[] needGetTypeHandlerTypes = {
        Date.class, java.sql.Date.class, Time.class, Timestamp.class,
        Instant.class, LocalDate.class, LocalDateTime.class, LocalTime.class, OffsetDateTime.class, OffsetTime.class, ZonedDateTime.class,
        Year.class, Month.class, YearMonth.class, JapaneseDate.class,
        byte[].class, Byte[].class, Byte.class,
    };

    /**
     * 数据库列名。
     */
    protected String column;

    /**
     * 列的别名。
     */
    protected String[] alias;

    /**
     * java entity 定义的属性名称（field name）。
     */
    protected String property;

    /**
     * 数据库字段注释，在 AI 时代，注释的内容往往可用于 AI 辅助对话
     */
    protected String comment;

    /**
     * 属性类型。
     */
    protected Class<?> propertyType;

    /**
     * 该列对应的 jdbcType。
     */
    protected JdbcType jdbcType;

    /**
     * 自定义 TypeHandler。
     */
    protected TypeHandler<?> typeHandler;

    /**
     * 最终使用和构建出来的 typeHandler
     */
    protected TypeHandler<?> buildTypeHandler;

    /**
     * 数据脱敏类型。
     */
    protected String maskType;

    /**
     * 是否忽略
     */
    protected boolean ignore;


    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String[] getAlias() {
        return alias;
    }

    public void setAlias(String[] alias) {
        this.alias = alias;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public TypeHandler<?> buildTypeHandler(Configuration configuration) {

        if (buildTypeHandler != null) {
            return buildTypeHandler;
        }

        //脱敏规则配置
        else if (StringUtil.isNotBlank(maskType)) {
            if (typeHandler != null) {
                //noinspection unchecked
                buildTypeHandler = new CompositeMaskTypeHandler(maskType, (TypeHandler<Object>) typeHandler);
            } else {
                buildTypeHandler = new MaskTypeHandler(maskType);
            }
        }

        //用户自定义的 typeHandler
        else if (typeHandler != null) {
            buildTypeHandler = typeHandler;
        }

        //枚举
        else if (propertyType.isEnum() || ArrayUtil.contains(needGetTypeHandlerTypes, propertyType)) {
            if (configuration == null) {
                configuration = FlexGlobalConfig.getDefaultConfig().getConfiguration();
            }
            if (configuration != null) {
                buildTypeHandler = configuration.getTypeHandlerRegistry().getTypeHandler(propertyType);
            }
        }

        return buildTypeHandler;
    }

    public void setTypeHandler(TypeHandler<?> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public String getMaskType() {
        return maskType;
    }

    public void setMaskType(String maskType) {
        this.maskType = maskType;
    }


    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }
}
