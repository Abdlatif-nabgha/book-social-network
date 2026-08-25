import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideIcons } from '@ng-icons/core';
import { heroBookOpenSolid, heroUsersSolid, heroArrowRightSolid } from '@ng-icons/heroicons/solid';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideIcons({ heroBookOpenSolid, heroUsersSolid, heroArrowRightSolid })
  ]
};
