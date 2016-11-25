import {Injectable} from '@angular/core';
import {Http, Response, Headers, Request, RequestMethod, ResponseOptionsArgs} from '@angular/http';
import {Observable} from 'rxjs/Rx';
import {Configuration} from '../app.configuration';
import {RequestArgs} from "@angular/http/src/interfaces";

@Injectable()
export class JenkinsLauncher {

    private actionUrl: string;

    constructor(private _http: Http, private _configuration: Configuration) {
        this.actionUrl = _configuration.jenkinsUrl + '/job/' + _configuration.jenkinsJobName + '/build?token=' + _configuration.jenkinsJobToken;
    }

    public LaunchJob = (): Observable<Response> => {


        var headers: Headers = new Headers();
        headers.append('Access-Control-Allow-Origin', '*');

        return this._http.post(this.actionUrl, headers).catch(this.handleError);
    }

    private handleError(error: Response) {
        console.error(error);
        return Observable.throw(error.json().error || 'Server error');
    }

}
