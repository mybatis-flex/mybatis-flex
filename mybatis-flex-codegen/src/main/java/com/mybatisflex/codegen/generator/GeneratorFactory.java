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
package com.mybatisflex.codegen.generator;

import com.mybatisflex.codegen.constant.GenTypeConst;
import com.mybatisflex.codegen.generator.impl.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 代码生成器工厂，用于创建各种类型文件的生成。
 *
 * @see GenTypeConst
 */
public class GeneratorFactory {

    private static final Map<String, IGenerator> generators = new LinkedHashMap<>();

    static {
        registerGenerator(GenTypeConst.ENTITY, new EntityGenerator());
        registerGenerator(GenTypeConst.MAPPER, new MapperGenerator());
        registerGenerator(GenTypeConst.SERVICE, new ServiceGenerator());
        registerGenerator(GenTypeConst.SERVICE_IMPL, new ServiceImplGenerator());
        registerGenerator(GenTypeConst.CONTROLLER, new ControllerGenerator());
        registerGenerator(GenTypeConst.TABLE_DEF, new TableDefGenerator());
        registerGenerator(GenTypeConst.MAPPER_XML, new MapperXmlGenerator());
        registerGenerator(GenTypeConst.PACKAGE_INFO, new PackageInfoGenerator());
    }

    private GeneratorFactory() {
    }

    /**
     * 获取指定类型文件的生成器。
     *
     * @param genType 生成类型
     * @return 该类型的文件生成器
     */
    public static IGenerator getGenerator(String genType) {
        return generators.get(genType);
    }

    /**
     * 获取所有的文件生成器。
     *
     * @return 所有的文件生成器
     */
    public static Collection<IGenerator> getGenerators() {
        return generators.values();
    }

    /**
     * 注册文件生成器。
     *
     * @param name      生成器名称
     * @param generator 生成器
     */
    public static void registerGenerator(String name, IGenerator generator) {
        generators.put(name, generator);
    }

}
