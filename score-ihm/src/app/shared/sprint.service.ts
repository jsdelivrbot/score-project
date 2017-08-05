import { Injectable } from '@angular/core';
import { Subject } from "rxjs/Subject";
import { Observable } from "rxjs/Observable";

@Injectable()
export class SprintService {

  lastSprint: Subject<number> = new Subject<number>();
  
  constructor() { 
    this.lastSprint.next(0);
  }

  getLastSprint(): Observable<number> {
    return this.lastSprint.asObservable();
  }

  setLastSprint(value: number): void {
    this.lastSprint.next(value);
  }

}
