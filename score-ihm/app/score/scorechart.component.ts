import {Component, Output, OnInit, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ScoreResult} from "./ScoreResult";
import {Observable} from "rxjs/Observable";
import {JenkinsLauncher} from "./jenkins.launcher";
import {Response} from "@angular/http";
import {Subject} from "rxjs/Subject";
import {Subscription} from "rxjs";

@Component({
    selector: 'score-chart',
    templateUrl: 'app/score/scorechart.component.html'
})
export class ScoreChartComponent implements OnInit, OnChanges {

    ngOnInit(): void {
        this.initFlag = true;
        this.observableTimer =  Observable.interval(1000);
    }

    public initFlag: boolean = false;

    private subscription: Subscription;
    private observableTimer: Observable<any>;

    ngOnChanges(changes: SimpleChanges): void {
        if (this.initFlag && this.scoreResultList.length > 0) {
            this.updateData(this.scoreResultList);
        }
    }

    constructor(private _jenkinsLauncher: JenkinsLauncher) {

    }

    @Input() public sprintTimelaps:number;

    @Input() scoreResultList: ScoreResult[];

    // lineChart
    @Output() public lineChartData: Array<any>;

    @Output() public lineChartLabels: Array<any>;

    @Output() public lineColors: Array<any>;

    public lineChartOptions: any = {
        animation: false,
        responsive: true,
        scaleShowLabels: false,
        elements: {point: {radius: 0}},
        scales: {
            xAxes: [{
                display: false
            }]
        }
    };

    public lineChartLegend: boolean = false;

    public lineChartType: string = 'line';

    public prepareNextSprintCountdown:number = 0;

    private updateData(scoreResultList: ScoreResult[]): void {

        this.lineColors = scoreResultList.map(scoreResult => this.formatLineColor(scoreResult.color));

        this.lineChartData = scoreResultList.map(scoreResult => {
            var json = {};
            json["fill"] = false;
            json["label"] = scoreResult.team;
            json["data"] = scoreResult.scores.map(score => score.points);
            return json;
        });

        this.lineChartLabels = scoreResultList[0].scores.map(score => score.sprint);

    }

    private rgba(colour: Array<number>, alpha: number): string {
        return 'rgba(' + colour.concat(alpha).join(',') + ')';
    }

    private formatLineColor(colors: Array<number>): any {
        return {
            backgroundColor: this.rgba(colors, 0.4),
            borderColor: this.rgba(colors, 1),
            pointBackgroundColor: this.rgba(colors, 1),
            pointBorderColor: '#fff',
            pointHoverBackgroundColor: '#fff',
            pointHoverBorderColor: this.rgba(colors, 0.8)
        };
    }

    public started: boolean = false;

    pauseClicked(event) {
        this.started = false;
        this.subscription.unsubscribe();
    }

    startClicked(event) {
        this.started = true;
        this.subscription = this.observableTimer.subscribe(this.updateLaunchNextSprintCountdown);
    }

    private updateLaunchNextSprintCountdown = (): void => {
        if (this.prepareNextSprintCountdown > 0) {
            this.prepareNextSprintCountdown = this.prepareNextSprintCountdown - 1;
        } else {
            this._jenkinsLauncher.LaunchJob()
                .subscribe((response: Response) => {
                        console.log("Job launched : " + JSON.stringify(response));
                    },
                    error => console.log(error));;
            this.prepareNextSprintCountdown = this.sprintTimelaps;
        }
    }

}