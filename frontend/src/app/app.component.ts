import { Component, OnInit, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouterOutlet } from '@angular/router';
import { ToastContainerComponent } from './common/components/toast-container/toast-container.component';
import { LoadingSpinnerComponent } from './common/components/loading-spinner/loading-spinner.component';

/**
 * Componente raiz da aplicação SIGEA com inicialização silenciosa e aquecimento do backend.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, ToastContainerComponent, LoadingSpinnerComponent],
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  /** Título oficial do sistema */
  title = 'SIGEA';
  /** Cliente HTTP opcional para requisição silenciosa de aquecimento */
  private readonly http = inject(HttpClient, { optional: true });

  /** Inicialização da aplicação com aquecimento assíncrono do backend */
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
