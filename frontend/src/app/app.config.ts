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
      50: '#eff6f6',
      100: '#eff6f6',
      200: '#eff6f6',
      300: '#cddde0',
      400: '#a7bcca',
      500: '#467098',
      600: '#467098',
      700: '#467098',
      800: '#467098',
      900: '#467098',
      950: '#467098',
    },
    colorScheme: {
      light: {
        primary: {
          color: '#467098',
          inverseColor: '#eff6f6',
          hoverColor: '#7594ae',
          activeColor: '#467098',
        },
        surface: {
          0: '#eff6f6',
          50: '#eff6f6',
          100: '#eff6f6',
          200: '#cddde0',
          300: '#cddde0',
          400: '#a7bcca',
          500: '#7594ae',
          600: '#467098',
          700: '#467098',
          800: '#467098',
          900: '#467098',
          950: '#467098',
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
