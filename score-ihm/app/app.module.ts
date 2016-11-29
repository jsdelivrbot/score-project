import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppComponent}  from './app.component';
import {Configuration} from './app.configuration';
import {HttpModule, JsonpModule} from '@angular/http';
import {FormsModule} from '@angular/forms';
import {ScoreDataService} from './score/score.dataservice';
import {ScoreChartComponent} from "./score/scorechart.component";
import {ChartsModule} from "./charts/chart.module";
import {SprintTimerComponent} from "./score/sprinttimer.component";

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
        JsonpModule,
        FormsModule,
        ChartsModule
    ],

    declarations: [
        AppComponent,
        ScoreChartComponent,
        SprintTimerComponent
    ],

    providers: [
        Configuration,
        ScoreDataService
    ],

    bootstrap: [AppComponent]
})

export class AppModule {
}
