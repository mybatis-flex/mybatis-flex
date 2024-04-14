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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.test.alisa.SysUser;
import com.mybatisflex.test.alisa.UserVO;
import org.apache.ibatis.mapping.ResultMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static com.mybatisflex.core.query.QueryMethods.column;
import static com.mybatisflex.core.query.QueryMethods.select;
import static com.mybatisflex.test.alisa.table.SysDeptTableDef.SYS_DEPT;
import static com.mybatisflex.test.alisa.table.SysRoleTableDef.SYS_ROLE;
import static com.mybatisflex.test.alisa.table.SysUserTableDef.SYS_USER;

/**
 * 别名测试。
 *
 * @author 王帅
 * @since 2023-11-16
 */
@SpringBootTest
class AlisaTest {

    @Autowired
    SysUserMapper userMapper;

    @Autowired
    ObjectMapper objectMapper;

    void printList(QueryWrapper queryWrapper) {
        List<SysUser> users = userMapper.selectListByQuery(queryWrapper);
        Assertions.assertDoesNotThrow(() ->
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(users)));
    }

    @Test
    void test01() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(SYS_USER.DEFAULT_COLUMNS)
            .select(SYS_ROLE.DEFAULT_COLUMNS)
            .from(SYS_USER.as("u"))
            .leftJoin(SYS_ROLE.as("r")).on(SYS_USER.ID.eq(1));

        printList(queryWrapper);
    }

    @Test
    void test02() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(SYS_USER.DEFAULT_COLUMNS)
            .select(SYS_ROLE.DEFAULT_COLUMNS)
            .select(SYS_DEPT.DEFAULT_COLUMNS)
            .from(SYS_USER.as("u"))
            .leftJoin(SYS_ROLE.as("r")).on(SYS_USER.ID.eq(1))
            .leftJoin(SYS_DEPT.as("d")).on(SYS_USER.ID.eq(1));

        printList(queryWrapper);
    }

    @Test
    void test03() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            // 调整 SELECT 顺序
            .select(SYS_ROLE.DEFAULT_COLUMNS)
            .select(SYS_DEPT.DEFAULT_COLUMNS)
            .select(SYS_USER.DEFAULT_COLUMNS)
            .from(SYS_USER.as("u"))
            .leftJoin(SYS_ROLE.as("r")).on(SYS_USER.ID.eq(1))
            .leftJoin(SYS_DEPT.as("d")).on(SYS_USER.ID.eq(1));

        printList(queryWrapper);
    }

    @Test
    void test04() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(column("`u`.`create_by` AS `sys_user$create_by`"))
            .select(column("`u`.`update_by` AS `sys_user$update_by`"))
            .from(SYS_USER.as("u"));

        printList(queryWrapper);
    }

    @Test
    void test05() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            // 不支持的情况
            .select(column("`u`.`create_by`"))
            .select(column("`u`.`update_by`"))
            .select(column("`d`.`create_by`"))
            .select(column("`d`.`update_by`"))
            .from(select(column("*")).from(SYS_USER)).as("u")
            .from(SYS_DEPT.as("d"));

        printList(queryWrapper);
    }

    @Test
    void test06() {
        QueryWrapper queryWrapper = QueryWrapper.create()
            // SELECT 里没有重名列 例如：id
            .select(SYS_USER.ID, SYS_USER.USER_NAME, SYS_USER.AGE, SYS_USER.BIRTHDAY)
            .select(SYS_ROLE.CREATE_BY.as("sys_role$create_by"))
            .from(SYS_USER.as("u"))
            .leftJoin(SYS_ROLE.as("r")).on(SYS_USER.ID.eq(1));

        Object[] objects = FlexGlobalConfig.getDefaultConfig()
            .getConfiguration()
            .getResultMaps()
            .toArray();

        Object[] resultMaps = Arrays.stream(objects)
            .filter(e -> e instanceof ResultMap)
            .map(e -> (ResultMap) e)
            .filter(e -> e.getId().contains("Sys"))
            .filter(e -> !e.getId().contains("select"))
            .toArray();

        System.out.println(resultMaps.length);

        printList(queryWrapper);
    }

    @Test
    void test07() throws JsonProcessingException {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(SYS_USER.DEFAULT_COLUMNS)
            .select(SYS_ROLE.DEFAULT_COLUMNS)
            .select(SYS_DEPT.DEFAULT_COLUMNS)
            .from(SYS_USER.as("u"))
            .leftJoin(SYS_ROLE.as("r")).on(SYS_USER.ID.eq(1))
            .leftJoin(SYS_DEPT.as("d")).on(SYS_USER.ID.eq(1));

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

        String userList1 = objectWriter.writeValueAsString(userMapper.selectListByQuery(queryWrapper));
        String userList2 = objectWriter.writeValueAsString(userMapper.selectListByQueryAs(queryWrapper, UserVO.class));

        Assertions.assertEquals(userList1, userList2);
    }

    @Test
    void test08() throws JsonProcessingException {
        SysUser user = userMapper.selectOneById(1);
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        System.out.println(objectWriter.writeValueAsString(user));
    }

}
