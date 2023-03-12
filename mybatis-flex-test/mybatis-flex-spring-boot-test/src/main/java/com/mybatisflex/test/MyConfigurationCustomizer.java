package com.mybatisflex.test;

import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.spring.boot.ConfigurationCustomizer;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfigurationCustomizer implements ConfigurationCustomizer {

    @Override
    public void customize(FlexConfiguration configuration) {
        System.out.println(">>>>>>> MyConfigurationCustomizer.customize() invoked");
        configuration.setLogImpl(StdOutImpl.class);
    }
}
