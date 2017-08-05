import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Trip } from './trip.model';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class TripService {

  private baseUrl: string;
  
  constructor(private _httpClient: HttpClient) { 
    this.baseUrl = environment.backend + '/api';
  }

  getTrips(): Observable<any> {
    return this._httpClient.get(this.baseUrl + '/trip');
  }

}
