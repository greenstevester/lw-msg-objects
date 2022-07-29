package net.greensill.lw.testutils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import java.time.ZoneId;
import java.util.TimeZone;

import static net.greensill.lw.common.Common.DATE_TIME_FORMATTER;
import static net.greensill.lw.common.Common.DEFAULT_SIMPLE_DATE_FORMAT;

public class TestUtils {

    public static ObjectMapper createJsonMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.setDateFormat(DEFAULT_SIMPLE_DATE_FORMAT);
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);
        SimpleModule module = new SimpleModule();
        module.addSerializer(new LocalDateSerializer(DATE_TIME_FORMATTER));
        module.addDeserializer(LocalDateDeserializer.class, (JsonDeserializer) new LocalDateDeserializer(DATE_TIME_FORMATTER));
        objectMapper.registerModule(module);
        return objectMapper;
    }

}
