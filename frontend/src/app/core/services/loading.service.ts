import { Injectable, signal } from '@angular/core';

/**
 * Serviço reativo para controle do overlay global de carregamento via Signals.
 */
@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  private activeRequests = 0;

  /**
   * Signal indicando se há operações assíncronas em andamento.
   */
  readonly isLoading = signal<boolean>(false);

  /**
   * Incrementa o contador de requisições ativas e ativa o indicador visual.
   */
  show(): void {
    this.activeRequests++;
    this.isLoading.set(true);
  }

  /**
   * Decrementa o contador de requisições ativas e desativa o indicador se zerado.
   */
  hide(): void {
    this.activeRequests = Math.max(0, this.activeRequests - 1);
    if (this.activeRequests === 0) {
      this.isLoading.set(false);
    }
  }

  /**
   * Força a redefinição do estado de carregamento para inativo.
   */
  reset(): void {
    this.activeRequests = 0;
    this.isLoading.set(false);
  }
}
