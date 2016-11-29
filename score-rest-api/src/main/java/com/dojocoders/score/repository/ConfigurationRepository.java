package com.dojocoders.score.repository;

import com.dojocoders.score.model.Configuration;
import org.springframework.data.repository.CrudRepository;

public interface ConfigurationRepository extends CrudRepository<Configuration, String> {
}
