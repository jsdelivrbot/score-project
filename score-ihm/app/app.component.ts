import {Component, Input, OnInit, Output} from '@angular/core';
import { ScoreDataService } from './score/score.dataservice';
import {ScoreResult} from './score/ScoreResult';

@Component({
    selector: 'score-app',
    templateUrl: 'app/app.component.html'
})
export class AppComponent implements OnInit {

    public ngOnInit(): any
    {
        this.GetAllScores();
    }

    scoresResult: ScoreResult[];

    constructor(private _scoreDataService: ScoreDataService) {

    }

    private GetAllScores = (): void => {
        this._scoreDataService
            .GetAllScores()
            .subscribe((response: ScoreResult[]) => {
                    console.log("persons : " + JSON.stringify(response));
                    this.scoresResult = response;
                },
                error => console.log(error));
    }

}
