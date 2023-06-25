package com.mybatisflex.test;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.FlexDataSource;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class DataSourceInitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FlexDataSource dataSource = (FlexDataSource) FlexGlobalConfig.getDefaultConfig()
                .getConfiguration().getEnvironment().getDataSource();

        System.out.println("onApplicationEvent>>>> datasource:" + dataSource);
    }

}
