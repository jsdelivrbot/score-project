import {HttpClientModule, HttpClient} from '@angular/common/http';
import { TripService } from './trips/trip.service';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { ScoreDataService } from './score/score.dataservice';
import { SprintTimerComponent } from './score/sprinttimer.component';
import { ScoreChartComponent } from './score/scorechart.component';
import { HttpModule } from '@angular/http';
import { Configuration } from './app.configuration';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ChartsModule } from './chart/chart.module';
import { SprintService } from './shared/sprint.service';
import { RouterModule, Routes } from '@angular/router';
import { TripsComponent } from './trips/trips.component';
import { MessagesComponent } from './trips/messages.component';
import { CourseComponent } from './trips/course.component';

const appRoutes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  { path: 'trips',      component: TripsComponent },
  { path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
];


@NgModule({
  declarations: [
    AppComponent,
    SprintTimerComponent,
    ScoreChartComponent,
    DashboardComponent,
    TripsComponent,
    MessagesComponent,
    CourseComponent
  ],
  imports: [
    BrowserModule,
    ChartsModule,
    HttpModule,
    HttpClientModule,
    RouterModule.forRoot(appRoutes),
  ],
  providers: [
    ScoreDataService,
    Configuration,
    SprintService,
    TripService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
