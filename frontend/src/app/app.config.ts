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
import { erroInterceptor } from './nucleo/interceptores/erro.interceptor';

/**
 * Preset de tema PrimeNG personalizado para o SIGEA-GTT.
 *
 * Substitui a paleta de cores primárias do tema Aura pela escala Teal do Tailwind,
 * transmitindo identidade visual institucional clínica. O dark mode é desativado por padrão.
 */
const ClinicalAura = definePreset(Aura, {
  semantic: {
    primary: {
      50: '{teal.50}',
      100: '{teal.100}',
      200: '{teal.200}',
      300: '{teal.300}',
      400: '{teal.400}',
      500: '{teal.500}',
      600: '{teal.600}',
      700: '{teal.700}',
      800: '{teal.800}',
      900: '{teal.900}',
      950: '{teal.950}',
    },
  },
});

/**
 * Configuração raiz da aplicação Angular (Standalone API).
 *
 * Registra os providers globais: roteamento com input binding, cliente HTTP com interceptors
 * em cadeia (URL da API → loading → erros globais), animações assíncronas, tema PrimeNG
 * com preset clínico e serviços de mensagens e confirmação.
 */
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(
      withFetch(),
      withInterceptors([apiUrlInterceptor, loadingInterceptor, erroInterceptor]),
    ),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: ClinicalAura,
        options: {
          darkModeSelector: 'none',
          cssLayer: {
            name: 'primeng',
            order: 'tailwind-base, primeng, tailwind-utilities',
          },
        },
      },
      ripple: true,
    }),
    MessageService,
    ConfirmationService,
  ],
};
