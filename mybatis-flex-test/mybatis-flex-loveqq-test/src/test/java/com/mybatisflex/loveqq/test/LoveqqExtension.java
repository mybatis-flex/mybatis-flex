package com.mybatisflex.loveqq.test;

import com.kfyty.loveqq.framework.boot.K;
import com.kfyty.loveqq.framework.core.autoconfig.ApplicationContext;
import com.kfyty.loveqq.framework.core.autoconfig.beans.AutowiredCapableSupport;
import com.kfyty.loveqq.framework.core.utils.BeanUtil;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LoveqqExtension implements BeforeAllCallback, BeforeEachCallback {
    private static ApplicationContext context;
    private static AutowiredCapableSupport autowiredCapable;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (context == null) {
            context = K.start(SampleApplication.class);
            autowiredCapable = context.getBean(AutowiredCapableSupport.BEAN_NAME);
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Object instance = extensionContext.getRequiredTestInstance();
        autowiredCapable.autowiredBean(BeanUtil.getBeanName(instance.getClass()), instance);
    }
}
