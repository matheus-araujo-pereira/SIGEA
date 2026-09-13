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
      50: '#f7f8fc',
      100: '#f7f8fc',
      200: '#f7f8fc',
      300: '#bbdec6',
      400: '#5a7f78',
      500: '#010300',
      600: '#010300',
      700: '#010300',
      800: '#010300',
      900: '#010300',
      950: '#010300',
    },
    colorScheme: {
      light: {
        primary: {
          color: '#010300',
          inverseColor: '#f7f8fc',
          hoverColor: '#314c53',
          activeColor: '#010300',
        },
        surface: {
          0: '#f7f8fc',
          50: '#f7f8fc',
          100: '#f7f8fc',
          200: '#bbdec6',
          300: '#bbdec6',
          400: '#5a7f78',
          500: '#314c53',
          600: '#010300',
          700: '#010300',
          800: '#010300',
          900: '#010300',
          950: '#010300',
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
