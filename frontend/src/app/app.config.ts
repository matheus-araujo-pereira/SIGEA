import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import { definePreset } from '@primeng/themes';
import Aura from '@primeng/themes/aura';
import { MessageService, ConfirmationService } from 'primeng/api';
import { routes } from './app.routes';
import { loadingInterceptor } from './nucleo/interceptores/loading.interceptor';
import { apiUrlInterceptor } from './nucleo/interceptores/api-url.interceptor';

const SigeaPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '#EFFAFD',
      100: '#EFFAFD',
      200: '#EFFAFD',
      300: '#4A8BDF',
      400: '#4A8BDF',
      500: '#4A8BDF',
      600: '#4A8BDF',
      700: '#4A8BDF',
      800: '#4A8BDF',
      900: '#A0006D',
      950: '#A0006D',
    },
    colorScheme: {
      light: {
        primary: {
          color: '#4A8BDF',
          inverseColor: '#FFFFFF',
          hoverColor: '#A0006D',
          activeColor: '#A0006D',
        },
        surface: {
          0: '#FFFFFF',
          50: '#EFFAFD',
          100: '#EFFAFD',
          200: '#EFFAFD',
          300: '#4A8BDF',
          400: '#4A8BDF',
          500: '#4A8BDF',
          600: '#A0006D',
          700: '#A0006D',
          800: '#A0006D',
          900: '#A0006D',
          950: '#A0006D',
        },
      },
    },
  },
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(withFetch(), withInterceptors([apiUrlInterceptor, loadingInterceptor])),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: SigeaPreset,
        options: {
          darkModeSelector: false,
        },
      },
      ripple: false,
    }),
    MessageService,
    ConfirmationService,
  ],
};
