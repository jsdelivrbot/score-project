import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {Subscription} from "rxjs";
import {ScoreDataService} from "./score.dataservice";
import {SprintTimer} from "./ScoreResult";

@Component({
    selector: 'score-timer',
    template: `
        <table>
            <tr>
                <td class = "col-md-9" style="font-size: 4em;">Preparing next Sprint in : {{sprintTimer.countdown}} seconds</td>
                <td class = "col-md-3">
                    <div class="btn-group">
                        <button (click)="pauseClicked()" class="btn btn-warning btn-lg" [disabled]="!sprintTimer.started"><strong>Pause</strong></button>
                        <button (click)="startClicked()" class="btn btn-info btn-lg" [disabled]="sprintTimer.started"><strong>Start</strong></button>
                    </div>
                </td>
            </tr>
        </table>`
})
export class SprintTimerComponent implements OnInit {

    ngOnInit(): void {
        this.launchOnceSynchroCounter();
        this.observableTimer = Observable.interval(1000);
        Observable.interval(10000).subscribe(this.launchOnceSynchroCounter);
    }

    private subscription: Subscription;
    private observableTimer: Observable<any>;

    private sprintTimer: SprintTimer = {
        countdown: 0,
        started: false
    };


    constructor(private _scoreDataService: ScoreDataService) {

    }

    pauseClicked(event) {
        this._scoreDataService.PauseSprintTimer()
            .subscribe((response: SprintTimer) => this.updateComponentState(response),
                error => console.log(error));
        this.subscription.unsubscribe();
    }

    startClicked(event) {
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