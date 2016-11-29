import {Component, OnInit, Output} from '@angular/core';
import {ScoreDataService} from './score/score.dataservice';
import {ScoreResult} from './score/ScoreResult';
import {Configuration} from "./app.configuration";
import {Observable} from "rxjs/Observable";

@Component({
    selector: 'score-app',
    templateUrl: 'app/app.component.html'
})
export class AppComponent implements OnInit {

    public ngOnInit(): any {
        this.scoreIhmTitle = this._configuration.title;
        this.GetAllScores()
        Observable.interval(3000).subscribe(this.GetAllScores);

    }

    public scoreChartColors: Array<number[]> = [
        [255, 99, 132],
        [54, 162, 235],
        [255, 206, 86],
        [231, 233, 237],
        [75, 192, 192],
        [151, 187, 205],
        [220, 220, 220],
        [247, 70, 74],
        [70, 191, 189],
        [253, 180, 92],
        [148, 159, 177],
        [242, 9, 21],
        [255, 182, 0],
        [255, 247, 30],
        [83, 255, 69],
        [214, 40, 40],
        [247, 127, 0],
        [252, 191, 73],
        [29, 172, 214],
        [77, 83, 96]
    ];

    teamColorCache = {};

    @Output() scoreResultList: ScoreResult[];

    lastSprint: number = 0;

    scoreIhmTitle: string;

    constructor(private _scoreDataService: ScoreDataService, private _configuration: Configuration) {

    }

    private GenerateTeamColors = (scoreResults: ScoreResult[]): ScoreResult[] => {
        for (let i = 0; i < scoreResults.length; i++) {
            scoreResults[i].color = this.teamColorCache[scoreResults[i].team] || this.scoreChartColors[i] || this.GetRandomColor();
            scoreResults[i].maxPoints = scoreResults[i].scores[scoreResults[i].scores.length - 1].points;
            scoreResults[i].increment = scoreResults[i].maxPoints - scoreResults[i].scores[scoreResults[i].scores.length - 2].points
            this.teamColorCache[scoreResults[i].team] = scoreResults[i].color;
        }

        return scoreResults.sort((score1, score2) => score2.maxPoints - score1.maxPoints);
    }

    private GetRandomColor = (): number[] => {
        return [this.GetRandomInt(0, 255), this.GetRandomInt(0, 255), this.GetRandomInt(0, 255)];
    }

    private GetRandomInt = (min: number, max: number): number => {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    private GetAllScores = (): void => {
        this._scoreDataService
            .GetAllScores()
            .subscribe((response: ScoreResult[]) => {
                    this.scoreResultList = this.GenerateTeamColors(response);
                    this.updateLastSprint();
                },
                error => console.log(error));
    }

    private updateLastSprint = (): void => {
        if (this.scoreResultList.length > 0) {
            this.lastSprint = this.scoreResultList[0].scores[this.scoreResultList[0].scores.length - 1].sprint;
        }
    }

}
