package com.dojocoders.score.api;

import com.dojocoders.score.model.SprintTimer;
import com.dojocoders.score.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sprint")
@CrossOrigin
public class SprintController {

	private Subscription subscription;
	private static SprintTimer sprintTimer = new SprintTimer();

	@Autowired
	private SprintService sprintService;

	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public SprintTimer start() {

		if (subscription == null) {
			sprintTimer.setCountdown(sprintService.getSprintTime());
		} else {
			subscription.unsubscribe();
		}

		subscription = Observable
				.interval(1, TimeUnit.SECONDS).timeInterval().observeOn(Schedulers.newThread())
				.subscribe(aLong -> {
					int countdown = sprintTimer.getCountdown();
					if (countdown >= 0) {
						sprintTimer.setCountdown(countdown - 1);
					} else {
						sprintTimer.setCountdown(sprintService.getSprintTime());
						// TODO launch jenkins job
					}
				});
		sprintTimer.setStarted(true);
		return sprintTimer;
	}

	@RequestMapping(value = "/pause", method = RequestMethod.PUT)
	public SprintTimer pause() {
		if (subscription == null) {
			throw new IllegalStateException("Start counter first");
		}
		subscription.unsubscribe();
		sprintTimer.setStarted(false);
		return sprintTimer;
	}

	@RequestMapping
	public SprintTimer get() {
		if (subscription == null) {
			throw new IllegalStateException("Start counter first");
		}
		return sprintTimer;
	}

}
