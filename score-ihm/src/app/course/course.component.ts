import { GridItem, Grid } from '../trips/trip.model';
import { Component, OnInit, Input, ElementRef, ViewChild, Renderer2, AfterViewInit } from '@angular/core';

@Component({
  selector: 'app-course',
  templateUrl: './course.component.html',
  styleUrls: ['./course.component.css']
})
export class CourseComponent implements AfterViewInit, OnInit {

  @Input() grid: Grid;
  @ViewChild('content') gridElmt: ElementRef;
  @ViewChild('cell') gridCellElmt: ElementRef;

  private gridContainer: any;
  private gridCell: any;
  private viewBox = '0 0 100 100';
  graphicalItems: any[];
  cellWidth = 20;
  cellHeight = 20;
  gridWidth = 0;
  gridHeight = 0;
  squareSide = 0;
  svgPath: string;

  constructor(private _renderer: Renderer2) { }

  ngOnInit() {
    this.graphicalItems = [];
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
      });
    }
  }

  private calculateGridSize(): void {
    this.gridWidth = (this.gridContainer.clientWidth - 2) / this.grid.getWidth(); // this.cellWidth;
    this.gridHeight = (this.gridContainer.clientHeight - 2) / this.grid.getHeight(); // this.cellHeight;
    this.squareSide = Math.round((this.gridWidth > this.gridHeight) ? this.gridHeight : this.gridWidth);
    this.svgPath = 'M ' + this.squareSide + ' 0 L 0 0 0 ' + this.squareSide;
  }

  private placeObjectsOnGrid(): void {
    this.graphicalItems = this.grid.getContent().map(it => this.addGraphicalAttributes(it, this.squareSide));
  }

  private addGraphicalAttributes(item: GridItem, objectSize: number): any {
    return {
      object: item.getObject(),
      posX: (item.getLocation().getPosX() * this.squareSide) + (this.squareSide / 2),
      posY: (item.getLocation().getPosY() * this.squareSide) + (this.squareSide / 2),
      size: this.squareSide,
      ray: this.squareSide / 2,
      color: this.getColorClass(item.getObject())
    };
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
    }
    return className;
  }
}
