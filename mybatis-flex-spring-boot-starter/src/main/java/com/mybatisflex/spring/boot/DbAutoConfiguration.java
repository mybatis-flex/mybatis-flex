/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.spring.boot;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.spring.SpringRowSessionManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

@ConditionalOnClass(Db.class)
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({MybatisFlexAutoConfiguration.class})
public class DbAutoConfiguration {

    public DbAutoConfiguration() {
        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();
        if (defaultConfig == null){
            Logger.getLogger(Db.class.getName()).log(Level.WARNING,"Cannot get FlexGlobalConfig instance, Perhaps the dataSource config error.");
        }else {
            Db.invoker().setRowSessionManager(new SpringRowSessionManager());
        }
    }
}
