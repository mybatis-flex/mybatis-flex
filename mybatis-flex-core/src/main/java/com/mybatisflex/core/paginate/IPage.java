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

package com.mybatisflex.core.paginate;

import java.io.Serializable;
import java.util.List;

/**
 * 分页接口。
 *
 * @param <T> 数据类型
 * @author 王帅
 * @since 2023-06-18
 */
public interface IPage<T> extends Serializable {

    /**
     * 获取当前页码。
     *
     * @return 页码
     */
    int getPageNumber();

    /**
     * 设置当前页码。
     *
     * @param pageNumber 页码
     */
    void setPageNumber(int pageNumber);

    /**
     * 获取当前每页数据数量。
     *
     * @return 每页数据数量
     */
    int getPageSize();

    /**
     * 设置当前每页数据数量。
     *
     * @param pageSize 每页数据数量
     */
    void setPageSize(int pageSize);

    /**
     * 获取数据总数。
     *
     * @return 数据总数
     */
    long getTotalRow();

    /**
     * 设置数据总数。
     *
     * @param totalRow 数据总数
     */
    void setTotalRow(long totalRow);

    /**
     * 获取当前页的数据。
     *
     * @return 当前页的数据
     */
    List<T> getRecords();

    /**
     * 设置当前页的数据。
     *
     * @param records 当前页的数据
     */
    void setRecords(List<T> records);

    /**
     * 获取当前分页偏移量。
     *
     * @return 偏移量
     */
    default int getOffset() {
        return getPageSize() * (getPageNumber() - 1);
    }

    /**
     * 是否自动优化 COUNT 查询语句（默认优化）。
     *
     * @return {@code true} 优化，{@code false} 不优化
     */
    default boolean isOptimizeCountSql() {
        return true;
    }

    /**
     * 设置是否自动优化 COUNT 查询语句。
     *
     * @param optimizeCountSql 是否优化
     */
    default void setOptimizeCountSql(boolean optimizeCountSql) {
        // 默认总是优化
    }

    /**
     * 获取总页数。
     *
     * @return 总页数
     */
    default long getTotalPage() {
        // 实时计算总页数
        int pageSize = getPageSize();
        if (pageSize == 0) {
            return 0L;
        }
        long totalRow = getTotalRow();
        long totalPage = totalRow / pageSize;
        if (totalRow % pageSize != 0) {
            totalPage++;
        }
        return totalPage;
    }

    /**
     * 设置总页数。
     *
     * @param totalPage 总页数
     */
    default void setTotalPage(long totalPage) {
        // 总页数是实时计算的，所以这里设置了也没用。
    }

    /**
     * 是否存在上一页。
     *
     * @return {@code true} 存在上一页，{@code false} 不存在上一页
     */
    default boolean hasPrevious() {
        return getPageNumber() > 1;
    }

    /**
     * 是否存在下一页。
     *
     * @return {@code true} 存在下一页，{@code false} 不存在下一页
     */
    default boolean hasNext() {
        return getPageNumber() < getTotalPage();
    }

    /**
     * 当前页是否为空。
     *
     * @return {@code true} 空页，{@code false} 非空页
     */
    default boolean isEmpty() {
        return getTotalRow() == 0 || getPageNumber() > getTotalPage();
    }

}