package com.dojocoders.score.timer;

import com.dojocoders.score.model.SprintTimer;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SprintTimerComponent {

    private Subscription subscription;

    private SprintTimer sprintTimer = new SprintTimer();

    private List<EndSprintAction> schedulers = Lists.newArrayList();

    public SprintTimer getTimer() {
        return sprintTimer;
    }

    public void addScheduler(EndSprintAction scheduler) {
        schedulers.add(scheduler);
    }

    public void clearSchedulers() {
        schedulers.clear();
    }

    public void start(int countdownTimer) {
        if (subscription == null) {
            sprintTimer.setCountdown(countdownTimer);
            subscription = Observable.interval(1, TimeUnit.SECONDS).timeInterval().observeOn(Schedulers.newThread()).subscribe(aLong -> {
                int countdown = sprintTimer.getCountdown();
                if (countdown > 0) {
                    sprintTimer.setCountdown(countdown - 1);
                } else {
                    for (EndSprintAction scheduler : schedulers) {
                        scheduler.schedule();
                    }

                }
            });
            sprintTimer.setStarted(true);
        }
    }

    public void stop() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
            sprintTimer.setStarted(false);
        }
    }

}
