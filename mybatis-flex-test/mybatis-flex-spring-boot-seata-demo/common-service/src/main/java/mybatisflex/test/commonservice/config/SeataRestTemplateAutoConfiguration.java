/*
 *  Copyright 1999-2021 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package mybatisflex.test.commonservice.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import mybatisflex.test.commonservice.interceptor.SeataRestTemplateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SeataRestTemplateAutoConfiguration {
    @Autowired(required = false)
    private Collection<RestTemplate> restTemplates;
    @Autowired
    private SeataRestTemplateInterceptor seataRestTemplateInterceptor;

    public SeataRestTemplateAutoConfiguration() {
    }

    @Bean
    public SeataRestTemplateInterceptor seataRestTemplateInterceptor() {
        return new SeataRestTemplateInterceptor();
    }

    @PostConstruct
    public void init() {
        if (this.restTemplates != null) {
            Iterator var1 = this.restTemplates.iterator();

            while (var1.hasNext()) {
                RestTemplate restTemplate = (RestTemplate)var1.next();
                List<ClientHttpRequestInterceptor> interceptors = new ArrayList(restTemplate.getInterceptors());
                interceptors.add(this.seataRestTemplateInterceptor);
                restTemplate.setInterceptors(interceptors);
            }
        }

    }
}
