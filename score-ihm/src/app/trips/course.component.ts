import { Observable } from 'rxjs/Observable';
import { TripAnimation } from './animations';
import { GridItem, Grid, Location } from '../trips/trip.model';
import { Component, OnInit, Input, ElementRef, ViewChild, Renderer2, AfterViewInit } from '@angular/core';

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
  @ViewChild('cell') gridCellElmt: ElementRef;

  private gridContainer: any;
  private gridCell: any;
  private viewBox = '0 0 100 100';
  graphicalItems: any[];
  graphicalLocations: any[];
  stars: any[];
  randomArray: any[];
  cellWidth = 20;
  cellHeight = 20;
  gridWidth = 0;
  gridHeight = 0;
  squareSide = 0;
  svgPath: string;
  courseVisible: string;

  constructor(private _renderer: Renderer2) {

  }

  ngOnInit() {
    this.graphicalItems = [];
    this.graphicalLocations = [];
    this.courseVisible = 'false';
    this.stars = [];
  }

  ngAfterViewInit() {
    if (this.grid !== undefined) {
      this.gridContainer = this.gridElmt.nativeElement;
      this.gridCell = this.gridCellElmt.nativeElement;
      // wait a tick to avoid one-time devMode
      // unidirectional-data-flow-violation erro
      setTimeout(_ => {
        this.calculateGridSize();
        this.placeObjectsOnGrid();
        this.placeSomeStars(100);
        this.drawCourseOnGrid();
        this.courseVisible = 'true';
      });
    }
  }

  private calculateGridSize(): void {
    this.gridWidth = (this.gridContainer.clientHeight - 2) / this.grid.getWidth();
    this.gridHeight = (this.gridContainer.clientHeight - 2) / this.grid.getHeight();
    this.squareSide = Math.round((this.gridWidth > this.gridHeight) ? this.gridHeight : this.gridWidth);
    this.svgPath = 'M ' + this.squareSide + ' 0 L 0 0 0 ' + this.squareSide;
  }

  private placeObjectsOnGrid(): void {
    this.graphicalItems = this.grid.getContent().map(it => this.addObjectGraphicalAttributes(it, this.squareSide));
  }

  private drawCourseOnGrid(): void {
    this.graphicalLocations = this.course.map(it => this.addCourseGraphicalAttributes(it, this.squareSide));
  }

  private addObjectGraphicalAttributes(item: GridItem, objectSize: number): any {
    return {
      object: item.getObject(),
      posX: (item.getLocation().getPosX() * this.squareSide) + (this.squareSide / 2),
      posY: (item.getLocation().getPosY() * this.squareSide) + (this.squareSide / 2),
      size: this.squareSide,
      ray: this.squareSide / 2,
      color: this.getColorClass(item.getObject())
    };
  }

  private addCourseGraphicalAttributes(location: Location, objectSize: number): any {
    return {
      posX: (location.getPosX() * this.squareSide) + (this.squareSide / 2),
      posY: (location.getPosY() * this.squareSide) + (this.squareSide / 2),
      size: this.squareSide,
      ray: this.squareSide / 4,
      color: this.getColorClass('L')
    };
  }

  private placeSomeStars(nb: number) {
    Observable.from(Array(nb)) //
              .forEach(elt =>
                this.stars.push({ posX: this.getRandomGridPos(), posY: this.getRandomGridPos(), size: 1, ray: 1, color: 'white' } ));
  }

  private getRandomGridPos(): number {
    return Math.floor(Math.random() * this.grid.getWidth() * this.squareSide) + 1;
  }

  private getColorClass(obj: string): string {
    let className = 'color-';
    switch (obj) {
      case 'B': {
        className = className + 'bomb';
        break;
      }
      case 'E': {
        className = className + 'enemy';
        break;
      }
      case 'A': {
        className = className + 'asteroid';
        break;
      }
      case 'M': {
        className = className + 'meteorite';
        break;
      }
      case 'L': {
        className = className + 'location';
        break;
      }
    }
    return className;
  }
}
