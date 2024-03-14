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

import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.core.FlexGlobalConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * 全局配置测试用例
 *
 * @author 阮胜
 * @since 2024-02-19
 */
public class FlexGlobalConfigTest {

    @Test
    public void testGetSupportedUpdateListener1() {
        FlexGlobalConfig flexGlobalConfig = new FlexGlobalConfig();
        flexGlobalConfig.registerUpdateListener(new CustomEntityListener(), Staff.class);
        List<UpdateListener> supportedUpdateListener = flexGlobalConfig.getSupportedUpdateListener(Staff.class);
        Assert.assertEquals(supportedUpdateListener.size(), 1);
    }

    @Test
    public void testGetSupportedUpdateListener2() {
        FlexGlobalConfig flexGlobalConfig = new FlexGlobalConfig();
        flexGlobalConfig.registerUpdateListener(new CustomEntityListener(), IBaseEntity.class);
        List<UpdateListener> supportedUpdateListener = flexGlobalConfig.getSupportedUpdateListener(Staff.class);
        Assert.assertEquals(supportedUpdateListener.size(), 1);
    }

    @Test
    public void testGetSupportedUpdateListener3() {
        FlexGlobalConfig flexGlobalConfig = new FlexGlobalConfig();
        flexGlobalConfig.registerUpdateListener(new CustomEntityListener(), Dept.class);
        List<UpdateListener> supportedUpdateListener = flexGlobalConfig.getSupportedUpdateListener(Dept.class);
        Assert.assertEquals(supportedUpdateListener.size(), 1);
    }

    @Test
    public void testGetSupportedUpdateListener4() {
        FlexGlobalConfig flexGlobalConfig = new FlexGlobalConfig();
        flexGlobalConfig.registerUpdateListener(new CustomEntityListener(), IGeneralEntity.class);
        List<UpdateListener> supportedUpdateListener = flexGlobalConfig.getSupportedUpdateListener(Dept.class);
        Assert.assertEquals(supportedUpdateListener.size(), 1);
    }

    @Test
    public void testGetSupportedUpdateListener5() {
        FlexGlobalConfig flexGlobalConfig = new FlexGlobalConfig();
        flexGlobalConfig.registerUpdateListener(new CustomEntityListener(), IBaseEntity.class);
        List<UpdateListener> supportedUpdateListener = flexGlobalConfig.getSupportedUpdateListener(Dept.class);
        Assert.assertEquals(supportedUpdateListener.size(), 1);
    }

    public static class CustomEntityListener implements UpdateListener {
        @Override
        public void onUpdate(Object entity) {
        }
    }

    public interface IBaseEntity {
    }

    public interface IGeneralEntity extends IBaseEntity {
    }

    public static class Staff implements IBaseEntity {

    }

    public static class Dept implements IGeneralEntity {
    }


}
