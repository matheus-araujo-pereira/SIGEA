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
  templateUrl: './activity-resolution.component.html'
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
