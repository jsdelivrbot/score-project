import {Component, Output, OnInit, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ScoreResult} from "./ScoreResult";
import {Observable} from "rxjs/Observable";

@Component({
    selector: 'score-chart',
    templateUrl: 'app/score/scorechart.component.html'
})
export class ScoreChartComponent implements OnInit, OnChanges {

    ngOnInit(): void {
        this.initFlag = true;
        Observable.interval(1000).subscribe(this.updateLaunchNextSprintCountdown);
    }

    public initFlag: boolean = false;

    ngOnChanges(changes: SimpleChanges): void {
        if (this.initFlag && this.scoreResultList.length > 0) {
            this.updateData(this.scoreResultList);
        }
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

    private updateLaunchNextSprintCountdown = (): void => {
        if (this.prepareNextSprintCountdown > 0) {
            this.prepareNextSprintCountdown = this.prepareNextSprintCountdown - 1;
        } else {
            // TODO launch jenkins from here
            this.prepareNextSprintCountdown = this.sprintTimelaps;
        }
    }

}