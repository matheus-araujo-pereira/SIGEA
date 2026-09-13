import { Injectable, signal, effect, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export type ModoTema = 'light' | 'dark';

/**
 * Serviço de gerenciamento de tema (Light / Dark Mode).
 * Controla a ativação da classe `.app-dark` e persiste a preferência no localStorage.
 */
@Injectable({
  providedIn: 'root',
})
export class TemaService {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly chaveStorage = 'sigea_tema';

  /** Sinal reativo que indica o modo do tema atual ('light' | 'dark'). */
  readonly modoAtual = signal<ModoTema>(this.detectarTemaInicial());

  constructor() {
    // Efeito para sincronizar a classe CSS .app-dark no elemento raiz <html>
    effect(() => {
      const modo = this.modoAtual();
      if (isPlatformBrowser(this.platformId)) {
        const root = document.documentElement;
        if (modo === 'dark') {
          root.classList.add('app-dark');
        } else {
          root.classList.remove('app-dark');
        }
        localStorage.setItem(this.chaveStorage, modo);
      }
    });
  }

  /**
   * Alterna entre o modo claro e o modo escuro.
   */
  alternarTema(): void {
    this.modoAtual.update((atual) => (atual === 'dark' ? 'light' : 'dark'));
  }

  /**
   * Define explicitamente o modo do tema.
   * @param modo 'light' ou 'dark'
   */
  definirTema(modo: ModoTema): void {
    this.modoAtual.set(modo);
  }

  /**
   * Indica se o modo escuro está atualmente ativo.
   */
  ehEscuro(): boolean {
    return this.modoAtual() === 'dark';
  }

  /**
   * Detecta a preferência inicial a partir do localStorage ou das preferências do sistema.
   */
  private detectarTemaInicial(): ModoTema {
    if (isPlatformBrowser(this.platformId)) {
      const salvo = localStorage.getItem(this.chaveStorage) as ModoTema | null;
      if (salvo === 'light' || salvo === 'dark') {
        return salvo;
      }
      if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        return 'dark';
      }
    }
    return 'light';
  }
}
