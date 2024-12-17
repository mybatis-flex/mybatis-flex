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
package com.mybatisflex.solon;

import com.mybatisflex.core.FlexGlobalConfig;

/**
 * <p>MyBatis-Flex 配置。
 *
 * <p>一般可以用于去初始化：
 *
 * <ul>
 *      <li>FlexGlobalConfig 的全局配置
 *      <li>自定义主键生成器
 *      <li>多租户配置
 *      <li>动态表名配置
 *      <li>逻辑删除处理器配置
 *      <li>自定义脱敏规则
 *      <li>SQL 审计配置
 *      <li>SQL 打印配置
 *      <li>数据源解密器配置
 *      <li>自定义数据方言配置
 *      <li>...
 * </ul>
 */
@FunctionalInterface
public interface MyBatisFlexCustomizer {

    /**
     * 自定义 MyBatis-Flex 配置。
     *
     * @param globalConfig 全局配置
     */
    void customize(FlexGlobalConfig globalConfig);
}
