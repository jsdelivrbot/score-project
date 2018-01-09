import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { ScoreDataService } from './score.dataservice';
import { SprintTimer } from './score.model';

@Component({
    selector: 'score-timer',
    templateUrl: './sprinttimer.component.html'
})
export class SprintTimerComponent implements OnInit {

    private subscription: Subscription;
    private observableTimer: Observable<any>;

    sprintTimer: SprintTimer = {
        countdown: 0,
        started: false
    };

    constructor(private _scoreDataService: ScoreDataService) {

    }

    ngOnInit(): void {
        this.launchOnceSynchroCounter();
        this.observableTimer = Observable.interval(1000);
        Observable.interval(10000).subscribe(this.launchOnceSynchroCounter);
    }

    pauseClicked() {
        this._scoreDataService.PauseSprintTimer()
            .subscribe((response: SprintTimer) => this.updateComponentState(response),
            error => console.log(error));
        this.subscription.unsubscribe();
    }

    startClicked() {
        this._scoreDataService.StartSprintTimer()
            .subscribe((response: SprintTimer) => this.updateComponentState(response),
            error => console.log(error));
    }

    private updateLaunchNextSprintCountdown = (): void => {
        if (this.sprintTimer.countdown > 0) {
            this.sprintTimer.countdown = this.sprintTimer.countdown - 1;
        } else {
            this.sprintTimer.countdown = 0;
            this.launchOnceSynchroCounter();
        }
    }

    private launchOnceSynchroCounter = (): void => {
        this._scoreDataService.SynchroSprintTimer()
            .subscribe((response: SprintTimer) => this.updateComponentState(response),
            error => console.log(error)
            );
    }

    private updateComponentState = (sprintTimer: SprintTimer): void => {
        if (sprintTimer.started && this.subscription == null) {
            this.subscription = this.observableTimer.subscribe(this.updateLaunchNextSprintCountdown);
        }

        if (!sprintTimer.started && this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
        }

        this.sprintTimer = sprintTimer;
    }


}