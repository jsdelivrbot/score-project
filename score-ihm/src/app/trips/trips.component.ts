import { TripService } from './trip.service';
import { Component, OnInit } from '@angular/core';
import {GridItem, Grid,  Trip, Location } from './trip.model';

@Component({
  selector: 'app-trips',
  templateUrl: './trips.component.html',
  styleUrls: ['./trips.component.css']
})
export class TripsComponent implements OnInit {

  trips: Trip[];
  trip: Trip;

  constructor(private tripService: TripService) { 
    this.tripService.getTrips().subscribe(data => {
      this.trips = data.map(elmt => this.buildTrip(elmt));
      this.trip = this.trips[0];
    });
  }

  ngOnInit() {

  }

  private buildTrip(jsonElmt: any) {
    return  new Trip(jsonElmt.id, jsonElmt.team, jsonElmt.messages, this.buildGrid(jsonElmt.grid));
  }

  private buildGrid(jsonElmt: any) {
    return  new Grid(jsonElmt.width, jsonElmt.height, this.buildItems(jsonElmt.content));
  }

  private buildItems(jsonElmt: any[]) {
    return jsonElmt.map(elt => new GridItem(elt.object, new Location(elt.location.posX, elt.location.posY) ));
  }

}
