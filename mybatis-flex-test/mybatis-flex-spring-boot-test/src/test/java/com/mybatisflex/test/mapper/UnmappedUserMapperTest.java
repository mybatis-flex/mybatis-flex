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
package com.mybatisflex.test.mapper;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.model.UnmappedUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.mybatisflex.test.model.table.UnmappedUserTableDef.UNMAPPED_USER;

/**
 * UnMappedUserMapperTest
 *
 * @author wy
 * @version 1.0
 * @date 2024/9/12 11:39
 **/
@SpringBootTest
@SuppressWarnings("all")
public class UnmappedUserMapperTest {

    @Autowired
    private UnmappedUserMapper unmappedUserMapper;

    /**
     * 额外字段查询，数据库中有，但是实体类中没有
     * 应用：前端具有一定查询数据库能力时，不改后端代码情况下，返回新增字段数据
     */
    @Test
    void testExtraColumn() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(UNMAPPED_USER.ID, UNMAPPED_USER.AGE, UNMAPPED_USER.NAME,
                // 额外字段查询
                new QueryColumn("code"))
            .where(UNMAPPED_USER.ID.in(1, 2, 3));
        List<UnmappedUser> unmappedUserList = unmappedUserMapper.selectListByQuery(queryWrapper);
        System.out.println(unmappedUserList);
    }

    /**
     * 同名字段
     * 多数据源下同表同名字段不同含义或者需要同时展示
     */
    @Test
    void testSameColumn() {
        // 可能多数据源下会存在同表同名字段不同值
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(UNMAPPED_USER.ID, UNMAPPED_USER.AGE, UNMAPPED_USER.NAME,
                // 同名字段重置
                UNMAPPED_USER.NAME.as("ext_name"))
            .where(UNMAPPED_USER.ID.in(1, 2, 3));
        List<UnmappedUser> unmappedUserList = unmappedUserMapper.selectListByQuery(queryWrapper);
        System.out.println(unmappedUserList);
    }

    /**
     * 计算或者处理的字段
     * sql中进行处理的字段，不直接映射到实体类上的域
     */
    @Test
    void testCalColumn() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(UNMAPPED_USER.ID, UNMAPPED_USER.NAME, UNMAPPED_USER.AGE,
                // 字段计算结果
                QueryMethods.case_()
                    .when(UNMAPPED_USER.AGE.ge(18)).then("adult")
                    .when(UNMAPPED_USER.AGE.le(14)).then("child")
                    .else_("juvenile")
                    .end()
                    .as("age_group"))
            .where(UNMAPPED_USER.ID.in(1, 2, 3));
        List<UnmappedUser> unmappedUserList = unmappedUserMapper.selectListByQuery(queryWrapper);
        System.out.println(unmappedUserList);
    }

}
