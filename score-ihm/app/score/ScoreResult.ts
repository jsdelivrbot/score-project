export class ScoreResult {
    public team: string;
    public scores: Score[];
}

export class Score {
    public sprint: Date;
    public points: number;
}

export class Rank {
    public team: string;
    public points: number;
}