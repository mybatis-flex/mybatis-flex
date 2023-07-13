///*
// *  Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
// *  <p>
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *  <p>
// *  http://www.apache.org/licenses/LICENSE-2.0
// *  <p>
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//package com.mybatisflex.core.relation;
//
//import com.mybatisflex.core.query.QueryCondition;
//
//import static com.mybatisflex.core.query.QueryMethods.column;
//
//class Condition {
//
//    private String column;
//    private String logic;
//    private String[] value;
//
//    public Condition(com.mybatisflex.annotation.Condition annotation) {
//        this.column = annotation.column();
//        this.logic = " " + annotation.logic().toUpperCase().trim()+" ";
//        this.value = annotation.value();
//    }
//
//    public QueryCondition toQueryCondition() {
//        return QueryCondition.create(column(column), logic, getValue());
//    }
//
//    public Object getValue() {
//        if (value == null || value.length == 0) {
//            return null;
//        } else if (value.length == 1) {
//            return value[0];
//        }
//        return value;
//    }
//}
