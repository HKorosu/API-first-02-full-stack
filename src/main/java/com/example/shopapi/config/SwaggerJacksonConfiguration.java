package com.example.shopapi.config;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Json31;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerJacksonConfiguration {
    public SwaggerJacksonConfiguration() {
        Json.mapper().registerModule(new Jdk8Module());
        Json31.mapper().registerModule(new Jdk8Module());
    }
}
