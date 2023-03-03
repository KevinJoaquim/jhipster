import { Component } from '@angular/core';
import { VERSION } from 'app/app.constants';

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.component.html',
})
export class FooterComponent {
  version = '';

  constructor() {
    if (VERSION) {
      this.version = VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`;
    }
  }
}
