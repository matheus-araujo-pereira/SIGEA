import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FiveWTwoHItemData } from '../../../models/activity.model';

/**
 * Componente interativo para elaboração do plano de ação 5W2H para prevenção de eventos adversos.
 */
@Component({
  selector: 'app-five-w-two-h-tool',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
      <div class="flex items-center justify-between">
        <div>
          <h3 class="text-sm font-black text-slate-900">Matriz 5W2H (Plano de Ação)</h3>
          <p class="text-[11px] text-slate-500 mt-0.5">
            Defina ações concretas de melhoria assistencial e prevenção de eventos adversos
          </p>
        </div>
        <button
          type="button"
          (click)="addItem.emit()"
          class="px-3 py-1.5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 font-semibold text-xs transition-colors shadow-xs"
        >
          + Adicionar Ação
        </button>
      </div>

      <div class="space-y-4">
        @for (item of fiveWTwoHItems; track $index) {
          <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl relative space-y-3">
            <button
              type="button"
              (click)="removeItem.emit($index)"
              class="absolute top-3 right-3 text-slate-400 hover:text-rose-500 text-xs font-semibold"
            >
              Excluir Ação
            </button>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-3 pr-16 text-xs">
              <div>
                <label class="block font-bold text-slate-700 mb-1">O quê (What)?</label>
                <input type="text" [(ngModel)]="item.what" placeholder="O que será feito?" class="w-full px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs" />
              </div>
              <div>
                <label class="block font-bold text-slate-700 mb-1">Por quê (Why)?</label>
                <input type="text" [(ngModel)]="item.why" placeholder="Por que será feito?" class="w-full px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs" />
              </div>
              <div>
                <label class="block font-bold text-slate-700 mb-1">Onde (Where)?</label>
                <input type="text" [(ngModel)]="item.where" placeholder="Onde será executado?" class="w-full px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs" />
              </div>
              <div>
                <label class="block font-bold text-slate-700 mb-1">Quando (When)?</label>
                <input type="text" [(ngModel)]="item.when" placeholder="Qual o prazo de execução?" class="w-full px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs" />
              </div>
              <div>
                <label class="block font-bold text-slate-700 mb-1">Quem (Who)?</label>
                <input type="text" [(ngModel)]="item.who" placeholder="Quem é o responsável?" class="w-full px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs" />
              </div>
              <div>
                <label class="block font-bold text-slate-700 mb-1">Como (How)?</label>
                <input type="text" [(ngModel)]="item.how" placeholder="Qual método / procedimento?" class="w-full px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs" />
              </div>
            </div>
            <div class="text-xs">
              <label class="block font-bold text-slate-700 mb-1">Quanto custa (How much)?</label>
              <input type="text" [(ngModel)]="item.howMuch" placeholder="Custo estimado / Recursos necessários" class="w-full px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs" />
            </div>
          </div>
        } @empty {
          <p class="text-center text-slate-400 py-6 text-xs">Nenhuma ação cadastrada no plano 5W2H.</p>
        }
      </div>
    </div>
  `
})
export class FiveWTwoHToolComponent {
  /**
   * Planos de ação estruturados pelo discente.
   */
  @Input() fiveWTwoHItems: FiveWTwoHItemData[] = [];

  /**
   * Notifica a adição de uma nova ação ao plano.
   */
  @Output() addItem = new EventEmitter<void>();

  /**
   * Notifica a exclusão de uma ação por índice.
   */
  @Output() removeItem = new EventEmitter<number>();
}
