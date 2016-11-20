import { Injectable } from '@angular/core';
import {Http, Response, RequestOptionsArgs, Headers} from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { ScoreResult, Rank } from './ScoreResult';
import { Configuration } from '../app.configuration';

@Injectable()
export class ScoreDataService {

    private actionUrl: string;

    constructor(private _http: Http, private _configuration: Configuration) {
        this.actionUrl = _configuration.baseUrlLocal + 'api';
    }

    public GetRank = (): Observable<Rank[]> => {
        return this._http.get(this.actionUrl + '/rank')
            .map((response: Response) => <Rank[]>response.json())
            .catch(this.handleError);
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
