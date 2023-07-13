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

import com.mybatisflex.annotation.RelationOneToMany;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Field;

class OneToMany<SelfEntity> extends ToManyRelation<SelfEntity> {

    private String orderBy;
    private int limit;


    public OneToMany(RelationOneToMany annotation, Class<SelfEntity> entityClass, Field relationField) {
        super(getDefaultPrimaryProperty(annotation.selfField(), entityClass, "@RelationOneToMany.selfField can not be empty in field: \"" + entityClass.getName() + "." + relationField.getName() + "\"")
            , annotation.targetSchema()
            , annotation.targetTable()
            , annotation.targetField()
            , annotation.joinTable()
            , annotation.joinSelfColumn()
            , annotation.joinTargetColumn()
            , annotation.dataSource(), entityClass, relationField
            , annotation.extraCondition());
        this.orderBy = annotation.orderBy();
        this.limit = annotation.limit();
    }


    @Override
    public void customizeQueryWrapper(QueryWrapper queryWrapper) {
        if (StringUtil.isNotBlank(orderBy)) {
            queryWrapper.orderBy(orderBy);
        }

        if (limit > 0) {
            queryWrapper.limit(limit);
        }
    }

}
