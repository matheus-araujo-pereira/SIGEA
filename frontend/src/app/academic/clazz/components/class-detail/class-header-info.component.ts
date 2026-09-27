import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AcademicClassDetailDTO } from '../../models/academic-class.model';

/**
 * Componente atômico para exibição do cabeçalho da turma, dados do docente,
 * métricas gerais e botões de ação (Boletim, CSV, Painel Analítico, Nova Atividade).
 */
@Component({
  selector: 'app-class-header-info',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <!-- Barra Superior / Botão Voltar -->
      <div class="flex items-center justify-between">
        <button
          type="button"
          (click)="goBack.emit()"
          class="btn-secondary gap-2 text-xs"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          <span>Voltar para Turmas</span>
        </button>

        <div class="flex items-center gap-3">
          @if (isAdmin() || isProfessor()) {
            <button
              type="button"
              (click)="downloadBulletin.emit()"
              class="btn-secondary gap-2 text-clinical-700 hover:bg-clinical-50 border-clinical-200"
              title="Baixar Boletim Epidemiológico em PDF"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <span>Boletim (PDF)</span>
            </button>

            <button
              type="button"
              (click)="downloadResearch.emit()"
              class="btn-secondary gap-2 text-slate-700 hover:bg-slate-50 border-slate-200"
              title="Exportar Base de Dados de Pesquisa (CSV)"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
              <span>Dados (CSV)</span>
            </button>

            <button
              type="button"
              (click)="viewDashboard.emit()"
              class="btn-secondary gap-2 text-emerald-700 hover:bg-emerald-50 border-emerald-200"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
              </svg>
              <span>Painel Analítico GTT</span>
            </button>
          }

          @if ((isAdmin() || isProfessor()) && !classData().isClosed) {
            <button
              type="button"
              (click)="createNewActivity.emit()"
              class="btn-primary gap-2"
            >
              <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              <span>Nova Atividade</span>
            </button>
          }
        </div>
      </div>

      <!-- Card Principal da Turma -->
      <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs">
        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div class="flex items-center gap-3">
              <h1 class="text-2xl font-black text-slate-900 tracking-tight">{{ classData().formattedName }}</h1>
              @if (!classData().isClosed) {
                <span class="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                  Ativa
                </span>
              } @else {
                <span class="px-2.5 py-0.5 rounded-full text-xs font-bold bg-slate-100 text-slate-600 border border-slate-200">
                  Encerrada
                </span>
              }
            </div>
            <p class="text-xs text-slate-500 mt-1">
              Disciplina: <strong class="text-slate-800">{{ classData().subjectName }}</strong> •
              Turma: <strong class="text-slate-800">{{ classData().classCode }}</strong> •
              Período: <strong class="text-slate-800">{{ classData().academicPeriod }}</strong>
            </p>
          </div>

          <!-- Card Resumo do Professor -->
          <div class="p-3.5 bg-slate-50 border border-slate-200/80 rounded-2xl flex items-center gap-3">
            <div class="w-10 h-10 rounded-xl bg-clinical-100 text-clinical-700 flex items-center justify-center font-black text-sm">
              Doc
            </div>
            <div>
              <p class="text-[10px] uppercase font-bold tracking-wider text-slate-400">Docente Responsável</p>
              <p class="text-xs font-bold text-slate-900">{{ classData().professorName }}</p>
              <p class="text-[11px] text-slate-500 font-mono">{{ classData().professorEmail }}</p>
            </div>
          </div>
        </div>

        <!-- Métricas Resumo -->
        <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 mt-6 pt-6 border-t border-slate-100 text-xs">
          <div class="p-3 bg-slate-50/60 rounded-2xl">
            <span class="text-slate-400 font-medium block text-[11px]">Estudantes</span>
            <span class="text-xl font-black text-slate-900">{{ classData().studentCount }}</span>
          </div>
          <div class="p-3 bg-slate-50/60 rounded-2xl">
            <span class="text-slate-400 font-medium block text-[11px]">Atividades Avaliativas</span>
            <span class="text-xl font-black text-slate-900">{{ activitiesCount() }}</span>
          </div>
          <div class="p-3 bg-slate-50/60 rounded-2xl">
            <span class="text-slate-400 font-medium block text-[11px]">Período Letivo</span>
            <span class="text-xl font-black text-clinical-700">{{ classData().academicPeriod }}</span>
          </div>
          <div class="p-3 bg-slate-50/60 rounded-2xl">
            <span class="text-slate-400 font-medium block text-[11px]">Instituição</span>
            <span class="text-xl font-black text-slate-900">UFS</span>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class ClassHeaderInfoComponent {
  /** Dados completos de detalhes da turma */
  readonly classData = input.required<AcademicClassDetailDTO>();
  /** Flag indicando se o usuário logado é administrador */
  readonly isAdmin = input<boolean>(false);
  /** Flag indicando se o usuário logado é professor */
  readonly isProfessor = input<boolean>(false);
  /** Quantidade total de atividades vinculadas à turma */
  readonly activitiesCount = input<number>(0);

  /** Emite evento para retorno à lista de turmas */
  readonly goBack = output<void>();
  /** Emite solicitação de download do boletim em PDF */
  readonly downloadBulletin = output<void>();
  /** Emite solicitação de exportação de dados em CSV */
  readonly downloadResearch = output<void>();
  /** Emite solicitação de abertura do painel analítico GTT */
  readonly viewDashboard = output<void>();
  /** Emite solicitação de cadastro de nova atividade */
  readonly createNewActivity = output<void>();
}
