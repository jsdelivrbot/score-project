import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent }  from './app.component';
import { Configuration } from './app.configuration';
import { HttpModule, JsonpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { ScoreDataService } from './score/score.dataservice';

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
        JsonpModule,
        FormsModule
    ],

    declarations: [
        AppComponent
    ],

    providers: [
        Configuration,
        ScoreDataService
    ],

    bootstrap: [AppComponent]
})

export class AppModule { }
