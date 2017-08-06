import { transition, trigger, query, style, animate, stagger } from '@angular/animations';

export class TripAnimation {
        static buildTripLocation() {
        return [
            trigger('displayLocation', [
                transition('* => true', [
                    query('#loc', [style({ opacity: 0 })], { optional: true }),
                    query('#loc', [
                        stagger('.2s', animate('350ms ease-out', style({ opacity: 1})))
                    ], { optional: true })])
            ])
        ];
    }
}
