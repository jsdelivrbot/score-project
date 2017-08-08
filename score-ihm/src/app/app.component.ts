import { Component, OnInit, Output } from '@angular/core';
import { ScoreDataService } from './score/score.dataservice';
import { ScoreResult, Metric } from './score/score.model';
import { Configuration } from './app.configuration';
import { Observable } from 'rxjs/Observable';
import { SprintService } from './shared/sprint.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
     styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

    scoreIhmTitle: string;
    lastSprint = 0;
    constructor(
        private _configuration: Configuration,
        private _sprintService: SprintService) {

    }
    public ngOnInit(): any {
        this.scoreIhmTitle = this._configuration.title;
        this._sprintService.getLastSprint().subscribe(last => this.lastSprint = last);
    }

}
