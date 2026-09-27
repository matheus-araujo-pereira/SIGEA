import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ActivityService } from '../../services/activity.service';
import { AcademicClassService } from '../../../clazz/services/academic-class.service';
import { ToastService } from '../../../../common/services/toast.service';
import { AcademicClassResponseDTO } from '../../../clazz/models/academic-class.model';
import { ClinicalCaseData } from '../../models/activity.model';
import { ClinicalCaseTemplateService } from '../../../clinical/services/clinical-case-template.service';
import { ClinicalCaseTemplateResponseDTO } from '../../../clinical/models/clinical-case-template.model';
import { TemplateSelectorComponent } from './template-selector.component';
import { ActivityInfoFormComponent } from './activity-info-form.component';
import { PatientDemographicsFormComponent } from './patient-demographics-form.component';
import { EvolutionNotesEditorComponent } from './evolution-notes-editor.component';
import { PrescriptionsEditorComponent } from './prescriptions-editor.component';
import { LabExamsEditorComponent } from './lab-exams-editor.component';
import { ProceduresEditorComponent } from './procedures-editor.component';

/**
 * Componente orquestrador para criação e edição de Atividades Avaliativas com Prontuário Simulado estruturado.
 */
@Component({
  selector: 'app-activity-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    TemplateSelectorComponent,
    ActivityInfoFormComponent,
    PatientDemographicsFormComponent,
    EvolutionNotesEditorComponent,
    PrescriptionsEditorComponent,
    LabExamsEditorComponent,
    ProceduresEditorComponent
  ],
  templateUrl: './activity-form.component.html'
})
export class ActivityFormComponent implements OnInit {
  /** Construtor reativo de formulários */
  private readonly fb = inject(FormBuilder);
  /** Rota ativa para leitura de parâmetros de ID e turma */
  private readonly route = inject(ActivatedRoute);
  /** Serviço de navegação de rotas SPA */
  private readonly router = inject(Router);
  /** Serviço de integração com a API de atividades */
  private readonly activityService = inject(ActivityService);
  /** Serviço de integração com a API de turmas acadêmicas */
  private readonly classService = inject(AcademicClassService);
  /** Serviço de integração com o catálogo de casos clínicos */
  private readonly templateService = inject(ClinicalCaseTemplateService);
  /** Serviço de exibição de mensagens toast */
  private readonly toast = inject(ToastService);

  /**
   * Coleção de turmas sob responsabilidade do docente.
   */
  readonly classes = signal<AcademicClassResponseDTO[]>([]);

  /**
   * Catálogo de casos clínicos padronizados IHI-GTT.
   */
  readonly templates = signal<ClinicalCaseTemplateResponseDTO[]>([]);

  /**
   * Identificador do modelo selecionado no dropdown.
   */
  readonly selectedTemplateId = signal<string>('');

  /**
   * Dados do caso clínico do modelo selecionado.
   */
  readonly activeTemplate = signal<ClinicalCaseTemplateResponseDTO | null>(null);

  /**
   * Indicador de persistência em andamento.
   */
  readonly isSaving = signal(false);

  /**
   * Identificador da atividade em caso de edição.
   */
  activityId: string | null = null;

  /**
   * Identificador da turma alvo caso navegado a partir de uma turma específica.
   */
  targetClassId: string | null = null;

  /**
   * Formulário reativo central da atividade e prontuário simulado.
   */
  form: FormGroup = this.fb.group({
    classId: ['', [Validators.required]],
    title: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', [Validators.required]],
    deadline: ['', [Validators.required]],
    clinicalCase: this.fb.group({
      patientName: ['', [Validators.required]],
      age: [null],
      gender: ['Feminino'],
      bed: [''],
      admissionDate: [''],
      patientDays: [1, [Validators.required, Validators.min(1)]],
      admissionNotes: [''],
      evolutionNotes: this.fb.array([]),
      prescriptions: this.fb.array([]),
      labExams: this.fb.array([]),
      procedures: this.fb.array([]),
    }),
  });

  /**
   * Verifica se o formulário está operando em modo de edição.
   */
  get isEditMode(): boolean {
    return !!this.activityId;
  }

  /**
   * Acesso tipado ao FormGroup do prontuário simulado.
   */
  get clinicalCaseGroup(): FormGroup {
    return this.form.get('clinicalCase') as FormGroup;
  }

  /**
   * Acesso tipado ao FormArray de anotações de evolução.
   */
  get evolutionNotesArray(): FormArray {
    return this.clinicalCaseGroup.get('evolutionNotes') as FormArray;
  }

  /**
   * Acesso tipado ao FormArray de prescrições medicamentosas.
   */
  get prescriptionsArray(): FormArray {
    return this.clinicalCaseGroup.get('prescriptions') as FormArray;
  }

  /**
   * Acesso tipado ao FormArray de exames laboratoriais.
   */
  get labExamsArray(): FormArray {
    return this.clinicalCaseGroup.get('labExams') as FormArray;
  }

  /**
   * Acesso tipado ao FormArray de procedimentos invasivos.
   */
  get proceduresArray(): FormArray {
    return this.clinicalCaseGroup.get('procedures') as FormArray;
  }

  /**
   * Ciclo de inicialização: recupera parâmetros de rota, carrega turmas, templates e atividade se aplicável.
   */
  ngOnInit(): void {
    this.activityId = this.route.snapshot.paramMap.get('id');
    this.targetClassId = this.route.snapshot.queryParamMap.get('classId');

    if (this.targetClassId) {
      this.form.patchValue({ classId: this.targetClassId });
    }

    this.loadClasses();
    this.loadTemplates();

    if (this.activityId) {
      this.loadActivity(this.activityId);
    }
  }

  /**
   * Busca as turmas ativas do professor para preenchimento do select.
   */
  loadClasses(): void {
    this.classService.getMyClasses(0, 50).subscribe({
      next: (res) => this.classes.set(res.data.content),
    });
  }

  /**
   * Carrega a listagem de modelos de prontuários canônicos IHI-GTT.
   */
  loadTemplates(): void {
    this.templateService.listTemplates().subscribe({
      next: (res) => this.templates.set(res.data),
      error: () => this.toast.error('Erro ao carregar catálogo de casos clínicos.'),
    });
  }

  /**
   * Atualiza o template ativo quando o usuário seleciona uma opção no dropdown.
   */
  onTemplateSelect(id: string): void {
    this.selectedTemplateId.set(id);
    const found = this.templates().find((t) => t.id === id) || null;
    this.activeTemplate.set(found);
  }

  /**
   * Aplica os dados do caso clínico modelo selecionado aos campos do formulário.
   */
  applySelectedTemplate(): void {
    const tpl = this.activeTemplate();
    if (!tpl || !tpl.clinicalCaseData) {
      this.toast.error('Nenhum modelo selecionado.');
      return;
    }

    const currentTitle = this.form.get('title')?.value;
    const currentDesc = this.form.get('description')?.value;

    this.form.patchValue({
      title: currentTitle || tpl.title,
      description: currentDesc || tpl.description,
    });

    const cc = tpl.clinicalCaseData;
    this.clinicalCaseGroup.patchValue({
      patientName: cc.patientName || '',
      age: cc.age ?? null,
      gender: cc.gender || 'Feminino',
      bed: cc.bed || '',
      admissionDate: cc.admissionDate || '',
      patientDays: cc.patientDays || 1,
      admissionNotes: cc.admissionNotes || '',
    });

    this.evolutionNotesArray.clear();
    (cc.evolutionNotes || []).forEach((n: any) => {
      const { dateTime, professionalRole, note } = this.extractEvolutionNote(n);
      this.addEvolutionNote(dateTime, professionalRole, note);
    });

    this.prescriptionsArray.clear();
    (cc.prescriptions || []).forEach((p: any) => {
      const { medication, dosage, route, frequency, check } = this.extractPrescription(p);
      this.addPrescription(medication, dosage, route, frequency, check);
    });

    this.labExamsArray.clear();
    (cc.labExams || []).forEach((e: any) => {
      const { examName, result, ref, date } = this.extractLabExam(e);
      this.addLabExam(examName, result, ref, date);
    });

    this.proceduresArray.clear();
    (cc.procedures || []).forEach((pr: any) => {
      const { procedureName, description, date } = this.extractProcedure(pr);
      this.addProcedure(procedureName, description, date);
    });

    this.toast.success(`Modelo "${tpl.title}" aplicado ao prontuário com sucesso!`);
  }

  /**
   * Extrai e normaliza os dados de anotação de evolução a partir do payload de template.
   */
  private extractEvolutionNote(n: any): { dateTime: string; professionalRole: string; note: string } {
    const dateTime = n.dateTime ? n.dateTime : (n.date ? n.date : '');
    const professionalRole = n.professionalRole ? n.professionalRole : (n.role ? n.role : '');
    const note = n.note ? n.note : (n.content ? n.content : '');
    return { dateTime, professionalRole, note };
  }

  /**
   * Extrai e normaliza os dados de prescrição de medicamentos a partir do payload de template.
   */
  private extractPrescription(p: any): { medication: string; dosage: string; route: string; frequency: string; check: string } {
    let check = p.administrationCheck ? p.administrationCheck : '';
    if (!check && p.checked) {
      check = 'Checado/Administrado';
    }
    return {
      medication: p.medication ? p.medication : '',
      dosage: p.dosage ? p.dosage : '',
      route: p.route ? p.route : '',
      frequency: p.frequency ? p.frequency : '',
      check,
    };
  }

  /**
   * Extrai e normaliza os dados de exame laboratorial a partir do payload de template.
   */
  private extractLabExam(e: any): { examName: string; result: string; ref: string; date: string } {
    const ref = e.referenceValue ? e.referenceValue : (e.referenceRange ? e.referenceRange : '');
    return {
      examName: e.examName ? e.examName : '',
      result: e.result ? e.result : '',
      ref,
      date: e.date ? e.date : '',
    };
  }

  /**
   * Extrai e normaliza os dados de procedimento realizado a partir do payload de template.
   */
  private extractProcedure(pr: any): { procedureName: string; description: string; date: string } {
    const description = pr.description ? pr.description : (pr.details ? pr.details : '');
    return {
      procedureName: pr.procedureName ? pr.procedureName : '',
      description,
      date: pr.date ? pr.date : '',
    };
  }

  /**
   * Carrega uma atividade existente para preenchimento dos campos de edição.
   */
  loadActivity(id: string): void {
    this.activityService.getActivityById(id).subscribe({
      next: (res) => {
        const a = res.data;
        this.targetClassId = a.classId;

        // Converter deadline para formato datetime-local (YYYY-MM-DDTHH:mm)
        const d = new Date(a.deadline);
        const isoLocal = new Date(d.getTime() - d.getTimezoneOffset() * 60000)
          .toISOString()
          .slice(0, 16);

        this.form.patchValue({
          classId: a.classId,
          title: a.title,
          description: a.description,
          deadline: isoLocal,
        });

        const cc = a.clinicalCaseData;
        if (cc) {
          this.clinicalCaseGroup.patchValue({
            patientName: cc.patientName || '',
            age: cc.age,
            gender: cc.gender || 'Feminino',
            bed: cc.bed,
            admissionDate: cc.admissionDate,
            patientDays: cc.patientDays || 1,
            admissionNotes: cc.admissionNotes,
          });

          this.evolutionNotesArray.clear();
          cc.evolutionNotes?.forEach((n) => this.addEvolutionNote(n.dateTime, n.professionalRole, n.note));

          this.prescriptionsArray.clear();
          cc.prescriptions?.forEach((p) =>
            this.addPrescription(p.medication, p.dosage, p.route, p.frequency, p.administrationCheck)
          );

          this.labExamsArray.clear();
          cc.labExams?.forEach((e) => this.addLabExam(e.examName, e.result, e.referenceValue, e.date));

          this.proceduresArray.clear();
          cc.procedures?.forEach((pr) => this.addProcedure(pr.procedureName, pr.description, pr.date));
        }
      },
      error: () => this.toast.error('Erro ao carregar atividade.'),
    });
  }

  /**
   * Adiciona uma linha de anotação de evolução médica/enfermagem.
   */
  addEvolutionNote(dateTime = '', professionalRole = '', note = ''): void {
    this.evolutionNotesArray.push(
      this.fb.group({
        dateTime: [dateTime],
        professionalRole: [professionalRole],
        note: [note],
      })
    );
  }

  /**
   * Remove uma linha de anotação de evolução.
   */
  removeEvolutionNote(index: number): void {
    this.evolutionNotesArray.removeAt(index);
  }

  /**
   * Adiciona um medicamento prescrito com posologia e via.
   */
  addPrescription(medication = '', dosage = '', route = '', frequency = '', administrationCheck = ''): void {
    this.prescriptionsArray.push(
      this.fb.group({
        medication: [medication],
        dosage: [dosage],
        route: [route],
        frequency: [frequency],
        administrationCheck: [administrationCheck],
      })
    );
  }

  /**
   * Remove um item da prescrição medicamentosa.
   */
  removePrescription(index: number): void {
    this.prescriptionsArray.removeAt(index);
  }

  /**
   * Adiciona um exame laboratorial ao prontuário.
   */
  addLabExam(examName = '', result = '', referenceValue = '', date = ''): void {
    this.labExamsArray.push(
      this.fb.group({
        examName: [examName],
        result: [result],
        referenceValue: [referenceValue],
        date: [date],
      })
    );
  }

  /**
   * Remove um exame laboratorial.
   */
  removeLabExam(index: number): void {
    this.labExamsArray.removeAt(index);
  }

  /**
   * Adiciona um procedimento cirúrgico ou invasivo.
   */
  addProcedure(procedureName = '', description = '', date = ''): void {
    this.proceduresArray.push(
      this.fb.group({
        procedureName: [procedureName],
        description: [description],
        date: [date],
      })
    );
  }

  /**
   * Remove um procedimento cadastrado.
   */
  removeProcedure(index: number): void {
    this.proceduresArray.removeAt(index);
  }

  /**
   * Valida e submete a atividade para criação ou atualização na API.
   */
  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.toast.error('Por favor, preencha todos os campos obrigatórios.');
      return;
    }

    this.isSaving.set(true);
    const v = this.form.value;

    const clinicalCaseData: ClinicalCaseData = {
      patientName: v.clinicalCase.patientName,
      age: v.clinicalCase.age,
      gender: v.clinicalCase.gender,
      bed: v.clinicalCase.bed,
      admissionDate: v.clinicalCase.admissionDate,
      patientDays: v.clinicalCase.patientDays,
      admissionNotes: v.clinicalCase.admissionNotes,
      evolutionNotes: v.clinicalCase.evolutionNotes,
      prescriptions: v.clinicalCase.prescriptions,
      labExams: v.clinicalCase.labExams,
      procedures: v.clinicalCase.procedures,
    };

    const deadlineInstant = new Date(v.deadline).toISOString();

    if (this.isEditMode && this.activityId) {
      this.activityService
        .updateActivity(this.activityId, {
          title: v.title,
          description: v.description,
          clinicalCaseData,
          deadline: deadlineInstant,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toast.success('Atividade avaliativa atualizada com sucesso!');
            this.goBack();
          },
          error: (err) => {
            this.isSaving.set(false);
            this.toast.error(err?.error?.message || 'Erro ao atualizar atividade.');
          },
        });
    } else {
      this.activityService
        .createActivity({
          classId: v.classId,
          title: v.title,
          description: v.description,
          clinicalCaseData,
          deadline: deadlineInstant,
        })
        .subscribe({
          next: () => {
            this.isSaving.set(false);
            this.toast.success('Atividade avaliativa criada com sucesso!');
            this.goBack();
          },
          error: (err) => {
            this.isSaving.set(false);
            this.toast.error(err?.error?.message || 'Erro ao criar atividade.');
          },
        });
    }
  }

  /**
   * Navega de volta para a turma ou listagem de turmas.
   */
  goBack(): void {
    if (this.targetClassId) {
      this.router.navigate(['/academic/classes', this.targetClassId]);
    } else {
      this.router.navigate(['/academic/classes']);
    }
  }
}
