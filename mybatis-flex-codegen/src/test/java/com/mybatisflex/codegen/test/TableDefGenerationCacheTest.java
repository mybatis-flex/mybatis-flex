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

package com.mybatisflex.codegen.test;

import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.TableConfig;
import com.mybatisflex.codegen.entity.Table;
import com.mybatisflex.codegen.generator.impl.TableDefGenerator;
import com.mybatisflex.core.table.TableDef;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TableDefGenerationCacheTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldIsolateGeneratedTableDefsInDifferentPackages() throws Exception {
        File orderSource = generateTableDef("generated.orders");
        File reportSource = generateTableDef("generated.reports");
        File classes = temporaryFolder.newFolder("classes");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler);
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            assertTrue(compiler.getTask(null, fileManager, null,
                Arrays.asList("-proc:none", "-classpath",
                    System.getProperty("surefire.test.class.path", System.getProperty("java.class.path")),
                    "-d", classes.getAbsolutePath()), null,
                fileManager.getJavaFileObjects(orderSource, reportSource)).call());
        }

        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{classes.toURI().toURL()},
            TableDef.class.getClassLoader())) {
            TableDef order = (TableDef) classLoader.loadClass("generated.orders.CacheCodegenOrdersTableDef")
                .getDeclaredConstructor().newInstance();
            TableDef report = (TableDef) classLoader.loadClass("generated.reports.CacheCodegenOrdersTableDef")
                .getDeclaredConstructor().newInstance();
            TableDef orderAlias = order.as("o");
            TableDef reportAlias = report.as("o");

            assertEquals(order.getClass(), orderAlias.getClass());
            assertEquals(report.getClass(), reportAlias.getClass());
            assertNotSame(orderAlias, reportAlias);
            assertEquals("o", orderAlias.getAlias());
            assertEquals("o", reportAlias.getAlias());
            assertSame(orderAlias, order.as("o"));
            assertSame(reportAlias, report.as("o"));
            assertNotSame(orderAlias, order.as("different"));
        }
    }

    private File generateTableDef(String packageName) {
        GlobalConfig config = new GlobalConfig();
        config.setSourceDir(temporaryFolder.getRoot().getAbsolutePath());
        config.setTableDefPackage(packageName);
        config.setTableDefGenerateEnable(true);
        Table table = new Table();
        table.setName("cache_codegen_orders");
        table.setGlobalConfig(config);
        table.setEntityConfig(config.getEntityConfig());
        table.setTableConfig(new TableConfig());
        new TableDefGenerator().generate(table, config);
        return new File(temporaryFolder.getRoot(), packageName.replace('.', '/')
            + "/" + table.buildTableDefClassName() + ".java");
    }
}
