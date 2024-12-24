/*
 *  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
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

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.datasource.DataSourceDecipher;
import com.mybatisflex.core.datasource.DataSourceManager;
import com.mybatisflex.core.datasource.DataSourceProperty;
import com.mybatisflex.core.mybatis.FlexConfiguration;
import com.mybatisflex.solon.ConfigurationCustomizer;
import com.mybatisflex.solon.MyBatisFlexCustomizer;
import com.mybatisflex.test.unmapped.MyUnMappedColumnHandler;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.noear.solon.annotation.Configuration;

@Configuration
public class MyConfigurationCustomizer implements ConfigurationCustomizer, MyBatisFlexCustomizer {

    @Override
    public void customize(FlexConfiguration configuration) {
        System.out.println(">>>>>>> MyConfigurationCustomizer.customize() invoked");
        configuration.setLogImpl(StdOutImpl.class);
    }

//    @Bean
//    public DataSourceDecipher decipher() {
//        DataSourceDecipher decipher = new DataSourceDecipher() {
//            @Override
//            public String decrypt(DataSourceProperty property, String value) {
//                System.out.println(">>>>>> decipher.decrypt");
//                return value;
//            }
//        };
//        return decipher;
//    }

    @Override
    public void customize(FlexGlobalConfig globalConfig) {

        DataSourceDecipher decipher = (property, value) -> {
            System.out.println(">>>>>> decipher.decrypt");
            return value;
        };
        DataSourceManager.setDecipher(decipher);
        globalConfig.setUnMappedColumnHandler(new MyUnMappedColumnHandler());

    }
}
