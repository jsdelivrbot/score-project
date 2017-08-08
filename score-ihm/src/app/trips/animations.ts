import { transition, trigger, query, style, animate, stagger, group } from '@angular/animations';

export class TripAnimation {
        static buildTripLocation() {
        return [
            trigger('displayLocation', [
                transition('* => true', [
                    query('#loc', [style({ opacity: 0 })], { optional: true }),
                    query('#loc',
                        group([
                            stagger('.1s', group([
                                animate('150ms ease-out', style({ opacity: 1})),
                                animate('5s ease-out', style({ opacity: 0})),
                                ]),
                            ),
                            query('#ship', [style({ transform: 'translate(100,100)' })], { optional: true }),
                ]), { optional: true })])
            ])
        ];
    }
}
