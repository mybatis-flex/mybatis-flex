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
package com.mybatisflex.core.keygen;

import com.mybatisflex.core.exception.FlexExceptions;
import com.mybatisflex.core.exception.locale.LocalizedFormats;
import com.mybatisflex.core.keygen.impl.FlexIDKeyGenerator;
import com.mybatisflex.core.keygen.impl.SnowFlakeIDKeyGenerator;
import com.mybatisflex.core.keygen.impl.UUIDKeyGenerator;
import com.mybatisflex.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class KeyGeneratorFactory {

    private KeyGeneratorFactory() {
    }

    private static final Map<String, IKeyGenerator> KEY_GENERATOR_MAP = new HashMap<>();

    static {
        /** 内置了 uuid 的生成器，因此主键配置的时候可以直接配置为 @Id(keyType = KeyType.Generator, value = "uuid")
         * {@link com.mybatisflex.annotation.Id}
         */
        register(KeyGenerators.uuid, new UUIDKeyGenerator());
        register(KeyGenerators.flexId, new FlexIDKeyGenerator());
        register(KeyGenerators.snowFlakeId, new SnowFlakeIDKeyGenerator());
    }


    /**
     * 获取 主键生成器
     *
     * @param name
     * @return 主键生成器
     */
    public static IKeyGenerator getKeyGenerator(String name) {
        if (StringUtil.isBlank(name)){
            throw FlexExceptions.wrap(LocalizedFormats.KEY_GENERATOR_BLANK);
        }
        return KEY_GENERATOR_MAP.get(name.trim());
    }


    /**
     * 注册一个主键生成器
     *
     * @param key
     * @param keyGenerator
     */
    public static void register(String key, IKeyGenerator keyGenerator) {
        KEY_GENERATOR_MAP.put(key.trim(), keyGenerator);
    }

}
