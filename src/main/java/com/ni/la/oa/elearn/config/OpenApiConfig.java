package com.ni.la.oa.elearn.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI baseOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("E-Learning API")
                .version("v1")
                .description("Backend-only API for courses, lessons, quizzes and progress"));
    }
}
