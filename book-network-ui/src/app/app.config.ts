import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideToastr } from 'ngx-toastr';
import { provideIcons } from '@ng-icons/core';
import { heroBookOpenSolid, heroUsersSolid, heroArrowRightSolid } from '@ng-icons/heroicons/solid';

import { routes } from './app.routes';
import { httpTokenInterceptor } from './services/interceptor/http-token.interceptor';
import { provideApiConfiguration } from './services/api-configuration';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([httpTokenInterceptor])),
    provideAnimationsAsync(),
    provideToastr({
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      closeButton: true,
      progressBar: false,
      easeTime: 0,
      tapToDismiss: true
    }),
    provideIcons({ heroBookOpenSolid, heroUsersSolid, heroArrowRightSolid }),
    provideApiConfiguration('/api/v1')
  ]
};
