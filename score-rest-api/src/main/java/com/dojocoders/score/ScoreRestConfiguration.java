package com.dojocoders.score;

import com.dojocoders.score.junit.persistence.Couchbase;
import com.dojocoders.score.junit.persistence.PersistUnit;
import com.dojocoders.score.junit.ScoreService;
import com.dojocoders.score.junit.persistence.StaticMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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

}
