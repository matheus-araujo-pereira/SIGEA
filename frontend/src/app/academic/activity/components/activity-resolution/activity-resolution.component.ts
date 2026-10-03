import { Component, DestroyRef, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ActivityService } from '../../services/activity.service';
import { GttTriggerService } from '../../../../gtt/trigger/services/gtt-trigger.service';
import { HarmSeverityService } from '../../../../gtt/severity/services/harm-severity.service';
import { ToastService } from '../../../../common/services/toast.service';
import {
  ActivityDetailDTO,
  ClinicalCaseData,
  FiveWTwoHItemData,
  GutItemData,
  IdentifiedTriggerData,
  IshikawaData,
  PdcaData,
  QualityToolsData,
  SubmissionCreateDTO,
  SwotData,
} from '../../models/activity.model';
import { GttTrigger } from '../../../../gtt/trigger/models/gtt-trigger.model';
import { HarmSeverity } from '../../../../gtt/severity/models/harm-severity.model';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog/confirm-dialog.component';
import { ResolutionHeaderComponent } from './resolution-header/resolution-header.component';
import { PatientRecordViewerComponent } from './patient-record-viewer/patient-record-viewer.component';
import { TriggerDetectorPanelComponent } from './trigger-detector-panel/trigger-detector-panel.component';
import { IshikawaToolComponent } from './quality-tools/ishikawa-tool/ishikawa-tool.component';
import { GutMatrixToolComponent } from './quality-tools/gut-matrix-tool/gut-matrix-tool.component';
import { FiveWTwoHToolComponent } from './quality-tools/five-w-two-h-tool/five-w-two-h-tool.component';
import { PdcaToolComponent } from './quality-tools/pdca-tool/pdca-tool.component';
import { SwotToolComponent } from './quality-tools/swot-tool/swot-tool.component';

/**
 * Interface interativa do estudante para resolução de Atividade Avaliativa,
 * com visualizador de prontuário simulado, cronômetro IHI de 20 min, rastreador de gatilhos
 * e suíte de Ferramentas da Qualidade (Ishikawa 6M, GUT, 5W2H, PDCA, SWOT e Brainstorming).
 */
@Component({
  selector: 'app-activity-resolution',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    ConfirmDialogComponent,
    ResolutionHeaderComponent,
    PatientRecordViewerComponent,
    TriggerDetectorPanelComponent,
    IshikawaToolComponent,
    GutMatrixToolComponent,
    FiveWTwoHToolComponent,
    PdcaToolComponent,
    SwotToolComponent
  ],
  templateUrl: './activity-resolution.component.html'
})
export class ActivityResolutionComponent implements OnInit, OnDestroy {
  /** Rota ativa para identificação da atividade avaliativa */
  private readonly route = inject(ActivatedRoute);
  /** Serviço de navegação de rotas SPA */
  private readonly router = inject(Router);
  /** Serviço de gestão de atividades */
  private readonly activityService = inject(ActivityService);
  /** Serviço de catálogo de gatilhos clínicos GTT */
  private readonly triggerService = inject(GttTriggerService);
  /** Serviço de gravidade de dano NCC MERP */
  private readonly severityService = inject(HarmSeverityService);
  /** Serviço de notificações de toast */
  private readonly toast = inject(ToastService);
  /** Referência de destruição para ciclo de vida do componente */
  private readonly destroyRef = inject(DestroyRef);

  /**
   * Detalhes da atividade avaliativa carregada.
   */
  readonly activity = signal<ActivityDetailDTO | null>(null);

  /**
   * Catálogo de gatilhos clínicos disponíveis para auditoria.
   */
  readonly availableTriggers = signal<GttTrigger[]>([]);

  /**
   * Classificações de severidade de dano NCC MERP.
   */
  readonly harmSeverities = signal<HarmSeverity[]>([]);

  /**
   * Aba principal ativa: RECORD (Prontuário), TRIGGERS (Gatilhos) ou QUALITY (Ferramentas da Qualidade).
   */
  readonly activeMainTab = signal<'RECORD' | 'TRIGGERS' | 'QUALITY'>('RECORD');

  /**
   * Ferramenta da qualidade atualmente ativa na visualização.
   */
  readonly activeQualityTool = signal<'ISHIKAWA' | 'GUT' | '5W2H' | 'PDCA' | 'SWOT'>('ISHIKAWA');

  /**
   * Tempo restante em segundos (iniciando em 20 minutos / 1200 segundos).
   */
  readonly timerSeconds = signal(1200);

  /**
   * Indica se o cronômetro está em pausa.
   */
  readonly isTimerPaused = signal(false);
  /** Handle do setInterval para controle do cronômetro */
  private timerInterval: any = null;

  /**
   * Lista de gatilhos clínicos identificados pelo estudante na auditoria.
   */
  readonly identifiedTriggers = signal<IdentifiedTriggerData[]>([]);
  /** Código do gatilho clínico atualmente selecionado no formulário */
  selectedTriggerCode = '';
  /** Flag indicando se o gatilho ocasionou dano ao paciente (NCC MERP E a I) */
  isHarmSelected = false;
  /** Letra de gravidade de dano NCC MERP selecionada */
  selectedHarmSeverityLetter = 'E';
  /** Anotações e justificativa clínica do discente para o gatilho detectado */
  triggerNotes = '';

  /**
   * Dados estruturados das Ferramentas da Qualidade preenchidas pelo aluno.
   */
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

  /**
   * Lista de opções de ferramentas da qualidade para navegação por abas.
   */
  readonly toolsList = [
    { key: 'ISHIKAWA' as const, name: 'Ishikawa (6M)' },
    { key: 'GUT' as const, name: 'Matriz GUT' },
    { key: '5W2H' as const, name: '5W2H' },
    { key: 'PDCA' as const, name: 'PDCA / PDSA' },
    { key: 'SWOT' as const, name: 'SWOT & Brainstorming' },
  ];

  /**
   * Controle de abertura do diálogo de confirmação de envio da submissão.
   */
  readonly isConfirmSubmitOpen = signal(false);

  /**
   * Identificador da atividade atual.
   */
  activityId: string | null = null;

  /**
   * Ciclo de inicialização: obtém o ID da atividade, carrega triggers, severidades e inicia o timer.
   */
  ngOnInit(): void {
    this.loadTriggersAndSeverities();

    const directId = this.route.snapshot?.paramMap?.get('activityId') || this.route.snapshot?.paramMap?.get('id');
    if (directId) {
      this.activityId = directId;
      this.loadActivity(directId);
      this.startTimer();
    } else if (this.route.paramMap) {
      this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
        const id = params.get('activityId') || params.get('id');
        if (id && id !== this.activityId) {
          this.activityId = id;
          this.loadActivity(id);
          this.startTimer();
        }
      });
    }
  }

  /**
   * Limpeza de timers e assinaturas ao destruir o componente.
   */
  ngOnDestroy(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  /**
   * Acesso aos dados do prontuário simulado.
   */
  clinicalCase(): ClinicalCaseData | undefined {
    return this.activity()?.clinicalCaseData;
  }

  /**
   * Inicial do paciente para avatar.
   */
  patientInitial(): string {
    const name = this.clinicalCase()?.patientName;
    return name ? name.charAt(0).toUpperCase() : 'P';
  }

  /**
   * Carrega os dados da atividade avaliativa e eventual submissão prévia em rascunho.
   */
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

  /**
   * Carrega o catálogo de gatilhos GTT e severidades NCC MERP.
   */
  loadTriggersAndSeverities(): void {
    this.triggerService.listTriggers(undefined, undefined, true, 0, 100).subscribe({
      next: (res) => this.availableTriggers.set(res.data.content),
    });
    this.severityService.listHarmSeverities(undefined, true, 0, 20).subscribe({
      next: (res) => this.harmSeverities.set(res.data.content),
    });
  }

  /**
   * Inicializa o cronômetro regressivo IHI.
   */
  startTimer(): void {
    this.timerInterval = setInterval(() => {
      if (!this.isTimerPaused() && this.timerSeconds() > 0) {
        this.timerSeconds.update((s) => s - 1);
      }
    }, 1000);
  }

  /**
   * Alterna entre pausar e retomar o cronômetro.
   */
  toggleTimer(): void {
    this.isTimerPaused.update((p) => !p);
  }

  /**
   * Formata os segundos em mm:ss.
   */
  formattedTime(): string {
    const total = this.timerSeconds();
    const minutes = Math.floor(total / 60);
    const seconds = total % 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  }

  /**
   * Recebe um gatilho adicionado a partir do painel de gatilhos.
   */
  onAddTriggerFromPanel(item: IdentifiedTriggerData): void {
    this.identifiedTriggers.update((items) => [...items, item]);
  }

  /**
   * Adiciona um gatilho utilizando o estado de formulário interno.
   */
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

  /**
   * Remove um gatilho identificado por índice.
   */
  removeIdentifiedTrigger(index: number): void {
    this.identifiedTriggers.update((items) => items.filter((_, i) => i !== index));
  }

  /**
   * Adiciona causa no Diagrama de Ishikawa.
   */
  addIshikawaCause(type: 'method' | 'manpower' | 'material' | 'machine' | 'environment' | 'measurement'): void {
    const listKey = `${type}Causes` as keyof IshikawaData;
    (this.qualityTools.ishikawa![listKey] as string[]).push('');
  }

  /**
   * Remove causa no Diagrama de Ishikawa.
   */
  removeIshikawaCause(type: 'method' | 'manpower' | 'material' | 'machine' | 'environment' | 'measurement', index: number): void {
    const listKey = `${type}Causes` as keyof IshikawaData;
    (this.qualityTools.ishikawa![listKey] as string[]).splice(index, 1);
  }

  /**
   * Adiciona item na Matriz GUT.
   */
  addGutItem(): void {
    this.qualityTools.gutItems!.push({ problem: '', gravity: 3, urgency: 3, trend: 3 });
  }

  /**
   * Remove item da Matriz GUT.
   */
  removeGutItem(index: number): void {
    this.qualityTools.gutItems!.splice(index, 1);
  }

  /**
   * Calcula a pontuação GUT (G × U × T).
   */
  calculateGutScore(item: GutItemData): number {
    const g = item.gravity || 1;
    const u = item.urgency || 1;
    const t = item.trend || 1;
    return g * u * t;
  }

  /**
   * Adiciona ação na Matriz 5W2H.
   */
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

  /**
   * Remove ação da Matriz 5W2H.
   */
  removeFiveWTwoHItem(index: number): void {
    this.qualityTools.fiveWTwoHItems!.splice(index, 1);
  }

  /**
   * Adiciona item em quadrante da Matriz SWOT.
   */
  addSwotItem(quadrant: 'strengths' | 'weaknesses' | 'opportunities' | 'threats'): void {
    this.qualityTools.swot![quadrant]!.push('');
  }

  /**
   * Remove item de quadrante da Matriz SWOT.
   */
  removeSwotItem(quadrant: 'strengths' | 'weaknesses' | 'opportunities' | 'threats', index: number): void {
    this.qualityTools.swot![quadrant]!.splice(index, 1);
  }

  /**
   * Adiciona anotação livre de Brainstorming.
   */
  addBrainstormingNote(): void {
    this.qualityTools.brainstormingNotes!.push('');
  }

  /**
   * Remove anotação de Brainstorming.
   */
  removeBrainstormingNote(index: number): void {
    this.qualityTools.brainstormingNotes!.splice(index, 1);
  }

  /**
   * Abre o modal de confirmação para submeter a resolução.
   */
  confirmSubmit(): void {
    this.isConfirmSubmitOpen.set(true);
  }

  /**
   * Efetiva a submissão dos dados ao backend.
   */
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

  /**
   * Retorna para a página de atividades do estudante.
   */
  goBack(): void {
    this.router.navigate(['/academic/student/activities']);
  }
}
