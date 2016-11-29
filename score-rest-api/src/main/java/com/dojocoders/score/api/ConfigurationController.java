package com.dojocoders.score.api;

import com.dojocoders.score.model.Configuration;
import com.dojocoders.score.repository.ConfigurationRepository;
import com.dojocoders.score.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;

@RestController
@RequestMapping("/api/conf")
@CrossOrigin
public class ConfigurationController {

	@Autowired
	private ConfigurationService configurationService;

	@RequestMapping("/{mode}")
	public Configuration get(@PathVariable String mode) {
		return configurationService.getConfiguration(mode);
	}

	@RequestMapping(method = RequestMethod.POST)
	public Configuration post(@RequestBody Configuration conf) {
		return configurationService.setConfiguration(conf);
	}

	@RequestMapping(value = "/{mode}/{key}/{value}", method = RequestMethod.PATCH)
	public Configuration patch(@PathVariable String mode, @PathVariable String key, @PathVariable String value) {
		return configurationService.updateConfiguration(mode, key, value);
	}

}
