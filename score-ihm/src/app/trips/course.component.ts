import { Observable } from 'rxjs/Observable';
import { TripAnimation } from './animations';
import { GridItem, Grid, Location } from '../trips/trip.model';
import { Component, OnInit, Input, ElementRef, ViewChild, Renderer2, AfterViewInit } from '@angular/core';
import { TweenLite } from 'gsap';

@Component({
  selector: 'app-course',
  templateUrl: './course.component.html',
  styleUrls: ['./course.component.css'],
  animations: [TripAnimation.buildTripLocation()]
})
export class CourseComponent implements AfterViewInit, OnInit {

  @Input() grid: Grid;
  @Input() course: Location[];
  @ViewChild('content') gridElmt: ElementRef;
  @ViewChild('ship') shipElmt: ElementRef;

  private gridContainer: any;
  private shipContainer: any;
  staticObjects: any[];
  shipPositions: any[];
  stars: any[];
  randomArray: any[];
  cellWidth = 20;
  cellHeight = 20;
  gridWidth = 0;
  gridHeight = 0;
  squareSize = 0;
  objectSize = 0;
  svgPath: string;
  staticObjectsSvgPath: string;
  shipPath: string;
  shipPos: string;
  shipSize: string;
  courseVisible: string;
  shipX = 0;
  shipY = 0;

  constructor(private _renderer: Renderer2) {

  }

  ngOnInit() {
    this.staticObjects = [];
    this.shipPositions = [];
    this.courseVisible = 'false';
    this.stars = [];
  }

  ngAfterViewInit() {
    if (this.grid !== undefined) {
      this.gridContainer = this.gridElmt.nativeElement;
      this.shipContainer = document.getElementById('ship');
      // wait a tick to avoid one-time devMode
      // unidirectional-data-flow-violation erro
      setTimeout(_ => {
        this.calculateGridSize();
        this.placeObjectsOnGrid();
        this.placeSomeStars(100);
        this.drawCourseOnGrid();
        this.courseVisible = 'true';
        this.startShipTrip();
      });
    }
  }

  private calculateGridSize(): void {
    this.gridWidth = (this.gridContainer.clientHeight - 2) / this.grid.getWidth();
    this.gridHeight = (this.gridContainer.clientHeight - 2) / this.grid.getHeight();
    this.squareSize = Math.round((this.gridWidth > this.gridHeight) ? this.gridHeight : this.gridWidth);
    this.objectSize = this.squareSize * 1.5;
    this.svgPath = 'M ' + this.squareSize + ' 0 L 0 0 0 ' + this.squareSize;
    this.staticObjectsSvgPath = 'M 0 0 H ' + this.objectSize + ' V 0 H '
      + this.objectSize + ' V ' + this.objectSize + ' H 0 V ' + this.objectSize + ' H 0 V 0';
  }

  private placeObjectsOnGrid(): void {
    this.staticObjects = this.grid.getContent()
      .map(it => this.addObjectGraphicalAttributes(it, this.squareSize));
  }

  private drawCourseOnGrid(): void {
    this.shipPositions = this.course
      .map(it => this.addCourseGraphicalAttributes(it, this.squareSize));
  }

  private addObjectGraphicalAttributes(item: GridItem, objectSize: number): any {
    return {
      object: item.getObject(),
      posX: (item.getLocation().getPosX() * this.squareSize) + (this.objectSize / 2),
      posY: (item.getLocation().getPosY() * this.squareSize) + (this.objectSize / 2),
      size: this.objectSize,
      url: this.getObjectUrl(item.getObject())
    };
  }

  private addCourseGraphicalAttributes(location: Location, objectSize: number): any {
    return {
      posX: (location.getPosX() * this.squareSize) + (this.squareSize / 2),
      posY: (location.getPosY() * this.squareSize) + (this.squareSize / 2),
      size: this.squareSize,
      ray: this.squareSize / 4,
      color: 'color-location'
    };
  }

  private placeSomeStars(nb: number) {
    Observable.from(Array(nb)) //
      .forEach(elt =>
        this.stars.push({ posX: this.getRandomGridPos(), posY: this.getRandomGridPos(), size: 1, ray: 1, color: 'white' }));
  }

  private getRandomGridPos(): number {
    return Math.floor(Math.random() * this.grid.getWidth() * this.squareSize) + 1;
  }

  private startShipTrip() {
    const subs = Observable
      .interval(100)
      .map(pos => pos + 1).subscribe(
      pos => {
        const posX = (this.course[pos].getPosX() * this.squareSize) + (this.squareSize / 2);
        const posY = (this.course[pos].getPosY() * this.squareSize) + (this.squareSize / 2);
        TweenLite.to(this.shipContainer, .2, { x: posX, y: posY });
        if (pos === (this.course.length - 1)) {
          subs.unsubscribe();
        }
      });
  }

  private getObjectUrl(obj: string): string {
    let objectUrl = 'url(';
    switch (obj) {
      case 'B': {
        objectUrl = objectUrl + '#mineimg';
        break;
      }
      case 'E': {
        objectUrl = objectUrl + '#enemyimg';
        break;
      }
      case 'A': {
        objectUrl = objectUrl + '#meteoriteimg';
        break;
      }
      case 'M': {
        objectUrl = objectUrl + '#meteoriteimg';
        break;
      }
    }
    return objectUrl + ')';
  }
}
