import { Injectable } from '@angular/core';

@Injectable()
export class Configuration {
    title: string = "SOFT - Battle Code 2016";
    sprintTimelaps:  number = 20;

    /// A externaliser dans un json, avec un json par défaut, pour un deploiement plus easy
    /// see https://medium.com/@hasan.hameed/reading-configuration-files-in-angular-2-9d18b7a6aa4#.byxyfdx09
    jenkinsUrl: string = "http://localhost:8888";
    jenkinsJobName: string = "TEST";
    jenkinsJobToken: string = "TOKEN-JOB";
}
