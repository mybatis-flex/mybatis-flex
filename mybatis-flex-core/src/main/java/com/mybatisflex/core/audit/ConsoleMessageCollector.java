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
package com.mybatisflex.core.audit;


public class ConsoleMessageCollector implements MessageCollector {

    private SqlDebugPrinter printer = (sql, tookTimeMillis) -> {
        if (tookTimeMillis != null) {
            System.out.println("Flex exec sql took " + tookTimeMillis + " ms >>>  " + sql);
        } else {
            System.out.println("Flex exec sql >>>  " + sql);
        }
    };

    private SqlDebugExtPrinter extPrinter = (sql, dsName, tookTimeMillis) -> {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Flex exec");
        buffer.append("dsName >>> ").append(dsName);
        if (tookTimeMillis != null) {
            buffer.append(" sql took ").append(tookTimeMillis).append(" ms >>>  ").append(sql);
        } else {
            buffer.append(" sql >>> ").append(sql);
        }
        System.out.println(buffer);
    };

    public ConsoleMessageCollector() {
    }

    public ConsoleMessageCollector(SqlDebugPrinter printer) {
        this.printer = printer;
    }


    public ConsoleMessageCollector(SqlDebugExtPrinter printer) {
        this.extPrinter = printer;
    }

    @Override
    public void collect(AuditMessage message) {
        if (message.getDsName() == null) {
            printer.print(message.getFullSql(), message.getElapsedTime());
        } else {
            extPrinter.print(message.getFullSql(), message.getDsName(), message.getElapsedTime());
        }
    }

    public interface SqlDebugPrinter {

        void print(String sql, Long tookTimeMillis);

    }


    public interface SqlDebugExtPrinter {

        void print(String sql, String dsName, Long tookTimeMillis);

    }

}
