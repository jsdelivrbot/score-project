import {Component, OnInit, Output} from '@angular/core';
import { ScoreDataService } from './score/score.dataservice';
import {Rank, ScoreResult} from './score/ScoreResult';
import { Configuration } from "./app.configuration";
import {Observable} from "rxjs/Observable";

@Component({
    selector: 'score-app',
    templateUrl: 'app/app.component.html'
})
export class AppComponent implements OnInit {


    public ngOnInit(): any
    {
        this.scoreIhmTitle = this._configuration.title;
        this.timer3Sec = Observable.interval(3000);
        this.timer3Sec.subscribe(this.GetRank);
        this.timer3Sec.subscribe(this.GetAllScores);

    }

    public timer3Sec: Observable<number>;

    rankList: Rank[];
    @Output() scoreResultList: ScoreResult[];

    scoreIhmTitle: string;

    constructor(private _scoreDataService: ScoreDataService, private _configuration: Configuration) {

    }

    private GetRank = (): void => {
        this._scoreDataService
            .GetRank()
            .subscribe((response: Rank[]) => {
                    console.log("persons : " + JSON.stringify(response));
                    this.rankList = response;
                },
                error => console.log(error));
    }

    private GetAllScores = (): void => {
        this._scoreDataService
            .GetAllScores()
            .subscribe((response: ScoreResult[]) => {
                    this.scoreResultList = response;
                },
                error => console.log(error));
    }

}
