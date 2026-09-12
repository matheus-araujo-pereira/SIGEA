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
      50: '#EFF6FF',
      100: '#DBEAFE',
      200: '#BFDBFE',
      300: '#93C5FD',
      400: '#60A5FA',
      500: '#3B82F6',
      600: '#2563EB',
      700: '#1D4ED8',
      800: '#1E40AF',
      900: '#1E3A8A',
      950: '#172554',
    },
    colorScheme: {
      light: {
        primary: {
          color: '#1E40AF',
          inverseColor: '#FFFFFF',
          hoverColor: '#1E3A8A',
          activeColor: '#1E3A8A',
        },
        surface: {
          0: '#FFFFFF',
          50: '#F8FAFC',
          100: '#F1F5F9',
          200: '#E2E8F0',
          300: '#CBD5E1',
          400: '#94A3B8',
          500: '#64748B',
          600: '#475569',
          700: '#334155',
          800: '#1E293B',
          900: '#0F172A',
          950: '#020617',
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
