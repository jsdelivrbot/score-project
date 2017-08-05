export class ScoreResult {
    public team: string;
    public maxPoints: number;
    public increment: number;
    public color: number[];
    public scores: Score[];

    public buildStatus: string;
    public buildStatusColor: string;
    public testsStatus: string;
    public testsStatusColor: string;
    public coverageStatus: string;
    public coverageStatusColor: string;
    public complexityStatus: number;
    public complexityStatusColor: string;
    public validationStatus: string;
    public validationStatusColor: string;
}

export class Metric {
    public team: string;
    public metrics: Object;
}

export class Score {
    public sprint: number;
    public points: number;
}

export class SprintTimer {
    public countdown: number;
    public started: boolean;
}
