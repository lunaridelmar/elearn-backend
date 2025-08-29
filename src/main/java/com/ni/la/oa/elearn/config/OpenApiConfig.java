package com.ni.la.oa.elearn.config;

import com.ni.la.oa.elearn.api.dto.ApiResponse;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;

@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig().addResponseWrapperToIgnore(ApiResponse.class);
    }

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Learning API")
                        .version("v1")
                        .description("Backend-only API for courses, lessons, quizzes and progress"))
                // security scheme
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
