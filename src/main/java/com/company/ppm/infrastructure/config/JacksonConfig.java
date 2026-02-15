package com.company.ppm.infrastructure.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer strictDeserializationCustomizer() {
        return builder -> builder.featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
