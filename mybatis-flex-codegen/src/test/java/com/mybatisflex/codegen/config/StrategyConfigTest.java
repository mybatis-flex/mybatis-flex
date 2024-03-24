package com.mybatisflex.codegen.config;

import junit.framework.TestCase;

public class StrategyConfigTest extends TestCase {

    StrategyConfig strategyConfig = new StrategyConfig();

    private void init(){
        strategyConfig.setUnGenerateTable("user*","sys_*","db*01");
    }

    public void testIsSupportGenerate() {
        init();
        boolean var1 = strategyConfig.isSupportGenerate("username");
        boolean var2 = strategyConfig.isSupportGenerate("sys_info");
        boolean var3 = strategyConfig.isSupportGenerate("db_redis_01");
        boolean var4 = strategyConfig.isSupportGenerate("other");
        if (var1 || var2 || var3 || !var4){
            throw new RuntimeException("测试失败");
        }
    }
}
