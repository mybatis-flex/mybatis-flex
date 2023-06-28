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

package com.mybatisflex.solon.integration;

import org.apache.ibatis.solon.MybatisAdapter;
import org.apache.ibatis.solon.MybatisAdapterFactory;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Props;

/**
 * MyBatis-Flex 适配器工厂。
 *
 * @author noear
 * @since 2.2
 */
public class MybatisAdapterFactoryFlex implements MybatisAdapterFactory {

    @Override
    public MybatisAdapter create(BeanWrap dsWrap) {
        return new MybatisAdapterFlex(dsWrap);
    }

    @Override
    public MybatisAdapter create(BeanWrap dsWrap, Props dsProps) {
        return new MybatisAdapterFlex(dsWrap, dsProps);
    }

}
