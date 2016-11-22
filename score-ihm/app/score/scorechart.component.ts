import {Component, Output, OnInit} from '@angular/core';
import {ScoreDataService} from "./score.dataservice";
import {ScoreResult} from "./ScoreResult";

@Component({
    selector: 'score-chart',
    template: `<base-chart class="chart"
                [datasets]="lineChartData"
                [labels]="lineChartLabels"
                [options]="lineChartOptions"
                [legend]="lineChartLegend"
                [chartType]="lineChartType"></base-chart>`
})
export class ScoreChartComponent implements OnInit {

    ngOnInit(): void {
        this.GetAllScores();
    }

    constructor(private _scoreDataService: ScoreDataService) {

    }

    private GetAllScores = (): void => {
        this._scoreDataService
            .GetAllScores()
            .subscribe((response: ScoreResult[]) => {
                    var scoreResultList = response;
                    this.updateData(scoreResultList);
                },
                error => console.log(error));
    }


    // lineChart
    @Output() public lineChartData: Array<any>;

    @Output() public lineChartLabels: Array<any>;

    public lineChartOptions: any = {
        animation: false,
        responsive: true
    };

    public lineChartLegend: boolean = true;

    public lineChartType: string = 'line';

    private updateData(scoreResultList: ScoreResult[]): void {

        this.lineChartData = scoreResultList.map(scoreResult => {
            var json = {};
            json["fill"]=false;
            json["label"]=scoreResult.team;
            json["data"]= scoreResult.scores.map(score => score.points);
            console.log("JSON : " + JSON.stringify(json));
            return json;
        });

        this.lineChartLabels = scoreResultList[0].scores.map(score => score.sprint);

    }
}