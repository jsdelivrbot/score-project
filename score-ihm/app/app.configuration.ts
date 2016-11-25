import { Injectable } from '@angular/core';

@Injectable()
export class Configuration {
    scoreRestApiUrl: string = "http://localhost:8080/";

    title: string = "SOFT - Battle Code 2016";

    sprintTimelaps:  number = 20;

    jenkinsUrl: string = "http://localhost:8888";
    jenkinsJobName: string = "TEST";
    jenkinsJobToken: string = "TOKEN-JOB";
}