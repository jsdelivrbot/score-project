import {Component, OnInit, Output} from '@angular/core';
import {ScoreDataService} from './score/score.dataservice';
import {ScoreResult, Metric} from './score/ScoreResult';
import {Configuration} from "./app.configuration";
import {Observable} from "rxjs/Observable";

@Component({
    selector: 'score-app',
    templateUrl: 'app/app.component.html'
})
export class AppComponent implements OnInit {

    public ngOnInit(): any {
        this.scoreIhmTitle = this._configuration.title;
        this.RefreshScoresAndMetrics()
        Observable.interval(3000).subscribe(this.RefreshScoresAndMetrics);
    }

    public scoreChartColors: Array<number[]> = [
        [255, 99, 132],
        [54, 162, 235],
        [255, 206, 86],
        [231, 233, 237],
        [75, 192, 192],
        [151, 187, 205],
        [220, 220, 220],
        [247, 70, 74],
        [70, 191, 189],
        [253, 180, 92],
        [148, 159, 177],
        [242, 9, 21],
        [255, 182, 0],
        [255, 247, 30],
        [83, 255, 69],
        [214, 40, 40],
        [247, 127, 0],
        [252, 191, 73],
        [29, 172, 214],
        [77, 83, 96]
    ];

    teamColorCache = {};

    @Output() scoreResultList: ScoreResult[] = [];
    metrics: Metric[] = [];

    lastSprint: number = 0;

    scoreIhmTitle: string;

    constructor(private _scoreDataService: ScoreDataService, private _configuration: Configuration) {

    }

    private GenerateTeamColors = (scoreResults: ScoreResult[]): ScoreResult[] => {
        for (let i = 0; i < scoreResults.length; i++) {
            scoreResults[i].color = this.teamColorCache[scoreResults[i].team] || this.scoreChartColors[i] || this.GetRandomColor();
            scoreResults[i].maxPoints = scoreResults[i].scores[scoreResults[i].scores.length - 1].points;
            scoreResults[i].increment = scoreResults[i].maxPoints - scoreResults[i].scores[scoreResults[i].scores.length - 2].points
            this.teamColorCache[scoreResults[i].team] = scoreResults[i].color;
        }

        return scoreResults.sort((score1, score2) => score2.maxPoints - score1.maxPoints);
    }

    private BindMetricsToScoreResult = (scoreResults: ScoreResult[], metricsList: Metric[]) => {
        let metricsByTeam = new Map<string, Map<string,string>>()
        for (let i = 0; i < metricsList.length; i++) {
            let metrics = new Map<string,string>()
            for(var key in metricsList[i].metrics) {
                metrics.set(key, metricsList[i].metrics[key])
            }
            metricsByTeam.set(metricsList[i].team.substring("metrics-".length), metrics)
        }

        for (let i = 0; i < scoreResults.length; i++) {
            this.ResetMetricsInScoreResult(scoreResults[i])
            let teamMetrics = metricsByTeam.get(scoreResults[i].team)
            if(teamMetrics != null) {
                this.ComputeBuildStatus(teamMetrics, scoreResults[i])
                this.ComputeTestResultStatus(teamMetrics, scoreResults[i])
                this.ComputeCoverageStatus(teamMetrics, scoreResults[i])
                this.ComputeValidationStatus(teamMetrics, scoreResults[i])
            }
        }
    }

    private ResetMetricsInScoreResult = (scoreResult: ScoreResult) => {
        scoreResult.buildStatus = "en attente"
        scoreResult.buildStatusColor = "lightgrey"
        scoreResult.testsStatus = null
        scoreResult.testsStatusColor = null
        scoreResult.coverageStatus = null
        scoreResult.coverageStatusColor = null
        scoreResult.validationStatus = null
        scoreResult.validationStatusColor = null
    }

    private ComputeBuildStatus = (teamMetrics: Map<string,string>, scoreResult: ScoreResult) => {
        if(teamMetrics.get("build") == null) {
            return
        }

        let build = teamMetrics.get("build")
        if(build == "pass") {
            scoreResult.buildStatus = "succès"
            scoreResult.buildStatusColor = "green"
        } else if(build == "fail") {
            scoreResult.buildStatus = "crash"
            scoreResult.buildStatusColor = "red"
        } else {
            scoreResult.buildStatusColor = "orange"
               if(build == "building") {
                   scoreResult.buildStatus = "embarquement"
               } else if(build == "validating") {
                   scoreResult.buildStatus = "décollage"
               } else {
                   scoreResult.buildStatus = build
               }
        }
    }

    private ComputeCoverageStatus = (teamMetrics: Map<string,string>, scoreResult: ScoreResult) => {
        if(teamMetrics.get("branchCoverage") == null) {
            return
        }

        let coverage = Math.round( +teamMetrics.get("branchCoverage")*100 )
        scoreResult.coverageStatus = coverage + "%"
        if(coverage > 80) {
            scoreResult.coverageStatusColor = "green"
        } else if(coverage > 40) {
            scoreResult.coverageStatusColor = "orange"
        } else {
            scoreResult.coverageStatusColor = "red"
        }
    }

    private ComputeTestResultStatus = (teamMetrics: Map<string,string>, scoreResult: ScoreResult) => {
        if(teamMetrics.get("numberOfTests") == null || teamMetrics.get("numberOfFailures") == null || teamMetrics.get("numberOfSkippedTests") == null) {
            return
        }

        let numberOfTests = +teamMetrics.get("numberOfTests")
        let numberOfFailures = +teamMetrics.get("numberOfFailures") + +teamMetrics.get("numberOfSkippedTests")
        if(numberOfTests == 0) {
            scoreResult.testsStatus = "no tests"
            scoreResult.testsStatusColor = "red"
        } else if(numberOfFailures == 0) {
            scoreResult.testsStatus = "pass"
            scoreResult.testsStatusColor = "green"
        } else {
            let percent = Math.round( 100 * (numberOfTests - numberOfFailures) / numberOfTests)
            scoreResult.testsStatus = percent + "%"
            if(percent > 60) {
                scoreResult.testsStatusColor = "orange"
            } else {
                scoreResult.testsStatusColor = "red"
            }
        }
    }

    private ComputeValidationStatus = (teamMetrics: Map<string,string>, scoreResult: ScoreResult) => {
        if(teamMetrics.get("validation") == null) {
            return
        }

        scoreResult.validationStatus = teamMetrics.get("validation")
        if(scoreResult.validationStatus.substring(0, 1) == "0") {
            scoreResult.validationStatusColor = "red"
        } else {
            scoreResult.validationStatusColor = "green"
        }
    }

    private GetRandomColor = (): number[] => {
        return [this.GetRandomInt(0, 255), this.GetRandomInt(0, 255), this.GetRandomInt(0, 255)];
    }

    private GetRandomInt = (min: number, max: number): number => {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    private GetAllScores = () => {
        this._scoreDataService
            .GetAllScores()
            .subscribe((response: ScoreResult[]) => {
                    this.scoreResultList = this.GenerateTeamColors(response);
                    this.BindMetricsToScoreResult(this.scoreResultList, this.metrics)
                    this.updateLastSprint();
                },
                error => console.log(error));
    }

    private GetMetrics = () => {
        this._scoreDataService
            .GetMetrics()
            .subscribe((response: Metric[]) => {
                    this.metrics = response
                    this.BindMetricsToScoreResult(this.scoreResultList, this.metrics)
                },
                error => console.log(error));
    }

    private RefreshScoresAndMetrics = () => {
        this.GetAllScores()
        this.GetMetrics()
    }

    private updateLastSprint = (): void => {
        if (this.scoreResultList.length > 0) {
            this.lastSprint = this.scoreResultList[0].scores[this.scoreResultList[0].scores.length - 1].sprint;
        }
    }

}
