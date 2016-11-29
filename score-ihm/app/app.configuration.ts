import { Injectable } from '@angular/core';

@Injectable()
export class Configuration {
    scoreRestApiUrl: string = "http://localhost:8080/";
    title: string = "SOFT - Battle Code 2016";
}
