
export class Trip {
    private id: string;
    private team: string;
    private messages: string[];
    private grid: Grid;

    constructor(id: string, team: string, messages: string[], grid: Grid) {
        this.id = id;
        this.team = team;
        this.messages = messages;
        this.grid = grid;
    }

    getId(): string {
        return this.id;
    }

    getTeam(): string {
        return this.team;
    }

    getMessages(): string[] {
        return this.messages;
    }

    getGrid(): Grid {
        return this.grid;
    }

    setId(id: string): void {
        this.id = id;
    }

    setTeam(team: string): void {
        this.team = team;
    }

    setMessages(messages: string[]): void {
        this.messages = messages;
    }

    setGrid(grid: Grid): void {
        this.grid = grid;
    }
}

export class Grid {
    private width: number;
    private height: number;
    private content: GridItem[];

    constructor(width: number, height: number, content: GridItem[]) {
        this.width = width;
        this.height = height;
        this.content = content;
    }

    getWidth(): number {
        return this.width;
    }

    getHeight(): number {
        return this.height;
    }

    getContent(): GridItem[] {
        return this.content;
    }

    setWidth(width: number): void {
        this.width = width;
    }

    SetHeight(height: number): void {
        this.height = height;
    }

    setContent(content: GridItem[]): void {
        this.content = content;
    }
}

export class GridItem {
    private object: string;
    private location: Location;

    constructor(object: string, location: Location) {
        this.object = object;
        this.location = location;
    }

    getObject(): string {
        return this.object;
    }

    getLocation(): Location {
        return this.location;
    }

    setObject(object: string): void {
        this.object = object;
    }

    setLocation(location: Location): void {
        this.location = location;
    }
}

export class Location {
    private posX: number;
    private posY: number;

    constructor(posX: number, posY: number) {
        this.posX = posX;
        this.posY = posY;
    }
    
    getPosX(): number {
        return this.posX;
    }

    getPosY(): number {
        return this.posY;
    }

    setPosX(posX: number): void {
        this.posX = posX;
    }
    setPosY(posY: number): void {
        this.posY = posY;
    }
}