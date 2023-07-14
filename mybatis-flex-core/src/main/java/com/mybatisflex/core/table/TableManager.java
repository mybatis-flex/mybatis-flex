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
package com.mybatisflex.core.table;

import com.mybatisflex.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class TableManager {

    private TableManager() {
    }

    private static DynamicTableProcessor dynamicTableProcessor;
    private static DynamicSchemaProcessor dynamicSchemaProcessor;

    private static final ThreadLocal<Map<String, String>> tableNameMappingTL = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<String, String>> schemaMappingTL = ThreadLocal.withInitial(HashMap::new);


    public static DynamicTableProcessor getDynamicTableProcessor() {
        return dynamicTableProcessor;
    }

    public static void setDynamicTableProcessor(DynamicTableProcessor dynamicTableProcessor) {
        TableManager.dynamicTableProcessor = dynamicTableProcessor;
    }

    public static DynamicSchemaProcessor getDynamicSchemaProcessor() {
        return dynamicSchemaProcessor;
    }

    public static void setDynamicSchemaProcessor(DynamicSchemaProcessor dynamicSchemaProcessor) {
        TableManager.dynamicSchemaProcessor = dynamicSchemaProcessor;
    }

    public static void setHintTableMapping(String tableName, String mappingTable) {
        tableNameMappingTL.get().put(tableName, mappingTable);
    }

    public static String getHintTableMapping(String tableName) {
        return tableNameMappingTL.get().get(tableName);
    }

    public static void setHintSchemaMapping(String schema, String mappingSchema) {
        schemaMappingTL.get().put(schema, mappingSchema);
    }

    public static String getHintSchemaMapping(String schema) {
        return schemaMappingTL.get().get(schema);
    }


    public static String getRealTable(String tableName) {
        if (dynamicTableProcessor == null) {
            return tableName;
        }

        Map<String, String> mapping = tableNameMappingTL.get();

        String dynamicTableName = mapping.get(tableName);
        if (StringUtil.isNotBlank(dynamicTableName)) {
            return dynamicTableName;
        }

        dynamicTableName = dynamicTableProcessor.process(tableName);
        mapping.put(tableName, dynamicTableName);
        return dynamicTableName;
    }


    public static String getRealSchema(String schema) {
        if (dynamicSchemaProcessor == null) {
            return schema;
        }

        Map<String, String> mapping = schemaMappingTL.get();
        String dynamiSchema = mapping.get(schema);
        if (StringUtil.isNotBlank(dynamiSchema)) {
            return dynamiSchema;
        }

        dynamiSchema = dynamicSchemaProcessor.process(schema);
        mapping.put(schema, dynamiSchema);
        return dynamiSchema;
    }


//    public static void clear() {
//        if (dynamicTableProcessor != null) {
//            tableNameMappingTL.remove();
//        }
//        if (dynamicSchemaProcessor != null) {
//            schemaMappingTL.remove();
//        }
//    }

}
