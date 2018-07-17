package com.mitrais.polsdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.Collections;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket authApi(ServletContext servletContext) {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathProvider(new RelativePathProvider(servletContext))
                .select().apis(RequestHandlerSelectors.basePackage("com.mitrais.polsdemo.controller"))
                .paths(regex("/api.*"))
                .build();
    }

    private ApiInfo infoMetaData() {
        ApiInfo apiInfo = new ApiInfo(
                "Poll Rest API",
                "Rest API Demo using Spring boot, MySql, Spring Security, JWT",
                "1.0",
                "CDC",
                new Contact("Mitrais", "https://mitrais.com", "user@test.email"),
                "Mitrais", "https://mitrais.com", Collections.emptyList()
        );
        return apiInfo;
    }
}
