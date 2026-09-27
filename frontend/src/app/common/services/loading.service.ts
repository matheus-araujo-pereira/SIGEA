import { Injectable, signal } from '@angular/core';

/**
 * Serviço reativo para controle do overlay global de carregamento via Angular Signals.
 *
 * Mantém um contador interno de requisições assíncronas em andamento para exibir
 * o indicador de progresso enquanto houver pelo menos uma operação pendente.
 */
@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  /**
   * Contador interno de requisições ativas concomitantes.
   */
  private activeRequests = 0;

  /**
   * Signal reativo indicando se há operações assíncronas em andamento.
   */
  readonly isLoading = signal<boolean>(false);

  /**
   * Incrementa o contador de requisições ativas e ativa o indicador visual de carregamento.
   *
   * @returns void
   */
  show(): void {
    this.activeRequests++;
    this.isLoading.set(true);
  }

  /**
   * Decrementa o contador de requisições ativas e desativa o indicador se zerado.
   *
   * @returns void
   */
  hide(): void {
    this.activeRequests = Math.max(0, this.activeRequests - 1);
    if (this.activeRequests === 0) {
      this.isLoading.set(false);
    }
  }

  /**
   * Força a redefinição emergencial do estado de carregamento para inativo.
   *
   * @returns void
   */
  reset(): void {
    this.activeRequests = 0;
    this.isLoading.set(false);
  }
}
