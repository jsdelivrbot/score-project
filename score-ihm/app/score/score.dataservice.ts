import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import {ScoreResult, SprintTimer, Metric} from './ScoreResult';
import {Configuration} from '../app.configuration';

@Injectable()
export class ScoreDataService {

    private actionUrl: string;

    constructor(private _http: Http, private _configuration: Configuration) {
        this.actionUrl = '/api';
    }

    public GetAllScores = (): Observable<ScoreResult[]> => {
        return this._http.get(this.actionUrl + '/scores')
            .map((response: Response) => <ScoreResult[]>response.json())
            .catch(this.handleError);
    }


    public GetMetrics = (): Observable<Metric[]> => {
        return this._http.get(this.actionUrl + '/metrics')
            .map((response: Response) => <Metric[]>response.json())
            .catch(this.handleError);
    }


    public StartSprintTimer = (): Observable<SprintTimer> => {
        return this._http.post(this.actionUrl + '/sprint/start', {})
            .map((response: Response) => <SprintTimer>response.json())
            .catch(this.handleError);
    }


    public PauseSprintTimer = (): Observable<SprintTimer> => {
        return this._http.put(this.actionUrl + '/sprint/pause',{})
            .map((response: Response) => <SprintTimer>response.json())
            .catch(this.handleError);
    }


    public SynchroSprintTimer = (): Observable<SprintTimer> => {
        return this._http.get(this.actionUrl + '/sprint')
            .map((response: Response) => <SprintTimer>response.json())
            .catch(this.handleError);
    }

    private handleError(error: Response) {
        console.error(error);
        return Observable.throw(error.json().error || 'Server error');
    }

}
