import { Component, OnInit, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterOutlet } from '@angular/router';
import { ToastContainerComponent } from './shared/components/toast-container/toast-container.component';
import { LoadingSpinnerComponent } from './shared/components/loading-spinner/loading-spinner.component';

/**
 * Componente raiz da aplicação SIGEA com inicialização silenciosa e aquecimento do backend.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ToastContainerComponent, LoadingSpinnerComponent],
  template: `
    <router-outlet></router-outlet>
    <app-toast-container></app-toast-container>
    <app-loading-spinner></app-loading-spinner>
  `,
})
export class AppComponent implements OnInit {
  title = 'SIGEA';
  private readonly http = inject(HttpClient, { optional: true });

  ngOnInit(): void {
    this.warmupBackend();
  }

  /**
   * Dispara um ping em segundo plano para acordar a instância do backend (Render cold start)
   * sem bloquear o usuário e sem exibir spinners ou erros visuais.
   */
  private warmupBackend(): void {
    if (this.http) {
      this.http
        .get('/api/public/ping', {
          headers: { 'X-Skip-Loading': 'true', 'X-Silent-Error': 'true' },
        })
        .subscribe({
          error: () => {
            // Falha silenciosa intencional no warmup
          },
        });
    }
  }
}
