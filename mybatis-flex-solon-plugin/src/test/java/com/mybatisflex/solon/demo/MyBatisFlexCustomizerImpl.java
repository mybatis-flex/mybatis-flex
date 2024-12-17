package com.mybatisflex.solon.demo;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.solon.ConfigurationCustomizer;
import com.mybatisflex.solon.MyBatisFlexCustomizer;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2024/12/17 created
 */
@Component
public class MyBatisFlexCustomizerImpl implements MyBatisFlexCustomizer, ConfigurationCustomizer {
    @Override
    public void customize(FlexGlobalConfig globalConfig) {

    }

    @Override
    public void customize(FlexConfiguration configuration) {

    }
}
