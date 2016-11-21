package org.soneira.score;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.soneira.score.junit.ScoreService;
import org.soneira.score.junit.model.Score;
import org.soneira.score.junit.persistence.Couchbase;
import org.soneira.score.junit.persistence.PersistUnit;
import org.soneira.score.junit.persistence.StaticMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class ScoreRestConfiguration {

    @Value("${couchbase.hosts}")
    private String couchbaseHosts;

    @Value("${couchbase.bucket}")
    private String bucketName;

    @Value("${couchbase.password}")
    private String bucketPassword;

    @Value("${persist.scope}")
    private String persistScope;

    @Bean
    public ScoreService scoreService() {
        return new ScoreService(persistUnit());
    }

    private PersistUnit persistUnit() {
        return "couchbase".equals(persistScope) ?
                new Couchbase(couchbaseHosts.split(","), bucketName, bucketPassword)
                : new StaticMap();
    }

    @Bean
    @Primary
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonComponentModule javaTimeModule = new JsonComponentModule();
        javaTimeModule.addSerializer(Date.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(Date.class, new LocalDateDeserializer());
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    public class LocalDateSerializer extends JsonSerializer<Date> {
        @Override
        public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            SimpleDateFormat sm = new SimpleDateFormat(Score.JSON_DATE_PATTERN);
            gen.writeString(sm.format(value));
        }
    }

    public class LocalDateDeserializer extends JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            SimpleDateFormat sm = new SimpleDateFormat(Score.JSON_DATE_PATTERN);
            try {
                return sm.parse(p.getValueAsString());
            } catch (ParseException e) {
                throw new IOException(e);
            }
        }
    }
}
