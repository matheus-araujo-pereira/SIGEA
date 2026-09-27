import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import type { SwotData } from '../../../../models/activity.model';

/**
 * Componente interativo para análise de fatores internos e externos via Matriz SWOT/FOFA e notas de Brainstorming.
 */
@Component({
  selector: 'app-swot-tool',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './swot-tool.component.html'
})
export class SwotToolComponent {
  /**
   * Dados dos 4 quadrantes da análise SWOT / FOFA.
   */
  @Input({ required: true }) swot!: SwotData;

  /**
   * Coleção de anotações livres da sessão de Brainstorming.
   */
  @Input() brainstormingNotes: string[] = [];

  /**
   * Notifica a adição de um item a um quadrante da matriz SWOT.
   */
  @Output() addSwotItem = new EventEmitter<'strengths' | 'weaknesses' | 'opportunities' | 'threats'>();

  /**
   * Notifica a exclusão de um item da matriz SWOT.
   */
  @Output() removeSwotItem = new EventEmitter<{ quadrant: 'strengths' | 'weaknesses' | 'opportunities' | 'threats'; index: number }>();

  /**
   * Notifica a adição de uma nota de brainstorming.
   */
  @Output() addBrainstormingNote = new EventEmitter<void>();

  /**
   * Notifica a exclusão de uma nota de brainstorming por índice.
   */
  @Output() removeBrainstormingNote = new EventEmitter<number>();
}
