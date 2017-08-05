import {Component, Output, OnInit, Input, OnChanges, SimpleChanges} from '@angular/core';
import {ScoreResult} from "./score.model";

@Component({
    selector: 'score-chart',
    templateUrl: './scorechart.component.html'
})
export class ScoreChartComponent implements OnInit, OnChanges {

    ngOnInit(): void {
        this.initFlag = true;
    }

    public initFlag: boolean = false;

    ngOnChanges(changes: SimpleChanges): void {
        if (this.initFlag && this.scoreResultList.length > 0) {
            this.updateData(this.scoreResultList);
        }
    }

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
            pointHoverBackgroundColor: '#ffffff',
            pointHoverBorderColor: this.rgba(colors, 0.8)
        };
    }

}