import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivityDetailDTO } from '../../../models/activity.model';

/**
 * Componente que renderiza a barra superior de navegação, dados da atividade e cronômetro metodológico IHI (20 min).
 */
@Component({
  selector: 'app-resolution-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './resolution-header.component.html'
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
