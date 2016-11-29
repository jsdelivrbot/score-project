package com.dojocoders.score.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableCouchbaseRepositories
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {

    @Value("${couchbase.bucket}")
    private String bucketName;

    @Value("${couchbase.password}")
    private String password;

    @Value("${couchbase.hosts}")
    private String hosts;

    @Value("${persist.scope}")
    private String persistScope;

    @Override
    protected List<String> getBootstrapHosts() {
        return Arrays.asList(this.hosts.split(","));
    }

    @Override
    protected String getBucketName() {
        return this.bucketName;
    }

    @Override
    protected String getBucketPassword() {
        return this.password;
    }

}
