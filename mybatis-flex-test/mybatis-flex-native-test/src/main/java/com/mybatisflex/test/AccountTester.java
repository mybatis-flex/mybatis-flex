package com.mybatisflex.test;

import com.mybatisflex.annotation.SetListener;
import com.mybatisflex.core.FlexGlobalConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author 王帅
 * @since 2024-04-12
 */
public class AccountTester {

    @Test
    public void testRegisterListener() {
        FlexGlobalConfig globalConfig = FlexGlobalConfig.getDefaultConfig();
        globalConfig.registerSetListener(new AccountOnSetListener2(), Account.class);
        globalConfig.registerSetListener(new AccountOnSetListener3(), Account.class);
        List<SetListener> setListener = globalConfig.getSupportedSetListener(Account.class);
        Assert.assertEquals(2, setListener.size());
    }

}
