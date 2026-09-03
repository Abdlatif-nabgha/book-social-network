import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideIcons } from '@ng-icons/core';
import { heroBookOpenSolid, heroUsersSolid, heroArrowRightSolid } from '@ng-icons/heroicons/solid';

import { routes } from './app.routes';
import { httpTokenInterceptor } from './services/interceptor/http-token.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([httpTokenInterceptor])),
    provideIcons({ heroBookOpenSolid, heroUsersSolid, heroArrowRightSolid })
  ]
};
