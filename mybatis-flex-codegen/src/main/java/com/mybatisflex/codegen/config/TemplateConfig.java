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
package com.mybatisflex.codegen.config;

import com.mybatisflex.codegen.constant.GenTypeConst;
import com.mybatisflex.codegen.generator.GeneratorFactory;
import com.mybatisflex.codegen.template.ITemplate;
import com.mybatisflex.codegen.template.impl.EnjoyTemplate;

import java.io.Serializable;

/**
 * 模板配置。
 *
 * @author 王帅
 * @since 2023-05-17
 */
@SuppressWarnings("unused")
public class TemplateConfig implements Serializable {

    private static final long serialVersionUID = 6700855804948021101L;
    /**
     * 生成代码的模板引擎。
     */
    private ITemplate template = new EnjoyTemplate();

    /**
     * 获取模板引擎。
     */
    public ITemplate getTemplate() {
        return template;
    }

    /**
     * 设置模板引擎。
     */
    public TemplateConfig setTemplate(ITemplate template) {
        this.template = template;
        return this;
    }

    /**
     * 获取生成 Entity 模板文件的位置。
     */
    public String getEntity() {
        return GeneratorFactory.getGenerator(GenTypeConst.ENTITY).getTemplatePath();
    }

    /**
     * 设置生成 Entity 模板文件的位置。
     */
    public TemplateConfig setEntity(String entity) {
        GeneratorFactory.getGenerator(GenTypeConst.ENTITY).setTemplatePath(entity);
        return this;
    }

    /**
     * 获取生成 Mapper 模板文件的位置。
     */
    public String getMapper() {
        return GeneratorFactory.getGenerator(GenTypeConst.MAPPER).getTemplatePath();
    }

    /**
     * 设置生成 Mapper 模板文件的位置。
     */
    public TemplateConfig setMapper(String mapper) {
        GeneratorFactory.getGenerator(GenTypeConst.MAPPER).setTemplatePath(mapper);
        return this;
    }

    /**
     * 获取生成 Service 模板文件的位置。
     */
    public String getService() {
        return GeneratorFactory.getGenerator(GenTypeConst.SERVICE).getTemplatePath();
    }

    /**
     * 设置生成 Service 模板文件的位置。
     */
    public TemplateConfig setService(String service) {
        GeneratorFactory.getGenerator(GenTypeConst.SERVICE).setTemplatePath(service);
        return this;
    }

    /**
     * 获取生成 ServiceImpl 模板文件的位置。
     */
    public String getServiceImpl() {
        return GeneratorFactory.getGenerator(GenTypeConst.SERVICE_IMPL).getTemplatePath();
    }

    /**
     * 设置生成 ServiceImpl 模板文件的位置。
     */
    public TemplateConfig setServiceImpl(String serviceImpl) {
        GeneratorFactory.getGenerator(GenTypeConst.SERVICE_IMPL).setTemplatePath(serviceImpl);
        return this;
    }

    /**
     * 获取生成 Controller 模板文件的位置。
     */
    public String getController() {
        return GeneratorFactory.getGenerator(GenTypeConst.CONTROLLER).getTemplatePath();
    }

    /**
     * 设置生成 Controller 模板文件的位置。
     */
    public TemplateConfig setController(String controller) {
        GeneratorFactory.getGenerator(GenTypeConst.CONTROLLER).setTemplatePath(controller);
        return this;
    }

    /**
     * 获取生成 TableDef 模板文件的位置。
     */
    public String getTableDef() {
        return GeneratorFactory.getGenerator(GenTypeConst.TABLE_DEF).getTemplatePath();
    }

    /**
     * 设置生成 TableDef 模板文件的位置。
     */
    public TemplateConfig setTableDef(String tableDef) {
        GeneratorFactory.getGenerator(GenTypeConst.TABLE_DEF).setTemplatePath(tableDef);
        return this;
    }

    /**
     * 获取生成 MapperXml 模板文件的位置。
     */
    public String getMapperXml() {
        return GeneratorFactory.getGenerator(GenTypeConst.MAPPER_XML).getTemplatePath();
    }

    /**
     * 设置生成 MapperXml 模板文件的位置。
     */
    public TemplateConfig setMapperXml(String mapperXml) {
        GeneratorFactory.getGenerator(GenTypeConst.MAPPER_XML).setTemplatePath(mapperXml);
        return this;
    }

}
