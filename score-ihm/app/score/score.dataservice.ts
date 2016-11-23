import { Injectable } from '@angular/core';
import {Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { ScoreResult } from './ScoreResult';
import { Configuration } from '../app.configuration';

@Injectable()
export class ScoreDataService {

    private actionUrl: string;

    constructor(private _http: Http, private _configuration: Configuration) {
        this.actionUrl = _configuration.baseUrlLocal + 'api';
    }

    public GetAllScores = (): Observable<ScoreResult[]> => {
        return this._http.get(this.actionUrl + '/scores')
            .map((response: Response) => <ScoreResult[]>response.json())
            .catch(this.handleError);
    }

    private handleError(error: Response) {
        console.error(error);
        return Observable.throw(error.json().error || 'Server error');
    }

}
