import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivityDetailDTO } from '../../models/activity.model';

/**
 * Componente que renderiza a barra superior de navegação, dados da atividade e cronômetro metodológico IHI (20 min).
 */
@Component({
  selector: 'app-resolution-header',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white rounded-2xl border border-slate-200/80 p-4 shadow-xs flex flex-col md:flex-row items-center justify-between gap-4 sticky top-4 z-30">
      <div class="flex items-center gap-3">
        <button
          type="button"
          (click)="goBack.emit()"
          class="px-3 py-1.5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 font-semibold text-xs transition-colors shadow-xs"
        >
          Voltar
        </button>
        <div>
          <h2 class="text-sm font-black text-slate-900 truncate max-w-md">
            {{ activity?.title || 'Resolução de Atividade' }}
          </h2>
          <p class="text-[11px] text-slate-500 font-medium">
            {{ activity?.className }} • Prazo: {{ activity?.deadline | date:'dd/MM/yyyy HH:mm' }}
          </p>
        </div>
      </div>

      <!-- Cronômetro IHI de 20 minutos (1.200 segundos) -->
      <div class="flex items-center gap-3">
        <div
          class="px-4 py-2 rounded-2xl border flex items-center gap-2 font-mono transition-colors shadow-2xs"
          [ngClass]="{
            'bg-rose-50 border-rose-200 text-rose-700 animate-pulse': timerSeconds < 300,
            'bg-slate-50 border-slate-200 text-slate-800': timerSeconds >= 300
          }"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span class="text-base font-black">{{ formattedTime }}</span>
          <span class="text-[10px] font-sans font-bold uppercase text-slate-400">Tempo IHI</span>
        </div>

        <button
          type="button"
          (click)="toggleTimer.emit()"
          class="px-3 py-2 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 font-semibold text-xs transition-colors shadow-xs"
          [title]="isTimerPaused ? 'Retomar cronômetro' : 'Pausar cronômetro'"
        >
          {{ isTimerPaused ? 'Retomar' : 'Pausar' }}
        </button>

        <button
          type="button"
          (click)="submit.emit()"
          class="px-4 py-2 rounded-xl bg-clinical-600 hover:bg-clinical-700 text-white font-bold text-xs shadow-sm transition-all"
        >
          Submeter Resposta
        </button>
      </div>
    </div>
  `
})
export class ResolutionHeaderComponent {
  /**
   * Dados da atividade clínica em resolução.
   */
  @Input() activity: ActivityDetailDTO | null = null;

  /**
   * Tempo restante em segundos (cronômetro regressivo).
   */
  @Input() timerSeconds = 1200;

  /**
   * Indica se o cronômetro está pausado.
   */
  @Input() isTimerPaused = false;

  /**
   * Tempo formatado em mm:ss.
   */
  @Input() formattedTime = '20:00';

  /**
   * Notifica a alternância entre pausar e retomar o cronômetro.
   */
  @Output() toggleTimer = new EventEmitter<void>();

  /**
   * Notifica a intenção de submeter a resolução clínica.
   */
  @Output() submit = new EventEmitter<void>();

  /**
   * Notifica a intenção de voltar para a listagem de atividades.
   */
  @Output() goBack = new EventEmitter<void>();
}
