package com.dojocoders.score.api;

import com.dojocoders.score.model.SprintTimer;
import com.dojocoders.score.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sprint")
@CrossOrigin
public class SprintController {

	@Autowired
	private SprintService sprintService;

	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public SprintTimer start() {
		return sprintService.startSprintTimer();
	}

	@RequestMapping(value = "/pause", method = RequestMethod.PUT)
	public SprintTimer pause() {
		return sprintService.pauseSprintTimer();
	}

	@RequestMapping
	public SprintTimer get() {
		return sprintService.getSprintTimer();
	}

}
