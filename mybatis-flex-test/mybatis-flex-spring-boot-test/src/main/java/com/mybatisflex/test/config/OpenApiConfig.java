package com.mybatisflex.test.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger配置
 *
 * @author sam
 */
@Configuration
public class OpenApiConfig {

    /**
     * 本系统端口
     */
    @Value("${spring-boot.version:2.7.11}")
    private String version;

    /**
     * swagger2 http://localhost:8080/swagger-ui.html
     * swagger3 http://localhost:8080/swagger-ui/index.html
     *
     * @return
     */
    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("mybatis-flex测试")
                        .description("mybatis-flex")
                        .version(version));
    }
}
