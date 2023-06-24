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
package com.mybatisflex.spring.boot;

/**
 * MyBatisFlex 初始化监听器
 * 一般可以用于去初始化：
 *
 * 1、自定义主键生成器
 * 2、FlexGlobalConfig 的全局配置
 * 3、多租户配置
 * 4、动态表名配置
 * 5、逻辑删除处理器配置
 * 6、自定义脱敏规则
 * 7、SQL 审计配置
 * 8、SQL 打印配置
 * 9、数据源解密器配置
 * 10、自定义数据方言配置
 * ...
 */
public interface MyBatisFlexInitializer {
    void onInitBefore();
}
