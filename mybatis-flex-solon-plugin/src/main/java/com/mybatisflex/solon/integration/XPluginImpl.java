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

import org.apache.ibatis.solon.integration.MybatisAdapterManager;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * 配置 MyBatis-Flex 插件。
 *
 * @author noear
 * @since 2.2
 */
public class XPluginImpl implements Plugin {

    //兼容 2.5 以下版本
    @Override
    public void start(AopContext context) throws Throwable {
        // 此插件的 solon.plugin.priority 会大于 mybatis-solon-plugin 的值
        MybatisAdapterManager.setAdapterFactory(new MybatisAdapterFactoryFlex());
    }

    //兼容 2.5 以上版本
    @Override
    public void start(AppContext context) throws Throwable {
        // 此插件的 solon.plugin.priority 会大于 mybatis-solon-plugin 的值
        MybatisAdapterManager.setAdapterFactory(new MybatisAdapterFactoryFlex());
    }
}
