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

package com.mybatisflex.coretest;

import com.mybatisflex.core.exception.MybatisFlexException;
import com.mybatisflex.core.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilTest {

    @Test
    public void underlineToCamel() {
        String testString1 = "aa_bb_";
        String underlineToCamel = StringUtil.underlineToCamel(testString1);
        System.out.println(underlineToCamel);
        Assert.assertEquals("aaBb", underlineToCamel);

        String underline = StringUtil.camelToUnderline(underlineToCamel);
        System.out.println(underline);
        Assert.assertEquals("aa_bb", underline);
    }

    @Test
    public void testMethod2Property() {
        Assert.assertEquals("u", StringUtil.methodToProperty("isU"));
        Assert.assertEquals("u", StringUtil.methodToProperty("getU"));
        Assert.assertEquals("name", StringUtil.methodToProperty("getName"));
        Assert.assertEquals("uName", StringUtil.methodToProperty("getUName"));
        Assert.assertEquals("uName", StringUtil.methodToProperty("isUName"));
        Assert.assertThrows(MybatisFlexException.class, () -> StringUtil.methodToProperty("name"));
    }

}
