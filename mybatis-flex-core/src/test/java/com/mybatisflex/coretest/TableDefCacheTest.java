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

package com.mybatisflex.coretest;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.TableDef;
import com.mybatisflex.coretest.table.CacheOrderTableDef;
import com.mybatisflex.coretest.table.CacheOrderReportTableDef;
import org.junit.Test;

import static com.mybatisflex.coretest.table.CacheOrderTableDef.CACHE_ORDER;
import static com.mybatisflex.coretest.table.CacheOrderReportTableDef.CACHE_ORDER_REPORT;
import static org.junit.Assert.*;

public class TableDefCacheTest {

    @Test
    public void shouldKeepGeneratedTypesWhenOrderIsAliasedFirst() {
        CacheOrderTableDef order = CACHE_ORDER.as("order_first");
        CacheOrderReportTableDef report = CACHE_ORDER_REPORT.as("order_first");

        assertNotSame(order, report);
        assertEquals("order_first", order.getAlias());
        assertEquals("order_first", report.getAlias());
        assertSame(order, CACHE_ORDER.as("order_first"));
        assertSame(report, CACHE_ORDER_REPORT.as("order_first"));
    }

    @Test
    public void shouldKeepGeneratedTypesWhenReportIsAliasedFirst() {
        CacheOrderReportTableDef report = CACHE_ORDER_REPORT.as("report_first");
        CacheOrderTableDef order = CACHE_ORDER.as("report_first");

        assertNotSame(order, report);
        assertSame(order, CACHE_ORDER.as("report_first"));
        assertSame(report, CACHE_ORDER_REPORT.as("report_first"));
    }

    @Test
    public void shouldNotShareGeneratedAliasWithPlainTableDef() {
        TableDef plain = new TableDef("", "cache_orders") {}.as("plain_first");
        CacheOrderTableDef order = CACHE_ORDER.as("plain_first");

        assertNotSame(plain, order);
        assertEquals(plain.getNameWithSchema(), order.getNameWithSchema());
        assertEquals(plain.getAlias(), order.getAlias());
    }

    @Test
    public void shouldKeepColumnsBoundToEachGeneratedAlias() {
        CacheOrderTableDef order = CACHE_ORDER.as("o");
        CacheOrderReportTableDef report = CACHE_ORDER_REPORT.as("o");

        assertEquals("SELECT `o`.`id`, `o`.`order_no` FROM `cache_orders` AS `o`",
            QueryWrapper.create().select(order.ID, order.ORDER_NO).from(order).toSQL());
        assertEquals("SELECT `o`.`id` FROM `cache_orders` AS `o`",
            QueryWrapper.create().select(report.ID).from(report).toSQL());
        assertNotSame(order, CACHE_ORDER.as("other"));
    }

}

@Table("cache_orders")
class CacheOrder {
    @Id
    private Long id;
    private String orderNo;
}

@Table("cache_orders")
class CacheOrderReport {
    @Id
    private Long id;
}
