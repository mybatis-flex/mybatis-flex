/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.paginate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Page<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int INIT_VALUE = -1;

    private List<T> records = Collections.emptyList();
    private int pageNumber = INIT_VALUE;
    private int pageSize = INIT_VALUE;
    private long totalPage = INIT_VALUE;
    private long totalRow = INIT_VALUE;

    public static Page of(int pageNumber, int pageSize) {
        return new Page(pageNumber, pageSize);
    }

    public static Page of(int pageNumber, int pageSize, long totalRow) {
        return new Page(pageNumber, pageSize, totalRow);
    }

    public Page() {

    }

    public Page(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public Page(int pageNumber, int pageSize, long totalRow) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalRow = totalRow;
        this.totalPage = totalRow % pageSize == 0 ? (totalRow / pageSize) : (totalRow / pageSize + 1);
    }

    public Page(List<T> records, int pageNumber, int pageSize, long totalRow) {
        this.records = records;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalRow = totalRow;
        this.totalPage = totalRow % pageSize == 0 ? (totalRow / pageSize) : (totalRow / pageSize + 1);
    }


    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }


    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(long totalRow) {
        this.totalRow = totalRow;
        this.totalPage = totalRow % pageSize == 0 ? (totalRow / pageSize) : (totalRow / pageSize + 1);
    }

    public <R> Page<R> map(Function<? super T, ? extends R> mapper) {
        Page<R> newPage = new Page<>();
        newPage.pageNumber = pageNumber;
        newPage.pageSize = pageSize;
        newPage.totalPage = totalPage;
        newPage.totalRow = totalRow;

        if (records != null) {
            List<R> newRecords = new ArrayList<>(records.size());
            for (T t : records) {
                newRecords.add(mapper.apply(t));
            }
            newPage.records = newRecords;
        }
        return newPage;
    }


    @Override
    public String toString() {
        return "Page{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalPage=" + totalPage +
                ", totalRow=" + totalRow +
                ", records=" + records +
                '}';
    }
}
