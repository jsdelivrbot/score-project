import {Component, OnInit} from '@angular/core';
import { ScoreDataService } from './score/score.dataservice';
import { Rank } from './score/ScoreResult';

@Component({
    selector: 'score-app',
    templateUrl: 'app/app.component.html'
})
export class AppComponent implements OnInit {

    public ngOnInit(): any
    {
        this.GetRank();
    }

    rankList: Rank[];

    constructor(private _scoreDataService: ScoreDataService) {

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

}
