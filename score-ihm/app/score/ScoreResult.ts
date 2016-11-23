export class ScoreResult {
    public team: string;
    public maxPoints: number;
    public color: number[];
    public scores: Score[];
}

export class Score {
    public sprint: number;
    public points: number;
}