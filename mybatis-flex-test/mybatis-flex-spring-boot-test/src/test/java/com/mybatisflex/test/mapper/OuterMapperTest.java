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

package com.mybatisflex.test.mapper;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.entity.Outer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.mybatisflex.test.entity.table.InnerTableDef.INNER;
import static com.mybatisflex.test.entity.table.OuterTableDef.OUTER;

/**
 * @author 王帅
 * @since 2023-07-01
 */
@SpringBootTest
class OuterMapperTest {

    @Autowired
    private OuterMapper outerMapper;

    @Autowired
    private InnerMapper innerMapper;

    @Test
    void testInsert() {
        Outer outer = new Outer();
        outer.setName("outer 01");
        int result = outerMapper.insertSelective(outer);
        Assertions.assertEquals(result,1);
    }

    @Test
    void testSelect() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(OUTER.ID,
                OUTER.NAME,
                INNER.ID,
                INNER.TYPE)
            .from(OUTER.as("o"))
            .leftJoin(INNER).as("i").on(INNER.ID.eq(2))
            .limit(1);
        Outer outer = outerMapper.selectOneByQuery(queryWrapper);
        System.out.println(outer);
    }

}
