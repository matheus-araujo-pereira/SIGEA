import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../../services/loading.service';

/**
 * Componente visual ergonômico de overlay global para carregamento assíncrono.
 *
 * Utiliza Angular Signals para escutar o estado de `LoadingService` e renderizar
 * um spinner com efeito glassmorphism (backdrop-blur) e semiótica hospitalar.
 */
@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading-spinner.component.html',
})
export class LoadingSpinnerComponent {
  /**
   * Instância injetada do serviço reativo de carregamento.
   */
  readonly loadingService = inject(LoadingService);
}
