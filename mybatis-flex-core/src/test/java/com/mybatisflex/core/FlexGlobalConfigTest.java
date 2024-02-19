package com.mybatisflex.core;

import com.mybatisflex.annotation.UpdateListener;
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
