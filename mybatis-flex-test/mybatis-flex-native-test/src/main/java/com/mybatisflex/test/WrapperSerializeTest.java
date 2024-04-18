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

package com.mybatisflex.test;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.mybatisflex.core.query.QueryWrapper;
import org.nustaq.serialization.FSTConfiguration;

import static com.mybatisflex.test.table.AccountTableDef.ACCOUNT;


public class WrapperSerializeTest {

    public static void main(String[] args) throws Exception {
        QueryWrapper wrapper = QueryWrapper.create()
            .select().from(ACCOUNT)
            .where(ACCOUNT.ID.eq("michael"))
            .and(ACCOUNT.AGE.ge(18))
            .orderBy(ACCOUNT.ID.desc());


        byFst(wrapper);

    }


    private static void byFst(QueryWrapper wrapper) {
        FSTConfiguration fst = FSTConfiguration.createDefaultConfiguration();
        byte[] bytes = fst.asByteArray(wrapper);
        QueryWrapper newWrapper = (QueryWrapper) fst.asObject(bytes);
        System.out.println(newWrapper);
    }


    private static void byFastjson2(QueryWrapper wrapper) {
        String s = JSON.toJSONString(wrapper, JSONWriter.Feature.FieldBased, JSONWriter.Feature.WriteClassName, JSONWriter.Feature.ReferenceDetection);
        System.out.println(s);

        QueryWrapper newWrapper = JSON.parseObject(s, QueryWrapper.class, JSONReader.Feature.FieldBased, JSONReader.Feature.SupportAutoType);
        String s2 = JSON.toJSONString(newWrapper, JSONWriter.Feature.FieldBased, JSONWriter.Feature.WriteClassName, JSONWriter.Feature.ReferenceDetection);
        System.out.println(s2);
    }

}
