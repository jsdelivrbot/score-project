export class ScoreResult {
    public team: string;
    public scores: Score[];
}

export class Score {
    public iteration: number;
    public points: number;
}

export class Rank {
    public team: string;
    public points: number;
}