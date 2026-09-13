import { Injectable, signal } from '@angular/core';

/**
 * Serviço de controle global de carregamento e spinners assíncronos.
 *
 * Mantém uma contagem de requisições HTTP ativas e expõe o sinal reativo
 * `carregando` para que componentes de UI exibam loaders de forma centralizada.
 *
 * @author SIGEA-GTT Team
 */
@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  /** Contador interno de requisições em trânsito. */
  private requisicoesAtivas = 0;

  /** Sinal reativo que indica se há pelo menos uma requisição HTTP ativa. */
  readonly carregando = signal(false);

  /**
   * Incrementa o contador de requisições ativas e ativa o estado de loading.
   */
  mostrar(): void {
    this.requisicoesAtivas++;
    if (!this.carregando()) {
      this.carregando.set(true);
    }
  }

  /**
   * Decrementa o contador de requisições ativas e encerra o estado de loading quando zerar.
   */
  ocultar(): void {
    this.requisicoesAtivas = Math.max(0, this.requisicoesAtivas - 1);
    if (this.requisicoesAtivas === 0) {
      this.carregando.set(false);
    }
  }
}
