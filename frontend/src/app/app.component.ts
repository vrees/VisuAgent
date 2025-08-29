import { Component } from '@angular/core';

@Component({
    selector: 'app-root',
    template: `<app-dashboard></app-dashboard>`,
    styles: [`
      :host {
        display: block;
        width: 100%;
        height: 100vh;
      }
    `],
    standalone: false
})
export class AppComponent {
}
