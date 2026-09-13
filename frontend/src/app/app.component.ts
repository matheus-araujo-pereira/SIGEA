import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { LoadingService } from './nucleo/servicos/loading.service';

import { Toast } from 'primeng/toast';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ProgressSpinner } from 'primeng/progressspinner';

/**
 * Componente raiz da aplicação SIGEA-GTT.
 *
 * Responsável por inicializar o tema visual no modo claro (removendo classes de dark-mode
 * que possam ter sido persistidas em sessões anteriores) e por renderizar os overlays globais
 * de feedback ao usuário: spinner de carregamento, toasts e diálogos de confirmação.
 */
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toast, ConfirmDialog, ProgressSpinner],
  templateUrl: './app.component.html',
})
export class AppComponent {
  readonly loadingService = inject(LoadingService);

  constructor() {
    if (typeof document !== 'undefined') {
      document.documentElement.classList.remove('app-dark', 'dark');
      document.body?.classList.remove('app-dark', 'dark');
      try {
        localStorage.removeItem('sigea_tema');
        localStorage.removeItem('theme');
        localStorage.removeItem('p-theme');
      } catch {
        // Ignora caso storage esteja indisponível
      }
    }
  }
}
