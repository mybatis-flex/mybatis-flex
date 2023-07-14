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
package com.mybatisflex.spring.service.impl;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.service.IService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 由 Mybatis-Flex 提供的顶级增强 Service 接口的默认实现类。
 *
 * @param <T> 实体类（Entity）类型
 * @param <M> 映射类（Mapper）类型
 * @author 王帅
 * @since 2023-05-01
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

    @Autowired
    protected M mapper;

    @Override
    public BaseMapper<T> getMapper() {
        return mapper;
    }

}
