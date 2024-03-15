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
package com.mybatisflex.core.update;

import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.dialect.IDialect;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.HasParamsColumn;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;

import java.io.Serializable;

/**
 * @author michael
 */
public class RawValue implements Serializable {

    private Object object;

    public RawValue(Object object) {
        this.object = object;
    }


    public String toSql(IDialect dialect) {
        if (object instanceof String) {
            return (String) object;
        }

        if (object instanceof QueryWrapper) {
            return SqlConsts.BRACKET_LEFT + dialect.buildSelectSql((QueryWrapper) object) + SqlConsts.BRACKET_RIGHT;
        }

        if (object instanceof QueryCondition) {
            return ((QueryCondition) object).toSql(null, dialect);
        }

        if (object instanceof QueryColumn) {
            return CPI.toSelectSql((QueryColumn) object, null, dialect);
        }

        return object.toString();
    }

    public Object[] getParams() {
        if (object instanceof String) {
            return new Object[0];
        }

        if (object instanceof QueryWrapper) {
            return CPI.getValueArray((QueryWrapper) object);
        }

        if (object instanceof QueryCondition) {
            return CPI.getConditionParams((QueryCondition) object);
        }

        if (object instanceof HasParamsColumn) {
            return ((HasParamsColumn) object).getParamValues();
        }

        return new Object[0];
    }

}
