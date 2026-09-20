import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ActivityService } from '../../../../core/services/activity.service';
import { GttTriggerService } from '../../../../core/services/gtt-trigger.service';
import { HarmSeverityService } from '../../../../core/services/harm-severity.service';
import { ToastService } from '../../../../core/services/toast.service';
import {
  ActivityDetailDTO,
  FiveWTwoHItemData,
  GutItemData,
  IdentifiedTriggerData,
  IshikawaData,
  PdcaData,
  QualityToolsData,
  SubmissionCreateDTO,
  SwotData,
} from '../../../../core/models/activity.model';
import { GttTrigger } from '../../../../core/models/gtt-trigger.model';
import { HarmSeverity } from '../../../../core/models/harm-severity.model';
import { ConfirmDialogComponent } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';

/**
 * Interface interativa do estudante para resolução de Atividade Avaliativa,
 * com visualizador de prontuário simulado, cronômetro IHI de 20 min, rastreador de gatilhos
 * e suíte de Ferramentas da Qualidade (Ishikawa 6M, GUT, 5W2H, PDCA, SWOT e Brainstorming).
 */
@Component({
  selector: 'app-activity-resolution',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, ConfirmDialogComponent],
  template: `
    <div class="space-y-6 max-w-7xl mx-auto pb-16">
      <!-- Barra Superior: Navegação e Cronômetro Metodológico IHI (20 minutos) -->
      <div class="bg-white rounded-2xl border border-slate-200/80 p-4 shadow-xs flex flex-col md:flex-row items-center justify-between gap-4 sticky top-4 z-30">
        <div class="flex items-center gap-3">
          <button
            type="button"
            (click)="goBack()"
            class="btn-secondary text-xs py-1.5 px-3"
          >
            Voltar
          </button>
          <div>
            <h2 class="text-sm font-black text-slate-900 truncate max-w-md">
              {{ activity()?.title || 'Resolução de Atividade' }}
            </h2>
            <p class="text-[11px] text-slate-500 font-medium">
              {{ activity()?.className }} • Prazo: {{ activity()?.deadline | date:'dd/MM/yyyy HH:mm' }}
            </p>
          </div>
        </div>

        <!-- Cronômetro IHI de 20 minutos (1.200 segundos) -->
        <div class="flex items-center gap-3">
          <div
            class="px-4 py-2 rounded-2xl border flex items-center gap-2 font-mono transition-colors shadow-2xs"
            [ngClass]="{
              'bg-rose-50 border-rose-200 text-rose-700 animate-pulse': timerSeconds() < 300,
              'bg-slate-50 border-slate-200 text-slate-800': timerSeconds() >= 300
            }"
          >
            <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span class="text-base font-black">{{ formattedTime() }}</span>
            <span class="text-[10px] font-sans font-bold uppercase text-slate-400">Tempo IHI</span>
          </div>

          <button
            type="button"
            (click)="toggleTimer()"
            class="btn-secondary text-xs py-2 px-3"
            [title]="isTimerPaused() ? 'Retomar cronômetro' : 'Pausar cronômetro'"
          >
            {{ isTimerPaused() ? 'Retomar' : 'Pausar' }}
          </button>

          <button
            type="button"
            (click)="confirmSubmit()"
            class="btn-primary text-xs py-2 px-4 shadow-sm"
          >
            Submeter Resposta
          </button>
        </div>
      </div>

      <!-- Seletor de Abas Principais (Prontuário vs Ferramentas da Qualidade) -->
      <div class="flex items-center gap-2 border-b border-slate-200 pb-2">
        <button
          type="button"
          (click)="activeMainTab.set('RECORD')"
          [class.border-clinical-600]="activeMainTab() === 'RECORD'"
          [class.text-clinical-800]="activeMainTab() === 'RECORD'"
          [class.border-transparent]="activeMainTab() !== 'RECORD'"
          [class.text-slate-500]="activeMainTab() !== 'RECORD'"
          class="px-4 py-2 text-xs font-black border-b-2 transition-all flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <span>Prontuário Simulado</span>
        </button>

        <button
          type="button"
          (click)="activeMainTab.set('TRIGGERS')"
          [class.border-clinical-600]="activeMainTab() === 'TRIGGERS'"
          [class.text-clinical-800]="activeMainTab() === 'TRIGGERS'"
          [class.border-transparent]="activeMainTab() !== 'TRIGGERS'"
          [class.text-slate-500]="activeMainTab() !== 'TRIGGERS'"
          class="px-4 py-2 text-xs font-black border-b-2 transition-all flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
          </svg>
          <span>Gatilhos GTT Identificados ({{ identifiedTriggers().length }})</span>
        </button>

        <button
          type="button"
          (click)="activeMainTab.set('QUALITY')"
          [class.border-clinical-600]="activeMainTab() === 'QUALITY'"
          [class.text-clinical-800]="activeMainTab() === 'QUALITY'"
          [class.border-transparent]="activeMainTab() !== 'QUALITY'"
          [class.text-slate-500]="activeMainTab() !== 'QUALITY'"
          class="px-4 py-2 text-xs font-black border-b-2 transition-all flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
          </svg>
          <span>Ferramentas da Qualidade</span>
        </button>
      </div>

      <!-- ABA 1: PRONTUÁRIO SIMULADO -->
      @if (activeMainTab() === 'RECORD') {
        <div class="space-y-6 animate-fade-in">
          <!-- Card de Identificação do Paciente -->
          <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs">
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-4">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-2xl bg-clinical-50 border border-clinical-200 text-clinical-700 flex items-center justify-center font-black text-lg">
                  {{ patientInitial() }}
                </div>
                <div>
                  <h3 class="text-lg font-black text-slate-900">{{ clinicalCase()?.patientName }}</h3>
                  <p class="text-xs text-slate-500">
                    {{ clinicalCase()?.age }} anos • {{ clinicalCase()?.gender }} • {{ clinicalCase()?.bed || 'Leito Clínico' }}
                  </p>
                </div>
              </div>
              <div class="flex items-center gap-3 text-xs">
                <div class="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-xl">
                  <span class="text-[10px] text-slate-400 font-bold block">Admissão</span>
                  <span class="font-bold text-slate-800">{{ clinicalCase()?.admissionDate || 'D0' }}</span>
                </div>
                <div class="px-3 py-1.5 bg-clinical-50 border border-clinical-200 rounded-xl text-clinical-900">
                  <span class="text-[10px] text-clinical-600 font-bold block">Permanência</span>
                  <span class="font-black">{{ clinicalCase()?.patientDays || 1 }} pacientes-dia</span>
                </div>
              </div>
            </div>

            <!-- Admissão e Histórico Clínico -->
            <div class="mt-4 space-y-2">
              <h4 class="text-xs font-black uppercase tracking-wider text-slate-400">Nota de Admissão & Histórico</h4>
              <p class="text-xs text-slate-700 bg-slate-50/80 p-4 rounded-2xl border border-slate-100 leading-relaxed">
                {{ clinicalCase()?.admissionNotes || 'Sem registro de admissão adicional.' }}
              </p>
            </div>
          </div>

          <!-- Evoluções Multiprofissionais -->
          <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
            <h3 class="text-sm font-black text-slate-900">Evoluções Médicas e de Enfermagem</h3>
            <div class="space-y-3">
              @for (note of clinicalCase()?.evolutionNotes; track $index) {
                <div class="p-4 bg-slate-50/70 border border-slate-200/80 rounded-2xl space-y-1.5">
                  <div class="flex items-center justify-between text-[11px]">
                    <span class="font-bold text-clinical-700">{{ note.professionalRole }}</span>
                    <span class="text-slate-400 font-mono">{{ note.dateTime }}</span>
                  </div>
                  <p class="text-xs text-slate-800 leading-relaxed">{{ note.note }}</p>
                </div>
              } @empty {
                <p class="text-slate-400 text-xs text-center py-4">Nenhuma anotação de evolução cadastrada.</p>
              }
            </div>
          </div>

          <!-- Prescrições e Checagem -->
          <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
            <h3 class="text-sm font-black text-slate-900">Prescrições Medicamentosas e Checagem</h3>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-slate-200 text-left text-xs">
                <thead class="bg-slate-50 text-slate-500 font-bold uppercase text-[10px]">
                  <tr>
                    <th class="px-4 py-2.5">Medicamento</th>
                    <th class="px-4 py-2.5">Dose</th>
                    <th class="px-4 py-2.5">Via</th>
                    <th class="px-4 py-2.5">Frequência</th>
                    <th class="px-4 py-2.5">Checagem de Administração</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-100 font-medium">
                  @for (rx of clinicalCase()?.prescriptions; track $index) {
                    <tr>
                      <td class="px-4 py-3 font-bold text-slate-900">{{ rx.medication }}</td>
                      <td class="px-4 py-3 text-slate-700 font-mono">{{ rx.dosage }}</td>
                      <td class="px-4 py-3 text-slate-700">{{ rx.route }}</td>
                      <td class="px-4 py-3 text-slate-700">{{ rx.frequency }}</td>
                      <td class="px-4 py-3">
                        <span class="inline-flex items-center px-2 py-0.5 rounded-md text-[11px] font-mono font-bold bg-clinical-50 text-clinical-800 border border-clinical-200">
                          {{ rx.administrationCheck || 'Administrado conforme prescrito' }}
                        </span>
                      </td>
                    </tr>
                  } @empty {
                    <tr>
                      <td colspan="5" class="px-4 py-6 text-center text-slate-400">Nenhum medicamento prescrito registrado.</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>

          <!-- Grid: Exames e Procedimentos -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Exames -->
            <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-3">
              <h3 class="text-sm font-black text-slate-900">Exames Laboratoriais</h3>
              <div class="space-y-2.5">
                @for (exam of clinicalCase()?.labExams; track $index) {
                  <div class="p-3 bg-slate-50 border border-slate-200/80 rounded-2xl flex items-center justify-between">
                    <div>
                      <p class="font-bold text-slate-900 text-xs">{{ exam.examName }}</p>
                      <p class="text-[10px] text-slate-400">Ref: {{ exam.referenceValue || 'N/A' }} • Data: {{ exam.date || 'D0' }}</p>
                    </div>
                    <span class="text-xs font-black font-mono px-2.5 py-1 bg-white border border-slate-200 rounded-lg text-slate-800">
                      {{ exam.result }}
                    </span>
                  </div>
                } @empty {
                  <p class="text-slate-400 text-xs text-center py-4">Sem exames registrados.</p>
                }
              </div>
            </div>

            <!-- Procedimentos -->
            <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-3">
              <h3 class="text-sm font-black text-slate-900">Procedimentos Cirúrgicos / Invasivos</h3>
              <div class="space-y-2.5">
                @for (proc of clinicalCase()?.procedures; track $index) {
                  <div class="p-3 bg-slate-50 border border-slate-200/80 rounded-2xl space-y-1">
                    <div class="flex items-center justify-between text-xs">
                      <span class="font-bold text-slate-900">{{ proc.procedureName }}</span>
                      <span class="text-[10px] text-slate-400 font-mono">{{ proc.date }}</span>
                    </div>
                    <p class="text-[11px] text-slate-600">{{ proc.description }}</p>
                  </div>
                } @empty {
                  <p class="text-slate-400 text-xs text-center py-4">Sem procedimentos registrados.</p>
                }
              </div>
            </div>
          </div>
        </div>
      }

      <!-- ABA 2: RASTREAMENTO DE GATILHOS GTT -->
      @if (activeMainTab() === 'TRIGGERS') {
        <div class="space-y-6 animate-fade-in">
          <!-- Card de Adição de Gatilho Identificado -->
          <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
              <h3 class="text-sm font-black text-slate-900">Rastrear Gatilho no Prontuário Simulado</h3>
              <span class="text-[11px] text-slate-400 font-medium">53 Gatilhos Oficiais IHI-GTT</span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
              <!-- Seleção do Gatilho -->
              <div class="md:col-span-2">
                <label class="block font-bold text-slate-700 mb-1.5">Gatilho IHI</label>
                <select
                  [(ngModel)]="selectedTriggerCode"
                  class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium text-slate-800 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                >
                  <option value="">Selecione um dos 53 gatilhos oficiais...</option>
                  @for (t of availableTriggers(); track t.id) {
                    <option [value]="t.code">
                      [{{ t.code }}] {{ t.name }} (Módulo {{ t.moduleCode }})
                    </option>
                  }
                </select>
              </div>

              <!-- É Evento Adverso com Dano? -->
              <div>
                <label class="block font-bold text-slate-700 mb-1.5">Gerou Dano (Evento Adverso)?</label>
                <select
                  [(ngModel)]="isHarmSelected"
                  class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-bold text-slate-800 focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
                >
                  <option [ngValue]="false">Não (Apenas Gatilho Sem Dano)</option>
                  <option [ngValue]="true">Sim (Evento Adverso Confirmado)</option>
                </select>
              </div>
            </div>

            <!-- Se houver dano: Gravidade NCC MERP (E a I) -->
            @if (isHarmSelected) {
              <div class="p-4 bg-amber-50 border border-amber-200 rounded-2xl space-y-2">
                <label class="block font-bold text-amber-900 text-xs">
                  Classificação de Severidade do Dano (Categorias E a I - NCC MERP)
                </label>
                <select
                  [(ngModel)]="selectedHarmSeverityLetter"
                  class="w-full px-3.5 py-2 bg-white border border-amber-200 rounded-xl text-xs font-bold text-amber-900 focus:outline-none focus:ring-2 focus:ring-amber-500"
                >
                  @for (s of harmSeverities(); track s.id) {
                    <option [value]="s.categoryLetter">
                      Categoria {{ s.categoryLetter }} - {{ s.description }}
                    </option>
                  }
                </select>
              </div>
            }

            <!-- Justificativa / Evidência no Prontuário -->
            <div>
              <label class="block font-bold text-slate-700 mb-1.5 text-xs">
                Evidência Encontrada no Prontuário Simulado
              </label>
              <textarea
                rows="2"
                [(ngModel)]="triggerNotes"
                placeholder="Indique a evolução médica, prescrição ou exame laboratorial que comprova a presença deste gatilho..."
                class="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-xs focus:bg-white focus:outline-none focus:ring-2 focus:ring-clinical-500"
              ></textarea>
            </div>

            <div class="flex justify-end">
              <button
                type="button"
                (click)="addIdentifiedTrigger()"
                [disabled]="!selectedTriggerCode"
                class="btn-primary text-xs py-2 px-4"
              >
                Adicionar Gatilho ao Caso
              </button>
            </div>
          </div>

          <!-- Relação dos Gatilhos já adicionados pelo aluno -->
          <div class="space-y-3">
            <h3 class="text-sm font-black text-slate-900">
              Gatilhos Adicionados à sua Resolução ({{ identifiedTriggers().length }})
            </h3>

            @for (item of identifiedTriggers(); track $index) {
              <div class="bg-white rounded-2xl border border-slate-200/80 p-4 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div class="space-y-1">
                  <div class="flex items-center gap-2">
                    <span class="px-2 py-0.5 rounded-md font-mono font-black text-xs bg-slate-100 text-slate-900">
                      {{ item.triggerCode }}
                    </span>
                    <span class="font-bold text-slate-900 text-xs">{{ item.triggerName }}</span>
                    @if (item.isHarm) {
                      <span class="px-2 py-0.5 rounded-full text-[10px] font-black bg-rose-50 text-rose-700 border border-rose-200">
                        Evento Adverso (Dano Cat. {{ item.harmSeverityLetter || 'E' }})
                      </span>
                    } @else {
                      <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-slate-100 text-slate-600">
                        Sem Dano
                      </span>
                    }
                  </div>
                  @if (item.notes) {
                    <p class="text-xs text-slate-500 font-medium">Evidência: {{ item.notes }}</p>
                  }
                </div>

                <button
                  type="button"
                  (click)="removeIdentifiedTrigger($index)"
                  class="text-slate-400 hover:text-rose-600 p-1.5 rounded-lg self-end sm:self-center"
                  title="Remover gatilho"
                >
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </button>
              </div>
            } @empty {
              <div class="bg-white rounded-2xl border border-slate-200/80 p-8 text-center text-slate-400 text-xs">
                Nenhum gatilho adicionado ainda. Revise o prontuário e selecione os gatilhos no formulário acima.
              </div>
            }
          </div>
        </div>
      }

      <!-- ABA 3: FERRAMENTAS DA QUALIDADE INTERATIVAS -->
      @if (activeMainTab() === 'QUALITY') {
        <div class="space-y-6 animate-fade-in text-xs">
          <!-- Sub-abas das Ferramentas da Qualidade -->
          <div class="flex flex-wrap gap-2">
            @for (tool of toolsList; track tool.key) {
              <button
                type="button"
                (click)="activeQualityTool.set(tool.key)"
                [class.bg-clinical-900]="activeQualityTool() === tool.key"
                [class.text-white]="activeQualityTool() === tool.key"
                [class.bg-white]="activeQualityTool() !== tool.key"
                [class.text-slate-700]="activeQualityTool() !== tool.key"
                class="px-3.5 py-2 rounded-xl font-bold border border-slate-200 shadow-2xs transition-all"
              >
                {{ tool.name }}
              </button>
            }
          </div>

          <!-- FERRAMENTA 1: DIAGRAMA DE ISHIKAWA (6M) -->
          @if (activeQualityTool() === 'ISHIKAWA') {
            <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-6">
              <div>
                <h3 class="text-sm font-black text-slate-900">Diagrama de Causa e Efeito (Ishikawa / 6M)</h3>
                <p class="text-[11px] text-slate-500 mt-0.5">
                  Analise as causas-raiz do evento adverso divididas nos 6M clássicos da engenharia da qualidade
                </p>
              </div>

              <!-- Problema Central (Cabeça do Peixe) -->
              <div class="p-4 bg-rose-50 border border-rose-200 rounded-2xl space-y-1.5">
                <label class="font-black text-rose-950 uppercase tracking-wider text-[10px]">
                  Problema Central / Efeito (Cabeça do Peixe)
                </label>
                <input
                  type="text"
                  [(ngModel)]="qualityTools.ishikawa!.centralProblem"
                  placeholder="Ex: Hemorragia grave por sobredose de heparina não monitorada"
                  class="w-full px-3 py-2 bg-white border border-rose-200 rounded-xl text-xs font-bold text-rose-950 focus:outline-none focus:ring-2 focus:ring-rose-500"
                />
              </div>

              <!-- Grade dos 6M -->
              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                <!-- Método -->
                <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="font-black text-slate-900">1. Método</span>
                    <button type="button" (click)="addIshikawaCause('method')" class="text-clinical-600 font-bold hover:underline">+ Causa</button>
                  </div>
                  @for (c of qualityTools.ishikawa!.methodCauses; track $index) {
                    <div class="flex items-center gap-1.5">
                      <input
                        type="text"
                        [(ngModel)]="qualityTools.ishikawa!.methodCauses![$index]"
                        class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
                      />
                      <button type="button" (click)="removeIshikawaCause('method', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                    </div>
                  }
                </div>

                <!-- Mão de Obra -->
                <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="font-black text-slate-900">2. Mão de Obra</span>
                    <button type="button" (click)="addIshikawaCause('manpower')" class="text-clinical-600 font-bold hover:underline">+ Causa</button>
                  </div>
                  @for (c of qualityTools.ishikawa!.manpowerCauses; track $index) {
                    <div class="flex items-center gap-1.5">
                      <input
                        type="text"
                        [(ngModel)]="qualityTools.ishikawa!.manpowerCauses![$index]"
                        class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
                      />
                      <button type="button" (click)="removeIshikawaCause('manpower', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                    </div>
                  }
                </div>

                <!-- Material -->
                <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="font-black text-slate-900">3. Material</span>
                    <button type="button" (click)="addIshikawaCause('material')" class="text-clinical-600 font-bold hover:underline">+ Causa</button>
                  </div>
                  @for (c of qualityTools.ishikawa!.materialCauses; track $index) {
                    <div class="flex items-center gap-1.5">
                      <input
                        type="text"
                        [(ngModel)]="qualityTools.ishikawa!.materialCauses![$index]"
                        class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
                      />
                      <button type="button" (click)="removeIshikawaCause('material', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                    </div>
                  }
                </div>

                <!-- Máquina -->
                <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="font-black text-slate-900">4. Máquina / Equipamentos</span>
                    <button type="button" (click)="addIshikawaCause('machine')" class="text-clinical-600 font-bold hover:underline">+ Causa</button>
                  </div>
                  @for (c of qualityTools.ishikawa!.machineCauses; track $index) {
                    <div class="flex items-center gap-1.5">
                      <input
                        type="text"
                        [(ngModel)]="qualityTools.ishikawa!.machineCauses![$index]"
                        class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
                      />
                      <button type="button" (click)="removeIshikawaCause('machine', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                    </div>
                  }
                </div>

                <!-- Meio Ambiente -->
                <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="font-black text-slate-900">5. Meio Ambiente</span>
                    <button type="button" (click)="addIshikawaCause('environment')" class="text-clinical-600 font-bold hover:underline">+ Causa</button>
                  </div>
                  @for (c of qualityTools.ishikawa!.environmentCauses; track $index) {
                    <div class="flex items-center gap-1.5">
                      <input
                        type="text"
                        [(ngModel)]="qualityTools.ishikawa!.environmentCauses![$index]"
                        class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
                      />
                      <button type="button" (click)="removeIshikawaCause('environment', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                    </div>
                  }
                </div>

                <!-- Medida -->
                <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl space-y-2">
                  <div class="flex items-center justify-between">
                    <span class="font-black text-slate-900">6. Medida</span>
                    <button type="button" (click)="addIshikawaCause('measurement')" class="text-clinical-600 font-bold hover:underline">+ Causa</button>
                  </div>
                  @for (c of qualityTools.ishikawa!.measurementCauses; track $index) {
                    <div class="flex items-center gap-1.5">
                      <input
                        type="text"
                        [(ngModel)]="qualityTools.ishikawa!.measurementCauses![$index]"
                        class="flex-1 px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs"
                      />
                      <button type="button" (click)="removeIshikawaCause('measurement', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                    </div>
                  }
                </div>
              </div>
            </div>
          }

          <!-- FERRAMENTA 2: MATRIZ GUT INTERATIVA -->
          @if (activeQualityTool() === 'GUT') {
            <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
              <div class="flex items-center justify-between">
                <div>
                  <h3 class="text-sm font-black text-slate-900">Matriz GUT (Gravidade × Urgência × Tendência)</h3>
                  <p class="text-[11px] text-slate-500 mt-0.5">
                    Priorização de problemas com cálculo automático imediato de 1 a 125
                  </p>
                </div>
                <button type="button" (click)="addGutItem()" class="btn-secondary text-xs py-1.5 px-3">
                  + Adicionar Problema
                </button>
              </div>

              <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-slate-200 text-left text-xs">
                  <thead class="bg-slate-50 text-slate-500 font-bold uppercase text-[10px]">
                    <tr>
                      <th class="px-4 py-2.5">Problema / Hipótese</th>
                      <th class="px-4 py-2.5 text-center">G (1 a 5)</th>
                      <th class="px-4 py-2.5 text-center">U (1 a 5)</th>
                      <th class="px-4 py-2.5 text-center">T (1 a 5)</th>
                      <th class="px-4 py-2.5 text-center">Score (G×U×T)</th>
                      <th class="px-4 py-2.5 text-right">Ação</th>
                    </tr>
                  </thead>
                  <tbody class="divide-y divide-slate-100 font-medium">
                    @for (item of qualityTools.gutItems; track $index) {
                      <tr>
                        <td class="px-4 py-3">
                          <input
                            type="text"
                            [(ngModel)]="item.problem"
                            placeholder="Descreva o problema assistencial..."
                            class="w-full px-2.5 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-xs"
                          />
                        </td>
                        <td class="px-4 py-3 text-center">
                          <select [(ngModel)]="item.gravity" class="px-2 py-1 bg-slate-50 border rounded-lg font-bold">
                            <option [ngValue]="1">1 - Sem gravidade</option>
                            <option [ngValue]="2">2 - Pouco grave</option>
                            <option [ngValue]="3">3 - Grave</option>
                            <option [ngValue]="4">4 - Muito grave</option>
                            <option [ngValue]="5">5 - Extremamente grave</option>
                          </select>
                        </td>
                        <td class="px-4 py-3 text-center">
                          <select [(ngModel)]="item.urgency" class="px-2 py-1 bg-slate-50 border rounded-lg font-bold">
                            <option [ngValue]="1">1 - Pode esperar</option>
                            <option [ngValue]="2">2 - Pouco urgente</option>
                            <option [ngValue]="3">3 - Urgente</option>
                            <option [ngValue]="4">4 - Muito urgente</option>
                            <option [ngValue]="5">5 - Imediata</option>
                          </select>
                        </td>
                        <td class="px-4 py-3 text-center">
                          <select [(ngModel)]="item.trend" class="px-2 py-1 bg-slate-50 border rounded-lg font-bold">
                            <option [ngValue]="1">1 - Não vai mudar</option>
                            <option [ngValue]="2">2 - Vai piorar a longo prazo</option>
                            <option [ngValue]="3">3 - Vai piorar a médio prazo</option>
                            <option [ngValue]="4">4 - Vai piorar rápido</option>
                            <option [ngValue]="5">5 - Vai piorar imediatamente</option>
                          </select>
                        </td>
                        <td class="px-4 py-3 text-center">
                          <span
                            class="px-2.5 py-1 rounded-xl text-xs font-black font-mono inline-block"
                            [ngClass]="{
                              'bg-rose-100 text-rose-800': calculateGutScore(item) >= 60,
                              'bg-amber-100 text-amber-800': calculateGutScore(item) >= 20 && calculateGutScore(item) < 60,
                              'bg-slate-100 text-slate-800': calculateGutScore(item) < 20
                            }"
                          >
                            {{ calculateGutScore(item) }}
                          </span>
                        </td>
                        <td class="px-4 py-3 text-right">
                          <button type="button" (click)="removeGutItem($index)" class="text-slate-400 hover:text-rose-500">
                            Excluir
                          </button>
                        </td>
                      </tr>
                    } @empty {
                      <tr><td colspan="6" class="px-4 py-6 text-center text-slate-400">Nenhum item na Matriz GUT.</td></tr>
                    }
                  </tbody>
                </table>
              </div>
            </div>
          }

          <!-- FERRAMENTA 3: MATRIZ 5W2H -->
          @if (activeQualityTool() === '5W2H') {
            <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
              <div class="flex items-center justify-between">
                <div>
                  <h3 class="text-sm font-black text-slate-900">Matriz 5W2H (Plano de Ação)</h3>
                  <p class="text-[11px] text-slate-500 mt-0.5">
                    Defina ações concretas de melhoria assistencial e prevenção de eventos adversos
                  </p>
                </div>
                <button type="button" (click)="addFiveWTwoHItem()" class="btn-secondary text-xs py-1.5 px-3">
                  + Adicionar Ação
                </button>
              </div>

              <div class="space-y-4">
                @for (item of qualityTools.fiveWTwoHItems; track $index) {
                  <div class="p-4 bg-slate-50 border border-slate-200 rounded-2xl relative space-y-3">
                    <button type="button" (click)="removeFiveWTwoHItem($index)" class="absolute top-3 right-3 text-slate-400 hover:text-rose-500">
                      Excluir Ação
                    </button>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-3 pr-16">
                      <div>
                        <label class="block font-bold text-slate-700 mb-1">O quê (What)?</label>
                        <input type="text" [(ngModel)]="item.what" placeholder="O que será feito?" class="w-full px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                      </div>
                      <div>
                        <label class="block font-bold text-slate-700 mb-1">Por quê (Why)?</label>
                        <input type="text" [(ngModel)]="item.why" placeholder="Por que será feito?" class="w-full px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                      </div>
                      <div>
                        <label class="block font-bold text-slate-700 mb-1">Onde (Where)?</label>
                        <input type="text" [(ngModel)]="item.where" placeholder="Onde será executado?" class="w-full px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                      </div>
                      <div>
                        <label class="block font-bold text-slate-700 mb-1">Quando (When)?</label>
                        <input type="text" [(ngModel)]="item.when" placeholder="Qual o prazo de execução?" class="w-full px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                      </div>
                      <div>
                        <label class="block font-bold text-slate-700 mb-1">Quem (Who)?</label>
                        <input type="text" [(ngModel)]="item.who" placeholder="Quem é o responsável?" class="w-full px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                      </div>
                      <div>
                        <label class="block font-bold text-slate-700 mb-1">Como (How)?</label>
                        <input type="text" [(ngModel)]="item.how" placeholder="Qual método / procedimento?" class="w-full px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                      </div>
                    </div>
                    <div>
                      <label class="block font-bold text-slate-700 mb-1">Quanto custa (How much)?</label>
                      <input type="text" [(ngModel)]="item.howMuch" placeholder="Custo estimado / Recursos necessários" class="w-full px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                    </div>
                  </div>
                } @empty {
                  <p class="text-center text-slate-400 py-6">Nenhuma ação cadastrada no plano 5W2H.</p>
                }
              </div>
            </div>
          }

          <!-- FERRAMENTA 4: CICLO PDCA / PDSA -->
          @if (activeQualityTool() === 'PDCA') {
            <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
              <div>
                <h3 class="text-sm font-black text-slate-900">Ciclo de Melhoria Contínua PDCA / PDSA</h3>
                <p class="text-[11px] text-slate-500 mt-0.5">
                  Estruture a melhoria da qualidade nos 4 quadrantes clássicos
                </p>
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <!-- Plan -->
                <div class="p-4 bg-clinical-50/50 border border-clinical-200 rounded-2xl space-y-2">
                  <span class="font-black text-clinical-900 text-xs">P - Plan (Planejar)</span>
                  <textarea
                    rows="3"
                    [(ngModel)]="qualityTools.pdca!.plan"
                    placeholder="Problema identificado, objetivos e metas a serem alcançadas..."
                    class="w-full px-3 py-2 bg-white border border-clinical-200 rounded-xl text-xs"
                  ></textarea>
                </div>

                <!-- Do -->
                <div class="p-4 bg-amber-50/50 border border-amber-200 rounded-2xl space-y-2">
                  <span class="font-black text-amber-900 text-xs">D - Do (Executar)</span>
                  <textarea
                    rows="3"
                    [(ngModel)]="qualityTools.pdca!.doPhase"
                    placeholder="Implementação do plano de ação e execução dos treinamentos..."
                    class="w-full px-3 py-2 bg-white border border-amber-200 rounded-xl text-xs"
                  ></textarea>
                </div>

                <!-- Check -->
                <div class="p-4 bg-emerald-50/50 border border-emerald-200 rounded-2xl space-y-2">
                  <span class="font-black text-emerald-900 text-xs">C - Check (Checar / Estudar)</span>
                  <textarea
                    rows="3"
                    [(ngModel)]="qualityTools.pdca!.checkPhase"
                    placeholder="Auditorias de prontuário, medição de taxas GTT e verificação de resultados..."
                    class="w-full px-3 py-2 bg-white border border-emerald-200 rounded-xl text-xs"
                  ></textarea>
                </div>

                <!-- Act -->
                <div class="p-4 bg-purple-50/50 border border-purple-200 rounded-2xl space-y-2">
                  <span class="font-black text-purple-900 text-xs">A - Act (Agir / Padronizar)</span>
                  <textarea
                    rows="3"
                    [(ngModel)]="qualityTools.pdca!.actPhase"
                    placeholder="Padronização do novo procedimento operacional e difusão na equipe..."
                    class="w-full px-3 py-2 bg-white border border-purple-200 rounded-xl text-xs"
                  ></textarea>
                </div>
              </div>
            </div>
          }

          <!-- FERRAMENTA 5: MATRIZ SWOT (FOFA) E BRAINSTORMING -->
          @if (activeQualityTool() === 'SWOT') {
            <div class="space-y-6">
              <!-- Matriz SWOT -->
              <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
                <h3 class="text-sm font-black text-slate-900">Matriz SWOT / FOFA</h3>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <!-- Forças -->
                  <div class="p-4 bg-emerald-50/40 border border-emerald-200 rounded-2xl space-y-2">
                    <div class="flex items-center justify-between">
                      <span class="font-black text-emerald-900">Forças (Strengths)</span>
                      <button type="button" (click)="addSwotItem('strengths')" class="text-emerald-700 font-bold hover:underline">+ Item</button>
                    </div>
                    @for (item of qualityTools.swot!.strengths; track $index) {
                      <div class="flex items-center gap-1.5">
                        <input type="text" [(ngModel)]="qualityTools.swot!.strengths![$index]" class="flex-1 px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                        <button type="button" (click)="removeSwotItem('strengths', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                      </div>
                    }
                  </div>

                  <!-- Fraquezas -->
                  <div class="p-4 bg-rose-50/40 border border-rose-200 rounded-2xl space-y-2">
                    <div class="flex items-center justify-between">
                      <span class="font-black text-rose-900">Fraquezas (Weaknesses)</span>
                      <button type="button" (click)="addSwotItem('weaknesses')" class="text-rose-700 font-bold hover:underline">+ Item</button>
                    </div>
                    @for (item of qualityTools.swot!.weaknesses; track $index) {
                      <div class="flex items-center gap-1.5">
                        <input type="text" [(ngModel)]="qualityTools.swot!.weaknesses![$index]" class="flex-1 px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                        <button type="button" (click)="removeSwotItem('weaknesses', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                      </div>
                    }
                  </div>

                  <!-- Oportunidades -->
                  <div class="p-4 bg-clinical-50/40 border border-clinical-200 rounded-2xl space-y-2">
                    <div class="flex items-center justify-between">
                      <span class="font-black text-clinical-900">Oportunidades (Opportunities)</span>
                      <button type="button" (click)="addSwotItem('opportunities')" class="text-clinical-700 font-bold hover:underline">+ Item</button>
                    </div>
                    @for (item of qualityTools.swot!.opportunities; track $index) {
                      <div class="flex items-center gap-1.5">
                        <input type="text" [(ngModel)]="qualityTools.swot!.opportunities![$index]" class="flex-1 px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                        <button type="button" (click)="removeSwotItem('opportunities', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                      </div>
                    }
                  </div>

                  <!-- Ameaças -->
                  <div class="p-4 bg-amber-50/40 border border-amber-200 rounded-2xl space-y-2">
                    <div class="flex items-center justify-between">
                      <span class="font-black text-amber-900">Ameaças (Threats)</span>
                      <button type="button" (click)="addSwotItem('threats')" class="text-amber-700 font-bold hover:underline">+ Item</button>
                    </div>
                    @for (item of qualityTools.swot!.threats; track $index) {
                      <div class="flex items-center gap-1.5">
                        <input type="text" [(ngModel)]="qualityTools.swot!.threats![$index]" class="flex-1 px-2.5 py-1.5 bg-white border rounded-lg text-xs" />
                        <button type="button" (click)="removeSwotItem('threats', $index)" class="text-slate-400 hover:text-rose-500">×</button>
                      </div>
                    }
                  </div>
                </div>
              </div>

              <!-- Painel de Brainstorming -->
              <div class="bg-white rounded-3xl border border-slate-200/80 p-6 shadow-xs space-y-4">
                <div class="flex items-center justify-between">
                  <div>
                    <h3 class="text-sm font-black text-slate-900">Painel de Ideação / Brainstorming</h3>
                    <p class="text-[11px] text-slate-500 mt-0.5">
                      Geração livre de ideias para mitigação de falhas e segurança do paciente
                    </p>
                  </div>
                  <button type="button" (click)="addBrainstormingNote()" class="btn-secondary text-xs py-1.5 px-3">
                    + Adicionar Ideia
                  </button>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3">
                  @for (idea of qualityTools.brainstormingNotes; track $index) {
                    <div class="p-3 bg-amber-50/60 border border-amber-200/80 rounded-2xl relative space-y-1">
                      <textarea
                        rows="2"
                        [(ngModel)]="qualityTools.brainstormingNotes![$index]"
                        placeholder="Escreva uma ideia..."
                        class="w-full bg-transparent border-none text-xs text-amber-950 resize-none focus:outline-none"
                      ></textarea>
                      <button
                        type="button"
                        (click)="removeBrainstormingNote($index)"
                        class="absolute top-2 right-2 text-amber-600 hover:text-rose-600 text-sm font-bold"
                      >
                        ×
                      </button>
                    </div>
                  } @empty {
                    <p class="col-span-3 text-center text-slate-400 py-4">Nenhuma ideia adicionada ao Brainstorming.</p>
                  }
                </div>
              </div>
            </div>
          }
        </div>
      }

      <!-- Modal de Confirmação de Submissão -->
      <app-confirm-dialog
        [isOpen]="isConfirmSubmitOpen()"
        title="Submeter Resolução de Atividade"
        message="Deseja realmente enviar sua resolução? Após o envio, você não poderá alterar suas respostas caso o professor já inicie a correção."
        confirmText="Confirmar e Enviar"
        [isDestructive]="false"
        (confirmed)="submitResolution()"
        (cancelled)="isConfirmSubmitOpen.set(false)"
        (confirm)="submitResolution()"
        (cancel)="isConfirmSubmitOpen.set(false)"
      />
    </div>
  `,
})
export class ActivityResolutionComponent implements OnInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly activityService = inject(ActivityService);
  private readonly triggerService = inject(GttTriggerService);
  private readonly severityService = inject(HarmSeverityService);
  private readonly toast = inject(ToastService);

  readonly activity = signal<ActivityDetailDTO | null>(null);
  readonly availableTriggers = signal<GttTrigger[]>([]);
  readonly harmSeverities = signal<HarmSeverity[]>([]);

  // Navegação de abas
  readonly activeMainTab = signal<'RECORD' | 'TRIGGERS' | 'QUALITY'>('RECORD');
  readonly activeQualityTool = signal<'ISHIKAWA' | 'GUT' | '5W2H' | 'PDCA' | 'SWOT'>('ISHIKAWA');

  // Cronômetro de 20 minutos (1200 segundos)
  readonly timerSeconds = signal(1200);
  readonly isTimerPaused = signal(false);
  private timerInterval: any = null;

  // Gatilhos Identificados
  readonly identifiedTriggers = signal<IdentifiedTriggerData[]>([]);
  selectedTriggerCode = '';
  isHarmSelected = false;
  selectedHarmSeverityLetter = 'E';
  triggerNotes = '';

  // Ferramentas da Qualidade
  qualityTools: QualityToolsData = {
    ishikawa: {
      centralProblem: '',
      methodCauses: [''],
      manpowerCauses: [''],
      materialCauses: [''],
      machineCauses: [''],
      environmentCauses: [''],
      measurementCauses: [''],
    },
    gutItems: [],
    fiveWTwoHItems: [],
    pdca: { plan: '', doPhase: '', checkPhase: '', actPhase: '' },
    swot: { strengths: [''], weaknesses: [''], opportunities: [''], threats: [''] },
    brainstormingNotes: [],
  };

  readonly toolsList = [
    { key: 'ISHIKAWA' as const, name: 'Ishikawa (6M)' },
    { key: 'GUT' as const, name: 'Matriz GUT' },
    { key: '5W2H' as const, name: '5W2H' },
    { key: 'PDCA' as const, name: 'PDCA / PDSA' },
    { key: 'SWOT' as const, name: 'SWOT & Brainstorming' },
  ];

  readonly isConfirmSubmitOpen = signal(false);
  activityId: string | null = null;

  ngOnInit(): void {
    this.loadTriggersAndSeverities();

    const directId = this.route.snapshot?.paramMap?.get('activityId') || this.route.snapshot?.paramMap?.get('id');
    if (directId) {
      this.activityId = directId;
      this.loadActivity(directId);
      this.startTimer();
    } else if (this.route.paramMap) {
      this.route.paramMap.subscribe((params) => {
        const id = params.get('activityId') || params.get('id');
        if (id && id !== this.activityId) {
          this.activityId = id;
          this.loadActivity(id);
          this.startTimer();
        }
      });
    }
  }

  ngOnDestroy(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  clinicalCase() {
    return this.activity()?.clinicalCaseData;
  }

  patientInitial(): string {
    const name = this.clinicalCase()?.patientName;
    return name ? name.charAt(0).toUpperCase() : 'P';
  }

  loadActivity(id: string): void {
    this.activityService.getActivityById(id).subscribe({
      next: (res) => {
        this.activity.set(res.data);
        if (res.data.studentSubmission) {
          const sub = res.data.studentSubmission;
          this.identifiedTriggers.set(sub.identifiedTriggers || []);
          if (sub.qualityToolsData) {
            this.qualityTools = {
              ...this.qualityTools,
              ...sub.qualityToolsData,
            };
          }
        }
      },
      error: () => this.toast.error('Erro ao carregar detalhes da atividade.'),
    });
  }

  loadTriggersAndSeverities(): void {
    this.triggerService.listTriggers(undefined, undefined, true, 0, 100).subscribe({
      next: (res) => this.availableTriggers.set(res.data.content),
    });
    this.severityService.listHarmSeverities(undefined, true, 0, 20).subscribe({
      next: (res) => this.harmSeverities.set(res.data.content),
    });
  }

  startTimer(): void {
    this.timerInterval = setInterval(() => {
      if (!this.isTimerPaused() && this.timerSeconds() > 0) {
        this.timerSeconds.update((s) => s - 1);
      }
    }, 1000);
  }

  toggleTimer(): void {
    this.isTimerPaused.update((p) => !p);
  }

  formattedTime(): string {
    const total = this.timerSeconds();
    const minutes = Math.floor(total / 60);
    const seconds = total % 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  }

  addIdentifiedTrigger(): void {
    if (!this.selectedTriggerCode) return;
    const trigger = this.availableTriggers().find((t) => t.code === this.selectedTriggerCode);

    const newItem: IdentifiedTriggerData = {
      triggerId: trigger?.id,
      triggerCode: this.selectedTriggerCode,
      triggerName: trigger?.name,
      moduleCode: trigger?.moduleCode,
      notes: this.triggerNotes,
      isHarm: this.isHarmSelected,
      harmSeverityLetter: this.isHarmSelected ? this.selectedHarmSeverityLetter : undefined,
    };

    this.identifiedTriggers.update((items) => [...items, newItem]);
    this.selectedTriggerCode = '';
    this.triggerNotes = '';
    this.isHarmSelected = false;
  }

  removeIdentifiedTrigger(index: number): void {
    this.identifiedTriggers.update((items) => items.filter((_, i) => i !== index));
  }

  // Métodos Ishikawa
  addIshikawaCause(type: 'method' | 'manpower' | 'material' | 'machine' | 'environment' | 'measurement'): void {
    const listKey = `${type}Causes` as keyof IshikawaData;
    (this.qualityTools.ishikawa![listKey] as string[]).push('');
  }

  removeIshikawaCause(type: 'method' | 'manpower' | 'material' | 'machine' | 'environment' | 'measurement', index: number): void {
    const listKey = `${type}Causes` as keyof IshikawaData;
    (this.qualityTools.ishikawa![listKey] as string[]).splice(index, 1);
  }

  // Métodos GUT
  addGutItem(): void {
    this.qualityTools.gutItems!.push({ problem: '', gravity: 3, urgency: 3, trend: 3 });
  }

  removeGutItem(index: number): void {
    this.qualityTools.gutItems!.splice(index, 1);
  }

  calculateGutScore(item: GutItemData): number {
    const g = item.gravity || 1;
    const u = item.urgency || 1;
    const t = item.trend || 1;
    return g * u * t;
  }

  // Métodos 5W2H
  addFiveWTwoHItem(): void {
    this.qualityTools.fiveWTwoHItems!.push({
      what: '',
      why: '',
      where: '',
      when: '',
      who: '',
      how: '',
      howMuch: '',
    });
  }

  removeFiveWTwoHItem(index: number): void {
    this.qualityTools.fiveWTwoHItems!.splice(index, 1);
  }

  // Métodos SWOT
  addSwotItem(quadrant: 'strengths' | 'weaknesses' | 'opportunities' | 'threats'): void {
    this.qualityTools.swot![quadrant]!.push('');
  }

  removeSwotItem(quadrant: 'strengths' | 'weaknesses' | 'opportunities' | 'threats', index: number): void {
    this.qualityTools.swot![quadrant]!.splice(index, 1);
  }

  // Brainstorming
  addBrainstormingNote(): void {
    this.qualityTools.brainstormingNotes!.push('');
  }

  removeBrainstormingNote(index: number): void {
    this.qualityTools.brainstormingNotes!.splice(index, 1);
  }

  confirmSubmit(): void {
    this.isConfirmSubmitOpen.set(true);
  }

  submitResolution(): void {
    this.isConfirmSubmitOpen.set(false);

    if (!this.activityId) {
      this.toast.error('Identificador de atividade não encontrado.');
      return;
    }

    const dto: SubmissionCreateDTO = {
      identifiedTriggers: this.identifiedTriggers(),
      qualityToolsData: this.qualityTools,
    };

    this.activityService.submitActivity(this.activityId, dto).subscribe({
      next: () => {
        this.toast.success('Resolução enviada com sucesso!');
        this.router.navigate(['/academic/student/activities']);
      },
      error: (err) => {
        this.toast.error(err?.error?.message || 'Erro ao submeter resolução.');
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/academic/student/activities']);
  }
}
