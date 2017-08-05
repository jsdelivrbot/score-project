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
  private viewBox: string = '0 0 100 100';
  graphicalTtems: any[];
  cellWidth: number = 20;
  cellHeight: number = 20;
  gridWidth: number = 0;
  gridHeight: number = 0;
  squareSide: number = 0;
  svgPath: string;

  constructor(private _renderer: Renderer2) { }

  ngOnInit() {
    this.graphicalTtems = this.grid.getContent()
  }

  ngAfterViewInit() {
    if (this.grid !== undefined) {
      this.gridContainer = this.gridElmt.nativeElement;
      this.gridCell = this.gridCellElmt.nativeElement;
      // wait a tick to avoid one-time devMode
      // unidirectional-data-flow-violation erro
      setTimeout(_ => {
        this.calculateGridSize();
        this.placeObjectsOnGrid()
      });
    }
  }

  private calculateGridSize(): void {
    this.gridWidth = this.gridContainer.clientWidth / this.grid.getWidth(); //* this.cellWidth;
    this.gridHeight = this.gridContainer.clientHeight / this.grid.getHeight(); //* this.cellHeight;
    this.squareSide = Math.round((this.gridWidth > this.gridHeight) ? this.gridHeight : this.gridWidth);
    this.svgPath = 'M ' + this.squareSide + ' 0 L 0 0 0 ' + this.squareSide;
  }

  private placeObjectsOnGrid(): void {
    this.graphicalTtems.map(it => this.addGraphicalAttributes(it));
  }

  private addGraphicalAttributes(item: GridItem): any {
    return {
        object: item.getObject(),
        posX: item.getLocation().getPosX(),
        posY: item.getLocation().getPosY(),
        size: this.squareSide
      };
  }
}
