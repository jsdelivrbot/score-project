export class ScoreResult {
    public team: string;
    public maxPoints: number;
    public increment: number;
    public color: number[];
    public scores: Score[];
}

export class Score {
    public sprint: number;
    public points: number;
}

export class SprintTimer {
    public countdown: number;
    public started: boolean;
}