package com.burnoutinhos.burnoutinhos_api.config;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Preserve existing behavior for setter nulls
        mapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP));
        // Register JavaTime module to support java.time types (LocalDateTime, LocalDate, etc.)
        mapper.registerModule(new JavaTimeModule());
        // Configure to use ISO-8601 textual representation instead of timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false
        );

        mapper.findAndRegisterModules();

        return mapper;
    }
}
